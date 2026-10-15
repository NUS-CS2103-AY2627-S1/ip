package catgpt;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays CatGPT's graphical interface using JavaFX and FXML.
 */
public class Main extends Application {
    private final CatGPT chatbot = new CatGPT();

    /**
     * Loads and displays the main CatGPT window.
     *
     * @param stage Primary JavaFX stage.
     * @throws IOException If the FXML layout cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

        MainWindow controller = loader.getController();
        controller.setChatbot(chatbot);

        stage.setTitle("CatGPT");
        stage.setMinWidth(520);
        stage.setMinHeight(640);
        stage.setWidth(720);
        stage.setHeight(760);
        stage.setScene(scene);
        stage.show();
    }
}
