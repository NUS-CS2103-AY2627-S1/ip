package stephen.helloworld;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/** Handles interaction in the isolated JavaFX setup-verification view. */
public class HelloWorldController {
    @FXML
    private Label messageLabel;

    /** Updates the label to prove that FXML event wiring and controller injection work. */
    @FXML
    private void handleVerification() {
        messageLabel.setText("FXML, controller, and CSS loaded successfully.");
    }
}
