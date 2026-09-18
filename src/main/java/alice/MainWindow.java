package alice;

import java.util.List;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI window (see {@code view/MainWindow.fxml}).
 *
 * <p>Wires the text field and send button to {@link Alice#getResponse}, and
 * renders the conversation as a scrolling column of {@link DialogBox}es
 * alongside a panel listing the current tasks.
 */
public class MainWindow {
    private static final Image ALICE_IMAGE = new Image(MainWindow.class.getResourceAsStream("/images/DaAlice.png"));

    /** Below this window width there is no room for two columns. */
    private static final double TASK_PANEL_MIN_WINDOW_WIDTH = 600.0;

    @FXML
    private HBox rootPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private VBox taskPanel;
    @FXML
    private VBox taskContainer;
    @FXML
    private Label taskCountLabel;

    private Alice alice;

    /**
     * Keeps the scroll pane pinned to the bottom as new dialog boxes are
     * added, and hides the task panel when the window is too narrow to
     * show two columns. Called automatically by the FXML loader after the
     * annotated fields above are injected.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        // Binding managed to visible means a hidden panel takes up no space
        // at all, rather than leaving a 250px gap where it used to be.
        taskPanel.managedProperty().bind(taskPanel.visibleProperty());
        taskPanel.visibleProperty().bind(
                rootPane.widthProperty().greaterThan(TASK_PANEL_MIN_WINDOW_WIDTH));
    }

    /**
     * Supplies the chatbot instance this window talks to, and shows its
     * greeting as the first message in the conversation.
     *
     * @param alice the chatbot instance backing this GUI.
     */
    public void setAlice(Alice alice) {
        this.alice = alice;
        dialogContainer.getChildren().add(DialogBox.getAliceDialog(alice.getGreeting(), ALICE_IMAGE));
        // A data file that failed to load has no console to complain to in
        // the GUI, so the warning is shown as the next message instead.
        alice.getStartupWarning().ifPresent(
                warning -> dialogContainer.getChildren().add(toDialogBox(warning)));
        refreshTaskPanel();
    }

    /**
     * Handles the user submitting a command, either by pressing Enter in
     * the text field or clicking the send button. Appends both the user's
     * input and Alice's reply as dialog boxes, refreshes the task panel,
     * then closes the window shortly after a "bye" command so the user can
     * read the goodbye message first.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        Response response = alice.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                toDialogBox(response)
        );
        userInput.clear();
        refreshTaskPanel();

        if (alice.isExit(input)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition closeDelay = new PauseTransition(Duration.seconds(1.5));
            closeDelay.setOnFinished(event -> Platform.exit());
            closeDelay.play();
        }
    }

    /**
     * Rebuilds the task panel from the current task list.
     *
     * <p>Redrawing after every command, rather than having the panel
     * observe the task list, keeps {@link TaskList} free of any dependency
     * on JavaFX - it is shared with the text UI, which has no business
     * pulling in a GUI toolkit.
     */
    private void refreshTaskPanel() {
        List<Task> tasks = alice.getTasks();
        taskCountLabel.setText(String.valueOf(tasks.size()));
        taskContainer.getChildren().clear();

        if (tasks.isEmpty()) {
            Label empty = new Label("Nothing here yet.");
            empty.getStyleClass().add("task-empty");
            taskContainer.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < tasks.size(); i++) {
            taskContainer.getChildren().add(createTaskRow(i + 1, tasks.get(i)));
        }
    }

    /**
     * Builds one row of the task panel.
     *
     * @param displayNumber the task's one-based position, matching the
     *        number the user would type in a command.
     * @param task the task to render.
     * @return the row to add to the panel.
     */
    private Node createTaskRow(int displayNumber, Task task) {
        Label number = new Label(String.valueOf(displayNumber));
        number.getStyleClass().add("task-index");

        Label description = new Label(task.getDescription());
        description.setWrapText(true);
        description.getStyleClass().add(task.isDone() ? "task-desc-done" : "task-desc");

        // Asking the task to describe itself keeps this method free of
        // instanceof checks against every task type.
        String schedule = task.getScheduleSummary();
        String metaText = schedule.isEmpty()
                ? task.getTypeName()
                : task.getTypeName() + " · " + schedule;
        Label meta = new Label(metaText);
        meta.setWrapText(true);
        meta.getStyleClass().add("task-meta");

        VBox details = new VBox(description, meta);
        HBox.setHgrow(details, Priority.ALWAYS);

        HBox row = new HBox(number, details);
        row.getStyleClass().add("task-row");
        return row;
    }

    /**
     * Renders a reply as a dialog box, styling failures differently from
     * ordinary replies.
     *
     * @param response the reply to render.
     * @return the dialog box to add to the conversation.
     */
    private static DialogBox toDialogBox(Response response) {
        return response.isError()
                ? DialogBox.getErrorDialog(response.getText(), ALICE_IMAGE)
                : DialogBox.getAliceDialog(response.getText(), ALICE_IMAGE);
    }
}
