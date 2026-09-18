# CS2103/T iP Requirements Guide

This is the living project guide for assistant work in this repository.
Before making future project changes, check this file and update it when the course pages change.

Source checked: 2026-08-27
Primary source: https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w2.html
Related source: https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-grading.html
Standards source: https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html

## Always Remember

- This is the CS2103/T individual project, based on Project Duke.
- Follow course instructions closely because grading scripts can miss work when names, paths, branches, tags, or repo setup deviate from the expected form.
- Do not clump all iP work into one short burst. Some increments need to be done weekly for full credit.
- It is fine to stay slightly ahead, but not more than one week ahead of the schedule unless the user explicitly decides otherwise.
- Use Java 25 for running and checking the project.
- Keep code in `[project root]/src`; grading scripts look there.
- After each code update, update `test/ui-test-plan.md` if the console behavior or relevant test coverage changed, then invoke the project-specific `test-ui` skill and report the test-session record.
- Do not commit or push unless the user explicitly asks.

## Week 2 Tasks

Deadline for this week's iP tasks: Friday, 2026-08-21, 16:00.
The course says catching up within one more week after the deadline is not penalized, but the target should still be the stated weekly deadline.

Week 2 setup requirements:

- Read the iP Overview and iP Grading pages.
- Ensure prerequisites are prepared: Java/programming language setup, Git, GitHub, AI coding tools, and IDE.
- Fork the upstream repository from `https://github.com/NUS-CS2103-AY2627-S1/ip`.
- Keep the fork name as `ip`.
- Keep the default branch name as `master`.
- Do not change the source location away from `[project root]/src`.
- When forking, uncheck `Copy the master branch only` so the full repo is copied.
- Enable Issues on the GitHub fork.
- Clone the fork locally.
- Avoid placing Git-controlled files in cloud-synced folders such as OneDrive or Dropbox.
- Run `Duke.java` to verify the setup works.

Week 2 implementation increments, in order:

- `Level-0`: Rename, greet, exit.
- `Level-1`: Echo.
- `Level-2`: Add and list tasks.
- `Level-3`: Mark tasks as done.
- `Level-4`: Support todos, events, and deadlines.
- `Level-5`: Handle errors.
- `Level-6`: Delete tasks.
- `A-Enums`: Use enums if applicable.

## Increment Workflow

- Commit at important points. Minimally, commit after completing each increment.
- After completing each increment, tag the exact commit with the exact increment ID, e.g. `Level-2`.
- Use lightweight tags unless the user requests annotated tags.
- Push code and tags to the user's fork only when the user asks.
- Remember that Git does not push tags automatically.
- Do not commit generated `.class` files or other build outputs.
- If a bug is found after an increment is tagged, fix it in a later commit rather than rewriting published history unless the user explicitly chooses otherwise.

## Standards and Conventions

The course standards page says the following are required unless a more specific course instruction overrides them.

Java:

- Follow the basic and intermediate rules in the SE-EDU Java coding standard: https://se-education.org/guides/conventions/java/intermediate.html
- For Java topics not covered by the SE-EDU standard, use the Google Java style guide as fallback guidance.
- Use lower-case package names based on the project name and logical groups; do not use `edu.nus.comp.*` or similar NUS-owned package names.
- Use `PascalCase` nouns for classes/enums, `camelCase` verbs for methods, `camelCase` for variables, and `SCREAMING_SNAKE_CASE` for constants.
- Name booleans so they read like booleans, normally with prefixes such as `is`, `has`, `was`, `can`, or `should`.
- Use 4 spaces for indentation, no tabs.
- Keep Java lines within the 120-character hard limit, aiming below 110 characters when practical.
- Use K&R brace style, with opening braces on the same line.
- Always use braces for loop and conditional bodies, even for single statements.
- Put every class in a package.
- Keep imports explicit; do not use wildcard imports such as `java.util.*`.
- Declare variables in the smallest reasonable scope and initialize them where they are declared when possible.
- Do not expose public class variables unless the class is a behavior-free data class; prefer encapsulation.
- Write comments in English, using American spelling and avoiding local slang.
- Write descriptive Javadoc/header comments for all classes and public methods, except simple getters/setters, exact inherited overrides, and test classes/methods.
- Keep comments useful and aligned with the code structure; avoid commented-out code.

