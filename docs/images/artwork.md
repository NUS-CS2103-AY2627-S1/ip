# GUI artwork provenance

The interface uses an original dark navy, cyan, and gold composition.
No layout or code was adopted from the linked course showcase projects.

- `src/main/resources/images/user-hero.png`: generated with the built-in OpenAI image-generation tool
  on 2026-09-17 for this project. It is an original, anonymous mission-control operator avatar with a
  transparent background; it does not depict a real person or a copyrighted character.
- `src/main/resources/images/friday-core.png`: generated with the built-in OpenAI image-generation tool
  on 2026-09-13 for this project. The PNG has transparency outside the core.
- The original `avatars.png` is retained as historical artwork but is no longer loaded by the GUI.
- GUI screenshots in this directory are actual JavaFX renders from the desktop acceptance tests,
  using temporary sample data. They exclude operating-system window borders.

Earlier third-party character artwork is not included in the application.

## Final user-avatar generation prompt

> Use case: stylized-concept. Original anonymous mission-control operator in a dark navy hoodie, with a
> subtle cyan rim light and warm gold detail. Centered, friendly head-and-shoulders portrait, transparent
> background, and a clean silhouette readable at 36 pixels. No real person, text, logos, masks, spiders,
> web patterns, or copyrighted-character references.

## Final FRIDAY core generation prompt

> Use case: stylized-concept. Single square avatar PNG with genuinely transparent background for FRIDAY AI assistant.
> A luminous cyan circular AI core with concentric dark navy metallic rings, restrained gold segments and bright
> central cyan light. Premium clean stylized illustration, simple bold silhouette readable at 36 pixels, front-on
> centered fills 90% square. No face, no humanoid, no text, no frame, no backdrop. Transparent outside the circular core.
