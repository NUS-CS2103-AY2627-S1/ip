package jarvis.ui;

import java.util.List;
import java.util.function.Consumer;

import jarvis.backend.JarvisController;
import jarvis.backend.Parser;
import jarvis.classes.Task;
import jarvis.exceptions.InvalidTaskNumberException;
import jarvis.exceptions.JarvisException;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for the Jarvis GUI.
 * Handles user interactions and displays tasks in the JavaFX interface.
 */
public class JarvisGuiController {

    private static final String WELCOME_MESSAGE = "Good evening.\n"
            + "I am J.A.R.V.I.S., your personal task management system.\n\n"
            + "How may I assist you?\n"
            + "- Type 'help' to view available commands.\n"
            + "- Type 'bye' to terminate the session.";
    private static final String BYE_MESSAGE = "Understood. Your tasks have been safely saved.\n"
            + "Until next time.";

    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String LIST_COMMAND = "list";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String FIND_COMMAND = "find";
    private static final String HELP_COMMAND = "help";
    private static final String BYE_COMMAND = "bye";
    private static final String UNKNOWN_COMMAND = "I'm afraid I don't recognize that command.\n"
            + "- Type 'help' to view the available commands.";

    private static final String JARVIS_IMAGE_DIRECTORY = "/images/jarvisPicture.png";
    private static final String DEFAULT_USER_IMAGE_DIRECTORY = "/images/blankProfilePicture.png";

    private static final String ADD_TASK_MSG_HEADER = "Certainly. I've added the following task:\n" + "  ";
    private static final String MARK_TASK_AS_DONE_MSG_HEADER =
            "Very good. I've marked the following task as complete:\n" + "  ";
    private static final String UNMARK_TASK_AS_DONE_MSG_HEADER =
            "Understood. I've restored the following task to your active list:\n" + "  ";
    private static final String DELETE_TASK_MSG_HEADER = "Understood. I've removed the following task:\n" + "  ";
    private static final String NON_CRITICAL_ERROR_MSG_HEADER = "⚠ Warning : \n";
    private static final String CRITICAL_ERROR_MSG_HEADER = "⚠ SYSTEM Error : \n";

    private static final String HELP_COMMAND_MSG_HEADER = "J.A.R.V.I.S. COMMAND DIRECTORY\n"
            + "============================\n";
    private static final String TODO_TASK_FORMAT = "- [TODO] : todo <Task Name> \n";
    private static final String DEADLINE_TASK_DATE_ONLY_FORMAT =
            "- [DEADLINE] (Date only) : deadline <Task Name> /by yyyy-MM-dd\n";
    private static final String DEADLINE_TASK_DATE_TIME_FORMAT =
            "- [DEADLINE] (Date and Time) : deadline <Task Name> /by yyyy-MM-dd HH:mm\n";
    private static final String EVENT_TASK_DATE_ONLY_FORMAT =
            "- [EVENT] (Date only) : event <Task Name> /from yyyy-MM-dd /to yyyy-MM-dd\n";
    private static final String EVENT_TASK_DATE_TIME_FORMAT =
            "- [EVENT (Date and Time) : event <Task Name> /from yyyy-MM-dd HH:mm /to yyyy-MM-dd HH:mm\n";
    private static final String MARK_TASK_FORMAT = "- [MARK] : mark <Valid task number>\n";
    private static final String UNMARK_TASK_FORMAT = "- [UNMARK] : unmark <Valid task number>\n";
    private static final String DELETE_TASK_FORMAT = "- [DELETE] : delete <Valid task number>\n";
    private static final String FIND_TASK_FORMAT = "- [FIND] : find <Keyword (Min 2 characters long)>\n";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Label taskStatusLabel;

    private JarvisController jarvisController;
    private Consumer<String> outputHandler;

    private final Image jarvisImage = new Image(this.getClass().getResourceAsStream(JARVIS_IMAGE_DIRECTORY));
    private final Image userImage = new Image(this.getClass().getResourceAsStream(DEFAULT_USER_IMAGE_DIRECTORY));

