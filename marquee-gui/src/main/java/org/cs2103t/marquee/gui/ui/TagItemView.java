package org.cs2103t.marquee.gui.ui;

import java.io.IOException;

import org.cs2103t.marquee.core.task.TaskTag;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Controller for tag chip.
 */
public class TagItemView extends HBox {
    private final ObjectProperty<TaskTag> tag = new SimpleObjectProperty<>(this, "tag");

    private final TagListView parent;

    @FXML
    private Label label;
    @FXML
    private Button button;

    /**
     * Creates a {@code TagItemView}.
     */
    public TagItemView(TagListView parent) throws IOException {
        this.parent = parent;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TagItemView.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        loader.load();
    }

    @FXML
    void initialize() {
        label.textProperty().bind(tag.asString());

        button.visibleProperty().bind(parent.addAllowedProperty().and(tag.isNotNull()));
        button.disableProperty().bind(parent.addAllowedProperty().and(tag.isNotNull()).not());
    }

    @FXML
    private void removeTag() {
        parent.getItems().remove(tag.get());
    }

    public TaskTag getTag() {
        return tag.get();
    }
    public void setTag(TaskTag newTag) {
        tag.set(newTag);
    }
    public ObjectProperty<TaskTag> tagProperty() {
        return tag;
    }
}
