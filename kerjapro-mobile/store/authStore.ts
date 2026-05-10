import { create } from 'zustand';
import * as SecureStore from 'expo-secure-store';

type Role = 'MAIN_CONTRACTOR' | 'SUBCONTRACTOR' | 'PLATFORM_ADMIN' | null;

interface AuthState {
  isAuthenticated: boolean;
  accessToken: string | null;
  refreshToken: string | null;
  userId: string | null;
  role: Role;
  fullName: string | null;
  // Tenant context — set for MAIN_CONTRACTOR, null for SUBCONTRACTOR
  tenantSlug: string | null;
  tenantName: string | null;

  setTokens: (access: string, refresh: string) => void;
  setUser: (userId: string, role: Role, fullName: string, tenantSlug?: string, tenantName?: string) => void;
  logout: () => Promise<void>;
  loadFromStorage: () => Promise<void>;
}

const TOKEN_KEY = 'kerjapro_access_token';
const REFRESH_KEY = 'kerjapro_refresh_token';

export const useAuthStore = create<AuthState>((set) => ({
  isAuthenticated: false,
  accessToken: null,
  refreshToken: null,
  userId: null,
  role: null,
  fullName: null,
  tenantSlug: null,
  tenantName: null,

  setTokens: async (access, refresh) => {
    await SecureStore.setItemAsync(TOKEN_KEY, access);
    await SecureStore.setItemAsync(REFRESH_KEY, refresh);
    set({ accessToken: access, refreshToken: refresh, isAuthenticated: true });
  },

  setUser: (userId, role, fullName, tenantSlug, tenantName) => {
    set({ userId, role, fullName, tenantSlug: tenantSlug ?? null, tenantName: tenantName ?? null });
  },

  logout: async () => {
    await SecureStore.deleteItemAsync(TOKEN_KEY);
    await SecureStore.deleteItemAsync(REFRESH_KEY);
    set({
      isAuthenticated: false,
      accessToken: null,
      refreshToken: null,
      userId: null,
      role: null,
      fullName: null,
      tenantSlug: null,
      tenantName: null,
    });
  },

  loadFromStorage: async () => {
    const access = await SecureStore.getItemAsync(TOKEN_KEY);
    const refresh = await SecureStore.getItemAsync(REFRESH_KEY);
    if (access && refresh) {
      set({ accessToken: access, refreshToken: refresh, isAuthenticated: true });
    }
  },
}));
