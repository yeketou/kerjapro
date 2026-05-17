import Constants from 'expo-constants';

// ─────────────────────────────────────────────────────────────
// LOCAL DEV: Replace with your Windows machine's local IP.
// Run `ipconfig` in PowerShell and look for:
//   "IPv4 Address . . . . : 192.168.x.x"  (under your WiFi adapter)
//
// Your phone AND PC must be on the SAME WiFi network.
// ─────────────────────────────────────────────────────────────
const LOCAL_IP = '192.168.1.105'; // ← CHANGE THIS to your actual IP

const ENV = {
  dev: {
    // Direct to service-contractor (no gateway needed for local dev)
    API_BASE_URL:   `http://${LOCAL_IP}:8081`,
    KEYCLOAK_URL:   `http://${LOCAL_IP}:8080`,
    KEYCLOAK_REALM: 'kerjapro',
    KEYCLOAK_CLIENT_ID: 'kerjapro-mobile',
  },
  prod: {
    API_BASE_URL:   'https://api.kerjapro.com',
    KEYCLOAK_URL:   'https://auth.kerjapro.com',
    KEYCLOAK_REALM: 'kerjapro',
    KEYCLOAK_CLIENT_ID: 'kerjapro-mobile',
  },
};

const getEnv = () => (__DEV__ ? ENV.dev : ENV.prod);
export const CONFIG = getEnv();

export const KEYCLOAK = {
  discoveryUrl:       `${CONFIG.KEYCLOAK_URL}/realms/${CONFIG.KEYCLOAK_REALM}/.well-known/openid-configuration`,
  authEndpoint:       `${CONFIG.KEYCLOAK_URL}/realms/${CONFIG.KEYCLOAK_REALM}/protocol/openid-connect/auth`,
  tokenEndpoint:      `${CONFIG.KEYCLOAK_URL}/realms/${CONFIG.KEYCLOAK_REALM}/protocol/openid-connect/token`,
  revocationEndpoint: `${CONFIG.KEYCLOAK_URL}/realms/${CONFIG.KEYCLOAK_REALM}/protocol/openid-connect/logout`,
  clientId:           CONFIG.KEYCLOAK_CLIENT_ID,
  redirectUri:        'kerjapro://auth/callback',
  scopes:             ['openid', 'profile', 'email'],
};

// Per-service URLs (when not using gateway)
export const SERVICES = {
  contractor:   `http://${LOCAL_IP}:8081`,
  project:      `http://${LOCAL_IP}:8082`,
  booking:      `http://${LOCAL_IP}:8083`,
  review:       `http://${LOCAL_IP}:8084`,
  notification: `http://${LOCAL_IP}:8085`,
};
