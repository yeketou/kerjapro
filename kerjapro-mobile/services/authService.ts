import * as AuthSession from 'expo-auth-session';
import * as WebBrowser from 'expo-web-browser';
import * as Crypto from 'expo-crypto';
import * as SecureStore from 'expo-secure-store';
import { KEYCLOAK } from '../constants/config';

WebBrowser.maybeCompleteAuthSession();

const TOKEN_KEY         = 'kerjapro_access_token';
const REFRESH_TOKEN_KEY = 'kerjapro_refresh_token';
const ID_TOKEN_KEY      = 'kerjapro_id_token';

export interface TokenSet {
  accessToken: string;
  refreshToken: string;
  idToken: string;
  expiresIn: number;
}

export interface KerjaProClaims {
  sub: string;
  email: string;
  name: string;
  roles: string[];
}

// ─────────────────────────────────────────────
// PKCE Login via Keycloak
// ─────────────────────────────────────────────
export async function loginWithKeycloak(): Promise<TokenSet | null> {
  const discovery: AuthSession.DiscoveryDocument = {
    authorizationEndpoint: KEYCLOAK.authEndpoint,
    tokenEndpoint: KEYCLOAK.tokenEndpoint,
    revocationEndpoint: KEYCLOAK.revocationEndpoint,
  };

  const codeVerifier  = await generateCodeVerifier();
  const codeChallenge = await generateCodeChallenge(codeVerifier);
  const redirectUri   = AuthSession.makeRedirectUri({ scheme: 'kerjapro', path: 'auth/callback' });

  const authRequest = new AuthSession.AuthRequest({
    clientId: KEYCLOAK.clientId,
    scopes: KEYCLOAK.scopes,
    redirectUri,
    codeChallenge,
    codeChallengeMethod: AuthSession.CodeChallengeMethod.S256,
    usePKCE: true,
  });

  authRequest.codeVerifier = codeVerifier;

  const result = await authRequest.promptAsync(discovery);

  if (result.type !== 'success' || !result.params.code) {
    return null;
  }

  const tokenResponse = await AuthSession.exchangeCodeAsync(
    {
      clientId: KEYCLOAK.clientId,
      code: result.params.code,
      redirectUri,
      codeVerifier,
    },
    discovery
  );

  const tokens: TokenSet = {
    accessToken:  tokenResponse.accessToken,
    refreshToken: tokenResponse.refreshToken ?? '',
    idToken:      tokenResponse.idToken ?? '',
    expiresIn:    tokenResponse.expiresIn ?? 900,
  };

  await saveTokens(tokens);
  return tokens;
}

// ─────────────────────────────────────────────
// Token Refresh
// ─────────────────────────────────────────────
export async function refreshAccessToken(): Promise<TokenSet | null> {
  const refreshToken = await SecureStore.getItemAsync(REFRESH_TOKEN_KEY);
  if (!refreshToken) return null;

  try {
    const response = await fetch(KEYCLOAK.tokenEndpoint, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        grant_type:    'refresh_token',
        client_id:     KEYCLOAK.clientId,
        refresh_token: refreshToken,
      }).toString(),
    });

    if (!response.ok) {
      await clearTokens();
      return null;
    }

    const data = await response.json();
    const tokens: TokenSet = {
      accessToken:  data.access_token,
      refreshToken: data.refresh_token,
      idToken:      data.id_token,
      expiresIn:    data.expires_in,
    };

    await saveTokens(tokens);
    return tokens;

  } catch {
    await clearTokens();
    return null;
  }
}

// ─────────────────────────────────────────────
// Logout
// ─────────────────────────────────────────────
export async function logout(): Promise<void> {
  const idToken = await SecureStore.getItemAsync(ID_TOKEN_KEY);
  await clearTokens();

  if (idToken) {
    const logoutUrl = `${KEYCLOAK.revocationEndpoint}?id_token_hint=${idToken}&post_logout_redirect_uri=kerjapro://auth/login`;
    await WebBrowser.openBrowserAsync(logoutUrl);
  }
}

// ─────────────────────────────────────────────
// JWT Parsing (no validation — gateway validates)
// ─────────────────────────────────────────────
export function parseJwtClaims(token: string): KerjaProClaims | null {
  try {
    const base64Url = token.split('.')[1];
    const base64    = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const payload   = JSON.parse(atob(base64));
    return {
      sub:   payload.sub,
      email: payload.email,
      name:  payload.name,
      roles: payload.roles ?? [],
    };
  } catch {
    return null;
  }
}

// ─────────────────────────────────────────────
// Secure Token Storage
// ─────────────────────────────────────────────
export async function saveTokens(tokens: TokenSet): Promise<void> {
  await Promise.all([
    SecureStore.setItemAsync(TOKEN_KEY,         tokens.accessToken),
    SecureStore.setItemAsync(REFRESH_TOKEN_KEY, tokens.refreshToken),
    SecureStore.setItemAsync(ID_TOKEN_KEY,      tokens.idToken),
  ]);
}

export async function loadTokens(): Promise<Partial<TokenSet> | null> {
  const [accessToken, refreshToken, idToken] = await Promise.all([
    SecureStore.getItemAsync(TOKEN_KEY),
    SecureStore.getItemAsync(REFRESH_TOKEN_KEY),
    SecureStore.getItemAsync(ID_TOKEN_KEY),
  ]);
  if (!accessToken) return null;
  return { accessToken, refreshToken: refreshToken ?? '', idToken: idToken ?? '' };
}

export async function clearTokens(): Promise<void> {
  await Promise.all([
    SecureStore.deleteItemAsync(TOKEN_KEY),
    SecureStore.deleteItemAsync(REFRESH_TOKEN_KEY),
    SecureStore.deleteItemAsync(ID_TOKEN_KEY),
  ]);
}

// ─────────────────────────────────────────────
// PKCE Helpers
// ─────────────────────────────────────────────
async function generateCodeVerifier(): Promise<string> {
  const randomBytes = await Crypto.getRandomBytesAsync(32);
  return btoa(String.fromCharCode(...randomBytes))
    .replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
}

async function generateCodeChallenge(verifier: string): Promise<string> {
  const digest = await Crypto.digestStringAsync(
    Crypto.CryptoDigestAlgorithm.SHA256,
    verifier,
    { encoding: Crypto.CryptoEncoding.BASE64 }
  );
  return digest.replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
}
