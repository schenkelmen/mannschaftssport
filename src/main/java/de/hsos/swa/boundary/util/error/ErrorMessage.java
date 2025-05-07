package de.hsos.swa.boundary.util.error;

public class ErrorMessage {
    public int internalId;
    public String message;
    public String details;

    public ErrorMessage() {
    }

    public ErrorMessage(int internalId, String message, String details) {
        this.internalId = internalId;
        this.message = message;
        this.details = details;
    }

    public String getDetails() {
        return details;
    }
    public int getInternalId() {
        return internalId;
    }
    public String getMessage() {
        return message;
    }
    public void setDetails(String details) {
        this.details = details;
    }
    public void setInternalId(int internalId) {
        this.internalId = internalId;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    @Override
    public String toString() {
        return this.internalId + ": " + this.message + " - " + this.details;
    }
}
