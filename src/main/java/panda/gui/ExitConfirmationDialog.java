package panda.gui;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.stage.Window;

/**
 * Displays the graphical confirmation used to complete Panda's exit flow.
 */
public final class ExitConfirmationDialog extends Dialog<Boolean> {
    @FXML
    private Label messageLabel;

    /**
     * Creates an exit-confirmation dialog containing the supplied save result.
     *
     * @param owner Window that owns the dialog, or {@code null} when unavailable.
     * @param message Save result and confirmation text to display.
     */
    public ExitConfirmationDialog(Window owner, String message) {
        loadDialogPane();
        messageLabel.setText(Objects.requireNonNull(message));
        setTitle("End Training");
        setHeaderText("Getting out of here?");
        if (owner != null) {
            initOwner(owner);
        }
        configureButtons();
    }

    /**
     * Shows the dialog and waits for the user to confirm or cancel termination.
     *
     * @return Whether the user selected the exit option.
     */
    public boolean showAndWaitForConfirmation() {
        return showAndWait().orElse(false);
    }

    /**
     * Loads the dialog layout and injects its controls into this controller.
     */
    private void loadDialogPane() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ExitConfirmationDialog.class.getResource("/view/ExitConfirmationDialog.fxml"));
            fxmlLoader.setController(this);
            DialogPane dialogPane = fxmlLoader.load();
            setDialogPane(dialogPane);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the exit confirmation dialog.", exception);
        }
        assert messageLabel != null : "FXML must inject messageLabel";
    }

    /**
     * Adds exit and cancel buttons and configures their keyboard behavior.
     */
    private void configureButtons() {
        ButtonType confirmButtonType = new ButtonType(
                "LET ME OUT!", ButtonBar.ButtonData.OK_DONE);
        ButtonType goBackButtonType = new ButtonType(
                "Oops no", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().setAll(confirmButtonType, goBackButtonType);

        Button confirmButton = (Button) getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDefaultButton(true);
        Button goBackButton = (Button) getDialogPane().lookupButton(goBackButtonType);
        goBackButton.setCancelButton(true);
        setResultConverter(buttonType -> buttonType == confirmButtonType);
    }
}
