package org.cs2103t.marquee.gui.ui;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.cs2103t.marquee.core.task.Task;
import org.cs2103t.marquee.core.time.DateTimeFormatter;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

/**
 * Controller for editable task item view.
 */
public class TaskEditorView extends GridPane {
    public static final StringConverter<LocalDateTime> DATETIME_STRING_CONVERTER =
            new StringConverter<LocalDateTime>() {
                @Override
                public String toString(LocalDateTime object) {
                    return DateTimeFormatter.formatDateTime(object);
                }

                @Override
                public LocalDateTime fromString(String string) {
                    try {
                        return DateTimeFormatter.parseDateTime(string);
                    } catch (DateTimeParseException e) {
                        Platform.runLater(() -> {
                            try {
                                CustomAlert<ButtonType> dialog = new CustomAlert<>(
                                        Alert.AlertType.ERROR,
                                        "Not a valid date-time",
                                        ButtonType.OK
                                );
                                int start = Math.max(e.getErrorIndex() - 10, 0);
                                int at = e.getErrorIndex();
                                int after = e.getErrorIndex() + 1;
                                int end = Math.min(e.getErrorIndex() + 5, e.getParsedString().length());
                                Text beforeText = new Text((start == 0 ? "..." : "")
                                        + e.getParsedString().substring(start, at));
                                Text errorText = new Text(e.getParsedString().substring(at, after));
                                errorText.getStyleClass().add("error");
                                Text afterText = new Text(e.getParsedString().substring(after, end)
                                        + (end == e.getParsedString().length() ? "..." : ""));
                                dialog.getContent().addAll(
                                        new Text("Parsing error at:\n  "),
                                        beforeText, errorText, afterText,
                                        new Text("\n" + e.getMessage())
                                );
                                dialog.showAndWait();
                            } catch (IOException ex) {
                                Alert backupDialog = new Alert(
                                        Alert.AlertType.ERROR,
                                        "'" + e.getParsedString() + "' is not a valid date-time",
                                        ButtonType.OK
                                );
                                backupDialog.showAndWait();
                            }
                        });
                        throw e;
                    }
                }
            };

    private final ObjectProperty<Task> task = new SimpleObjectProperty<>(this, "task");

    private final TextFormatter<LocalDateTime> startFormatter = new TextFormatter<>(DATETIME_STRING_CONVERTER);
    private final TextFormatter<LocalDateTime> endFormatter = new TextFormatter<>(DATETIME_STRING_CONVERTER);

    @FXML
    private CheckBox mark;
    @FXML
    private TextArea description;
    @FXML
    private StackPane tagsContainer;
    @FXML
    private TextField start;
    @FXML
    private TextField end;

    private final TagListView tags;

    /**
     * Creates a new {@code TaskEditorView}.
     * @throws IOException if an I/O error occurs
     */
    public TaskEditorView() throws IOException {
        tags = new TagListView();
        tags.setAddAllowed(true);

        task.addListener((_, oldTask, newTask) -> {
            if (oldTask != null) {
                mark.selectedProperty().unbindBidirectional(oldTask.markProperty());
                description.textProperty().unbindBidirectional(oldTask.descriptionProperty());
                tags.itemsProperty().unbindBidirectional(oldTask.tagsProperty());
                startFormatter.valueProperty().unbindBidirectional(oldTask.startProperty());
                endFormatter.valueProperty().unbindBidirectional(oldTask.endProperty());
            }
            if (newTask != null) {
                setDisable(false);
                mark.selectedProperty().bindBidirectional(newTask.markProperty());
                description.textProperty().bindBidirectional(newTask.descriptionProperty());
                tags.itemsProperty().bindBidirectional(newTask.tagsProperty());
                startFormatter.valueProperty().bindBidirectional(newTask.startProperty());
                endFormatter.valueProperty().bindBidirectional(newTask.endProperty());
            } else {
                setDisable(true);
                mark.setSelected(false);
                description.setText("");
                tags.setItems(null);
                start.setText("");
                end.setText("");
            }
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TaskEditorView.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        loader.load();
        setOnMousePressed(event -> requestFocus());
    }

    @FXML
    void initialize() {
        tagsContainer.getChildren().setAll(tags);
        start.setTextFormatter(startFormatter);
        end.setTextFormatter(endFormatter);
    }

    public Task getTask() {
        return task.get();
    }
    public void setTask(Task newTask) {
        task.set(newTask);
    }
    public ObjectProperty<Task> taskProperty() {
        return task;
    }
}
