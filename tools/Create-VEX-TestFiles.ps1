# VEX Test Fixture Generation Script
$targetDir = "C:\VEX\test_samples\usb_demo"
if (!(Test-Path $targetDir)) {
    New-Item -ItemType Directory -Force -Path $targetDir
}

# 1. safe.txt (Score: 10, SAFE, ALLOW)
Set-Content -Path "$targetDir\safe.txt" -Value "VEX Demo File: safe.txt
Category: SAFE
Expected Score: 10
Expected Action: ALLOW
Description: This is a clean demonstration fixture file with no security risks."

# 2. warn.txt (Score: 35, WARN, WARN)
Set-Content -Path "$targetDir\warn.txt" -Value "VEX Demo File: warn.txt
Category: WARN
Expected Score: 35
Expected Action: WARN
Description: This is a demonstration fixture file flagged for warning notification alerts."

# 3. harmful.txt (Score: 85, QUARANTINE, QUARANTINE)
Set-Content -Path "$targetDir\harmful.txt" -Value "VEX Demo File: harmful.txt
Category: QUARANTINE
Expected Score: 85
Expected Action: QUARANTINE
Description: This is a demonstration fixture file flagged as high risk. It triggers loud alarms and prominent quarantine warnings."

Write-Host "VEX test fixture files created successfully in $targetDir"
Write-Host "Instructions to transfer via USB/MTP:"
Write-Host "1. Connect your Android phone to this Windows PC via USB."
Write-Host "2. Select 'File Transfer (MTP)' on your phone."
Write-Host "3. Copy the files from $targetDir to your phone's Downloads folder."
Write-Host "4. VEX will automatically scan them and trigger the corresponding alerts!"
