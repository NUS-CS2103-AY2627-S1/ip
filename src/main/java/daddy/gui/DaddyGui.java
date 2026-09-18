package daddy.gui;

import java.util.List;
import java.util.Objects;

import daddy.Daddy;
import daddy.task.Task;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Provides the JavaFX chat window used to interact with Daddy.
 */
public class DaddyGui extends Application {
    /** Sets the initial width of Daddy's chat window. */
    private static final double WINDOW_WIDTH = 600.0;
    /** Sets the initial height of Daddy's chat window. */
    private static final double WINDOW_HEIGHT = 680.0;
    /** Allows the conversation to remain usable when the window is made narrow. */
    private static final double MIN_WINDOW_WIDTH = 420.0;
    /** Preserves room for the conversation and command field at compact window sizes. */
    private static final double MIN_WINDOW_HEIGHT = 480.0;
    /** Provides Daddy's decorative banner in a shape that fits within the graphical header. */
    private static final String ASCII_ART = " ____              _       _               \n"
            + "|  _ \\   __ _   __| |   __| |  _   _ \n"
            + "| | | | / _` | / _` |  / _` | | | | |\n"
            + "| | | || (_| || (_| | | (_| | | |_| |\n"
            + "| |_| | \\__,_| \\__,_|  \\__,_|  \\__, |\n"
            + "|____/                          |___/ ";
    /** Processes commands and owns the task list for this graphical session. */
    private final Daddy daddy;
    /** Holds the conversation's ordered user and Daddy messages. */
    private VBox dialogContainer;
    /** Lets the user scroll through messages that no longer fit in the window. */
    private ScrollPane scrollPane;
    /** Accepts the next command entered by the user. */
    private TextField userInput;
    /** Submits the command currently shown in the user input field. */
    private Button sendButton;

    /**
     * Creates Daddy's JavaFX application and its command engine.
     */
    public DaddyGui() {
        this.daddy = new Daddy();
    }

    /**
     * Creates and shows Daddy's graphical chat window.
     *
     * @param stage the primary JavaFX window
     */
    @Override
    public void start(Stage stage) {
        dialogContainer = new VBox(12.0);
        dialogContainer.setFillWidth(true);
        dialogContainer.getStyleClass().add("dialog-container");

        scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("conversation-scroll-pane");

        userInput = new TextField();
        userInput.setPromptText("Type a command, e.g. todo borrow book");
        userInput.getStyleClass().add("command-input");

        sendButton = new Button("Send");
        sendButton.getStyleClass().add("send-button");

        HBox inputBar = new HBox(10.0, userInput, sendButton);
        inputBar.getStyleClass().add("input-bar");
        HBox.setHgrow(userInput, Priority.ALWAYS);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(createHeader());
        mainLayout.setCenter(scrollPane);
        mainLayout.setBottom(inputBar);

        Scene scene = new Scene(mainLayout, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/styles/daddy.css")).toExternalForm());

        configureInputHandlers();
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));
        addDaddyDialog(daddy.getWelcomeMessage());

        stage.setTitle("Daddy");
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates the heading displayed above Daddy's conversation.
     *
     * @return the styled heading area
     */
    private HBox createHeader() {
        Label asciiArt = new Label(ASCII_ART);
        asciiArt.getStyleClass().add("header-ascii-art");

        Label title = new Label("Task wrangler");
        title.getStyleClass().add("header-title");
        Label subtitle = new Label("Commands in. Chaos out.");
        subtitle.getStyleClass().add("subtitle");
        VBox headerText = new VBox(2.0, title, subtitle);

        HBox header = new HBox(18.0, asciiArt, headerText);
        header.getStyleClass().add("header");
        return header;
    }

    /**
     * Connects the Send button and Enter key to command submission.
     */
    private void configureInputHandlers() {
        sendButton.setOnAction(event -> handleUserInput());
        userInput.setOnAction(event -> handleUserInput());
    }

    /**
     * Adds the entered command and Daddy's response to the conversation.
     */
    private void handleUserInput() {
        String userText = userInput.getText().trim();
        if (userText.isEmpty()) {
            return;
        }

        addUserDialog(userText);
        String response = daddy.getResponse(userText);
        if (daddy.hasLastResponseError()) {
            addErrorDialog(response);
        } else if (isListCommand(userText)) {
            addTaskList(daddy.getTasks());
        } else {
            addDaddyDialog(response);
        }
        userInput.clear();

        if (daddy.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    /**
     * Adds one right-aligned dialog for a user-entered command.
     *
     * @param text the command entered by the user
     */
    private void addUserDialog(String text) {
        dialogContainer.getChildren().add(DialogBox.forUser(text));
    }

    /**
     * Adds one left-aligned dialog for Daddy's response.
     *
     * @param text the response generated by Daddy
     */
    private void addDaddyDialog(String text) {
        dialogContainer.getChildren().add(DialogBox.forDaddy(text));
    }

    /**
     * Adds one visually emphasized dialog for an invalid command or execution error.
     *
     * @param text the error response generated by Daddy
     */
    private void addErrorDialog(String text) {
        dialogContainer.getChildren().add(DialogBox.forError(text));
    }

    /**
     * Determines whether a command requests the complete task list.
     *
     * @param command the user-entered command text
     * @return whether the command is the plain {@code list} command
     */
    private boolean isListCommand(String command) {
        return command.equalsIgnoreCase("list");
    }

    /**
     * Adds a graphical panel that presents all current tasks as individual cards.
     *
     * @param tasks the tasks to present in the panel
     */
    private void addTaskList(List<Task> tasks) {
        dialogContainer.getChildren().add(new TaskListView(tasks));
    }
}
