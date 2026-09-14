package arthur;

import java.nio.file.Path;

import arthur.state.CommandContext;
import arthur.state.Storage;

/**
 * Storage double that keeps executor tests independent from the filesystem.
 */
public class InMemoryStorage extends Storage {
    private CommandContext context = new CommandContext();

    public InMemoryStorage() {
        super(Path.of("unused.bin"));
    }

    // AI-assisted: Keep executor test state in memory.
    @Override
    public CommandContext load() {
        return context;
    }

    @Override
    public void save(CommandContext context) {
        this.context = context;
    }
}
