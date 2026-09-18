---
name: git-commit
description: Do the pre-commit checks, then commit, tag, and push this repository. Use when the user asks to commit, to save work to git, to tag an increment, or to push to GitHub.
---

# Git commit

You are responsible for commits in this repository. Do the checks in order. Stop at the first check that fails. Report the failure to the user, and wait.

## The two standards

* **Java code** — <https://se-education.org/guides/conventions/java/intermediate.html>, used in step 3.
* **Git commits and branches** — <https://se-education.org/guides/conventions/git.html>, used in steps 6 and 7.

Each standard has a helper script in `scripts/`. A script finds the mechanical breaches only. You must still read the code and the message yourself.

## Hard rules

* **Commit only when the user asks. Tag only when the user asks. Push only when the user asks.** A request to commit is not a request to push.
* **Stage by path.** Do not use `git add .` or `git add -A`. You must know why each file is in the commit.
* **Never commit a file that a tool creates.** See step 1.
* **Never skip a check without telling the user.** If you skip one, say which one, and why.
* **Show the real output** of each command. Do not report a check that you did not run.
* **Write the commit message. Show it. Wait for approval.** Then commit.

## 1. Look at what changed

```bash
git status --short
git diff --stat
git diff            # read it, do not only count the lines
```

Do not stage these:

| Pattern | Reason |
| --- | --- |
| `*.class`, `bin/`, `/build/`, `/out/`, `*.jar` | Build output. The build makes it again. |
| `ACTUAL.TXT`, `_temp/`, scratchpad files | Test output and scratch files. |
| Keys, tokens, passwords, personal data | Never in a public fork. |

If one of these files appears in `git status`, do not delete it. Tell the user, and add the pattern to `.gitignore` if the pattern is absent.

Then confirm that each remaining file belongs to the task that the user described. A file that you cannot explain does not go in the commit.

## 2. Compile

The repository has no build tool. Compile all classes together into the scratchpad, never into the repository:

```bash
SP="<scratchpad path from the system prompt>"
javac -d "$SP/bin" src/main/java/*.java
```

A compile error stops the commit. A warning does not stop the commit, but report it.

## 3. Check the code conventions

The standard is <https://se-education.org/guides/conventions/java/intermediate.html>.

Run the helper over the changed Java files:

```bash
bash .claude/skills/git-commit/scripts/check-conventions.sh $(git diff --name-only HEAD -- '*.java')
```

The helper output is a list of suspects, not a verdict. Read each line in the file before you call it a violation. The helper finds these:

* tab indentation (the standard is 4 spaces)
* a line longer than 120 characters (the soft limit is 110)
* trailing whitespace
* a wildcard import, such as `import java.util.*;`
* an opening brace on its own line (the standard is `while (x) {`)
* a missing space after `if`, `for`, `while`, `switch`, or `catch`
* a C-style array declaration, such as `int a[]`
* a public field that is not `static final`
* a `static final` field whose name is not `UPPER_CASE`
* a class name that is not `PascalCase`, or a method name that is not `camelCase`
* a public class or public method with no Javadoc above it

The helper cannot check these. Read the diff yourself:

* **Javadoc content.** The first sentence is a short summary. A method summary starts with a verb in the third person, such as "Returns" or "Adds". Use `@param` for all parameters or for none.
* **Names say what the thing is.** Methods are verbs. Classes are nouns. A boolean starts with `is`, `has`, `can`, or `was`. A collection has a plural name.
* **A variable is declared in the smallest scope, and is initialized where it is declared.**
* **One blank line between logical units.** No blank line between the Javadoc block and the declaration.
* **Every loop body and every `if` body has braces**, also when the body is one statement.

Fix a real violation in code that this commit already touches. If you find a violation in code that this commit does not touch, describe it to the user and leave it. Do not widen the diff.

## 4. Tests, or a green light

Use the first case that applies:

1. **`build.gradle` and `gradlew` exist** — run `./gradlew test`. All tests must pass.
2. **`src/test/java` exists but there is no build tool** — ask the user how they run the tests. Run them. All tests must pass.
3. **`text-ui-test/runtest.sh` exists** — run it. The output must match `EXPECTED.TXT`.
4. **No automated tests exist** — this is the current state of this repository. Then:
   * The `edge-case-probe` skill must have run against the **current** code, in this session. If it did not run, run it now.
   * List every open finding to the user, worst first. Include a finding that you decided not to fix.
   * Ask for a green light in one direct question, such as: "No automated tests exist. Open findings: A, B. Do I commit?"
   * **Wait for a clear yes.** Silence is not a yes. A yes for an earlier commit is not a yes for this one.

Report a failure with the real output. Never describe a test run that you did not do.

## 5. Cite the AI assistance

CLAUDE.md sets the reuse policy, and the course penalty for uncredited reuse is severe. This repository asks for **two records of AI use, not one**: a comment in the code **and** an entry in `README.md`. CLAUDE.md allows either one alone. Two is deliberate, so do not reduce it to one.

