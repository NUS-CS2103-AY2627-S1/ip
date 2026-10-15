"""Checks this project against the mechanically checkable parts of the
SE-EDU Java coding standard (intermediate level).

Usage: py check-style.py [source-root ...]
Defaults to src/main/java and src/test/java. Exits non-zero on any finding.
"""

import io, os, re, glob, sys

SOFT = 110
HARD = 120

WILDCARD = re.compile(r'^import\s+(static\s+)?[\w.]+\.\*;')
IMPORT = re.compile(r'^import\s+(static\s+)?([\w.]+);')
TYPE_DECL = re.compile(r'^(public|final|abstract|sealed)?\s*(class|interface|enum|record)\s')
NO_SPACE_KEYWORD = re.compile(r'\b(if|while|for|switch|catch|synchronized|return)\(')
ARRAY_ON_VAR = re.compile(r'\b\w+\s+\w+\s*\[\s*\]\s*[=;]')
UNBRACED = re.compile(r'^\s*(if|else if|for|while)\s*\(.*\)\s*[^{\s].*;\s*$')
PUBLIC_FIELD = re.compile(r'^\s*public\s+(?!static\s+final\b)(?!\w+\s*\()[\w<>\[\].,?]+\s+\w+\s*(=|;)')

def import_group(name, is_static):
    if is_static:
        return 0
    if name.startswith('java.') or name.startswith('javax.'):
        return 1
    if name.startswith('org.'):
        return 2
    if name.startswith('com.'):
        return 3
    return 4

roots = sys.argv[1:] or ['src/main/java', 'src/test/java']
findings = []

for root in roots:
    for f in sorted(glob.glob(os.path.join(root, '**', '*.java'), recursive=True)):
        path = f.replace(os.sep, '/')
        raw = io.open(f, encoding='utf-8', newline='').read()
        lines = raw.replace('\r\n', '\n').split('\n')

        imports = []
        last_import = -1
        first_type = -1

        for i, line in enumerate(lines):
            n = i + 1
            if '\t' in line:
                findings.append((path, n, 'tab character (indentation must be 4 spaces)'))
            if line.rstrip() != line:
                findings.append((path, n, 'trailing whitespace'))
            length = len(line)
            if length > HARD:
                findings.append((path, n, 'line is %d chars (hard limit %d)' % (length, HARD)))
            elif length > SOFT:
                findings.append((path, n, 'line is %d chars (soft limit %d)' % (length, SOFT)))

            stripped = line.strip()
            if WILDCARD.match(stripped):
                findings.append((path, n, 'wildcard import: ' + stripped))
            m = IMPORT.match(stripped)
            if m:
                imports.append((n, import_group(m.group(2), bool(m.group(1))), stripped))
                last_import = i
            if first_type < 0 and TYPE_DECL.match(stripped):
                first_type = i

            # skip comment lines for the code-shape checks
            if stripped.startswith('*') or stripped.startswith('//') or stripped.startswith('/*'):
                continue
            code = re.sub(r'"(?:[^"\\]|\\.)*"', '""', line)
            mk = NO_SPACE_KEYWORD.search(code)
            if mk and mk.group(1) != 'return':
                findings.append((path, n, 'missing space after "%s"' % mk.group(1)))
            if ARRAY_ON_VAR.search(code):
                findings.append((path, n, 'array specifier attached to the variable, not the type'))
            if UNBRACED.match(code) and 'else' not in code.split(')')[0]:
                findings.append((path, n, 'conditional/loop body without braces'))
            if PUBLIC_FIELD.match(code) and first_type >= 0 and i > first_type:
                findings.append((path, n, 'public non-constant field: ' + stripped))

        groups = [g for _, g, _ in imports]
        if groups != sorted(groups):
            findings.append((path, imports[0][0] if imports else 1,
                             'import groups out of order (static, java/javax, org.*, com.*, then the rest)'))
        for a, b in zip(imports, imports[1:]):
            if a[1] == b[1] and a[2] > b[2]:
                findings.append((path, b[0], 'imports not alphabetical within group: %s after %s'
                                 % (b[2], a[2])))
        if last_import >= 0 and first_type > last_import:
            between = lines[last_import + 1:first_type]
            if not any(l.strip() == '' for l in between):
                findings.append((path, last_import + 2,
                                 'no blank line between the imports and the type declaration'))

print('files scanned: %d' % sum(len(glob.glob(os.path.join(r, '**', '*.java'), recursive=True)) for r in roots))
print('findings: %d' % len(findings))
print('')
by_file = {}
for path, n, msg in findings:
    by_file.setdefault(path, []).append((n, msg))
for path in sorted(by_file):
    print(path)
    for n, msg in sorted(by_file[path]):
        print('   %5d  %s' % (n, msg))
    print('')

sys.exit(1 if findings else 0)
