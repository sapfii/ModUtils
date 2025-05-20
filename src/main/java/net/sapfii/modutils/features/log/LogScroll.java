package net.sapfii.modutils.features.log;

import net.minecraft.client.MinecraftClient;
import net.sapfii.modutils.features.FeatureScroll;

public class LogScroll extends FeatureScroll{
    public LogScroll(MinecraftClient client, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
    }

    @Override
    public int getRowWidth() {
        return 440;
    }
}
