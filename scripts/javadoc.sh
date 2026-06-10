#!/bin/bash
# Genera la Javadoc del progetto in docs/Javadoc

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

resolve_jar() {
    find "$HOME/.m2/repository/$1" -name "$2" 2>/dev/null | sort | tail -n 1
}

GSON_JAR="${GSON_JAR:-$(resolve_jar com/google/code/gson/gson 'gson-*.jar')}"
H2_JAR="${H2_JAR:-$(resolve_jar com/h2database/h2 'h2-*.jar')}"

if [ -z "$GSON_JAR" ] || [ -z "$H2_JAR" ]; then
    echo "ERRORE: dipendenze Gson/H2 non trovate (imposta GSON_JAR e H2_JAR)."
    exit 1
fi

rm -rf "$PROJECT_DIR/docs/Javadoc"
javadoc -d "$PROJECT_DIR/docs/Javadoc" \
    -sourcepath "$PROJECT_DIR/src/main/java" \
    -subpackages com.lost \
    -cp "$GSON_JAR:$H2_JAR" \
    -encoding UTF-8 -charset UTF-8 \
    -windowtitle "Lost - Javadoc" \
    -doctitle "Lost &mdash; Avventura testuale grafica" \
    -quiet

echo "Javadoc generata in docs/Javadoc"
