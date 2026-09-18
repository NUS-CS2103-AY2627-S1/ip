# FRIDAY GUI design

The JavaFX GUI uses a compact, mobile-inspired conversation layout. Its header uses a
gradient FRIDAY spark and the subtitle "Your task assistant". FRIDAY replies are charcoal,
left-aligned cards, while user commands are pink-to-purple right-aligned bubbles.
User-correctable errors use a high-contrast red card and warning icon.

The personality palette uses near-black (`#0B0B0F`), charcoal (`#1E1E24`), purple (`#9B3FD6`),
pink (`#C8678A`), and light gray text (`#EDEDF2`). Responses use concise acknowledgements such
as "Understood", "Confirmed", and "Noted"; validation errors begin with "That presents a
complication" to keep the assistant composed and clear.

The layout is responsive: the conversation scroll pane receives available vertical space,
and message labels wrap to the available width up to 480 pixels. The fixed header and
input bar preserve their natural heights when the window is resized.

Task responses use individual cards rather than raw command-line text. Every card shows a
checkbox, description, optional schedule detail, and a readable `To-do`, `Deadline`, or `Event`
pill; the GUI never exposes storage or console type markers such as `[T]`, `[D]`, or `[E]`.

The design is implemented with standard JavaFX FXML and CSS controls only. It uses simple
gradients, SVGPath icons, and a single drop shadow; no external font dependency is required.
