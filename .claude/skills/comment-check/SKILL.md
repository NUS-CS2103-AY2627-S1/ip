---
name: comment-check
description: Find comments that have gone stale or that add nothing, propose removing them, and delete them once approved. Checks all of src/main/java. Use when asked to check comments, clean up comments, find stale or outdated comments, remove redundant or unnecessary comments, or tidy Javadoc.
---

# comment-check

Finds two kinds of comment and proposes deleting them:

* **Stale** — the comment says something the code no longer does.
* **Redundant** — the comment says only what the line below it already says.

It proposes; it does not delete on its own. A deleted comment takes information
with it that the code does not contain, and a wrong call is invisible
afterwards — the diff shows a comment gone, not the understanding that went
with it.

## What this project asks for

`AGENTS.md` mandates comments rather than merely allowing them: Javadoc on all
classes and on nontrivial methods and fields, and explanatory comments wherever
they aid understanding. This is a teaching repository, and its comments are
written for a student reader.

So the threshold here is deliberately conservative. **Propose a removal only
when the comment is stale, or when it is provably redundant.** In particular,
never propose removing a comment because it explains something a competent Java
programmer would already know — that is the audience this code is not written
for.

Protect, always:

* Any comment giving a **reason**: why this way, what was rejected, what
  breaks otherwise. Most comments in this codebase are of this kind.
* Javadoc on a public class or method, which the style guide requires.
* A comment that heads off a misreading — one explaining why code that looks
  wrong is right.

Propose removal of:

* A comment restating its own line (`// increment i` over `i++`).
* Commented-out code.
* Javadoc that adds nothing to the signature (`@return the name` on
  `getName()`), or a boilerplate stub.
* Decorative separators and banner comments.

## Procedure

### 1. Scope

All tracked Java sources, every run:

```bash
git ls-files -- 'src/main/java/*.java'
```

### 2. Mechanical pass

These find candidates. Every hit is read before it becomes a proposal.

**Provably broken Javadoc.** The compiler knows the signature, so it settles
what no regex can — an `@param` naming a parameter that is gone, `@return` on a
void method, a `{@link}` pointing at something that no longer exists:

```bash
javac -Xdoclint:all,-missing -d "$SCRATCH/doclint" $(git ls-files -- 'src/main/java/*.java')
```

Anything it reports is stale by definition, not by judgment. Silence here means
every Javadoc tag still matches its signature.

**The missing-comment baseline.** Record this now; step 6 compares against it:

```bash
javac -Xdoclint:all -d "$SCRATCH/baseline" $(git ls-files -- 'src/main/java/*.java') 2>&1 |
  grep -E 'warning: (no comment|use of default constructor)' | sort > "$SCRATCH/missing-before.txt"
```

**Identifiers named in a comment that no longer exist.** A comment referring to
a renamed or deleted method is stale and can be proved so:

```bash
for id in $(grep -rhoE '//.*' src/main/java | grep -oE '\b[a-z]+[A-Z][a-zA-Z0-9]*\b' | sort -u); do
  hits=$(grep -rE "\b$id\b" src/main/java | grep -vE '^[^:]*:\s*(//|\*)' | wc -l)
  [ "$hits" -eq 0 ] && echo "STALE? $id named in a comment but not found in code"
done
```

**Commented-out code:**

```bash
grep -rnE '^\s*//\s*[A-Za-z_].*[;{}]\s*$' src/main/java
```

**Leftover markers.** Restrict this to comment lines. A bare `grep TODO` is
useless in this repository, because `TODO` is also a command keyword, an enum
constant, and `TODO_FIELDS`:

```bash
grep -rnE '^\s*(//|\*|/\*).*\b(TODO|FIXME|XXX|HACK)\b' src/main/java
```

**Claims about counts.** A comment saying "the two halves" or "all three" goes
stale the moment a third or fourth arrives, and nothing in the build notices.
This repository has already had two such bugs in its documentation, so check
each hit against the code:

```bash
grep -rniE '^\s*(//|\*|/\*).*\b(two|three|four|five|six|seven|both|neither|either)\b' src/main/java
```

Leave "one" out of that pattern — it matches ordinary prose on nearly every
line and buries the real hits.

### 3. Reading pass

The mechanical pass finds the provable cases. Most staleness is not provable,
so read each file and ask of every comment:

* **Is it still true?** Compare what it claims against what the code beside it
  does. A comment describing a behavior, an order of operations, or a reason
  that no longer holds is stale even though it compiles.
* **Does it describe code that moved?** A comment can stay behind when the
  code it explained was extracted into another method.
* **Does it add anything?** Read the comment, then the line. If the line alone
  says the same thing, the comment is redundant.

### 4. Propose

Present every candidate in one table, before touching anything:

| Location | Comment | Verdict | Why |
| --- | --- | --- | --- |
| `File.java:42` | the comment, or its first line | stale / redundant | the evidence, in a few words |

Say which are provable — a doclint warning, a name that is gone — and which are
judgment. Then ask. Do not delete anything until the answer comes back, and
delete only what was approved.

### 5. Delete, do not rewrite

A stale comment is removed rather than corrected. Rewriting it would mean
guessing at intent that the code no longer supports, and a confidently rewritten
comment is harder to catch later than an absent one.

The exception is a comment whose removal would break a rule — see below.

### 6. Verify

After deleting, check that nothing required went missing:

```bash
javac -Xdoclint:all -d "$SCRATCH/after" $(git ls-files -- 'src/main/java/*.java') 2>&1 |
  grep -E 'warning: (no comment|use of default constructor)' | sort > "$SCRATCH/missing-after.txt"
diff "$SCRATCH/missing-before.txt" "$SCRATCH/missing-after.txt"
```

Any new line is a comment the style guide wanted and the deletion removed. Then
follow `AGENTS.md`: run `style-check`, then the `test-ui` tests, since the files
under `src/main/java` have changed.

## When deleting leaves a gap

Deleting a stale Javadoc from a public class or method satisfies this skill and
breaks the style guide, which requires that Javadoc to exist. Removing it is
still right — a wrong description is worse than none — but the gap has to be
visible rather than left for the next reader to find.

So when a proposed deletion would empty a required Javadoc, mark it in the
table and offer the replacement as a **separate, explicitly labeled** item:
first the removal, then a newly written comment describing what the code
actually does now. Never fold the two together and present it as a deletion,
and never write the replacement without saying that is what it is.

## Notes

* This skill proposes and, once approved, deletes. It never rewrites a comment
  in place, and never edits code.
* Silence is a real result. A codebase whose comments are all reasons, as this
  one largely is, should come back with an empty table.
