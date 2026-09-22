package nico;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates Nico's task list, storage, and command execution.
 */
public class Nico {
    public static final Path FILE_PATH = Paths.get("data", "tasks.txt");
    public static final String DATE_TIME_INPUT_FORMAT = "dd-MM-yyyy HHmm";

    private final List<Task> tasks = new ArrayList<>();

    /**
     * Loads saved tasks into this chatbot instance.
     *
     * @throws NicoException if the saved task file cannot be read or parsed
     */
    public void loadTasks() throws NicoException {
        List<Task> loadedTasks = TaskStorage.readTasks(FILE_PATH);
        tasks.clear();
        tasks.addAll(loadedTasks);
        assert tasks.size() == loadedTasks.size() : "all successfully loaded tasks must be retained";
    }

    /**
     * Executes one user command against this chatbot's persistent task list.
     *
     * @param inputText command text entered by the user.
     * @param ui user interface used to display command output.
     * @return whether the command requests that the application exit
     * @throws NicoException if the command cannot be parsed or executed
     */
    public boolean processCommand(String inputText, Ui ui) throws NicoException {
        Command command = Parser.parseCommand(inputText);
        command.execute(tasks, ui);
        return command.isExit();
    }
}
