@echo off
setlocal
set "PROJECT_ROOT=%~dp0"
where gradle >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    gradle -p "%PROJECT_ROOT%" %*
    exit /b %ERRORLEVEL%
)
set "GRADLE_VERSION=8.10.2"
set "GRADLE_PARENT=%USERPROFILE%\.gradle\wrapper\mesh-bootstrap"
set "GRADLE_BIN=%GRADLE_PARENT%\gradle-%GRADLE_VERSION%\bin\gradle.bat"
if not exist "%GRADLE_BIN%" (
    if not exist "%GRADLE_PARENT%" mkdir "%GRADLE_PARENT%"
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference = 'Stop'; $version = '8.10.2'; $parent = Join-Path $env:USERPROFILE '.gradle\wrapper\mesh-bootstrap'; $archive = Join-Path $parent ('gradle-' + $version + '-bin.zip'); Invoke-WebRequest ('https://services.gradle.org/distributions/gradle-' + $version + '-bin.zip') -OutFile $archive; Expand-Archive -Path $archive -DestinationPath $parent -Force; Remove-Item $archive"
    if errorlevel 1 exit /b %ERRORLEVEL%
)
call "%GRADLE_BIN%" -p "%PROJECT_ROOT%" %*
exit /b %ERRORLEVEL%
