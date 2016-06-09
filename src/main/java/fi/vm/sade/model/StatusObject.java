package fi.vm.sade.model;

public class StatusObject {
	private double status;
	private double durationEstimate;
	private String statusText;
	
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
}
