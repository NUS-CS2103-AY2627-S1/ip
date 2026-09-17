# Glennon GUI artwork

Generated with the built-in image-generation tool for the navy and mint
mission-control theme. The GUI uses the original avatar and background.

## Active artwork

- [Original avatar](../src/main/resources/images/glennon-avatar.png): loaded
  in the header at 48 pixels with its transparency and aspect ratio preserved.
- [Original background](../src/main/resources/images/mission-background.png):
  fills the conversation viewport, scales to cover on resize, and stays fixed
  while the conversation scrolls. Message cards retain opaque backgrounds
  for readable text.

## Style-matched revision

The revised assets use the actual Glennon GUI as their style reference:
matte navy, muted mint, rounded shapes, and restrained mission-control detail.
These alternative versions are retained for comparison and are not used by
the current interface:

- [Revised avatar](../src/main/resources/images/glennon-avatar-v2.png):
  simplified shading and genuine alpha transparency.
- [Revised background](../src/main/resources/images/mission-background-v2.png):
  a quieter navy surface with faint orbital contours.

Both revisions were made with the built-in image-generation tool. The original
set was selected for the GUI.

### Avatar revision prompt

```text
Use case: style-transfer
Asset type: revised transparent profile picture for Glennon, the mission-control task chatbot.
Input images: Image 1 is the avatar to edit. Image 2 is a STYLE REFERENCE ONLY: the actual Glennon application screenshot. Do not output or edit the screenshot.
Primary request: restyle the avatar so it belongs to the actual Glennon interface in Image 2. Follow its understated, clean mission-control identity and quiet navy/mint palette.
Keep: one centered robot head, broad rounded-rectangular face panel, integrated side panels, friendly calm expression, isolated silhouette, transparent background, enough margin for a circular crop.
Change: use a clean flat-shaded raster illustration with just two or three broad tonal planes. Simplify the face to two small confident eyes and a slight closed smile. Use a matte navy housing and a muted mint face panel. Remove shiny 3D reflections, glossy oversized eyes, luminous ear rings, microtexture, ornamental seams, and dramatic lighting. The character should feel like a capable, approachable mission-control companion.
Color palette: match screenshot precisely: #101d26 navy, #172a35 slate, #2b4854 blue-slate, #a9ddca muted mint; almost no white.
Composition: square image, centered complete head occupying about 70 percent of the canvas with clean generous transparent margins. Strong readable silhouette at 36 pixels. No circular badge or enclosing background.
Constraints: genuinely transparent alpha, no checkerboard baked in; no body, accessories, text, logo, UI elements, extra objects, cast shadow, glow, metallic shine, gradients to white, or watermark. Deliver only the revised avatar PNG.
```

### Avatar transparency correction prompt

```text
Use case: background-extraction
Edit the supplied avatar image. Remove the entire white and gray patterned background surrounding the robot head and make that area genuinely transparent with an alpha channel. Preserve the robot head itself exactly: its silhouette, matte navy and mint colors, face, expression, size, position, edges and all internal details must stay unchanged. Produce an isolated cutout PNG with actual transparent pixels around the head. Do not replace the background with white, black, gray, a grid, a checker pattern, or another simulated transparency texture. Only remove the background; do not redesign or restyle the character. No text, shadow or added objects.
```

### Background revision prompt

```text
Use case: style-transfer
Asset type: revised portrait conversation background for Glennon.
Input images: Image 1 is the background to edit. Image 2 is a STYLE REFERENCE ONLY: the actual Glennon application screenshot. Do not output or edit the screenshot.
Primary request: restyle the background so it feels native to Glennon's restrained mission-control interface. Match its understated navy and muted mint design, with clean, quiet surfaces.
Keep: portrait composition, dark navy field, edge-oriented orbital motif, large calm empty central area for dense chat.
Change: simplify into a nearly flat matte navy #101d26 background with subtle slate #14232d variations. Replace photographic galaxy clouds and sparkly stars with one or two extremely faint broad orbital contours near the corners, using dark desaturated teal barely lighter than the navy. Keep the central 85 percent visually uniform. Create gentle atmosphere through very subtle fine grain and tonal layers, not light effects.
Composition: portrait 1024x1536, safe for responsive cropping. Background must remain visually quiet wherever chat cards appear.
Constraints: opaque image; no stars, nebula clouds, bright streaks, dramatic gradients, planets, objects, characters, labels, text, logos, borders, dashboard panels, or screenshot/UI. No decoration as bright as the app's #a9ddca mint accent. Deliver only the revised background PNG.
```

## Original assets

- [Avatar](../src/main/resources/images/glennon-avatar.png): transparent PNG.
  Used as a compact header image.
- [Background](../src/main/resources/images/mission-background.png): portrait
  PNG with quiet central space and subtle orbital details toward the edges.
  Used on the scroll viewport, behind opaque message cards.

The original PNG files are preserved without resizing or recompression.

## Avatar generation prompt

```text
Use case: stylized-concept
Asset type: square profile picture for Glennon, a compact JavaFX mission-tracking chatbot.
Primary request: create one friendly, distinctive mission-control assistant robot head as a polished raster illustration.
Subject: a softly rounded, compact robot head with a dark navy graphite shell, a pale mint face panel, two simple expressive dark eyes, and small integrated ear-like side panels. Calm, helpful expression. Head only, no body or accessories.
Style/medium: refined soft 3D illustration, smooth matte surfaces, restrained soft highlights, crisp silhouette. Charming but not toy-like or overly detailed.
Composition/framing: square 1024x1024 canvas; centered front three-quarter view; complete head within the central 70 percent, with generous transparent margins safe for circular or rounded-square cropping. The face must remain immediately readable at 32–40 pixels.
Color palette: match the existing app: midnight navy #101d26, slate #172a35, pale mint #a9ddca, restrained off-white highlights.
Background: genuinely transparent alpha, including the margins. Isolated clean cutout.
Constraints: no text, initials, logos, watermark, border, badge, scenery, cast shadow outside the head, or extra objects. Do not simulate transparency with a checkerboard. Deliver a single avatar image.
```

## Background generation prompt

```text
Use case: stylized-concept
Asset type: subtle portrait background artwork for the conversation area of Glennon, a compact mission-tracking chatbot.
Primary request: create a calm, beautiful midnight mission-control background that keeps chat text easy to read.
Scene/backdrop: an abstract deep-space atmosphere, dominated by an almost-solid midnight navy #101d26 field. Extremely faint diffuse teal haze and one or two soft orbital arcs appear only toward the outer edges, with very sparse tiny dim star-like specks near the perimeter. It should feel like a quiet night sky, not a dashboard or a busy galaxy.
Style/medium: premium atmospheric digital painting with smooth gradients and fine, unobtrusive texture, no hard edges.
Composition/framing: portrait approximately 1024x1536; broad uninterrupted negative space across the central 75 percent for dense chat content. Keep decoration low-contrast enough for comfortable reading even when cropped to narrower or wider windows. No focal object in the center.
Color palette: overwhelmingly #101d26 and #14232d, with restrained desaturated slate-teal #233c43 and very faint mint-tinted light. All decoration much darker and less saturated than the app's pale mint #a9ddca accent.
Constraints: opaque image, no text, UI, panels, grids, people, robots, planets, spacecraft, bright stars, bright light sources, logos, watermark, or border. Deliver one finished background texture, not a mockup.
```
