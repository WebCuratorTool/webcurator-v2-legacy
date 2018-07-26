package org.webcurator.core.harvester.agent;

public class HarvestAgentStatusDTO {
    private String jobNumber;
    private int status;
    private String message;

    public HarvestAgentStatusDTO(String jobNumber, int status, String message) {
        this.jobNumber = jobNumber;
        this.status = status;
        this.message = message;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
