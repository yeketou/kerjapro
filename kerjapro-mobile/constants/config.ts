import Constants from 'expo-constants';

const ENV = {
  dev: {
    API_BASE_URL: 'http://localhost:8079',
    KEYCLOAK_URL: 'http://localhost:8080',
    KEYCLOAK_REALM: 'kerjapro',
    KEYCLOAK_CLIENT_ID: 'kerjapro-mobile',
  },
  prod: {
    API_BASE_URL: 'https://api.kerjapro.com',
    KEYCLOAK_URL: 'https://auth.kerjapro.com',
    KEYCLOAK_REALM: 'kerjapro',
    KEYCLOAK_CLIENT_ID: 'kerjapro-mobile',
  },
};

const getEnv = () => {
  if (__DEV__) return ENV.dev;
  return ENV.prod;
};

export const CONFIG = getEnv();

export const KEYCLOAK = {
  discoveryUrl: `${CONFIG.KEYCLOAK_URL}/realms/${CONFIG.KEYCLOAK_REALM}/.well-known/openid-configuration`,
  authEndpoint: `${CONFIG.KEYCLOAK_URL}/realms/${CONFIG.KEYCLOAK_REALM}/protocol/openid-connect/auth`,
  tokenEndpoint: `${CONFIG.KEYCLOAK_URL}/realms/${CONFIG.KEYCLOAK_REALM}/protocol/openid-connect/token`,
  revocationEndpoint: `${CONFIG.KEYCLOAK_URL}/realms/${CONFIG.KEYCLOAK_REALM}/protocol/openid-connect/logout`,
  clientId: CONFIG.KEYCLOAK_CLIENT_ID,
  redirectUri: 'kerjapro://auth/callback',
  scopes: ['openid', 'profile', 'email'],
};
