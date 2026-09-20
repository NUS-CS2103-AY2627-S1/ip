package recordbase.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import recordbase.Record;
import recordbase.exceptions.RecordException;
import recordbase.types.ListItem;

/**
 * Controller for the main GUI.
 */
public class MainWindowController extends AnchorPane {
    private static final int MAX_TRANSCRIPT_NODES = 80;
    private static final int MAX_COMMAND_HISTORY = 100;
    private static final double NEAR_BOTTOM_THRESHOLD = 0.98;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private ChoiceBox<String> avatarChoice;

    private final ArrayList<String> commandHistory = new ArrayList<>();
    private int commandHistoryIndex = commandHistory.size();
    private VBox taskListPane;
    private String taskListSnapshot;

    private Image userImage;
    private final Image recordProfileImage = loadImage("/images/RecordAvatar.png");

    /**
     * Creates the controller instantiated by the main FXML document.
     */
    public MainWindowController() { }

    /**
     * Initializes the controller for the main ui of Record Application.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "Scroll pane must be injected by FXML";
        assert dialogContainer != null : "Dialog container must be injected by FXML";
        assert userInput != null : "User input field must be injected by FXML";
        assert sendButton != null : "Send button must be injected by FXML";
        assert avatarChoice != null : "Avatar choice must be injected by FXML";

        avatarChoice.getItems().addAll("Friendly", "Classic");
        avatarChoice.getSelectionModel().selectFirst();
        updateUserImage();

        // Open old list records.
        Record.retrieveList("data/listdata.txt");

        addTranscriptNodes(true, DialogBoxController.getRecordDialog(Record.greet(), recordProfileImage));

    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Record's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleSendUserInput() {
        assert userInput != null : "User input field must be injected by FXML";
        assert dialogContainer != null : "Dialog container must be injected by FXML";

        String input = userInput.getText();

        assert input != null : "User input text must not be null";

        if (input.isEmpty()) {
            return;
        }

        rememberCommand(input);
        if (input.trim().equalsIgnoreCase("clear")) {
            clearTranscript();
            return;
        }

        String response;
        boolean isError = false;
        try {
            response = Record.parseInput(input);
        } catch (RecordException e) {
            response = e.getMessage();
            isError = true;
        }

        if (response == null) {
            addTranscriptNodes(true, DialogBoxController.getRecordDialog(Record.goodbye(), recordProfileImage));
            Record.saveList("data/listdata.txt");
            Platform.exit();
            return;
        }

        if (!isError) {
            refreshTaskListIfChanged();
        }
        boolean shouldFollowOutput = isNearBottom();
        addTranscriptNodes(false, DialogBoxController.getUserDialog(input, userImage));
        if (!isError && input.trim().equalsIgnoreCase("list")) {
            showTaskList(true);
        } else if (!isError && input.trim().equalsIgnoreCase("help")) {
            addTranscriptNodes(shouldFollowOutput,
                    DialogBoxController.getHelpDialog(recordProfileImage));
        } else {
            addTranscriptNodes(shouldFollowOutput, isError
                    ? DialogBoxController.getErrorDialog(response, recordProfileImage)
                    : DialogBoxController.getRecordDialog(response, recordProfileImage));
        }
        userInput.clear();
        userInput.requestFocus();
    }

    /**
     * Handler to list all items in the current list.
     */
    @FXML
    private void handleButtonListItems() {
        assert dialogContainer != null : "Dialog container must be injected by FXML";

        showTaskList(true);
    }

    /**
     * Shows the task panel and reuses its controls while task data remains unchanged.
     *
     * @param shouldFollowOutput whether the conversation should scroll to the panel
     */
    private void showTaskList(boolean shouldFollowOutput) {
        String currentSnapshot = Record.getItems().toString();
        if (taskListPane != null && dialogContainer.getChildren().contains(taskListPane)
                && currentSnapshot.equals(taskListSnapshot)) {
            // Move the cached panel to the newest position without rebuilding all its controls.
            dialogContainer.getChildren().remove(taskListPane);
            addTranscriptNodes(shouldFollowOutput, taskListPane);
            return;
        }
        removeTaskListPane();
        taskListPane = createTaskListPane();
        taskListSnapshot = currentSnapshot;
        addTranscriptNodes(shouldFollowOutput, taskListPane);
    }

