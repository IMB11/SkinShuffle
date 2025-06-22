# Problem 1: OpenCarouselButton Skin Positioning Issues

## Root Cause Analysis

The OpenCarouselButton has inconsistent skin positioning due to incorrect coordinate calculations in the `renderSkinPreview()` call. Looking at the actual code:

### Current Issues:
1. **Inverted Y Coordinates**: The button calculates `y1 = entityY` and `y2 = getY()`, which means y1 > y2, creating an inverted bounding box.

2. **Incorrect Entity Positioning**: The calculation `int entityY = centerY + entitySize / 2` places the entity outside the button bounds.

3. **Inconsistent Size Logic**: The button calculates `entitySize` but doesn't use it consistently with the bounding box coordinates.

## Step-by-Step Solution

### Step 1: Fix the Coordinate Calculation
The main issue is the inverted Y coordinates. Replace the problematic coordinate calculation:

```java
// Current problematic code:
int centerY = this.getY() + this.getHeight() / 2;
int previewSpanY = Math.min(this.getWidth(), this.getHeight()) - 10;
int entitySize = previewSpanY / 10 * 8;
int entityY = centerY + entitySize / 2;
int x1 = getX();
int y1 = entityY;          // This is BELOW centerY
int x2 = getX() + getWidth();
int y2 = getY();           // This is ABOVE centerY -> y1 > y2 (invalid!)

// Fixed code:
int padding = 5;
int x1 = getX() + padding;
int y1 = getY() + padding;
int x2 = getX() + getWidth() - padding;
int y2 = getY() + getHeight() - padding;
```

### Step 2: Simplify the Rendering Call
Remove the complex entity positioning calculations and let `SkinPreviewRenderer` handle sizing automatically:

```java
@Override
public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    super.renderWidget(context, mouseX, mouseY, delta);

    if (selectedPreset != null) {
        // Simple bounding box within button bounds
        int padding = 5;
        int x1 = getX() + padding;
        int y1 = getY() + padding;
        int x2 = getX() + getWidth() - padding;
        int y2 = getY() + getHeight() - padding;

        renderer.renderSkinPreview(
                context,
                selectedPreset,
                mouseX, mouseY,
                x1, y1, x2, y2,
                SkinShuffleConfig.get().widgetSkinRenderStyle,
                false
        );
    }
}
```

### Step 3: Remove Unused Variables
Remove the now-unused calculations:
- `centerY`
- `previewSpanY` 
- `entitySize`
- `entityY`

## Expected Result
After implementing these changes, the OpenCarouselButton will:
- Consistently position the skin within the button bounds
- Properly handle different render styles (ROTATION, CURSOR)
- Automatically scale the skin to fit the button size
- Handle loading states gracefully
