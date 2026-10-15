package food;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import food.exception.FoodStorageException;

/** Entry point of the JavaFX GUI, which loads the chat window from FXML. */
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            Foodbot foodbot = new Foodbot(new Ui());
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            fxmlLoader.<MainWindow>getController().setFoodbot(foodbot);

            Scene scene = new Scene(root);
            // All colours and fonts live in the stylesheet, so the look can change without
            // touching Java code.
            scene.getStylesheets().add(Main.class.getResource("/css/kitchen.css").toExternalForm());

            stage.setTitle("Chef Food's Kitchen");
            stage.setScene(scene);
            stage.show();
        } catch (IOException | FoodStorageException e) {
            e.printStackTrace();
        }
    }
}
