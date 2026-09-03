package com.sdlc.pipeline.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipelineState {
    public String userStory;
    public int currentPhase;
    public Map<String, Artifact> artifacts;
    public Map<Integer, PhaseStatus> phaseStatus;
    public List<Decision> decisions;
    public List<Blocker> blockers;
    public List<String> executionLog;

    public PipelineState(String userStory) {
        this.userStory = userStory;
        this.currentPhase = 1;
        this.artifacts = new HashMap<>();
        this.phaseStatus = new HashMap<>();
        this.decisions = new ArrayList<>();
        this.blockers = new ArrayList<>();
        this.executionLog = new ArrayList<>();
    }

    public void addArtifact(Artifact artifact) {
        String key = "phase_" + artifact.type.name().toLowerCase();
        this.artifacts.put(key, artifact);
    }

    public void addBlocker(Blocker blocker) {
        this.blockers.add(blocker);
        this.phaseStatus.put(blocker.phase, PhaseStatus.BLOCKED);
    }

    public void addDecision(Decision decision) {
        this.decisions.add(decision);
    }

    public void advancePhase() {
        if (this.currentPhase < 8) {
            this.currentPhase++;
        }
    }
}

