package genie.ui;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import genie.logic.CommandProcessor;
import genie.util.DateFormats;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Represents the main entry point for the Genie chatbot.
 * Starts the JavaFX interface and delegates command processing.
 */
public class Genie extends Application {
    /** Path of the file used to persist Genie tasks. */
    public static final String FILE_PATH = "./data/genie.txt";
    /** Format used to parse task dates and times. */
    public static final DateTimeFormatter INPUT_FORMAT = DateFormats.INPUT_FORMAT;
    /** Format used to display task dates and times. */
    public static final DateTimeFormatter OUTPUT_FORMAT = DateFormats.OUTPUT_FORMAT;
    private final CommandProcessor commandProcessor = new CommandProcessor(FILE_PATH);

    /** Starts the JavaFX desktop application. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Genie.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            fxmlLoader.<MainWindow>getController().setGenie(this);

            Scene scene = new Scene(root);
            stage.setTitle("Genie");
            stage.setMinHeight(450);
            stage.setMinWidth(420);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the Genie GUI", e);
        }
    }

    /** Processes one command through the shared command processor. */
    String processCommand(String userInput) {
        return commandProcessor.processCommand(userInput);
    }
}
