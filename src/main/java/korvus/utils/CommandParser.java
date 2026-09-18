package korvus.utils;

/**
 * Parser to read a user's input and split it into a command type and arguments.
 */
public class CommandParser {

    /**
     * Returns an instance of CommandParser.
     */
    public CommandParser() {

    }

    /**
     * Returns a String array containing the command name followed by the args passed into it.
     *
     * @param input String of the initial input to be processed.
     * @return the command to run split with its argument.
     */
    public String[] parse(String input) {
        return switch (input) {
            case String s when s.matches("(good)?bye( -f)?") ->
                new String[] { "bye" , s.matches("-f") ? "force" : null };
            case String s when s.matches("help") -> new String[] { "help" , "" };
            case String s when s.matches("(task(s)?)|(list(s)?)") -> new String[] { "list" , "" };
            case String s when s.matches("find task .*") ->
                new String[] { "find", s.split("find task ", 2)[1] };

            // Add Task
            case String s when s.matches("add task .*") ->
                new String[]{ "add", s.split("add task ", 2)[1] };

            // Add Task subclasses
            case String s when s.matches("(add )?todo .*") ->
                new String[] { "add", "-t " + s.split("(add )?todo ", 2)[1] };
            case String s when s.matches("(add )?deadline .*") ->
                new String[] { "add", "-d " + s.split("(add )?deadline ", 2)[1] };
            case String s when s.matches("(add )?event .*") ->
                new String[] { "add", "-e " + s.split("(add )?event ", 2)[1] };

            // Task Operations (do, undo, delete, ...)
            case String s when s.matches("do(ne)? task .*") ->
                new String[] { "do", s.split("do(ne)? task ", 2)[1] };
            case String s when s.matches("undo(ne)? task .*") ->
                new String[] { "undo", s.split("undo(ne)? task ", 2)[1] };
            case String s when s.matches("del(ete)? task .*") ->
                new String[] { "del", s.split("del(ete)? task ", 2)[1] };
            case String s when s.matches("(undo|cancel) last") ->
                new String[] { "backtrack", "" };

            default -> new String[] { "echo", input.isEmpty() ? "Caw~" : input + "~" };
        };
    }
}
