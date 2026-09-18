package sozius;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import sozius.exception.SoziusException;
import sozius.parser.Parser;
import sozius.storage.Storage;
import sozius.task.Task;
import sozius.tasklist.TaskList;
import sozius.ui.Gui;

/**
 * Sozius class. The main class of the application where main is run.
 */
public class Sozius {
    private final TaskList tasks;
    private final Storage storage;
    private final Parser parser;
    private final String loadWarning;

    /**
     * Initializes the chatbot with the tasklist from tasks.txt
     */
    public Sozius() {
        storage = new Storage("./tasks.txt");
        List<Task> loaded;
        String warning = null;
        try {
            loaded = storage.load();
        } catch (SoziusException e) {
            loaded = new ArrayList<>();
            warning = e.getMessage();
        }
        if (storage.getSkippedLineCount() > 0) {
            String skipMessage = "Skipped " + storage.getSkippedLineCount()
                    + " corrupted or duplicated line(s) in tasks.txt";
            warning = warning == null ? skipMessage : warning + "\n" + skipMessage;
        }
        loadWarning = warning;
        tasks = new TaskList(loaded);
        parser = new Parser(tasks);
    }

    /**
     * Returns a description of any problem encountered while loading tasks,
     * or null if everything loaded fine.
     * @return the load warning, or null
     */
    public String getLoadWarning() {
        return loadWarning;
    }

    /**
     * Main function
     */
    public static void main(String[] args) {
        Application.launch(Gui.class, args);
    }

    public String getResponse(String command) throws SoziusException {
        return parser.parse(command);
    }

    public void saveTasks() throws SoziusException {
        storage.save(tasks);
    }
}
