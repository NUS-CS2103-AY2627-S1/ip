package tony;

import java.util.Objects;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Displays Tony's graphical chat interface.
 */
public class Main extends Application {
    /** Initial width of the application window. */
    private static final double WINDOW_WIDTH = 620;

    /** Initial height of the application window. */
    private static final double WINDOW_HEIGHT = 680;

    /** Width below which the interface uses smaller conversation gutters. */
    private static final double COMPACT_WIDTH = 500;

    /** Initial width of the command-reference dialog. */
    private static final double HELP_DIALOG_WIDTH = 720;

    /** Width needed to display the command-reference dialog's Close button without truncation. */
    private static final double HELP_CLOSE_BUTTON_WIDTH = 90;

    /** Contains the messages in conversation order. */
    private final VBox dialogContainer = new VBox();

    /** Scrolls through the conversation while keeping the composer visible. */
    private final ScrollPane conversationScroll = new ScrollPane(dialogContainer);

    /** Displays the current total number of tasks. */
    private final Label totalTaskCount = new Label("0");

    /** Displays how many tasks are currently complete. */
    private final Label completedTaskCount = new Label("0");

    /** Displays the secretary's workload-sensitive note to the chief. */
    private final Label overviewMessage = new Label();

    /** Accepts commands from the user. */
    private final TextField userInput = new TextField();

    /** Sends the current command to Tony. */
    private final Button sendButton = new Button("Send");

    /** Runs the list command without requiring the user to type it. */
    private final Button listButton = new Button("List");

    /** Ends the conversation without requiring the user to type the exit command. */
    private final Button byeButton = new Button("Bye");

    /** Opens a reference containing every supported command format. */
    private final Button helpButton = new Button("Help");

    /** Processes commands and manages persisted tasks. */
    private Tony tony;

    /**
     * Builds and displays the chatbot window.
     *
     * @param stage primary JavaFX window.
     */
    @Override
    public void start(Stage stage) {
        tony = new Tony();

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(new VBox(createHeader(), createTaskSummary()));
        root.setCenter(createConversation());
        root.setBottom(createComposer());

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        String stylesheet = Objects.requireNonNull(
                getClass().getResource("/css/main.css"), "Missing GUI stylesheet").toExternalForm();
        scene.getStylesheets().add(stylesheet);

        stage.setTitle("Tony");
        stage.setMinWidth(360);
        stage.setMinHeight(440);
        stage.setScene(scene);
        stage.show();

        showWelcomeMessage();
        Platform.runLater(userInput::requestFocus);
    }

    /** Creates a compact heading for the application. */
    private HBox createHeader() {
        Label title = new Label("Tony");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Your secretary is at your service, Chief.");
        subtitle.getStyleClass().add("app-subtitle");

        VBox identity = new VBox(0, title, subtitle);

        HBox header = new HBox(identity);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("app-header");
        return header;
    }

    /** Creates a compact overview of total and completed tasks above the conversation. */
    private VBox createTaskSummary() {
        VBox totalTasks = createSummaryItem("Total tasks", totalTaskCount);
        VBox completedTasks = createSummaryItem("Done", completedTaskCount);
        HBox.setHgrow(totalTasks, Priority.ALWAYS);
        HBox.setHgrow(completedTasks, Priority.ALWAYS);

        HBox taskSummary = new HBox(10, totalTasks, completedTasks);
        taskSummary.getStyleClass().add("task-summary");
        overviewMessage.setWrapText(true);
        overviewMessage.getStyleClass().add("overview-message");

        VBox overview = new VBox(7, taskSummary, overviewMessage);
        overview.getStyleClass().add("task-overview");
        updateTaskSummary();
        return overview;
    }

    /** Creates one equally sized statistic displayed in the task overview. */
    private static VBox createSummaryItem(String caption, Label valueLabel) {
        Label captionLabel = new Label(caption);
        captionLabel.getStyleClass().add("summary-caption");

        valueLabel.getStyleClass().add("summary-value");
        VBox item = new VBox(1, valueLabel, captionLabel);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setMaxWidth(Double.MAX_VALUE);
        item.getStyleClass().add("summary-item");
        return item;
    }

