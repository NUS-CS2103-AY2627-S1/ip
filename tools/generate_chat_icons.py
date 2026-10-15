"""Generate simple, matching PNG avatars for the JavaFX chat window."""

from pathlib import Path

from PIL import Image, ImageDraw


OUTPUT_DIRECTORY = Path(__file__).resolve().parents[1] / "src/main/resources/images"
SCALE = 4
SIZE = 100
MIDNIGHT = "#1b263c"
GOLD = "#d6b76f"
PALE_GOLD = "#f2d994"
CREAM = "#f5f0e5"


def scale_coordinates(coordinates):
    """Scale design coordinates for antialiased drawing."""
    return tuple(round(value * SCALE) for value in coordinates)


def draw_badge():
    """Return a dark circular badge with a thin gold border."""
    image = Image.new("RGBA", (SIZE * SCALE, SIZE * SCALE), (0, 0, 0, 0))
    drawing = ImageDraw.Draw(image)
    drawing.ellipse(scale_coordinates((5, 5, 95, 95)), fill=MIDNIGHT,
                    outline=GOLD, width=2 * SCALE)
    return image, drawing


def save_icon(image, name):
    """Save a smooth PNG at the size used by JavaFX."""
    image.resize((SIZE, SIZE), Image.Resampling.LANCZOS).save(OUTPUT_DIRECTORY / name)


def create_pawn():
    """Draw a gold pawn silhouette on a dark badge."""
    image, drawing = draw_badge()
    drawing.ellipse(scale_coordinates((40, 19, 60, 39)), fill=PALE_GOLD)
    drawing.rounded_rectangle(scale_coordinates((37, 40, 63, 48)),
                              radius=4 * SCALE, fill=PALE_GOLD)
    drawing.polygon([scale_coordinates(point) for point in
                     ((42, 47), (58, 47), (67, 71), (33, 71))], fill=GOLD)
    drawing.rounded_rectangle(scale_coordinates((26, 72, 74, 80)),
                              radius=3 * SCALE, fill=PALE_GOLD)
    save_icon(image, "PawnIcon.png")


def create_stickman():
    """Draw a cream stick figure on a dark badge."""
    image, drawing = draw_badge()
    line_width = 5 * SCALE
    drawing.ellipse(scale_coordinates((42, 20, 58, 36)), outline=CREAM,
                    width=4 * SCALE)
    for start, end in [((50, 39), (50, 64)), ((50, 47), (32, 57)),
                       ((50, 47), (68, 57)), ((50, 64), (35, 81)),
                       ((50, 64), (65, 81))]:
        drawing.line((scale_coordinates(start), scale_coordinates(end)),
                     fill=CREAM, width=line_width)
        drawing.ellipse(scale_coordinates((end[0] - 2.5, end[1] - 2.5,
                                           end[0] + 2.5, end[1] + 2.5)), fill=CREAM)
    save_icon(image, "StickmanIcon.png")


if __name__ == "__main__":
    create_pawn()
    create_stickman()