Git:

- Follow the SE-EDU Git commit message subject conventions: https://se-education.org/guides/conventions/git.html
- Keep commit subjects ideally within 50 characters and never above 72 characters.
- Write commit subjects in imperative mood, capitalize the first letter, and do not end with a period.
- A subject may include a useful `<scope>:` or `<category>:` prefix, such as `Parser: Handle empty input` or `bug fix: Trim task names`.
- For non-trivial commits, include a body separated from the subject by a blank line.
- In commit bodies, wrap at 72 characters, use paragraphs or bullets as useful, and explain what changed and why rather than restating how the diff works.
- Use meaningful branch names in kebab case, such as `refactor-ui-tests`; for issue-related branches, prefer `issueNumber-some-keywords`.

Markdown and documentation:

- The SE-EDU Markdown style guide is optional but useful for project documentation: https://se-education.org/guides/conventions/markdown.html
- The Google developer documentation style guide is optional; when using it, follow project-specific/course guidance first, then the Google guide.
- For Markdown, prefer strict GitHub Flavored Markdown so GitHub Pages renders documentation reliably.
- Keep documentation clear and consistent for the iP's users and reviewers.

## Grading Bars

The iP is worth 15 marks. The grading page says students get full marks if all bars are met, but less than half marks if any bar is missed. Treat every bar below as important.

Implementation, 10 marks:

- More than 90% of non-optional, non-if-applicable deliverables are completed in the final version.
- If a requirement asks only for a minimal version, that minimal version is enough for grading, though going further can be useful for learning.
- The final iP has a GUI at least as good as part 4 of the JavaFX tutorial.
- At least two optional increments using AI assistance are completed as stated in the Week 6 iP instructions.
- No major bugs.
- Reasonable OOP is used, including some inheritance and sensible class division such as `Ui`, `Storage`, `Parser`, `Todo`, `Deadline`, and `Event`.
- At least half of public methods/classes have Javadoc comments.
- Code quality is reasonable: no blatant Java or Git convention violations, no chunks of commented-out code, and no very long methods or deeply nested code.
- At least some errors are handled using exceptions.
- At least two methods have good JUnit unit tests.

Project management, 2 marks:

- Submit some deliverables in at least 4 of the 5 iP weeks, Week 2 through Week 6.
- Follow weekly requirements such as Git/GitHub usage for each increment and peer reviews in at least 4 weeks.
- For full marks, the last 5 iP commit message subjects must comply with the course Git convention so the Git Standard tag is green.
- If the Git Standard tag is not green, prefer adding small legitimate code tweaks in new commits instead of rewriting past commit messages and force-pushing.

Documentation, 3 marks:

- The product website and user guide should guide users sufficiently.
- Cover all non-trivial features.
- Avoid major formatting errors in the published view.

## Assistant Working Rules

- Before each code-quality review or refactoring, read
  `.codex/code-quality-guidelines.md` and apply its stand-alone refactoring
  checklist.
- Before implementing a feature, map it to the current iP increment and grading bar.
- Do not update the project-root `README.md` for chatbot feature or user-guide changes; it is the assignment/setup README. Put product usage documentation in `docs/README.md` instead.
- Prefer small, understandable Java/OOP steps that fit the student's learning stage.
- Add Javadocs for classes and for public or nontrivial methods when adding or changing code.
- Keep code neat and avoid speculative abstractions.
- Use exceptions for meaningful error handling once the project reaches error-handling increments.
- Add or update focused JUnit tests when behavior becomes nontrivial.
- After changing code, check whether `test/ui-test-plan.md` needs corresponding updates, then run the project-specific `test-ui` skill before finishing.
- Preserve grading-script-sensitive conventions: repo name `ip`, branch `master`, source path `src`, exact increment tag names.
- Check this guide against the official course pages whenever the user asks about a new week or grading-related change.