    /** Refreshes the task overview after Tony's task state may have changed. */
    private void updateTaskSummary() {
        int totalTasks = tony.getTaskCount();
        int completedTasks = tony.getCompletedTaskCount();
        totalTaskCount.setText(Integer.toString(totalTasks));
        completedTaskCount.setText(Integer.toString(completedTasks));
        overviewMessage.setText(tony.getOverviewMessage());
        totalTaskCount.setAccessibleText(totalTasks + " total tasks");
        completedTaskCount.setAccessibleText(completedTasks + " completed tasks");
        overviewMessage.setAccessibleText("Secretary's note: " + tony.getOverviewMessage());
    }

    /** Creates the scrollable conversation area. */
    private StackPane createConversation() {
        dialogContainer.setPadding(new Insets(16));
        dialogContainer.setSpacing(8);
        dialogContainer.getStyleClass().add("dialog-container");

        conversationScroll.setFitToWidth(true);
        conversationScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversationScroll.getStyleClass().add("conversation-scroll");
        conversationScroll.widthProperty().addListener((observable, oldWidth, newWidth) ->
                updateConversationPadding(newWidth.doubleValue()));
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(this::scrollToLatestResponse));

        Region officeBackground = new Region();
        officeBackground.setMouseTransparent(true);
        officeBackground.getStyleClass().add("conversation-background");

        Region backgroundWash = new Region();
        backgroundWash.setMouseTransparent(true);
        backgroundWash.getStyleClass().add("conversation-background-wash");

