import axios, { AxiosInstance, InternalAxiosRequestConfig } from 'axios';
import { CONFIG } from '../constants/config';
import { loadTokens, refreshAccessToken, parseJwtClaims } from './authService';
import { useAuthStore } from '../store/authStore';

const apiClient: AxiosInstance = axios.create({
  baseURL: CONFIG.API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
    'Accept':       'application/json',
  },
});

// ─────────────────────────────────────────────
// Request Interceptor
// Attaches: Authorization + X-Tenant-Slug
// ─────────────────────────────────────────────
apiClient.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    const tokens = await loadTokens();
    if (tokens?.accessToken) {
      config.headers.Authorization = `Bearer ${tokens.accessToken}`;

      // Inject tenant slug for MAIN_CONTRACTOR requests
      const { tenantSlug } = useAuthStore.getState();
      if (tenantSlug) {
        config.headers['X-Tenant-Slug'] = tenantSlug;
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ─────────────────────────────────────────────
// Response Interceptor
// Handles 401: attempt token refresh once, then logout
// ─────────────────────────────────────────────
let isRefreshing = false;
let refreshQueue: Array<(token: string) => void> = [];

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve) => {
          refreshQueue.push((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            resolve(apiClient(originalRequest));
          });
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const tokens = await refreshAccessToken();
        if (tokens) {
          const { setTokens, setUser } = useAuthStore.getState();
          await setTokens(tokens.accessToken, tokens.refreshToken);

          const claims = parseJwtClaims(tokens.accessToken);
          if (claims) {
            const role = claims.roles.find(r =>
              ['MAIN_CONTRACTOR', 'SUBCONTRACTOR', 'PLATFORM_ADMIN'].includes(r)
            ) as any;
            setUser(claims.sub, role, claims.name, claims.tenant_slug);
          }

          refreshQueue.forEach(cb => cb(tokens.accessToken));
          refreshQueue = [];

          originalRequest.headers.Authorization = `Bearer ${tokens.accessToken}`;
          return apiClient(originalRequest);
        }
      } catch {
        // Refresh failed — logout
        const { logout } = useAuthStore.getState();
        await logout();
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
