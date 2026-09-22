package echo;

import java.util.ArrayList;

/**
 * Parses user input into commands and their associated arguments.
 */
public class Parser {

    /**
     * Splits an input line into arguments and converts the command to lowercase.
     *
     * @param input the user input to parse
     * @return the command and its arguments
     */
    public static String[] convertInputToArgs(String input) {
        String[] commandArgs = input.trim().split(" ");
        commandArgs[0] = commandArgs[0].toLowerCase();
        return commandArgs;
    }

    /**
     * Returns the task number from the most recently parsed command.
     *
     * @return the task number specified in the command
     * @throws InvalidCommandException if the command does not include a task number
     * @throws NumberFormatException if the task number is not a valid integer
     */
    public static int getTaskNum(int todoListSize, String[] commandArgs) throws InvalidCommandException {
        try {
            int taskNum = Integer.parseInt(commandArgs[1]);
            if (taskNum < 1 || taskNum > todoListSize) {
                throw new InvalidCommandException();
            }
            return taskNum;
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new InvalidCommandException();
        }
    }

    public static String getKeyword(String input, String command) throws InvalidCommandException {
        if (input.trim().equals(command)) {
            throw new InvalidCommandException();
        }
        String[] commandArgs = input.trim()
                .substring(command.length() + 1).split(" ");
        if (commandArgs.length >= 1) {
            return String.join(" ", commandArgs);
        } else {
            throw new InvalidCommandException();
        }
    }

    /**
     * Extracts the task arguments without any slash-prefixed details from a task-creating command.
     *
     * @param input the complete user input
     * @param command the command prefix to remove from the input
     * @return the description and optional details, split at {@code " /"}
     * @throws InvalidCommandException if the command has no task description
     */
    public static String[] getTaskArgs(String input, String command) throws InvalidCommandException {
        if (input.trim().equals(command)) {
            throw new InvalidCommandException();
        }
        String[] commandArgs = input.trim()
                .substring(command.length() + 1).split(" /");
        if (commandArgs.length >= 1) {
            return commandArgs;
        } else {
            throw new InvalidCommandException();
        }
    }

    /**
     * Extracts the task arguments without any double slash-prefixed details from the storage text file.
     *
     * @param input the task in storage format
     * @param command the command prefix to remove from the input
     * @return the description and optional details, split at {@code " // "}
     * @throws InvalidCommandException if the stored task is invalid
     */
    public static String[] getStoredTaskArgs(String input, String command) throws InvalidCommandException {
        if (input.trim().equals(command)) {
            throw new InvalidCommandException();
        }
        String[] commandArgs = input.trim()
                .substring(command.length() + 1).split(" /");
        if (commandArgs.length >= 2) {
            ArrayList<String> taskArgs= new ArrayList<String>();
            for (int i = 1; i < commandArgs.length; i++) {
                if (!commandArgs[i].equals("null")) {
                    taskArgs.add(commandArgs[i]);
                }
            }
            return taskArgs.toArray(String[]::new);
        } else {
            throw new InvalidCommandException();
        }
    }
}
