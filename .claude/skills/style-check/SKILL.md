---
name: style-check
description: Check the project's code and documentation against the three style guides it follows — the se-edu Markdown conventions and the Google developer documentation style guide, fetched live from their URLs, plus the se-edu Java conventions by way of the seedu-java-coding-standard skill. Use after writing or editing any .java or .md file, and whenever asked to check the style, review the conventions, or confirm that the documentation follows the guidelines.
---

# style-check

Checks changed `.java` and `.md` files against three style guides. The Markdown
and Google rules are **read from the guides themselves at run time**, not copied
into this file, so the check follows them as they stand today rather than as
they stood when this skill was written.

## The guides

| Applies to | Guide | URL |
| --- | --- | --- |
| `.java` | se-edu Java conventions (intermediate) | `https://se-education.org/guides/conventions/java/intermediate.html` |
| `.md` | se-edu Markdown conventions | `https://se-education.org/guides/conventions/markdown.html` |
| Prose in both | Google developer documentation style guide | `https://developers.google.com/style` |

**The Java guide belongs to the `seedu-java-coding-standard` skill.** It is this
project's coding standard, it covers every `.java` file including the tests, and
it has its own written-out rules and its own mechanical checks. Whenever a
`.java` file is in scope, invoke that skill and fold its findings into the
report below, instead of checking the Java rules here. If it is not available,
read `.claude/skills/seedu-java-coding-standard/SKILL.md` and follow it
directly. What stays here for `.java` files is the prose inside them: Javadoc,
comments, and the messages the program prints, which the Google guide governs.

The Google guide applies to every piece of writing a reader sees: Markdown
files, Javadoc, ordinary comments, and the messages the program prints.

`https://developers.google.com/style` is a landing page — a table of contents,
not the rules. Its rules live on subpages, so fetch `/style/highlights` for the
summary and go to a specific subpage only when a specific question comes up.

## Procedure

### 1. Decide what to check

Default to the files changed since the last commit, which is what "after
writing code" means in practice:

```bash
git diff --name-only HEAD -- '*.java' '*.md'; git ls-files --others --exclude-standard -- '*.java' '*.md'
```

If that comes back empty, or the user asked for a full review, check every
tracked `.java` and `.md` file instead:

```bash
git ls-files -- '*.java' '*.md'
```

Say which set you checked. A clean report over two changed files means
something much narrower than a clean report over the whole repository, and the
difference has to be visible to the reader.

### 2. Fetch the guides

Fetch with WebFetch, one call per page, always at the start of a run:

1. `https://se-education.org/guides/conventions/markdown.html` — ask for every
   rule. Skip this fetch when no `.md` file is in scope.
1. `https://developers.google.com/style/highlights` — ask for the key rules on
   voice, grammar, punctuation, and formatting.

These pages are short, so ask for the rules in full rather than for a summary.
A summary of a summary loses the specifics that make a rule checkable.

Fetch a Google subpage only when a specific question needs settling, and name
the question in the prompt. All of these return 200 as of the last check:

`/style/tone`, `/style/voice`, `/style/person`, `/style/tense`,
`/style/capitalization`, `/style/commas`, `/style/headings`, `/style/lists`,
`/style/code-in-text`, `/style/ui-elements`, `/style/link-text`,
`/style/product-names`, `/style/word-list`, `/style/markdown` — each prefixed
with `https://developers.google.com`.

WebFetch caches each URL for about 15 minutes, so running the check twice in
one session costs one set of fetches, not two.

**If a fetch fails, stop and say so.** Report which page could not be read and
that the check did not run. Do not fall back on remembered rules and present
the result as a completed check: the whole point of fetching is that the guide,
not a recollection of it, is the authority. Reporting "no problems found" after
failing to read the guide is worse than reporting nothing.

### 3. Mechanical pass

These commands find _candidates_ for the rules that a regex can see. Every hit
still has to be read against the fetched guide before it becomes a finding —
each of these over-reports by design, because a check that misses violations is
worse than one that raises a few false alarms.

The `.java` commands live in the `seedu-java-coding-standard` skill, which runs
them as part of its own procedure. Nothing needs repeating here.

For `.md` files, missing blank lines before lists and code fences, and ordered
lists numbered sequentially instead of with a repeated `1.`:

```bash
awk '
  /^ *```/ { infence = !infence
             if (infence && prev != "") print FILENAME":"FNR": no blank line before code fence"
             prev = $0; next }
  infence  { prev = $0; next }
  /^ *([*+-]|[0-9]+\.) / && prev != "" && prev !~ /^ *([*+-]|[0-9]+\.) / && prev !~ /^ +[^ ]/ {
             print FILENAME":"FNR": no blank line before list" }
  /^ *([2-9]|[1-9][0-9]+)\. / { print FILENAME":"FNR": ordered list not numbered generically" }
  /^#{1,6}[^ #]/              { print FILENAME":"FNR": no space after heading marker" }
  { prev = $0 }
' $FILES
```

For prose in both, the two things that came up most often in this repository:

```bash
grep -nEi '\b(normalis|organis|recognis|initialis|serialis|summaris|customis|capitalis|analys|paralys|behaviour|colour|favour|honour|labour|centre|licence|defence|offence|catalogue|dialogue)[a-z]*' $FILES
grep -nE '[^,]+, [^,]+ (and|or) ' $FILES              # possible missing serial comma
```

The first finds British spellings, which the Google guide rules out in favor of
American ones — settle any doubtful word against `/style/word-list` rather
than by feel. The second finds three-item lists that may be missing the serial
comma the guide requires; it over-reports heavily, since most hits are ordinary
two-clause sentences.

### 4. Reading pass

The rules a regex cannot see are the ones worth the most care. Read each
changed file against the fetched guides and judge:

* **Headings.** Sentence case, per the Google guide — not title case.
* **Voice and person.** Active voice, second person, present tense.
* **Formatting of things a reader types or sees.** Code-related text in code
  font, UI elements in bold, link text that describes its destination.

### 5. Report

Group findings by file, and give each one the rule it breaks and the guide it
came from, so the reader can check the call rather than take it on trust.
Separate them into:

* **Violations** — a stated rule is broken, and the fix is not a matter of
  taste.
* **Judgment calls** — the rule arguably applies, but the current code is
  defensible. Say which way you lean and why, and leave the decision open.

Finish with the files checked and the guides fetched. If nothing was found,
say that plainly; a clean result is a real result.

## Fixing what you find

Fix violations. Raise judgment calls and leave them alone unless the user
decides.

Two cases need care:

* **Anything that changes what the program prints** — an error message, a
  reply, the banner — is covered by `test/ui-test-plan.md`. Update the affected
  expected output first, then run the `test-ui` skill, as `AGENTS.md` requires.
  Flag the change in the report: product wording is the user's to keep or
  revert, even when a guide is against it.
* **Renaming anything public** — a class, a method, an enum constant — is a
  design change wearing a style rule's clothes. Propose it; do not do it
  unasked.

## Notes

* This skill reads and reports. It edits only to fix violations, and never
  edits `test/ui-test-plan.md` merely to make a test agree with a change.
* Checking this file with its own commands makes the spelling pattern match
  itself, on the line where the pattern is written. That one hit is an artifact
  of the check reading its own source; every other hit is real.
* The guides outrank this file. If a fetched page contradicts something written
  here, follow the page and say that the skill is out of date.
