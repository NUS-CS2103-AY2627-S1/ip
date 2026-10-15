package carlo.persona;

/**
 * Something Carlo says, together with the mood he says it in.
 *
 * <p>This is a Java {@code record}: a short way of writing a class that only
 * holds data. Java generates the constructor, the getters {@code text()} and
 * {@code mood()}, and {@code equals}/{@code hashCode}/{@code toString}.
 *
 * @param text the words shown in Carlo's speech bubble
 * @param mood the mood shown around Carlo's photo
 */
public record CarloMessage(String text, CarloMood mood) {
}
