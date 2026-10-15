package bern.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Provides a task box's FXML loading and completion indicator.
 */
public class TaskBox extends VBox {
    private static final Image markImage = new Image(DialogBox.class.getResourceAsStream("/images/mark.png"));
    private static final Image unmarkImage = new Image(DialogBox.class.getResourceAsStream("/images/unmark.png"));

    /**
     * Completion indicator injected from the concrete task box's FXML layout.
     */
    @FXML
    protected ImageView mark;

    /**
     * Creates an empty task box whose subclass loads its layout and completion indicator afterward.
     */
    public TaskBox() {
    }

    /**
     * Loads a task box's FXML layout using this instance as its root and controller.
     * Prints a stack trace if loading the FXML raises an {@link IOException}.
     *
     * @param resourcePath The absolute classpath location of the task box's layout.
     */
    protected void loadView(String resourcePath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource(resourcePath));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the loaded task box's completion indicator.
     *
     * @param isDone Whether the task is complete.
     */
    protected void setMark(boolean isDone) {
        mark.setImage(isDone ? markImage : unmarkImage);
    }
}
