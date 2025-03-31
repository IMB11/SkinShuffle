package dev.imb11.skinshuffle.client.gui.widgets;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import net.minecraft.text.Text;

public class VariableSpruceButtonWidget extends SpruceButtonWidget {
    public VariableSpruceButtonWidget(Position position, int width, int height, Text message, PressAction action) {
        super(position, width, height, message, action);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void overrideDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void overridePosition(int x, int y) {
        this.position.setRelativeX(x);
        this.position.setRelativeY(y);
    }
}
