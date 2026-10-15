package jimbo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link Jimbo#getResponse}, focused on the "update" command. This is
 * the only place the routing logic that rejects a field flag (e.g.
 * {@code /by}) against a task type it doesn't apply to (e.g. a Todo) is
 * exercised, so it is covered directly rather than only through its
 * collaborators.
 */
public class JimboTest {
    @TempDir
    private Path tempDir;

    private Jimbo newJimbo() {
        return new Jimbo(tempDir.resolve("jimbo.txt").toString());
    }

    @Test
    public void update_descOnTodo_updatesDescription() {
        Jimbo jimbo = newJimbo();
        jimbo.getResponse("todo read book");

        String response = jimbo.getResponse("update 1 /desc read a different book").text();

        assertTrue(response.contains("read a different book"));
        assertTrue(jimbo.getResponse("list").text().contains("read a different book"));
    }

    @Test
    public void update_byOnDeadline_updatesByAndKeepsDescription() {
        Jimbo jimbo = newJimbo();
        jimbo.getResponse("deadline submit report /by 2/12/2019 1800");

        String response = jimbo.getResponse("update 1 /by 3/12/2019 0900").text();

        assertTrue(response.contains("submit report"));
        assertTrue(response.contains("Dec 03 2019"));
    }

    @Test
    public void update_fromAndToOnEvent_updatesBothIndependently() {
        Jimbo jimbo = newJimbo();
        jimbo.getResponse("event team meeting /from 2/12/2019 1400 /to 2/12/2019 1600");

        jimbo.getResponse("update 1 /from 2/12/2019 1000");
        String response = jimbo.getResponse("update 1 /to 2/12/2019 1100").text();

        assertTrue(response.contains("10:00AM"));
        assertTrue(response.contains("11:00AM"));
    }

    @Test
    public void update_byOnTodo_returnsError() {
        Jimbo jimbo = newJimbo();
        jimbo.getResponse("todo read book");

        Jimbo.Response response = jimbo.getResponse("update 1 /by 3/12/2019 0900");

        assertTrue(response.isError());
        assertTrue(response.text().startsWith("Yuck!"));
    }

    @Test
    public void update_fromOnDeadline_returnsError() {
        Jimbo jimbo = newJimbo();
        jimbo.getResponse("deadline submit report /by 2/12/2019 1800");

        Jimbo.Response response = jimbo.getResponse("update 1 /from 3/12/2019 0900");

        assertTrue(response.isError());
        assertTrue(response.text().startsWith("Yuck!"));
    }

    @Test
    public void update_unknownFlag_returnsError() {
        Jimbo jimbo = newJimbo();
        jimbo.getResponse("todo read book");

        Jimbo.Response response = jimbo.getResponse("update 1 /nope something");

        assertTrue(response.isError());
        assertTrue(response.text().startsWith("Yuck!"));
    }

    @Test
    public void update_missingValue_returnsError() {
        Jimbo jimbo = newJimbo();
        jimbo.getResponse("todo read book");

        Jimbo.Response response = jimbo.getResponse("update 1 /desc");

        assertTrue(response.isError());
        assertTrue(response.text().startsWith("Yuck!"));
    }

    @Test
    public void update_invalidIndex_returnsError() {
        Jimbo jimbo = newJimbo();
        jimbo.getResponse("todo read book");

        Jimbo.Response response = jimbo.getResponse("update 99 /desc new description");

        assertTrue(response.isError());
        assertTrue(response.text().startsWith("Yuck!"));
    }

    @Test
    public void update_invalidDateFormat_returnsError() {
        Jimbo jimbo = newJimbo();
        jimbo.getResponse("deadline submit report /by 2/12/2019 1800");

        Jimbo.Response response = jimbo.getResponse("update 1 /by tomorrow");

        assertTrue(response.isError());
        assertTrue(response.text().startsWith("Yuck!"));
    }
}
