package caitlyn;

/**
 * Pairs each command's syntax with an editable example for the command menu.
 */
enum CommandExample {
    TODO("todo <description>", "todo read book"),
    DEADLINE("deadline <description> /by <date>", "deadline return book /by 2027-01-25"),
    EVENT("event <description> /from <start> /to <end>",
            "event meeting /from 2027-01-15 1400 /to 2027-01-15 1600"),
    WITHIN("within <description> /from <start> /to <end>",
            "within collect certificate /from 2027-01-15 /to 2027-01-25"),
    LIST("list", "list"),
    FIND("find <keyword>", "find book"),
    MARK("mark <task number>", "mark 1"),
    UNMARK("unmark <task number>", "unmark 1"),
    DELETE("delete <task number>", "delete 1"),
    BYE("bye", "bye");

    private final String template;
    private final String example;

    /** Associates a syntax template with a complete, valid command example. */
    CommandExample(String template, String example) {
        this.template = template;
        this.example = example;
    }

    String getTemplate() {
        return template;
    }

    String getExample() {
        return example;
    }

    /** Returns the first argument's start, or the end for commands without arguments. */
    int getSelectionStart() {
        int firstSpace = example.indexOf(' ');
        return firstSpace < 0 ? example.length() : firstSpace + 1;
    }

    /** Returns the end of the first argument, preserving any date clauses when typing. */
    int getSelectionEnd() {
        int dateClause = example.indexOf(" /");
        return dateClause < 0 ? example.length() : dateClause;
    }
}
