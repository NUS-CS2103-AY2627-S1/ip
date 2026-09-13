package chimpanzinibananini;

import java.io.IOException;

import chimpanzinibananini.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays the ChimpanziniBananini GUI using FXML.
 */
public class Main extends Application {
    private final ChimpanziniBananini chatbot = new ChimpanziniBananini(ChimpanziniBananini.DATA_FILE);

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainWindow = fxmlLoader.load();
        fxmlLoader.<MainWindow>getController().setChatbot(chatbot);

        stage.setTitle("ChimpanziniBananini");
        stage.setResizable(false);
        stage.setScene(new Scene(mainWindow));
        stage.show();
    }
}
