package trayce.ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import trayce.parser.Parser;
import trayce.task.Task;
import trayce.task.TaskList;

/** Provides a graphical interface for viewing and adding Trayce tasks. */
public class TrayceGui extends Application {
    private final TaskList taskList = new TaskList();
    private final Parser parser = new Parser();
    private final ListView<String> taskView = new ListView<>();
    private final Label status = new Label("Ready");

    /** Builds and displays the Trayce window. */
    @Override
    public void start(Stage stage) {
        TextField commandField = new TextField();
        commandField.setPromptText("e.g. todo read a book");
        Button addButton = new Button("Add");
        addButton.setOnAction(event -> addTask(commandField));
        commandField.setOnAction(event -> addTask(commandField));

        Button markButton = new Button("Mark done");
        markButton.setOnAction(event -> markSelectedTask());
        HBox input = new HBox(8, commandField, addButton, markButton);
        VBox content = new VBox(8, new Label("Trayce tasks"), taskView, input, status);
        BorderPane root = new BorderPane(content);
        root.setPrefSize(600, 400);

        stage.setTitle("Trayce");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void addTask(TextField commandField) {
        Task task = parser.parseTask(commandField.getText());
        if (task == null) {
            status.setText("Invalid task command.");
            return;
        }
        taskList.add(task);
        commandField.clear();
        refreshTasks();
        status.setText("Added task " + taskList.size() + ".");
    }

    private void markSelectedTask() {
        int selectedIndex = taskView.getSelectionModel().getSelectedIndex();
        Task task = taskList.get(selectedIndex);
        if (task == null) {
            status.setText("Select a task first.");
            return;
        }
        task.markAsDone();
        refreshTasks();
        status.setText("Marked task as done.");
    }

    private void refreshTasks() {
        taskView.setItems(FXCollections.observableArrayList(taskList.getTasks().stream()
                .map(task -> "[" + task.getStatusIcon() + "] " + task.getDescription())
                .toList()));
    }
}
