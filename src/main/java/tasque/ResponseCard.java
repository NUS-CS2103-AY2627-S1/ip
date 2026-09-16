package tasque;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Displays a Tasque result with an optional submitted command above it.
 */
public class ResponseCard extends VBox {
    @FXML
    private Label command;
    @FXML
    private HBox resultSection;
    @FXML
    private Label responseText;
    @FXML
    private ImageView tasqueAvatar;

    private ResponseCard(TasqueResponse response, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(ResponseCard.class.getResource("/view/ResponseCard.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the response-card view.", e);
        }

        this.responseText.setText(response.getText());
        this.tasqueAvatar.setImage(image);
        if (response.isError()) {
            this.resultSection.getStyleClass().add("error-result");
        }
    }

    /**
     * Creates a card pairing the original user command with its result.
     *
     * @param command Original command text, including any surrounding spaces.
     * @param response Formatted Tasque response and its error status.
     * @param image Image representing Tasque.
     * @return Card containing the command and its result.
     */
    public static ResponseCard createExchange(String command, TasqueResponse response, Image image) {
        ResponseCard card = new ResponseCard(response, image);
        card.command.setText(command);
        return card;
    }

    /**
     * Creates a standalone result card without a command strip.
     *
     * @param response Formatted Tasque response and its error status.
     * @param image Image representing Tasque.
     * @return Result card suitable for the startup greeting.
     */
    public static ResponseCard createStandalone(TasqueResponse response, Image image) {
        ResponseCard card = new ResponseCard(response, image);
        card.command.setVisible(false);
        card.command.setManaged(false);
        card.resultSection.getStyleClass().add("standalone-result");
        return card;
    }
}
