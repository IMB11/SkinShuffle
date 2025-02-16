package dev.imb11.skinshuffle.client.gui;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.navigation.NavigationDirection;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

public class WelcomeGuideScreen extends SpruceScreen {
    private final Screen parent;
    private ScrollableTextContainer textContainer;

    public WelcomeGuideScreen(Screen parent) {
        super(Text.translatable("skinshuffle.welcome.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        // Create our custom scrollable container
        int containerHeight = this.height - 60; // Example height
        textContainer = new ScrollableTextContainer(
                Position.of(0, 20), // Position just below the title
                this.width,
                containerHeight
        );
        this.addDrawableChild(textContainer);

        // Add Continue and More Info buttons
        this.addDrawableChild(new SpruceButtonWidget(
                Position.of(this.width / 2 - 128 - 5, this.height - 23),
                128, 20,
                ScreenTexts.CONTINUE,
                button -> this.client.setScreen(parent)
        ));

        this.addDrawableChild(new SpruceButtonWidget(
                Position.of(this.width / 2 + 5, this.height - 23),
                128, 20,
                Text.translatable("skinshuffle.welcome.more_info"),
                button -> this.client.setScreen(
                        new ConfirmLinkScreen(
                                ignored -> close(),
                                "https://youtu.be/CNMASU7GQBs",
                                true
                        )
                )
        ));
    }

    @Override
    public void renderTitle(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawTextWithShadow(
                this.client.textRenderer,
                this.title,
                this.width / 2 - this.client.textRenderer.getWidth(this.title) / 2,
                10,
                0xFFFFFF
        );
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }

    private static class ScrollableTextContainer extends SpruceContainerWidget {
        private double scrollOffset = 0;
        private double maxScrollOffset = 0;
        private final Text[] lines;

        public ScrollableTextContainer(Position position, int width, int height) {
            super(position, width, height);

            this.lines = new Text[] {
                    Text.translatable("screen.skinshuffle.thankyou"),
                    Text.translatable("screen.skinshuffle.read_info"),
                    Text.translatable("screen.skinshuffle.blank"),
                    Text.translatable("screen.skinshuffle.carousel_heading"),
                    Text.translatable("screen.skinshuffle.carousel_desc1"),
                    Text.translatable("screen.skinshuffle.carousel_desc2"),
                    Text.translatable("screen.skinshuffle.carousel_desc3"),
                    Text.translatable("screen.skinshuffle.carousel_desc4"),
                    Text.translatable("screen.skinshuffle.blank"),
                    Text.translatable("screen.skinshuffle.preset_edit_heading"),
                    Text.translatable("screen.skinshuffle.preset_edit_desc1"),
                    Text.translatable("screen.skinshuffle.preset_edit_desc2"),
                    Text.translatable("screen.skinshuffle.blank"),
                    Text.translatable("screen.skinshuffle.config_heading"),
                    Text.translatable("screen.skinshuffle.config_desc"),
                    Text.translatable("screen.skinshuffle.blank"),
                    Text.translatable("screen.skinshuffle.hotswapping_heading"),
                    Text.translatable("screen.skinshuffle.hotswapping_desc1"),
                    Text.translatable("screen.skinshuffle.hotswapping_desc2")
            };
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
            scrollOffset = Math.max(0, Math.min(scrollOffset - scrollY * 10, maxScrollOffset));
            return true;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);

            context.enableScissor(
                    this.getX(),
                    this.getY(),
                    this.getX() + this.width,
                    this.getY() + this.height
            );

            int lineHeight = this.client.textRenderer.fontHeight + 5;
            int currentY = this.getY() + 5 - (int) scrollOffset;
            int wrapWidth = this.width - 20;
            int totalContentHeight = 0;

            // First pass: measure the total height of wrapped lines
            for (Text line : lines) {
                // Directly use the Text object
                if (line.getString().isEmpty()) {
                    totalContentHeight += lineHeight;
                } else {
                    var wrappedLines = this.client.textRenderer.wrapLines(line, wrapWidth);
                    totalContentHeight += wrappedLines.size() * lineHeight;
                }
            }

            maxScrollOffset = Math.max(0, totalContentHeight - this.height + 10);

            // Second pass: render wrapped text
            for (Text line : lines) {
                if (line.getString().isEmpty()) {
                    currentY += lineHeight;
                    continue;
                }
                // Wrap the Text object, then draw each wrapped line
                var wrappedLines = this.client.textRenderer.wrapLines(line, wrapWidth);
                for (OrderedText wrappedLine : wrappedLines) {
                    context.drawTextWithShadow(
                            this.client.textRenderer,
                            wrappedLine,
                            this.getX() + 10,
                            currentY,
                            0xFFFFFF
                    );
                    currentY += lineHeight;
                }
            }

            // Draw scrollbar if needed
            if (maxScrollOffset > 0) {
                int scrollbarHeight = (int) ((this.height * (double) this.height) / totalContentHeight);
                int scrollbarY = (int) (this.getY() + (scrollOffset / maxScrollOffset) * (this.height - scrollbarHeight));

                context.fill(
                        this.getX() + this.width - 5,
                        this.getY(),
                        this.getX() + this.width,
                        this.getY() + this.height,
                        0x80000000
                );
                context.fill(
                        this.getX() + this.width - 5,
                        scrollbarY,
                        this.getX() + this.width,
                        scrollbarY + scrollbarHeight,
                        0xFFFFFFFF
                );
            }

            context.disableScissor();
        }

        @Override
        public boolean onNavigation(NavigationDirection direction, boolean tab) {
            return super.onNavigation(direction, tab);
        }
    }
}
