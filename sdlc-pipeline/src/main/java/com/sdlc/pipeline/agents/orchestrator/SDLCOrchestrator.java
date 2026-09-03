package com.sdlc.pipeline.agents.orchestrator;

import com.sdlc.pipeline.types.*;
import com.sdlc.pipeline.agent.SDLCAgent;
import com.sdlc.pipeline.config.ConfigLoader;
import com.sdlc.pipeline.jira.JiraIntegration;
import java.util.*;
import java.util.Scanner;

/**
 * SDLC Orchestrator - Master Agent
 * Coordinates all 8 SDLC phases
 * Entry Point: Can accept JIRA ticket ID or manual user story
 */
public class SDLCOrchestrator {
    private PipelineState state;
    private Map<Integer, SDLCAgent> phaseAgents;
    private Map<Integer, String> phaseNames;
    private ConfigLoader config;
    private JiraIntegration jira;
    private String jiraTicketId;

    public SDLCOrchestrator() {
        this.phaseAgents = new HashMap<>();
        this.phaseNames = new HashMap<>();
        this.config = ConfigLoader.getInstance();
        this.jira = new JiraIntegration();
        initPhaseNames();
    }

    /*
     * ========== STEP 1 (MANDATORY): EXTRACT JIRA TICKET & REQUEST APPROVAL ==========
     * This is the FIRST and MANDATORY step in SDLC pipeline
     * All SDLC phases depend on successful JIRA ticket extraction and user approval
     */

    /**
     * STEP 1: Extract JIRA Ticket Details
     * 
     * This is the MANDATORY first step for SDLC pipeline execution
     * 
     * Process:
     * 1. Read JIRA config from api-configuration.properties
     * 2. Connect to JIRA via REST API using token-based auth
     * 3. Fetch ticket details from JIRA
     * 4. Display extracted ticket info to user
     * 5. Request user approval before proceeding to phases
     * 6. If approved, pipeline proceeds to Phase 1
     * 
     * @param ticketId JIRA ticket ID (e.g., "EDJOBASA-22")
     * @return true if ticket extracted and approved, false otherwise
     */
    public boolean extractJiraTicketDetails(String ticketId) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📌 STEP 1: EXTRACT JIRA TICKET DETAILS");
        System.out.println("=".repeat(70));

        // Step 1.1: Validate input
        if (ticketId == null || ticketId.trim().isEmpty()) {
            System.err.println("❌ ERROR: Ticket ID cannot be empty");
            System.err.println("   Please provide valid JIRA ticket ID (e.g., EDJOBASA-22)");
            return false;
        }

        this.jiraTicketId = ticketId.trim();
        System.out.println("\n🎯 Target Ticket: " + this.jiraTicketId);

        // Step 1.2: Verify JIRA integration enabled
        if (!config.isJiraEnabled()) {
            System.err.println("❌ ERROR: JIRA integration not enabled");
            System.err.println("   Please configure in api-configuration.properties:");
            System.err.println("     jira.enabled=true");
            System.err.println("     jira.url=https://your-jira-instance.atlassian.net");
            System.err.println("     jira.email=your-email@company.com");
            System.err.println("     jira.api.token=your_token_here");
            return false;
        }

        // Step 1.3: Connect to JIRA and fetch ticket details
        System.out.println("\n🔍 Connecting to JIRA and fetching ticket details...");
        JiraIntegration.JiraIssueData issueData = jira.fetchIssue(this.jiraTicketId);

        if (!issueData.success) {
            System.err.println("❌ ERROR: " + issueData.message);
            return false;
        }

        // Step 1.4: Display extracted ticket info to user
        System.out.println("\n✅ Ticket extracted successfully!\n");
        System.out.println(issueData.formattedData);

        // Step 1.5: Request user approval
        System.out.print("❓ Do you approve this ticket to proceed with SDLC pipeline? (yes/no): ");
        String approval = new Scanner(System.in).nextLine().trim().toLowerCase();

        if (!approval.equals("yes")) {
            System.out.println("❌ Ticket rejected by user. Pipeline execution cancelled.");
            return false;
        }

        System.out.println("✅ Ticket approved! Proceeding to SDLC phases...\n");

