# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: Intermediate

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

Follow the `seedu-java-coding-standard` skill for all Java code written
or reviewed in this project (naming, formatting, imports, Javadoc, and
so on). Invoke it before writing new code and when checking existing
code for compliance.

## Git

Follow the `seedu-git-standard` skill for every commit and branch name
from now on (commit message subject/body format, branch naming). This
mandates the course's Git conventions: https://se-education.org/guides/conventions/git.html

Other:
* Use lightweight tags unless the user requests an annotated tag.
* Do not commit or push unless explicitly asked.

## GitHub Flavored Markdown (GFMD)

Use proper GFMD formatting whenever writing content that will render on
GitHub: PR/issue descriptions, PR/issue comments, and Markdown files
(README, docs, this file). Reference: https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax

Prefer these elements where they fit naturally, rather than plain
unformatted paragraphs:

* Headings (`#`, `##`, ...) to structure longer descriptions.
* Bullet lists (`-` or `*`) and numbered lists (`1.`, `2.`, ...) for
  multiple related points or ordered steps.
* Task lists (`- [ ]` / `- [x]`) for checklists of things done or left
  to do.
* Fenced code blocks with a language tag (```java ... ```) for any
  multi-line code or console output; inline code (`like this`) for a
  single identifier, command, or short snippet.
* Blockquotes (`>`) when quoting someone else's text or an external
  source.
* Hyperlinks (`[text](url)`) instead of bare URLs when linking to
  documentation or other resources.
* Text formatting (`**bold**`, `_italic_`, `~~strikethrough~~`) to
  emphasize or mark out key words sparingly, not throughout.
* An emoji here and there is fine for tone, but do not overuse it.

## Testing after code changes

After making any code change that could affect the chatbot's console
behavior (a new/changed command, output wording, or formatting):

1. Update `test/ui-test-plan.md` if needed — add a test case for new
   behavior, or update the expected output of an existing test case if
   the change was intentional.
2. Invoke the `test-ui` skill to run the test plan against the current
   build and confirm nothing regressed before considering the change
   done.
