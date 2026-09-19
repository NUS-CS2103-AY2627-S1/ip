# Git conventions

Source: <https://se-education.org/guides/conventions/git.html>

Apply this to every commit message and branch name in this project.

## Commit message: subject

Every commit must have a well-written subject line.

- **Limit the subject to 50 characters**, hard limit 72.
  Rationale: some tools show only a limited number of characters.
- **Use the imperative mood.**
  - Good: `Add README.md`
  - Bad: `Added README.md`, `Adding README.md`
  - Test it by completing the sentence *"If applied, this commit will ..."*.
- **Capitalise the first letter.**
  - Good: `Move index.html file to root`
  - Bad: `move index.html file to root`
- **Do not end the subject with a period.**
  - Good: `Update sample data`
  - Bad: `Update sample data.`
- You may add a `<scope>:` or `<category>:` in front when applicable, e.g.
  `Person class: Remove static imports`, `Main.java: Remove blank lines`,
  `bug fix: Add space after name`, `chore: Update release date`.

### This project's prefix vocabulary

The prefix is optional upstream, but this project's history uses one on every
commit. Keep it consistent with what is already in the log — check with
`git log --oneline -20` before choosing.

| Prefix | Used for |
| --- | --- |
| `Feat:` | New user-facing behaviour |
| `Fix:` | A correction to existing behaviour |
| `Refactor:` | A change to structure that keeps behaviour identical |
| `Test:` | Test code and test plans |
| `Docs:` | Documentation, header comments, agent files, conventions |
| `Build:` | Build configuration and tooling |

## Commit message: body

Commit messages for non-trivial commits should have a body giving details of the
commit.

- **Separate subject from body with a blank line.**
- **Wrap the body at 72 characters.**
- **Use blank lines to separate paragraphs.**
- **Use bullet points as necessary.** Instead of relying entirely on paragraphs
  of text, use other constructs such as bullet lists when it helps.
- **Explain WHAT and WHY, not HOW.** The reader can refer to the diff to
  understand how the change was done. Give an explanation detailed enough that
  the reader can judge whether the change is a good thing to do, without reading
  the diff to determine how well the code does what the explanation promises.
- If the description starts to get too long, that is a sign the commit should be
  split into finer-grained pieces.
- **Minimise repeating information** already given in code comments of the same
  commit.

### Required body structure

```text
{current situation} -- use present tense

{why it needs to change}

{what is being done about it} -- use imperative mood

{why it is done that way}

{any other relevant info}
```

- Avoid terms such as *currently* and *originally* when describing the current
  situation. They are implied.
- The word **`Let's`** can be used to indicate the beginning of the section that
  describes the change done in the commit.

### Example: a commit that is part of a multi-commit PR

```text
Unify variations of toSet() methods

There are several methods that convert a collection to a set. In some
cases the conversion is in-lined as a code block in another method.

Unifying all those duplicated code improves the code quality.

As a step towards such unification, let's extract those duplicated code
blocks into separate methods in their respective classes. Doing so will
make the subsequent unification easier.
```

### Example: a bug fix, using bullet points

```text
Find command: make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of the keywords.

Let's,
* update the search algorithm to use case-insensitive matching
* add a script to migrate stress tests to the new format
```

### Example: a code quality refactoring

```text
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

## Branch names

- Use a meaningful name consisting of relevant keywords, in **kebab-case**,
  e.g. `refactor-ui-tests`.
- If the branch is related to an issue, use
  `issueNumber-some-keywords-from-issue-title`, e.g. `1234-ui-freeze-error`.
- Branches that implement a numbered course increment keep the increment's own
  name, e.g. `branch-A-FullCommitMessage`. That name is set by the course, so it
  overrides the kebab-case rule.

## Scope of a commit

- One commit does **one** thing. If the subject needs the word "and", or the
  body has to describe two unrelated changes, split the commit.
- Keep changes to code separate from standalone changes to documentation,
  agent files, or build configuration.

## Before committing

- Propose the message and check it against the rules above.
- Verify the subject: imperative, capitalised, no full stop, within 50/72.
- Verify the body wraps at 72 characters.
- Never commit or push unless the user has explicitly asked for it.

## Further reading

- [How to Write a Git Commit Message](https://cbea.ms/git-commit/)
- [Conventional Commits](https://www.conventionalcommits.org/)