        // Store ticket details in state for use by phases
        state = new PipelineState(issueData.formattedData);
        for (int i = 1; i <= 8; i++) {
            state.phaseStatus.put(i, PhaseStatus.PENDING);
        }
        state.executionLog.add("Step 1 Complete: JIRA ticket " + this.jiraTicketId + " extracted and approved");
        
        return true;
    }

    /*
     * ========== INTERNAL PHASE INITIALIZATION ==========
     */

    private void initPhaseNames() {
        phaseNames.put(1, "Requirements Specialist");
        phaseNames.put(2, "Architecture Designer");
        phaseNames.put(3, "Senior Design Reviewer");
        phaseNames.put(4, "Project Planner");
        phaseNames.put(5, "Code Implementer");
        phaseNames.put(6, "Peer Code Reviewer");
        phaseNames.put(7, "Verification & Testing Lead");
        phaseNames.put(8, "PR Coordinator");
    }

    public void registerAgent(int phase, SDLCAgent agent) {
        if (phase >= 1 && phase <= 8) {
            phaseAgents.put(phase, agent);
            System.out.println("✓ Agent registered for Phase " + phase + ": " + phaseNames.get(phase));
        }
    }

    public AgentResponse executePhase(int phase) {
        if (state == null) {
            AgentResponse response = new AgentResponse(phase);
            response.success = false;
            response.executionLog.add("No active pipeline");
            return response;
        }

        System.out.println("\n📋 PHASE " + phase + ": " + phaseNames.get(phase));
        System.out.println("─".repeat(70));

        SDLCAgent agent = phaseAgents.get(phase);
        if (agent == null) {
            AgentResponse response = new AgentResponse(phase);
            response.success = false;
            response.executionLog.add("No agent registered for Phase " + phase);
            return response;
        }

        // Check for blockers
        long activeBlockers = state.blockers.stream().filter(b -> !b.resolved).count();
        if (activeBlockers > 0) {
            System.out.println("⚠️  Pipeline has active blockers");
            AgentResponse response = new AgentResponse(phase);
            response.success = false;
            return response;
        }

        // Execute agent
        try {
            Map<String, Object> input = prepareInput(phase);
            AgentResponse response = agent.execute(input, state);

            if (response.success) {
                System.out.println("✅ Phase " + phase + " completed successfully");
                
                // Add artifacts
                for (Artifact artifact : response.artifactsGenerated) {
                    state.addArtifact(artifact);
                    System.out.println("   📄 Generated: " + artifact.name + " (v" + artifact.version + ")");
                }
                
                // Add decisions
                response.decisions.forEach(state::addDecision);
                
                // Add blockers
                response.blockers.forEach(state::addBlocker);
                response.blockers.forEach(b -> System.out.println("   🚫 Blocker: " + b.title));

                response.nextPhaseReady = response.blockers.isEmpty();
            } else {
                System.out.println("❌ Phase " + phase + " failed");
                response.executionLog.forEach(log -> System.out.println("   ⚠️  " + log));
            }

            return response;

        } catch (Exception e) {
            System.out.println("❌ Error executing Phase " + phase + ": " + e.getMessage());
            AgentResponse response = new AgentResponse(phase);
            response.success = false;
            response.executionLog.add(e.getMessage());
            return response;
        }
    }

    private Map<String, Object> prepareInput(int phase) {
        Map<String, Object> input = new HashMap<>();
        input.put("phase", phase);
        input.put("user_story", state.userStory);
        
        // Add relevant artifacts
        if (phase >= 2) {
            Artifact req = state.artifacts.get("phase_requirements");
            if (req != null) input.put("requirements", req.content);
        }
        if (phase >= 3) {
            Artifact arch = state.artifacts.get("phase_architecture");
            if (arch != null) input.put("architecture", arch.content);
        }
        if (phase >= 4) {
            Artifact review = state.artifacts.get("phase_design_review");
            if (review != null) input.put("design_review", review.content);
        }
        
        return input;
    }

    /**
     * STEP 2 ONWARDS: Execute SDLC Phases
     * 
     * IMPORTANT: This method can ONLY be called after STEP 1 (extractJiraTicketDetails) completes successfully
     * 
     * Executes all 8 SDLC phases in sequence:
     * Phase 1: Requirements Specialist
     * Phase 2: Architecture Designer
     * Phase 3-8: Other phases
     * 
     * @return true if pipeline completed successfully
     */
    public boolean executePhases() {
        if (state == null) {
            System.err.println("❌ ERROR: STEP 1 (JIRA ticket extraction) must be completed first");
            System.err.println("   Please call extractJiraTicketDetails(ticketId) first");
            return false;
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("🚀 STARTING SDLC PHASES (Continuous Execution)");
        System.out.println("=".repeat(70));

        state.currentPhase = 1;
        state.phaseStatus.put(1, PhaseStatus.IN_PROGRESS);

        for (int phase = 1; phase <= 8; phase++) {
            AgentResponse response = executePhase(phase);

            if (!response.success) {
                System.out.println("\n❌ Pipeline halted at Phase " + phase);
                return false;
            }

            if (!response.blockers.isEmpty()) {
                System.out.println("\n⚠️  Pipeline blocked at Phase " + phase);
                System.out.println("   Please resolve blockers before continuing");
                return false;
            }

            // Automatically advance to next phase (continuous flow)
            if (phase < 8) {
                advancePhase();
            }
        }

        printStatus();
        return true;
    }

    /**
     * LEGACY: Kept for backward compatibility
     * Deprecated - Use extractJiraTicketDetails() + executePhases() instead
     */
    @Deprecated
    public void runPipeline(String userStory, boolean autoAdvance) {
        // Initialize state
        if (state == null) {
            state = new PipelineState(userStory);
            for (int i = 1; i <= 8; i++) {
                state.phaseStatus.put(i, PhaseStatus.PENDING);
            }
            state.phaseStatus.put(1, PhaseStatus.IN_PROGRESS);
            state.executionLog.add("Pipeline initialized from: " + userStory.substring(0, Math.min(50, userStory.length())) + "...");
        }

        for (int phase = 1; phase <= 8; phase++) {
            AgentResponse response = executePhase(phase);

            if (!response.success) {
                System.out.println("\n❌ Pipeline halted at Phase " + phase);
                break;
            }

            if (!response.blockers.isEmpty()) {
                System.out.println("\n⚠️  Pipeline blocked at Phase " + phase);
                System.out.println("   Please resolve blockers before continuing");
                break;
            }

            if (response.nextPhaseReady && autoAdvance && phase < 8) {
                advancePhase();
            } else if (phase < 8) {
                System.out.println("\n⏸️  Phase " + phase + " complete. Review artifacts before continuing.");
                break;
            }
        }

        printStatus();
    }

    public void advancePhase() {
        if (state != null && state.currentPhase < 8) {
            int oldPhase = state.currentPhase;
            state.phaseStatus.put(oldPhase, PhaseStatus.COMPLETED);
            state.advancePhase();
            state.phaseStatus.put(state.currentPhase, PhaseStatus.IN_PROGRESS);
            state.executionLog.add("Advanced from Phase " + oldPhase + " to Phase " + state.currentPhase);
            System.out.println("➡️  Advanced to Phase " + state.currentPhase);
        }
    }

    public void printStatus() {
        if (state == null) return;

        System.out.println("\n" + "=".repeat(70));
        System.out.println("📊 PIPELINE STATUS");
        System.out.println("=".repeat(70));
        System.out.println("Current Phase: " + state.currentPhase);
        System.out.println("Artifacts: " + state.artifacts.size());
        System.out.println("Decisions: " + state.decisions.size());
        System.out.println("Active Blockers: " + state.blockers.stream().filter(b -> !b.resolved).count());
        System.out.println("\nPhase Statuses:");
        for (int i = 1; i <= 8; i++) {
            PhaseStatus status = state.phaseStatus.get(i);
            String emoji = status == PhaseStatus.COMPLETED ? "✅" : 
                          status == PhaseStatus.IN_PROGRESS ? "🔄" : "⏳";
            System.out.println("  Phase " + i + ": " + emoji + " " + status);
        }
        System.out.println("=".repeat(70) + "\n");
    }

    public PipelineState getState() {
        return state;
    }
}



