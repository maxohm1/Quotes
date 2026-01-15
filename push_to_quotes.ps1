Write-Host "Setting remote to 'Quotes' repository..."
git remote set-url origin https://github.com/maxohm1/Quotes.git

Write-Host "Pushing code to https://github.com/maxohm1/Quotes.git ..."
git push origin main

Write-Host "Process Complete."
