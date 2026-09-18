package plana.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import plana.Plana;

/**
 * Starts Plana's FXML-based JavaFX interface.
 */
public class Main extends Application {
    private static final String APP_TITLE = "Plana • Your Task Buddy";

    /**
     * Loads and displays Plana's main window.
     *
     * @param stage the primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            MainWindow controller = fxmlLoader.getController();
            controller.setPlana(new Plana());

            Scene scene = new Scene(mainWindow);
            stage.setTitle(APP_TITLE);
            stage.getIcons().add(createAppIcon());
            stage.setScene(scene);
            stage.setMinHeight(380);
            stage.setMinWidth(520);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Plana's GUI.", exception);
        }
    }

    /**
     * Creates Plana's green circular application icon with a white letter P.
     *
     * @return generated application icon.
     */
    private Image createAppIcon() {
        Canvas canvas = new Canvas(64, 64);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.web("#e8fff1"));
        graphics.fillRect(0, 0, 64, 64);
        graphics.setFill(Color.web("#38a978"));
        graphics.fillOval(4, 4, 56, 56);
        graphics.setFill(Color.WHITE);
        graphics.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.setTextBaseline(VPos.CENTER);
        graphics.fillText("P", 32, 32);
        return canvas.snapshot(new SnapshotParameters(), null);
    }
}
