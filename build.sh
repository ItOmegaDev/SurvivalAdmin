#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$(readlink -f "$0")")"

VERSION="2.0.0"
JAR_NAME="SurvivalAdmin-${VERSION}.jar"
BASE="$(pwd)"

PAPER_API="${PAPER_API:-$(find "$BASE"/../../servers -name 'purpur-api-*.jar' 2>/dev/null | head -1)}"
ADVENTURE_API="${ADVENTURE_API:-$(find "$BASE"/../../servers -name 'adventure-api-*.jar' 2>/dev/null | head -1)}"
ADVENTURE_KEY="${ADVENTURE_KEY:-$(find "$BASE"/../../servers -name 'adventure-key-*.jar' 2>/dev/null | head -1)}"
BUNGEE_CHAT="${BUNGEE_CHAT:-$(find "$BASE"/../../servers -name 'bungeecord-chat-*.jar' 2>/dev/null | head -1)}"

for f in "$PAPER_API" "$ADVENTURE_API" "$ADVENTURE_KEY" "$BUNGEE_CHAT"; do
  if [ ! -f "$f" ]; then
    echo "Missing: $f" >&2
    exit 1
  fi
done

CP="$PAPER_API:$ADVENTURE_API:$ADVENTURE_KEY:$BUNGEE_CHAT"

rm -rf target/classes target
mkdir -p target/classes
find src/main/java -name '*.java' > target/sources.txt

echo "Compiling $(wc -l < target/sources.txt) source files..."
javac -encoding UTF-8 --release 21 -cp "$CP" -d target/classes @target/sources.txt

cp -r src/main/resources/. target/classes/

cd target/classes
jar --create --file "../${JAR_NAME}" .
cd "$BASE"

echo "Done: $BASE/target/${JAR_NAME}"
ls -lh "target/${JAR_NAME}"
