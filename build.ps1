$folderName = (Get-Item -Path ".\").Name

$newFolderName = $folderName + "-BlueJ"

$newFolderPath = "..\$newFolderName"

$exclude = @("out", ".idea", "build.ps1")

if (-not (Test-Path $newFolderPath)) {
    New-Item -ItemType Directory -Path $newFolderPath
} else {
    Remove-Item -Path $newFolderPath -Recurse -Force
    New-Item -ItemType Directory -Path $newFolderPath
}

$items_source = Get-ChildItem -Path ".\src" -Exclude $exclude
$items_test = Get-ChildItem -Path ".\test" -Exclude $exclude

foreach ($item in $items_source + $items_test) {
    $destination = Join-Path -Path $newFolderPath -ChildPath $item.Name
    Copy-Item -Path $item.FullName -Destination $destination -Force -Recurse
}

$packageFile = Join-Path -Path $newFolderPath -ChildPath "package.bluej"
Set-Content -Path $packageFile -Value "#BlueJ package file"

Write-Host "Archivos y carpetas copiados a ../$newFolderName"