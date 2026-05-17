// ─────────────────────────────────────────────────────────────
// LOCAL DEV CONFIG
// 1. Run `ipconfig` in PowerShell
// 2. Find your WiFi "IPv4 Address" e.g. 192.168.1.105
// 3. Replace LOCAL_IP below with that value
// 4. Your phone and PC must be on the same WiFi
// ─────────────────────────────────────────────────────────────
const LOCAL_IP = '192.168.100.49';

const ENV = {
  dev: {
    API_BASE_URL:       `http://${LOCAL_IP}:8081`,
    KEYCLOAK_URL:       `http://${LOCAL_IP}:8085`,  // Keycloak on 8085
    KEYCLOAK_REALM:     'kerjapro',
    KEYCLOAK_CLIENT_ID: 'kerjapro-mobile',
  },
  prod: {
    API_BASE_URL:       'https://api.kerjapro.com',
    KEYCLOAK_URL:       'https://auth.kerjapro.com',
    KEYCLOAK_REALM:     'kerjapro',
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

export const SERVICES = {
  contractor:   `http://${LOCAL_IP}:8081`,
  project:      `http://${LOCAL_IP}:8082`,
  booking:      `http://${LOCAL_IP}:8083`,
  review:       `http://${LOCAL_IP}:8084`,
  notification: `http://${LOCAL_IP}:8086`, // moved to 8086 (8085 = Keycloak)
};
