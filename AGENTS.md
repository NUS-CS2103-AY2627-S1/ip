# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Standard
* IDE and level of expertise: IntelliJ, standard

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

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

### Commit message: Subject

Every commit must have a well-written commit message subject line.

- Try to limit the subject line to 50 characters (hard limit: 72 chars).
  Rationale: Some tools show only a limited number of characters from the commit message.
- Use the imperative mood in the subject line.
  - Good: Add README.md
  - Bad: Added README.md
  - Bad: Adding README.md
- Capitalize the first letter of the subject line.
  - Good: Move index.html file to root
  - Bad: move index.html file to root
- Do not end the subject line with a period.
  - Good: Update sample data
  - Bad: Update sample data.
- You may add a `<scope>:` or `<category>:` in front, when applicable.
  - e.g. Person class: Remove static imports
  - e.g. Main.java: Remove blank lines
  - e.g. bug fix: Add space after name
  - e.g. chore: Update release date

### Commit message: Body

Commit messages for non-trivial commits should have a body giving details of the commit.

- Separate subject from body with a blank line.
- Wrap the body at 72 characters.
- Use blank lines to separate paragraphs.
- Explain WHAT, WHY, not HOW. Use the body to explain WHAT the commit is about and WHY it was done that way. The reader can refer to the diff to understand HOW the change was done.
- Give an explanation for the change(s) that is detailed enough so that the reader can judge if it is a good thing to do, without reading the actual diff to determine how well the code does what the explanation promises to do.
- If your description starts to get too long, that's a sign that you probably need to split up your commit to finer grained pieces.
- Minimize repeating information that are given in code comments of the same commit.
- Use bullet points as necessary. Instead of relying entirely on paragraphs of text, use other constructs such as bullet lists when it helps.
- Structure the body as follows:
  - `{current situation}` -- use present tense
  - `{why it needs to change}`
  - `{what is being done about it}` -- use imperative mood
  - `{why it is done that way}`
  - `{any other relevant info}`
  - Avoid terms such as 'currently', 'originally' when describing the current situation. They are implied.
  - The word "Let's" can be used to indicate the beginning of the section that describes the change done in the commit.

### Branch names

- Use a meaningful name consisting of some relevant keywords, in the kebab case format e.g., refactor-ui-tests.
- If the branch is related to an issue, use the format `issueNumber-some-keywords-from-issue-title` e.g., 1234-ui-freeze-error.
