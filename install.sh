#!/bin/zsh
# Build the mod and copy it into Hytale's Mods folder.
set -e
cd "$(dirname "$0")"
export JAVA_HOME="${JAVA_HOME_HYTALE:-/opt/homebrew/opt/openjdk}"
gradle build
MODS="$HOME/Library/Application Support/Hytale/UserData/Mods"
cp build/libs/Jogging-*.jar "$MODS/"
echo "Installed to $MODS"
echo "Enable \"TBFox:Jogging\" in your test world, then (re)load the world."
