package com.amitcodes.model;

public class ElectionResult {
    private boolean leader;
    private String leaseId;
    public ElectionResult(boolean leader, String leaseId) {
        this.leader = leader;
        this.leaseId = leaseId;
    }
    
    public boolean isLeader() {
        return leader;
    }
    
    public void setLeader(boolean leader) {
        this.leader = leader;
    }
    
    public String getLeaseId() {
        return leaseId;
    }
    
    public void setLeaseId(String leaseId) {
        this.leaseId = leaseId;
    }
    
    @Override
    public String toString() {
        return "ElectionResult{" + "leader=" + leader + ", leaseId='" + leaseId + '\'' + '}';
    }
}
