package chris;

/** Interprets user commands and their task numbers. */
public class Parser {
    /** Returns the type of the supplied command. */
    public CommandType parseCommandType(String input) {
        return CommandType.from(input);
    }

    /** Converts a user-facing task number into a valid zero-based index. */
    public int parseTaskIndex(String input, String command, int taskCount) throws ChrisException {
        String numberText = input.substring(command.length()).trim();
        if (numberText.isEmpty()) {
            throw new ChrisException("Please provide a task number after '" + command + "'.");
        }
        try {
            int taskNumber = Integer.parseInt(numberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new ChrisException("Task " + taskNumber + " is not in the list.");
            }
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new ChrisException("The task number must be a whole number.");
        }
    }
}
