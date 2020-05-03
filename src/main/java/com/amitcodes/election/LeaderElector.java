package com.amitcodes.election;

import com.amitcodes.model.ElectionResult;

/**
 * Used to perform leader election.
 */
public interface LeaderElector {
    /**
     * Used to participate in leader election.
     * @return ElectionResult. Invoke ElectionResult.isLeader() == true if elected as leader, false otherwise.
     */
    ElectionResult contest();
}
