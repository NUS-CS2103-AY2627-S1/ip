# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [to be filled]
* IDE and level of expertise: [to be filled]

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.


## Git message standard
Commit message: Subject
 Every commit must have a well-written commit message subject line.

Try to limit the subject line to 50 characters (hard limit: 72 chars)
Rationale: Some tools show only a limited number of characters from the commit message.

 Use the imperative mood in the subject line.

 Good: Add README.md
 Bad: Added README.md
 Bad: Adding README.md
 Capitalize the first letter of the subject line.

 Good: Move index.html file to root
 Bad: move index.html file to root
 Do not end the subject line with a period.

 Good: Update sample data
 Bad: Update sample data.
You may add a <scope>: or <category>: in front, when applicable.

e.g. Person class: Remove static imports
Main.java: Remove blank lines
bug fix: Add space after name
chore: Update release date
There are other commit subject conventions such as the Conventional Commits Format which are more elaborate but have additional benefits.



## C++ to Java → JUnit → JUnit: Intermediate
Given below are some noteworthy JUnit concepts, as per the JUnit 5 User Guide.

Annotations: In addition to the @Test annotation you've seen already, there are many other annotations in JUnit. For example, the @Disabled annotation can be used to disable a test temporarily. [more ...]

Pre/post-test tasks: In order to allow individual test methods to be executed in isolation and to avoid unexpected side effects due to mutable test instance state, JUnit creates a new instance of each test class before executing each test method. It is possible to supply code that should be run before/after every test method/class (e.g., for setting up the environment required by the tests, or cleaning up things after a test is completed) by using test instance lifecycle annotations such as @BeforeEach @AfterAll. [more ...]

Conditional test execution: It is possible to configure tests to run only under certain conditions. For example, @TestOnMac annotation can be used to specify tests that should run on Mac OS only. [more ...]

Assumptions: It is possible to specify assumptions that must hold for a test to be executed (i.e., the test will be skipped if the assumption does not hold). [more ...]

