package nyonbot;

import nyonbot.command.Command;
import nyonbot.model.NyonException;
import nyonbot.model.Task;
import nyonbot.model.TaskList;

/**
 * Stores the logic and handling of the NyonBot program.
 */
public class Logic {

    /**
     * Stores the result of executing a command.
     *
     * @param out text to display to the user.
     * @param shouldExit whether the application should close.
     * @param shouldWrite whether the task list should be saved.
     * @param funValue control flag used mainly easter egg purposes.
     */
    public record Result(String out, boolean shouldExit, boolean shouldWrite, int funValue) {
        public Result(String out) {
            this(out, false, false, 0);
        }

        public Result(String out, boolean shouldExit) {
            this(out, shouldExit, false, 0);
        }

        public Result(String out, boolean shouldExit, boolean shouldWrite) {
            this(out, shouldExit, shouldWrite, 0);
        }
    }


    private static Logic instance = null;
    private TaskList list;

    private Logic() {
        this.list = new TaskList();
    }

    public static synchronized Logic getInstance() {
        if (instance == null) {
            instance = new Logic();
        }
        return instance;
    }

    public TaskList getList() {
        return list;
    }

    /**
     * Replaces the current task list with the input list.
     *
     * @param newList task list to load, or {@code null} to clear the current list.
     */
    public void loadList(TaskList newList) {
        list.clear();
        if (newList == null) {
            return;
        }

        for (Task task : newList) {
            list.add(task);
        }
    }

    /**
     * Executes the given command.
     *
     * @param command command to execute.
     * @return result of the command.
     * @throws NyonException if the command input is invalid.
     */
    public Result execute(Command command) throws NyonException {
        return command.execute();
    }
}
