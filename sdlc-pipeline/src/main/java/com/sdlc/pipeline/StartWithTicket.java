package com.sdlc.pipeline;

import com.sdlc.pipeline.agents.orchestrator.SDLCOrchestrator;
import com.sdlc.pipeline.agent.SDLCAgent;
import com.sdlc.pipeline.types.*;

/**
 * SDLC Pipeline Entry Point
 * 
 * STEP 1 (MANDATORY): Extract JIRA Ticket Details
 * - Reads JIRA config from api-configuration.properties
 * - Connects to JIRA via REST API with token-based auth
 * - Fetches ticket details
 * - Displays to user for approval
 * 
 * STEP 2 ONWARDS: Execute SDLC Phases (Phase 1-8)
 * 
 * Usage: java StartWithTicket
 */
public class StartWithTicket {
    
    public static void main(String[] args) {
        // JIRA Ticket ID (your input)
        String ticketId = "EDJOBASA-22";

        System.out.println("\n" + "=".repeat(70));
        System.out.println("🎯 SDLC PIPELINE - JIRA TICKET ENTRY");
        System.out.println("=".repeat(70));

        // Initialize orchestrator (loads api-configuration.properties)
        SDLCOrchestrator orchestrator = new SDLCOrchestrator();
        
        // Register agents for all phases
        orchestrator.registerAgent(1, createRequirementsAgent());
        orchestrator.registerAgent(2, createArchitectureAgent());
        // Phase 3-8: Register additional agents here

        // ===== STEP 1: EXTRACT JIRA TICKET (MANDATORY) =====
        boolean ticketApproved = orchestrator.extractJiraTicketDetails(ticketId);
        
        if (!ticketApproved) {
            System.out.println("\n❌ Pipeline execution cancelled - ticket not approved");
            System.exit(1);
        }

        // ===== STEP 2: EXECUTE SDLC PHASES =====
        boolean pipelineSuccess = orchestrator.executePhases();
        
        if (pipelineSuccess) {
            System.out.println("\n✅ SDLC Pipeline completed successfully!");
        } else {
            System.out.println("\n❌ SDLC Pipeline failed");
            System.exit(1);
        }
    }

    private static SDLCAgent createRequirementsAgent() {
        return (input, state) -> {
            String userStory = (String) input.get("user_story");
            
            String requirementsContent = 
                "# Requirements Document\n\n" +
                "## From JIRA Ticket\n" + userStory + "\n\n" +
                "## Functional Requirements\n" +
                "- Analyzed from ticket\n\n" +
                "## Success Criteria\n" +
                "- All requirements documented\n";
            
            Artifact artifact = new Artifact(
                ArtifactType.REQUIREMENTS,
                "requirements.md",
                requirementsContent,
                "Phase 1: Requirements Specialist"
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
                "## Based on Requirements\n" +
                "Designed from JIRA ticket requirements\n\n" +
                "## Components\n" +
                "1. Frontend 2. API Gateway 3. Business Logic 4. Database\n";
            
            Artifact artifact = new Artifact(
                ArtifactType.ARCHITECTURE,
                "architecture.md",
                architectureContent,
                "Phase 2: Architecture Designer"
            );
            
            AgentResponse response = new AgentResponse(2);
            response.success = true;
            response.artifactsGenerated.add(artifact);
            response.nextPhaseReady = true;
            
            return response;
        };
    }
}

