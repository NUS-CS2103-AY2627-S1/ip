package squirtlebot.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import squirtlebot.SquirtleBot;
import squirtlebot.ui.controller.GuiMainWindow;

/**
 * Starts the GUI for SquirtleBot.
 * Loads the GuiMainWindow layout, initializes SquirtleBot then sends instance of SquirtleBot to controller.
 */
public class GuiMain extends Application {
    private SquirtleBot squirtleBot = new SquirtleBot();

    /**
     * Initializes and displays SquirtleBot GUI, connects controller
     * to application and loads saved tasks.
     *
     * @param stage the primary stage for this application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(GuiMain.class.getResource("/view/GuiMainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);

            stage.setMinWidth(417);
            stage.setMinHeight(220);
            stage.setScene(scene);
            stage.setTitle("SquirtleBot");

            GuiMainWindow controller = fxmlLoader.<GuiMainWindow>getController();
            controller.setSquirtleBot(squirtleBot);

            stage.show();

            controller.displayWelcomeMessage();

            boolean isLoaded = squirtleBot.tryInitializeTasks();
            if (!isLoaded) {
                controller.promptOnStorageIssue();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
