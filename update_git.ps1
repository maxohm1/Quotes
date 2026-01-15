Write-Host "Starting segmented git update..."

# 1. Configuration Changes
Write-Host "Committing configuration changes..."
git add *.gradle *.gradle.kts gradle.properties settings.gradle.kts
git commit -m "chore: Update build configuration and dependencies"

# 2. XML Resources and Layouts
Write-Host "Committing resource changes..."
git add app/src/main/res
git commit -m "ui: Refine UI layouts and update application resources"

# 3. Java/Kotlin Source Code
Write-Host "Committing source code changes..."
git add app/src/main/java
git commit -m "feat: Update core application logic and functionality"

# 4. Manifest and Remaining Files
Write-Host "Committing remaining files..."
git add .
git commit -m "chore: Sync remaining project files and manifest updates"

# 5. Push changes
Write-Host "Pushing changes to remote..."
git push origin HEAD

Write-Host "Update complete!"
