#!/bin/bash
# Script di compilazione per Lost Thesis

echo "═══════════════════════════════════════════════════"
echo "  ✈️ LOST THESIS - Compilazione"
echo "═══════════════════════════════════════════════════"

# Directory di lavoro
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
SRC_DIR="$PROJECT_DIR/src"
BIN_DIR="$PROJECT_DIR/bin"

# Crea cartella bin se non esiste
mkdir -p "$BIN_DIR"

# Pulisci precedenti compilazioni
rm -rf "$BIN_DIR"/*

echo "📁 Compilazione da: $SRC_DIR"
echo "📁 Output in: $BIN_DIR"
echo ""

# Trova tutti i file Java
echo "🔍 Ricerca file Java..."
JAVA_FILES=$(find "$SRC_DIR" -name "*.java")
FILE_COUNT=$(echo "$JAVA_FILES" | wc -l | tr -d ' ')
echo "   Trovati $FILE_COUNT file Java"
echo ""

# Compila
echo "⚙️ Compilazione in corso..."
if javac -d "$BIN_DIR" -sourcepath "$SRC_DIR" $JAVA_FILES 2>&1; then
    echo ""
    echo "✅ Compilazione completata con successo!"
    echo ""
    echo "Per avviare il gioco, esegui:"
    echo "   ./scripts/run.sh"
else
    echo ""
    echo "❌ Errore durante la compilazione!"
    exit 1
fi

echo "═══════════════════════════════════════════════════"
