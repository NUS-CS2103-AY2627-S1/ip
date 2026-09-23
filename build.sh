#!/bin/sh
set -eu

BUILD_DIR="build/classes"
rm -rf build
mkdir -p "$BUILD_DIR"
javac --release 25 -d "$BUILD_DIR" src/main/java/vermithor/*.java
jar --create --file build/vermithor.jar --main-class vermithor.Launcher -C "$BUILD_DIR" .
echo "Created build/vermithor.jar"
