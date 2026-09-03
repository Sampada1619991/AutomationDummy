package com.sdlc.pipeline.agent;

import com.sdlc.pipeline.types.*;
import java.util.Map;

/**
 * Interface for SDLC Phase Agents
 * Each phase implements this to execute its logic
 */
public interface SDLCAgent {
    AgentResponse execute(Map<String, Object> input, PipelineState state);
}



