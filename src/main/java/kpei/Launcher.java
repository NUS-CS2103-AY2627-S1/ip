package kpei;

import javafx.application.Application;

import kpei.datatypes.TaskList;
import kpei.storage.Storage;
import kpei.ui.Cli;
import kpei.ui.Gui;

/**
 * Entry point that initializes core resources and launches BERT in CLI or GUI mode.
 */
public class Launcher {

    private static final String DEFAULT_STORAGE_PATH = "data/todo_list.txt";

    /**
     * Main method deciding whether to launch the CLI or GUI version of BERT.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        if (isCli(args)) {
            Storage storage = new Storage(DEFAULT_STORAGE_PATH);
            TaskList taskList = new TaskList(storage.getFileName());
            Cli cli = new Cli(System.in, System.out);
            CommandCenter commandCenter = new CommandCenter(storage, taskList, cli);
            cli.runCliOnly(commandCenter);
        } else {
            Gui.initDependencies(DEFAULT_STORAGE_PATH);
            Application.launch(Gui.class, args);
        }
    }

    /**
     * Returns whether the arguments request CLI mode.
     *
     * @param args Command line arguments.
     * @return Whether CLI mode was requested.
     */
    private static boolean isCli(String[] args) {
        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg.trim())) {
                return true;
            }
        }
        return false;
    }
}
