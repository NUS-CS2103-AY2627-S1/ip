package xiaozhi.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import xiaozhi.XiaoZhi;

/**
 * A JavaFX GUI for {@link XiaoZhi}, loaded from {@code /view/MainWindow.fxml}.
 */
public class Main extends Application {
    private static final String SAVE_FILE_PATH = "./data/xiaozhi.txt";
    private static final double MIN_WINDOW_WIDTH = 320.0;
    private static final double MIN_WINDOW_HEIGHT = 420.0;

    private final XiaoZhi xiaoZhi = new XiaoZhi(SAVE_FILE_PATH);

    /**
     * Builds and shows the main window, injecting the {@link XiaoZhi} instance
     * this app was started with into its controller.
     * <p>
     * The window is resizable (the JavaFX default); a minimum size is set so
     * the layout never gets squeezed smaller than it can lay out sensibly.
     * {@code Stage.setMinWidth}/{@code setMinHeight} constrain the window's
     * outer frame, chrome (title bar) included, but {@link #MIN_WINDOW_WIDTH}
     * and {@link #MIN_WINDOW_HEIGHT} match the content-only minimums declared
     * on the FXML root -- so the Stage minimum has to be padded out by
     * however much chrome the platform actually adds, or the enforced floor
     * ends up a title-bar's-worth of pixels too small and the bottom-most
     * region (the input bar) gets clipped by the Scene's own edge well
     * before the "minimum" size is reached. That padding is measured after
     * {@link Stage#show()}, once both the Stage and the Scene have real,
     * laid-out sizes, rather than assumed, since it varies by platform.
     *
     * @param stage The primary stage provided by the JavaFX runtime.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Master Zhi");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/XiaoZhi.png")));
            fxmlLoader.<MainWindow>getController().setXiaoZhi(xiaoZhi);
            stage.show();

            double chromeWidth = stage.getWidth() - scene.getWidth();
            double chromeHeight = stage.getHeight() - scene.getHeight();
            stage.setMinWidth(MIN_WINDOW_WIDTH + chromeWidth);
            stage.setMinHeight(MIN_WINDOW_HEIGHT + chromeHeight);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load the GUI layout.", e);
        }
    }
}
