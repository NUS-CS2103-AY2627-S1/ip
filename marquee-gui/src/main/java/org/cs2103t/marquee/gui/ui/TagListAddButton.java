package org.cs2103t.marquee.gui.ui;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Controller for tags list add button.
 */
public class TagListAddButton extends HBox {
    private final TagListView parent;

    @FXML
    private HBox buttonContainer;
    @FXML
    private Label hiddenCountLabel;

    /**
     * Creates a {@code TagListAddButton}.
     */
    public TagListAddButton(TagListView parent) throws IOException {
        this.parent = parent;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TagListAddButton.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        loader.load();
    }

    @FXML
    void initialize() {
        NumberBinding hiddenCount = parent.itemsProperty().sizeProperty().subtract(parent.lastIndexProperty());
        hiddenCountLabel.textProperty().bind(Bindings.concat("+", hiddenCount, " more..."));
        hiddenCountLabel.visibleProperty().bind(hiddenCount.isNotEqualTo(0));
        hiddenCountLabel.managedProperty().bind(hiddenCount.isNotEqualTo(0));

        buttonContainer.visibleProperty().bind(parent.addAllowedProperty());
    }

    @FXML
    private void addTag() {
        try {
            TagSelectDialog dialog = new TagSelectDialog();
            dialog.showAndWait().ifPresent(result ->
                    parent.getItems().add(result)
            );
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(
                    Alert.AlertType.ERROR,
                    "Cannot open tag list dialog\n" + e.getMessage(),
                    ButtonType.OK
            ).showAndWait();
        }
    }
}
