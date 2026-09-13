#!/usr/bin/env bash
set -euo pipefail

fail() {
  printf 'ERROR: %s\n' "$*" >&2
  exit 1
}

command -v npm >/dev/null 2>&1 || fail 'Node.js and npm are required for markdownlint. Run npm install.'
[[ -d node_modules/markdownlint-cli2 ]] || fail 'markdownlint is not installed. Run npm install.'

if [[ -x ./gradlew ]]; then
  ./gradlew --no-daemon checkstyleMain checkstyleTest
elif command -v cmd.exe >/dev/null 2>&1 && [[ -f ./gradlew.bat ]]; then
  cmd.exe /d /c "gradlew.bat --no-daemon checkstyleMain checkstyleTest"
elif command -v gradlew >/dev/null 2>&1; then
  gradlew --no-daemon checkstyleMain checkstyleTest
else
  fail 'Gradle Wrapper is required for Checkstyle.'
fi

npx --no-install markdownlint-cli2
if command -v gitlint >/dev/null 2>&1; then
  gitlint --config .gitlint --commits HEAD^..HEAD
elif command -v cmd.exe >/dev/null 2>&1; then
  cmd.exe /d /c "gitlint --config .gitlint --commits HEAD^..HEAD"
else
  fail 'gitlint is required. Install requirements-dev.txt.'
fi