**In the code.** Every block that an AI tool generated carries a comment next to it that names the tool, the date, and what was asked for. A block added later to a file that already has a citation needs that citation extended, not left as it was.

**In `README.md`.** Under `## Acknowledgements`, keep a section `### AI-assisted code`. Add one entry per commit, newest first:

```markdown
#### {the subject line of the commit}

{Tool} generated the code below on {date}, from my specification.

* `src/main/java/File.java` — `methodOne`, `methodTwo`
* `src/main/java/Other.java` — whole file
```

Rules for the entry:

* List **every file** in the commit that received directly generated code. Name the methods. Write `whole file` when the tool generated all of it.
* A file that a person wrote or edited by hand does not appear, even when it is in the same commit.
* A commit with no generated code gets **no entry**. Do not write "none".
* Write the entry **before** the commit, and stage `README.md` with the code. The record and the code must land in the same commit.
* **Do not back-fill.** Commits made before 2026-08-27 stay as they are. This rule starts with the current commit.

**Other reuse.** Code from a website, a forum, or a classmate keeps the citation wording that CLAUDE.md defines, directly above the code. A new third-party library needs forum approval first, and its own acknowledgement in `README.md`.

A missing code comment, or a missing `README.md` entry, stops the commit until you add it.

## 6. Write the commit message

The standard is <https://se-education.org/guides/conventions/git.html>.

**Subject line**

* Imperative mood. `Add task deletion` is correct. `Added task deletion` and `Adding task deletion` are wrong.
* A capital first letter.
* No period at the end.
* 50 characters if possible. 72 characters is the hard limit.
* A scope prefix is optional, such as `Task class: Extract the date parser` or `chore: Update the release date`.

**Body**

* A blank line between the subject and the body.
* Wrapped at 72 characters. Blank lines between paragraphs. Bullets where they help.
* It states **what** changed and **why**. The reader sees **how** in the diff.
* The standard recommends this order: the situation now, in the present tense; the reason for a change; the action, in the imperative, often after "let's"; why you chose this solution; anything else that the reader needs.
* Give the reader enough to judge the change without a look at the code. If the body becomes long, the commit is probably too large. Split it.
* Write a body for each commit that is more than a rename. CLAUDE.md asks for the rationale.

The example from the standard:

```
Unify variations of toSet() methods

There are several methods that convert a collection to a set. In some
cases the conversion is in-lined as a code block in another method.

Unifying all those duplicated code improves the code quality.

As a step towards such unification, let's extract those duplicated code
blocks into separate methods in their respective classes.
```

**Check the message before you commit.** Write it to a file in the scratchpad, check that file, and commit that same file. Then the text that you checked is the text that you commit.

```bash
SP="<scratchpad path from the system prompt>"
cat > "$SP/msg.txt" <<'MSG'
<subject>

<body>
MSG
bash .claude/skills/git-commit/scripts/check-commit-message.sh "$SP/msg.txt"
```

The script reports `BREACH` for a rule that it can test, and `WARN` where it needs your judgement. It cannot judge whether the body says what and why. You must do that.

Show the message to the user. Wait for approval. Then:

```bash
git add <path> <path>              # by path, never .
git commit -F "$SP/msg.txt"        # commits the exact text that you checked
```

The course checks the **last 5 commit messages** at the end of the project. Do not rewrite an old message for this reason, but keep each new message compliant.

## 7. Branch, tag, and push

Only on request.

**Branch names.** Meaningful keywords in kebab-case, such as `refactor-ui-tests`. A branch for an issue starts with the issue number, as in `1234-ui-freeze-error`. The iP overrides this with its own pattern: one branch per increment, named `branch-{increment ID}`, such as `branch-Level-7`.

**Which increments use a branch.** From week 3 of the iP, `Level-7`, `Level-8`, `A-JavaDoc`, `A-CodingStandard`, and `Level-9` are done on a branch. The other `A-*` increments are done on `master`. Ask the user if you are not certain which case applies.

**The branch cycle:**

```bash
git switch -c branch-Level-7        # cut the branch from master
# ... commits, each through steps 1 to 6 ...
git switch master
git merge --no-ff branch-Level-7    # --no-ff forces a merge commit, even when a
                                    # fast-forward is possible. The course needs
                                    # that commit, because the tag goes on it.
git tag Level-7                     # lightweight tag on the MERGE commit
git push origin master
git push origin branch-Level-7      # the branch is a deliverable; do not delete it
git push origin --tags
```

**On `master`, with no branch:**

```bash
git tag A-Packages
git push origin master
git push origin --tags
```

Tags do not travel with `git push`. You must push them separately. The tag ID must match the increment ID exactly, and CLAUDE.md requires a lightweight tag.

If the tag exists already, stop, and ask the user. A moved tag confuses the grader.

## Report

After the commit, report in this shape:

```
Checked   | files staged: 3, build output excluded
Compiled  | javac clean
Style     | 2 long lines fixed, 1 missing Javadoc added
Tests     | none exist; user approved after 2 open probe findings
Credit    | code comments updated; README AI entry added for this commit
Commit    | a1b2c3d  Add task deletion
Pushed    | no (not requested)
```
