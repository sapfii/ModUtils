package net.sapfii.modutils.features.vanishdisplay;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.feature.trait.RenderedFeature;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class VanishDisplay implements RenderedFeature {
    VanishDisplayData data;

    long lastRender = 0;

    public VanishDisplay(VanishDisplayData data) {
        this.data = data;
    }

    @Override
    public void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        float delta = (System.currentTimeMillis() - lastRender) / 1000.0F;
        TextRenderer textRenderer = Flint.getClient().textRenderer;
        if (lastRender == 0) {
            data.finishAnimation(data.text.getString());
        } else {
            data.animationStep(delta);
        }
        drawContext.fill(data.offsetX-3, data.offsetY-10, data.boxLength+data.offsetX+3, data.boxHeight+data.offsetY+3, data.boxColor);
        drawContext.drawTextWithShadow(
                textRenderer,
                data.text,
                data.offsetX, data.offsetY,
                0xFFFFFF
        );
        lastRender = System.currentTimeMillis();
    }
}
