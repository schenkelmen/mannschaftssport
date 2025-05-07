package de.hsos.swa.entity;

public class PersonPass {
    private String personId;
    private boolean valid;
    private String issuedDate;   // optional: Ausstellungsdatum als ISO-String

    public PersonPass() {}

    public PersonPass(String personId, boolean valid, String issuedDate) {
        this.personId   = personId;
        this.valid      = valid;
        this.issuedDate = issuedDate;
    }

    public String getPersonId() { return personId; }
    public void setPersonId(String personId) { this.personId = personId; }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getIssuedDate() { return issuedDate; }
    public void setIssuedDate(String issuedDate) { this.issuedDate = issuedDate; }
}
