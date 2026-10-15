package sanyueqi.ui;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    private SanYueQi syq;

    public MainWindow() throws IOException, URISyntaxException {
    }

    private Path getUserProfileImage() throws IOException, URISyntaxException {
        Path applicationLocation = Paths.get(
                MainWindow.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI()
        );

        Path directory;

        if (Files.isDirectory(applicationLocation)) {
            // Running from IntelliJ / exploded classes
            directory = applicationLocation;
        } else {
            // Running from SanYueQi.jar
            directory = applicationLocation.getParent();
        }

        Path jpg = directory.resolve("user.jpg");
        Path png = directory.resolve("user.png");

        if (Files.exists(jpg)) {
            return jpg;
        }

        if (Files.exists(png)) {
            return png;
        }

        // Neither exists: create the default user.png
        Path defaultImage = directory.resolve("user.png");

        try (InputStream input = MainWindow.class
                .getResourceAsStream("/images/user.png")) {

            if (input == null) {
                throw new IOException("Default profile picture not found.");
            }

            Files.copy(input, defaultImage);
        }

        return defaultImage;
    }

    Path profileImage = getUserProfileImage();

    Image userImage = new Image(profileImage.toUri().toString());

    private Image syqImage =
            new Image(this.getClass().getResourceAsStream("/images/march.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Syq instance */
    public void setSyq(SanYueQi s) {
        syq = s;
    }

    public void makeInitResponseStatusDialog() {
        String initResponseStatus = syq.getInitResponseStatus();
        dialogContainer.getChildren().add(
                DialogBox.getSyqDialog(initResponseStatus, syqImage));
    }

    public void makeFinalResponseStatusDialog() {
        String finalResponseStatus = syq.getFinalResponseStatus();
        dialogContainer.getChildren().add(
                DialogBox.getSyqDialog(finalResponseStatus, syqImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other
     * containing Syq's reply, then appends them to the dialog container.
     * Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() throws InterruptedException {
        String input = userInput.getText();
        String response = syq.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getSyqDialog(response, syqImage)
        );

        if (response.equals("Bye. Hope to see you again soon! ")) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> System.exit(0));
            pause.play();
        }

        userInput.clear();
    }
}