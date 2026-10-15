package cooper.note;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import cooper.exception.CooperException;

/** Immutable note collection; mutations produce candidates that can be saved before publication. */
public class NoteList {
    private final List<Note> notes;

    /** Creates a snapshot in creation order. */
    public NoteList(List<Note> notes) {
        this.notes = List.copyOf(notes);
    }

    public int size() {
        return notes.size();
    }

    public List<Note> asList() {
        return notes;
    }

    /** Looks up a current one-based number. */
    public Note get(int number) {
        if (number < 1 || number > notes.size()) {
            throw new CooperException("I couldn't find a note with that number :(");
        }
        return notes.get(number - 1);
    }

    /** Returns a candidate with the new note appended. */
    public NoteList add(Note note) {
        List<Note> candidate = new ArrayList<>(notes);
        candidate.add(note);
        return new NoteList(candidate);
    }

    /** Returns a candidate with replacement text at the same position. */
    public NoteList edit(int number, Note note) {
        get(number);
        List<Note> candidate = new ArrayList<>(notes);
        candidate.set(number - 1, note);
        return new NoteList(candidate);
    }

    /** Returns a candidate with later notes renumbered after deletion. */
    public NoteList delete(int number) {
        get(number);
        List<Note> candidate = new ArrayList<>(notes);
        candidate.remove(number - 1);
        return new NoteList(candidate);
    }

    /** Returns original one-based numbers matching a case-insensitive literal phrase. */
    public List<Integer> find(String keyword) {
        String normalized = keyword.toLowerCase(Locale.ROOT);
        return IntStream.range(0, notes.size())
                .filter(i -> notes.get(i).text().toLowerCase(Locale.ROOT).contains(normalized))
                .map(i -> i + 1).boxed().toList();
    }
}
