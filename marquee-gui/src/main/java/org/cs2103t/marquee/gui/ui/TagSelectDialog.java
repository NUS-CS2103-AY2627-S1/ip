package org.cs2103t.marquee.gui.ui;

import java.io.IOException;

import org.cs2103t.marquee.core.task.TaskTag;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;

/**
 * Dialog for adding tags.
 */
public class TagSelectDialog extends Dialog<TaskTag> {
    private final ListProperty<TaskTag> tags =
            new SimpleListProperty<>(this, "tags", FXCollections.observableArrayList());

    private class TagSelectDialogView extends DialogPane {
        private final FilteredList<TaskTag> filtered = new FilteredList<>(tags);

        @FXML
        private TextField response;
        @FXML
        private ListView<TaskTag> searchResult;

        public TagSelectDialogView() throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TagSelectDialogView.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }

        @FXML
        void initialize() {
            filtered.predicateProperty().bind(
                    Bindings.createObjectBinding(
                            () -> label -> label.toString().contains(response.getText()),
                            response.textProperty()
                    )
            );
            searchResult.setCellFactory(_ -> new ListCell<>() {
                {
                    setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            TaskTag selected = searchResult.getSelectionModel().getSelectedItem();
                            if (selected != null) {
                                response.setText(selected.toString());
                            }
                        }
                    });
                }

                @Override
                protected void updateItem(TaskTag item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(null);
                    if (empty) {
                        setText(null);
                    } else if (item == null) {
                        setText("Add new tag...");
                    } else {
                        setText(item.toString());
                    }
                }
            });
            searchResult.itemsProperty().bind(Bindings.createObjectBinding(() -> {
                if (filtered.isEmpty()) {
                    ObservableList<TaskTag> nullList = FXCollections.observableArrayList();
                    nullList.add(null);
                    return nullList;
                } else {
                    return filtered;
                }
            }, filtered));
        }
    }
    private final TagSelectDialogView dialogPane;

    /**
     * Creates a {@code TagSelectDialog}.
     */
    public TagSelectDialog() throws IOException {
        setTitle("Add tag...");
        tags.setAll(TaskTag.getDictionary().values());
        dialogPane = new TagSelectDialogView();

        setDialogPane(dialogPane);
        setResultConverter(response ->
                response.equals(ButtonType.OK) ? TaskTag.createOrGet(dialogPane.response.getText()) : null);
    }
}
