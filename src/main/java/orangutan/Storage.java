package orangutan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import orangutan.chatlist.ChatList;
import orangutan.chatlist.ListItem;
import orangutan.command.SortMethod;

/**
 * Handles storing and loading of list data to and from file.
 */
public class Storage {
    /**
     * Reads new ChatList from save file.
     *
     * @throws IOException If program does not have read access to the directory.
     */
    public ChatList readFromFile(Path filePath) throws IOException {
        assert filePath != null : "Storage says: filePath should not be null!!!";

        ChatList list = new ChatList();

        if (Files.exists(filePath)) {
            List<String> lines = Files.readAllLines(filePath);
            SortMethod method = SortMethod.valueOf(lines.get(0));
            lines.stream()
                    .skip(1)
                    .forEach(line -> list.addItem(ListItem.parseLine(line)));
            list.setSorting(method);

        }

        return list;
    }

    /**
     * Writes ChatList to a save file.
     * First line of the save file is the sorting method, followed by all list items.
     * If file already exists, overwrites current contents.
     * If file and/or directory does not exist, creates a new one at the given path.
     *
     * @throws IOException If program does not have write access.
     */
    public void writeToFile(Path filePath, ChatList list) throws IOException {

        String toWrite = list.getSortMethod().name() + "\n";
        toWrite += list.toStream().map(item -> item.toFile()).collect(Collectors.joining("\n"));

        Files.createDirectories(filePath.getParent()); // create directory structure (does nothing if already exists)
        Files.writeString(filePath, toWrite); // write to file (creates file if does not exist)
    }
}
