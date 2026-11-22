# Genera el par de llaves RSA que usa auth-service para firmar los JWT.
# Las llaves NO se versionan (están en .gitignore): cada quien genera las suyas.
# Ejecutar una sola vez desde la raíz del repositorio:
#   powershell -ExecutionPolicy Bypass -File scripts/generar-llaves.ps1
#
# Requiere openssl en el PATH. Git para Windows lo incluye en:
#   C:\Program Files\Git\usr\bin\openssl.exe

$ErrorActionPreference = "Stop"

$destino = Join-Path $PSScriptRoot "..\keys"
$privada = Join-Path $destino "private.pem"
$publica = Join-Path $destino "public.pem"

if ((Test-Path $privada) -and (Test-Path $publica)) {
    Write-Host "Las llaves ya existen en $destino. No se sobreescriben."
    exit 0
}

$openssl = Get-Command openssl -ErrorAction SilentlyContinue
if (-not $openssl) {
    $candidatos = @(
        "C:\Program Files\Git\usr\bin\openssl.exe",
        "C:\Program Files (x86)\Git\usr\bin\openssl.exe"
    )
    foreach ($c in $candidatos) {
        if (Test-Path $c) { $openssl = $c; break }
    }
}

if (-not $openssl) {
    Write-Error "No se encontró openssl. Instala Git for Windows (incluye openssl) o agregalo al PATH."
    exit 1
}

$rutaOpenSsl = if ($openssl -is [string]) { $openssl } else { $openssl.Source }

New-Item -ItemType Directory -Force -Path $destino | Out-Null

Write-Host "Generando par de llaves RSA 2048 en $destino ..."

# openssl escribe su progreso por stderr; se redirige para que PowerShell no lo
# interprete como error nativo.
$ErrorActionPreference = "Continue"
& $rutaOpenSsl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out $privada 2>&1 | Out-Null
& $rutaOpenSsl rsa -in $privada -pubout -out $publica 2>&1 | Out-Null
$ErrorActionPreference = "Stop"

if ((Test-Path $privada) -and (Test-Path $publica)) {
    Write-Host "Listo:"
    Write-Host "  $privada"
    Write-Host "  $publica"
} else {
    Write-Error "No se pudieron generar las llaves."
    exit 1
}
