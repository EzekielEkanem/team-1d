package edu.vassar.cmpu203.vassareats.model;

public class InputReport {
    private boolean statusSuccess;
    private int errorCode;

    public InputReport(boolean status) {
        this.statusSuccess = status;
    }

    public InputReport(boolean status, int errorCode) {
        this.statusSuccess = status;
        this.errorCode = errorCode;
    }

    public boolean getStatusSuccess() {
        return statusSuccess;
    }

    public void setStatusSuccess(boolean statusSuccess) {
        this.statusSuccess = statusSuccess;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
