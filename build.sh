#!/bin/bash
set -e
FX_PATH="${JAVA_FX_LIB:-/usr/share/openjfx/lib}"
FX_MODULES="javafx.controls,javafx.fxml,javafx.media"
find src -name '*.java' ! -path '*/old/*' > sources.txt
mkdir -p build
javac --module-path "$FX_PATH" --add-modules "$FX_MODULES" -classpath lib/json-20250517.jar -d build @sources.txt

