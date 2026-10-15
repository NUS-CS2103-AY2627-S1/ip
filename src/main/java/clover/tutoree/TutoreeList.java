package clover.tutoree;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Stores tutorees and provides lookup operations for task linking.
 */
public class TutoreeList {
    private final ArrayList<Tutoree> tutorees;

    /** Creates an empty tutoree list. */
    public TutoreeList() {
        tutorees = new ArrayList<>();
    }

    /** Creates a tutoree list containing a copy of the supplied tutorees. */
    public TutoreeList(List<Tutoree> tutorees) {
        assert tutorees != null : "A tutoree list must have a backing collection.";
        this.tutorees = new ArrayList<>(tutorees);
    }

    /** Adds a tutoree to this list. */
    public void add(Tutoree tutoree) {
        assert tutoree != null : "A tutoree list must not contain null tutorees.";
        tutorees.add(tutoree);
    }

    /** Removes and returns the most recently added tutoree. */
    public Tutoree removeLast() {
        return tutorees.removeLast();
    }

    /** Returns a tutoree whose name matches the supplied name, ignoring case. */
    public Optional<Tutoree> findExactName(String name) {
        return tutorees.stream()
                .filter(tutoree -> tutoree.getName().equalsIgnoreCase(name.trim()))
                .findFirst();
    }

    /** Returns tutorees whose names contain the supplied keyword, ignoring case. */
    public List<Tutoree> findNameContaining(String keyword) {
        String lowercaseKeyword = keyword.toLowerCase(Locale.ROOT);
        return tutorees.stream()
                .filter(tutoree -> tutoree.getName().toLowerCase(Locale.ROOT).contains(lowercaseKeyword))
                .toList();
    }

    /** Returns the number of tutorees in this list. */
    public int size() {
        return tutorees.size();
    }

    /** Returns an unmodifiable snapshot of the tutorees in this list. */
    public List<Tutoree> asList() {
        return List.copyOf(tutorees);
    }
}
