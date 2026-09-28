package nicola;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Nicola using FXML.
 */
public class Main extends Application {

    private final Nicola nicola = new Nicola();

    @Override
    public void start(Stage stage) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));

            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            Image background = new Image(getClass().getResource(
                    "/images/nicola-background.jpg").toExternalForm());
            ap.widthProperty().addListener((observable, oldValue, newValue) ->
                    centerBackgroundOnFace(ap, background));
            ap.heightProperty().addListener((observable, oldValue, newValue) ->
                    centerBackgroundOnFace(ap, background));

            stage.setScene(scene);
            stage.setTitle("Nicola Francesca");
            stage.setMinWidth(360);
            stage.setMinHeight(400);
            stage.getIcons().add(new Image(getClass().getResource(
                    "/images/app-icon.jpg").toExternalForm()));
            fxmlLoader.<MainWindow>getController().setNicola(nicola);  // inject the Duke instance
            stage.show();
    }

    /**
     * Fills the window with minimal zoom and positions the face above centre
     * where possible, clamping the image position to avoid empty edges.
     *
     * @param pane the window's root pane
     * @param image the background used to calculate its natural proportions
     */
    private void centerBackgroundOnFace(AnchorPane pane, Image image) {
        double faceX = image.getWidth() * 0.51;
        double faceY = image.getHeight() * 0.32;
        double scale = Math.max(
                pane.getWidth() / image.getWidth(),
                pane.getHeight() / image.getHeight());
        double width = image.getWidth() * scale;
        double height = image.getHeight() * scale;
        double left = Math.max(pane.getWidth() - width,
                Math.min(0, pane.getWidth() / 2 - faceX * scale));
        double top = Math.max(pane.getHeight() - height,
                Math.min(0, pane.getHeight() * 0.4 - faceY * scale));
        pane.setStyle("-fx-background-size: " + width + "px " + height + "px;"
                + "-fx-background-position: left " + left + "px top " + top + "px;");
    }
}
