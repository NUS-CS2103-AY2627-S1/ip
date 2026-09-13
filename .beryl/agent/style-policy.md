# Style Policy

## Java

Java code follows the SE-EDU Java coding standard. Checkstyle enforces tabs, end-of-file newlines, line length (120 hard limit), explicit and ordered imports, no wildcard imports, identifier casing, abbreviation casing, array declaration form, declaration and modifier order, braces, variable declarations, whitespace, wrapping, indentation, Javadoc structure, and required public type/method documentation.

Review the following requirements when Checkstyle cannot determine intent: packages are lowercase English project names grouped by related responsibility; types and enums are PascalCase nouns; methods are camelCase verbs; variables are camelCase and scoped no wider than necessary; constants are SCREAMING_SNAKE_CASE; booleans read as predicates; use English and American spelling; keep lines below the 110-character soft limit; wrap for readability after commas and before operators; initialize variables at declaration where possible; do not expose public mutable class variables; and write descriptive headers for non-trivial private methods. Test methods may use `featureUnderTest_testScenario_expectedBehavior` naming.

Every production class belongs to the `odysseus` package. Related classes remain together until a distinct bounded context justifies a subpackage.

## Git

When a commit is authorized, create a complete, reviewable logical commit; do
not create a partial, placeholder, or message-less commit. Write its message
before committing and include all changes that belong to its stated boundary.
Split unrelated changes into separate commits instead of obscuring the intent
of either change.

Every subject uses imperative mood, begins with a capital letter, has a
50-character soft limit and a 72-character hard limit, and does not end with a
period. An optional `<scope>:` or `<category>:` prefix is allowed. Non-trivial
commits have a body separated from the subject by one blank line; body lines
are at most 72 characters and paragraphs are separated by blank lines. The
body explains the current situation in present tense, why change is needed,
what is being done in imperative mood, why that approach is used, and relevant
information. It explains what and why, not implementation mechanics; use
bullets when they make the rationale clearer.

If the explanation cannot fit a focused message, split the work into smaller
logical commits. Do not repeat details already clear from code comments or the
diff. Before committing, verify that the staged changes, commit boundary, and
message agree.

Gitlint enforces the 72-character limits, capitalization, optional-prefix structure, absence of a final period, and body line wrapping. Reviewers must verify imperative mood, the 50-character subject target, whether a body is needed, its what/why content, and branch names. Branches use meaningful kebab-case keywords; issue branches begin with the issue number followed by keywords from the title.

## Markdown

Markdown is GitHub Flavored Markdown. Do not impose a line-length wrap. Use a blank line before lists and fenced code blocks, put a space after every heading marker, and put blank lines between headings and their content. Put `>` on every line of a blockquote. Use `1.` for every ordered-list item, `*` for unordered-list items, and `_` rather than `*` for italics.

Markdownlint enforces the heading, blank-line, blockquote, generic-numbering, bullet-marker, and italic-marker rules. Reviewers must ensure GitHub Flavored Markdown syntax remains valid where a rule is outside markdownlint's scope.
