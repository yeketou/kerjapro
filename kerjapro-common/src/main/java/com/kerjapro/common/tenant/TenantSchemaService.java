package com.kerjapro.common.tenant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * Provisions and migrates per-tenant PostgreSQL schemas.
 *
 * Called by service-contractor (or a future service-tenant) when a new
 * tenant is onboarded. Creates the schema if absent, then applies all
 * tenant Flyway migrations from db/migration/tenant/.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantSchemaService {

    private final DataSource dataSource;

    /**
     * Creates the schema tenant_{slug} and applies all tenant migrations.
     *
     * @param slug the tenant slug (e.g. "gamuda", "ijm")
     */
    public void provisionTenantSchema(String slug) {
        String schemaName = "tenant_" + slug.toLowerCase().trim();
        log.info("Provisioning tenant schema: {}", schemaName);

        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(schemaName)
                    .locations("classpath:db/migration/tenant")
                    .table("flyway_schema_history")
                    .baselineOnMigrate(true)
                    .load();

            flyway.migrate();
            log.info("Tenant schema {} provisioned successfully", schemaName);

        } catch (Exception e) {
            log.error("Failed to provision tenant schema {}: {}", schemaName, e.getMessage(), e);
            throw new TenantProvisioningException(
                    "Failed to provision schema for tenant: " + slug, e);
        }
    }

    /**
     * Checks whether the schema for the given slug already exists.
     */
    public boolean schemaExists(String slug) {
        String schemaName = "tenant_" + slug.toLowerCase().trim();
        try (var connection = dataSource.getConnection();
             var rs = connection.getMetaData().getSchemas()) {
            while (rs.next()) {
                if (schemaName.equals(rs.getString("TABLE_SCHEM"))) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.warn("Could not check schema existence for {}: {}", schemaName, e.getMessage());
        }
        return false;
    }
}
