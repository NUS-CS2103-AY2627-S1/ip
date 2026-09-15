# Project context

This repository is a student's CS2103/T individual project (iP), based on Project Duke. The project is completed through weekly increments and is assessed using both the final submission and evidence from the weekly development process.

The official course instructions are the source of truth. In particular, consult the current weekly iP page and the [iP grading criteria](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-grading.html) before making decisions that could affect grading. If this file conflicts with an official requirement, follow the official requirement and alert the user to the conflict.

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
  * Add explanatory Javadoc comments to all public classes and nontrivial public methods. Document non-obvious fields and implementation details where doing so improves understanding.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.
* Help the student understand and remain responsible for AI-assisted work. Explain significant generated code and avoid making design choices the student cannot justify.

# Full-marks safeguards

The iP grading scheme is threshold-based: missing any required bar can reduce the score to less than half marks. For each requested change, identify the applicable increment and preserve evidence needed for grading. Do not claim that the project is ready for full marks without checking every relevant criterion below.

## Weekly workflow and repository structure

* Follow weekly instructions closely, implement increments in the specified order, and do not work more than one week ahead unless the official instructions allow it.
* Preserve grading-sensitive names and locations unless the official instructions explicitly change them:
  * Keep the GitHub repository name as `ip`.
  * Keep the default branch as `master`.
  * Keep source code under the project-root `src` directory.
  * Do not commit generated `.class` files or other build outputs.
* At the end of an increment, remind the user to:
  * create a focused commit that completes the increment;
  * apply a lightweight Git tag whose name exactly matches the increment ID, including capitalization; and
  * push both the commit and the tag so that the course dashboard can detect them.
* Do not combine multiple increments into one commit when separate increment evidence is expected.
* Track requirements that code changes alone cannot prove. The student must submit some deliverables and follow the other weekly requirements, including applicable peer reviews, in at least 4 of Weeks 2–6.
* Do not suggest changing old public history or force-pushing merely to repair commit messages or tags. Prefer safe corrective commits and follow the course FAQ for incorrectly placed tags.

## Implementation quality

When implementing or reviewing the final iP, protect all of these grading bars:

* Complete more than 90% of non-optional, applicable deliverables.
* Provide a working JavaFX GUI that satisfies the current course requirement.
* Complete at least two of the Week 6 optional increments using substantial AI assistance, as required by the Week 6 instructions.
* Prevent major bugs and handle common user, parsing, and storage errors gracefully.
* Use OOP sensibly: divide responsibilities among cohesive classes and include appropriate inheritance or polymorphism.
* Use exceptions for at least some error handling; do not silently ignore failures.
* Provide good JUnit tests for at least two nontrivial methods. Add further focused tests where they reduce meaningful risk.
* Follow the basic and intermediate rules of the course Java coding standard.
* Keep code neat and readable: use SLAP, avoid very long or deeply nested methods, remove dead or commented-out code, and avoid needless complexity.
* Preserve user data and existing behavior when refactoring. Prefer the simplest design that meets the current increment.

## Documentation and product identity

* Ensure the final product name is not `Duke` and is consistent everywhere, including the GUI title and documentation. Do not rename the `ip` repository.
* Keep `docs/Ui.png` named exactly with that capitalization. It should show one complete GUI window and ideally display the product name.
* Keep the published User Guide accurate, cover every nontrivial feature and command, and check the rendered website for broken links or major formatting problems.
* Update user-facing documentation whenever commands, behavior, storage, setup, or error messages change.

## Final verification

Before describing the final submission as ready:

* Re-read the current iP grading page and the applicable weekly submission instructions.
* Confirm that required increments and exact tags are present and pushed.
* Confirm that weekly process requirements and peer reviews were completed in at least 4 of Weeks 2–6; do not infer this from the final code.
* Run the relevant automated tests and build checks using Java 25.
* Build the cross-platform fat JAR using the Gradle Shadow task specified by the course, currently `./gradlew clean shadowJar`.
* Smoke-test the JAR from an otherwise empty directory using `java -jar`, exercising the main features and common error cases so hard-coded paths and missing-resource problems are detected.
* Verify the User Guide and product website in their published form.
* Ask the user to confirm that the latest public GitHub release contains only the intended `.jar` asset and that the iP Progress Dashboard, especially the `Git Standard` item, is green.
* Clearly report anything that could not be verified instead of assuming it passed.

# Project-specific requirements

## Mandatory standards skills

* Before creating, editing, formatting, or reviewing Java code, read and follow
  `.agents/skills/seedu-java-coding-standard/SKILL.md`.
* Before proposing, reviewing, or creating a Git commit, read and follow
  `.agents/skills/seedu-git-standard/SKILL.md`.

## Java version

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

* Use lightweight tags unless the official course instructions or the user require otherwise.
* When proposing a commit message subject, follow the course Git conventions:
  * use the imperative mood;
  * capitalize the first letter;
  * do not end with a period; and
  * aim for at most 50 characters and never exceed 72 characters.
* For a nontrivial commit, propose a body that explains what changed and why, separated from the subject by a blank line and wrapped at 72 characters.
* Keep commits focused. Minimally, make one commit per completed increment, and use additional small commits at meaningful development points.
* The final five iP commit subjects must comply with the Git standard for the dashboard item to be green.
* Do not run `git add`, commit, tag, push, create a release, or otherwise change remote state unless explicitly asked. When permission is absent, give the user the exact safe commands and explain what each command does.

## UI testing after code changes

After each Java code update, update `test/ui-test-plan.md` when user-visible
console behavior or coverage changes, then invoke the project-specific
`test-ui` skill. Show the recorded console input and output, and stop at the
first failed test case.
