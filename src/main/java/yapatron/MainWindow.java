package yapatron;

import java.net.URL;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.text.Font;

/**
 * A Controller for MainWindow
 * Provides the layout for the other controls
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
    @FXML
    private ImageView backgroundImage;

    @FXML
    private ImageView bannerImage;

    @FXML
    private Label bannerTitle;

    @FXML
    private Label bannerSubtitle;

    private Yapatron yapatron;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/1077114.png"));

    private Image yapatronImage = new Image(
            getClass().getResourceAsStream("/images/happyhamster.png"));

    private Image errorImage = new Image(
            getClass().getResourceAsStream(
                "/images/mac-jones-september-11.png"));

    private Image skyWallpaper =
        new Image(getClass().getResourceAsStream(
                    "/images/skyWallpaperIp.jpg"));

    private Image yapatronBannerImage =
        new Image(getClass().getResourceAsStream(
                    "/images/Yapatron.png"));


    private static final String FONT = loadMinecraftFont();

    /**
     * Loads the bundled Minecraft font.
     *
     * @return the loaded font family name
     */
    private static String loadMinecraftFont() {
        URL fontUrl = MainWindow.class.getResource("/fonts/Minecraft.ttf");

        if (fontUrl == null) {
            throw new IllegalStateException("Minecraft.ttf was not found");
        }

        Font font = Font.loadFont(fontUrl.toExternalForm(), 13);

        if (font == null) {
            throw new IllegalStateException("Minecraft.ttf could not be loaded");
        }

        return font.getFamily();
    }

    /**
     * Gets the font family used by the application.
     *
     * @return Minecraft font family
     */
    public static String getFont() {
        return FONT;
    }


    @FXML
    public void initialize() {

        backgroundImage.setImage(skyWallpaper);
        backgroundImage.setPreserveRatio(false);

        AnchorPane root = (AnchorPane) backgroundImage.getParent();

        backgroundImage.fitWidthProperty().bind(root.widthProperty());
        backgroundImage.fitHeightProperty().bind(root.heightProperty());

        bannerImage.setImage(yapatronBannerImage);

        bannerTitle.setStyle(
                "-fx-font-family: '" + FONT + "';"
                + "-fx-font-size: 42px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #000000;");

        bannerSubtitle.setStyle(
                "-fx-font-family: '" + FONT + "';"
                + "-fx-font-size: 22px;"
                + "-fx-font-style: italic;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #607D8B;");


        scrollPane.vvalueProperty().
            bind(dialogContainer.
                    heightProperty());

        scrollPane.setStyle("-fx-background-color: transparent;"
                + "-fx-background-insets: 0;"
                + "-fx-padding: 0;"
                );

        scrollPane.skinProperty().addListener(
                (observable, oldSkin, newSkin) -> {
                    if (newSkin != null) {
                        Platform.runLater(() -> {
                            Node viewport = scrollPane.lookup(".viewport");

                            if (viewport != null) {
                                viewport.setStyle(
                                        "-fx-background-color: transparent;"
                                        + "-fx-background-insets: 0;"
                                        );
                            }
                        });
                    }
                });

        String controlStyle = "-fx-font-family: '" + FONT + "';"
            + "-fx-font-size: 16px;";

        String sendButtonStyle = controlStyle
            + "-fx-background-color: #1976D2;"
            + "-fx-text-fill: white;"
            + "-fx-border-color: transparent;"
            + "-fx-background-radius: 0;";

        userInput.setStyle(controlStyle);
        sendButton.setStyle(sendButtonStyle);    
    }

    public void setYapatron(Yapatron y) {
        this.yapatron = y;
        dialogContainer.getChildren().add(
                DialogBox.getYapatronDialog("Hello hellooooo"
                    + "!!! My name is Yapatron :D What's the haps for todayayayay?", 
                    yapatronImage));
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = yapatron.getResponse(input);
        Image responseImage = yapatron.wasLastResponseAnError()
            ? errorImage
            : yapatronImage;

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getYapatronDialog(response, responseImage)
                );
        userInput.clear();

        if (input.trim().equalsIgnoreCase("bye")) {
            javafx.application.Platform.exit();
        }
    }
}




