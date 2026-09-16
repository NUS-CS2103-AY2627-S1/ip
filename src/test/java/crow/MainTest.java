package crow;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import crow.ui.DialogBox;
import crow.ui.MainWindow;
import javafx.application.Application;
import javafx.scene.layout.HBox;

class MainTest {
    @Test
    void main_isJavaFxApplication() {
        assertTrue(Application.class.isAssignableFrom(Main.class));
    }

    @Test
    void launcher_hasPublicStaticMainMethod() throws NoSuchMethodException {
        Method mainMethod = Launcher.class.getMethod("main", String[].class);

        assertTrue(Modifier.isPublic(mainMethod.getModifiers()));
        assertTrue(Modifier.isStatic(mainMethod.getModifiers()));
    }

    @Test
    void dialogBox_isReusableHBoxControl() {
        assertTrue(HBox.class.isAssignableFrom(DialogBox.class));
    }

    @Test
    void dialogBox_hasUserAndCrowFactoryMethods() throws NoSuchMethodException {
        Method userFactory = DialogBox.class.getMethod(
                "createUserDialog", String.class);
        Method crowFactory = DialogBox.class.getMethod(
                "createCrowDialog", String.class);

        assertTrue(Modifier.isStatic(userFactory.getModifiers()));
        assertTrue(Modifier.isStatic(crowFactory.getModifiers()));
    }

    @Test
    void mainWindow_hasUserInputHandler() throws NoSuchMethodException {
        assertNotNull(MainWindow.class.getDeclaredMethod("handleUserInput"));
    }

    @Test
    void mainWindow_byeCommand_isRecognizedAsExitCommand() throws ReflectiveOperationException {
        Method isExitCommand = MainWindow.class.getDeclaredMethod("isExitCommand", String.class);
        isExitCommand.setAccessible(true);

        assertTrue((boolean) isExitCommand.invoke(null, "bye"));
        assertTrue((boolean) isExitCommand.invoke(null, "  BYE  "));
        assertFalse((boolean) isExitCommand.invoke(null, "bye now"));
    }

    @Test
    void stylesheet_isAvailableOnClasspath() {
        assertNotNull(Main.class.getResource("/view/styles.css"));
    }

    @Test
    void fxmlViews_areAvailableOnClasspath() {
        assertNotNull(Main.class.getResource("/view/MainWindow.fxml"));
        assertNotNull(Main.class.getResource("/view/DialogBox.fxml"));
    }
}
