package sel;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sel.gui.MainWindow;

/** JavaFX application for Sel. */
public class Main extends Application {

    /** Fraction of the screen's width the window takes up on startup. */
    private static final double WIDTH_FRACTION = 0.5;

    /** Fraction of the screen's height the window takes up on startup. */
    private static final double HEIGHT_FRACTION = 2.0 / 3.0;

    /** Smallest sizes that still keep the chat and input bar usable. */
    private static final double MIN_WIDTH = 360;
    private static final double MIN_HEIGHT = 420;

    private final Sel sel = new Sel("data/sel.txt");

    /**
     * Loads and displays the main JavaFX window.
     *
     * @param stage the primary application stage.
     * @throws IOException if the FXML file cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/view/MainWindow.fxml"));
        Parent root = fxmlLoader.load();

        MainWindow controller = fxmlLoader.getController();
        controller.setSel(sel);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Sel");
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);

        sizeAndCentreOnScreen(stage);

        stage.show();
    }

    /**
     * Sizes the window relative to the screen and places it in the middle
     * of the screen. The window stays resizable (JavaFX stages are
     * resizable by default); these values only set the starting size.
     *
     * <p>{@code getVisualBounds} is used rather than {@code getBounds} so
     * the window is centred within the space left by the menu bar and
     * dock/taskbar, not the whole physical screen.
     *
     * @param stage the window to size and position.
     */
    private void sizeAndCentreOnScreen(Stage stage) {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        double width = Math.max(MIN_WIDTH, screen.getWidth() * WIDTH_FRACTION);
        double height = Math.max(MIN_HEIGHT, screen.getHeight() * HEIGHT_FRACTION);

        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(screen.getMinX() + (screen.getWidth() - width) / 2);
        stage.setY(screen.getMinY() + (screen.getHeight() - height) / 2);
    }
}
