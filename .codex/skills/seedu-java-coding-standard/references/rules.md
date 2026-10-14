# SE-EDU Java coding standard: project checklist

Source: <https://se-education.org/guides/conventions/java/intermediate.html>

- Name packages in lowercase; use PascalCase nouns for classes/enums and
  camelCase verbs for methods. Use camelCase English variable names and
  SCREAMING_SNAKE_CASE constants. Name booleans as predicates such as `isDone`
  or `hasData`, and collections with plural nouns.
- Use 4 spaces for indentation, K&R braces, spaces around operators and after
  commas, and braces for every loop and conditional body. Keep lines at 120
  characters or fewer (prefer 110 or fewer) and wrap for readability.
- Keep imports consistent and explicit; never use wildcard imports. Put every
  class in a package and keep variables in the smallest possible scope.
- Write comments in English using American spelling and avoid local slang.
  Write descriptive Javadoc for public classes and public methods unless an
  obvious getter/setter or an exactly applicable override makes it redundant.
- Start Javadoc summaries with third-person verbs such as `Returns`, `Adds`,
  and `Creates`. Keep tag descriptions punctuated and document all parameters
  together when parameter documentation adds value.

For topics not covered above, follow the Google Java Style Guide as directed by
the SE-EDU standard.
