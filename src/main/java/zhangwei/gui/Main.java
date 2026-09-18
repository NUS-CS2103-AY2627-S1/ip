package zhangwei.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import zhangwei.ZhangWei;
import zhangwei.ui.Ui;

/**
 * The JavaFX application: it builds the window from
 * {@code /view/MainWindow.fxml} and hands the chatbot to its controller.
 */
public class Main extends Application {

    /** How small the window may be dragged before it stops being usable. */
    private static final double MIN_WIDTH = 400.0;

    /** How short the window may be dragged before it stops being usable. */
    private static final double MIN_HEIGHT = 500.0;

    /** Where the tasks are loaded from and saved to. */
    private static final String SAVE_FILE_PATH = "./data/zhangwei.txt";

    private final ZhangWei zhangWei = new ZhangWei(SAVE_FILE_PATH);

    /**
     * Shows the chat window.
     *
     * @param stage the window JavaFX has already created for this application.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();

            // The controller is given the chatbot only after loading, because
            // FXMLLoader is the one that creates it.
            MainWindow controller = loader.getController();
            controller.setZhangWei(zhangWei);

            stage.setScene(new Scene(root));
            stage.setTitle(Ui.NAME);
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/DaZhangWei.png")));
            stage.setMinWidth(MIN_WIDTH);
            stage.setMinHeight(MIN_HEIGHT);
            stage.show();
        } catch (IOException e) {
            // The FXML is packaged with the program, so a failure here is a
            // broken build rather than anything the user can put right.
            throw new IllegalStateException("Could not load the chat window.", e);
        }
    }
}
