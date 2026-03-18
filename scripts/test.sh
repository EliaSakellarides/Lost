#!/bin/bash
# Script di smoke test per Lost Thesis

echo "═══════════════════════════════════════════════════"
echo "  ✈️ LOST THESIS - Smoke Test"
echo "═══════════════════════════════════════════════════"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
MAIN_SRC_DIR="$PROJECT_DIR/src/main/java"
TEST_SRC_DIR="$PROJECT_DIR/src/test/java"
RES_DIR="$PROJECT_DIR/src/main/resources"
MAIN_OUT_DIR="$PROJECT_DIR/target/test-main-classes"
TEST_OUT_DIR="$PROJECT_DIR/target/test-classes"

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

GSON_JAR_PATH="$(resolve_gson_jar || true)"
if [ -z "$GSON_JAR_PATH" ]; then
    echo "❌ Dipendenza Gson non trovata."
    echo "   Imposta GSON_JAR=/percorso/a/gson.jar oppure esegui prima la compilazione su una macchina con Maven."
    exit 1
fi

if [ ! -d "$TEST_SRC_DIR" ]; then
    echo "⚠️ Nessun test trovato in $TEST_SRC_DIR"
    exit 0
fi

mkdir -p "$MAIN_OUT_DIR" "$TEST_OUT_DIR"
rm -rf "$MAIN_OUT_DIR"/* "$TEST_OUT_DIR"/*

MAIN_FILES=$(find "$MAIN_SRC_DIR" -name "*.java")
TEST_FILES=$(find "$TEST_SRC_DIR" -name "*.java")

echo "📦 Gson: $GSON_JAR_PATH"
echo "⚙️ Compilo i sorgenti principali..."
javac -cp "$GSON_JAR_PATH" -d "$MAIN_OUT_DIR" -sourcepath "$MAIN_SRC_DIR" $MAIN_FILES || exit 1

echo "⚙️ Compilo i test..."
javac -cp "$MAIN_OUT_DIR:$RES_DIR:$GSON_JAR_PATH" -d "$TEST_OUT_DIR" -sourcepath "$TEST_SRC_DIR" $TEST_FILES || exit 1

echo "🧪 Eseguo gli smoke test..."
java -Djava.awt.headless=true -ea -cp "$TEST_OUT_DIR:$MAIN_OUT_DIR:$RES_DIR:$GSON_JAR_PATH" com.lostthesis.SmokeTests
