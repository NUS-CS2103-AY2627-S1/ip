package walter.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import walter.place.Place;
import walter.task.Deadline;
import walter.task.Task;
import walter.task.Todo;

/**
 * Tests deterministic user-facing output produced by Walter's presentation layer.
 */
public class UiTest {
    private ByteArrayOutputStream outputBytes;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        outputBytes = new ByteArrayOutputStream();
        ui = new Ui(new PrintStream(outputBytes, true, StandardCharsets.UTF_8));
    }

    @Test
    public void showTaskList_emptyAndPopulated_correctOutput() {
        ui.showTaskList(List.of());
        ui.showTaskList(List.of(new Todo("read book")));

        assertEquals("The board is clean. Nothing needs doing.\n"
                + "Here are your tasks:\n"
                + "1. [T][ ] read book\n", getOutput());
    }

    @Test
    public void showFindResults_emptyAndPopulated_correctOutput() {
        Task task = new Todo("read book");

        ui.showFindResults(List.of(task), List.of());
        ui.showFindResults(List.of(new Todo("skip"), task), List.of(1));

        assertEquals("I found no tasks matching that keyword.\n"
                + "Here's what I found:\n"
                + "2. [T][ ] read book\n", getOutput());
    }

    @Test
    public void showDeadlinesOn_emptyAndPopulated_correctDateAndNumbering() {
        LocalDate date = LocalDate.of(2026, 8, 30);
        Deadline deadline = new Deadline("submit report", date);

        ui.showDeadlinesOn(date, List.of());
        ui.showDeadlinesOn(date, List.of(deadline));

        assertEquals("No deadlines are scheduled for Aug 30 2026.\n"
                + "Here is the schedule for Aug 30 2026:\n"
                + "1. [D][ ] submit report (by: Aug 30 2026)\n", getOutput());
    }

    @Test
    public void showPlaces_emptyAndPopulated_correctOutput() {
        Place place = new Place("NUS Library", "12 Computing Drive");

        ui.showPlaces(List.of());
        ui.showPlaces(List.of(place));

        assertEquals("The location list is empty.\n"
                + "Here are the recorded locations:\n"
                + "1. NUS Library — 12 Computing Drive\n", getOutput());
    }

    @Test
    public void showMutationConfirmations_taskAndPlaceDetailsAndCountsIncluded() {
        Todo task = new Todo("read book");
        Place place = new Place("home", "Clementi");

        ui.showAddedTask(task, 1);
        ui.showDeletedTask(task, 0);
        ui.showMarkedTask(task);
        ui.showUnmarkedTask(task);
        ui.showAddedPlace(place, 1);
        ui.showDeletedPlace(place, 0);

        assertEquals("Good. That's on the list now:\n[T][ ] read book\n"
                + "The list now contains 1 task.\n"
                + "Done. I've removed this from the list:\n[T][ ] read book\n"
                + "The list now contains 0 tasks.\n"
                + "Done. Consider this one handled:\n[T][ ] read book\n"
                + "Understood. This goes back into the mix:\n[T][ ] read book\n"
                + "Good. I've recorded this location:\nhome — Clementi\n"
                + "The location list now contains 1 place.\n"
                + "Done. I've removed this location:\nhome — Clementi\n"
                + "The location list now contains 0 places.\n", getOutput());
    }

    @Test
    public void showError_messageRemainsClearAndInCharacter() {
        ui.showError("Task number is out of range.");

        assertEquals("Task number is out of range. Stay sharp, Jesse.\n", getOutput());
    }

    /**
     * Returns all output captured from the output-only UI.
     */
    private String getOutput() {
        return outputBytes.toString(StandardCharsets.UTF_8);
    }
}
