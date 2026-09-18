$env:JAVA_HOME = 'C:\Program Files\Java\jdk-25.0.4'
$env:GRADLE_USER_HOME = Join-Path $PSScriptRoot '_temp\gradle-verify'

& (Join-Path $PSScriptRoot 'gradlew.bat') run
