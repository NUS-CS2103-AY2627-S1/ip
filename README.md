# Apollo project template

A task manager chatbot, bestowed the personality of a Greek God.

## AI-usage
Used ChatGPT's Codex 5.6 Sol in the following locations

### Apollo.java
- Added date parsing and date-query commands.
- Integrated extracted classes TaskList, Storage, Parser and UI.

### Deadline.java
- Changed /by from String to LocalDateTime
- Added display and ISO persistence formatting

### Event.java
- Changed /from and /to from String to LocalDateTime
- Added display and ISO persistence formatting

### A-MoreOOP
- Extracted and abstracted function from Apollo.java into the respective classes
- DateParser.java, TaskList.java, Storage.java, Parser.java and UI.java

### A-JavaDoc
- Generated relevant and useful JavaDoc for public classes/methods
- JavaDoc was reviewed by me 

### A-CodingStandard
- Bulk of coding standard was fixed by AI, majority of it looks correct to me.

### Level-10
- Helped refactor Apollo to become UI-independent, as well as create CLIAppliation.java to allow users to access the CLI appliation, whilst the default `./gradlew run`  will run the JavaFX application

### A-Assertions
- Helped identify possible locations where assert statements would be useful.

### A-CodeQuality
- Identify and implement parts of code where code quality can be improved
- Removed redundant code violating DRY Principle in Ui
- Refactored magic strings used for responses and introduced ResponseType enum

### A-Streams
- Helped replace for-loops with cleaner stream operations in TaskList and Storage

### A-BetterGui
- Prompted codex with the design I had in mind, and updated the relevant files like MainWindow.fxml and css classes
- Ensured that the style changes did not impact the functionality of the code

### A-Personality
- Main idea of personality (Greek God Apollo) was already there, Codex helped to adjust some messages to align with the personality

### A-MoreTesting
- With the addition of past few increments, Codex helped to generate relevant and important test cases that were useful in catching potential bugs. (e.g. Caught bug on allowing blank descriptions)

>All AI-generated code was reviewed by me, Isaac Choong, understood and tested before commiting and pushing to the remote repository.