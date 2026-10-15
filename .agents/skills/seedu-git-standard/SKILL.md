# SE-EDU Git Standard

Follow the SE-EDU Git conventions for all commits in this project.

## Commit messages

- Use the imperative mood in the subject line.
- Capitalize the first word of the subject.
- Do not end the subject line with a period.
- Keep the subject line concise, preferably within 50 characters and no more than 72 characters.
- Explain what the commit does rather than describing the state of the repository.
- For non-trivial changes, include a body explaining the rationale and important implementation details.
- Separate the subject from the body with a blank line.

## Commits

- Make each commit represent one logical change.
- Avoid mixing unrelated changes in the same commit.
- Commit at appropriate points so that each commit leaves the project in a meaningful state.
- Do not commit generated files such as build output or JAR files unless explicitly required.

## Branches and tags

- Use descriptive branch names.
- Keep commits on the appropriate feature branch before merging into the main branch.
- Use lightweight tags unless an annotated tag is explicitly required.
- Tags for increments should identify the commit that completes that increment.

## Future commits

All future commits in this project must follow this Git standard.
