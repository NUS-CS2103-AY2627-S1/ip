package murphy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/** Tests Murphy's UI-independent command processing. */
public class MurphyServiceTest {
    @Test
    public void respond_addsAndListsTodo() throws Exception {
        MurphyService service = new MurphyService(Files.createTempDirectory("murphy-test").resolve("tasks.txt"));

        String response = service.respond("todo submit assignment");

        assertTrue(response.contains("I've added this task"));
        assertEquals(1, service.getTasks().size());
        assertTrue(service.respond("list").contains("submit assignment"));
    }

    @Test
    public void respond_marksAndFindsTask() throws Exception {
        MurphyService service = new MurphyService(Files.createTempDirectory("murphy-test").resolve("tasks.txt"));
        service.respond("todo Read JavaFX guide");

        assertTrue(service.respond("find javafx").contains("Read JavaFX guide"));
        service.respond("mark 1");
        assertTrue(service.getTasks().get(0).isDone());
    }

    @Test
    public void respond_rejectsInvalidCommandArgument() throws Exception {
        MurphyService service = new MurphyService(Files.createTempDirectory("murphy-test").resolve("tasks.txt"));

        assertTrue(service.respond("deadline report /by not-a-date").startsWith("OOPS!"));
        assertEquals(0, service.getTasks().size());
    }

    @Test
    public void respond_rejectsDuplicateTodoIgnoringCaseAndStatus() throws Exception {
        MurphyService service = new MurphyService(Files.createTempDirectory("murphy-test").resolve("tasks.txt"));

        service.respond("todo Buy milk");
        service.respond("mark 1");

        String response = service.respond("todo   buy milk");

        assertEquals("OOPS! This task is already in your list:\n[T][X] Buy milk"
                + "\nI kept the existing task and did not add a duplicate.", response);
        assertEquals(1, service.getTasks().size());
    }

    @Test
    public void respond_allowsTasksWithDifferentTypeOrDetails() throws Exception {
        MurphyService service = new MurphyService(Files.createTempDirectory("murphy-test").resolve("tasks.txt"));

        service.respond("todo submit report");
        service.respond("deadline submit report /by 2026-09-10");
        service.respond("deadline submit report /by 2026-09-11");

        assertEquals(3, service.getTasks().size());
    }

    @Test
    public void respond_rejectsRepeatedEventMarkers() throws Exception {
        MurphyService service = new MurphyService(Files.createTempDirectory("murphy-test").resolve("tasks.txt"));

        String response = service.respond("event meeting /from 2pm /from 3pm /to 4pm");

        assertTrue(response.startsWith("OOPS!"));
        assertEquals(0, service.getTasks().size());
    }
}
