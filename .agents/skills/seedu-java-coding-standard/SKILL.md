---
name: seedu-java-coding-standard
description: Enforce SE-EDU Java coding conventions for naming, formatting, documentation, and code organization.
---

# SeEDU Java Coding Standard

Enforce the Java coding conventions from https://se-education.org/guides/conventions/java/intermediate.html for all Java code in this project.

## Java file naming and structure

- Use PascalCase for filenames (e.g., Person.java, AddressBook.java).
- Use singular noun for class names (e.g., Person not People).
- Use plural noun for collections (e.g., PersonList, Person[]).
- One top-level class per file; class name must match filename.
- File layout:
  1. Package declaration
  2. Import statements
  3. Class/interface declaration
  4. Blank line
  5. Class body (fields, then constructors, then methods)

## Naming conventions

- **Classes**: PascalCase, singular noun (e.g., Person, AddressBook)
- **Interfaces**: PascalCase, adjective or noun (e.g., Predicate, Sortable)
- **Methods**: camelCase, verb phrase (e.g., addPerson, getAddress)
- **Fields**: camelCase (e.g., name, address)
- **Constants**: UPPER_SNAKE_CASE (e.g., MAX_SIZE, DEFAULT_TIMEOUT)
- **Type parameters**: Single uppercase letter (e.g., T, E, V), or PascalCase for bounded parameters

## Formatting

- **Indentation**: 4 spaces per indent level (no tabs)
- **Line width**: 120 characters
- **Braces**: Opening brace on same line as statement, closing brace on new line
- **Blank lines**: 
  - One blank line between methods
  - One blank line between class fields and methods
  - Two blank lines between top-level classes
- **Imports**: One per line, no wildcard imports except test utilities

## Documentation

- **Javadoc for all public classes and methods**: Include @param, @return, @throws tags as needed
- **Field comments**: Add inline comments for non-obvious fields
- **Implementation comments**: Explain WHY, not HOW, in single-line comments
- **Class-level Javadoc**: Include @author tag when appropriate

## Code organization

- **Visibility**: Use most restrictive visibility appropriate (private > package-private > protected > public)
- **Constructors**: Declare private if class is utility or singleton
- **Getters/setters**: Only if field needs to be exposed; consider immutability
- **Exceptions**: Use checked exceptions for recoverable conditions, unchecked for programming errors

## Code quality

- **Avoid magic numbers**: Use named constants
- **Avoid long methods**: Split complex logic into well-named helper methods
- **Avoid duplicate code**: Extract common logic to shared methods
- **Fail fast**: Validate inputs early with descriptive error messages

## Testing

- **Test method names**: Describe what is tested, e.g., addPerson_validPerson_success()
- **Test structure**: Arrange-Act-Assert (AAA) pattern
- **Test classes**: Name after class being tested + Test suffix (e.g., PersonTest)