    /**
     * Rebuilds an open task panel in the same transcript position when a text command changes
     * task state. This keeps checkbox state and row indexes synchronized with the saved model.
     */
    private void refreshTaskListIfChanged() {
        if (taskListPane == null || !dialogContainer.getChildren().contains(taskListPane)) {
            return;
        }
        String currentSnapshot = Record.getItems().toString();
        if (currentSnapshot.equals(taskListSnapshot)) {
            return;
        }
        int panelIndex = dialogContainer.getChildren().indexOf(taskListPane);
        VBox refreshedTaskList = createTaskListPane();
        dialogContainer.getChildren().set(panelIndex, refreshedTaskList);
        taskListPane = refreshedTaskList;
        taskListSnapshot = currentSnapshot;
    }

    /**
     * Creates an interactive representation of the current task list.
     *
     * <p>Each task receives a checkbox that immediately updates the corresponding model item.
     * An explanatory empty state is returned when the list contains no tasks.</p>
     *
     * @return newly constructed task-list pane
     */
    private VBox createTaskListPane() {
        VBox taskList = new VBox(6);
        taskList.getStyleClass().add("task-list");
        taskList.setPadding(new Insets(12));
        Label heading = new Label("On your record");
        heading.getStyleClass().add("task-list-title");
        taskList.getChildren().add(heading);

        List<ListItem> items = Record.getItems();
        if (items.isEmpty()) {
            Label emptyState = new Label("The record is quiet. Add a task when you're ready.");
            emptyState.getStyleClass().add("empty-state");
            taskList.getChildren().add(emptyState);
            return taskList;
        }
        for (int index = 0; index < items.size(); index++) {
            ListItem item = items.get(index);
            CheckBox taskRow = new CheckBox((index + 1) + ". " + item);
            taskRow.setSelected(item.isDone());
            taskRow.setWrapText(true);
            taskRow.setMaxWidth(Double.MAX_VALUE);
            taskRow.getStyleClass().add("task-row");
            int itemIndex = index;
            taskRow.setOnAction(event -> {
                Record.setItemCompletion(itemIndex, taskRow.isSelected());
                taskRow.setText((itemIndex + 1) + ". " + Record.getItems().get(itemIndex));
                taskListSnapshot = Record.getItems().toString();
                taskRow.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("completed"),
                        taskRow.isSelected());
            });
            taskRow.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("completed"),
                    taskRow.isSelected());
            taskList.getChildren().add(taskRow);
        }
        return taskList;
    }

    /**
     * Stores a command for keyboard history navigation.
     *
     * <p>Consecutive duplicates are ignored and the oldest entry is discarded when the
     * configured history limit is exceeded.</p>
     *
     * @param input command to retain
     */
    private void rememberCommand(String input) {
        if (commandHistory.isEmpty()
                || !input.equalsIgnoreCase(commandHistory.get(commandHistory.size() - 1))) {
            commandHistory.add(input);
            if (commandHistory.size() > MAX_COMMAND_HISTORY) {
                commandHistory.remove(0);
            }
        }
        commandHistoryIndex = commandHistory.size();
    }

    /**
     * Clears rendered conversation content without changing tasks or command history.
     */
    private void clearTranscript() {
        dialogContainer.getChildren().clear();
        taskListPane = null;
        taskListSnapshot = null;
        userInput.clear();
        userInput.requestFocus();
    }

    /**
     * Removes the currently rendered task panel and invalidates its cached snapshot.
     */
    private void removeTaskListPane() {
        if (taskListPane != null) {
            dialogContainer.getChildren().remove(taskListPane);
            taskListPane = null;
            taskListSnapshot = null;
        }
    }

    /**
     * Adds nodes to the transcript while enforcing its configured display limit.
     *
     * @param shouldFollowOutput whether to scroll to the newest output after layout
     * @param nodes nodes to append in display order
     */
    private void addTranscriptNodes(boolean shouldFollowOutput, javafx.scene.Node... nodes) {
        dialogContainer.getChildren().addAll(nodes);
        while (dialogContainer.getChildren().size() > MAX_TRANSCRIPT_NODES) {
            javafx.scene.Node removed = dialogContainer.getChildren().remove(0);
            if (removed == taskListPane) {
                taskListPane = null;
                taskListSnapshot = null;
            }
        }
        if (shouldFollowOutput) {
            scrollToBottomAfterLayout();
        }
    }

    /**
     * Waits for both the content and ScrollPane layout passes before following the newest output.
     * A second deferred pass is needed because the first pass may only update the viewport extent.
     */
    private void scrollToBottomAfterLayout() {
        Platform.runLater(() -> {
            dialogContainer.applyCss();
            dialogContainer.layout();
            scrollPane.applyCss();
            scrollPane.layout();
            Platform.runLater(() -> scrollPane.setVvalue(scrollPane.getVmax()));
        });
    }

    /**
     * Reports whether the viewport is already following the newest output.
     *
     * @return {@code true} if all content fits or the viewport is near the bottom
     */
    private boolean isNearBottom() {
        boolean contentFitsWithoutScrolling = dialogContainer.getHeight()
                <= scrollPane.getViewportBounds().getHeight() + 1;
        return contentFitsWithoutScrolling || scrollPane.getVvalue() >= NEAR_BOTTOM_THRESHOLD;
    }

    /**
     * Handler for Up and Down keypress in user input textbox.
     * Allows retrieval of past user inputs.
     */
    @FXML
    private void handleUpDownKeyPress(KeyEvent event) {
        assert event != null : "Key event must not be null";

        switch (event.getCode()) {
            case KeyCode.UP -> {
                showPreviousUserInput();
                event.consume();
            }
            case KeyCode.DOWN -> {
                showNextUserInput();
                event.consume();
            }
            default -> { }
        }
    }

    /**
     * Retrieves and displays the user's previous input.
     */
    @FXML
    private void showPreviousUserInput() {
        assert commandHistoryIndex >= 0 && commandHistoryIndex <= commandHistory.size()
                : "Past message index must remain within valid bounds";

        if (commandHistoryIndex > 0) {
            commandHistoryIndex--;
            userInput.setText(commandHistory.get(commandHistoryIndex));
            userInput.positionCaret(userInput.getLength());
        }
    }

    /**
     * Retrieves and displays the user's next input.
     */
    @FXML
    private void showNextUserInput() {
        assert commandHistoryIndex >= 0 && commandHistoryIndex <= commandHistory.size()
                : "Past message index must remain within valid bounds";

        if (commandHistoryIndex < commandHistory.size() - 1) {
            commandHistoryIndex++;
            userInput.setText(commandHistory.get(commandHistoryIndex));
            userInput.positionCaret(userInput.getLength());
        } else if (commandHistoryIndex == commandHistory.size() - 1) {
            commandHistoryIndex++;
            userInput.clear();
        }
    }

    /**
     * Applies the selected avatar to subsequent user messages and restores input focus.
     */
    @FXML
    private void handleAvatarChoice() {
        updateUserImage();
        userInput.requestFocus();
    }

    /**
     * Loads the selected built-in avatar at its original resolution.
     *
     * <p>Retaining the source resolution allows JavaFX to scale the image smoothly on displays
     * with different pixel densities.</p>
     */
    private void updateUserImage() {
        String path = avatarChoice.getSelectionModel().getSelectedIndex() == 1
                ? "/images/SmallLogo.png"
                : "/images/UserAvatar.png";
        userImage = loadImage(path);
    }

    /**
     * Loads an image bundled with the application.
     *
     * @param path absolute classpath resource path
     * @return loaded image
     */
    private static Image loadImage(String path) {
        return new Image(MainWindowController.class.getResourceAsStream(path));
    }
}
