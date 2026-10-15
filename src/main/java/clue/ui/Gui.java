package clue.ui;

import java.io.CharArrayWriter;
import java.io.IOException;

import clue.Clue;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Create a GUI for our program.
 * Initialize and interface with the Ui class to cause our program input and output to come from the GUI.
 */
public class Gui extends Application {
    private class Output extends CharArrayWriter {
        /**
         * Flushes the output buffer, outputting the entire buffer as a single message in the GUI.
         */
        @Override
        public void flush() {
            writeResponse(toString());
            reset();
        }
    }

    private final Ui ui;

    @FXML
    private TextField entryField;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox messages;

    /**
     * Default constructor used by JavaFX.
     * Uses global state to get around the no parameter limitation.
     * <p>
     * Specifically, initializes Ui object with global state and my writer.
     * Does not provide an input stream, we will call {@link Ui#receive} on any input.
     */
    public Gui() {
        ui = new Ui(Clue.getParser(), null, new Output());
    }

    /**
     * Initializes JavaFX components, then shows our preamble.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(messages.heightProperty());
        ui.printPreamble();
    }

    /**
     * Creates our main window and sets it on the given stage, starting the JavaFX visuals.
     *
     * @param stage the primary stage for this application.
     *
     * @throws IOException May throw IOException if the main window's FXML cannot be found, or other loading error.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/main_window.fxml"));
        AnchorPane ap = fxmlLoader.load();
        Scene scene = new Scene(ap);
        stage.setScene(scene);
        stage.setTitle("Clue");
        stage.show();
    }

    /**
     * Writes a response from our chatbot to our GUI in an unbroken block.
     *
     * @param out message to be written to the screen.
     */
    public void writeResponse(String out) {
        messages.getChildren().add(new Message(out, false));
    }

    /**
     * Handler for the user interfacing with the entry text field.
     * Updates the GUI and passes the user's input to the Ui object for our program to handle and respond.
     */
    public void onInput() {
        String txt = entryField.getText();
        // add user message block
        messages.getChildren().add(new Message(txt, true));

        if (!ui.receive(txt)) {
            // exit
            Platform.exit();
            return;
        }

        entryField.clear();
    }
}
