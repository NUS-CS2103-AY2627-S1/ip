package yuki.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import yuki.Yuki;

/**
 * Displays Yuki's JavaFX user interface.
 */
public class Main extends Application {
    /** Generates responses to commands entered through the GUI. */
    private final Yuki yuki = Yuki.createGuiInstance();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setYuki(yuki);

            stage.setScene(new Scene(mainLayout));
            stage.setTitle("Yuki");
            stage.getIcons().add(new Image(
                    Main.class.getResourceAsStream("/images/YukiLogo.png")));
            stage.setMinHeight(220.0);
            stage.setMinWidth(417.0);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the main window.", e);
        }
    }
}
