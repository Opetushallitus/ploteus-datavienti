package fi.vm.sade.model;

public class StatusObject {
    private double status;
    private double durationEstimate;
    private String statusText;
    private String frontendOutput;

    public String getFrontendOutput() {
        return frontendOutput;
    }

    public void setFrontendOutput(String frontendOutput) {
        this.frontendOutput = frontendOutput;
    }

    public double getStatus() {
        return status;
    }

    public void setStatus(double status) {
        this.status = status;
    }

    public double getDurationEstimate() {
        return durationEstimate;
    }

    public void setDurationEstimate(double durationEstimate) {
        this.durationEstimate = durationEstimate;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public void addFrontendOutput(String string) {
        if(frontendOutput == null){
            setFrontendOutput(string);
        }else{
            this.frontendOutput = this.frontendOutput.concat("\n" + string);
        }
    }
}
