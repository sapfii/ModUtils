package net.sapfii.modutils.features.reportdisplay;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.dfonline.flint.Flint;
import dev.dfonline.flint.feature.trait.CommandFeature;
import dev.dfonline.flint.feature.trait.PacketListeningFeature;
import dev.dfonline.flint.feature.trait.RenderedFeature;
import dev.dfonline.flint.util.result.EventResult;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.sapfii.modutils.GUIKeyBinding;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportHandler implements RenderedFeature, PacketListeningFeature, CommandFeature {
    private static final Pattern REPORTRGX = Pattern.compile("! Incoming Report \\((?<reporter>.+)\\)\nOffender: (?<offender>.+)\nOffense: (?<offense>.+)\nLocation: (?<location>.+)");

    ReportList reports = new ReportList();
    ArrayList<Report> allReports = new ArrayList<>();
    ReportScreen reportScreen;
    boolean openReportsScreen = false;

    long lastClear = System.currentTimeMillis();

    private final GUIKeyBinding keyBinding = new GUIKeyBinding(
            "key.modutils.dismissReports",
            InputUtil.Type.KEYSYM,
            InputUtil.GLFW_KEY_J,
            "key.modutils.category"
    );

    public ReportHandler() {
        KeyBindingHelper.registerKeyBinding(keyBinding);
    }

    public void openScreen(ClientPlayerEntity player) {
        if (!reports.isEmpty()) {
            for (Report report : reports) {
                report.data.beingRemoved = true;
            }
            player.playSound(SoundEvent.of(Identifier.of("modutils", "report_dismiss")));
        }
        reportScreen = new ReportScreen(Text.empty());
        MinecraftClient.getInstance().setScreen(reportScreen);
        reportScreen.addReport(OrderedText.empty());
        for (Report report : allReports) {
            for (OrderedText text : report.data.texts) {
                reportScreen.addReport(text);
            }
            reportScreen.addReport(OrderedText.empty());
        }
    }

    @Override
    public void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        ClientPlayerEntity player = Flint.getUser().getPlayer();
        boolean ctrlPressed = InputUtil.isKeyPressed(Flint.getClient().getWindow().getHandle(), InputUtil.GLFW_KEY_LEFT_CONTROL);
        if (openReportsScreen) {
            openReportsScreen = false;
            openScreen(player);
        }
        if (keyBinding.isPressed() && !reports.isEmpty() && System.currentTimeMillis() - lastClear > 500) {
            lastClear = System.currentTimeMillis();
            if (ctrlPressed) {
                for (Report report : reports) {
                    report.data.beingRemoved = true;
                }
            } else {
                reports.getFirst().data.beingRemoved = true;
            }
            player.playSound(SoundEvent.of(Identifier.of("modutils", "report_dismiss")));
        }
    }

    @Override
    public EventResult onReceivePacket(Packet<?> packet) {
        if (!(packet instanceof GameMessageS2CPacket(Text msgText, boolean overlay))) {
            return EventResult.PASS;
        }
        String string = msgText.getString();
        Matcher matcher = REPORTRGX.matcher(string);
        if (matcher.find()) {
            Report report = new Report(
                    matcher.group("reporter"),
                    matcher.group("offender"),
                    matcher.group("offense"),
                    matcher.group("location"),
                    reports
            );
            allReports.addFirst(report);
            return EventResult.CANCEL;
        }
        return EventResult.PASS;
    }

    @Override
    public String commandName() {
        return "reports";
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand(LiteralArgumentBuilder<FabricClientCommandSource> literalArgumentBuilder, CommandRegistryAccess commandRegistryAccess) {
        return literalArgumentBuilder.executes(this::run);
    }

    private int run(CommandContext<FabricClientCommandSource> fabricClientCommandSourceCommandContext) {
        openReportsScreen = true;
        return 0;
    }
}
