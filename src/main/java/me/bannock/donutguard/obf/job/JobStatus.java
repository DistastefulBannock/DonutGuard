package me.bannock.donutguard.obf.job;

public enum JobStatus {

    QUEUED("Queued"),
    RUNNING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled"),
    NOT_FOUND("Not Found");

    JobStatus(String friendlyName){
        this.FRIENDLY_NAME = friendlyName;
    }
    private final String FRIENDLY_NAME;

    public String getFriendlyName() {
        return FRIENDLY_NAME;
    }

}
