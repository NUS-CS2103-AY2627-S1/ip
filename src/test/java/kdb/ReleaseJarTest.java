package kdb;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.junit.jupiter.api.Test;

/** Checks the actual release JAR without relying on the project's runtime classpath. */
class ReleaseJarTest {
    private static final List<String> PLATFORMS = List.of("win", "linux", "mac", "mac-aarch64");

    @Test
    void releaseJar_platformFactories_loadFromJar() throws Exception {
        URL jarUrl = Path.of(System.getProperty("kdb.releaseJar")).toUri().toURL();
        try (URLClassLoader loader = new URLClassLoader(new URL[] {jarUrl},
                ClassLoader.getPlatformClassLoader())) {
            for (String name : List.of("javafx.application.Application", "javafx.fxml.FXMLLoader",
                    "javafx.scene.control.Control", "com.sun.glass.ui.win.WinPlatformFactory",
                    "com.sun.glass.ui.gtk.GtkPlatformFactory", "com.sun.glass.ui.mac.MacPlatformFactory")) {
                Class<?> type = Class.forName(name, false, loader);
                assertSame(loader, type.getClassLoader(), "Runtime masked a missing bundled class: " + name);
                assertEquals(jarUrl, type.getProtectionDomain().getCodeSource().getLocation());
            }
        }
    }

    @Test
    void releaseJar_linuxGraphicsAndFonts_loadFromJar() throws Exception {
        URL jarUrl = Path.of(System.getProperty("kdb.releaseJar")).toUri().toURL();
        try (URLClassLoader loader = new URLClassLoader(new URL[] {jarUrl},
                ClassLoader.getPlatformClassLoader())) {
            // Loading without initialization checks linkage without executing Linux native code on macOS.
            for (String name : List.of("com.sun.glass.ui.gtk.GtkApplication",
                    "com.sun.glass.ui.gtk.GtkWindow", "com.sun.glass.ui.gtk.GtkView",
                    "com.sun.prism.es2.X11GLFactory", "com.sun.prism.es2.X11GLContext",
                    "com.sun.prism.es2.X11GLDrawable", "com.sun.prism.sw.SWPipeline",
                    "com.sun.javafx.font.freetype.FTFactory", "com.sun.javafx.font.freetype.OSFreetype",
                    "com.sun.javafx.font.freetype.OSPango")) {
                Class<?> type = Class.forName(name, false, loader);
                assertSame(loader, type.getClassLoader(), "Linux class not loaded from the JAR: " + name);
                assertEquals(jarUrl, type.getProtectionDomain().getCodeSource().getLocation());
            }
        }
    }

    @Test
    void releaseJar_allPlatformClassesAndResources_areIncluded() throws IOException {
        try (JarFile release = new JarFile(System.getProperty("kdb.releaseJar"))) {
            for (String platform : PLATFORMS) {
                try (JarFile graphics = new JarFile(System.getProperty("kdb.graphics." + platform))) {
                    for (ZipEntry entry : graphics.stream().toList()) {
                        String name = entry.getName();
                        if (!entry.isDirectory() && (name.startsWith("com/") || name.startsWith("javafx/"))) {
                            assertNotNull(release.getEntry(name), platform + " is missing " + name);
                        }
                    }
                }
            }
        }
    }

    @Test
    void releaseJar_nativeLibraries_preserveEachPlatformBinary() throws IOException {
        try (JarFile release = new JarFile(System.getProperty("kdb.releaseJar"))) {
            for (String platform : PLATFORMS) {
                int nativeCount = 0;
                try (JarFile graphics = new JarFile(System.getProperty("kdb.graphics." + platform))) {
                    for (ZipEntry entry : graphics.stream().toList()) {
                        String name = entry.getName();
                        if (isNativeLibrary(name)) {
                            nativeCount++;
                            ZipEntry bundled = release.getEntry("kdb/natives/" + platform + "/" + name);
                            assertNotNull(bundled, platform + " is missing " + name);
                            try (var expected = graphics.getInputStream(entry);
                                    var actual = release.getInputStream(bundled)) {
                                assertArrayEquals(expected.readAllBytes(), actual.readAllBytes(),
                                        platform + "/" + name);
                            }
                        }
                    }
                }
                assertTrue(nativeCount > 0, "No native libraries checked for " + platform);
            }
            assertFalse(release.stream().anyMatch(entry -> !entry.getName().contains("/")
                    && isNativeLibrary(entry.getName())), "Conflicting root native libraries must be absent");
        }
    }

    private static boolean isNativeLibrary(String name) {
        return name.endsWith(".dll") || name.endsWith(".so") || name.endsWith(".dylib");
    }
}
