package joebiden.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import joebiden.JoeBiden;
import joebiden.exception.JoeBidenException;
import joebiden.parser.Parser;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private JoeBiden joeBiden;

    private final Image userImage =
            new Image(getClass().getResourceAsStream(
                    "/images/rubberducky.png"
            ));

    private final Image joeBidenImage =
            new Image(getClass().getResourceAsStream(
                    "/images/bidenblast.png"
            ));

    /**
     * Initializes the GUI and keeps the scroll pane
     * scrolled to the latest dialog.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null
                : "scrollPane should be injected by FXML";
        assert dialogContainer != null
                : "dialogContainer should be injected by FXML";
        scrollPane.vvalueProperty().bind(
                dialogContainer.heightProperty()
        );
    }

    /**
     * Injects the JoeBiden chatbot instance.
     *
     * @param joeBiden Joe Biden chatbot instance
     */
    public void setJoeBiden(JoeBiden joeBiden) {
        this.joeBiden = joeBiden;

        dialogContainer.getChildren().add(
                DialogBox.getJoeBidenDialog(
                        JoeBiden.getWelcomeBanner(),
                        joeBidenImage
                )
        );

        String reminders = joeBiden.getTomorrowReminders();

        if (!reminders.isEmpty()) {
            dialogContainer.getChildren().add(
                    DialogBox.getJoeBidenDialog(
                            reminders,
                            joeBidenImage
                    )
            );
        }
    }

    /**
     * Processes and displays the user's input.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = joeBiden.getResponse(input);

        DialogBox responseDialog;

        if (response.startsWith("ERROR!")) {
            responseDialog = DialogBox.getErrorDialog(
                    response,
                    joeBidenImage
            );
        } else {
            responseDialog = DialogBox.getJoeBidenDialog(
                    response,
                    joeBidenImage
            );
        }

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                responseDialog
        );

        userInput.clear();

        try {
            String command = Parser.getCommand(input);
            String arguments = Parser.getArguments(input);

            if (command.equals("bye")) {
                Parser.validateNoArguments(command, arguments);

                Platform.exit();
            }
        } catch (JoeBidenException e) {
            // getResponse() already displays the error
        }
    }
}