    /**
     * Initializes the GUI controller.
     * Sets up the Jarvis controller and dialog display.
     */
    @FXML
    public void initialize() {
        jarvisController = new JarvisController();
        outputHandler = this::displayMessage;

        // Automatically scroll to the latest message
        dialogContainer.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            scrollPane.setVvalue(1.0);
        });

        // Set up send button action
        sendButton.setOnAction(event -> handleUserInput());

        // Set up enter key on text field
        userInput.setOnAction(event -> handleUserInput());

        // Display welcome message
        displayWelcomeMessage();

        // Update number of outstanding tasks
        updateTaskStatus();
    }

    /**
     * Handles user input from the text field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        // Display user input
        displayUserMessage(input);

        // Clear input field
        userInput.clear();

        // Process command
        processCommand(input);
    }

    /**
     * Processes the user command and executes the appropriate action.
     *
     * @param input User command.
     */
    private void processCommand(String input) {

        try {
            if (isCommand(input, TODO_COMMAND)) {
                displayTodoTaskCreation(input);
            } else if (isCommand(input, DEADLINE_COMMAND)) {
                displayDeadlineTaskCreation(input);
            } else if (isCommand(input, EVENT_COMMAND)) {
                displayEventTaskCreation(input);
            } else if (isCommand(input, LIST_COMMAND)) {
                displayTasks();
            } else if (isCommand(input, MARK_COMMAND)) {
                displayMarkedTask(input);
            } else if (isCommand(input, UNMARK_COMMAND)) {
                displayUnmarkedTask(input);
            } else if (isCommand(input, DELETE_COMMAND)) {
                displayDeletedTask(input);
            } else if (isCommand(input, FIND_COMMAND)) {
                displayFindResults(input);
            } else if (isCommand(input, HELP_COMMAND)) {
                displayHelpCommands();
            } else if (isCommand(input, BYE_COMMAND)) {
                displayByeCommand();
            } else {
                displayMessage(UNKNOWN_COMMAND);
            }
        } catch (JarvisException e) {
            displayErrorMessage(NON_CRITICAL_ERROR_MSG_HEADER + e.getMessage());
        } catch (Exception e) {
            displayErrorMessage(CRITICAL_ERROR_MSG_HEADER + e.getMessage());
        }
    }

    /**
     * Checks if the user input matches the given command.
     *
     * @param input User's input.
     * @param command Command to check against.
     * @return true if the input is the given command or starts with the command
     *         followed by a space.
     */
    private boolean isCommand(
            String input,
            String command) {

        return input.equals(command)
                || input.startsWith(command + " ");
    }

    /**
     * Create a new todo task and displays it back to the user.
     *
     * @param userInput User's command for todo task.
     * @throws JarvisException If there is issue in creating or displaying todo task.
     */
    private void displayTodoTaskCreation(String userInput) throws JarvisException {
        jarvisController.createToDoTask(userInput);
        displayMessage(ADD_TASK_MSG_HEADER + getLastTask().toString());
        displayTaskCount();
        updateTaskStatus();
    }

    /**
     * Create a new deadline task and displays it back to the user.
     *
     * @param userInput User's command for deadline task.
     * @throws JarvisException If there is issue in creating or displaying deadline task.
     */
    private void displayDeadlineTaskCreation(String userInput) throws JarvisException {
        jarvisController.createDeadlineTask(userInput);
        displayMessage(ADD_TASK_MSG_HEADER + getLastTask().toString());
        displayTaskCount();
        updateTaskStatus();
    }

    /**
     * Create a new event task and displays it back to the user.
     *
     * @param userInput User's command for event task.
     * @throws JarvisException If there is issue in creating or displaying deadline task.
     */
    private void displayEventTaskCreation(String userInput) throws JarvisException {
        jarvisController.createEventTask(userInput);
        displayMessage(ADD_TASK_MSG_HEADER + getLastTask().toString());
        displayTaskCount();
        updateTaskStatus();
    }

    /**
     * Marks task as done and displays it back to the user.
     *
     * @param userInput User's command to mark a task as done.
     * @throws JarvisException If there is issue in marking or displaying the task.
     */
    private void displayMarkedTask(String userInput) throws JarvisException {
        jarvisController.markTaskAs(userInput, Task.CompletionStatus.DONE);
        displayMessage(MARK_TASK_AS_DONE_MSG_HEADER + getTaskFromCommand(userInput).toString());
    }

    /**
     * Unmarks task as done and displays it back to the user.
     *
     * @param userInput User's command to unmark a task as done.
     * @throws JarvisException If there is issue in marking or displaying the task.
     */
    private void displayUnmarkedTask(String userInput) throws JarvisException {
        jarvisController.markTaskAs(userInput, Task.CompletionStatus.UNDONE);
        displayMessage(UNMARK_TASK_AS_DONE_MSG_HEADER + getTaskFromCommand(userInput).toString());
    }

    /**
     * Deletes a task from the .txt file and display the deleted
     * task back to the user.
     *
     * @param userInput User's command to delete a task.
     * @throws JarvisException If there is an issue deleting or displaying the task.
     */
    private void displayDeletedTask(String userInput) throws JarvisException {
        Task deletedTask = jarvisController.deleteTask(userInput);
        displayMessage(DELETE_TASK_MSG_HEADER + deletedTask.toString());
        displayTaskCount();
        updateTaskStatus();
    }

    /**
     * Updates the task count displayed in the J.A.R.V.I.S. header.
     */
    private void updateTaskStatus() {
        int count = jarvisController.size();
        taskStatusLabel.setText("TASKS: " + count);
    }

    /**
     * Displays bye message and closes the GUI console.
     */
    private void displayByeCommand() {
        displayMessage(BYE_MESSAGE);
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));

        pause.setOnFinished(event -> {
            Stage stage = (Stage) userInput.getScene().getWindow();
            stage.close();
        });

        pause.play();
    }

    /**
     * Displays all tasks in the dialog container.
     */
    private void displayTasks() {
        List<Task> tasks = jarvisController.getTasks();
        if (tasks.isEmpty()) {
            displayMessage("Your task list is currently empty.");
            return;
        }

        StringBuilder sb = new StringBuilder("Certainly. Here is your current task list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(String.format("%d. %s\n", i + 1, tasks.get(i).toString()));
        }
        displayMessage(sb.toString());
    }


    /**
     * Filter tasks by user's inputted key word and display it back to the user
     *
     * @param userInput User's command to find tasks based on a key word
     * @throws JarvisException If there is an issue filtering tasks based on key word
     */
    private void displayFindResults(String userInput) throws JarvisException {

        List<Task> matchingTasks = jarvisController.filterTasks(userInput);

        if (matchingTasks.isEmpty()) {
            displayMessage("I'm afraid I couldn't locate any tasks matching that description.");
            return;
        }

        StringBuilder sb = new StringBuilder("I've located the following matching tasks:\n");
        for (int i = 0; i < matchingTasks.size(); i++) {
            sb.append(String.format("%d. %s\n", i + 1, matchingTasks.get(i).toString()));
        }
        displayMessage(sb.toString());
    }

    /**
     * Displays the task count.
     */
    private void displayTaskCount() {
        int size = jarvisController.size();
        displayMessage(
                "Task registry updated. You now have "
                        + size
                        + " active task"
                        + (size == 1 ? "" : "s")
                        + ".");
    }

    /**
     * Displays list of commands and their respective formats
     */
    private void displayHelpCommands() {
        displayMessage(HELP_COMMAND_MSG_HEADER
                + TODO_TASK_FORMAT
                + DEADLINE_TASK_DATE_ONLY_FORMAT
                + DEADLINE_TASK_DATE_TIME_FORMAT
                + EVENT_TASK_DATE_ONLY_FORMAT
                + EVENT_TASK_DATE_TIME_FORMAT
                + MARK_TASK_FORMAT
                + UNMARK_TASK_FORMAT
                + DELETE_TASK_FORMAT
                + FIND_TASK_FORMAT);
    }

    /**
     * Displays a welcome message.
     */
    private void displayWelcomeMessage() {
        displayMessage(WELCOME_MESSAGE);
    }

    /**
     * Displays a user message in the dialog container.
     *
     * @param message User message.
     */
    private void displayUserMessage(String message) {
        MessageBox userMessageBox = new MessageBox(message, userImage, MessageBox.MessageType.USER);
        dialogContainer.getChildren().add(userMessageBox);
    }

    /**
     * Displays a system message in the dialog container.
     *
     * @param message System message.
     */
    private void displayMessage(String message) {
        MessageBox jarvisMessageBox = new MessageBox(message, jarvisImage, MessageBox.MessageType.JARVIS);
        dialogContainer.getChildren().add(jarvisMessageBox);
    }

    /**
     * Displays an error message in the dialog container.
     *
     * @param message Error message.
     */
    private void displayErrorMessage(String message) {
        MessageBox errorMessageBox =
                new MessageBox(message, jarvisImage, MessageBox.MessageType.ERROR);

        dialogContainer.getChildren().add(errorMessageBox);
    }


    /**
     * Gets the last task from the task list.
     *
     * @return Last task.
     * @throws InvalidTaskNumberException If the number is invalid.
     */
    private Task getLastTask() throws InvalidTaskNumberException {
        assert jarvisController.size() > 0
                : "getLastTask should only be called when at least one task exists";

        return jarvisController.getTask(jarvisController.size() - 1);
    }

    /**
     * Gets a task from a command that includes a task number.
     *
     * @param input Command input.
     * @return Task at the specified index.
     */
    private Task getTaskFromCommand(String input) throws JarvisException {
        int taskNumber = Parser.parseTaskNumber(input);
        return jarvisController.getTask(taskNumber - 1);
    }
}
