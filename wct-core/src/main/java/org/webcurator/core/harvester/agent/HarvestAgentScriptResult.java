package org.webcurator.core.harvester.agent;

/**
 * Wrapper for the H3 org.netarchivesuite.heritrix3wrapper.ScriptResult class.
 * Used so there's no dependency between WCT Core and H3 wrapper classes.
 */
public class HarvestAgentScriptResult {
    private int responseCode;
    private int status;
    private String output;

    public HarvestAgentScriptResult() {}

    public HarvestAgentScriptResult(int responseCode, int status, String output) {
        this.responseCode = responseCode;
        this.status = status;
        this.output = output;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    @Override
    public String toString() {
        String lineSep = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append("Response Code: " + responseCode + lineSep);
        sb.append("Status: " + status + lineSep);
        sb.append(lineSep);
        sb.append(output);
        return sb.toString();
    }
}
