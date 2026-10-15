package slotbot.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import slotbot.SlotBot;

/**
 * Creates the SlotBot window and connects it to the chatbot.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

        MainWindow controller = loader.getController();
        controller.setBot(new SlotBot(), stage::close);

        stage.setTitle("SlotBot");
        stage.setMinWidth(400);
        stage.setMinHeight(420);
        stage.setScene(scene);
        stage.show();
        controller.focusInput();
    }
}
