package net.sapfii.modutils.features.reportdisplay;

import net.minecraft.text.OrderedText;

import java.util.ArrayList;

public class ReportData {
    String reporter, offender, offense, location;
    ArrayList<OrderedText> texts = new ArrayList<>();
    int x, y = 35, expectedX = 0, boxLength, index;
    float subpixelX;
    int opacity = 255, expectedOpacity = 255;
    float subvalueOpacity = 255.0F;
    boolean beingRemoved = false, remove = false, removed = false;

    float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public void animationStep(float delta) {
        if (beingRemoved) {
            expectedOpacity = 0;
            if (index >= 2 && opacity == 4) {
                remove = true;
                removed = true;
                return;
            }
        }
        subpixelX = lerp(subpixelX, (float) expectedX, 10.0F * delta);
        x = Math.round(subpixelX);
        subvalueOpacity = lerp(subvalueOpacity, (float) expectedOpacity, 10.0F * delta);
        opacity = Math.round(subvalueOpacity);
        if (opacity < 4 && !beingRemoved) {
            opacity = 4;
        }
        if ((opacity <= 4 && beingRemoved) && !removed) {
            remove = true;
            removed = true;
        }
    }
}
