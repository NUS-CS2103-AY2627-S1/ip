package discitrack.gui;

import java.time.LocalDate;
import java.util.List;

import discitrack.DisciTrack;
import discitrack.task.Deadline;
import discitrack.task.Event;
import discitrack.task.Task;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Presents the coach's desk and routes buttons and typed input through the same commands.
 */
public class MainWindow {
    @FXML
    private BorderPane root;
    @FXML
    private VBox coachPanel;
    @FXML
    private ImageView coachImage;
    @FXML
    private Label coachMessage;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private VBox taskContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Button clearCommandButton;
    @FXML
    private TextField searchInput;

    private DisciTrack disciTrack;
    private String searchKeyword = "";
    private List<Task> visibleTasks;
    private String boardHeading = "Your tasks";
    private final ScaleTransition celebration = new ScaleTransition(Duration.millis(180));

    /**
     * Loads the saved task board and the initial coach response.
     *
     * @param disciTrack application used to execute commands.
     */
    public void setDisciTrack(DisciTrack disciTrack) {
        this.disciTrack = disciTrack;
        showResponse(disciTrack.hasLoadError() ? "Saved data needs attention" : "Coach is in!",
                disciTrack.getGreeting(), disciTrack.hasLoadError(), false);
        if (!disciTrack.hasLoadError() && disciTrack.getTasks().isEmpty()) {
            coachMessage.setText("Zero tasks. Maximum breathing room. Enjoy it, champ!");
            celebrate();
        }
        refreshTasks();
        Platform.runLater(userInput::requestFocus);
    }

    @FXML
    private void initialize() {
        sendButton.disableProperty().bind(userInput.textProperty().isEmpty());
        clearCommandButton.visibleProperty().bind(userInput.textProperty().isNotEmpty());
        clearCommandButton.managedProperty().bind(clearCommandButton.visibleProperty());
        root.widthProperty().addListener((observable, previous, width) -> resizeCoach(width.doubleValue()));
        root.heightProperty().addListener((observable, previous, height) ->
                coachImage.setFitHeight(Math.max(70, Math.min(330, height.doubleValue() - 450))));
    }

    private void resizeCoach(double width) {
        boolean isCompact = width < 780;
        coachPanel.setPrefWidth(isCompact ? 180 : 270);
        coachImage.setFitWidth(isCompact ? 156 : 246);
        // Preserve the original logo's aspect ratio at both window sizes.
        ImageView logo = (ImageView) coachPanel.getChildren().getFirst();
        logo.setFitWidth(isCompact ? 156 : 238);
        coachPanel.getChildren().get(1).setVisible(!isCompact);
        coachPanel.getChildren().get(1).setManaged(!isCompact);
        coachPanel.getChildren().get(3).setStyle(isCompact ? "-fx-font-size: 22px;" : "");
        coachPanel.getChildren().get(4).setVisible(!isCompact);
        coachPanel.getChildren().get(4).setManaged(!isCompact);
    }

    @FXML
    private void handleClearCommand() {
        userInput.clear();
        userInput.requestFocus();
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (!input.isEmpty()) {
            if (runCommand(input)) {
                userInput.clear();
            }
        }
    }

    /**
     * Executes one action and refreshes the board only from the application's task state.
     */
    private boolean runCommand(String input) {
        return runCommand(input, false);
    }

