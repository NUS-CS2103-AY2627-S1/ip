package dog;

import java.io.IOException;

import dog.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Main entry point for the Dog application.
 */
public class Main extends Application {
    private static final String APP_NAME = "Dog";
    private static final Dog dog = new Dog();

    private final Image appIcon = new Image(this.getClass().getResourceAsStream("/images/AvatarDog.jpg"));

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setMinHeight(640);
            stage.setMinWidth(480);
            stage.setTitle(APP_NAME);
            stage.getIcons().add(appIcon); // set window icon to dog image
            MainWindow controller = fxmlLoader.<MainWindow>getController();
            controller.setDog(dog); // inject the Dog instance
            controller.sendWelcomeMessage(); // display greeting on start
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
