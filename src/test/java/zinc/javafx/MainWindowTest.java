package zinc.javafx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import zinc.ui.BackgroundType;

/** Tests command handling decisions made by the main JavaFX window. */
public class MainWindowTest {
    @Test
    public void isHelpCommand_helpWithWhitespaceCaseAndParameters_returnsTrue() {
        assertTrue(MainWindow.isHelpCommand("help"));
        assertTrue(MainWindow.isHelpCommand("  help  "));
        assertTrue(MainWindow.isHelpCommand("HELP"));
        assertTrue(MainWindow.isHelpCommand("help todo"));
    }

    @Test
    public void isHelpCommand_nonHelpCommands_returnsFalse() {
        assertFalse(MainWindow.isHelpCommand(""));
        assertFalse(MainWindow.isHelpCommand("helpdesk"));
        assertFalse(MainWindow.isHelpCommand("todo help"));
    }

    @Test
    public void isExitCommand_exitWithWhitespaceAndCase_returnsTrue() {
        assertTrue(MainWindow.isExitCommand("bye"));
        assertTrue(MainWindow.isExitCommand("  BYE\t"));
    }

    @Test
    public void isExitCommand_nonExitCommands_returnsFalse() {
        assertFalse(MainWindow.isExitCommand(""));
        assertFalse(MainWindow.isExitCommand("bye now"));
        assertFalse(MainWindow.isExitCommand("byebye"));
    }

    @Test
    public void getBackgroundStyleClass_timesAcrossPeriods_returnsCorrectBackground() {
        assertEquals("background-morning", MainWindow.getBackgroundStyleClass(LocalTime.of(6, 0)));
        assertEquals("background-sunset", MainWindow.getBackgroundStyleClass(LocalTime.of(18, 0)));
        assertEquals("background-night", MainWindow.getBackgroundStyleClass(LocalTime.of(22, 0)));
        assertEquals("background-morning", MainWindow.getBackgroundStyleClass(LocalTime.of(17, 59)));
        assertEquals("background-sunset", MainWindow.getBackgroundStyleClass(LocalTime.of(21, 59)));
        assertEquals("background-night", MainWindow.getBackgroundStyleClass(LocalTime.of(2, 0)));
    }

    @Test
    public void getSidebarStyleClass_timesAcrossPeriods_returnsMatchingSidebarStyle() {
        assertEquals("sidebar-morning", MainWindow.getSidebarStyleClass(LocalTime.of(6, 0)));
        assertEquals("sidebar-sunset", MainWindow.getSidebarStyleClass(LocalTime.of(18, 0)));
        assertEquals("sidebar-night", MainWindow.getSidebarStyleClass(LocalTime.of(22, 0)));
        assertEquals("sidebar-night", MainWindow.getSidebarStyleClass(LocalTime.of(2, 0)));
    }

    @Test
    public void getBackgroundStyleClass_manualSelection_returnsSelectedBackground() {
        LocalTime morning = LocalTime.of(8, 0);

        assertEquals("background-sunset", MainWindow.getBackgroundStyleClass(morning, BackgroundType.EVENING));
        assertEquals("background-night", MainWindow.getBackgroundStyleClass(morning, BackgroundType.NIGHT));
    }

    @Test
    public void getBackgroundStyleClass_autoSelection_usesCurrentTime() {
        assertEquals("background-morning",
                MainWindow.getBackgroundStyleClass(LocalTime.of(8, 0), BackgroundType.AUTO));
    }
}
