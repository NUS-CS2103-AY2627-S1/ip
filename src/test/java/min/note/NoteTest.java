package min.note;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NoteTest {
    @Test
    void toString_returnsTextOnly() {
        assertEquals("watch Dune", new Note("watch Dune").toString());
    }

    @Test
    void toFileString_returnsTypedRecord() {
        assertEquals("N | watch Dune", new Note("watch Dune").toFileString());
    }

    @Test
    void getText_returnsTextUnchanged() {
        assertEquals("read chapter 4", new Note("read chapter 4").getText());
    }
}
