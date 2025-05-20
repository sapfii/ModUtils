package net.sapfii.modutils.features.reportdisplay;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.FlintAPI;
import dev.dfonline.flint.feature.trait.RenderedFeature;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Report implements RenderedFeature {
    ReportData data = new ReportData();
    ReportList reports;

    public Report(String reporter, String offender, String offense, String location, ReportList reports) {
        FlintAPI.registerFeature(this);
        this.reports = reports;
        this.reports.addFirst(this);
        data.texts = new ArrayList<>();
        TextRenderer textRenderer = Flint.getClient().textRenderer;

        data.reporter = reporter;
        data.texts.add(Text.literal("! ").styled(style -> {
                    style = style.withColor(0xFB5454);
                    style = style.withBold(true);
                    return style;})
                .append(Text.literal("Incoming Report ").styled(style -> {
                    style = style.withColor(0xa7a7a7);
                    style = style.withBold(false);
                    return style;}))
                .append(Text.literal("(" + data.reporter + ")").styled(style -> {
                    style = style.withColor(0x545454);
                    style = style.withBold(false);
                    return style;})).asOrderedText());

        data.offender = offender;
        data.texts.add(Text.literal("Offender: ").withColor(0xFF545454)
                .append(Text.literal(data.offender).withColor(0xFFFFFFFF)).asOrderedText());

        data.offense = offense;
        List<OrderedText> offenseWrapped = textRenderer.wrapLines(Text.empty().append(Text.literal("Offense: ").styled(style -> {
                    style = style.withColor(0x545454);
                    return style;}))
                .append(Text.literal(data.offense).styled(style -> {
                    style = style.withColor(0xFFFFFF);
                    return style;})), 200);
        data.texts.addAll(offenseWrapped);

        data.location = location;
        data.texts.add(Text.literal("Location: ").withColor(0xFF545454)
                .append(Text.literal(data.location).withColor(0xFFFFFFFF)).asOrderedText());

        for (OrderedText text : data.texts) {
            if (textRenderer.getWidth(text) > data.boxLength) {
                data.boxLength = textRenderer.getWidth(text);
            }
        }
        data.subpixelX = -data.boxLength-data.subpixelX-10;
        data.x = Math.round(data.subpixelX);
    }


    long lastRender = System.currentTimeMillis();


    @Override
    public void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (!data.removed) {
            float delta = (System.currentTimeMillis() - lastRender) / 1000.0F;
            TextRenderer textRenderer = Flint.getClient().textRenderer;
            data.expectedOpacity = 255;
            if (data.index >= 2) {
                data.expectedOpacity = 0;
            }
            data.animationStep(delta);
            int color = (data.opacity << 24) | 0x00FFFFFF;
            int bgcolor = (Math.round(data.opacity * 0.4F) << 24);
            if (data.index == 0) {
                data.expectedX = 0;
            } else {
                data.expectedX = reports.get(data.index - 1).data.boxLength + reports.get(data.index - 1).data.expectedX + 15;
            }
            drawContext.fill(data.x, data.y - 5, data.x + data.boxLength + 10, data.y + 5 + textRenderer.fontHeight * data.texts.size(), bgcolor);
            for (int txtI = 0; txtI < data.texts.size(); ++txtI) {
                OrderedText text = data.texts.get(txtI);
                drawContext.drawTextWithShadow(
                        textRenderer,
                        text,
                        data.x + 5, data.y + (textRenderer.fontHeight * txtI),
                        color
                );
            }
            if (data.remove) {
                reports.remove(data.index);
            }
            lastRender = System.currentTimeMillis();
        }
    }
}
