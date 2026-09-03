package com.sdlc.pipeline.demo;

import com.sdlc.pipeline.agents.orchestrator.SDLCOrchestrator;
import com.sdlc.pipeline.agent.SDLCAgent;
import com.sdlc.pipeline.types.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * SDLC Pipeline Demo with JIRA Integration
 * 
 * STEP 1 (MANDATORY): Extract JIRA Ticket
 * - Accepts JIRA ticket ID as input
 * - Reads config from api-configuration.properties
 * - Connects to JIRA via REST API
 * - Fetches and displays ticket details
 * - Gets user approval
 * 
 * STEP 2 ONWARDS: Execute SDLC Phases
 */
public class PipelineDemoWithJira {
    
    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("🎯 SDLC PIPELINE DEMO - JIRA TICKET INTEGRATION");
        System.out.println("=".repeat(70) + "\n");

        // Get JIRA ticket ID from user
        System.out.print("📌 Enter JIRA Ticket ID (e.g., EDJOBASA-22): ");
        String ticketId = new Scanner(System.in).nextLine().trim();

        if (ticketId.isEmpty()) {
            System.out.println("❌ Ticket ID cannot be empty");
            System.exit(1);
        }

        // Initialize orchestrator (reads api-configuration.properties)
        SDLCOrchestrator orchestrator = new SDLCOrchestrator();
        
        // Register agents for all phases
        orchestrator.registerAgent(1, createRequirementsAgent());
        orchestrator.registerAgent(2, createArchitectureAgent());

        // ===== STEP 1: EXTRACT JIRA TICKET (MANDATORY) =====
        boolean ticketApproved = orchestrator.extractJiraTicketDetails(ticketId);
        
        if (!ticketApproved) {
            System.out.println("\n❌ Pipeline execution cancelled");
            System.exit(1);
        }

        // ===== STEP 2: EXECUTE SDLC PHASES =====
        boolean pipelineSuccess = orchestrator.executePhases();
        
        if (!pipelineSuccess) {
            System.out.println("\n❌ Pipeline execution failed");
            System.exit(1);
        }
    }

    private static SDLCAgent createRequirementsAgent() {
        return (input, state) -> {
            String userStory = (String) input.get("user_story");
            
            String requirementsContent = 
                "# Requirements Document\n\n" +
                "## User Story\n" + userStory + "\n\n" +
                "## Functional Requirements\n" +
                "- Core features identified\n" +
                "- User workflows mapped\n\n" +
                "## Success Criteria\n" +
                "- Requirements complete\n";
            
            Artifact artifact = new Artifact(
                ArtifactType.REQUIREMENTS,
                "requirements.md",
                requirementsContent,
                "Requirements Agent (Phase 1)"
            );
            
            AgentResponse response = new AgentResponse(1);
            response.success = true;
            response.artifactsGenerated.add(artifact);
            response.nextPhaseReady = true;
            
            return response;
        };
    }

    private static SDLCAgent createArchitectureAgent() {
        return (input, state) -> {
            String architectureContent =
                "# System Architecture\n\n" +
                "## Components\n" +
                "1. Frontend - User Interface\n" +
                "2. API Gateway - Request routing\n" +
                "3. Business Logic - Core services\n" +
                "4. Database - Data persistence\n\n" +
                "## Technology Stack\n" +
                "- Backend: Java Spring Boot\n" +
                "- Database: PostgreSQL\n";
            
            Artifact artifact = new Artifact(
                ArtifactType.ARCHITECTURE,
                "architecture.md",
                architectureContent,
                "Architecture Agent (Phase 2)"
            );
            
            AgentResponse response = new AgentResponse(2);
            response.success = true;
            response.artifactsGenerated.add(artifact);
            response.nextPhaseReady = true;
            
            return response;
        };
    }
}

