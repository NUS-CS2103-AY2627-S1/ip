#!/usr/bin/env bash
set -euo pipefail

test_output_dir="$(mktemp -d)"
trap 'rm -rf "${test_output_dir}"' EXIT

javac -d "${test_output_dir}" \
  $(find src/main/java/odysseus -maxdepth 1 -name '*.java' \
    ! -name 'DialogBox.java' ! -name 'HelloWorld.java' \
    ! -name 'Launcher.java' ! -name 'MainWindow.java') \
  tests/OdysseusTest.java
java -ea -cp "${test_output_dir}" odysseus.OdysseusTest
