# Contact Management System - PowerShell Launcher
# Group 17 | CSE2003 Java OOP Mini Project

$jdkPath = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.10002-hotspot"
if (Test-Path "$jdkPath\bin\java.exe") {
    $java = "$jdkPath\bin\java.exe"
} else {
    $java = "java"
}

Set-Location $PSScriptRoot

if (!(Test-Path "target\classes\com\cms\Main.class")) {
    Write-Host "Classes not found. Invoking build.ps1 first..." -ForegroundColor Yellow
    & ".\build.ps1"
}

Write-Host "Launching Contact Management System Desktop UI..." -ForegroundColor Cyan
& $java -Dfile.encoding=UTF-8 -cp "target\classes;lib\*" com.cms.Main
