package duchess.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** A reusable JavaFX header containing the application title, status, and Help button. */
public class Header extends HBox {
    @FXML
    private Button helpButton;

    @FXML
    private Label statusLabel;

    /** Action to run when the Help button is pressed. */
    private Runnable helpAction = () -> { };

    /** Loads the header layout from its FXML view. */
    public Header() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Header.class.getResource("/view/Header.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException | NullPointerException exception) {
            throw new IllegalStateException("Unable to load the header resource.", exception);
        }
    }

    /** Runs the configured Help action when the FXML button is pressed. */
    @FXML
    private void handleHelp() {
        helpAction.run();
    }

    /**
     * Sets the action to run when the Help button is pressed.
     *
     * @param helpAction the action to run
     */
    public void setHelpAction(Runnable helpAction) {
        this.helpAction = helpAction;
    }

    /**
     * Sets the status text and its JavaFX CSS style class.
     *
     * @param status the text to display
     * @param styleClass the status style class to apply
     */
    public void updateStatus(String status, String styleClass) {
        statusLabel.getStyleClass().removeAll(
                "status-ready", "status-success", "status-warning", "status-muted");
        statusLabel.getStyleClass().add(styleClass);
        statusLabel.setText(status);
    }

    /**
     * Enables or disables the Help button.
     *
     * @param disabled whether the button should be disabled
     */
    public void setHelpDisabled(boolean disabled) {
        helpButton.setDisable(disabled);
    }
}
