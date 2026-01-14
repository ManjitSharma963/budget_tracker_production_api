package com.expensetracker.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@Component
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.jpa.hibernate.ddl-auto:update}")
    private String ddlAuto;

    @Value("${app.database.initialize:true}")
    private boolean initializeDatabase;

    @PostConstruct
    public void initializeDatabase() {
        if (!initializeDatabase) {
            logger.info("Database initialization is disabled. Skipping.");
            return;
        }

        if (jdbcTemplate == null) {
            logger.warn("JdbcTemplate not available. Skipping database initialization.");
            return;
        }

        // Skip if using JPA auto DDL
        if ("create".equals(ddlAuto) || "create-drop".equals(ddlAuto)) {
            logger.info("JPA DDL auto is set to '{}'. Skipping manual table creation.", ddlAuto);
            return;
        }

        try {
            logger.info("Starting database table initialization...");
            
            // Read SQL file from resources directory
            Resource resource = new ClassPathResource("create_table.sql");
            
            if (!resource.exists()) {
                logger.warn("create_table.sql not found in resources. Skipping database initialization.");
                return;
            }

            Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            String sqlScript = FileCopyUtils.copyToString(reader);

            // Split script into individual statements
            String[] statements = sqlScript.split(";");
            
            int tablesCreated = 0;
            int tablesSkipped = 0;

            for (String statement : statements) {
                String trimmedStatement = statement.trim();
                
                // Skip empty statements and comments
                if (trimmedStatement.isEmpty() || 
                    trimmedStatement.startsWith("--") || 
                    trimmedStatement.startsWith("SET ")) {
                    continue;
                }

                // Extract table name from CREATE TABLE IF NOT EXISTS statement
                String tableName = extractTableName(trimmedStatement);
                
                if (tableName != null) {
                    // Check if table already exists
                    if (tableExists(tableName)) {
                        logger.debug("Table '{}' already exists. Skipping creation.", tableName);
                        tablesSkipped++;
                        continue;
                    }
                }

                try {
                    // Execute the statement
                    jdbcTemplate.execute(trimmedStatement);
                    if (tableName != null) {
                        logger.info("Table '{}' created successfully.", tableName);
                        tablesCreated++;
                    }
                } catch (Exception e) {
                    // If table already exists (from IF NOT EXISTS), that's okay
                    if (e.getMessage() != null && 
                        (e.getMessage().contains("already exists") || 
                         e.getMessage().contains("Duplicate"))) {
                        logger.debug("Table already exists (caught exception): {}", e.getMessage());
                        if (tableName != null) {
                            tablesSkipped++;
                        }
                    } else {
                        logger.error("Error executing SQL statement: {}", e.getMessage());
                        logger.debug("Statement: {}", trimmedStatement);
                    }
                }
            }

            logger.info("Database initialization completed. Created: {}, Skipped: {}", 
                       tablesCreated, tablesSkipped);

        } catch (Exception e) {
            logger.error("Error initializing database: {}", e.getMessage(), e);
        }
    }

    private String extractTableName(String statement) {
        // Extract table name from CREATE TABLE IF NOT EXISTS statement
        if (statement.toUpperCase().contains("CREATE TABLE")) {
            String upperStatement = statement.toUpperCase();
            int createIndex = upperStatement.indexOf("CREATE TABLE");
            int ifNotExistsIndex = upperStatement.indexOf("IF NOT EXISTS", createIndex);
            
            int startIndex = ifNotExistsIndex > 0 ? 
                ifNotExistsIndex + "IF NOT EXISTS".length() : 
                createIndex + "CREATE TABLE".length();
            
            // Find the table name (skip whitespace)
            while (startIndex < statement.length() && 
                   (Character.isWhitespace(statement.charAt(startIndex)) || 
                    statement.charAt(startIndex) == '`')) {
                startIndex++;
            }
            
            int endIndex = startIndex;
            while (endIndex < statement.length() && 
                   !Character.isWhitespace(statement.charAt(endIndex)) && 
                   statement.charAt(endIndex) != '(' &&
                   statement.charAt(endIndex) != '`') {
                endIndex++;
            }
            
            if (endIndex > startIndex) {
                String tableName = statement.substring(startIndex, endIndex).trim();
                // Remove backticks if present
                tableName = tableName.replace("`", "");
                return tableName;
            }
        }
        return null;
    }

    private boolean tableExists(String tableName) {
        try {
            return jdbcTemplate.execute((Connection connection) -> {
                DatabaseMetaData metaData = connection.getMetaData();
                String catalog = connection.getCatalog();
                ResultSet tables = metaData.getTables(catalog, null, tableName, null);
                boolean exists = tables.next();
                tables.close();
                return exists;
            });
        } catch (Exception e) {
            logger.debug("Error checking if table exists: {}", e.getMessage());
            return false;
        }
    }
}

