#!/bin/sh
cd "$(dirname "$0")" || exit 1
if ! java -version >/dev/null 2>&1; then
    printf '%s\n' 'Wangsa needs Java 25. Install it, then open this file again.'
    printf '%s\n' 'See QUICK-START.txt for the download link. Press Enter to close.'
    read -r reply
    exit 1
fi
if ! java -jar Wangsa.jar; then
    printf '%s\n' 'Wangsa could not start. Check Java 25 and see QUICK-START.txt.'
    printf '%s\n' 'Press Enter to close.'
    read -r reply
    exit 1
fi
