package potatobot.ui.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import potatobot.logic.PotatoBot;

/**
 * Starts PotatoBot's JavaFX frontend.
 */
public class GuiApplication extends Application {
    private static final double MINIMUM_WIDTH = 417;
    private static final double MINIMUM_HEIGHT = 220;

    private final PotatoBot potatoBot = new PotatoBot();

    /**
     * Creates and displays the initial PotatoBot window.
     *
     * @param stage Primary JavaFX stage supplied by the runtime.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                GuiApplication.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        MainWindow controller = fxmlLoader.getController();
        controller.setPotatoBot(potatoBot);

        Scene scene = new Scene(root);

        stage.setTitle("PotatoBot");
        stage.setMinWidth(MINIMUM_WIDTH);
        stage.setMinHeight(MINIMUM_HEIGHT);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> potatoBot.shutdown());
        stage.show();
    }
}
