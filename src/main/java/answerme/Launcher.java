package answerme;

import answerme.ui.gui.Main;
import javafx.application.Application;

/**
 * Provides the entry point for the AnswerMe graphical user interface.
 */
public class Launcher {
    /**
     * Starts the AnswerMe graphical user interface.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
