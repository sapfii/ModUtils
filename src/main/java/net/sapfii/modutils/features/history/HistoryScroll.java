package net.sapfii.modutils.features.history;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.sapfii.modutils.features.FeatureScroll;

public class HistoryScroll extends FeatureScroll {

    public HistoryScroll(MinecraftClient client, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
    }

    public void addLine(OrderedText text) {
        this.addEntry(new Entry(text));
    }

    @Override
    public int getRowWidth() {
        return 240;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