    private boolean runCommand(String input, boolean keepEditorOnError) {
        if (disciTrack == null || disciTrack.shouldExit()) {
            return false;
        }
        long completedBefore = disciTrack.getTasks().stream().filter(Task::isDone).count();
        String response = disciTrack.getResponse(input);
        boolean didComplete = disciTrack.getTasks().stream().filter(Task::isDone).count() > completedBefore;
        String command = input.split("\\s+", 2)[0];
        boolean hasError = disciTrack.hasResponseError();
        selectTaskView(command, input, hasError);
        if (hasError && !keepEditorOnError) {
            taskContainer.setVisible(false);
            taskContainer.setManaged(false);
            searchKeyword = "";
            searchInput.clear();
        }
        if (!hasError && command.equals("find")) {
            searchKeyword = input.substring(command.length()).trim();
            searchInput.setText(searchKeyword);
        } else if (!hasError) {
            searchKeyword = "";
            searchInput.clear();
        }
        celebration.stop();
        coachImage.setScaleX(1);
        coachImage.setScaleY(1);
        Label entry = label(input, "user-command");
        HBox commandRow = new HBox(entry);
        commandRow.setAlignment(Pos.CENTER_RIGHT);
        dialogContainer.getChildren().add(commandRow);
        String title = getHeading(command, hasError);
        coachMessage.setText(getEncouragement(command, hasError));
        if (!hasError && command.equals("mark") && !didComplete) {
            title = "Already completed";
            coachMessage.setText("That win is already counted, champ. Pick your next challenge!");
        }
        String body = response.replaceAll("(\\R){2,}", "\n");
        if (!hasError && command.equals("list")) {
            body = disciTrack.getTasks().isEmpty()
                    ? "All clear, champ! Nothing on the board. Enjoy the breather - coach approves."
                    : "Here's the lineup. Pick your next win and Let's gooo!";
        } else if (!hasError && command.equals("find")) {
            body = disciTrack.getMatchingTasks(searchKeyword).isEmpty()
                    ? "Nothing on the radar, champ. Try another keyword or hit Show all."
                    : "Scouting report is in! Here's what matches. Hit Show all for the full lineup.";
        } else if (!hasError && command.equals("help")) {
            body = "The playbook is open. Pick your move, champ!";
        }
        showResponse(title, body, hasError, didComplete);
        if (!hasError) {
            refreshTasks();
        }
        Platform.runLater(() -> scrollToCommand(commandRow));
        userInput.requestFocus();
        if (!hasError && didComplete) {
            celebrate();
        } else if (!hasError && disciTrack.getTasks().isEmpty()
                && (command.equals("list") || command.equals("delete"))) {
            celebrate();
        }
        if (!hasError && command.equals("help")) {
            showCommandGuide(response);
        }
        if (disciTrack.shouldExit()) {
            root.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
        return !hasError;
    }

    /**
     * Brings the latest exchange into view without discarding earlier conversation history.
     */
    private void scrollToCommand(HBox commandRow) {
        root.applyCss();
        root.layout();
        double scrollableHeight = scrollPane.getContent().getLayoutBounds().getHeight()
                - scrollPane.getViewportBounds().getHeight();
        double commandOffset = dialogContainer.getLayoutY() + commandRow.getLayoutY();
        scrollPane.setVvalue(scrollableHeight <= 0 ? 0 : Math.min(1, commandOffset / scrollableHeight));
    }

    /**
     * Limits the board to the current command's result while retaining original task numbers.
     */
    private void selectTaskView(String command, String input, boolean hasError) {
        if (hasError) {
            return;
        }
        visibleTasks = List.of();
        boardHeading = "Updated task";
        boolean isBoardVisible = !hasError;
        if (!hasError) {
            switch (command) {
                case "list":
                    visibleTasks = disciTrack.getTasks();
                    boardHeading = "Your tasks";
                    break;
                case "find":
                    String keyword = input.substring(command.length()).trim();
                    visibleTasks = disciTrack.getMatchingTasks(keyword);
                    boardHeading = "Search results: " + keyword;
                    break;
                case "checkdate":
                    LocalDate date = LocalDate.parse(input.substring(command.length()).trim());
                    visibleTasks = disciTrack.getTasksOnDate(date);
                    boardHeading = "Tasks on " + date;
                    break;
                case "todo":
                case "deadline":
                case "event":
                    visibleTasks = disciTrack.getTasks();
                    boardHeading = "Your tasks";
                    break;
                case "mark":
                case "unmark":
                case "tag":
                case "untag":
                    int number = Integer.parseInt(input.split("\\s+")[1]);
                    visibleTasks = List.of(disciTrack.getTasks().get(number - 1));
                    break;
                default:
                    isBoardVisible = command.equals("delete") && disciTrack.getTasks().isEmpty();
                    break;
            }
        }
        taskContainer.setVisible(isBoardVisible);
        taskContainer.setManaged(isBoardVisible);
    }

    /**
     * Briefly pulses the coach portrait to celebrate completion or an empty list.
     */
    private void celebrate() {
        celebration.setNode(coachImage);
        celebration.setFromX(1);
        celebration.setFromY(1);
        celebration.setToX(1.04);
        celebration.setToY(1.04);
        celebration.setCycleCount(2);
        celebration.setAutoReverse(true);
        celebration.playFromStart();
    }

    private String getHeading(String command, boolean hasError) {
        if (hasError) {
            return "UHOH!";
        }
        if (command.equals("list") && disciTrack.getTasks().isEmpty()) {
            return "All clear, champ!";
        }
        return switch (command) {
            case "mark" -> "Task crushed!";
            case "todo", "deadline", "event" -> "On the board!";
            case "list" -> "The game plan";
            case "find", "checkdate" -> "Scouting report";
            case "delete" -> "Lineup updated";
            case "unmark" -> "Back to training";
            case "bye" -> "Catch you next round!";
            default -> "Coach's update";
        };
    }

    private String getEncouragement(String command, boolean hasError) {
        if (hasError) {
            if (disciTrack.hasStorageError()) {
                return "Your data needs attention. Follow the file recovery advice in the error message, champ.";
            }
            return "Check the input and try again, champ. You've got this!";
        }
        if (disciTrack.getTasks().isEmpty() && !command.equals("bye")) {
            return "Zero tasks. Maximum breathing room. Enjoy it, champ!";
        }
        return switch (command) {
            case "mark" -> "Let's goooo! You did the thing! That's a big W. Coach is absolutely proud of you!";
            case "todo", "deadline", "event" -> "IT'S On the board! Now let's turn 'I'll do it' into 'I DID IT!'";
            case "list" -> "Game face on! One task at a time. We've got this!";
            case "unmark" -> "Back to training. We'll call that a practice rep.";
            case "bye" -> "Rest up, champ. We'll go again next time.";
            default -> "Locked in! One task, one win. Let's make it happen!";
        };
    }

    private void showResponse(String title, String body, boolean hasError, boolean isSuccess) {
        VBox card = new VBox(9, label(title, "card-title"), label(body, "card-body"));
        card.getStyleClass().add("response-card");
        if (hasError) {
            card.getStyleClass().add("error-card");
        } else if (isSuccess) {
            card.getStyleClass().add("success-card");
        }
        dialogContainer.getChildren().add(card);
    }

    /**
     * Rebuilds task rows with full-list numbers, including dates and tags in their descriptions.
     */
    private void refreshTasks() {
        taskContainer.getChildren().setAll(label(boardHeading, "card-title"));
        if (disciTrack.hasLoadError()) {
            taskContainer.getChildren().add(label("Saved tasks could not be loaded. See the error above.", "muted"));
            return;
        }
        List<Task> matchingTasks = visibleTasks == null ? disciTrack.getTasks() : visibleTasks;
        if (matchingTasks.isEmpty()) {
            boolean isEmptyList = disciTrack.getTasks().isEmpty();
            String emptyMessage = isEmptyList
                    ? "All clear, champ! Nothing on the board. Enjoy the breather - coach approves."
                    : "Nothing on the radar, champ. Try another keyword or hit Show all.";
            taskContainer.getChildren().add(label(emptyMessage, "card-body"));
        }
        int number = 1;
        for (Task task : disciTrack.getTasks()) {
            final int taskNumber = number++;
            if (!matchingTasks.contains(task)) {
                continue;
            }
            VBox description = new VBox(4,
                    label(task.getActivity(), task.isDone() ? "completed-task" : "card-body"),
                    label(getTaskDetails(task), "muted"));
            description.setMinWidth(0);
            description.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(description, Priority.ALWAYS);
            Button mark = new Button(task.isDone() ? "Unmark" : "Mark done");
            mark.getStyleClass().add(task.isDone() ? "secondary-button" : "gold-button");
            mark.setMinWidth(95);
            mark.setOnAction(event -> runCommand((task.isDone() ? "unmark " : "mark ") + taskNumber));
            HBox taskHeading = new HBox(12, label(Integer.toString(taskNumber), "muted"), description);
            taskHeading.setAlignment(Pos.CENTER_LEFT);
            Button tag = new Button("Tag");
            Button untag = new Button("Untag");
            Button delete = new Button("Delete");
            tag.getStyleClass().add("secondary-button");
            untag.getStyleClass().add("secondary-button");
            delete.getStyleClass().add("delete-button");
            untag.setDisable(task.getTags().isEmpty());
            FlowPane actions = new FlowPane(8, 8, mark, tag, untag, delete);
            actions.getStyleClass().add("task-actions");
            VBox editor = new VBox(8);
            editor.setVisible(false);
            editor.setManaged(false);
            tag.setOnAction(event -> showTagEditor(editor, taskNumber, task, false));
            untag.setOnAction(event -> showTagEditor(editor, taskNumber, task, true));
            delete.setOnAction(event -> runCommand("delete " + taskNumber));
            VBox row = new VBox(10, taskHeading, actions, editor);
            row.getStyleClass().add("task-row");
            taskContainer.getChildren().add(row);
        }
    }

    /**
     * Opens a tag editor inside the selected task row, keeping the task in context.
     */
    private void showTagEditor(VBox editor, int taskNumber, Task task, boolean isRemoval) {
        editor.getChildren().clear();
        editor.setVisible(true);
        editor.setManaged(true);
        Button submit = new Button(isRemoval ? "Remove tag" : "Add tag");
        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("secondary-button");
        cancel.setOnAction(event -> {
            editor.setVisible(false);
            editor.setManaged(false);
        });
        if (isRemoval) {
            ComboBox<String> tags = new ComboBox<>();
            tags.getItems().addAll(task.getTags());
            tags.getSelectionModel().selectFirst();
            tags.setMaxWidth(Double.MAX_VALUE);
            submit.setOnAction(event -> runCommand("untag " + taskNumber + " " + tags.getValue(), true));
            editor.getChildren().add(tags);
        } else {
            TextField name = new TextField();
            name.setId("tagInput");
            name.setPromptText("Tag name, e.g. school");
            submit.disableProperty().bind(name.textProperty().isEmpty());
            submit.setOnAction(event -> runCommand("tag " + taskNumber + " " + name.getText().trim(), true));
            name.setOnAction(event -> submit.fire());
            editor.getChildren().add(name);
            Platform.runLater(name::requestFocus);
        }
        editor.getChildren().add(new FlowPane(8, 8, submit, cancel));
    }

    private String getTaskDetails(Task task) {
        String detail = "To-do";
        if (task instanceof Deadline deadline) {
            detail = "Due " + deadline.getTime();
        } else if (task instanceof Event event) {
            detail = event.getFrom() + " to " + event.getTo();
        }
        if (!task.getTags().isEmpty()) {
            detail += "  #" + String.join(" #", task.getTags());
        }
        return task.isDone() ? "Completed | " + detail : detail;
    }

    @FXML
    private void handleList() {
        runCommand("list");
    }

    @FXML
    private void handleHelp() {
        runCommand("help");
    }

    @FXML
    private void handleFind() {
        String keyword = searchInput.getText().trim();
        runCommand(keyword.isEmpty() ? "list" : "find " + keyword);
        searchInput.requestFocus();
    }

    /**
     * Collects task fields and submits the equivalent command to the existing parser.
     */
    @FXML
    private void handleAdd() {
        Dialog<String> dialog = new Dialog<>();
        dialog.initOwner(root.getScene().getWindow());
        dialog.setTitle("Add a task");
        dialog.setHeaderText("Put your next win on the board.");
        ComboBox<String> type = new ComboBox<>();
        type.setId("taskType");
        type.getItems().addAll("todo", "deadline", "event");
        type.setValue("todo");
        TextField description = new TextField();
        description.setId("taskDescription");
        description.setPromptText("e.g. Review lecture notes");
        TextField from = new TextField();
        from.setPromptText("yyyy-MM-dd");
        TextField until = new TextField();
        until.setId("taskDate");
        until.setPromptText("yyyy-MM-dd");
        Label fromLabel = label("From (events)", "muted");
        Label untilLabel = label("Due / end date", "muted");
        from.disableProperty().bind(type.valueProperty().isNotEqualTo("event"));
        until.disableProperty().bind(type.valueProperty().isEqualTo("todo"));
        VBox fields = new VBox(8, label("Task type", "muted"), type, label("Description", "muted"),
                description, fromLabel, from, untilLabel, until);
        fields.setPrefWidth(360);
        dialog.getDialogPane().setContent(fields);
        styleDialog(dialog);
        ButtonType add = new ButtonType("Add task", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(add, ButtonType.CANCEL);
        Label error = label("", "form-error");
        error.setMinHeight(Region.USE_PREF_SIZE);
        error.setMaxWidth(Double.MAX_VALUE);
        error.setVisible(false);
        error.setManaged(false);
        fields.getChildren().add(error);
        dialog.getDialogPane().lookupButton(add).addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            String command = type.getValue() + " " + description.getText().trim();
            if (type.getValue().equals("deadline")) {
                command += " /by " + until.getText().trim();
            } else if (type.getValue().equals("event")) {
                command += " /from " + from.getText().trim() + " /to " + until.getText().trim();
            }
            if (runCommand(command)) {
                dialog.close();
            } else {
                error.setText(disciTrack.getLastResponse());
                error.setVisible(true);
                error.setManaged(true);
                dialog.getDialogPane().getScene().getWindow().sizeToScene();
            }
        });
        dialog.showAndWait();
    }

    private void showCommandGuide(String text) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(root.getScene().getWindow());
        dialog.setTitle("DisciTrack commands");
        dialog.setHeaderText("The playbook. Buttons or commands: your call.");
        VBox sections = new VBox(12);
        for (String section : text.split("\\R\\R")) {
            sections.getChildren().add(label(section, "card-body"));
        }
        ScrollPane guide = new ScrollPane(sections);
        guide.setFitToWidth(true);
        guide.setPrefViewportWidth(480);
        guide.setPrefViewportHeight(420);
        dialog.getDialogPane().setContent(guide);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        styleDialog(dialog);
        dialog.showAndWait();
        userInput.requestFocus();
    }

    private void styleDialog(Dialog<?> dialog) {
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
    }

    private Label label(String text, String style) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMinWidth(0);
        label.getStyleClass().add(style);
        return label;
    }
}
