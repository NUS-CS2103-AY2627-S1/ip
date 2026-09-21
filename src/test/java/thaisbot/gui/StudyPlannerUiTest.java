package thaisbot.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import thaisbot.task.Deadline;
import thaisbot.task.TaskList;
import thaisbot.task.Todo;

public class StudyPlannerUiTest {
    @Test
    public void welcomeAndErrorMessages_useNewPersona() {
        StudyPlannerUi ui = new StudyPlannerUi();

        ui.showWelcome();
        ui.showError("bad command");

        assertEquals(List.of(
                "Hello! I'm Thai's Bot. Let's make the chaos manageable.",
                "Bring me tasks, deadlines, events, and tags.",
                "Oops — Thai's Bot got tangled up: bad command"
        ), ui.drainMessages());
    }

    @Test
    public void showMatchingTasks_keepsFullListNumbers() {
        StudyPlannerUi ui = new StudyPlannerUi();
        TaskList tasks = new TaskList();
        tasks.add(new Todo("alpha"));
        tasks.add(new Todo("beta"));
        tasks.add(new Deadline("beta report", LocalDateTime.of(2026, 9, 18, 0, 0), false));

        ui.showMatchingTasks(tasks, "beta");
        ui.showTasksOnDate(tasks, LocalDate.of(2026, 9, 18));

        assertEquals(List.of(
                "Here are the matches I found:",
                "2.[T][ ] beta",
                "3.[D][ ] beta report (by: Sep 18 2026)",
                "Let's check what's on 2026-09-18:",
                "3.[D][ ] beta report (by: Sep 18 2026)"
        ), ui.drainMessages());
    }
}
