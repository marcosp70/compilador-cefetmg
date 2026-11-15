$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$jarPath = Join-Path $projectRoot 'dist\compilador-etapa2.jar'
if (-not (Test-Path $jarPath)) { & (Join-Path $PSScriptRoot 'build.ps1') }
$resultsRoot = Join-Path $projectRoot 'resultados\etapa2'
New-Item -ItemType Directory -Force $resultsRoot | Out-Null
$scenarios = Get-Content -Raw -Encoding UTF8 (Join-Path $projectRoot 'testes\etapa2\cenarios.json') | ConvertFrom-Json
for ($i = 1; $i -le 5; $i++) {
    $baseSource = [IO.File]::ReadAllText((Join-Path $projectRoot "testes\corrigidos\teste${i}_corrigido.lpp")).Replace("`r`n", "`n").TrimEnd()
    $initialSource = [IO.File]::ReadAllText((Join-Path $projectRoot "testes\etapa2\teste$i\passo00.lpp")).Replace("`r`n", "`n").TrimEnd()
    if ($baseSource -cne $initialSource) { throw "O teste $i não parte da versão corrigida da Etapa 1." }
}
foreach ($scenario in $scenarios) {
    $sourcePath = Join-Path $projectRoot $scenario.source
    $output = (& java -jar $jarPath $sourcePath 2>&1 | Out-String).TrimEnd()
    $code = $LASTEXITCODE
    $expected = if ($scenario.success) { 0 } else { 1 }
    if ($code -ne $expected -or $output -match 'erro léxico|Erro léxico') {
        throw "Resultado inesperado para $($scenario.source): $output"
    }
    if ($output -match 'SEQUÊNCIA DE TOKENS|TABELA DE SÍMBOLOS') {
        throw "A Etapa 2 não deve imprimir tokens ou tabela de símbolos."
    }
    $name = ($scenario.source -replace '^testes/etapa2/', '' -replace '/', '_') + '.txt'
    [IO.File]::WriteAllText((Join-Path $resultsRoot $name), $output + [Environment]::NewLine, [Text.UTF8Encoding]::new($false))
    Write-Host "$($scenario.source): $output"
}
Write-Host "OK - $($scenarios.Count) cenários conferidos."

