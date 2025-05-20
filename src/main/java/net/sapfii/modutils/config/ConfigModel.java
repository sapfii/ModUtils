package net.sapfii.modutils.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.SectionHeader;

@Modmenu(modId = "modutils")
@Config(name = "modutils-config", wrapperName = "ModUtilsConfig")
public class ConfigModel {

    @SectionHeader("logs")
    public LogDirections logDirection = LogDirections.UP;

    @SectionHeader("serverMute")
    public PunishmentAttemptOptions mutePunishmentAttempts = PunishmentAttemptOptions.MUTE;


    public enum LogDirections {
        UP, DOWN
    }
    public enum PunishmentAttemptOptions {
        MUTE, DONT_MUTE
    }
}
