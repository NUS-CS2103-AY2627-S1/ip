package wwaffle.gui;

import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import wwaffle.WWaffle;
import wwaffle.task.Task;

/**
 * Controls the main WWaffle chat window.
 */
public class MainWindow {
    private static final Duration EXIT_DELAY = Duration.millis(1400);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private WWaffle wwaffle;

    /**
     * Configures automatic scrolling after the FXML controls are injected.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollToLatestMessage());
    }

    /**
     * Supplies the application instance used to process commands.
     *
     * @param wwaffle WWaffle application instance.
     */
    public void setWWaffle(WWaffle wwaffle) {
        this.wwaffle = wwaffle;
        String welcome = wwaffle.getWelcomeMessage();
        if (welcome.startsWith("⚠️")) {
            addDialogBoxes(DialogBox.getWWaffleDialog(welcome));
        } else {
            dialogContainer.getChildren().add(new WelcomeBox(welcome));
        }
    }

    /**
     * Moves keyboard focus to the command field.
     */
    public void focusCommandBox() {
        Platform.runLater(userInput::requestFocus);
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText().strip().replaceAll("\\s+", " ");
        if (input.isEmpty()) {
            return;
        }

        List<Task> previousTasks = wwaffle.getTasksForDisplay();
        String response = wwaffle.getResponse(input);
        DialogBox reply;
        if (input.equals("list")) {
            reply = DialogBox.getTaskListDialog(response, wwaffle.getTasksForDisplay());
        } else if (!response.startsWith("⚠️")
                && (input.startsWith("find ") || input.startsWith("sort "))) {
            List<Task> allTasks = wwaffle.getTasksForDisplay();
            boolean isSearch = input.startsWith("find ");
            String keyword = isSearch ? input.substring(5).toLowerCase(Locale.ROOT) : "";
            List<Integer> numbers = IntStream.range(0, allTasks.size())
                    .filter(i -> !isSearch
                            || allTasks.get(i).getDescription().toLowerCase(Locale.ROOT).contains(keyword))
                    .map(i -> i + 1).boxed().toList();
            List<Task> displayed = numbers.stream().map(number -> allTasks.get(number - 1)).toList();
            reply = DialogBox.getChecklistResponse(response, displayed, numbers);
        } else if (response.startsWith("Task added. 🧁\n")
                || response.startsWith("Deadline added. 🧁\n") || response.startsWith("Event added. 🧁\n")) {
            List<Task> currentTasks = wwaffle.getTasksForDisplay();
            int number = currentTasks.size();
            reply = DialogBox.getTaskChangeDialog(response, currentTasks.get(number - 1), number);
        } else if (response.startsWith("Task deleted. ☀️\n")) {
            int number = Integer.parseInt(input.substring("delete".length()).trim());
            reply = DialogBox.getTaskChangeDialog(response, previousTasks.get(number - 1), number);
        } else if (response.startsWith("Task marked. 🤩\n")) {
            // The successful response means the command number has already been validated.
            int number = Integer.parseInt(input.substring("mark".length()).trim());
            reply = DialogBox.getMarkedTaskDialog(response, wwaffle.getTasksForDisplay().get(number - 1), number);
        } else if (response.startsWith("Task unmarked. 😑\n")) {
            // The successful response means the command number has already been validated.
            int number = Integer.parseInt(input.substring("unmark".length()).trim());
            reply = DialogBox.getUnmarkedTaskDialog(response, wwaffle.getTasksForDisplay().get(number - 1), number);
        } else if (response.equals("Cleared!")) {
            reply = DialogBox.getClearDialog(response);
        } else if (wwaffle.isExitCommand(input)) {
            reply = DialogBox.getGoodbyeDialog(response);
        } else {
            reply = DialogBox.getWWaffleDialog(response);
        }
        addDialogBoxes(DialogBox.getUserDialog(input), reply);
        userInput.clear();
        focusCommandBox();

        if (wwaffle.isExitCommand(input)) {
            userInput.setDisable(true);
            PauseTransition exitPause = new PauseTransition(EXIT_DELAY);
            exitPause.setOnFinished(event -> Platform.exit());
            exitPause.play();
        }
    }

    /**
     * Appends any number of dialog boxes to the conversation in the given order.
     *
     * @param dialogBoxes Dialog boxes to append.
     */
    private void addDialogBoxes(DialogBox... dialogBoxes) {
        for (DialogBox dialogBox : dialogBoxes) {
            dialogContainer.getChildren().add(dialogBox);
        }
    }

    private void scrollToLatestMessage() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
}
