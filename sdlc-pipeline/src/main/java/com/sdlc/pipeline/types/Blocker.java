package com.sdlc.pipeline.types;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Blocker {
    public int phase;
    public String title;
    public String description;
    public BlockerSeverity severity;
    public List<String> resolutionSteps;
    public LocalDateTime createdAt;
    public boolean resolved;

    public Blocker(int phase, String title, String description, BlockerSeverity severity) {
        this.phase = phase;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.resolutionSteps = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.resolved = false;
    }
}

