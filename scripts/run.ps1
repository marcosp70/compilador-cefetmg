param(
    [Parameter(Mandatory = $true, Position = 0)]
    [string]$Arquivo
)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$jarPath = Join-Path $projectRoot 'dist\compilador-etapa2.jar'
if (-not (Test-Path $jarPath)) { & (Join-Path $PSScriptRoot 'build.ps1') }
& java -jar $jarPath $Arquivo
exit $LASTEXITCODE

