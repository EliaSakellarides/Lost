#!/bin/bash
# Script di esecuzione per Lost Thesis

echo "═══════════════════════════════════════════════════"
echo "  ✈️ LOST THESIS - L'Isola Misteriosa"
echo "═══════════════════════════════════════════════════"

# Directory di lavoro
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
BIN_DIR="$PROJECT_DIR/bin"

# Controlla se è stato compilato
if [ ! -d "$BIN_DIR" ] || [ -z "$(ls -A "$BIN_DIR")" ]; then
    echo "⚠️ Il gioco non è stato compilato."
    echo "   Eseguo prima la compilazione..."
    echo ""
    "$SCRIPT_DIR/compile.sh"
    echo ""
fi

# Avvia il gioco
echo "🚀 Avvio Lost Thesis..."
echo ""
cd "$PROJECT_DIR"
java -cp "$BIN_DIR" com.lostthesis.Main

echo ""
echo "═══════════════════════════════════════════════════"
echo "  Grazie per aver giocato a Lost Thesis!"
echo "═══════════════════════════════════════════════════"
