Write-Host "Initializing fresh commit sequence..."

# 1. Update Remote
Write-Host "Setting remote to 'Quotes'..."
git remote set-url origin https://github.com/maxohm1/Quotes.git

# 2. Create orphan branch to reset history/timestamps
# This allows us to re-commit everything with "now" timestamps and proper structure
Write-Host "Creating fresh branch..."
git checkout --orphan fresh_upload
git reset .

# 3. Commit: Configuration
Write-Host "Committing: Configuration..."
git add *.gradle *.gradle.kts gradle.properties settings.gradle.kts
git commit -m "chore: Update build configuration and dependencies"

# 4. Commit: UI/Resources
Write-Host "Committing: UI & Resources..."
git add app/src/main/res
git commit -m "ui: Refine UI layouts and update application resources"

# 5. Commit: Source Code
Write-Host "Committing: Source Code..."
git add app/src/main/java
git commit -m "feat: Update core application logic and functionality"

# 6. Commit: Everything else
Write-Host "Committing: Remaining files..."
git add .
git commit -m "chore: Sync remaining project files and manifest updates"

# 7. Push Forcefully to Main
Write-Host "Pushing to GitHub..."
git push -f origin fresh_upload:main

Write-Host "Done! Code pushed to https://github.com/maxohm1/Quotes.git with fresh timestamps."
