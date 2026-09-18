# The se-edu Git conventions

Written out from
[the guide](https://se-education.org/guides/conventions/git.html). The guide is
the authority; this file is a copy for use while drafting. If the two disagree,
follow the guide and say that this file is out of date.

The guide marks each rule **basic** or **intermediate**. This project follows
both, so the levels are noted only to show which rules the guide treats as the
irreducible minimum.

The guide covers commit message subjects, commit message bodies, and branch
names. **It says nothing about tags.** `AGENTS.md` supplies the missing rule:
use a lightweight tag unless the user asks for an annotated one.

## The subject line

Every commit needs a well-written subject line (basic).

* **Keep the subject under 50 characters**, and never over 72. Some tools show
  only the first few dozen characters of a message, so a subject that runs long
  is a subject that gets truncated where the reader can see it.
* **Write it in the imperative mood** (basic) — the mood you would use to give
  an instruction, which is also the mood Git's own generated messages use.

  | Good | Bad |
  | --- | --- |
  | `Add README.md` | `Added README.md` |
  | `Add README.md` | `Adding README.md` |

* **Capitalize the first letter** (basic).

  | Good | Bad |
  | --- | --- |
  | `Move index.html file to root` | `move index.html file to root` |

* **Do not end it with a period** (basic).

  | Good | Bad |
  | --- | --- |
  | `Update sample data` | `Update sample data.` |

* **Add a scope or category prefix where it helps** (intermediate), as
  `<scope>: <subject>`. The guide's examples:

  ```text
  Person class: Remove static imports
  Main.java: Remove blank lines
  bug fix: Add space after name
  chore: Update release date
  ```

  The prefix is optional, and it earns its place only when it is accurate: a
  prefix naming one class is wrong on a commit that touches four.

## The body

A non-trivial commit needs a body explaining it (intermediate). A trivial,
self-explanatory one does not.

* **Separate the subject from the body with a blank line.** Without it, Git
  treats the whole thing as one long subject.
* **Wrap the body at 72 characters.** Git does not wrap it for you, and a
  terminal showing an indented log will run it off the edge.
* **Separate paragraphs with blank lines**, and **use bullet points** where a
  list reads better than prose.
* **Explain what and why, not how.** The how is in the diff. Write enough that
  a reader can judge whether the change is right _without_ reading the diff. If
  that takes more explaining than a body can hold, the commit is too big — split
  it into finer-grained commits instead of writing a longer message.
* **Do not repeat what the code comments already say.**

### The shape of a body

The guide asks for the body to move through these, in order:

1. The current situation, in the **present tense**.
1. Why it has to change.
1. What is being done about it, in the **imperative mood**.
1. Why it is being done that way.
1. Anything else the reader needs.

Two wording rules go with that shape:

* **Avoid "currently" and "originally".** Describing the present situation in
  the present tense makes them redundant, and they age badly: the commit that
  reads "currently" is describing a past that has since moved on.
* **Use "Let's" to introduce the change**, which is what separates the
  description of the problem from the description of the fix.

An example of the shape, written for this project:

```text
Parser: Indent the cases of a switch

The case labels in the two switch expressions sit at the same
indentation as the switch itself. The se-edu Java conventions put
them one level in, and IntelliJ's default for Java reformats them
back out, so the two disagree every time the file is touched.

Let's indent the case labels to match the conventions, and leave a
note in the coding standard skill so the next edit does not undo it.
```

## Branch names

* **Use relevant keywords in kebab case** (basic): lowercase words joined by
  hyphens.

  ```text
  refactor-ui-tests
  ```

* **Name a branch after its issue when it has one** (basic), as
  `issueNumber-some-keywords-from-issue-title`.

  ```text
  1234-ui-freeze-error
  ```

### The course's increment branches are exempt

This repository's increment branches are named by the course, not chosen:

```text
branch-A-CodingStandard
branch-A-JUnit
branch-Level-7
```

Those names are not kebab case, and renaming them to fit the guide would break
what the course asks for. Leave them alone. The rule above applies in full to
every branch you name yourself, such as `add-gradle-support`.
