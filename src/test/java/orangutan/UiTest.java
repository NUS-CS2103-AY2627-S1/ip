package orangutan;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import orangutan.chatlist.ChatList;
import orangutan.command.Context;
import orangutan.command.Parser;

public class UiTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void welcome_validParser_printsBannerAndInitializesContext() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Context context = new Context();
        context.setFilePath(temporaryFolder.resolve("orangutan.txt"));
        Parser parser = new Parser(context);
        Ui ui = new Ui(new PrintStream(output), new ByteArrayInputStream(new byte[0]));

        ui.welcome(parser);

        String printed = output.toString(StandardCharsets.UTF_8);
        assertTrue(printed.contains("___"));
        assertTrue(printed.contains("Greetings, I am Orangutan."));
        assertTrue(context.isRun());
    }

    @Test
    public void getInput_todoCommand_printsPromptAndResponse() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayInputStream input = new ByteArrayInputStream("todo read book\n".getBytes(StandardCharsets.UTF_8));
        Context context = new Context(new ChatList(), true, temporaryFolder.resolve("orangutan.txt"), new Storage());
        Ui ui = new Ui(new PrintStream(output), input);

        ui.getInput(new Parser(context));

        String printed = output.toString(StandardCharsets.UTF_8);
        assertTrue(printed.startsWith(" > "));
        assertTrue(printed.contains("Hark! A task hath been added to your list:"));
        assertTrue(printed.contains("[T] [ ] read book"));
        assertTrue(printed.contains("___________________________________________________________"));
    }
}
