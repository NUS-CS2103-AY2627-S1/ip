package yapatron.ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import yapatron.YapException;
import yapatron.save.Save;
import yapatron.task.Deadline;
import yapatron.task.Event;
import yapatron.task.Task;
import yapatron.task.TaskList;
import yapatron.task.Todo;

import java.util.List;
import java.util.Locale;

/**
 * JavaFX graphical user interface for Yapatron.
 *
 * The GUI handles user interaction while reusing the existing
 * Task, TaskList, and Save classes.
 */
public class Gui extends Application {

    private static final String DATA_FILE = "./data/yapatron.txt";

    private TaskList tasks;
    private Save save;

    private final ObservableList<String> displayedTasks =
        FXCollections.observableArrayList();

    private ListView<String> taskListView;
    private TextField commandField;
    private Label statusLabel;
    private Stage stage;
    private String activeFilter = "";

    /**
     * Starts the JavaFX application.
     *
     * @param primaryStage main application window
     */
    @Override
        public void start(Stage primaryStage) {
            this.stage = primaryStage;

            initialiseData();
            initialiseWindow();
            refreshTaskList();

            primaryStage.show();
        }

    /**
     * Loads saved tasks from local storage.
     */
    private void initialiseData() {
        save = new Save(DATA_FILE);

        try {
            tasks = new TaskList(save.getTasks());
        } catch (YapException e) {
            tasks = new TaskList();
        }
    }

    /**
     * Creates and configures the main application window.
     */
    private void initialiseWindow() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F7F9FC;");

        VBox content = new VBox(15);
        content.getChildren().addAll(
                createHeader(),
                createTaskList(),
                createCommandArea(),
                createHelpText()
                );

        root.setCenter(content);

        Scene scene = new Scene(root, 760, 620);

