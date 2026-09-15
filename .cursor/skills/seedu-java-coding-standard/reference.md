# SE-EDU Java standard — examples

Source: https://se-education.org/guides/conventions/java/intermediate.html

## Test method names

```
sortList_emptyList_exceptionThrown()
getMember_memberNotFound_nullReturned()
sortList_emptyList()   // all empty-list cases
sortList()             // all cases for sortList
```

## Wrapped lines (8 extra spaces)

```
setText("Long line split"
        + "into two parts.");
if (isReady) {
    setText("Long line split"
            + "into two parts.");
}
```

## Control-flow shape

```
if (condition) {
    statements;
} else if (condition) {
    statements;
} else {
    statements;
}

try {
    statements;
} catch (Exception exception) {
    statements;
} finally {
    statements;
}

switch (condition) {
    case ABC:
        statements;
        // Fallthrough
    case DEF:
        statements;
        break;
    default:
        statements;
        break;
}
```

## Javadoc shape

```
/**
 * Returns lateral location of the specified position.
 * If the position is unset, NaN is returned.
 *
 * @param x X coordinate of position.
 * @param y Y coordinate of position.
 * @return Lateral location.
 * @throws IllegalArgumentException If zone is <= 0.
 */
public double computeLocation(double x, double y, int zone) {
    // ...
}
```

Single-line member Javadoc is allowed:

```
/** Number of connections to this database */
private int connectionCount;
```

## Checkstyle: member order

Fields, then constructors, then methods (static factories included):

```
public class TodoCommand {
    private final String description;

    private TodoCommand(String description) {
        this.description = description;
    }

    public static TodoCommand parse(String arguments) throws EkudException {
        return new TodoCommand(arguments.trim());
    }
}
```

## Checkstyle: keep `(` on the previous line

```
// Not allowed: '(' starts the continuation line
EkudException missing = assertThrows(EkudException.class,
        () -> Parser.parseOneBasedIndex("mark", ""));

// Allowed
EkudException missing = assertThrows(EkudException.class, () ->
        Parser.parseOneBasedIndex("mark", ""));
```

## Checkstyle: array initialization indent

Simple array children indent **4** spaces past the field (8 total at class level). IntelliJ often uses 12 and fails Checkstyle. Method-call elements such as `ofPattern(...)` may use the 8-space wrap. Prefer the same shape for every element:

```
private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("d/M/yyyy")
};
```
