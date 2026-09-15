#!/bin/sh

project_root=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
task_data_directory=$(mktemp -d)

if [ -z "$task_data_directory" ] || [ ! -d "$task_data_directory" ]; then
    echo "Unable to create an isolated UI-test directory." >&2
    exit 1
fi

cleanup() {
    cd / || exit 1
    rm -rf -- "$task_data_directory"
}
trap cleanup EXIT HUP INT TERM

cd "$task_data_directory" || exit 1
java -cp "$project_root/build/classes/java/main" monkey.Monkey