        stage.setTitle("Yapatron :D");
        stage.setScene(scene);
        stage.setMinWidth(600);
        stage.setMinHeight(500);
    }

    /**
     * Creates the title and status area.
     *
     * @return header layout
     */
    private VBox createHeader() {

        String yapBanner = "██╗    ██╗ █████╗ ██████╗  █████╗ ████████╗██████╗  ██████╗ ███╗   ██╗\n" +
            "╚██╗ ██╔╝██╔══██╗██╔══██╗██╔══██╗╚══██╔══╝██╔══██╗██╔═══██╗████╗  ██║\n" +
            " \\████╔╝ ███████║██████╔╝███████║   ██║   ██████╔╝██║   ██║██╔██╗ ██║\n" +
            "  ╚██╔╝  ██╔══██║██╔═══╝ ██╔══██║   ██║   ██╔══██╗██║   ██║██║╚██╗██║\n" +
            "   ██║   ██║  ██║██║     ██║  ██║   ██║   ██║  ██║╚██████╔╝██║ ╚████║\n" +
            "   ╚═╝   ╚═╝  ╚═╝╚═╝     ╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═══╝";

        Label banner = new Label(yapBanner);
        banner.setMaxWidth(Double.MAX_VALUE);
        banner.setWrapText(false);
        banner.setStyle(
                "-fx-background-color: transparent;"
                + "-fx-border-color: transparent;"
                + "-fx-text-fill: #80CBC4;"
                );

        banner.setFont(Font.font(
                    "Monospaced",
                    FontWeight.BOLD,
                    18
                    ));

        banner.widthProperty().addListener((observable, oldWidth, newWidth) -> {
                double fontSize = newWidth.doubleValue() / 55;

                fontSize = Math.max(8, Math.min(24, fontSize));

                banner.setFont(Font.font(
                            "Monospaced",
                            FontWeight.BOLD,
                            fontSize
                            ));
                });


        Label title = new Label("Yapatron");
        title.setStyle(
                "-fx-font-size: 28px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #263238;"
                );

        Label subtitle = new Label("Too many thoughts? I got you!!!!");
        subtitle.setStyle(
                "-fx-font-size: 14px;"
                + "-fx-text-fill: #607D8B;"
                );

        statusLabel = new Label("Ready");
        statusLabel.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-text-fill: #455A64;"
                );

        VBox header = new VBox(4);
        header.getChildren().addAll(banner, title, subtitle, statusLabel);

        return header;
    }

    /**
     * Creates the list displaying all current tasks.
     *
     * @return task list layout
     */
    private VBox createTaskList() {
        Label listTitle = new Label("your tasks :D");
        listTitle.setStyle(
                "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #263238;"
                );

        taskListView = new ListView<>(displayedTasks);
        taskListView.setPrefHeight(350);
        taskListView.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 16;"
                + "-fx-border-color: #D5DDE5;"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 16;"
                + "-fx-padding: 8;"
                + "-fx-font-size: 14px;"
                );

        VBox.setVgrow(taskListView, Priority.ALWAYS);

        VBox taskArea = new VBox(8);
        taskArea.getChildren().addAll(listTitle, taskListView);

        return taskArea;
    }

    /**
     * Creates the text field and buttons used to execute commands.
     *
     * @return command input layout
     */
    private VBox createCommandArea() {
        commandField = new TextField();
        commandField.setPromptText("what's new for today? e.g. todo complete CS2103T tutorial");
        commandField.setOnAction(event -> executeCommand());

        Button executeButton = new Button("GO!");
        executeButton.setDefaultButton(true);
        executeButton.setOnAction(event -> executeCommand());
        executeButton.setStyle(
                "-fx-background-color: #1976D2;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                );

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> {
                activeFilter = "";
                refreshTaskList();
                statusLabel.setText("Task list refreshed.");
                });
        HBox commandRow = new HBox(10);
        commandRow.setAlignment(Pos.CENTER_LEFT);
        commandRow.getChildren().addAll(commandField, executeButton, refreshButton);

        HBox.setHgrow(commandField, Priority.ALWAYS);

        Label commandTitle = new Label("Command");
        commandTitle.setStyle(
                "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #263238;"
                );

        VBox commandArea = new VBox(8);
        commandArea.getChildren().addAll(commandTitle, commandRow);

        return commandArea;
    }

    /**
     * Displays the supported commands.
     *
     * @return help label
     */
    private Label createHelpText() {
        Label help = new Label(
                "need help? here are the supported commands! todo <description> | "
                + "deadline <description> /by <d/M/yyyy HHmm> | "
                + "event <description> /from <time> /to <time> | "
                + "list | mark <number> | unmark <number> | "
                + "delete <number> | find <keyword> | bye"
                );

        help.setWrapText(true);
        help.setStyle(
                "-fx-font-size: 12px;"
                + "-fx-text-fill: #607D8B;"
                );

        return help;
    }

    /**
     * Executes the command entered by the user.
     */
    private void executeCommand() {
        String command = commandField.getText().trim();

        if (command.isEmpty()) {
            statusLabel.setText("Please enter a command.");
            return;
        }

        try {
            processCommand(command);
            commandField.clear();

            if (!command.startsWith("find ")) {
                refreshTaskList();
            }
        } catch (YapException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }
    /**
     * Parses and executes a user command.
     *
     * @param command complete command string
     * @throws YapException if the command is invalid
     */
    private void processCommand(String command) throws YapException {
        String[] parts = command.split(" ", 2);
        String commandName = parts[0].toLowerCase(Locale.ROOT);

        switch (commandName) {
            case "todo":
                addTodo(parts);
                break;

            case "deadline":
                addDeadline(parts);
                break;

            case "event":
                addEvent(parts);
                break;

            case "list":
                activeFilter = "";
                refreshTaskList();
                statusLabel.setText("Displaying all tasks.");
                break;

            case "mark":
                markTask(parts);
                break;

            case "unmark":
                unmarkTask(parts);
                break;

            case "delete":
                deleteTask(parts);
                break;

            case "find":
                findTasks(parts);
                break;

            case "bye":
                stage.close();
                break;

            default:
                throw new YapException(
                        "i don't know what that means :( please refer to our trusty guide at the bottom!"
                        );
        }
    }

    /**
     * Adds a todo task.
     *
     * @param parts command split into command name and description
     * @throws YapException if the description is missing
     */
    private void addTodo(String[] parts) throws YapException {
        requireDescription(parts, "TODO");

        Task task = new Todo(parts[1]);
        tasks.addTask(task);
        saveTasks();

        statusLabel.setText("todo " + parts[1] + " added!");
    }

    /**
     * Adds a deadline task.
     *
     * @param parts command split into command name and details
     * @throws YapException if the details are invalid
     */
    private void addDeadline(String[] parts) throws YapException {
        requireDescription(parts, "DEADLINE");

        String[] deadlineParts = parts[1].split(" /by ", 2);

        if (deadlineParts.length < 2
                || deadlineParts[0].trim().isEmpty()
                || deadlineParts[1].trim().isEmpty()) {
            throw new YapException(
                    "Whoops! Please use: deadline <description> /by <d/M/yyyy HHmm>"
                    );
        }

        Task task = new Deadline(
                deadlineParts[0].trim(),
                deadlineParts[1].trim()
                );

        tasks.addTask(task);
        saveTasks();

        statusLabel.setText("Deadline " + deadlineParts[0] + "  added successfully.");
    }

    /**
     * Adds an event task.
     *
     * @param parts command split into command name and details
     * @throws YapException if the details are invalid
     */
    private void addEvent(String[] parts) throws YapException {
        requireDescription(parts, "EVENT");

        String[] eventParts = parts[1].split(" /from ", 2);

        if (eventParts.length < 2) {
            throw new YapException(
                    "Whoops! Please use: event <description> /from <time> /to <time>"
                    );
        }

        String[] timeParts = eventParts[1].split(" /to ", 2);

        if (eventParts[0].trim().isEmpty()
                || timeParts.length < 2
                || timeParts[0].trim().isEmpty()
                || timeParts[1].trim().isEmpty()) {
            throw new YapException(
                    "Use: event <description> /from <time> /to <time>"
                    );
        }

        Task task = new Event(
                eventParts[0].trim(),
                timeParts[0].trim(),
                timeParts[1].trim()
                );

        tasks.addTask(task);
        saveTasks();

        statusLabel.setText("event " + eventParts[0] + "  added successfully.");
    }

    /**
     * Marks a task as completed.
     *
     * @param parts command split into command name and index
     * @throws YapException if the index is invalid
     */
    private void markTask(String[] parts) throws YapException {
        int index = parseTaskIndex(parts);
        Task task = tasks.mark(index);

        saveTasks();
        statusLabel.setText("great job!  " + task.getDesc() + " done:)");
    }

    /**
     * Marks a task as incomplete.
     *
     * @param parts command split into command name and index
     * @throws YapException if the index is invalid
     */
    private void unmarkTask(String[] parts) throws YapException {
        int index = parseTaskIndex(parts);
        Task task = tasks.unmark(index);

        saveTasks();
        statusLabel.setText("unmarked " + task.getDesc());
    }

    /**
     * Deletes a task.
     *
     * @param parts command split into command name and index
     * @throws YapException if the index is invalid
     */
    private void deleteTask(String[] parts) throws YapException {
        int index = parseTaskIndex(parts);
        Task deletedTask = tasks.delete(index);

        saveTasks();
        statusLabel.setText("deleted " + deletedTask.getDesc());
    }

    /**
     * Searches for matching tasks.
     *
     * @param parts command split into command name and keyword
     * @throws YapException if the keyword is missing
     */
    private void findTasks(String[] parts) throws YapException {
        requireDescription(parts, "FIND");

        activeFilter = parts[1].trim();

        List<Task> matchingTasks = tasks.find(activeFilter);

        refreshTaskList();

        if (matchingTasks.isEmpty()) {
            statusLabel.setText("No matching tasks found.");
        } else {
            statusLabel.setText(
                    matchingTasks.size() + " matching task(s) found."
                    );
        }
    }
    /**
     * Refreshes the list view using the current task list.
     */
    private void refreshTaskList() {
        displayedTasks.clear();

        List<Task> tasksToDisplay;

        if (activeFilter.isBlank()) {
            tasksToDisplay = tasks.getTasks();
        } else {
            tasksToDisplay = tasks.find(activeFilter);
        }

        for (int i = 0; i < tasksToDisplay.size(); i++) {
            displayedTasks.add(
                    (i + 1) + ". " + tasksToDisplay.get(i)
                    );
        }
    }   
    /**
     * Saves the current tasks.
     *
     * @throws YapException if saving fails
     */
    private void saveTasks() throws YapException {
        save.saveTasks(tasks.getTasks());
    }

    /**
     * Checks that a command has a second argument.
     *
     * @param parts command components
     * @param commandName command name for the error message
     * @throws YapException if the argument is missing
     */
    private void requireDescription(
            String[] parts,
            String commandName
            ) throws YapException {
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new YapException(
                    commandName + " is missing required details."
                    );
        }
    }

    /**
     * Converts a user-facing task number into a zero-based index.
     *
     * @param parts command components
     * @return zero-based task index
     * @throws YapException if the index is invalid
     */
    private int parseTaskIndex(String[] parts) throws YapException {
        requireDescription(parts, "TASK NUMBER");

        try {
            return Integer.parseInt(parts[1].trim()) - 1;
        } catch (NumberFormatException e) {
            throw new YapException("Please enter a valid task number");
        }
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
