package tuesday.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Reports fatal interface failures without depending on FXML or conversation controls.
 */
final class UiErrors {
    private static final Logger LOGGER = Logger.getLogger(UiErrors.class.getName());

    private UiErrors() {
    }

    /**
     * Shows a plain error alert and exits after dismissal, retaining the diagnostic cause in the log.
     */
    static void showFatal(String message, Throwable cause) {
        LOGGER.log(Level.SEVERE, message, cause);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Tuesday could not continue");
        alert.setHeaderText(message);
        alert.setContentText("Please resolve the problem and restart Tuesday.");
        try {
            alert.showAndWait();
        } finally {
            Platform.exit();
        }
    }
}
