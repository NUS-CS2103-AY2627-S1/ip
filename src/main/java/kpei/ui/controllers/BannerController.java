package kpei.ui.controllers;

import java.io.InputStream;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Controller for the Banner component displayed at the top-left of the application.
 */
public class BannerController {

    private static final String BANNER_IMAGE_PATH = "/kpei/images/banner.png";

    @FXML
    private StackPane bannerContainer;

    @FXML
    private ImageView bannerImageView;

    /**
     * Initializes the banner component, attempting to load the custom banner image if present.
     */
    @FXML
    public void initialize() {
        InputStream imageStream = getClass().getResourceAsStream(BANNER_IMAGE_PATH);
        if (imageStream != null) {
            bannerImageView.setImage(new Image(imageStream));
        }
    }
}
