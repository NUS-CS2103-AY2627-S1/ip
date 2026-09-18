package com.fireinyu.themyth.ui.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Unit tests for {@link SparkleAnimator}.
 */
public class SparkleAnimatorTest {

    @Test
    public void constants_haveValidConfiguration() {
        assertTrue(SparkleAnimator.PARTICLE_COUNT > 0);
        assertTrue(SparkleAnimator.SPARKLE_GLYPHS.length > 0);
        assertTrue(SparkleAnimator.THE_MYTH_SPARKLE_COLORS.length > 0);
        assertTrue(SparkleAnimator.USER_SPARKLE_COLORS.length > 0);

        for (String glyph : SparkleAnimator.SPARKLE_GLYPHS) {
            assertNotNull(glyph);
            assertFalse(glyph.isBlank());
        }

        for (String colorHex : SparkleAnimator.THE_MYTH_SPARKLE_COLORS) {
            Color color = Color.web(colorHex);
            assertNotNull(color);
        }

        for (String colorHex : SparkleAnimator.USER_SPARKLE_COLORS) {
            Color color = Color.web(colorHex);
            assertNotNull(color);
        }
    }

    @Test
    public void createSparkleParticle_createsUnmanagedMouseTransparentNode() {
        String[] palette = SparkleAnimator.THE_MYTH_SPARKLE_COLORS;
        List<String> allowedGlyphs = Arrays.asList(SparkleAnimator.SPARKLE_GLYPHS);
        List<String> allowedColors = Arrays.asList(palette);

        for (int i = 0; i < 20; i++) {
            Text particle = SparkleAnimator.createSparkleParticle(palette);
            assertNotNull(particle);
            assertFalse(particle.isManaged(), "Sparkle particle must be unmanaged");
            assertTrue(particle.isMouseTransparent(), "Sparkle particle must be mouse transparent");
            assertEquals(0.1, particle.getScaleX(), 0.001);
            assertEquals(0.1, particle.getScaleY(), 0.001);
            assertEquals(0.0, particle.getOpacity(), 0.001);

            assertTrue(allowedGlyphs.contains(particle.getText()),
                    "Particle text must be one of the allowed sparkle glyphs");

            String style = particle.getStyle();
            boolean matchesPalette = allowedColors.stream().anyMatch(style::contains);
            assertTrue(matchesPalette, "Particle style should contain a color from the palette: " + style);
        }
    }

    @Test
    public void play_withNullDialogBox_doesNotThrow() {
        SparkleAnimator.play(null, true);
        SparkleAnimator.play(null, false);
    }
}
