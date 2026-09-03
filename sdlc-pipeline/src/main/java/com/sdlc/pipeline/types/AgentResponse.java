package com.sdlc.pipeline.types;

import java.util.ArrayList;
import java.util.List;

public class AgentResponse {
    public boolean success;
    public int phase;
    public List<Artifact> artifactsGenerated;
    public List<Blocker> blockers;
    public List<Decision> decisions;
    public List<String> executionLog;
    public boolean nextPhaseReady;

    public AgentResponse(int phase) {
        this.phase = phase;
        this.artifactsGenerated = new ArrayList<>();
        this.blockers = new ArrayList<>();
        this.decisions = new ArrayList<>();
        this.executionLog = new ArrayList<>();
        this.nextPhaseReady = false;
    }
}

