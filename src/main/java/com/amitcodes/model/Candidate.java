package com.amitcodes.model;

import com.amitcodes.election.LeaderElector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Candidate implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger( Candidate.class );
    
    private LeaderElector leaderElector;
    
    public Candidate(LeaderElector leaderElector) {
        this.leaderElector = leaderElector;
    }
    
    @Override
    public void run() {
        try {
            ElectionResult electionResult = leaderElector.contest();
            LOGGER.info( "Results: {}", electionResult );
        } catch (Exception e) {
            LOGGER.info( "Election failed", e );
        }
    }
}
