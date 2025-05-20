package net.sapfii.modutils.features;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public abstract class FeatureScreen extends Screen {
    protected FeatureScreen(Text title) {
        super(title);
    }
}
