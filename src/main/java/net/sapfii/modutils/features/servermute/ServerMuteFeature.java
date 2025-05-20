package net.sapfii.modutils.features.servermute;

import dev.dfonline.flint.feature.trait.PacketListeningFeature;
import dev.dfonline.flint.util.result.EventResult;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.sapfii.modutils.ModUtils;
import net.sapfii.modutils.config.ConfigModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerMuteFeature implements PacketListeningFeature {
    private static final Pattern FAILEDSPEECH = Pattern.compile("(?<player>.+) tried to speak, but is muted\\.");
    private static final Pattern FAILEDJOIN = Pattern.compile("(?<player>.+) tried to join, but is banned \\((?<time>.+)\\)!");

    private EventResult mutePunishmentAttempts(String s) {
        Matcher matcher = FAILEDSPEECH.matcher(s);
        if (matcher.find()) {
            return EventResult.CANCEL;
        }
        matcher = FAILEDJOIN.matcher(s);
        if (matcher.find()) {
            return EventResult.CANCEL;
        }
        return EventResult.PASS;
    }

    @Override
    public EventResult onReceivePacket(Packet<?> packet) {
        if (!(packet instanceof GameMessageS2CPacket(Text msgText, boolean overlay))) {
            return EventResult.PASS;
        }
        String string = msgText.getString();

        if (string.startsWith("[ViaVersion] There is a newer plugin version available:") ||
                string.startsWith("(FAWE) An update for FastAsyncWorldEdit is available.")) {
            return EventResult.CANCEL;
        }

        if (ModUtils.CONFIG.mutePunishmentAttempts() == ConfigModel.PunishmentAttemptOptions.MUTE) {
            if (mutePunishmentAttempts(string) == EventResult.CANCEL) {
                return mutePunishmentAttempts(string);
            }
        }

        return switch (string) {
            case "[Server: Automatic saving is now enabled]",
                 "[Server: Automatic saving is now disabled]",
                 "[Server: Saved the game]" -> EventResult.CANCEL;
            default -> EventResult.PASS;
        };
    }

    @Override
    public boolean alwaysOn() {
        return true;
    }
}

