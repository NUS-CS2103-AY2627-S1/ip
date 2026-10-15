package org.cs2103t.marquee.gui.ui;

import java.io.IOException;
import java.util.Objects;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Window;

/**
 * Dialog with text flows for customizable text.
 *
 * @param <R> the return type of the dialog
 */
public class CustomAlert<R> extends Dialog<ButtonType> {
    private static final Image CONFIRM_ICON =
            new Image(Objects.requireNonNull(CustomAlert.class.getResourceAsStream("/icon/dialog-confirm.png")));
    private static final Image ERROR_ICON =
            new Image(Objects.requireNonNull(CustomAlert.class.getResourceAsStream("/icon/dialog-error.png")));
    private static final Image INFORMATION_ICON =
            new Image(Objects.requireNonNull(CustomAlert.class.getResourceAsStream("/icon/dialog-information.png")));
    private static final Image WARNING_ICON =
            new Image(Objects.requireNonNull(CustomAlert.class.getResourceAsStream("/icon/dialog-warning.png")));

    private class CustomDialogView extends DialogPane {
        @FXML
        private Label header;
        @FXML
        private TextFlow content;
        @FXML
        private TextFlow details;
        @FXML
        private ImageView icon;

        public CustomDialogView() throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomDialogView.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }

        @FXML
        void initialize() {
            if (getAlertType() == Alert.AlertType.NONE) {
                icon.setVisible(false);
                icon.setManaged(false);
            } else {
                icon.setImage(switch (getAlertType()) {
                    case INFORMATION -> INFORMATION_ICON;
                    case WARNING -> WARNING_ICON;
                    case ERROR -> ERROR_ICON;
                    case CONFIRMATION -> CONFIRM_ICON;
                    case NONE -> null;
                });
            }
            header.textProperty().bind(headerTextProperty());
            Bindings.bindContent(content.getChildren(), CustomAlert.this.getContent());
            Bindings.bindContent(details.getChildren(), CustomAlert.this.getDetails());
            CustomAlert.this.detailsProperty().emptyProperty().addListener((_, _, isDetailsEmpty) -> {
                if (isDetailsEmpty) {
                    setExpandableContent(null);
                } else {
                    setExpandableContent(details);
                }
            });
            if (CustomAlert.this.detailsProperty().isEmpty()) {
                setExpandableContent(null);
            } else {
                setExpandableContent(details);
            }
        }
    }

    private final ListProperty<Text> content = new SimpleListProperty<>(this, "content", FXCollections.observableArrayList());
    private final ListProperty<Text> details = new SimpleListProperty<>(this, "details", FXCollections.observableArrayList());

    private final Alert.AlertType alertType;

    /**
     * Creates a {@code CustomAlert}.
     *
     * @throws IOException
     */
    public CustomAlert(Alert.AlertType alertType, String headerText, ButtonType... buttons) throws IOException {
        this.alertType = alertType;

        setTitle(switch (alertType) {
            case INFORMATION -> "Info";
            case WARNING -> "Warning";
            case ERROR -> "Error";
            case CONFIRMATION -> "Confirm";
            case NONE -> null;
        });
        setResizable(false);

        CustomDialogView dialogPane = new CustomDialogView();
        dialogPane.expandedProperty().addListener((_, _, isExpanded) -> {
            Window window = getOwner();
            if (window != null) {
                window.sizeToScene();
            }
        });
        dialogPane.getButtonTypes().addAll(buttons);
        dialogPane.setHeaderText(headerText);
        setDialogPane(dialogPane);
    }

    public Alert.AlertType getAlertType() {
        return alertType;
    }

    public ObservableList<Text> getContent() {
        return content.get();
    }

    public void setContent(ObservableList<Text> newContent) {
        content.set(newContent);
    }

    public ListProperty<Text> contentProperty() {
        return content;
    }

    public ObservableList<Text> getDetails() {
        return details;
    }

    public void setDetails(ObservableList<Text> newDetails) {
        details.set(newDetails);
    }

    public ListProperty<Text> detailsProperty() {
        return details;
    }
}
