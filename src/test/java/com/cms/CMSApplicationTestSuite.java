package com.cms;

/**
 * Master Test Suite Runner for the Contact Management System.
 * Can be run independently via 'java com.cms.CMSApplicationTestSuite' or through JUnit.
 * 
 * Verifies 100% of the core OOP logic, validation, business rules, and serialization.
 */
public class CMSApplicationTestSuite {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("CONTACT MANAGEMENT SYSTEM — MASTER TEST SUITE");
        System.out.println("Group 17 | CSE2003 OOP Mini Project Verification");
        System.out.println("=================================================\n");

        int passed = 0;
        int failed = 0;

        try {
            ContactModelTest.runTests();
            passed++;
        } catch (Throwable t) {
            System.err.println("✕ ContactModelTest FAILED: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            ContactValidationTest.runTests();
            passed++;
        } catch (Throwable t) {
            System.err.println("✕ ContactValidationTest FAILED: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            ContactServiceTest.runTests();
            passed++;
        } catch (Throwable t) {
            System.err.println("✕ ContactServiceTest FAILED: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            SerializationAndIoTest.runTests();
            passed++;
        } catch (Throwable t) {
            System.err.println("✕ SerializationAndIoTest FAILED: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        System.out.println("\n-------------------------------------------------");
        System.out.println("TEST SUITE EXECUTION SUMMARY:");
        System.out.println("Passed: " + passed + " / 4 Modules");
        System.out.println("Failed: " + failed + " / 4 Modules");
        if (failed == 0) {
            System.out.println(">>> ALL OOP & DATA INTEGRITY TESTS PASSED 100% <<<");
        } else {
            System.out.println(">>> SOME TESTS FAILED <<<");
            System.exit(1);
        }
        System.out.println("=================================================");
    }
}
