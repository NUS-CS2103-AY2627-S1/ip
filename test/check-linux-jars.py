#!/usr/bin/env python3
"""Check release artifacts, including native CPU types, without requiring a Linux host."""
from pathlib import Path
import struct
from zipfile import ZipFile


ROOT = Path(__file__).resolve().parents[1]


def check_jar(architecture, machine):
    """Check the launcher, dependencies, resources, and ELF headers of one release."""
    path = ROOT / f'build/libs/stewie-linux-{architecture}.jar'
    with ZipFile(path) as jar:
        names = jar.namelist()
        assert len(names) == len(set(names)), 'Duplicate archive entries'
        manifest = jar.read('META-INF/MANIFEST.MF').decode()
        assert 'Main-Class: stewie.ui.gui.Launcher' in manifest, manifest
        assert 'Enable-Native-Access: ALL-UNNAMED' in manifest, manifest
        for required in [
            'stewie/ui/gui/Launcher.class', 'javafx/application/Application.class',
            'com/sun/glass/ui/gtk/GtkApplication.class', 'javafx/scene/control/Control.class',
            'javafx/fxml/FXMLLoader.class', 'images/stewie_photo.png',
            'stewie/ui/gui/instagram.css', 'libglassgtk3.so', 'libprism_sw.so',
            'libjavafx_font.so', 'libjavafx_iio.so',
        ]:
            assert required in names, f'Missing {required}'
        assert not any(n.endswith(('.dylib', '.dll')) for n in names), 'Wrong OS natives'
        native_files = [n for n in names if n.endswith('.so')]
        assert native_files, 'Missing Linux native libraries'
        for name in native_files:
            header = jar.read(name)[:20]
            assert header[:4] == b'\x7fELF' and header[4] == 2, f'Not 64-bit ELF: {name}'
            byte_order = '<' if header[5] == 1 else '>'
            actual_machine = struct.unpack(byte_order + 'H', header[18:20])[0]
            assert actual_machine == machine, f'Wrong CPU type in {name}: {actual_machine}'
        services = jar.read('META-INF/services/org.kordamp.ikonli.IkonHandler').decode()
        assert 'org.kordamp.ikonli.feather.FeatherIkonHandler' in services, services
        print(f'PASS: {path.name}; launcher, JavaFX, resources, icons, '
              f'{len(native_files)} native libraries ({architecture})')


if __name__ == '__main__':
    # ELF e_machine values: EM_X86_64 = 62 and EM_AARCH64 = 183.
    for architecture, machine in [('x64', 62), ('aarch64', 183)]:
        check_jar(architecture, machine)
