package net.sapfii.modutils.features.reportdisplay;

import dev.dfonline.flint.Flint;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.sapfii.modutils.features.FeatureScreen;

import java.util.ArrayList;
import java.util.List;

public class ReportScreen extends FeatureScreen {
    List<OrderedText> lines = new ArrayList<>();
    private ReportScroll reportScroll;

    protected ReportScreen(Text title) {
        super(title);
    }

    public void addReport(Text text) {
        reportScroll.addEntry(text.asOrderedText());
        addSelectableChild(reportScroll);
        lines.add(text.asOrderedText());
    }

    public void addReport(OrderedText text) {
        reportScroll.addEntry(text);
        addSelectableChild(reportScroll);
        lines.add(text);
    }

    public void refresh() {
        this.reportScroll = new ReportScroll(client, width, height-90, 50, 10);
        addSelectableChild(reportScroll);
        for (OrderedText text : lines) {
            reportScroll.addEntry(text);
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        refresh();
    }

    @Override
    protected void init() {
        reportScroll = new ReportScroll(client, width, height-90, 50, 10);
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Window window = Flint.getClient().getWindow();
        super.render(context, mouseX, mouseY, delta);
        int titleX = window.getScaledWidth()/2-this.textRenderer.getWidth("Reports")/2;
        reportScroll.render(context, mouseX, mouseY, delta);
        context.drawText(this.textRenderer, "Reports", titleX, 30, 0xFFFFFFFF, true);
    }
}
