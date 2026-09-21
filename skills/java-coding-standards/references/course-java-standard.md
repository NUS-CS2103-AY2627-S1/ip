# Course Java coding standard

This reference condenses the supplied course standard. It takes priority over
Google Java Style. Use Google Java Style only where this reference is silent.

## Naming

- Packages are lowercase. For school work, start packages with the project or
  group name; do not use `edu.nus.comp`-style names.
- Classes and enums are PascalCase nouns. Variables are camelCase. Methods are
  camelCase verbs. Constants use UPPER_SNAKE_CASE.
- Lowercase acronyms within identifiers (`openDvdPlayer`, not `openDVDPlayer`).
  Use English, descriptive names for broad scopes, short names only for short
  lived scratch values, and plural names for collections.
- Boolean names should read as booleans (`isOpen`, `hasData`, `canEvaluate`). A
  boolean setter has the form `setFound(boolean isFound)`.
- JUnit method names may use
  `featureUnderTest_testScenario_expectedBehavior`; omitted parts are allowed.

## Layout and statements

- Indent with four spaces, never tabs. Prefer lines no longer than 110
  characters; 120 is the hard limit. Continuation lines are indented eight
  spaces relative to the parent line.
- Use K&R braces and always use braces for loop and conditional bodies. Put
  conditionals on their own line.
- Break lines to improve readability: normally after commas and before
  operators; keep a method or constructor name with its opening parenthesis.
- Put one space around binary and ternary operators, after keywords before `(`,
  and after commas and `for` semicolons. Separate logical units with one blank
  line.
- Give each class a package; group related classes in packages. Use explicit,
  minimal imports, never wildcard imports, and keep import ordering consistent.
- Order class contents as documentation, declaration, static fields (public to
  private), instance fields (public to private), constructors, then methods.
  Put access modifiers first (`public static`, not `static public`).
- Attach array brackets to the type (`int[] values`). Declare variables in the
  smallest useful scope and initialize them when declared where feasible. Avoid
  public mutable fields and unnecessary `this`.
- Mark intentional old-style switch fall-through with `// Fallthrough`.

## Comments and Javadoc

- Write comments in American English, avoid local slang, and align them with the
  surrounding code. Trailing comments are permitted when useful.
- Write descriptive Javadoc for public classes and methods, except trivial
  getters/setters, exact overrides, and tests. Document nontrivial private
  methods too.
- Start Javadoc with a short third-person summary (`Returns`, `Adds`, `Sends`).
  Use the standard `/**` layout, an empty `*` line before tags, punctuation in
  tag descriptions, and no blank line between Javadoc and its declaration.
  Omit `@return` only when it is obvious; use either all `@param` tags or none
  when parameter names need no extra explanation.
