package nico;

import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Provides the JavaFX user interface and FXML controller for the Nico chatbot.
 */
public class Ui extends Application {
    private static final int MINIMUM_WINDOW_WIDTH = 320;
    private static final int MINIMUM_WINDOW_HEIGHT = 420;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Nico nico;
    private final StringBuilder responseBuilder;
    private Stage stage;

    /**
     * Creates the state used by the JavaFX application.
     */
    public Ui() {
        nico = new Nico();
        responseBuilder = new StringBuilder();
    }

    /**
     * Initializes event handlers after the FXML controls have been injected.
     */
    @FXML
    private void initialize() {
        sendButton.setOnAction(event -> handleUserInput());
        userInput.setOnAction(event -> handleUserInput());
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
    }

    /**
     * Loads and displays the FXML-based Nico application window.
     */
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Ui.fxml"));
        loader.setController(this);

        Parent root;
        try {
            root = loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Nico user interface.", exception);
        }

        stage.setTitle("Nico");
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setResizable(true);
        stage.setScene(new Scene(root));
        stage.show();

        try {
            nico.loadTasks();
        } catch (NicoException exception) {
            beginResponse();
            showMessage(exception.getMessage());
            displayErrorResponse();
        }
        addBotDialog("Eh hello! I'm Nico, your task kaki. What you need help with today?");
    }

    /**
     * Executes the current input and renders either its response or its error.
     */
    private void handleUserInput() {
        String userText = userInput.getText();
        dialogContainer.getChildren().add(DialogBox.getUserDialog(userText));
        userInput.clear();
        beginResponse();

        try {
            boolean shouldExit = nico.processCommand(userText, this);
            displayResponse();
            if (shouldExit) {
                stage.close();
            }
        } catch (NicoException exception) {
            showMessage(exception.getMessage());
            displayErrorResponse();
        } catch (RuntimeException exception) {
            System.err.println("Unexpected error while processing a command: " + exception.getMessage());
            exception.printStackTrace();
            showMessage("Aiyoh! Something went wrong with that command lah."
                    + System.lineSeparator() + "Try again can?");
            displayErrorResponse();
        }
    }

    /**
     * Starts collecting output for the next chatbot response.
     */
    private void beginResponse() {
        responseBuilder.setLength(0);
    }

    /**
     * Adds the collected chatbot response to the conversation.
     */
    private void displayResponse() {
        String response = responseBuilder.toString().stripTrailing();
        if (!response.isEmpty()) {
            addBotDialog(response);
        }
    }

    /**
     * Adds the collected error response to the conversation in an error-styled dialog.
     */
    private void displayErrorResponse() {
        String response = responseBuilder.toString().stripTrailing();
        if (!response.isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(response));
        }
    }

    /**
     * Adds a chatbot message to the conversation.
     */
    private void addBotDialog(String message) {
        dialogContainer.getChildren().add(DialogBox.getNicoDialog(message));
    }

    /**
     * Appends one line to the current chatbot response.
     */
    private void appendLine(String message) {
        if (responseBuilder.length() > 0) {
            responseBuilder.append(System.lineSeparator());
        }
        responseBuilder.append(message.stripLeading());
    }

    /**
     * Shows the farewell message before the application closes.
     */
    public void showGoodbye() {
        appendLine("Okay lah, see you again! Take care hor!");
    }

    /**
     * Shows an error or informational message from the chatbot.
     */
    public void showMessage(String message) {
        appendLine(message);
    }

    /**
     * Shows all tasks with their one-based list numbers.
     */
    public void showTaskList(List<Task> tasks) {
        appendLine(tasks.isEmpty() ? "Your list empty lah. Add a task to get started!"
                : "Here's your task list lah:");
        for (int i = 0; i < tasks.size(); i++) {
            appendLine(String.format("%d. %s", i + 1, tasks.get(i)));
        }
    }

    /**
     * Shows the confirmation after a task is added.
     */
    public void showTaskAdded(Task task, int taskCount) {
        appendLine("Can lah! I've added this task:");
        appendLine(task.toString());
        appendLine("Now you got " + taskCount + " task(s) on your list lah.");
    }

    /**
     * Shows the confirmation after a task is marked as done.
     */
    public void showTaskMarkedDone(Task task) {
        appendLine("Steady lah, this task is done already:");
        appendLine(task.toString());
    }

    /**
     * Shows the confirmation after a task is marked as not done.
     */
    public void showTaskMarkedNotDone(Task task) {
        appendLine("Okay lah, this task not done yet:");
        appendLine(task.toString());
    }

    /**
     * Shows the confirmation after a task is removed.
     */
    public void showTaskRemoved(Task task, int taskCount) {
        appendLine("Can, I've removed this task:");
        appendLine(task.toString());
        appendLine("Now you got " + taskCount + " task(s) on your list lah.");
    }

    /**
     * Shows one task or all tasks tied for the requested group.
     */
    public void showTaskGroup(String label, List<? extends Task> tasks) {
        if (tasks.isEmpty()) {
            appendLine(label + ": Don't have lah");
        } else if (tasks.size() == 1) {
            appendLine(label + ": " + tasks.getFirst());
        } else {
            appendLine(label + ":");
            for (Task task : tasks) {
                appendLine("- " + task);
            }
        }
    }
}
