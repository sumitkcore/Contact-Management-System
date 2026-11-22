# Contact Management System - Test Suite Runner
# Group 17 | CSE2003 Java OOP Mini Project

$jdkPath = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.10002-hotspot"
if (Test-Path "$jdkPath\bin\java.exe") {
    $java = "$jdkPath\bin\java.exe"
} else {
    $java = "java"
}

Set-Location $PSScriptRoot

if (!(Test-Path "target\test-classes\com\cms\CMSApplicationTestSuite.class")) {
    & ".\build.ps1"
}

Write-Host "Running Comprehensive OOP Mini Project Test Suite (-ea)..." -ForegroundColor Cyan
& $java -ea -cp "target\classes;target\test-classes;lib\*" com.cms.CMSApplicationTestSuite
