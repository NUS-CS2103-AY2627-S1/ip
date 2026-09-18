# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Comfortable. Has written several non-trivial programs; fine with classes, collections, and exceptions, and can debug independently. Skip programming basics and focus explanations on design rationale and software engineering practice.
* IDE and level of expertise: VS Code on Linux. Prefer VS Code and command-line (terminal) instructions over IntelliJ-specific ones.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. This machine runs Linux with several JDKs installed under `/usr/lib/jvm`; the default `java`/`javac` on the PATH is already JDK 25. If a different version is ever active, switch with `sudo update-alternatives --config java` (and `--config javac`), or set `JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64` for a single command.

## Testing

After every change to code under `src/main/java/`, and before proposing a commit:

1. **Update `test/ui-test-plan.md` if the change alters console behaviour.** Add a case for a new command or behaviour, update the expected output of every case that shows wording or formatting you changed, and delete cases for behaviour you removed. A change that alters no console output needs no plan change.
2. **Run the build**, which compiles, runs the JUnit tests and checks the code style:

   ```bash
   ./gradlew build
   ```

   Checkstyle is wired into `check`, so a style violation fails the build. Its rules
   are the SE-EDU Java coding standard in mechanical form, so passing it is evidence
   the standard was followed rather than merely intended.

3. **Run the UI tests**, via the `test-ui` skill if your harness supports skills, or directly:

   ```bash
   python3 test/run-ui-tests.py
   ```

4. **Show the user the session transcript the runner prints**, so the commands typed and the output returned are visible rather than merely summarised.

Rules that make this worth doing:

* Write expected output from what the increment's specification requires, not by pasting what the program currently prints. Pasting actual output turns a test into the claim that today's behaviour equals today's behaviour, which cannot fail and therefore proves nothing.
* When a case fails, work out which side is wrong before changing anything. A failure means the code and the plan disagree; editing the plan to match broken code hides the bug instead of fixing it.
* Never describe a failing or unrun suite as passing. If the tests were not run, say so.

## Git

Do not commit or push unless explicitly asked.

### Commit messages

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html), which the course requires. The subject line rules are mandatory; a body is optional, but when included it must follow the conventions below.

Subject line:

* Limit to 50 characters (72 is a hard limit), because some tools truncate longer subjects.
* Use the imperative mood: `Add README.md`, not `Added README.md` or `Adding README.md`.
* Capitalize the first letter.
* Do not end with a period.
* Optionally prefix a scope or category, e.g. `Duke.java: Rename logo -> banner`, `bug fix: Add space after name`.

Body:

* Separate it from the subject with a blank line and wrap it at 72 characters.
* Explain WHAT the change is and WHY it was done that way, not HOW. The reader can consult the diff for the how.
* Structure it as: current situation (present tense), why it needs to change, what is being done about it (imperative mood, optionally introduced with `Let's`), why it is done that way, then any other relevant information.
* Avoid the words "currently" and "originally" when describing the current situation; they are implied.
* Use bullet lists instead of prose where they read more clearly.
* Do not repeat information already given in code comments of the same commit.

Write a body for any non-trivial commit. If the body grows long enough to describe several unrelated concerns, that is a signal to split the work into finer-grained commits instead.

### Tags

Use lightweight tags unless the user requests an annotated tag.

Each iP increment is tagged with its exact increment ID (e.g. `Level-2`, `A-Enums`) on the single commit that *completed* that increment. The tag is separate from the commit message, so the message should describe the change itself rather than name the increment. Tags are not pushed by default; push them explicitly.
