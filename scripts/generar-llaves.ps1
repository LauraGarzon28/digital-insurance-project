# Genera el par de llaves RSA que usa auth-service para firmar los JWT.
# Las llaves NO se versionan (están en .gitignore): cada quien genera las suyas.
# Ejecutar una sola vez desde la raíz del repositorio:
#   powershell -ExecutionPolicy Bypass -File scripts/generar-llaves.ps1

$ErrorActionPreference = "Stop"

$destino = Join-Path $PSScriptRoot "..\keys"
$privada = Join-Path $destino "private.pem"
$publica = Join-Path $destino "public.pem"

if ((Test-Path $privada) -and (Test-Path $publica)) {
    Write-Host "Las llaves ya existen en $destino. No se sobreescriben."
    exit 0
}

New-Item -ItemType Directory -Force -Path $destino | Out-Null

Write-Host "Generando par de llaves RSA 2048 en $destino ..."

$rsa = [System.Security.Cryptography.RSA]::Create(2048)
$privateBytes = $rsa.ExportRSAPrivateKey()
$publicBytes = $rsa.ExportRSAPublicKey()

function Convertir-A-Pem([byte[]]$bytes, [string]$etiqueta) {
    $base64 = [Convert]::ToBase64String($bytes, [System.Base64FormattingOptions]::InsertLineBreaks)
    return "-----BEGIN $etiqueta-----`n$base64`n-----END $etiqueta-----`n"
}

[System.IO.File]::WriteAllText($privada, (Convertir-A-Pem $privateBytes "PRIVATE KEY"))
[System.IO.File]::WriteAllText($publica, (Convertir-A-Pem $publicBytes "PUBLIC KEY"))

$rsa.Dispose()

Write-Host "Listo:"
Write-Host "  $privada"
Write-Host "  $publica"
