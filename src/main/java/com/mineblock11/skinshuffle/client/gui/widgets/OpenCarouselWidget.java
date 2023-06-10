package com.mineblock11.skinshuffle.client.gui.widgets;

import com.mineblock11.skinshuffle.client.gui.SkinCarouselScreen;
import com.mineblock11.skinshuffle.client.gui.cursed.DummyClientPlayerEntity;
import com.mineblock11.skinshuffle.client.gui.cursed.GuiEntityRenderer;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import com.mineblock11.skinshuffle.client.skin.UrlSkin;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

import java.util.UUID;

public class OpenCarouselWidget extends SpruceContainerWidget {
    private SkinPreset selectedPreset;
    private DummyClientPlayerEntity entity;

    public OpenCarouselWidget(Position position, int width, int height) {
        super(position, width, height);

        this.addChild(new SpruceButtonWidget(Position.of(0, 0), width, 20, Text.translatable("skinshuffle.button"), button -> {
            this.client.setScreen(new SkinCarouselScreen());
        }));

        var preset = new SkinPreset(new UrlSkin("https://s.namemc.com/i/37529af66bcdd70d.png", "default"));
        preset.setName("Technoblade");

        setSelectedPreset(preset);
    }

    public void setSelectedPreset(SkinPreset preset) {
        this.selectedPreset = preset;
        var skin = selectedPreset.getSkin();
        this.entity = new DummyClientPlayerEntity(
                null, UUID.randomUUID(),
                skin::getTexture, skin::getModel
        );
    }

    @Override
    protected void renderWidget(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(getX(), getY(), getX() + 5, getY() + 5, 0xFFFFFFFF);

        if(this.entity != null) {
            GuiEntityRenderer.drawEntity(
                    graphics.getMatrices(), getX() + (this.getWidth() / 2), this.getY() - (this.height / 8),
                    (int)( 20 * this.client.getWindow().getScaleFactor()), 0, (float)(getX() + (this.getWidth() / 2)) - mouseX, (float)(this.getY() - (this.height / 2)) - mouseY, entity
            );
        }

        super.renderWidget(graphics, mouseX, mouseY, delta);
    }
}
