package larry.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import larry.task.TaskList;

/**
 * Tests Larry's user-facing responses for important empty and warning states.
 */
class UiTest {
    @Test
    void showEmptyResults_personalitySpecificExplanationsDisplayed() {
        ArrayList<String> outputLines = new ArrayList<>();
        Ui ui = new Ui(outputLines::add, ignored -> { });

        ui.showTasks(new TaskList());
        ui.showMatchingTasks(List.of());
        ui.showTasksOnDate(LocalDate.of(2026, 9, 9), new TaskList());

        assertEquals(List.of(
                "     EVIL LARRY's task vault is empty. Enjoy your freedom while it lasts.",
                "     EVIL LARRY found nothing. Try a less pathetic keyword.",
                "     EVIL LARRY found no tasks on 9 Sep 2026. Your reprieve is temporary."
        ), outputLines);
    }

    @Test
    void showWarning_corruptedDataWarningUsesLarrysVoice() {
        ArrayList<String> warningLines = new ArrayList<>();
        Ui ui = new Ui(ignored -> { }, warningLines::add);

        ui.showWarning("Rejected line 2: unknown task type 'X'");

        assertEquals(List.of(
                "WARNING: EVIL LARRY rejected corrupted task data. "
                        + "Rejected line 2: unknown task type 'X'"
        ), warningLines);
    }
}
