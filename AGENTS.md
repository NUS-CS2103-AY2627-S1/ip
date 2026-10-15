a# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [to be filled]
* IDE and level of expertise: [to be filled]

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Coding standard

The course mandates the standards at
<https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html>.
Follow them in all new and modified code; the `A-CodingStandard` increment is graded against them.

**Java — REQUIRED** (basic + intermediate rules of the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/index.html)).
The rules that bite most often in this project:

* Naming: classes/enums are PascalCase nouns; methods are camelCase verbs; variables camelCase;
  constants `UPPER_SNAKE_CASE`; collections take a plural name (`tasks`, not `taskList`);
  booleans and boolean-returning methods take an `is`/`has`/`was`/`can` prefix.
* Acronyms are not written in all-caps inside a name: `parseUiCommand()`, not `parseUICommand()`.
* Name length tracks scope: wide-scope names are spelled out, loop indices may stay `i`/`j`/`k`.
* Layout: 4-space indentation (never tabs), 8-space indentation for wrapped lines,
  lines under 110 chars (hard limit 120), K&R braces.
* Every `if`/`else`/`for`/`while` body is wrapped in braces, even a single statement.
* Every class sits in a package, and imports are listed explicitly — no wildcard imports.
* Separate logical units inside a method with a single blank line.
* Comments and names are in English, American spelling.

**Javadoc — REQUIRED for all public classes and methods.** Opening `/**` on its own line, first
sentence a short summary, blank line before the `@param`/`@return` block, `@param` given for every
parameter or none at all, `@return` omissible when obvious, no blank line between the comment and
the thing it documents. Writing this as we go means the `A-JavaDoc` increment needs no separate
branch — the tag can go straight onto the current commit.

**Markdown and prose — OPTIONAL** per the course
([SE-EDU Markdown style guide](https://se-education.org/guides/conventions/markdown.html),
[Google developer documentation style guide](https://developers.google.com/style)).
Follow them where convenient; do not spend effort retrofitting existing files.

## Git

Use lightweight tags unless the user requests an annotated tag.

Commit message **subject lines** follow the course's REQUIRED
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html):

* Imperative mood — `Add save-on-exit`, never `Added` or `Adds`.
* Begin with a capital letter, and do not end with a period.
* Keep within 50 characters where possible, 72 at the absolute most.
* Optionally prepend a context prefix where it aids scanning, e.g. `Storage class: Handle a
  missing data file` or `bug fix: Reject a blank todo description`.

Commit message **bodies** are optional for trivial commits and expected for the rest. Separate the
body from the subject with a blank line and wrap it at 72 characters. Structure the body as the
standard prescribes, one paragraph per part, omitting any part that has nothing to say:

```
{current situation} — use present tense

{why it needs to change}

{what is being done about it} — use imperative mood

{why it is done that way}

{any other relevant info}
```

* Describe the current situation in the **present tense** — `Rex knows the shape of every
  command`, not `Rex knew` or `Rex still knew`.
* Do not write `currently`, `originally`, `still` or the like: describing the situation already
  implies it.
* `Let's` may open the paragraph describing the change, e.g. `Let's move it into a Parser class`.
* Explain *what* changed and *why* — not *how*, which the diff already shows.
* Minimize repeating information already given in code comments in the same commit.

Do not rewrite past commit messages: that changes commit timestamps and distorts the iP progress
timeline seen by the course's tracking scripts.

Do not commit or push unless explicitly asked.

## iP increment workflow

Each increment that the weekly brief asks to be done on a branch follows the same sequence:

```
git switch -c branch-<Increment>       # e.g. branch-Level-7
# implement, committing at meaningful points
git switch master
git merge --no-ff branch-<Increment>   # --no-ff forces a real merge commit
git tag <Increment>                    # lightweight tag on the merge commit
git push origin master branch-<Increment> <Increment>
```

* `--no-ff` is not optional: a fast-forward merge erases the branch from the history graph, and the
  course's progress script only detects branches that produced a merge commit.
* Never delete a branch after merging it, and push the branch itself — pushing `master` does not
  carry a merged branch along with it.
* Tags must be pushed explicitly; they are not included in a normal `git push`.

## Testing

After any code update that changes the chatbot's behavior:

1. Update `test/ui-test-plan.md` if the change adds, removes, or alters any command or output format (see that file's "Format" section for the test case syntax).
2. Invoke the `test-ui` skill to run the console regression tests and confirm the change works as expected before considering the change done.
