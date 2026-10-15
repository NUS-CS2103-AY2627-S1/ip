package amigobot;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for AmigoBot using FXML.
 */
public class Main extends Application {

    private AmigoBot amigoBot = new AmigoBot();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("AmigoBot - Your Friendly Capybara Assistant");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/DaBot.png")));
            fxmlLoader.<MainWindow>getController().setAmigoBot(amigoBot);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
