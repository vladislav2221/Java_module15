#!/usr/bin/env sh
set -eu
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
OUT="$ROOT/target/test-classes"
rm -rf "$ROOT/target"
mkdir -p "$OUT"
find "$ROOT/src/main/java" "$ROOT/src/test/java" -name '*.java' -print > "$ROOT/target/sources.list"
javac -encoding UTF-8 -Xlint:all -Werror -d "$OUT" @"$ROOT/target/sources.list"
java -ea -cp "$OUT" academy.tochkavhoda.competition.TestAccountFlow
