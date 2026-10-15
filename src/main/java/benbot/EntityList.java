package benbot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/** Owns an ordered collection of one type of non-task entity. */
final class EntityList<T extends StorableEntity> {
    /** The entities in the order in which the user added them. */
    private final List<T> entities = new ArrayList<>();

    /** Adds an entity to the end of this collection. */
    void add(T entity) {
        assert entity != null : "An entity must be provided";
        entities.add(entity);
    }

    /** Removes and returns the entity at the supplied zero-based index. */
    T remove(int index) {
        assert index >= 0 && index < entities.size() : "Only an existing entity can be removed";
        return entities.remove(index);
    }

    /** Returns the number of entities in this collection. */
    int size() {
        return entities.size();
    }

    /** Returns display lines numbered from one in collection order. */
    String[] toNumberedDisplayLines() {
        return IntStream.range(0, entities.size())
                .mapToObj(index -> (index + 1) + "." + entities.get(index))
                .toArray(String[]::new);
    }

    /** Returns commands that recreate every entity in collection order. */
    List<String> toStorageCommands() {
        return entities.stream()
                .map(StorableEntity::toStorageString)
                .toList();
    }

    /** Returns a sequential stream of the entities in collection order. */
    Stream<T> stream() {
        return entities.stream();
    }
}
