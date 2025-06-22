# Problem 2: Trial and Error Breaking Mouse Movement Style

## Root Cause Analysis

The trial-and-error approach breaks the mouse movement (CURSOR) style because of coordinate calculation issues and inconsistent parameter passing between components.

### Why Trial and Error Breaks CURSOR Style:

1. **Inconsistent Coordinate Systems**: Each component calculates bounding boxes differently, leading to different mouse sensitivity and behavior.

2. **Parameter Order Confusion**: The `renderSkinPreview` method expects (x1, y1, x2, y2) but some components pass invalid bounding boxes (like y1 > y2).

3. **Different Mouse Handling**: The `SkinPreviewRenderer.renderSkinPreview()` method doesn't properly handle the mouseX/mouseY parameters - it calculates `followX` and `followY` but then passes them incorrectly to `GuiEntityRenderer.drawEntity()`.

## Step-by-Step Solution

### Step 1: Fix SkinPreviewRenderer Parameter Passing
The current `SkinPreviewRenderer.renderSkinPreview()` method has incorrect parameter passing to `GuiEntityRenderer.drawEntity()`. Looking at the method signature:

```java
// Current SkinPreviewRenderer (INCORRECT):
GuiEntityRenderer.drawEntity(
    graphics, x1, y1, x2, y2, size,
    rotation, followX, followY, preset.getSkin(), renderStyle
);

// But GuiEntityRenderer.drawEntity expects:
public static void drawEntity(DrawContext context, int x1, int y1, int x2, int y2, int size,
                              float rotation, double mouseX, double mouseY, Skin skin,
                              SkinShuffleConfig.SkinRenderStyle style)
```

The issue is that `followX` and `followY` are being passed where `mouseX` and `mouseY` should be passed directly.

### Step 2: Update SkinPreviewRenderer to Pass Mouse Coordinates Directly
Fix the `renderSkinPreview` method:

```java
public void renderSkinPreview(DrawContext graphics, SkinPreset preset,
                              int mouseX, int mouseY,
                              int x1, int y1, int x2, int y2,
                              SkinShuffleConfig.SkinRenderStyle renderStyle,
                              boolean isLoading) {
    
    if (preset.getSkin().isLoading() || isLoading) {
        int centerX = (x1 + x2) / 2;
        int centerY = (y1 + y2) / 2;
        renderLoadingIndicator(graphics, centerX, centerY);
        if (preset.getSkin().isLoading()) {
            preset.getSkin().getTexture();
        }
        return;
    }

    int size = Math.min(x2 - x1, y2 - y1);
    float rotation = 180; // Default facing forward
    
    if (SkinShuffleConfig.SkinRenderStyle.ROTATION.equals(renderStyle)) {
        rotation = getEntityRotation() * SkinShuffleConfig.get().rotationMultiplier;
    }
    
    // Pass mouseX and mouseY directly - let GuiEntityRenderer handle the mouse calculations
    GuiEntityRenderer.drawEntity(
        graphics, x1, y1, x2, y2, size,
        rotation, mouseX, mouseY, preset.getSkin(), renderStyle
    );
}
```

### Step 3: Ensure All Components Pass Valid Bounding Boxes
Make sure all components pass valid coordinates where x1 < x2 and y1 < y2:

```java
// In OpenCarouselButton - FIXED:
int padding = 5;
int x1 = getX() + padding;
int y1 = getY() + padding;
int x2 = getX() + getWidth() - padding;
int y2 = getY() + getHeight() - padding;

// In PresetWidget - FIXED:
int margin = 10;
int nameHeight = this.client.textRenderer.fontHeight + margin;
int x1 = getX() + margin;
int y1 = getY() + nameHeight;
int x2 = getX() + getWidth() - margin;
int y2 = getY() + getHeight() - margin;
```

## Expected Result
After implementing these changes:
- CURSOR style will work consistently across all components
- No more trial-and-error needed for positioning
- Mouse movement will be smooth and predictable
- ROTATION style will continue to work properly
- All skin previews will behave the same way
