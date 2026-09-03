package com.sdlc.pipeline.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration Loader - Reads api-configuration.properties
 * Simple, lightweight configuration management
 */
public class ConfigLoader {
    private static ConfigLoader instance;
    private Properties properties;

    private ConfigLoader() {
        properties = new Properties();
        loadConfig();
    }

    /**
     * Singleton pattern
     */
    public static ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }

    /**
     * Load configuration from properties file
     */
    private void loadConfig() {
        try {
            String configPath = "api-configuration.properties";
            properties.load(new FileInputStream(configPath));
            System.out.println("✅ Configuration loaded from: " + configPath);
        } catch (IOException e) {
            System.err.println("⚠️  Configuration file not found. Using defaults.");
            System.err.println("   Create 'api-configuration.properties' in project root");
        }
    }

    /**
     * Get string property
     */
    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get boolean property
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }

    /**
     * Get int property
     */
    public int getInt(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key, String.valueOf(defaultValue));
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * JIRA Configuration Getters
     */
    public boolean isJiraEnabled() {
        return getBoolean("jira.enabled", false);
    }

    public String getJiraUrl() {
        return getString("jira.url", "");
    }

    public String getJiraApiToken() {
        return getString("jira.api.token", "");
    }

    public String getJiraEmail() {
        return getString("jira.email", "");
    }

    /**
     * Pipeline Configuration Getters
     */
    public boolean isAutoAdvanceEnabled() {
        return getBoolean("pipeline.auto.advance", false);
    }

    public boolean isPersistenceEnabled() {
        return getBoolean("pipeline.artifacts.persist", true);
    }

    public String getArtifactsDirectory() {
        return getString("pipeline.artifacts.dir", "./pipeline-artifacts");
    }

    /**
     * GitHub Configuration Getters
     */
    public boolean isGitHubEnabled() {
        return getBoolean("github.enabled", false);
    }

    public String getGitHubToken() {
        return getString("github.token", "");
    }

    public String getGitHubRepoOwner() {
        return getString("github.repo.owner", "");
    }

    public String getGitHubRepoName() {
        return getString("github.repo.name", "");
    }

    /**
     * Logging Configuration Getters
     */
    public String getLoggingLevel() {
        return getString("logging.level", "INFO");
    }

    public String getLoggingFile() {
        return getString("logging.file", "./logs/pipeline.log");
    }

    @Override
    public String toString() {
        return "ConfigLoader{" +
                "jira.enabled=" + isJiraEnabled() +
                ", jira.url=" + getJiraUrl() +
                ", github.enabled=" + isGitHubEnabled() +
                '}';
    }
}

