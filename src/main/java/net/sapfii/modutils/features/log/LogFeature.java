package net.sapfii.modutils.features.log;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.feature.trait.PacketListeningFeature;
import dev.dfonline.flint.feature.trait.RenderedFeature;
import dev.dfonline.flint.feature.trait.UserCommandListeningFeature;
import dev.dfonline.flint.util.result.EventResult;
import dev.dfonline.flint.util.result.ReplacementEventResult;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.sapfii.modutils.ModUtils;
import net.sapfii.modutils.config.ConfigModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogFeature implements PacketListeningFeature, RenderedFeature, UserCommandListeningFeature {
    private static final Pattern LOGPADDING = Pattern.compile("--------------\\[ (?<logType>.+) \\| (?<node>.+) ]--------------");
    private static final Pattern LOGCMD = Pattern.compile("(?<logType>.+) log(?<params>.+)?");
    private static final Pattern LOGEND = Pattern.compile("\\[(?<logType>.+)] (?<msgCount>.+) log entries found\\.");
    private static final Pattern LOGENDSEARCH = Pattern.compile("\\[(?<logType>.+)] (?<msgCount>.+) log entries found containing the search entry\\.");
    private static final Pattern PLOTMSG = Pattern.compile("(?<time>.+) \\[(?<action>.+)]\\[(?<plot>.+)] (?<player>.+): (?<msg>.+)");

    private boolean loggingMessages = false;
    private boolean ignoreNextPadding = false;
    LogScreen logScreen;
    boolean openLogScreen = false;
    @Override
    public void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (openLogScreen) {
            openLogScreen = false;
            if (Flint.getClient().currentScreen instanceof LogScreen) {
                logScreen = new LogScreen(
                        Text.empty(),
                        logScreen.filter.getText(), logScreen.plotFilter.getText(),
                        logScreen.duration.getText(), logScreen.durationUnits.getValue(),
                        logScreen.secondDuration.getText(), logScreen.secondDurationUnits.getValue()
                );
            } else {
                logScreen = new LogScreen(Text.empty());
            }
            Flint.getClient().setScreen(logScreen);
        }
    }

    @Override
    public ReplacementEventResult<String> sendCommand(String s) {
        Matcher cmdMatcher = LOGCMD.matcher(s);
        if (cmdMatcher.find()) {
            loggingMessages = true;
            openLogScreen = true;
        }
        return ReplacementEventResult.pass();
    }

    @Override
    public EventResult onReceivePacket(Packet<?> packet) {
        TextRenderer textRenderer = Flint.getClient().textRenderer;
        if (!(packet instanceof GameMessageS2CPacket(Text msgText, boolean overlay))) {
            return EventResult.PASS;
        }
        String msgString = msgText.getString();
        Matcher logPadding = LOGPADDING.matcher(msgString);
        Matcher plotMsg = PLOTMSG.matcher(msgString);
        System.out.println(msgString);
        if (ignoreNextPadding && logPadding.find()) {
            ignoreNextPadding = false;
            return EventResult.CANCEL;
        }
        if (loggingMessages) {
            System.out.println("wow");
            if (logPadding.find()) {
                logScreen.setTitle(logPadding.group("node"), logPadding.group("logType"));
                return EventResult.CANCEL;
            }
            if (plotMsg.find() && !plotMsg.group("plot").endsWith(logScreen.plotFilter.getText())) {
                return EventResult.CANCEL;
            }
            if (ModUtils.CONFIG.logDirection() == ConfigModel.LogDirections.UP) {
                for (OrderedText txt : textRenderer.wrapLines(msgText,400).reversed()) {
                    logScreen.addFirst(txt);
                    logScreen.setLogScroll(0);
                }
            } else {
                for (OrderedText txt : textRenderer.wrapLines(msgText,400)) {
                    logScreen.addLine(txt);
                    logScreen.setLogScroll(logScreen.getMaxLogScroll());
                }
            }

            Matcher logEnd = LOGEND.matcher(msgString);
            Matcher logEndSearch = LOGENDSEARCH.matcher(msgString);
            if (logEnd.find() || logEndSearch.find()) {
                System.out.println("ended");
                loggingMessages = false;
                ignoreNextPadding = true;
            }
            return EventResult.CANCEL;
        }
        return EventResult.PASS;
    }
}
