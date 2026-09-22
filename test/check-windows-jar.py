#!/usr/bin/env python3
"""Check the Windows release and native CPU types without requiring a Windows host."""
from pathlib import Path
import struct
from zipfile import ZipFile


ROOT = Path(__file__).resolve().parents[1]


def check_jar():
    """Check the launcher, Windows JavaFX classes, resources, and x64 PE libraries."""
    path = ROOT / 'build/libs/stewie-windows-x64.jar'
    with ZipFile(path) as jar:
        names = jar.namelist()
        assert len(names) == len(set(names)), 'Duplicate archive entries'
        manifest = jar.read('META-INF/MANIFEST.MF').decode()
        assert 'Main-Class: stewie.ui.gui.Launcher\n' in manifest.replace('\r\n', '\n'), manifest
        assert 'Enable-Native-Access: ALL-UNNAMED' in manifest, manifest
        for required in [
            'stewie/ui/gui/Launcher.class', 'stewie/ui/gui/StewieApplication.class',
            'javafx/application/Application.class', 'com/sun/glass/ui/win/WinApplication.class',
            'javafx/scene/control/Control.class', 'javafx/fxml/FXMLLoader.class',
            'images/stewie_photo.png', 'stewie/ui/gui/instagram.css',
            'glass.dll', 'prism_d3d.dll', 'prism_sw.dll', 'javafx_font.dll', 'javafx_iio.dll',
        ]:
            assert required in names, f'Missing {required}'
        assert not any(n.endswith(('.dylib', '.so')) for n in names), 'Wrong OS natives'
        assert not any(n.startswith(('com/sun/glass/ui/mac/', 'com/sun/glass/ui/gtk/'))
                       for n in names), 'Wrong OS JavaFX classes'
        assert not any(n.endswith('module-info.class') for n in names), 'Unexpected module descriptor'
        native_files = [n for n in names if n.endswith('.dll')]
        for name in native_files:
            data = jar.read(name)
            assert data[:2] == b'MZ', f'Not a Windows executable: {name}'
            pe_offset = struct.unpack_from('<I', data, 0x3c)[0]
            assert data[pe_offset:pe_offset + 4] == b'PE\0\0', f'Not PE: {name}'
            machine = struct.unpack_from('<H', data, pe_offset + 4)[0]
            assert machine == 0x8664, f'Not x86-64 in {name}: {machine:#x}'
            magic = struct.unpack_from('<H', data, pe_offset + 24)[0]
            assert magic == 0x20b, f'Not PE32+ in {name}: {magic:#x}'
        services = jar.read('META-INF/services/org.kordamp.ikonli.IkonHandler').decode()
        assert 'org.kordamp.ikonli.feather.FeatherIkonHandler' in services, services
        print(f'PASS: {path.name}; launcher, Windows JavaFX, resources, icons, '
              f'{len(native_files)} native libraries (x64)')


if __name__ == '__main__':
    check_jar()
