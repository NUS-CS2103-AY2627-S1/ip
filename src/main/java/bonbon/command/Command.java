package bonbon.command;

public enum Command {
    LIST("list","list", "Shows all tasks"),
    MARK("mark","mark <index>", "Marks index as done"),
    UNMARK("unmark","unmark <index>", "Marks index as not done"),
    TODO("todo","todo <name>", "Creates to-do with name"),
    DEADLINE("deadline","deadline <name> /by <due_date>",
            "Creates deadline with name and due date"),
    EVENT("event","event <name> /from <start> /to <end> /at <place>",
            "Creates event with name, start and end date, and place"),
    FIND("find","find <substring>", "Returns all the tasks with given description"),
    DELETE("delete","delete <index>", "Deletes task at given index"),
    BYE("bye","bye", "Exits chatbot");

    private final String keyword;
    private final String syntax;
    private final String description;

    Command(String keyword ,String syntax, String description) {
        this.keyword = keyword;
        this.syntax = syntax;
        this.description = description;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getSyntax() {
        return syntax;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return syntax + " -> " + description;
    }


    public static String toStringCommands() {
        StringBuilder finalStr = new StringBuilder();
        for (Command command : Command.values()) {
            finalStr.append(command.toString()).append("\n");
        }
        return finalStr.toString();
    }
}
