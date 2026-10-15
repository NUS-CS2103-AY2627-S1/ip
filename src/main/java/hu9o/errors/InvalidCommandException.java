package hu9o.errors;

/** Indicates that the command name is not supported. */
public class InvalidCommandException extends Hu9oException {
    /** Creates the exception with the full list of task and contact commands Hu9o understands. */
    public InvalidCommandException() {
        super("Unknown command, woof! Here's what I understand -- "
                + "Tasks: todo, list, find, deadline, event, mark, unmark, delete, progress, bye. "
                + "Contacts: person, people, findperson, deleteperson, link, connections, select, deselect.");
    }
}
