# Contact Management System - PowerShell Build Script
# Group 17 | CSE2003 Java OOP Mini Project

$ErrorActionPreference = "Stop"

$jdkPath = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.10002-hotspot"
if (Test-Path "$jdkPath\bin\javac.exe") {
    $javac = "$jdkPath\bin\javac.exe"
} else {
    $javac = "javac"
}

Write-Host "===================================================" -ForegroundColor Cyan
Write-Host " Building Contact Management System (Java 21)" -ForegroundColor Cyan
Write-Host " Group 17 | CSE2003 OOP Mini Project" -ForegroundColor Cyan
Write-Host "==================================================="

Set-Location $PSScriptRoot

if (!(Test-Path "target\classes")) { New-Item -ItemType Directory -Force -Path "target\classes" | Out-Null }
if (!(Test-Path "target\test-classes")) { New-Item -ItemType Directory -Force -Path "target\test-classes" | Out-Null }

Write-Host "[1/3] Compiling Application Sources..." -ForegroundColor Yellow
$sources = Get-ChildItem -Path .\src\main\java -Filter *.java -Recurse | ForEach-Object { $_.FullName }
& $javac -encoding UTF-8 -cp "lib\*" -d target\classes $sources

if (Test-Path "src\main\resources") {
    Copy-Item -Path "src\main\resources\*" -Destination "target\classes\" -Recurse -Force
}

Write-Host "[2/3] Compiling Test Sources..." -ForegroundColor Yellow
$testSources = Get-ChildItem -Path .\src\test\java -Filter *.java -Recurse | ForEach-Object { $_.FullName }
& $javac -encoding UTF-8 -cp "target\classes;lib\*" -d target\test-classes $testSources

Write-Host "[3/3] Build Complete Successfully!" -ForegroundColor Green
Write-Host "Run '.\run.ps1' or 'run.bat' to launch the application."
Write-Host "Run '.\test.ps1' or 'test.bat' to execute the test suite."
