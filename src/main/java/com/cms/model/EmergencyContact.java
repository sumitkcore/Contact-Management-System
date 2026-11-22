package com.cms.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Concrete Subclass representing an Emergency Contact (Doctor, Hospital, Ambulance, Next of Kin).
 * 
 * Demonstrates:
 * - Inheritance (extends Contact)
 * - Polymorphism (overrides getContactType(), getSummary(), getSpecificDetailsMap(), validate())
 * - Critical domain modeling for healthcare & safety emergencies.
 */
public class EmergencyContact extends Contact {
    private static final long serialVersionUID = 1L;

    private EmergencyPriority priority;
    private BloodGroup bloodGroup;
    private String medicalNotes;
    private String knownAllergies;
    private String alternateEmergencyPhone;
    private String hospitalAffiliation;

    public EmergencyContact(String id, String firstName, String lastName) {
        super(id, firstName, lastName);
        this.priority = EmergencyPriority.PRIMARY;
        this.bloodGroup = BloodGroup.UNKNOWN;
        this.medicalNotes = "";
        this.knownAllergies = "";
        this.alternateEmergencyPhone = "";
        this.hospitalAffiliation = "";
        this.group = ContactGroup.EMERGENCY;
    }

    public EmergencyContact(String firstName, String lastName) {
        this(null, firstName, lastName);
    }

    @Override
    public ContactType getContactType() {
        return ContactType.EMERGENCY;
    }

    public EmergencyPriority getPriority() {
        return priority != null ? priority : EmergencyPriority.PRIMARY;
    }

    public void setPriority(EmergencyPriority priority) {
        this.priority = priority != null ? priority : EmergencyPriority.PRIMARY;
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup != null ? bloodGroup : BloodGroup.UNKNOWN;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup != null ? bloodGroup : BloodGroup.UNKNOWN;
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public String getMedicalNotes() {
        return medicalNotes;
    }

    public void setMedicalNotes(String medicalNotes) {
        this.medicalNotes = medicalNotes != null ? medicalNotes.trim() : "";
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public String getKnownAllergies() {
        return knownAllergies;
    }

    public void setKnownAllergies(String knownAllergies) {
        this.knownAllergies = knownAllergies != null ? knownAllergies.trim() : "";
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public String getAlternateEmergencyPhone() {
        return alternateEmergencyPhone;
    }

    public void setAlternateEmergencyPhone(String alternateEmergencyPhone) {
        this.alternateEmergencyPhone = alternateEmergencyPhone != null ? alternateEmergencyPhone.trim() : "";
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public String getHospitalAffiliation() {
        return hospitalAffiliation;
    }

    public void setHospitalAffiliation(String hospitalAffiliation) {
        this.hospitalAffiliation = hospitalAffiliation != null ? hospitalAffiliation.trim() : "";
        this.updatedAt = java.time.LocalDateTime.now();
    }

    @Override
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(getPriority().name()).append(" RESPONDER");
        if (bloodGroup != null && bloodGroup != BloodGroup.UNKNOWN) {
            sb.append(" • Blood: ").append(bloodGroup.getLabel());
        }
        if (hospitalAffiliation != null && !hospitalAffiliation.isEmpty()) {
            sb.append(" • ").append(hospitalAffiliation);
        }
        return sb.toString();
    }

    @Override
    public Map<String, String> getSpecificDetailsMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Priority Level", getPriority().getDescription());
        if (bloodGroup != null && bloodGroup != BloodGroup.UNKNOWN) {
            map.put("Blood Group", bloodGroup.getLabel());
        }
        if (alternateEmergencyPhone != null && !alternateEmergencyPhone.isEmpty()) {
            map.put("Alternate SOS Phone", alternateEmergencyPhone);
        }
        if (hospitalAffiliation != null && !hospitalAffiliation.isEmpty()) {
            map.put("Hospital / Clinic", hospitalAffiliation);
        }
        if (knownAllergies != null && !knownAllergies.isEmpty()) {
            map.put("Known Allergies", knownAllergies);
        }
        if (medicalNotes != null && !medicalNotes.isEmpty()) {
            map.put("Medical Notes", medicalNotes);
        }
        return map;
    }

    @Override
    protected void appendSpecificVCardDetails(StringBuilder sb) {
        sb.append("X-CMS-EMERGENCY-PRIORITY:").append(getPriority().name()).append("\r\n");
        if (bloodGroup != null && bloodGroup != BloodGroup.UNKNOWN) {
            sb.append("X-CMS-BLOOD-GROUP:").append(bloodGroup.getLabel()).append("\r\n");
        }
        if (alternateEmergencyPhone != null && !alternateEmergencyPhone.isEmpty()) {
            sb.append("TEL;TYPE=EMERGENCY:").append(alternateEmergencyPhone).append("\r\n");
        }
        if (hospitalAffiliation != null && !hospitalAffiliation.isEmpty()) {
            sb.append("ORG:").append(hospitalAffiliation).append("\r\n");
        }
    }

    @Override
    public ValidationResult validate() {
        Map<String, String> errors = new LinkedHashMap<>();
        if (firstName.isEmpty() && lastName.isEmpty()) {
            errors.put("Name", "Emergency contact name is mandatory.");
        }
        if (phoneNumbers.isEmpty() && alternateEmergencyPhone.isEmpty()) {
            errors.put("Phone", "An active emergency phone number is strictly required.");
        }
        if (errors.isEmpty()) {
            return ValidationResult.success();
        }
        return ValidationResult.failure(errors);
    }
}
