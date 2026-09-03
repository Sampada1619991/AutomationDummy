package com.sdlc.pipeline.types;

import java.util.ArrayList;
import java.util.List;

public class Decision {
    public int phase;
    public String decisionPoint;
    public String choice;
    public String rationale;
    public List<String> alternatives;

    public Decision(int phase, String decisionPoint, String choice, String rationale) {
        this.phase = phase;
        this.decisionPoint = decisionPoint;
        this.choice = choice;
        this.rationale = rationale;
        this.alternatives = new ArrayList<>();
    }
}

