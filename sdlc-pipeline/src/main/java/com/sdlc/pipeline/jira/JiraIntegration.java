package com.sdlc.pipeline.jira;

import com.sdlc.pipeline.config.ConfigLoader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;

/**
 * Simplified JIRA Integration
 * Token-based Authentication
 * Uses api-configuration.properties for settings
 */
public class JiraIntegration {
    private ConfigLoader config;
    private static final String API_PATH = "/rest/api/3/issues/";

    public JiraIntegration() {
        this.config = ConfigLoader.getInstance();
    }

    /**
     * Fetch JIRA issue by key (e.g., "PROJ-123")
     * Returns formatted issue data for approval
     */
    public JiraIssueData fetchIssue(String issueKey) {
        if (!config.isJiraEnabled()) {
            return new JiraIssueData(false, "❌ JIRA not enabled in api-configuration.properties", null);
        }

        if (issueKey == null || issueKey.isEmpty()) {
            return new JiraIssueData(false, "❌ Issue key cannot be empty", null);
        }

        try {
            System.out.println("\n🔄 Fetching JIRA issue: " + issueKey);
            String url = config.getJiraUrl() + API_PATH + issueKey;
            
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", getAuthHeader());
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                String jsonResponse = readResponse(conn);
                String formatted = formatIssueForApproval(jsonResponse, issueKey);
                return new JiraIssueData(true, "✅ Issue fetched successfully", formatted);
            } else if (responseCode == 401) {
                return new JiraIssueData(false, "❌ Authentication failed (401). Check jira.api.token", null);
            } else if (responseCode == 404) {
                return new JiraIssueData(false, "❌ Issue not found: " + issueKey, null);
            } else {
                return new JiraIssueData(false, "❌ Error " + responseCode + " from JIRA", null);
            }

        } catch (IOException e) {
            return new JiraIssueData(false, "❌ Connection error: " + e.getMessage(), null);
        }
    }

    /**
     * Test JIRA connection
     */
    public boolean testConnection() {
        if (!config.isJiraEnabled()) {
            System.out.println("⚠️  JIRA not enabled in configuration");
            return false;
        }

        try {
            System.out.println("🔄 Testing JIRA connection...");
            URL url = new URL(config.getJiraUrl() + "/rest/api/3/myself");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", getAuthHeader());

            if (conn.getResponseCode() == 200) {
                System.out.println("✅ JIRA connection successful!");
                return true;
            } else {
                System.out.println("❌ JIRA connection failed: HTTP " + conn.getResponseCode());
                return false;
            }
        } catch (IOException e) {
            System.out.println("❌ Cannot connect to JIRA: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate Basic Auth header with token
     * Format: email:api_token (Base64 encoded)
     */
    private String getAuthHeader() {
        String email = config.getJiraEmail();
        String token = config.getJiraApiToken();
        String credentials = email + ":" + token;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encoded;
    }

    /**
     * Format JIRA issue for user approval
     */
    private String formatIssueForApproval(String jsonResponse, String issueKey) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n").append("=".repeat(70)).append("\n");
        sb.append("📋 JIRA ISSUE DETAILS - ").append(issueKey).append("\n");
        sb.append("=".repeat(70)).append("\n\n");

        // Extract fields
        String title = extractField(jsonResponse, "\"summary\":\"", "\"");
        String description = extractField(jsonResponse, "\"description\":\"", "\"");
        String type = extractField(jsonResponse, "\"issuetype\":{\"name\":\"", "\"");
        String status = extractField(jsonResponse, "\"status\":{\"name\":\"", "\"");
        String priority = extractField(jsonResponse, "\"priority\":{\"name\":\"", "\"");

        if (!title.isEmpty()) {
            sb.append("📌 TITLE\n").append(title).append("\n\n");
        }

        if (!description.isEmpty()) {
            sb.append("📝 DESCRIPTION\n").append(description).append("\n\n");
        }

        if (!type.isEmpty()) {
            sb.append("🏷️  TYPE: ").append(type).append("\n");
        }
        if (!status.isEmpty()) {
            sb.append("📊 STATUS: ").append(status).append("\n");
        }
        if (!priority.isEmpty()) {
            sb.append("⚡ PRIORITY: ").append(priority).append("\n");
        }

        sb.append("\n").append("=".repeat(70)).append("\n");
        sb.append("✅ Do you want to proceed with this issue? (yes/no)\n");
        sb.append("=".repeat(70)).append("\n");

        return sb.toString();
    }

    /**
     * Simple field extraction from JSON
     */
    private String extractField(String json, String startMarker, String endMarker) {
        int start = json.indexOf(startMarker);
        if (start == -1) return "";
        
        start += startMarker.length();
        int end = json.indexOf(endMarker, start);
        
        if (end == -1) return "";
        return json.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"");
    }

    /**
     * Read HTTP response
     */
    private String readResponse(HttpURLConnection conn) throws IOException {
        Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    /**
     * Simple data class for issue response
     */
    public static class JiraIssueData {
        public boolean success;
        public String message;
        public String formattedData;

        public JiraIssueData(boolean success, String message, String formattedData) {
            this.success = success;
            this.message = message;
            this.formattedData = formattedData;
        }
    }
}

