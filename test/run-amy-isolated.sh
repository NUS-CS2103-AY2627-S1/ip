#!/bin/zsh

# Each UI case needs a fresh save file because Amy now restores saved tasks at startup.
rm -f data/amy.txt
project_root="$(cd "$(dirname "$0")/.." && pwd)"
exec java "$project_root/src/main/java/amy/Amy.java"
