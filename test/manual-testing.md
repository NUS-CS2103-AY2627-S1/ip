# Manual testing record

Date: 2026-09-17. Host: macOS. Java: 25.0.3.fx-zulu.

## Observed results

- The ten isolated console acceptance cases in [ui-test-plan.md](ui-test-plan.md) all passed.
  Their full inputs, actual output, expected output, and exit codes are in
  [ui-test-session.md](ui-test-session.md). These are scripted console checks,
  not claims of GUI or Windows testing.
- The built JavaFX application was launched from a temporary working directory
  on macOS. Its process remained running without terminal output, but its
  window was not exposed to the available desktop inspection interface. The
  process was stopped. No visual GUI result can be asserted from this attempt.

## Pending GUI checks

Run with Java 25 and an isolated data directory; do not use personal saved tasks.
Record actual outcomes and display settings before marking a row complete.

| Environment | Checks | Status |
| --- | --- | --- |
| macOS GUI | Startup; greeting, avatars, font fallback, Help and nested Edit controls; Send and Enter; keyboard navigation; repeated Send; valid, invalid, long and multiline messages; many exchanges and scroll-to-latest; `bye` and window closing | Pending: window not inspectable in this session |
| macOS sizes | Default, minimum 440×560, larger window, approximately 1366×768 and 1920×1080, high-DPI/scaled display; visible controls, wrapping and overlap | Pending: window not inspectable in this session |
| macOS locales | English and Chinese OS language; English, Chinese and mixed descriptions; Unicode stars and dates; custom-font fallback | Pending: OS language changes and window inspection unavailable |
| Windows GUI | The same GUI, resolution and locale checks on Windows | Pending: Windows environment unavailable |
| Linux GUI | The same GUI, resolution and locale checks on Linux | Pending: Linux environment unavailable |

The scripted Windows-symbol case uses a simulated `os.name`; it does not
replace a real Windows GUI check. The JavaFX resource tests check packaged
assets and layout declarations, but cannot prove rendered appearance.
