package surveyprogram.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import surveyprogram.object.TaskList;

/** Tests that the command loop reads commands in order and stops at exit. */
class CommandLoopTest {
    @Test
    void run_endOfInput_stopsNormally() {
        for (String text : List.of("", "list")) {
            RecordingProcessor processor = new RecordingProcessor();
            new CommandLoop(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), processor).run();
            assertEquals(text.isEmpty() ? List.of() : List.of("list"), processor.inputs);
        }
    }

    @Test
    void run_commandsEndingInBye_processesEachCommandExactlyOnce() {
        RecordingProcessor processor = new RecordingProcessor();
        ByteArrayInputStream input = new ByteArrayInputStream(
                "list\nmark 1\nbye\nignored\n".getBytes(StandardCharsets.UTF_8));

        new CommandLoop(input, processor).run();

        assertEquals(List.of("list", "mark 1", "bye"), processor.inputs);
    }

    private static class RecordingProcessor extends TaskCommandProcessor {
        private final ArrayList<String> inputs = new ArrayList<>();

        RecordingProcessor() {
            super(new TaskList(0));
        }

        @Override
        public CommandResult process(String input) {
            inputs.add(input);
            return new CommandResult("", !input.equals("bye"));
        }
    }
}
