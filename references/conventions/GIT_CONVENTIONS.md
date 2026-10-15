---
title: "Git conventions"
source: "https://se-education.org/guides/conventions/git.html"
author:
published:
created: 2026-08-29
description:
tags:
  - "clippings"
---
## Guides for SE student projects »

## Git conventions

**Legend**: basic rule | intermediate rule | advanced rule

## Commit message: Subject

**Every commit must have a well-written commit message *subject line*.**

- **Try to limit the subject line to 50 characters (hard limit: 72 chars)**

**==Use the imperative mood in the subject line.==**

**Capitalize the first letter of the subject line.**

**Do not end the subject line with a period.**

- **Good**: `Update sample data`
- **Bad**: `Update sample data.`

## Commit message: Body

**Commit messages for non-trivial commits should have a *body* giving details of the commit.**

- **Separate subject from body with a blank line.**
- **Wrap the body at 72 characters.**
- **Use blank lines to separate paragraphs.**

Example: A commit message for a commit that is part of a multi-commit PR:

```
Unify variations of toSet() methods

There are several methods that convert a collection to a set. In some
cases the conversion is in-lined as a code block in another method.

Unifying all those duplicated code improves the code quality.

As a step towards such unification, let's extract those duplicated code
blocks into separate methods in their respective classes. Doing so will
make the subsequent unification easier.
```

**Use bullet points as necessary.** Instead of relying entirely on paragraphs of text, use other constructs such as bullet lists when it helps.

Example: A commit message for a bug fix:

```
Find command: make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of the keywords.

Let's,
* update the search algorithm to use case-insensitive matching
* add a script to migrate stress tests to the new format
```

**Explain WHAT, WHY, not HOW**.

- Use the body to explain WHAT the commit is about and WHY it was done that way. The reader can refer to the diff to understand HOW the change was done.
- Give an explanation for the change(s) that is detailed enough so that the reader can judge if it is a good thing to do, without reading the actual diff to determine how well the code does what the explanation promises to do.  
	If your description starts to get too long, that's a sign that you probably need to split up your commit to finer grained pieces. \[adapted from: [git project](https://github.com/git/git/blob/e05806da9ec4aff8adfed142ab2a2b3b02e33c8c/Documentation/SubmittingPatches#L37-L132)\]
- Minimize repeating information that are given in code comments of the same commit.

**Structure the body as follows**:

```
{current situation} -- use present tense

{why it needs to change}

{what is being done about it} -- use imperative mood

{why it is done that way}

{any other relevant info}
```
- Avoid terms such as 'currently', 'originally' when describing the current situation. They are implied.
- The word `Let's` can be used to indicate the beginning of the section that describes the change done in the commit.

Example: A commit message for a code quality refactoring:

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

> [!info] Info
> Refer to the article *[How to Write a Git Commit Message](http://chris.beams.io/posts/git-commit/)* for more advice on writing good commit messages.

## Branch names

Follow these rules to improve consistency:

- Use a meaningful name consisting of some relevant keywords, in the *kebab case* format e.g., `refactor-ui-tests`.
- If the branch is related to an issue, use the format `issueNumber-some-keywords-from-issue-title` e.g., `1234-ui-freeze-error`