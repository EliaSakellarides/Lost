#!/bin/bash
# Script di esecuzione per Lost Thesis

echo "═══════════════════════════════════════════════════"
echo "  ✈️ LOST THESIS - L'Isola Misteriosa"
echo "═══════════════════════════════════════════════════"

# Directory di lavoro
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
BIN_DIR="$PROJECT_DIR/bin"

resolve_gson_jar() {
    if [ -n "${GSON_JAR:-}" ] && [ -f "$GSON_JAR" ]; then
        printf '%s\n' "$GSON_JAR"
        return 0
    fi

    local cached_jar
    cached_jar="$(find "$HOME/.m2/repository/com/google/code/gson/gson" -name 'gson-*.jar' 2>/dev/null | sort | tail -n 1)"
    if [ -n "$cached_jar" ] && [ -f "$cached_jar" ]; then
        printf '%s\n' "$cached_jar"
        return 0
    fi

    return 1
}

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
GSON_JAR_PATH="$(resolve_gson_jar || true)"
JAVA_OPTS=()

if [ -z "$GSON_JAR_PATH" ]; then
    echo "❌ Dipendenza Gson non trovata."
    echo "   Imposta GSON_JAR=/percorso/a/gson.jar oppure scarica le dipendenze con Maven."
    exit 1
fi

if [ "$(uname -s)" = "Darwin" ]; then
    JAVA_OPTS+=("-XstartOnFirstThread")
fi

java "${JAVA_OPTS[@]}" -cp "$BIN_DIR:$GSON_JAR_PATH" com.lostthesis.Main

echo ""
echo "═══════════════════════════════════════════════════"
echo "  Grazie per aver giocato a Lost Thesis!"
echo "═══════════════════════════════════════════════════"
