---
name: seedu-git-standard
description: The Git standard this project follows — the se-edu Git conventions for commit message subjects, bodies, and branch names. Use before drafting or proposing a commit message, before creating any commit, when naming or renaming a branch, and whenever asked to check a commit message or a branch name against the conventions.
---

# seedu-git-standard

Every commit in this project follows the
[se-edu Git conventions](https://se-education.org/guides/conventions/git.html).
So does every branch name.

The rules are written out in [rules.md](rules.md), so you can apply them while
drafting rather than only catching them afterwards. **The page at the URL above
outranks that file.** Fetch it whenever a rule is contested, whenever a finding
would change a message, and whenever `rules.md` is silent on the question in
front of you. If the fetch fails and the answer matters, say the check could not
be settled rather than guessing.

Two things outrank both: this project's own instructions in `AGENTS.md`, and
anything the user has said about how they want their messages written. See
"Local rules that outrank the guide" below.

## When to use this

* **Before drafting a commit message.** Read `rules.md` first, so the message
  comes out conforming. A subject rewritten before it is committed costs
  nothing; the same subject rewritten afterwards costs an amend, or cannot be
  fixed at all once it is pushed.
* **Before creating any commit**, and before proposing a message for approval,
  as `AGENTS.md` requires.
* **When creating or renaming a branch.**
* **Whenever asked** to check a commit message, a subject line, or a branch
  name against the conventions.

This skill covers the whole of it: the rules, the checks over a drafted
message, the branch name, getting the message approved, and what to do about a
message that has already been committed.

## Procedure

### 1. Write the draft to a file

Check a message that exists on disk, not one that exists only in your head —
that is how a 74-character subject gets through. Put it in the scratch
directory, then set `MSG` to its path.

### 2. Check the subject and body

```bash
subject=$(sed -n '1p' "$MSG")
n=${#subject}
[ "$n" -gt 72 ] && echo "FAIL subject is $n chars (hard limit 72)"
[ "$n" -gt 50 ] && [ "$n" -le 72 ] && echo "WARN subject is $n chars (aim for 50)"

case "$subject" in
  *.) echo "FAIL subject ends with a period" ;;
esac

# Everything after an optional "scope: " prefix. The guide's own examples show
# that prefix in lower case, so the capitalization and mood rules both apply to
# this part rather than to the whole line.
verb=$(printf '%s' "$subject" | sed -E 's/^[^:]{1,30}: *//')
case "$verb" in
  [A-Z]*) ;;
  *) echo "FAIL subject does not start with a capital letter: '$verb'" ;;
esac
printf '%s' "$verb" |
  grep -qiE '^(add|fix|updat|remov|chang|creat|implement|refactor|delet|mov|renam|us|writ|test)(ed|ing)\b' &&
  echo "FAIL subject is not in the imperative mood: '$verb'"

awk 'NR == 2 && $0 != "" { print "FAIL no blank line between subject and body" }
     NR >= 3 && length > 72 { print "FAIL body line "NR" is "length" chars (wrap at 72)" }
     NR >= 3 && tolower($0) ~ /(^|[^a-z])(currently|originally)([^a-z]|$)/ {
       print "WARN body line "NR" uses a term the guide asks you to avoid" }' "$MSG"
```

The imperative check is a heuristic over the verbs that come up most often. It
catches `Added` and `Adding` but not `Wrote`, so read the subject yourself as
well rather than treating a silent run as proof.

The last check is a warning rather than a failure, because the fix is usually
to rewrite the sentence in the present tense rather than to delete a word. Read
any near-miss the same way: "has so far been" hedges exactly as "currently"
does, and the pattern cannot see it. It looks at the body alone, so that a
subject naming those terms does not match itself.

### 3. Check the branch name

```bash
branch=$(git rev-parse --abbrev-ref HEAD)
case "$branch" in
  master|main)
    echo "OK '$branch' is the default branch; the naming rules do not apply" ;;
  branch-A-*|branch-Level-*)
    echo "OK '$branch' is a course increment branch; the course names it, not you" ;;
  *)
    printf '%s' "$branch" | grep -qE '^([0-9]+-)?[a-z0-9]+(-[a-z0-9]+)*$' ||
      echo "FAIL branch '$branch' is not in kebab case" ;;
esac
```

The increment branches are the exception, and a real one. The course prescribes
`branch-A-<Increment>` and `branch-Level-<n>` by name — `branch-A-CodingStandard`,
`branch-Level-7` — so they are not kebab case and cannot be made kebab case
without breaking what the course asks for. Reporting them as violations every
time would train the reader to ignore the check. Every branch the student names
themselves, such as `add-gradle-support`, follows the guide.

A branch name is worth fixing early, while `git branch -m <new-name>` is all it
takes. Once a branch is pushed and others have it, renaming stops being free.

### 4. Read what the commands cannot see

The rules that matter most are the ones no pattern can check:

* **Does the subject say what the commit does?** A subject that passes every
  mechanical rule can still be uninformative. `Update files` breaks nothing and
  tells the reader nothing.
* **Does the body explain what and why, rather than how?** The how is in the
  diff. A reader goes to the body for the reasoning that is _not_ in the diff.
* **Can a reader judge the change without reading it?** That is the standard
  the guide sets for a body. If it takes so much explaining that the body runs
  long, the commit wants splitting into finer-grained ones.
* **Is the scope prefix, if used, accurate?** `Person class: Remove static
  imports` is only right if the change is confined to that class.
* **Does the body follow the shape in `rules.md`** — the situation, why it has
  to change, what is being done, and why that way?

### 5. Report

Show the message in full, in a fenced block, then say what it was checked
against and anything that failed. Separate:

* **Violations** — a stated rule is broken.
* **Judgment calls** — the rule arguably applies, but the draft is defensible.
  Say which way you lean, and leave the decision to the user.

`AGENTS.md` requires a message to be approved before the commit is made, so end
by asking. Never commit off the back of a clean check alone.

## Local rules that outrank the guide

The guide is a general one; this project's own instructions come first, which
is the ordering the guide itself recommends.

* `AGENTS.md` asks for enough detail to explain the rationale. Weigh that
  against the size of the change: a one-line style fix does not need a body,
  and demanding one produces padding rather than reasoning. Flag a missing body
  only when the reasoning genuinely is not evident from the subject and diff.
* Follow any preference the user has stated about message length or shape, over
  the guide's default.
* Do not add trailers the project has not asked for, such as `Co-Authored-By`.
* `AGENTS.md` also says not to commit or push unless explicitly asked, and to
  use lightweight tags unless an annotated tag is requested.

## If the message is already committed

Sometimes the message under review turns out to be the one on `HEAD` already.
Check whether that commit has been pushed:

```bash
git log --oneline origin/$(git rev-parse --abbrev-ref HEAD)..HEAD
```

* **Not pushed** (the commit is listed): offer `git commit --amend` to correct
  the message, and ask before running it. Amending rewrites that commit, which
  is harmless while it exists only locally.
* **Already pushed** (nothing listed): report the problem and stop. Correcting
  it would mean rewriting published history and force-pushing, which can break
  anyone who has already pulled the branch. That is the user's call to make
  explicitly, not something to offer as a convenience.
