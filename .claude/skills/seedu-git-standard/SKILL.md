---
name: seedu-git-standard
description: The SE-EDU Git conventions that ALL commits and branches in this project must follow - commit subject line format, commit body structure, and branch naming. Use whenever writing, proposing, amending, or reviewing a commit message, whenever creating or naming a branch, and whenever asked to check or fix Git history style.
---

# SE-EDU Git Conventions

Source: https://se-education.org/guides/conventions/git.html

Every commit made in this repository must satisfy the rules below. Note the
repository's standing rule in `AGENTS.md`: **do not commit or push unless
explicitly asked** - these conventions govern *how* a commit is written, not
*whether* to make one.

**Levels.** The source page grades each rule: (B) basic, (I) intermediate,
(A) advanced. This project follows all three, so every rule here is required.
The markers are kept so the origin of each rule stays visible.

## 1. Commit message: subject line

**(B) Every commit must have a well-written commit message subject line.**

| Rule | Good | Bad |
|---|---|---|
| Imperative mood (B) | `Add README.md` | `Added README.md`, `Adding README.md` |
| Capitalise the first letter (B) | `Move index.html file to root` | `move index.html file to root` |
| No trailing period (B) | `Update sample data` | `Update sample data.` |

- **(B) Length: aim for 50 characters, hard limit 72.**
  Rationale: some tools show only a limited number of characters from the
  commit message.
- **(B) An optional `<scope>:` or `<category>:` prefix** may lead the subject
  where applicable:
  - `Person class: Remove static imports`
  - `Main.java: Remove blank lines`
  - `bug fix: Add space after name`
  - `chore: Update release date`

  The rest of the subject still starts with a capital and stays imperative.
- The page notes that other subject conventions exist, such as the
  [Conventional Commits format](https://www.conventionalcommits.org/), which
  are more elaborate but have additional benefits. **This project uses the
  SE-EDU format above, not Conventional Commits** - do not write
  `feat:`/`fix:`/`BREAKING CHANGE:` subjects unless the user asks to switch.

## 2. Commit message: body

**(B) Commit messages for non-trivial commits should have a body giving details
of the commit.** Trivial ones (a typo fix, a version bump) may be subject-only.

**Mechanics**

- **(B) Separate subject from body with a blank line.**
- **(B) Wrap the body at 72 characters.**
- **(B) Use blank lines to separate paragraphs.**
- **(B) Use bullet points as necessary.** Rather than relying entirely on
  paragraphs of text, use constructs such as bullet lists when it helps.

**Content**

- **(I) Explain WHAT, WHY, not HOW.** Use the body to explain WHAT the commit
  is about and WHY it was done that way; the reader can refer to the diff to
  understand HOW.
- **(I) Give an explanation detailed enough that the reader can judge whether
  the change is a good thing to do**, without reading the actual diff to
  determine how well the code does what the explanation promises.
- **(I) If the description starts to get too long, that is a sign the commit
  needs splitting into finer-grained pieces.** (adapted from: the git project)
- **(I) Minimise repeating information given in code comments of the same
  commit.**

**(A) Structure the body as follows:**

```
{current situation} -- use present tense

{why it needs to change}

{what is being done about it} -- use imperative mood

{why it is done that way}

{any other relevant info}
```

- **(A) Avoid terms such as "currently" and "originally"** when describing the
  current situation. They are implied.
- **(A) The word `Let's`** can be used to indicate the beginning of the section
  that describes the change done in the commit.

## 3. Worked examples

**A commit that is part of a multi-commit PR:**

```
Unify variations of toSet() methods

There are several methods that convert a collection to a set. In some
cases the conversion is in-lined as a code block in another method.

Unifying all those duplicated code improves the code quality.

As a step towards such unification, let's extract those duplicated code
blocks into separate methods in their respective classes. Doing so will
make the subsequent unification easier.
```

**A bug fix, using bullets:**

```
Find command: make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of the keywords.

Let's,
* update the search algorithm to use case-insensitive matching
* add a script to migrate stress tests to the new format
```

**A code-quality refactoring, closing with rationale and a reference:**

```
Person attributes classes: extract a parent class PersonAttribute

Person attribute classes (e.g. Name, Address, Age etc.) have some common
behaviors (e.g. isValid()).

The common behaviors across person attribute classes cause code duplication.

Extracting the common behavior into a super class allows us to use
polymorphism when dealing with person attributes. For example, validity
checking can be done for all attributes of a person in one loop.

Let's pull up behaviors common to all person attribute classes into a new
parent class named PersonAttribute.

Using inheritance is preferable over composition in this situation
because the common behaviors are not composable.

Refer to this S/O discussion on dealing with attributes
http://stackoverflow.com/some/question
```

## 4. Branch names

Follow these rules to improve consistency:

- **Use a meaningful name consisting of some relevant keywords, in kebab-case**,
  e.g. `refactor-ui-tests`.
- **A branch related to an issue uses the format
  `issueNumber-some-keywords-from-issue-title`**, e.g. `1234-ui-freeze-error`.

## 5. Writing the commit

Compose the message in a file or a quoted heredoc so the 72-character wrapping
survives the shell - never build a multi-paragraph body out of repeated `-m`
flags, which discards your line breaks:

```bash
git commit -F - <<'EOF'
Subject line in imperative mood

Body paragraph explaining the situation and why it must change,
hard-wrapped at 72 characters.

Let's do the thing, because of this reason.
EOF
```

**Tips for SourceTree users** (Tools -> Options -> General -> Commit settings):

- Tick **"Display a column guide at commit message at [72] characters"** to keep
  the body within 72 characters. If the option is disabled, first tick **"use
  fixed-width fonts for commit messages"** - a column guide only makes sense
  with a fixed-width font.
- Tick **"spell check commit messages"** in the same place.

## Further reading

- [How to Write a Git Commit Message](https://cbea.ms/git-commit/) - the article
  the SE-EDU page recommends for more advice on writing good commit messages.
- [Conventional Commits](https://www.conventionalcommits.org/) - the alternative
  subject convention the page mentions (not used by this project).

## Self-check before proposing any commit message

```
- Subject: imperative, capitalised, no trailing period, <= 72 chars?
- Blank line between subject and body?
- Body wrapped at 72 chars?
- Body says WHAT and WHY, not HOW?
- Body detailed enough to judge the change without reading the diff?
- No "currently"/"originally"?
- Body short enough that the commit does not need splitting?
- Bullet lists used where they beat prose?
- Branch name kebab-case (and issue-number-prefixed if issue-related)?
```
