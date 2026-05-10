package com.kerjapro.common.tenant;

/**
 * Holds the current tenant slug per request thread.
 * Uses InheritableThreadLocal so async child threads inherit the tenant context.
 */
public final class TenantContext {

    private static final InheritableThreadLocal<String> CURRENT_TENANT =
            new InheritableThreadLocal<>();

    private TenantContext() {}

    public static void setTenantSlug(String slug) {
        CURRENT_TENANT.set(slug);
    }

    public static String getTenantSlug() {
        return CURRENT_TENANT.get();
    }

    public static boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }

    /**
     * Returns the PostgreSQL schema name for the current tenant.
     * e.g. slug "gamuda" → schema "tenant_gamuda"
     */
    public static String getCurrentSchema() {
        String slug = CURRENT_TENANT.get();
        if (slug == null) {
            return "public";
        }
        return "tenant_" + slug;
    }
}
