# SE-EDU conventions

This folder is the single place where the conventions this project follows are
written down. Each file is a working copy of one SE-EDU guide, trimmed to the
rules that apply here and annotated where this project makes a choice the guide
leaves open.

The upstream guides are the authority. If a page changes, update the file here
and say so in the commit body.

## The files

| File | Covers | Upstream source |
| --- | --- | --- |
| [git.md](git.md) | Commit message subject and body, branch names | <https://se-education.org/guides/conventions/git.html> |
| [java.md](java.md) | Naming, layout, statements, comments | <https://se-education.org/guides/conventions/java/index.html> |
| [logging.md](logging.md) | Logging levels and when to use each | <https://se-education.org/guides/conventions/java/logging.html> |
| [checkstyle.md](checkstyle.md) | Static style checking, Gradle and IDE setup | <https://se-education.org/guides/tutorials/checkstyle.html> |

The full guide index, covering everything not reproduced here, is at
<https://se-education.org/guides/>.

## What this project has committed to

- **Java standard level: basic + intermediate.** Advanced rules are recorded in
  [java.md](java.md) under a clearly marked section and are not enforced.
- **Java version: 25.** Set by `sourceCompatibility` in `build.gradle`.
- **Checkstyle is enforced.** `config/checkstyle/checkstyle.xml` is the machine
  readable half of [java.md](java.md); `./gradlew checkstyleMain checkstyleTest`
  must pass before a commit.
- **Commit subjects carry a category prefix.** The permitted prefixes are listed
  in [git.md](git.md). The prefix is optional in the upstream guide, but this
  project's history uses it consistently, so keep using it.

## How to use this folder

Read the relevant file *before* writing code or a commit message, not after.
Checkstyle catches layout and naming; it cannot catch a missing header comment
that says WHAT a method is for, a commit body that explains HOW instead of WHY,
or a user-facing string that reads ambiguously. Those are on the author.

When a rule and the surrounding code disagree, the rule wins. Fix the
surrounding code in the same change when the fix is small and in scope;
otherwise leave it and say so in the commit body.