Tagging tests: It is possible to tag tests (e.g., @Tag("slow") so that tests can be selected based on tags. [more ...]

Test execution order: By default, JUnit executes test classes and methods in a deterministic but intentionally nonobvious order. This ensures that subsequent runs of a test suite execute tests in the same order, thereby allowing for repeatable builds. But it is possible to specify a specific testing order. [more ...]

Test hierarchies: Normally, we organize tests into separate test classes. If a more hierarchical structure is needed, the @Nested annotation can be used to express the relationship among groups of tests. [more ...]

Repeated tests: JUnit provides the ability to repeat a test a specified number of times by annotating a method with @RepeatedTest and specifying the total number of repetitions desired. [more ...]

Parameterized tests make it possible to run a test multiple times with different arguments. The parameter values can be supplied using a variety of ways e.g., an array of values, enums, a csv file, etc. [more ...]

Dynamic tests: The @TestFactory annotation can be used to specify factory methods that generate tests dynamically. [more ...]

Timeouts: The @Timeout annotation allows one to declare that a test should fail if its execution time exceeds a given duration. [more ...]

Parallel execution: By default, JUnit tests are run sequentially in a single thread. Running tests in parallel—for example, to speed up execution—is available as an opt-in feature. [more ...]

Extensions: JUnit supports third-party extensions. The built-in TempDirectory extension is used to create and clean up a temporary directory for an individual test or all tests in a test class. [more ...]


## Java coding standard (basic + intermediate)
Versions: [Basic Rules] [Basic + Intermediate Rules] [All Rules]

 Use the Google Java style guide for any topics not covered in this document.

Legend:  basic rule |  intermediate rule |  advanced rule

Naming
Layout
Statements: Package/Import | Types | Variables | Loops | Conditionals
Comments
References
Contributors
Naming
 Names representing packages should be in all lower case.

com.company.application.ui
 More on package naming

For school projects, the root name of the package should be your group name or project name followed by logical group names. e.g. todobuddy.ui, todobuddy.file etc.

 Rationale: Your code is not officially ‘produced by NUS’, therefore do not use edu.nus.comp.* or anything similar.

 Class/enum names must be nouns and written in PascalCase.

Line, AudioSystem
 Variable names must be in camelCase.

line, audioSystem
 Constant names must be all uppercase using underscore to separate words (aka SCREAMING_SNAKE_CASE). To find what exactly are considered constants, refer to this page in the Google Java Style Guide.

MAX_ITERATIONS, COLOR_RED
 Names representing methods must be verbs and written in camelCase.

getName(), computeTotalWidth()
Underscores may be used in test method names using the following three part format featureUnderTest_testScenario_expectedBehavior()

e.g. sortList_emptyList_exceptionThrown() getMember_memberNotFound_nullReturned

Third part or both second and third parts can be omitted depending on what's covered in the test. For example, the test method sortList_emptyList() will test sortList() method for all variations of the 'empty list' scenario and the test method sortList() will test the sortList() method for all scenarios.

 Abbreviations and acronyms should not be uppercase when used as a part of a name.

 Good

exportHtmlSource();
openDvdPlayer();
 Bad

exportHTMLSource();
openDVDPlayer();
 All names should be written in English.

 Rationale: The code is meant for an international audience.

 Variables with a large scope should have long names, variables with a small scope can have short names.

Scratch variables used for temporary storage or indices can be kept short. A programmer reading such variables should be able to assume that its value is not used outside a few lines of code. Common scratch variables for integers are i, j, k, m, n and for characters c and d.

 Rationale: When the scope is small, the reader does not have to remember it for long.

 Boolean variables/methods should be named to sound like booleans

// variables
isSet, isVisible, isFinished, isFound, isOpen, hasData, wasOpen

// methods
boolean hasLicense();
boolean canEvaluate();
boolean shouldAbort = false;
As much as possible, use a prefix such as is, has, was, etc. for boolean variable/method names so that linters can automatically verify that this style rule is being followed.

Setter methods for boolean variables must be of the form:

void setFound(boolean isFound);
 Rationale: This is the naming convention for boolean methods and variables used by Java core packages. It also makes the code read like normal English e.g. if(isOpen) ...

 Plural form should be used on names representing a collection of objects.

Collection<Point> points;
int[] values;
 Rationale: Enhances readability since the name gives the user an immediate clue of the type of the variable and the operations that can be performed on its elements. One space character after the variable type is enough to obtain clarity.

 Iterator variables can be called i, j, k etc.

Variables named j, k etc. should be used for nested loops only.

for (Iterator i = points.iterator(); i.hasNext(); ) {
    ...
}

for (int i = 0; i < nTables; i++) {
    ...
}
 Rationale: The notation is taken from mathematics where it is an established convention for indicating iterators.

 Associated constants should have a common prefix.

static final int COLOR_RED   = 1;
static final int COLOR_GREEN = 2;
static final int COLOR_BLUE  = 3;
 Rationale: This indicates that they belong together, and make them appear together when sorted alphabetically.

Layout
 Basic indentation should be 4 spaces (not tabs).

for (i = 0; i < nElements; i++) {
    a[i] = 0;
}
 Rationale: Just follow it 

 Line length should be no longer than 120 chars.

Try to keep line length shorter than 110 characters (soft limit). But it is OK to exceed the limit slightly (hard limit: 120 chars). If the line exceeds the limit, use line wrapping at appropriate places of the line.

Indentation for wrapped lines should be 8 spaces (i.e. twice the normal indentation of 4 spaces) more than the parent line.

setText("Long line split"
        + "into two parts.");
if (isReady) {
    setText("Long line split"
            + "into two parts.");
}
 Place line break to improve readability

When wrapping lines, the main objective is to improve readability. Do not always accept the auto-formatting suggested by the IDE.

In general:

Break after a comma.

Break before an operator. This also applies to the following "operator-like" symbols: the dot separator ., the ampersand in type bounds <T extends Foo & Bar>, and the pipe in catch blocks catch (FooException | BarException e)

totalSum = a + b + c
        + d + e;
setText("Long line split"
        + "into two parts.");
method(param1,
        object.method()
                .method2(),
        param3);
A method or constructor name stays attached to the open parenthesis ( that follows it.
 Good

someMethodWithVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLongName(
        int anArg, Object anotherArg);
 Bad

someMethodWithVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLongName
        (int anArg, Object anotherArg);
Prefer higher-level breaks to lower-level breaks. In the example below, the first is preferred, since the break occurs outside the parenthesized expression, which is at a higher level.
 Good

longName1 = longName2 * (longName3 + longName4 - longName5)
        + 4 * longname6
 Bad

longName1 = longName2 * (longName3 + longName4
        - longName5) + 4 * longname6;
Here are two acceptable ways to format ternary expressions:
alpha = (aLongBooleanExpression) ? beta : gamma;
alpha = (aLongBooleanExpression)
        ? beta
        : gamma;
 Use K&R style brackets (aka Egyptian style).

 Good

while (!done) {
    doSomething();
    done = moreToDo();
}

 Bad

while (!done)
{
    doSomething();
    done = moreToDo();
}
 Rationale: Just follow it. 

 Method definitions should have the following form:

public void someMethod() throws SomeException {
    ...
}
 The if-else class of statements should have the following form:

if (condition) {
    statements;
}




if (condition) {
    statements;
} else {
    statements;
}


if (condition) {
    statements;
} else if (condition) {
    statements;
} else {
    statements;
}
 The for statement should have the following form:

for (initialization; condition; update) {
    statements;
}
 The while and the do-while statements should have the following form:

while (condition) {
    statements;
}
do {
    statements;
} while (condition);
 The switch statement should have the following form:

switch (condition) {
    case ABC:
        statements;
        // Fallthrough
    case DEF:
        statements;
        break;
    case XYZ:
        statements;
        break;
    default:
        statements;
        break;
}
switch (condition) {
    case ABC -> method("1");
    case DEF -> method("2");
    case XYZ -> method("3");
    default -> method("0");
}
int size = switch (condition) {
    case ABC -> 1;
    case DEF -> 2;
    case XYZ -> 3;
    default -> 0;
}
The explicit // Fallthrough comment should be included whenever there is a case statement without a break statement.

 Rationale: Leaving out the break is a common error, and it must be made clear that it is intentional when it is not there.

 A try-catch statement should have the following form:

try {
    statements;
} catch (Exception exception) {
    statements;
}


try {
    statements;
} catch (Exception exception) {
    statements;
} finally {
    statements;
}
 White space within a statement

It is difficult to give a complete list of the suggested use of whitespace in Java code. The examples below however should give a general idea of the intentions.

Rule	 Good	 Bad
Operators should be surrounded by a space character.	a = (b + c) * d;	a=(b+c)*d;
Java reserved words should be followed by a white space.	while (true) {	while(true){
Commas should be followed by a white space.	doSomething(a, b, c, d);	doSomething(a,b,c,d);
Colons should be surrounded by white space when
used as a binary/ternary operator.
Does not apply to switch x:. Semicolons in for
statements should be followed by a space character.	for (i = 0; i < 10; i++) {	for(i=0;i<10;i++){
 Rationale: Makes the individual components of the statements stand out and enhances readability.

 Logical units within a block should be separated by one blank line.

// Create a new identity matrix
Matrix4x4 matrix = new Matrix4x4();

// Precompute angles for efficiency
double cosAngle = Math.cos(angle);
double sinAngle = Math.sin(angle);

// Specify matrix as a rotation transformation
matrix.setElement(1, 1,  cosAngle);
matrix.setElement(1, 2,  sinAngle);
matrix.setElement(2, 1, -sinAngle);
matrix.setElement(2, 2,  cosAngle);

// Apply rotation
transformation.multiply(matrix);
 Rationale: Enhances readability by introducing white space between logical units. Each block is often introduced by a comment as indicated in the example above.

Statements
Package and Import Statements
 Put every class in a package.

Every class should be part of some package.

 Rationale: It will help you and other developers easily understand the code base when all the classes have been grouped in packages.

 The ordering of import statements must be consistent.

 Rationale: A consistent ordering of import statements makes it easier to browse the list and determine the dependencies when there are many imports.

Example:
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.loadui.testfx.GuiTest;
import org.testfx.api.FxToolkit;

import com.google.common.io.Files;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import junit.framework.AssertionFailedError;
 IDEs have support for auto-ordering import statements. However, note that the default orderings of different IDEs are not always the same. It is recommended that you and your team use the same IDE and stick to a consistent ordering.

 Imported classes should always be listed explicitly.

 Good

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
 Bad

import java.util.*;


 Rationale: Importing classes explicitly gives an excellent documentation value for the class at hand and makes the class easier to comprehend and maintain. Appropriate tools should be used in order to always keep the import list minimal and up to date. IDEs can be configured to do this easily.

Types
 Array specifiers must be attached to the type not the variable.

 Good

int[] a = new int[20];
 Bad

int a[] = new int[20];
 Rationale: The arrayness is a feature of the base type, not the variable. Java allows both forms however.

Variables
 Variables should be initialized where they are declared and they should be declared in the smallest scope possible.

 Good

int sum = 0;
for (int i = 0; i < 10; i++) {
    for (int j = 0; j < 10; j++) {
        sum += i * j;
    }
}

 Bad

int i, j, sum;
sum = 0;
for (i = 0; i < 10; i++) {
    for (j = 0; j < 10; j++) {
        sum += i * j;
    }
}
 Rationale: This ensures that variables are valid at any time. Sometimes it is impossible to initialize a variable to a valid value where it is declared. In these cases it should be left uninitialized rather than initialized to some phony value.

 Class variables should never be declared public unless the class is a data class with no behavior. This rule does not apply to constants.

 Bad

public class Foo{

   public int bar;

}
 Rationale: The concept of Java information hiding and encapsulation is violated by public variables. Use non-public variables and access functions instead.

Loops
 The loop body should be wrapped by curly brackets irrespective of how many lines there are in the body.

 Good

for (i = 0; i < 100; i++) {
    sum += value[i];
}
 Bad

for (i = 0, sum = 0; i < 100; i++)
    sum += value[i];

 Rationale: When there is only one statement in the loop body, Java allows it to be written without wrapping it between { }. However that is error prone and very strongly discouraged from using.

Conditionals
 The conditional should be put on a separate line.

 Good

if (isDone) {
    doCleanup();
}
 Bad

if (isDone) doCleanup();


 Rationale: This helps when debugging using an IDE debugger. When writing on a single line, it is not apparent whether the condition is really true or not.

 Single statement conditionals should still be wrapped by curly brackets.

 Good

InputStream stream = File.open(fileName, "w");
if (stream != null) {
    readFile(stream);
}
 Bad

InputStream stream = File.open(fileName, "w");
if (stream != null)
    readFile(stream);

The body of the conditional should be wrapped by curly brackets irrespective of how many statements.

 Rationale: Omitting braces can lead to subtle bugs.

Comments
 All comments should be written in English.

Use American spelling.
Avoid local slang.

 Rationale: The code is meant for an international audience.

 Write descriptive header comments for all public classes/methods.

You MUST write header comments for all classes, public methods. But they can be omitted for the following cases:
i. Getters/setters
ii. When overriding methods (provided the parent method's Javadoc applies exactly as is to the overridden method)
iii. In classes/methods used for testing

 Rationale: public methods are meant to be used by others and the users should not be forced to read the code of the method to understand its exact behavior. The code, even if it is self-explanatory, can only tell the reader HOW the code works, not WHAT the code is supposed to do.

 Javadoc comments should have the following form:

/**
 * Returns lateral location of the specified position.
 * If the position is unset, NaN is returned.
 *
 * @param x X coordinate of position.
 * @param y Y coordinate of position.
 * @param zone Zone of position.
 * @return Lateral location.
 * @throws IllegalArgumentException If zone is <= 0.
 */
public double computeLocation(double x, double y, int zone)
        throws IllegalArgumentException {
    // ...
}
Note in particular:

The opening /** on a separate line.
Write the first sentence as a short summary of the method, as Javadoc automatically places it in the method summary table (and index).
In method header comments, the first sentence should start in the form Returns ..., Sends ..., Adds ... etc. (not Return or Returning etc.)
Subsequent * is aligned with the first one.
Space after each *.
Empty line between description and parameter section.
Punctuation behind each parameter description.
No blank line between the documentation block and the method/class.
@return can be omitted if the method does not return anything or the return value is obvious from the rest of the comment.
@params can be omitted if all parameters of a method have self-explanatory names, or they are already explained in the main part of the comment i.e., if none of the @params add any value. This means the comment will have @param for all its parameters, or none.
When writing Javadocs for overridden methods, the @inheritDoc tag can be used to reuse the header comment from the parent method but with further modifications e.g., when the method has a slightly different behavior from the parent method.
Javadoc of class members can be specified on a single line as follows:

/** Number of connections to this database */
private int connectionCount;
 Comments should be indented relative to their position in the code.

 Good

while (true) {
    // Do something
    something();
}
 Bad

while (true) {
        // Do something
    something();
}
 Bad

while (true) {
// Do something
    something();
}
 Rationale: This is to avoid the comments from breaking the logical structure of the program.

Note that trailing comments such as the below are allowed as well.

    process('ABC'); // process a dummy String frst

Implementation → Code Quality → Naming → Introduction
Proper naming improves the readability of code. It also reduces bugs caused by ambiguities regarding the intent of a variable or a method.

 There are only two hard things in Computer Science: cache invalidation and naming things.  -- Phil Karlton

W4.6b 
Implementation → Code Quality → Naming → Basic → Use nouns for things and verbs for actions
 Every system is built from a domain-specific language designed by the programmers to describe that system. Functions are the verbs of that language, and classes are the nouns. 
-- Robert C. Martin, Clean Code: A Handbook of Agile Software Craftsmanship

Use nouns for classes/variables and verbs for methods/functions.

 Example Naming a class and a method:

Name for a	 Bad	 Good
Class	CheckLimit	LimitChecker
Method	result()	calculate()
Distinguish clearly between single-valued and multi-valued variables.

 Example Naming single-valued and multi-valued variables:

 Good

Person student;
ArrayList<Person> students;
W4.6c 
Implementation → Code Quality → Naming → Basic → Use standard words
Use correct spelling in names. Avoid 'texting-style' spelling. Avoid foreign language words, slang, and names that are only meaningful within specific contexts/times e.g., terms from private jokes, a TV show currently popular in your country.

W4.6d 
Implementation → Code Quality → Naming → Intermediate → Use name to explain
A name is not just for differentiation; it should explain the named entity to the reader accurately and at a sufficient level of detail.

 Example Names that explain, at a sufficient level of detail:

 Bad	 Good
processInput() (what 'process'?)	removeWhiteSpaceFromInput()
flag	isValidInput
temp	
If a name has multiple words, they should be in a sensible order.

 Example Word order within a name:

 Bad	 Good
bySizeOrder()	orderBySize()
Imagine going to the doctor's and saying "My eye1 is swollen"! Don’t use numbers or case to distinguish names.

 Example Names distinguished only by a number or by case:

 Bad	 Bad	 Good
value1, value2	value, Value	originalValue, finalValue
W4.6e 
Implementation → Code Quality → Naming → Intermediate → Not too long, not too short
While it is preferable not to have lengthy names, names that are 'too short' are even worse. If you must abbreviate or use acronyms, do it consistently. Explain their full meaning at an obvious location.

W4.6f 
Implementation → Code Quality → Naming → Intermediate → Avoid misleading names
Related things should be named similarly, while unrelated things should NOT.

 Example Consider these variables:

colorBlack: hex value for color black
colorWhite: hex value for color white
colorBlue: number of times blue is used
hexForRed: hex value for color red
This is misleading because colorBlue is named similarly to colorWhite and colorBlack but has a different purpose while hexForRed is named differently but has a very similar purpose to the first two variables. The following is better:

hexForBlack hexForWhite hexForRed
blueColorCount
Avoid misleading or ambiguous names (e.g., those with multiple meanings), similar-sounding names, hard-to-pronounce ones (e.g., avoid ambiguities like "is that a lowercase L, capital I or number 1?", or "is that number 0 or letter O?"), almost similar names.

 Example Names that are misleading, ambiguous, or hard to say:

 Bad	 Good	Reason
phase0	phaseZero	Is that zero or letter O?
rwrLgtDirn	rowerLegitDirection	Hard to pronounce
right left wrong	rightDirection leftDirection wrongResponse	right is for 'correct' or 'opposite of 'left'?
redBooks readBooks	redColorBooks booksRead	red and read (past tense) sound the same
FiletMignon	egg	If the requirement is just a name of a food, egg is a much easier choice to type/say than FiletMignon

Readability
 Video Q+
W5.4a 
Implementation → Code Quality → Readability → Introduction
 Programs should be written and polished until they acquire publication quality.  --Niklaus Wirth

Among various dimensions of code quality, such as run-time efficiency, security, and robustness, one of the most important is readability (aka understandability). This is because in any non-trivial software project, code needs to be read, understood, and modified by other developers later on. Even if you do not intend to pass the code to someone else, code quality is still important because you will become a 'stranger' to your own code someday.

W5.4b 
Implementation → Code Quality → Readability → Basic → Avoid long methods
Avoid long methods as they often contain more information than what the reader can process at a time. Consider if shortening is possible when a method goes beyond 30 LoC. The bigger the haystack, the harder it is to find a needle.

W5.4c 
Implementation → Code Quality → Readability → Basic → Avoid deep nesting
If you need more than 3 levels of indentation, you're screwed anyway, and should fix your program. --Linux 1.3.53 Coding Style

Avoid deep nesting -- the deeper the nesting, the harder it is for the reader to keep track of the logic.

In particular, avoid arrowhead style code.


 Example A real code example:

 Bad

int subsidy() {
    int subsidy;
    if (!age) {
        if (!sub) {
            if (!notFullTime) {
                subsidy = 500;
            } else {
                subsidy = 250;
            }
        } else {
            subsidy = 250;
        }
    } else {
        subsidy = -1;
    }
    return subsidy;
}
 Good

int calculateSubsidy() {
    int subsidy;
    if (isSenior) {
        subsidy = REJECT_SENIOR;
    } else if (isAlreadySubsidized) {
        subsidy = SUBSIDIZED_SUBSIDY;
    } else if (isPartTime) {
        subsidy = FULLTIME_SUBSIDY * RATIO;
    } else {
        subsidy = FULLTIME_SUBSIDY;
    }
    return subsidy;
}
W5.4d 
Implementation → Code Quality → Readability → Basic → Avoid complicated expressions
Avoid complicated expressions, especially those having many negations and nested parentheses. If you must evaluate complicated expressions, have them done in steps (i.e., calculate some intermediate values first and use them to calculate the final value).

 Example Evaluating a complicated expression in steps:

 Bad

return ((length < MAX_LENGTH) || (previousSize != length))
        && (typeCode == URGENT);
 Good

boolean isWithinSizeLimit = length < MAX_LENGTH;
boolean isSameSize = previousSize != length;
boolean isValidCode = isWithinSizeLimit || isSameSize;

boolean isUrgent = typeCode == URGENT;

return isValidCode && isUrgent;
 The competent programmer is fully aware of the strictly limited size of his own skull; therefore he approaches the programming task in full humility, and among other things he avoids clever tricks like the plague.  -- Edsger Dijkstra

W5.4e 
Implementation → Code Quality → Readability → Basic → Avoid magic numbers
Avoid magic numbers in your code. When the code has a number that does not explain the meaning of the number, it is called a "magic number" (as in "the number appears as if by magic"). Using a named constant makes the code easier to understand because the name tells us more about the meaning of the number.

 Example Replacing magic numbers with named constants:

 Bad

return 3.14159;
...
return 9;
  

 Good

static final double PI = 3.14159;
static final int MAX_SIZE = 10;
...
return PI;
...
return MAX_SIZE - 1;
Similarly, you can have ‘magic’ values of other data types.

 Example A magic string:

 Bad

return "Error 1432"; // A magic string!
Avoid any magic literals in general, not just magic numbers.

W5.4f 
Implementation → Code Quality → Readability → Basic → Make the code obvious
Make the code as explicit as possible, even if the language syntax allows it to be implicit. Here are some examples:

[Java] Use explicit type conversion instead of implicit type conversion.
[Java, Python] Use parentheses/braces to show groupings even when they can be skipped.
[Java, Python] Use enumerations when a certain variable can take only a small number of finite values. For example, instead of declaring the variable 'state' as an integer and using values 0, 1, 2 to denote the states 'starting', 'enabled', and 'disabled' respectively, declare 'state' as type SystemState and define an enumeration SystemState that has values 'STARTING', 'ENABLED', and 'DISABLED'.
W5.4g 
Implementation → Code Quality → Readability → Intermediate → Structure code logically
Lay out the code so that it adheres to the logical structure. The code should read like a story. Just as you use section breaks, chapters, and paragraphs to organize a story, use classes, methods, indentation, and line spacing in your code to group related segments of the code. For example, you can use blank lines to separate groups of related statements.

Sometimes, the correctness of your code does not depend on the order in which you perform certain intermediary steps. Nevertheless, this order may affect the clarity of the story you are trying to tell. Choose the order that makes the story most readable.

 Example Grouping related statements, in an order that tells the story:

 Bad

statement A1
statement A2
statement A3
statement B1
statement C1
statement B2
statement C2
  

 Good

statement A1
statement A2
statement A3

statement B1
statement B2

statement C1
statement C2
W5.4h 
Implementation → Code Quality → Readability → Intermediate → Do not 'trip up' reader
Avoid things that would make the reader go ‘huh?’, such as:

unused parameters in the method signature
similar things that look different
different things that look similar
multiple statements in the same line
data flow anomalies, such as assigning values to variables and then modifying them before using the assigned values
W5.4i 
Implementation → Code Quality → Readability → Intermediate → Practice KISSing
Do not try to write ‘clever’ code. “Keep it simple, stupid” (KISS), as the old adage goes. For example, do not dismiss the brute-force yet simple solution in favor of a complicated one because of some ‘supposed benefits’ such as 'better reusability' unless you have a strong justification.

 Debugging is twice as hard as writing the code in the first place. Therefore, if you write the code as cleverly as possible, you are, by definition, not smart enough to debug it.  -- Brian W. Kernighan

 Programs must be written for people to read, and only incidentally for machines to execute.  -- Abelson and Sussman

W5.4j 
Implementation → Code Quality → Readability → Intermediate → Avoid premature optimizations
Optimizing code prematurely has several drawbacks:

You may not know which parts are the real performance bottlenecks. This is especially the case when the code undergoes transformations (e.g., compiling, minifying, transpiling, etc.) before it becomes an executable. Ideally, you should use a profiler tool to identify the actual bottlenecks of the code first, and optimize only those parts.
Optimizing can complicate the code, affecting correctness and readability.
Hand-optimized code can be harder for the compiler to optimize (the simpler the code, the easier it is for the compiler to optimize). In many cases, a compiler can do a better job of optimizing the runtime code if you don't get in the way by trying to hand-optimize the source code.
Make it work, make it right, make it fast is a popular saying in the industry, which means in most cases, getting the code to perform correctly should take priority over optimizing it. If the code doesn't work correctly, it has no value no matter how fast/efficient it is.

 Premature optimization is the root of all evil in programming.  -- Donald Knuth

Of course, there are cases in which optimizing takes priority over other things e.g., when writing code for resource-constrained environments. This guideline is simply a caution that you should optimize only when needed.

W5.4k 
Implementation → Code Quality → Readability → Intermediate → SLAP hard
Avoid having multiple levels of abstraction within a code fragment. Note: The book The Productive Programmer (by Neal Ford) calls this the Single Level of Abstraction Principle (SLAP) while the book Clean Code (by Robert C. Martin) calls this One Level of Abstraction per Function.

 Example Two levels of abstraction mixed within one code fragment:

 Bad (readData(); and salary = basic * rise + 1000; are at different levels of abstraction)

readData();
salary = basic * rise + 1000;
tax = (taxable ? salary * 0.07 : 0);
displayResult();
 Good (all statements are at the same level of abstraction)

readData();
processData();
displayResult();
Also ensure that the code is written at the highest level of abstraction possible.

 Example The same logic written at a low level of abstraction, and at a higher one:

 Bad (all statements are at low levels of abstraction)

low-level statement A1
low-level statement A2
low-level statement A3
low-level statement B1
low-level statement B2
if condition X :
    low-level statement C1
    low-level statement C2
 Good (all statements are at the same high level of abstraction)

high-level step A
high-level step B
if condition X:
  high-level step C
That said, it is sometimes possible to pack two levels of abstraction into the code without affecting readability that much, provided each step in the higher-level logic is clearly marked using comments and separated (e.g., using a blank line) from adjacent steps.

 Example The following pseudocode packs two levels of abstraction, with each higher-level step marked by a comment and separated by a blank line.

//high-level step A
low-level statement A1
low-level statement A2
low-level statement A3

//high-level step B
low-level statement B1
low-level statement B2

if condition X :
    //high-level step C
    low-level statement C1
    low-level statement C2
W5.4l 
Implementation → Code Quality → Readability → Advanced → Make the happy path prominent
The happy path should be clear and prominent in your code. Restructure the code to make the happy path (i.e., the execution path taken when everything goes well) less-nested as much as possible. It is the ‘unusual’ cases that should be nested. Someone reading the code should not get distracted by alternative paths taken when error conditions happen. One technique that could help in this regard is the use of guard clauses.

 Example Guard clauses can reduce the nesting of the happy path.

 Bad

if (!isUnusualCase) {  //detecting an unusual condition
    if (!isErrorCase) {
        start();    //main path
        process();
        cleanup();
        exit();
    } else {
        handleError();
    }
} else {
    handleUnusualCase(); //handling that unusual condition
}
In the code above,

unusual condition detections are separated from their handling.
the main path is nested deeply.
 Good

if (isUnusualCase) { //Guard Clause
    handleUnusualCase();
    return;
}

if (isErrorCase) { //Guard Clause
    handleError();
    return;
}

start();
process();
cleanup();
exit();
In contrast, the above code

deals with unusual conditions as soon as they are detected so that the reader doesn't have to remember them for long.
keeps the main path un-indented.
 Example Reducing the nesting of the happy path inside a loop, using a continue statement:

 Bad

for (condition1)
    if (condition2)
        statement A
        statement B
        statement C
        statement D
statement E
  

 Good

for (condition1)
    if (not condition2)
        continue
    statement A
    statement B
    statement C
    statement D
statement E

Unsafe Practices
W5.4m 
Implementation → Code Quality → Error-Prone Practices → Introduction
It is safer to use language constructs in the way they are meant to be used, even if the language allows shortcuts. Such coding practices are common sources of bugs. Know them and avoid them.

W5.4n 
Implementation → Code Quality → Error-Prone Practices → Basic → Use the default branch
Always include a default branch in case statements. This ensures that all possible outcomes have been considered at the branching point.

Furthermore, use the default branch for the intended default action and not just to execute the last option. If there is no default action, you can use the default branch to detect errors (i.e., if execution reached the default branch, raise a suitable error). This also applies to the final else of an if-else construct. That is, the final else should mean 'everything else', and not the final option. Do not use else when an if condition can be explicitly specified, unless there is absolutely no other possibility.

 Example A final else used as the last option, and used for 'everything else':

 Bad

if (red) print "red";
else print "blue";
  

 Good

if (red) print "red";
else if (blue) print "blue";
else error("incorrect input");
W5.4o 
Implementation → Code Quality → Error-Prone Practices → Basic → Don't recycle variables or parameters
Use one variable for one purpose. Do not reuse a variable for a purpose other than its intended one, just because the data type is the same.
Do not reuse formal parameters as local variables inside the method.
 Example Reusing a parameter as a local variable, and the alternative:

 Bad

double computeRectangleArea(double length, double width) {
    length = length * width;  // parameter reused as a variable
    return length;
}
 Good

double computeRectangleArea(double length, double width) {
    double area;
    area = length * width;
    return area;
}
W5.4p 
Implementation → Code Quality → Error-Prone Practices → Basic → Avoid empty catch blocks
Avoid empty catch statements, as they are a way to ignore errors silently (which is not a good thing). In cases when it is unavoidable, at least give a comment to explain why the catch block is left empty.

W5.4q 
Implementation → Code Quality → Error-Prone Practices → Basic → Delete dead code
Get rid of unused code the moment it becomes redundant. You might feel reluctant to delete code you have painstakingly written, even if you have no use for that code anymore ("I spent a lot of time writing that code; what if I need it again?"). Consider all code as baggage you have to carry. If you need that code again, simply recover it from the revision control tool you are using. Deleting code you wrote previously is a sign that you are improving.

W5.4r 
Implementation → Code Quality → Error-Prone Practices → Intermediate → Minimize scope of variables
Minimize global variables. Global variables may be the most convenient way to pass information around, but they create implicit links between code segments that use the global variable. Avoid them as much as possible.

Define variables in the least possible scope. For example, if the variable is used only within the if block of the conditional statement, it should be declared inside that if block.

The most powerful technique for minimizing the scope of a local variable is to declare it where it is first used. -- Effective Java, by Joshua Bloch


 Resources:
Refactoring: Reduce Scope of Variable
W5.4s 
Implementation → Code Quality → Error-Prone Practices → Intermediate → Minimize code duplication
Code duplication, especially when you copy-paste-modify code, often indicates a poor quality implementation. While it may not be possible to have zero duplication, always think twice before duplicating code; most often there is a better alternative.

This guideline is closely related to the DRY Principle.


Code Comments
 Video Q+
W5.4t 
Implementation → Code Quality → Comments → Introduction
Good code is its own best documentation. As you’re about to add a comment, ask yourself, ‘How can I improve the code so that this comment isn’t needed?’ Improve the code and then document it to make it even clearer. -- Steve McConnell, Author of Code Complete

Some think commenting heavily increases the 'code quality'. That is not so. Avoid writing comments to explain bad code. Improve the code to make it self-explanatory.

W5.4u 
Implementation → Code Quality → Comments → Basic → Do not repeat the obvious
Do not repeat in comments information that is already obvious from the code. If the code is self-explanatory, a comment may not be needed.

 Example Comments that merely restate the code:

 Bad

//increment x
x++;

//trim the input
trimInput();
W5.4v 
Implementation → Code Quality → Comments → Basic → Write to the reader
Write comments targeting other programmers reading the code. Do not write comments as if they are private notes to yourself. One type of comment that is almost always useful is the header comment that you write for a class or an operation to explain its purpose.

 Example A header comment written as a private note, and the same one written for the reader:

 Bad Reason: this comment will only make sense to the person who wrote it

// a quick trim function used to fix bug I detected overnight
void trimInput() {
    ....
}


 Good

/** Trims the input of leading and trailing spaces */
void trimInput() {
    ....
}
W5.4w 
Implementation → Code Quality → Comments → Intermediate → Explain WHAT and WHY, not HOW
Comments should explain the WHAT and WHY aspects of the code, rather than the HOW aspect.

 WHAT: The specification of what the code is supposed to do. The reader can compare such comments to the implementation to verify if the implementation is correct.

 Example This method is possibly buggy because the implementation does not seem to match the comment. In this case, the comment could help the reader to detect the bug.

/** Removes all spaces from the {@code input} */
void compact(String input) {
    input.trim();
}
 WHY: The rationale for the current implementation.

 Example Without this comment, the reader will not know the reason for calling this method.

// Remove spaces to comply with IE23.5 formatting rules
compact(input);
 HOW: The explanation for how the code works. This should already be apparent from the code, if the code is self-explanatory. Adding comments to explain the same thing is redundant.

 Example A comment explaining HOW, and the self-explanatory code that makes it unnecessary:

 Bad Reason: Comment explains how the code works.

// return true if both left end and right end are correct
//    or the size has not incremented
return (left && right) || (input.size() == size);
 Good Reason: The code is now self-explanatory -- the comment is no longer needed.

boolean isSameSize = (input.size() == size);
return (isLeftEndCorrect && isRightEndCorrect) || isSameSize;