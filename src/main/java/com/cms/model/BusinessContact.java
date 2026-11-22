package com.cms.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Concrete Subclass representing a Business / Professional Contact.
 * 
 * Demonstrates:
 * - Inheritance (extends Contact)
 * - Polymorphism (overrides getContactType(), getSummary(), getSpecificDetailsMap(), validate())
 * - Encapsulation (company, jobTitle, department, workPhone, workEmail, linkedinUrl)
 */
public class BusinessContact extends Contact {
    private static final long serialVersionUID = 1L;

    private String companyName;
    private String jobTitle;
    private String department;
    private String workExtension;
    private String workEmail;
    private String linkedinUrl;

    public BusinessContact(String id, String firstName, String lastName) {
        super(id, firstName, lastName);
        this.companyName = "";
        this.jobTitle = "";
        this.department = "";
        this.workExtension = "";
        this.workEmail = "";
        this.linkedinUrl = "";
        this.group = ContactGroup.COLLEAGUES;
    }

    public BusinessContact(String firstName, String lastName) {
        this(null, firstName, lastName);
    }

    @Override
    public ContactType getContactType() {
        return ContactType.BUSINESS;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName != null ? companyName.trim() : "";
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle != null ? jobTitle.trim() : "";
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department != null ? department.trim() : "";
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public String getWorkExtension() {
        return workExtension;
    }

    public void setWorkExtension(String workExtension) {
        this.workExtension = workExtension != null ? workExtension.trim() : "";
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail != null ? workEmail.trim() : "";
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl != null ? linkedinUrl.trim() : "";
        this.updatedAt = java.time.LocalDateTime.now();
    }

    @Override
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        if (jobTitle != null && !jobTitle.isEmpty()) {
            sb.append(jobTitle);
        }
        if (companyName != null && !companyName.isEmpty()) {
            if (sb.length() > 0) sb.append(" at ");
            sb.append(companyName);
        }
        if (sb.length() == 0) {
            sb.append("Business Professional");
        }
        return sb.toString();
    }

    @Override
    public Map<String, String> getSpecificDetailsMap() {
        Map<String, String> map = new LinkedHashMap<>();
        if (companyName != null && !companyName.isEmpty()) {
            map.put("Company", companyName);
        }
        if (jobTitle != null && !jobTitle.isEmpty()) {
            map.put("Job Title", jobTitle);
        }
        if (department != null && !department.isEmpty()) {
            map.put("Department", department);
        }
        if (workExtension != null && !workExtension.isEmpty()) {
            map.put("Work Extension", "Ext. " + workExtension);
        }
        if (workEmail != null && !workEmail.isEmpty()) {
            map.put("Work Email", workEmail);
        }
        if (linkedinUrl != null && !linkedinUrl.isEmpty()) {
            map.put("LinkedIn", linkedinUrl);
        }
        return map;
    }

    @Override
    protected void appendSpecificVCardDetails(StringBuilder sb) {
        if (companyName != null && !companyName.isEmpty()) {
            sb.append("ORG:").append(companyName);
            if (department != null && !department.isEmpty()) {
                sb.append(";").append(department);
            }
            sb.append("\r\n");
        }
        if (jobTitle != null && !jobTitle.isEmpty()) {
            sb.append("TITLE:").append(jobTitle).append("\r\n");
        }
        if (workEmail != null && !workEmail.isEmpty()) {
            sb.append("EMAIL;TYPE=WORK:").append(workEmail).append("\r\n");
        }
        if (linkedinUrl != null && !linkedinUrl.isEmpty()) {
            sb.append("URL:").append(linkedinUrl).append("\r\n");
        }
    }

    @Override
    public ValidationResult validate() {
        Map<String, String> errors = new LinkedHashMap<>();
        if (firstName.isEmpty() && lastName.isEmpty() && companyName.isEmpty()) {
            errors.put("Identity", "First name, last name, or company name is required.");
        }
        if (phoneNumbers.isEmpty() && email.isEmpty() && workEmail.isEmpty()) {
            errors.put("Contact Method", "At least one contact phone or email is required.");
        }
        if (errors.isEmpty()) {
            return ValidationResult.success();
        }
        return ValidationResult.failure(errors);
    }
}
