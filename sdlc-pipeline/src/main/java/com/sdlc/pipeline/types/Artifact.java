package com.sdlc.pipeline.types;

import java.time.LocalDateTime;

public class Artifact {
    public ArtifactType type;
    public String name;
    public String content;
    public int version;
    public LocalDateTime createdAt;
    public String createdBy;
    public String filePath;

    public Artifact(ArtifactType type, String name, String content, String createdBy) {
        this.type = type;
        this.name = name;
        this.content = content;
        this.version = 1;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
    }

    public void incrementVersion() {
        this.version++;
    }
}

