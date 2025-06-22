# Problem 3: PresetWidget Skin Rendering Issues

## Root Cause Analysis

The PresetWidget is not showing the correct skin because of severely incorrect coordinate calculations that create invalid bounding boxes.

### Specific Issues:

1. **Invalid Bounding Box**: The widget calculates:
   ```java
   int x2 = 15 + getX() + getWidth() / 4;  // This makes the width only 1/4 of the widget!
   int y2 = 4 + getY() + getHeight() * 2;  // This extends FAR beyond the widget bounds!
   ```

2. **Inconsistent Width**: The render area is only 1/4 of the widget width, making the skin preview tiny and off-center.

3. **Massive Height**: The y2 coordinate extends to 2x the widget height, causing the skin to render outside the visible area.

4. **Unused Helper Methods**: The widget has `getPreviewX()`, `getPreviewY()`, and `getPreviewSize()` methods that aren't being used in the coordinate calculation.

## Step-by-Step Solution

### Step 1: Fix the Coordinate Calculations
Replace the problematic coordinate calculation with proper bounding box coordinates:

```java
// Current problematic code:
int x1 = getX() + 15;
int y1 = getY() + 4;
int x2 = 15 + getX() + getWidth() / 4;    // Only 1/4 width!
int y2 = 4 + getY() + getHeight() * 2;    // 2x height!

// Fixed code:
int margin = 10;
int nameHeight = this.client.textRenderer.fontHeight + margin * 2;
int buttonSpace = showButtons ? 30 : 0;

int x1 = getX() + margin;
int y1 = getY() + nameHeight;
int x2 = getX() + getWidth() - margin;
int y2 = getY() + getHeight() - buttonSpace - margin;
```

### Step 2: Update the renderWidget Method
Replace the entire skin rendering section:

```java
@Override
protected void renderWidget(SpruceGuiGraphics graphics, int mouseX, int mouseY, float delta) {
    super.renderWidget(graphics, mouseX, mouseY, delta);

    // Render name (existing code)
    var margin = this.client.textRenderer.fontHeight / 2;
    var name = this.skinPreset.getName() != null ? this.skinPreset.getName() : "Unnamed Preset";
    var nameWidth = this.client.textRenderer.getWidth(name);
    var halfWidth = this.width / 2;
    var halfNameWidth = nameWidth / 2;
    ClickableWidget.drawScrollableText(
            graphics.vanilla(), this.client.textRenderer,
            Text.of(name),
            getX() + halfWidth - Math.min(halfWidth - margin, halfNameWidth), getY() + margin,
            getX() + halfWidth + Math.min(halfWidth - margin, halfNameWidth), getY() + margin + this.client.textRenderer.fontHeight,
            this.active ? 0xFFFFFFFF : 0xFF808080
    );

    // Calculate proper skin preview area
    int textAreaHeight = this.client.textRenderer.fontHeight + margin * 2;
    int buttonSpace = showButtons ? 30 : 0;
    int skinMargin = 10;
    
    int x1 = getX() + skinMargin;
    int y1 = getY() + textAreaHeight;
    int x2 = getX() + getWidth() - skinMargin;
    int y2 = getY() + getHeight() - buttonSpace - skinMargin;
    
    // Get render style
    SkinShuffleConfig.SkinRenderStyle renderStyle =
            SkinShuffleConfig.get().carouselSkinRenderStyle;

    // Render the skin preview
    renderer.renderSkinPreview(
            graphics.vanilla(),
            this.skinPreset,
            mouseX, mouseY,
            x1, y1, x2, y2,
            this.active ? renderStyle : null,
            false
    );
}
```

### Step 3: Remove or Update Unused Helper Methods
Since the coordinate calculation is now properly handled, you can remove these methods:

```java
// Remove these methods:
protected int getPreviewX() { ... }
protected int getPreviewY() { ... }
protected int getPreviewSize() { ... }
private float getEntityRotation() { ... }
```

### Step 4: Verify Button Layout
The `showButtons` variable should correctly determine if space needs to be reserved for buttons at the bottom of the widget.

## Expected Result
After implementing these changes:
- PresetWidget will display the correct skin associated with each preset
- Skin previews will be properly sized and positioned within the widget bounds
- The rendering will be consistent with other components
- Loading states will be handled gracefully
- Button layout won't interfere with skin preview
- All render styles (ROTATION, CURSOR) will work properly
