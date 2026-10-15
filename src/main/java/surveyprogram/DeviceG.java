package surveyprogram;

import surveyprogram.object.TaskList;
import surveyprogram.processor.CommandLoop;
import surveyprogram.processor.TaskCommandProcessor;
import surveyprogram.ui.Secret;

/**
 * Starts the application and owns its presentation-only introduction.
 */
public class DeviceG {
    private DeviceG() {
        // This class contains only application entry points.
    }

    /**
     * Displays the introduction before handing control to the command interface.
     *
     * @param args Unused command-line arguments.
     * @throws InterruptedException If the introductory pauses are interrupted.
     */
    public static void main(String[] args) throws InterruptedException {
        String banner = """
                ░▒▓███████▓▒░░▒▓████████▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓██████▓▒░░▒▓████████▓▒░▒▓██████▓▒░ \s
                ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░\s
                ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░       ░▒▓█▓▒▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░     ░▒▓█▓▒░       \s
                ░▒▓█▓▒░░▒▓█▓▒░▒▓██████▓▒░  ░▒▓█▓▒▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓██████▓▒░░▒▓█▓▒▒▓███▓▒░\s
                ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░        ░▒▓█▓▓█▓▒░ ░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░\s
                ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░        ░▒▓█▓▓█▓▒░ ░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░\s
                ░▒▓███████▓▒░░▒▓████████▓▒░  ░▒▓██▓▒░  ░▒▓█▓▒░░▒▓██████▓▒░░▒▓████████▓▒░▒▓██████▓▒░ \s
                """;
        System.out.println(banner.stripTrailing());
        Secret.run();
    }

    /** Starts the functional console interface after the introduction. */
    public static void boot() {
        new CommandLoop(System.in, createCommandProcessor()).run();
    }

    /**
     * Creates a command processor backed by the saved task list.
     *
     * @return initialized command processor
     */
    public static TaskCommandProcessor createCommandProcessor() {
        TaskList taskList = new TaskList(100);
        taskList.load();
        return new TaskCommandProcessor(taskList);
    }
}
