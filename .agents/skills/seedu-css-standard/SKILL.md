---
name: seedu-css-standard
description: >-
  Enforces the SE-EDU CSS Coding Standard and Google CSS Style Guide conventions based on se-education.org guidelines.
  Mandates selector formatting, file structure, naming conventions, declaration rules, and separation of concerns.
---

# SE-EDU CSS Coding Standard

Reference: [SE-EDU CSS Coding Standard](https://se-education.org/guides/conventions/css.html) and [Google HTML/CSS Style Guide](https://google.github.io/styleguide/htmlcssguide.xml).

This skill provides mandatory CSS conventions and style rules for projects in SE-EDU.

---

## 1. General Rules & Baseline

* **Style Guide Baseline**: Use the [Google CSS Style Guide](https://google.github.io/styleguide/htmlcssguide.xml) for any rules not explicitly covered in this document.
* **Separation of Concerns (SoC)**:
  * Do NOT use inline styles in HTML, JSP, or FXML markup. All visual presentation must reside in external CSS files.
  * Do NOT use `@import` statements in CSS files. Link external stylesheets explicitly from HTML/JSP (`<link>`) or FXML (`stylesheets="@path/to.css"`).
* **Sectioning**: Divide CSS files into logical sections using descriptive comment banners so that rules are easily located and duplicates are prevented.

---

## 2. Naming & Selectors

* **Naming Conventions**:
  * Use lowercase, hyphen-delimited names for class and ID selectors (e.g. `.reply-label`, `.dialog-container`, `.button-sort-ascending`).
  * Avoid `camelCase`, `snake_case`, or uppercase names unless required by framework bindings (e.g. JavaFX node IDs matching `fx:id`).
  * Use meaningful and functional names that describe purpose, not appearance.
* **Avoid CSS3 Selector Hacks**:
  * Prefer class selectors over fragile structural or complex pseudo-selectors to ensure compatibility and maintainability.
* **Selector Formatting**:
  * When a rule targets multiple selectors, put **each selector on a separate line**:
    ```css
    /* Good */
    .button-sort-ascending:hover,
    .button-sort-none:hover {
        cursor: pointer;
    }

    /* Bad */
    .button-sort-ascending:hover, .button-sort-none:hover {
        cursor: pointer;
    }
    ```
* **Grouping & Hierarchy**:
  * Group related rules and state pseudo-classes together (e.g., base selector followed by `:hover`, `:pressed`, and child selectors).

---

## 3. Formatting & Declarations

* **Indentation**: Consistent indentation (2 or 4 spaces per block). Do not use tab characters.
* **Brace Style**:
  * Space before the opening curly brace `{` on the selector line.
  * Closing curly brace `}` on a new line, aligned with the selector.
* **Declaration Layout**:
  * Put each declaration on its own line.
  * Use a single space after the colon (e.g. `property: value;`).
  * End every declaration with a semicolon `;`.
* **Values & Units**:
  * **Zero values**: Omit units after 0 (e.g. `margin: 0;`, `-fx-rotate: 0;` NOT `0px`).
  * **Decimals**: Always use a leading 0 for fractional values between -1 and 1 (e.g. `0.5` NOT `.5`).
  * **Hexadecimal colors**: Use lowercase hex codes (e.g. `#ffffff`, `#ff9cb4`). Use 3-character notation where possible (e.g. `#fff`).
  * **Quotes in URLs**: Optional, but keep quoting consistent within the file.
* **Blank Lines**: Use a single blank line between rules and sections. Avoid consecutive blank lines.
