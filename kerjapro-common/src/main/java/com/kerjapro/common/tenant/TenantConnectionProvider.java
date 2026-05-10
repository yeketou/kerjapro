package com.kerjapro.common.tenant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Sets PostgreSQL search_path on every connection checkout.
 *
 * When a tenant slug is present:  search_path = tenant_{slug}, public
 * When no tenant (subcontractor): search_path = public
 *
 * IMPORTANT: HikariCP must be configured with auto-commit=false
 * to allow SET LOCAL search_path within the transaction.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantConnectionProvider implements MultiTenantConnectionProvider<String> {

    private final DataSource dataSource;

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantSlug) throws SQLException {
        Connection connection = dataSource.getConnection();
        String schema = (tenantSlug != null && !tenantSlug.isBlank())
                ? "tenant_" + tenantSlug + ", public"
                : "public";

        try {
            connection.createStatement()
                    .execute("SET search_path TO " + schema);
            log.debug("search_path set to: {}", schema);
        } catch (SQLException e) {
            log.error("Failed to set search_path to {}: {}", schema, e.getMessage());
            connection.close();
            throw e;
        }

        return connection;
    }

    @Override
    public void releaseConnection(String tenantSlug, Connection connection) throws SQLException {
        try {
            connection.createStatement().execute("SET search_path TO public");
        } finally {
            connection.close();
        }
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        throw new UnsupportedOperationException("Cannot unwrap TenantConnectionProvider");
    }
}
