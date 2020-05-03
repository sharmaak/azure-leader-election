package com.amitcodes;


import com.amitcodes.election.AzureBlobLeaderElector;
import com.amitcodes.election.LeaderElector;
import com.amitcodes.model.Candidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger( App.class );
    
    public static void main(String[] args) throws InterruptedException {
        
        if(args.length < 2) {
            System.err.println("Must pass {account-name} {account-key} {blob-container-name} {blob-name}" +
                               "for azure blob in the specified order.");
        }
        final String azBlobAccountName     = args[0];
        final String azBlobAccountKey      = args[1];
        final String azBlobContainerName   = args[2];
        final String azBlobName            = args[3];
        
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor( 1 );
    
        LeaderElector leaderElector;
        // Create 5 leader election candidates. These candidates run every 65 seconds and contest for leader election.
        for( int i = 0; i < 1; i++ ) {
            leaderElector = new AzureBlobLeaderElector( azBlobAccountName, azBlobAccountKey, azBlobContainerName, azBlobName );
            executor.scheduleAtFixedRate( new Candidate( leaderElector ), 5, 65, TimeUnit.SECONDS );
        }
        
        Thread.sleep( 3_00_000 /* 5 minutes */ );
    }
}
