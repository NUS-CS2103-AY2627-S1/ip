package probe.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TagTest {

    @Test
    void addingAndDeletingTagUsesTagName() {
        Todo todo = new Todo("Read a book");

        todo.addTag("#fun");
        todo.deleteTag("fun");

        assertEquals(0, todo.getTags().size());
    }

    @Test
    void addingDuplicateTagKeepsOneTag() {
        Todo todo = new Todo("Read a book");

        todo.addTag("fun", "#fun");

        assertEquals(1, todo.getTags().size());
    }

    @Test
    void emptyTagNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Tag("#"));
    }

    @Test
    void specialCharactersInTagNameAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Tag("#fun!"));
    }

    @Test
    void tagsWithSameNameAreEqual() {
        assertEquals(new Tag("#fun"), new Tag("fun"));
    }

    @Test
    void clearTagsRemovesAllTags() {
        Todo todo = new Todo("Read a book");
        todo.addTag("fun", "weekend");

        todo.clearTags();

        assertEquals(0, todo.getTags().size());
    }
}