        return new StackPane(officeBackground, backgroundWash, conversationScroll);
    }

    /** Uses compact gutters when the window is narrow. */
    private void updateConversationPadding(double width) {
        double horizontalPadding = width < COMPACT_WIDTH ? 10 : 16;
        dialogContainer.setPadding(new Insets(14, horizontalPadding, 14, horizontalPadding));
    }

    /** Creates quick actions above the text field and send button. */
    private VBox createComposer() {
        configureQuickAction(listButton, "Show all tasks", () -> submitCommand("list", false));
        configureQuickAction(byeButton, "End the conversation", () -> submitCommand("bye", false));
        configureQuickAction(helpButton, "Show every command format", this::showCommandHelp);

        HBox quickActions = new HBox(7, listButton, byeButton, helpButton);
        quickActions.getStyleClass().add("quick-actions");

        userInput.setPromptText("Type a command...");
        userInput.setAccessibleText("Chatbot command");
        userInput.setOnAction(event -> handleUserInput());
        HBox.setHgrow(userInput, Priority.ALWAYS);

        sendButton.setDefaultButton(true);
        sendButton.setAccessibleText("Send command");
        sendButton.setTooltip(new Tooltip("Send command (Enter)"));
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                userInput.getText().isBlank(), userInput.textProperty()));
        sendButton.setOnAction(event -> handleUserInput());

        HBox composer = new HBox(10, userInput, sendButton);
        composer.getStyleClass().add("composer");

        VBox composerArea = new VBox(7, quickActions, composer);
        composerArea.getStyleClass().add("composer-area");
        return composerArea;
    }

    /** Configures a compact button that performs one common action. */
    private static void configureQuickAction(Button button, String accessibleText, Runnable action) {
        button.getStyleClass().add("quick-action-button");
        button.setAccessibleText(accessibleText);
        button.setTooltip(new Tooltip(accessibleText));
        button.setOnAction(event -> action.run());
    }

    /** Opens a concise reference showing every supported command and its required format. */
    private void showCommandHelp() {
        Dialog<Void> helpDialog = new Dialog<>();
        helpDialog.setTitle("Tony's command reference");
        helpDialog.setHeaderText("Your command reference, Chief");
        helpDialog.initOwner(helpButton.getScene().getWindow());
        helpDialog.setResizable(true);

        GridPane commands = new GridPane();
        commands.setHgap(14);
        commands.setVgap(8);
        commands.getStyleClass().add("command-help-grid");
        ColumnConstraints syntaxColumn = new ColumnConstraints();
        syntaxColumn.setMinWidth(Region.USE_PREF_SIZE);
        ColumnConstraints descriptionColumn = new ColumnConstraints();
        descriptionColumn.setMinWidth(Region.USE_PREF_SIZE);
        descriptionColumn.setHgrow(Priority.ALWAYS);
        commands.getColumnConstraints().addAll(syntaxColumn, descriptionColumn);
        addCommandHelpRow(commands, 0, "todo <description>", "Add a task without a date");
        addCommandHelpRow(commands, 1, "deadline <description> /by <yyyy-MM-dd>", "Add a deadline");
        addCommandHelpRow(commands, 2,
                "event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>", "Add an event");
        addCommandHelpRow(commands, 3, "list", "Show every task");
        addCommandHelpRow(commands, 4, "find <keyword>", "Find matching tasks");
        addCommandHelpRow(commands, 5, "mark <task number>", "Mark a task as done");
        addCommandHelpRow(commands, 6, "unmark <task number>", "Mark a task as not done");
        addCommandHelpRow(commands, 7, "delete <number> [number ...]", "Delete one or more tasks");
        addCommandHelpRow(commands, 8, "bye", "End the conversation");

        Label hint = new Label("Dates use the year-month-day format, for example 2026-09-20.");
        hint.setWrapText(true);
        hint.getStyleClass().add("command-help-hint");

        VBox content = new VBox(12, commands, hint);
        DialogPane dialogPane = helpDialog.getDialogPane();
        dialogPane.setContent(content);
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);
        dialogPane.setPrefWidth(HELP_DIALOG_WIDTH);
        dialogPane.setMinWidth(HELP_DIALOG_WIDTH);
        dialogPane.getStyleClass().add("command-help-dialog");
        dialogPane.getStylesheets().setAll(helpButton.getScene().getStylesheets());
        Button closeButton = (Button) dialogPane.lookupButton(ButtonType.CLOSE);
        closeButton.setMinWidth(HELP_CLOSE_BUTTON_WIDTH);
        closeButton.setPrefWidth(HELP_CLOSE_BUTTON_WIDTH);
        helpDialog.showAndWait();
    }

    /** Adds one formatted command and its purpose to the help reference. */
    private static void addCommandHelpRow(GridPane commands, int row, String syntax, String description) {
        Label syntaxLabel = new Label(syntax);
        syntaxLabel.getStyleClass().add("command-syntax");

        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("command-description");
        commands.addRow(row, syntaxLabel, descriptionLabel);
    }

    /** Displays the opening prompt and any storage warning. */
    private void showWelcomeMessage() {
        addDialog(DialogBox.getTonyDialog(
                "Good day, Chief. What shall I arrange for you?", Tony.ResponseType.NORMAL));
        if (!tony.getStartupMessage().isEmpty()) {
            addDialog(DialogBox.getTonyDialog(
                    tony.getStartupMessage(), Tony.ResponseType.WARNING));
        }
    }

    /** Sends the entered command to Tony and adds both sides of the exchange. */
    private void handleUserInput() {
        String command = userInput.getText();
        if (command.isBlank()) {
            return;
        }

        submitCommand(command, true);
    }

    /** Runs a typed or quick-action command and updates the conversation. */
    private void submitCommand(String command, boolean isTypedCommand) {
        Tony.CommandResult result = tony.getCommandResult(command);
        updateTaskSummary();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(command),
                DialogBox.getTonyDialog(result.message(), result.type()));

        if (isTypedCommand && result.type() == Tony.ResponseType.ERROR) {
            userInput.selectAll();
        } else if (isTypedCommand || Tony.isExitCommand(command)) {
            userInput.clear();
        }

        if (Tony.isExitCommand(command)) {
            userInput.setPromptText("Conversation ended");
            userInput.setDisable(true);
            sendButton.disableProperty().unbind();
            sendButton.setDisable(true);
            listButton.setDisable(true);
            byeButton.setDisable(true);
        } else {
            Platform.runLater(userInput::requestFocus);
        }
    }

    /** Adds one dialog to the conversation. */
    private void addDialog(DialogBox dialog) {
        dialogContainer.getChildren().add(dialog);
    }

    /** Scrolls to the newest response. */
    private void scrollToLatestResponse() {
        conversationScroll.setVvalue(conversationScroll.getVmax());
    }
}
