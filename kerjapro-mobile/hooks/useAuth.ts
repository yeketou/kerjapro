import { useCallback } from 'react';
import { router } from 'expo-router';
import { useAuthStore } from '../store/authStore';
import {
  loginWithKeycloak,
  logout as keycloakLogout,
  parseJwtClaims,
  loadTokens,
} from '../services/authService';

type Role = 'MAIN_CONTRACTOR' | 'SUBCONTRACTOR' | 'PLATFORM_ADMIN';

export function useAuth() {
  const store = useAuthStore();

  const login = useCallback(async () => {
    const tokens = await loginWithKeycloak();
    if (!tokens) return false;

    const claims = parseJwtClaims(tokens.accessToken);
    if (!claims) return false;

    await store.setTokens(tokens.accessToken, tokens.refreshToken);

    const role = claims.roles.find(r =>
      ['MAIN_CONTRACTOR', 'SUBCONTRACTOR', 'PLATFORM_ADMIN'].includes(r)
    ) as Role | undefined;

    store.setUser(claims.sub, role ?? null, claims.name);

    // Route based on role
    if (role === 'MAIN_CONTRACTOR') {
      router.replace('/main-contractor/dashboard');
    } else if (role === 'SUBCONTRACTOR') {
      router.replace('/subcontractor/dashboard');
    } else if (role === 'PLATFORM_ADMIN') {
      router.replace('/admin/dashboard');
    }

    return true;
  }, [store]);

  const logout = useCallback(async () => {
    await keycloakLogout();
    await store.logout();
    router.replace('/auth/login');
  }, [store]);

  const restoreSession = useCallback(async () => {
    const tokens = await loadTokens();
    if (!tokens?.accessToken) return false;

    const claims = parseJwtClaims(tokens.accessToken);
    if (!claims) return false;

    const role = claims.roles.find(r =>
      ['MAIN_CONTRACTOR', 'SUBCONTRACTOR', 'PLATFORM_ADMIN'].includes(r)
    ) as Role | undefined;

    await store.setTokens(tokens.accessToken, tokens.refreshToken ?? '');
    store.setUser(claims.sub, role ?? null, claims.name);
    return true;
  }, [store]);

  return {
    ...store,
    login,
    logout,
    restoreSession,
  };
}
