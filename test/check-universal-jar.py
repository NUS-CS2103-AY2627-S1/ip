#!/usr/bin/env python3
"""Inspect every runtime inside the universal release without loading foreign native code."""
from io import BytesIO
from pathlib import Path
import struct
from zipfile import ZipFile


ROOT = Path(__file__).resolve().parents[1]
PLATFORMS = {
    'windows-x64': ('win/WinApplication', '.dll', 0x8664),
    'linux-x64': ('gtk/GtkApplication', '.so', 62),
    'linux-aarch64': ('gtk/GtkApplication', '.so', 183),
    'mac-x64': ('mac/MacApplication', '.dylib', 0x1000007),
    'mac-aarch64': ('mac/MacApplication', '.dylib', 0x100000c),
}


def native_machine(data, suffix):
    """Read the native machine type directly from a PE, ELF, or Mach-O header."""
    if suffix == '.dll':
        assert data[:2] == b'MZ', 'Missing DOS signature'
        offset = struct.unpack_from('<I', data, 0x3c)[0]
        assert data[offset:offset + 4] == b'PE\0\0', 'Missing PE signature'
        assert struct.unpack_from('<H', data, offset + 24)[0] == 0x20b, 'Not PE32+'
        return struct.unpack_from('<H', data, offset + 4)[0]
    if suffix == '.so':
        assert data[:4] == b'\x7fELF' and data[4] == 2, 'Not 64-bit ELF'
        return struct.unpack_from(('<' if data[5] == 1 else '>') + 'H', data, 18)[0]
    assert data[:4] in (b'\xcf\xfa\xed\xfe', b'\xfe\xed\xfa\xcf'), 'Not 64-bit Mach-O'
    return struct.unpack_from(('<' if data[:4] == b'\xcf\xfa\xed\xfe' else '>') + 'I', data, 4)[0]


def check_runtime(jar, platform):
    """Check platform classes, dependencies, UI resources, services, and every native library."""
    application, suffix, machine = PLATFORMS[platform]
    names = jar.namelist()
    assert len(names) == len(set(names)), f'{platform}: duplicate entries'
    manifest = jar.read('META-INF/MANIFEST.MF').decode().replace('\r\n', '\n')
    assert 'Main-Class: stewie.ui.gui.Launcher\n' in manifest, manifest
    assert 'Enable-Native-Access: ALL-UNNAMED\n' in manifest, manifest
    for name in [
        'stewie/ui/gui/Launcher.class', 'stewie/ui/gui/StewieApplication.class',
        'javafx/beans/Observable.class', 'javafx/application/Application.class',
        'javafx/scene/control/Control.class', 'javafx/fxml/FXMLLoader.class',
        'com/sun/glass/ui/' + application + '.class',
        'org/kordamp/ikonli/javafx/FontIcon.class', 'org/kordamp/ikonli/feather/Feather.class',
        'images/stewie_photo.png', 'stewie/ui/gui/instagram.css',
    ]:
        assert name in names, f'{platform}: missing {name}'
    for other in {value[0] for value in PLATFORMS.values()} - {application}:
        assert 'com/sun/glass/ui/' + other + '.class' not in names, f'{platform}: foreign platform classes'
    natives = [name for name in names if name.endswith(('.dll', '.so', '.dylib'))]
    expected = {
        '.dll': ['glass.dll', 'prism_d3d.dll', 'prism_sw.dll', 'javafx_font.dll', 'javafx_iio.dll'],
        '.so': ['libglassgtk3.so', 'libprism_sw.so', 'libjavafx_font.so', 'libjavafx_iio.so'],
        '.dylib': ['libglass.dylib', 'libprism_sw.dylib', 'libjavafx_font.dylib', 'libjavafx_iio.dylib'],
    }[suffix]
    for name in expected:
        assert name in natives, f'{platform}: missing {name}'
    for name in natives:
        assert name.endswith(suffix), f'{platform}: foreign OS library {name}'
        assert native_machine(jar.read(name), suffix) == machine, f'{platform}: wrong CPU in {name}'
    services = jar.read('META-INF/services/org.kordamp.ikonli.IkonHandler').decode()
    assert 'org.kordamp.ikonli.feather.FeatherIkonHandler' in services, services
    print(f'PASS: {platform}; launcher, JavaFX, icons, resources, {len(natives)} native libraries')


def check_universal():
    """Verify the size limit and reconstruct all five runtimes from shared files and overlays."""
    path = ROOT / 'build/libs/stewie.jar'
    assert path.stat().st_size <= 12_000_000, 'Universal JAR exceeds 12 MB'
    with ZipFile(path) as jar:
        names = jar.namelist()
        assert len(names) == len(set(names)), 'Duplicate outer entries'
        manifest = jar.read('META-INF/MANIFEST.MF').decode().replace('\r\n', '\n')
        assert 'Main-Class: stewie.launcher.UniversalLauncher\n' in manifest, manifest
        assert 'stewie/launcher/UniversalLauncher.class' in names, 'Missing bootstrap'
        assert not any(name.startswith('javafx/') for name in names), 'Unisolated JavaFX classes'
        assert not any(name.endswith(('.dll', '.so', '.dylib')) for name in names), 'Unisolated natives'
        for platform in PLATFORMS:
            resource = f'platforms/stewie-{platform}.jar'
            reconstructed = BytesIO()
            with ZipFile(reconstructed, 'w') as output:
                for part in ('platforms/common.jar', resource):
                    with ZipFile(BytesIO(jar.read(part))) as payload:
                        for name in payload.namelist():
                            output.writestr(name, payload.read(name))
            with ZipFile(reconstructed) as runtime:
                check_runtime(runtime, platform)
                with ZipFile(path.parent / f'stewie-{platform}.jar') as original:
                    expected = {n: original.read(n) for n in original.namelist() if not n.endswith('/')}
                    actual = {n: runtime.read(n) for n in runtime.namelist()}
                    assert actual == expected, f'{platform}: reconstructed runtime differs from original'
                print(f'PASS: {platform}; every reconstructed file matches the complete platform JAR')
    print(f'PASS: {path.name}; all five runtimes isolated ({path.stat().st_size:,} bytes)')


if __name__ == '__main__':
    check_universal()
