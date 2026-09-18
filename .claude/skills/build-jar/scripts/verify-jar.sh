#!/usr/bin/env bash
# Checks that the shadow JAR is a genuine fat JAR with JavaFX bundled inside.
# Exits non-zero on the first failed check so a caller can stop there.

set -u

JAR="${1:-build/libs/davidgoggins.jar}"
MIN_BYTES=$((5 * 1024 * 1024))  # 5 MB; anything smaller cannot hold JavaFX
failed=0

pass() { printf 'PASS  %s\n' "$1"; }
fail() { printf 'FAIL  %s\n' "$1"; failed=1; }

if [ ! -f "$JAR" ]; then
    printf 'FAIL  no JAR at %s -- run ./gradlew clean shadowJar first\n' "$JAR"
    exit 1
fi

# 1. Size. A lean JAR is a few tens of kilobytes; a fat one here is ~10 MB.
bytes=$(wc -c < "$JAR" | tr -d ' ')
human=$(printf '%.1f' "$(echo "$bytes / 1048576" | bc -l)")
if [ "$bytes" -ge "$MIN_BYTES" ]; then
    pass "size is ${human} MB (at least 5 MB)"
else
    fail "size is only ${human} MB -- JavaFX is probably not bundled"
fi

listing=$(unzip -l "$JAR")

# 2. Manifest must name the class holding main(), or `java -jar` cannot start.
main_class=$(unzip -p "$JAR" META-INF/MANIFEST.MF | tr -d '\r' | sed -n 's/^Main-Class: //p')
if [ -n "$main_class" ]; then
    pass "Main-Class is $main_class"
else
    fail "no Main-Class in META-INF/MANIFEST.MF"
fi

# 3. JavaFX's own classes.
fx_classes=$(printf '%s\n' "$listing" | grep -c 'javafx/')
if [ "$fx_classes" -gt 0 ]; then
    pass "$fx_classes JavaFX entries bundled"
else
    fail "no javafx/ entries -- the JavaFX dependencies are missing"
fi

# 4. Native libraries, one extension per platform. Without all three the JAR
#    runs only on the operating system it was built on.
for ext_and_os in "dll:Windows" "dylib:macOS" "so:Linux"; do
    ext="${ext_and_os%%:*}"
    os="${ext_and_os##*:}"
    count=$(printf '%s\n' "$listing" | grep -c "\.${ext}\$")
    if [ "$count" -gt 0 ]; then
        pass "$count $os native libraries (.$ext)"
    else
        fail "no $os native libraries (.$ext) -- the :${ext} classifier may be missing"
    fi
done

# 5. Native architecture. Entry names alone are not enough: the `:mac` and
#    `:mac-aarch64` artifacts both contain a `libglass.dylib`, so only one of
#    them can survive in a flat fat JAR. Unpack the natives and ask `file` which
#    CPU they were actually built for.
tmpdir=$(mktemp -d)
trap 'rm -rf "$tmpdir"' EXIT
if unzip -q -o -j "$JAR" '*.dylib' '*.so' -d "$tmpdir" 2>/dev/null; then
    arches=$(find "$tmpdir" -type f \( -name '*.dylib' -o -name '*.so' \) -exec file -b {} \; \
        | grep -oE 'x86[-_]64|arm64|aarch64' | sort -u | tr '\n' ' ')
    [ -n "$arches" ] && pass "native architectures present: $arches"

    # Does the JAR carry natives this very machine can load?
    host_os=$(uname -s)
    host_arch=$(uname -m)
    case "$host_os" in
        Darwin) host_ext="dylib" ;;
        Linux)  host_ext="so" ;;
        *)      host_ext="" ;;  # Windows natives are .dll; skip the host check
    esac
    if [ -n "$host_ext" ]; then
        case "$host_arch" in
            arm64|aarch64) want='arm64|aarch64' ;;
            *)             want='x86[-_]64' ;;
        esac
        host_natives=$(find "$tmpdir" -type f -name "*.${host_ext}" -exec file -b {} \; \
            | grep -cE "$want")
        if [ "$host_natives" -gt 0 ]; then
            pass "bundled natives match this machine ($host_os/$host_arch)"
        else
            fail "no bundled natives for this machine ($host_os/$host_arch) -- the JAR
      falls back to a JavaFX-bundling JDK here and will fail on a plain one"
        fi
    fi
fi

if [ "$failed" -eq 0 ]; then
    printf '\nAll checks passed. Try it with: java -jar %s\n' "$JAR"
else
    printf '\nSome checks failed; see the SKILL.md failure table.\n'
fi
exit "$failed"
