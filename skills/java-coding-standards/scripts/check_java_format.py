#!/usr/bin/env python3
"""Check deterministic formatting rules from this project's Java standard."""

import argparse
from pathlib import Path
import re
import sys


ROOT = Path(__file__).resolve().parents[3]
DEFAULT_SOURCE_DIR = ROOT / "src" / "main" / "java"
MAX_LINE_LENGTH = 120


def find_java_files(paths):
    """Returns Java files selected by paths, or the main source tree by default."""
    if not paths:
        return sorted(DEFAULT_SOURCE_DIR.rglob("*.java"))

    files = []
    for path_string in paths:
        path = Path(path_string)
        if path.is_dir():
            files.extend(path.rglob("*.java"))
        elif path.suffix == ".java":
            files.append(path)
    return sorted(set(files))


def check_file(path):
    """Returns formatting violations found in one Java source file."""
    violations = []
    lines = path.read_text(encoding="utf-8").splitlines()

    for line_number, line in enumerate(lines, start=1):
        stripped = line.lstrip(" \t")
        indentation = line[:len(line) - len(stripped)]
        is_javadoc_continuation = stripped.startswith("*")

        if "\t" in indentation:
            violations.append((line_number, "use spaces instead of tabs for indentation"))
        if (indentation and not is_javadoc_continuation
                and len(indentation.replace("\t", "    ")) % 4 != 0):
            violations.append((line_number, "indent by a multiple of four spaces"))
        if line.rstrip(" \t") != line:
            violations.append((line_number, "remove trailing whitespace"))
        if len(line) > MAX_LINE_LENGTH:
            violations.append((line_number, f"limit lines to {MAX_LINE_LENGTH} characters"))
        if re.match(r"\s*import\s+(static\s+)?[\w.]+\.\*;", line):
            violations.append((line_number, "replace wildcard imports with explicit imports"))
        if re.match(r"\s*static\s+(public|protected|private)\b", line):
            violations.append((line_number, "place the access modifier before static"))
        if re.search(r"\b(if|for|while|switch|catch)\(", line):
            violations.append((line_number, "put a space between a control keyword and '('") )
        if re.search(r"\b(?:if|for|while)\s*\([^()]*\)(?!\s*(?:\{|$))", line):
            violations.append((line_number, "wrap conditional and loop bodies in braces"))
        if re.search(r"\b[A-Za-z_$][\w$]*\s*\[\]\s*(?:[=;,])", line):
            violations.append((line_number, "attach array brackets to the type, not the variable"))

    for line_number, line in enumerate(lines[:-1], start=1):
        if line.rstrip().endswith(")") and lines[line_number].lstrip().startswith("{"):
            violations.append((line_number + 1, "place an opening brace on the preceding line"))

    return violations


def main():
    """Runs project Java formatting checks and reports every violation."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("paths", nargs="*", help="Java files or directories to check")
    arguments = parser.parse_args()
    files = find_java_files(arguments.paths)

    if not files:
        print("No Java source files found.", file=sys.stderr)
        return 2

    violations = []
    for path in files:
        for line_number, message in check_file(path):
            try:
                display_path = path.relative_to(ROOT)
            except ValueError:
                display_path = path
            violations.append(f"{display_path}:{line_number}: {message}")

    if violations:
        print("Java format check failed:")
        print("\n".join(violations))
        return 1

    print(f"Java format check passed for {len(files)} file(s).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
