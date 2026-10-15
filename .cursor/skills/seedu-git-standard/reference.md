# SE-EDU Git conventions — examples

Source: [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
Apply the rules in [SKILL.md](SKILL.md). These samples are the page’s own examples.

## Multi-commit PR step

```
Unify variations of toSet() methods

There are several methods that convert a collection to a set. In some
cases the conversion is in-lined as a code block in another method.

Unifying all those duplicated code improves the code quality.

As a step towards such unification, let's extract those duplicated code
blocks into separate methods in their respective classes. Doing so will
make the subsequent unification easier.
```

## Bug fix with bullets

```
Find command: make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of the keywords.

Let's,
* update the search algorithm to use case-insensitive matching
* add a script to migrate stress tests to the new format
```

## Refactoring with rationale

```
Person attributes classes: extract a parent class PersonAttribute

Person attribute classes (e.g. Name, Address, Age etc.) have some common
behaviors (e.g. isValid()).

The common behaviors across person attribute classes cause code duplication.

Extracting the common behavior into a super class allows us to use
polymorphism when dealing with person attributes. For example, validity
checking can be done for all attributes of a person in one loop.

Let's pull up behaviors common to all person attribute classes into a new
parent class named PersonAttribute.

Using inheritance is preferable over composition in this situation
because the common behaviors are not composable.

Refer to this S/O discussion on dealing with attributes
http://stackoverflow.com/some/question
```

## Body template

```
{current situation} -- use present tense

{why it needs to change}

{what is being done about it} -- use imperative mood

{why it is done that way}

{any other relevant info}
```

- Avoid “currently” and “originally” when describing the situation; they are implied.
- `Let's` marks the start of the change done in this commit.
