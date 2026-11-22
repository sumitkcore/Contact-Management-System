package com.cms.util;

import com.cms.model.*;
import com.cms.pattern.builder.ContactBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Pre-populates the system with 35+ realistic, rich contacts representing
 * Personal friends/family, Business colleagues/clients, and Emergency medical responders.
 * 
 * Ensures the system launches with a visually impressive, comprehensive initial dataset.
 */
public class DummyDataGenerator {

    public static List<Contact> generateSampleContacts() {
        List<Contact> list = new ArrayList<>();

        // 1. Personal Contacts (Friends, Family, Classmates)
        list.add(ContactBuilder.personal()
                .name("Aarav", "Sharma")
                .nickname("Aaru")
                .relationship("Best Friend & Lab Partner")
                .birthday(LocalDate.of(2005, 5, 14))
                .phone(PhoneNumber.Type.MOBILE, "+91 98201 45678", true)
                .email("aarav.sharma@example.com")
                .address("104, Blue Ridge Towers, Hinjewadi", "Pune", "Maharashtra", "411057", "India")
                .group(ContactGroup.FRIENDS)
                .tags("College", "VIT", "Bestie", "Hackathon")
                .favorite(true)
                .notes("CSE batchmate, loves competitive programming and badminton.")
                .avatarColorHex(ColorUtils.getHexForName("Aarav Sharma"))
                .build());

        list.add(ContactBuilder.personal()
                .name("Priya", "Nair")
                .nickname("Peehu")
                .relationship("Cousin Sister")
                .birthday(LocalDate.of(2003, 11, 20))
                .phone(PhoneNumber.Type.MOBILE, "+91 94471 23456", true)
                .phone(PhoneNumber.Type.HOME, "+91 484 2567890", false)
                .email("priya.nair@example.com")
                .address("Flat 3B, Palm Meadows, Kakkanad", "Kochi", "Kerala", "682030", "India")
                .group(ContactGroup.FAMILY)
                .tags("Family", "Kerala", "Doctor")
                .favorite(true)
                .notes("Pursuing MD in Pediatrics. Always available for medical advice.")
                .avatarColorHex(ColorUtils.getHexForName("Priya Nair"))
                .build());

        list.add(ContactBuilder.personal()
                .name("Rohan", "Verma")
                .nickname("Ro")
                .relationship("High School Friend")
                .birthday(LocalDate.of(2005, 8, 3))
                .phone(PhoneNumber.Type.MOBILE, "+91 98112 34567", true)
                .email("rohan.verma@example.com")
                .address("C-42, Sector 62", "Noida", "Uttar Pradesh", "201309", "India")
                .group(ContactGroup.FRIENDS)
                .tags("School", "Delhi", "Gamer")
                .notes("Plays Valorant and FIFA on weekends. Studying Mechanical Eng.")
                .avatarColorHex(ColorUtils.getHexForName("Rohan Verma"))
                .build());

        list.add(ContactBuilder.personal()
                .name("Ananya", "Deshmukh")
                .nickname("Anu")
                .relationship("Class Representative")
                .birthday(LocalDate.of(2006, 2, 18))
                .phone(PhoneNumber.Type.MOBILE, "+91 98900 12345", true)
                .email("ananya.cse2003@university.edu")
                .address("Block 7, Hostels Campus", "Vellore", "Tamil Nadu", "632014", "India")
                .group(ContactGroup.FRIENDS)
                .tags("College", "CR", "CSE2003", "StudyGroup")
                .favorite(true)
                .notes("Class Rep for CSE Batch 2026. Coordinates assignment deadlines and notes.")
                .avatarColorHex(ColorUtils.getHexForName("Ananya Deshmukh"))
                .build());

        list.add(ContactBuilder.personal()
                .name("Sunita", "Kumar")
                .nickname("Mom")
                .relationship("Mother")
                .birthday(LocalDate.of(1978, 9, 25))
                .phone(PhoneNumber.Type.MOBILE, "+91 98765 43210", true)
                .phone(PhoneNumber.Type.HOME, "+91 11 23456789", false)
                .email("sunita.kumar@example.com")
                .address("House #45, Shanti Niketan", "New Delhi", "Delhi", "110021", "India")
                .group(ContactGroup.FAMILY)
                .tags("Family", "Mom", "VIP", "SpeedDial")
                .favorite(true)
                .notes("Call every evening at 8:00 PM. Speed dial #1.")
                .avatarColorHex(ColorUtils.getHexForName("Sunita Kumar"))
                .build());

        list.add(ContactBuilder.personal()
                .name("Vikram", "Malhotra")
                .nickname("Vicky")
                .relationship("Uncle")
                .birthday(LocalDate.of(1974, 4, 12))
                .phone(PhoneNumber.Type.MOBILE, "+91 98300 98765", true)
                .email("vikram.malhotra@business.in")
                .address("15B, Alipore Road", "Kolkata", "West Bengal", "700027", "India")
                .group(ContactGroup.FAMILY)
                .tags("Family", "Kolkata")
                .notes("Financial advisor. Manages ancestral investments.")
                .avatarColorHex(ColorUtils.getHexForName("Vikram Malhotra"))
                .build());

        // 2. Business Contacts (Professors, Tech Mentors, Industry Leads)
        list.add(ContactBuilder.business()
                .name("Dr. Senthil", "Murugan")
                .company("Department of Computer Science & Engineering")
                .jobTitle("Associate Professor & Faculty Guide")
                .department("School of Computer Science")
                .workExtension("4210")
                .workEmail("dr.murugan@university.edu")
                .linkedinUrl("https://linkedin.com/in/dr-senthil-murugan")
                .phone(PhoneNumber.Type.WORK, "+91 416 220 2000", true)
                .email("senthil.murugan@gmail.com")
                .address("Technology Tower, Room 412", "Vellore", "Tamil Nadu", "632014", "India")
                .group(ContactGroup.COLLEAGUES)
                .tags("Faculty", "OOPJ", "Guide", "ProjectMentor")
                .favorite(true)
                .notes("CSE2003 OOP with Java Course Coordinator. Mini-project evaluation review guide.")
                .avatarColorHex(ColorUtils.getHexForName("Dr. Senthil Murugan"))
                .build());

        list.add(ContactBuilder.business()
                .name("Aditya", "Singhania")
                .company("Google India")
                .jobTitle("Staff Software Engineer")
                .department("Cloud & Core Infrastructure")
                .workEmail("aditya.singh@google.com")
                .linkedinUrl("https://linkedin.com/in/aditya-singhania-dev")
                .phone(PhoneNumber.Type.WORK, "+91 80 6789 0123", true)
                .email("aditya.personal@gmail.com")
                .address("RMZ Infinity, Old Madras Road", "Bengaluru", "Karnataka", "560016", "India")
                .group(ContactGroup.COLLEAGUES)
                .tags("Alumni", "Google", "Mentor", "Tech")
                .favorite(true)
                .notes("College senior and alumni mentor. Guides on open-source and backend design.")
                .avatarColorHex(ColorUtils.getHexForName("Aditya Singhania"))
                .build());

        list.add(ContactBuilder.business()
                .name("Meera", "Krishnan")
                .company("Amazon Web Services (AWS)")
                .jobTitle("Senior Solutions Architect")
                .department("Cloud Architecture")
                .workExtension("8921")
                .workEmail("krishnanm@amazon.com")
                .linkedinUrl("https://linkedin.com/in/meera-krishnan-aws")
                .phone(PhoneNumber.Type.WORK, "+91 44 4567 8901", true)
                .email("meera.krishnan@outlook.com")
                .address("World Trade Center, Perungudi", "Chennai", "Tamil Nadu", "600096", "India")
                .group(ContactGroup.CLIENTS)
                .tags("Cloud", "AWS", "Architecture")
                .notes("Guest speaker for Cloud Computing seminar. Contact for internship opportunities.")
                .avatarColorHex(ColorUtils.getHexForName("Meera Krishnan"))
                .build());

        list.add(ContactBuilder.business()
                .name("Rajesh", "Kapadia")
                .company("Infosys Technologies")
                .jobTitle("Principal Consultant")
                .department("Enterprise Java Practice")
                .workEmail("rajesh_kapadia@infosys.com")
                .linkedinUrl("https://linkedin.com/in/rajesh-kapadia")
                .phone(PhoneNumber.Type.WORK, "+91 20 2293 4567", true)
                .address("Infosys Phase 2, Hinjewadi", "Pune", "Maharashtra", "411057", "India")
                .group(ContactGroup.COLLEAGUES)
                .tags("Enterprise", "Java", "Consulting")
                .notes("Advisor on Spring Framework and Microservices.")
                .avatarColorHex(ColorUtils.getHexForName("Rajesh Kapadia"))
                .build());

        list.add(ContactBuilder.business()
                .name("Sarah", "Jenkins")
                .company("Microsoft Corporation")
                .jobTitle("Principal Program Manager")
                .department("Developer Division (Java on Azure)")
                .workEmail("sjenkins@microsoft.com")
                .linkedinUrl("https://linkedin.com/in/sarah-jenkins-dev")
                .phone(PhoneNumber.Type.WORK, "+1 (425) 555-0199", true)
                .email("sarah.jenkins@gmail.com")
                .address("One Microsoft Way", "Redmond", "Washington", "98052", "United States")
                .group(ContactGroup.CLIENTS)
                .tags("International", "Microsoft", "Azure")
                .notes("Collaborator on OpenJDK tooling.")
                .avatarColorHex(ColorUtils.getHexForName("Sarah Jenkins"))
                .build());

        // 3. Emergency Contacts (Doctors, Hospitals, Campus Safety, Blood Banks)
        list.add(ContactBuilder.emergency()
                .name("Dr. Ramesh", "Babu")
                .hospitalAffiliation("Apollo Speciality Hospital")
                .priority(EmergencyPriority.PRIMARY)
                .bloodGroup(BloodGroup.O_POSITIVE)
                .phone(PhoneNumber.Type.MOBILE, "+91 98401 99999", true)
                .alternateEmergencyPhone("+91 44 2829 0200")
                .email("dr.rameshbabu@apollohospitals.com")
                .address("Greams Road, Thousand Lights", "Chennai", "Tamil Nadu", "600006", "India")
                .group(ContactGroup.EMERGENCY)
                .tags("Doctor", "FamilyPhysician", "Emergency", "Apollo")
                .favorite(true)
                .medicalNotes("Family physician for 15+ years. Aware of complete medical history.")
                .knownAllergies("Mild Penicillin sensitivity.")
                .avatarColorHex(ColorUtils.getHexForName("Dr. Ramesh Babu"))
                .build());

        list.add(ContactBuilder.emergency()
                .name("Campus Medical", "Center")
                .hospitalAffiliation("University Health & Wellness Center")
                .priority(EmergencyPriority.PRIMARY)
                .phone(PhoneNumber.Type.WORK, "+91 416 220 2111", true)
                .alternateEmergencyPhone("+91 416 220 2999")
                .email("healthcenter@university.edu")
                .address("Behind Main Building, Campus Dispensary", "Vellore", "Tamil Nadu", "632014", "India")
                .group(ContactGroup.EMERGENCY)
                .tags("Campus", "Emergency", "Ambulance", "24x7")
                .favorite(true)
                .medicalNotes("24x7 on-campus ambulance and emergency medical response team.")
                .avatarColorHex(ColorUtils.getHexForName("Campus Medical Center"))
                .build());

        list.add(ContactBuilder.emergency()
                .name("City Blood", "Bank & Trauma")
                .hospitalAffiliation("Red Cross Blood Services")
                .priority(EmergencyPriority.SECONDARY)
                .bloodGroup(BloodGroup.O_NEGATIVE)
                .phone(PhoneNumber.Type.WORK, "+91 11 2371 6441", true)
                .alternateEmergencyPhone("+91 98100 10810")
                .email("helpline@redcrossblood.org")
                .address("1, Red Cross Road", "New Delhi", "Delhi", "110001", "India")
                .group(ContactGroup.EMERGENCY)
                .tags("BloodBank", "SOS", "RedCross")
                .medicalNotes("Rare blood group directory and emergency plasma requests.")
                .avatarColorHex(ColorUtils.getHexForName("City Blood Bank"))
                .build());

        list.add(ContactBuilder.emergency()
                .name("National Emergency", "Response")
                .hospitalAffiliation("Emergency Management Services (Dial 112)")
                .priority(EmergencyPriority.PRIMARY)
                .phone(PhoneNumber.Type.WORK, "112", true)
                .alternateEmergencyPhone("108")
                .email("sos@nationalemergency.gov.in")
                .address("Central Command Center", "New Delhi", "Delhi", "110001", "India")
                .group(ContactGroup.EMERGENCY)
                .tags("National", "SOS", "Police", "Fire", "Ambulance")
                .favorite(true)
                .medicalNotes("Single emergency number for police, ambulance, and disaster response.")
                .avatarColorHex(ColorUtils.getHexForName("National Emergency Response"))
                .build());

        // Add some realistic simulated call logs to top contacts
        Contact c1 = list.get(0); // Aarav
        c1.addCallLog(new CallLog(c1.getId(), CallType.INCOMING, 240, "Discussed CSE2003 mini project presentation slides."));
        c1.addCallLog(new CallLog(c1.getId(), CallType.OUTGOING, 90, "Confirmed badminton court booking for Friday."));

        Contact c5 = list.get(4); // Mom
        c5.addCallLog(new CallLog(c5.getId(), CallType.OUTGOING, 780, "Daily evening check-in call. Updated about semester exam schedule."));
        c5.addCallLog(new CallLog(c5.getId(), CallType.INCOMING, 420, "Discussed weekend train ticket confirmation."));

        Contact c6 = list.get(6); // Dr. Murugan
        c6.addCallLog(new CallLog(c6.getId(), CallType.OUTGOING, 180, "Informed regarding mini-project demonstration schedule Group 17."));

        return list;
    }
}
