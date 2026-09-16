package serangooner.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.jupiter.api.Test;

public class SoundPlayerTest {
    // Only the disabled player is exercised: an enabled one would make a
    // sound on the machine running the tests.

    @Test
    public void play_disabled_staysSilentWithoutFailing() {
        assertDoesNotThrow(() -> new SoundPlayer(false).play());
    }

    @Test
    public void play_disabledAndCalledRepeatedly_staysSilentWithoutFailing() {
        SoundPlayer player = new SoundPlayer(false);
        assertDoesNotThrow(() -> {
            player.play();
            player.play();
            player.play();
        });
    }

    @Test
    public void soundResource_packagedWithTheProgram_decodesAsAudio()
            throws IOException, UnsupportedAudioFileException {
        // The player swallows every failure, so a sound Java cannot decode would
        // otherwise go unnoticed until someone listened for it.
        InputStream resource = SoundPlayer.class.getResourceAsStream(SoundPlayer.SOUND_PATH);
        assertNotNull(resource);

        try (AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(resource))) {
            assertTrue(audio.getFrameLength() > 0);
        }
    }
}
