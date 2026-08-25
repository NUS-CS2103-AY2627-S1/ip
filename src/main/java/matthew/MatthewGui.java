package matthew;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import matthew.command.Command;
import matthew.exception.MatthewException;
import matthew.parser.Parser;
import matthew.storage.Storage;
import matthew.task.TaskList;
import matthew.ui.GuiUi;

/** Displays Matthew's task management chatbot in a JavaFX window. */
public class MatthewGui extends Application {
    private static final String STORAGE_PATH = "data/matthew.txt";
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 500;

    private final Storage storage = new Storage(STORAGE_PATH);
    private final GuiUi ui = new GuiUi();
    private TaskList tasks;
    private TextArea transcript;
    private TextField commandInput;
    private Stage stage;

    /** Creates the JavaFX application. */
    public MatthewGui() {
    }

    /** Builds and displays Matthew's user interface. */
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        tasks = loadTasks();
        transcript = createTranscript();
        commandInput = new TextField();
        commandInput.setPromptText("Type a command, e.g. list or todo read book");

        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> processCommand());
        commandInput.setOnAction(event -> processCommand());

        HBox commandBar = new HBox(8, commandInput, sendButton);
        HBox.setHgrow(commandInput, Priority.ALWAYS);

        Label heading = new Label("Matthew");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setTop(new VBox(4, heading,
                new Label("Your friendly task management chatbot")));
        root.setCenter(transcript);
        root.setBottom(commandBar);
        BorderPane.setMargin(transcript, new Insets(16, 0, 16, 0));

        primaryStage.setTitle("Matthew");
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(
                getClass().getResource("/matthew.css").toExternalForm());
        primaryStage.setScene(scene);
        ui.showWelcome();
        primaryStage.show();
        appendOutput();
        commandInput.requestFocus();
    }

    /** Loads saved tasks, retaining the application with an empty list on failure. */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load());
        } catch (MatthewException e) {
            ui.showLoadingError();
            return new TaskList();
        }
    }

    /** Creates the read-only command transcript. */
    private TextArea createTranscript() {
        TextArea output = new TextArea();
        output.setEditable(false);
        output.setWrapText(true);
        output.setFocusTraversable(false);
        return output;
    }

    /** Parses and executes the command currently entered by the user. */
    private void processCommand() {
        String input = commandInput.getText().trim();

        if (input.isEmpty()) {
            return;
        }

        transcript.appendText("> " + input + "\n");
        commandInput.clear();

        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
            appendOutput();

            if (command.isExit()) {
                stage.close();
            }
        } catch (MatthewException e) {
            ui.showError(e.getMessage());
            appendOutput();
        }
    }

    /** Appends buffered chatbot responses to the transcript. */
    private void appendOutput() {
        String output = ui.consumeOutput();

        if (!output.isEmpty()) {
            transcript.appendText(output);
            transcript.positionCaret(transcript.getLength());
        }
    }
}
