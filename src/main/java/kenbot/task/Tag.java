package kenbot.task;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import kenbot.KenbotException;

/**
 * A label attached to a task, such as {@code #fun}.
 *
 * <p>A tag is a value: two tags with the same name are the same tag, which is
 * what lets a task hold them in a {@link Set} and ignore a repeat without any
 * checking of its own.</p>
 *
 * <p>The name is stored without its {@code #}. The hash is punctuation the user
 * types and the program prints, not part of what the tag <em>is</em>, so it is
 * added back only in {@link #toString()}.</p>
 */
public class Tag {

    /** Shown whenever a tag cannot be made from what the user typed. */
    private static final String USAGE_MESSAGE =
            "Tags are one word, no spaces. Like: #fun";

    private final String name;

    /**
     * Creates a tag with the given name.
     *
     * <p>Private so that every tag has to come from {@link #of}, which means one
     * can never exist with a name that could not be written to the save file or
     * read back.</p>
     *
     * @param name the tag name, without its {@code #}
     */
    private Tag(String name) {
        this.name = name;
    }

    /**
     * Creates a tag from text the user typed or the save file holds.
     *
     * <p>One leading {@code #} is accepted and removed, so {@code tag 2 fun} and
     * {@code tag 2 #fun} both work. Any further characters are kept as they are,
     * apart from the two the program cannot store: a space would split one tag
     * into two, and a bar is the save file's field separator.</p>
     *
     * @param name the tag as typed, with or without a leading {@code #}
     * @return the new tag
     * @throws KenbotException if the name is blank, holds a space, or holds a bar
     */
    public static Tag of(String name) throws KenbotException {
        if (name == null || name.isBlank()) {
            throw new KenbotException(USAGE_MESSAGE);
        }

        String wanted = name.trim();
        if (wanted.startsWith("#")) {
            wanted = wanted.substring(1);
        }

        if (wanted.isBlank()) {
            throw new KenbotException("Need a name after the '#', like: #fun");
        }
        // Checked even though Parser rejects a bar in the whole line: tags are
        // also built from the save file, which never passes through Parser.
        if (wanted.contains("|")) {
            throw new KenbotException("Can't use '|' in a tag, that's what"
                    + " splits the fields in my save file.");
        }
        if (wanted.chars().anyMatch(Character::isWhitespace)) {
            throw new KenbotException(USAGE_MESSAGE);
        }

        return new Tag(wanted);
    }

    /**
     * Returns the tag name without its {@code #}, as stored in the save file.
     *
     * @return the bare tag name
     */
    public String getName() {
        return name;
    }

    /**
     * Splits trailing tags off the text typed after a command word.
     *
     * <p>Only {@code #} words at the <em>end</em> of the text are tags, so
     * {@code read book #fun} is a book with a tag while {@code read #1 book}
     * keeps the {@code #1} in its description. Scanning backwards is what makes
     * that distinction: the run of tags stops at the first word that is not one.</p>
     *
     * <p>Kept here rather than in each task type so that {@code todo},
     * {@code deadline} and {@code event} cannot drift apart on what counts as a
     * tag. It also runs before {@code /by} and {@code /from} are looked for, so
     * those methods never see a tag at all.</p>
     *
     * @param argument the text typed after the command word
     * @return the text with its trailing tags removed, and those tags
     * @throws KenbotException if a trailing {@code #} word is not a usable tag
     */
    public static TaggedText splitTrailingTags(String argument) throws KenbotException {
        String[] words = argument.trim().split("\\s+");

        int firstTag = words.length;
        while (firstTag > 0 && words[firstTag - 1].startsWith("#")) {
            firstTag--;
        }

        // Read left to right so the tags keep the order the user typed them.
        Set<Tag> tags = new LinkedHashSet<>();
        for (int i = firstTag; i < words.length; i++) {
            tags.add(Tag.of(words[i]));
        }

        String text = String.join(" ", Arrays.copyOfRange(words, 0, firstTag));
        return new TaggedText(text, tags);
    }

    /**
     * Text with its trailing tags taken off.
     *
     * @param text what is left once the trailing tags are removed
     * @param tags the tags that were removed, in the order they were typed
     */
    public record TaggedText(String text, Set<Tag> tags) { }

    /**
     * Returns whether two tags are the same tag.
     *
     * <p>Compared by name, so a task holding these in a {@link Set} ignores a
     * repeat without having to look for one. Upper and lower case count as
     * different, so {@code #Fun} and {@code #fun} are two tags.</p>
     *
     * @param other the object to compare with
     * @return true if the other object is a tag with the same name
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Tag)) {
            return false;
        }
        return name.equals(((Tag) other).name);
    }

    /**
     * Returns a hash based on the name, so that equal tags hash alike.
     *
     * @return the hash of this tag
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Returns this tag as it should be shown on screen.
     *
     * @return the name with its {@code #} put back
     */
    @Override
    public String toString() {
        return "#" + name;
    }
}
