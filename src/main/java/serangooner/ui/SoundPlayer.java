package serangooner.ui;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Plays the funny sound that accompanies a rejected command.
 * The sound is read from among the program's own resources, so it plays the
 * same from the packaged jar as from Gradle, on any platform with an audio
 * device. Playback is a side show: it never delays the next prompt, and is
 * skipped when no one is listening, as in a test, or when the sound cannot
 * be played.
 */
public class SoundPlayer {
    /** Path of the sound among the program's resources, open to tests in this package. */
    static final String SOUND_PATH = "/faaah.wav";

    private final boolean isEnabled;

    /**
     * Constructs a player that may or may not make a sound.
     *
     * @param isEnabled True to play the sound, false to stay silent.
     */
    public SoundPlayer(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * Plays the sound once, and does nothing at all if it cannot be played.
     */
    public void play() {
        if (!isEnabled) {
            return;
        }

        try {
            Clip clip = openClip();
            // Each play opens its own audio line, so give it back once the sound ends.
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.start();
        } catch (IOException | UnsupportedAudioFileException
                | LineUnavailableException | IllegalArgumentException exception) {
            // A missing sound or a machine without audio is no reason to trouble the user.
        }
    }

    /**
     * Returns the sound loaded into a clip that is ready to start.
     * Starting a clip returns at once, with the sound playing in the background.
     *
     * @return Clip holding the whole sound.
     * @throws IOException If the sound is missing or cannot be read.
     * @throws UnsupportedAudioFileException If the sound is in a format Java cannot decode.
     * @throws LineUnavailableException If no audio line is free to play it on.
     * @throws IllegalArgumentException If the machine has no audio device that can play a clip.
     */
    private static Clip openClip()
            throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        InputStream resource = SoundPlayer.class.getResourceAsStream(SOUND_PATH);
        if (resource == null) {
            throw new IOException("missing sound: " + SOUND_PATH);
        }

        // Decoding needs to look ahead and back, which a raw resource stream does not allow.
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(resource))) {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        }
    }
}
