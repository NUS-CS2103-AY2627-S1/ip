package min.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import min.note.Note;

class NoteListTest {
    @Test
    void constructor_inputListMutated_doesNotChangeNoteList() {
        Note note = new Note("watch Dune");
        List<Note> originalNotes = new ArrayList<>();
        originalNotes.add(note);
        NoteList noteList = new NoteList(originalNotes);

        originalNotes.clear();

        assertEquals(List.of(note), noteList.getNotes());
    }

    @Test
    void addNote_whileShowingAllNotes_appearsInDisplayedView() {
        NoteList noteList = new NoteList(List.of());

        noteList.addNote(new Note("watch Dune"));

        assertEquals(1, noteList.size());
        assertEquals(1, noteList.getDisplayedNoteCount());
    }

    @Test
    void addNote_whileShowingMatches_isHiddenFromDisplayedView() {
        NoteList noteList = new NoteList(List.of(new Note("watch Dune")));
        noteList.showMatchingNotes("Dune");

        noteList.addNote(new Note("read chapter 4"));

        assertEquals(2, noteList.size());
        assertEquals(1, noteList.getDisplayedNoteCount());
    }

    @Test
    void showMatchingNotes_partialMatch_returnsMatchesOnly() {
        Note match = new Note("read chapter 4");
        Note nonMatch = new Note("watch Dune");
        NoteList noteList = new NoteList(List.of(match, nonMatch));

        assertEquals(List.of(match), noteList.showMatchingNotes("read"));
    }

    @Test
    void showMatchingNotes_noMatches_returnsEmptyList() {
        NoteList noteList = new NoteList(List.of(new Note("watch Dune")));

        assertTrue(noteList.showMatchingNotes("nothing").isEmpty());
        assertEquals(0, noteList.getDisplayedNoteCount());
    }

    @Test
    void deleteNote_afterShowingMatches_removesDisplayedNote() {
        Note nonMatch = new Note("watch Dune");
        Note match = new Note("read chapter 4");
        NoteList noteList = new NoteList(List.of(nonMatch, match));
        noteList.showMatchingNotes("read");

        assertSame(match, noteList.deleteNote(0));
        assertEquals(List.of(nonMatch), noteList.getNotes());
        assertEquals(0, noteList.getDisplayedNoteCount());
    }

    @Test
    void showAllNotes_afterShowingMatches_restoresFullView() {
        Note firstNote = new Note("watch Dune");
        Note secondNote = new Note("read chapter 4");
        NoteList noteList = new NoteList(List.of(firstNote, secondNote));
        noteList.showMatchingNotes("read");

        assertEquals(List.of(firstNote, secondNote), noteList.showAllNotes());
        assertEquals(2, noteList.getDisplayedNoteCount());
    }
}
