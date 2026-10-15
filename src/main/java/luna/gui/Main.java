package luna.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import luna.Luna;

/**
 * Displays Luna's main JavaFX window.
 */
public class Main extends Application {

    private final Luna luna = new Luna();

    /**
     * Loads the main-window layout and displays it on the supplied stage.
     *
     * @param stage Primary stage supplied by JavaFX.
     * @throws IOException If the main FXML window cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        VBox mainLayout = fxmlLoader.load();
        Scene scene = new Scene(mainLayout);

        stage.setTitle("Luna");
        stage.setScene(scene);
        stage.setResizable(false);
        MainWindow controller = fxmlLoader.getController();
        controller.setLuna(luna);
        controller.showWelcomeMessage();
        stage.show();
    }
}
