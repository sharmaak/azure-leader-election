package com.amitcodes.election;

import com.amitcodes.model.ElectionResult;
import com.azure.core.http.HttpHeader;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.RequestConditions;
import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.specialized.BlobLeaseClient;
import com.azure.storage.blob.specialized.BlobLeaseClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Locale;

/**
 * Used to perform leader election. For a given blob, one instance should be created in a process.
 */
public class AzureBlobLeaderElector implements LeaderElector {
    private static final Logger LOGGER = LoggerFactory.getLogger( AzureBlobLeaderElector.class );
    
    private static final int LEASE_DURATION_SEC = -1;
    private static final RequestConditions REQUEST_CONDITIONS = null;
    private static final Duration TIMEOUT = Duration.ofSeconds( 10L );
    private static final Context CONTEXT = Context.NONE;
    
    private BlobLeaseClient blobLeaseClient;
    
    /**
     * Creates an instance of LeaderElector.
     * @param azBlobAccountName Account name of Azure storage account
     * @param azBlobAccountKey Account key of Azure storage account
     * @param azBlobContainerName Name of container under which the blob is located
     * @param azBlobName Name of the blob on which lock will be obtained
     */
    public AzureBlobLeaderElector(String azBlobAccountName,
                                  String azBlobAccountKey,
                                  String azBlobContainerName,
                                  String azBlobName) {
    
        StorageSharedKeyCredential credential = new StorageSharedKeyCredential( azBlobAccountName, azBlobAccountKey);
        String endpoint = String.format( Locale.ROOT, "https://%s.blob.core.windows.net", azBlobAccountName);
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().endpoint(endpoint).credential(credential).buildClient();
        // Create the container and return a container client object
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient( azBlobContainerName );
        BlobClient blobClient = containerClient.getBlobClient( azBlobName );
        blobLeaseClient = new BlobLeaseClientBuilder().blobClient(blobClient).buildClient();
    }
    
    /**
     * Used to participate in leader election.
     * @return ElectionResult. Invoke ElectionResult.isLeader() == true if elected as leader, false otherwise.
     */
    @Override
    public ElectionResult contest() {
        Response<String> response;
        try {
            response = blobLeaseClient.acquireLeaseWithResponse( LEASE_DURATION_SEC, REQUEST_CONDITIONS, TIMEOUT, CONTEXT );
            logSuccessResponse( response );
            return new ElectionResult( true, response.getValue() );
        } catch ( BlobStorageException e ) {
            logFailureResponse(e);
            return new ElectionResult( false, null );
        }
    }
    
    /**
     * Utility to log successful response when lock on targeted blob is granted
     */
    private void logSuccessResponse(Response< String > response) {
        if(!LOGGER.isDebugEnabled()) {return;}
        StringBuilder buffer = new StringBuilder();
        buffer.append( "HTTP " ).append( response.getStatusCode() ).append( System.lineSeparator() );
        for( HttpHeader header : response.getHeaders()) {
            buffer.append( header.getName() ).append( ": " ).append( header.getValue() ).append( System.lineSeparator() );
        }
        buffer.append( response.getValue() ).append( System.lineSeparator() );
        LOGGER.debug( "Received blob lease response: {}", buffer );
    }
    
    /**
     * Utility to log unsuccessful response when lock on targeted blob is rejected
     */
    private void logFailureResponse(BlobStorageException e) {
        if(!LOGGER.isDebugEnabled()) {return;}
        HttpResponse response = e.getResponse();
        StringBuilder buffer = new StringBuilder();
        buffer.append( "HTTP " ).append( response.getStatusCode() ).append( System.lineSeparator() );
        for( HttpHeader header : response.getHeaders()) {
            buffer.append( header.getName() ).append( ": " ).append( header.getValue() ).append( System.lineSeparator() );
        }
        buffer.append( System.lineSeparator() ).append( e.getServiceMessage() ).append( System.lineSeparator() );
        LOGGER.debug( "Received blob lease response: {}", buffer );
    }
    
}
