package net.sapfii.modutils.features.vanishdisplay;

import dev.dfonline.flint.Flint;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public class VanishDisplayData {
    int baseX = 8, baseY = 8, offsetX = 0, offsetY = 0, expectedOffsetX = 0, expectedOffsetY = 0, boxLength, boxHeight, expectedBoxLength, expectedBoxHeight, boxColor;
    float subpixelX, subpixelY, subpixelBoxLength, subpixelBoxHeight;
    Text text = Text.empty();

    float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public void animationStep(float delta) {
        TextRenderer textRenderer = Flint.getClient().textRenderer;
        subpixelX = lerp(subpixelX, (float)expectedOffsetX, 7.0F * delta);
        offsetX = Math.round(subpixelX) + baseX;

        expectedBoxLength = textRenderer.getWidth(text);
        subpixelBoxLength = lerp(subpixelBoxLength, (float)expectedBoxLength, 7.0F * delta);
        boxLength = Math.round(subpixelBoxLength);

        subpixelY = lerp(subpixelY, (float)expectedOffsetY, 7.0F * delta);
        offsetY = Math.round(subpixelY) + baseY;

        expectedBoxHeight = textRenderer.fontHeight;
        subpixelBoxHeight = lerp(subpixelBoxHeight, (float)expectedBoxHeight, 7.0F * delta);
        boxHeight = Math.round(subpixelBoxHeight);
    }

    public void finishAnimation(String text) {
        TextRenderer textRenderer = Flint.getClient().textRenderer;
        subpixelX = (float)expectedOffsetX;
        offsetX = expectedOffsetX + baseX;

        expectedBoxLength = textRenderer.getWidth(text);
        subpixelBoxLength = (float)expectedBoxLength;
        boxLength = expectedBoxLength;

        subpixelY = (float)expectedOffsetY;
        offsetY = expectedOffsetY + baseY;

        expectedBoxHeight = textRenderer.fontHeight;
        subpixelBoxHeight = (float)expectedBoxHeight;
        boxHeight = expectedBoxHeight;
    }
}
