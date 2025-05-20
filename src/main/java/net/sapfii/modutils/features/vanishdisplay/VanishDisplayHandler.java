package net.sapfii.modutils.features.vanishdisplay;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.feature.trait.PacketListeningFeature;
import dev.dfonline.flint.feature.trait.RenderedFeature;
import dev.dfonline.flint.feature.trait.UserCommandListeningFeature;
import dev.dfonline.flint.util.result.EventResult;
import dev.dfonline.flint.util.result.ReplacementEventResult;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;


public class VanishDisplayHandler implements RenderedFeature, PacketListeningFeature, UserCommandListeningFeature {

    Text MODVANISHOFF = Text.literal("[MOD] ").withColor(0xFF00a700).append(Text.literal("Mod Vanish disabled.").withColor(0xFFFFFFFF));
    Text MODVANISHON = Text.literal("[MOD] ").withColor(0xFF00a700).append(Text.literal("Mod Vanish enabled.").withColor(0xFFFFFFFF));
    Text ADMINVOFF = Text.literal("[ADMIN] ").withColor(0xFFfb2900).append(Text.literal("Admin Vanish disabled.").withColor(0xFFFFFFFF));
    Text ADMINVON = Text.literal("[ADMIN] ").withColor(0xFFfb2900).append(Text.literal("Admin Vanish enabled.").withColor(0xFFFFFFFF));
    Text ADMINVALREADYON = Text.literal("Error: ").withColor(0xFFfb5454).append(Text.literal("Admin vanish is already enabled.").withColor(0xFFa8a8a8));
    Text ADMINVALREADYOFF = Text.literal("Error: ").withColor(0xFFfb5454).append(Text.literal("Admin vanish is already disabled.").withColor(0xFFa8a8a8));

    Text INADMINV = Text.literal("Admin").withColor(0xfb2901).append(Text.literal(" Vanish").withColor(0xFFFFFF));
    Text INMODVANISH = Text.literal("Mod").withColor(0x54fb54).append(Text.literal(" Vanish").withColor(0xFFFFFF));
    Text INADMINMODV = Text.literal("Mod & Admin").withColor(0xfff630).append(Text.literal(" Vanish").withColor(0xFFFFFF));

    boolean inModVanish = false;
    boolean inAdminVanish = false;

    VanishDisplayData vanishDisplayData;

    public VanishDisplayHandler(VanishDisplayData vanishDisplayData) {
        this.vanishDisplayData = vanishDisplayData;
    }

    @Override
    public EventResult onReceivePacket(Packet<?> packet) {
        if (!(packet instanceof GameMessageS2CPacket(Text msgText, boolean overlay))) {
            return EventResult.PASS;
        }
        String string = msgText.getString();
        if (string.startsWith("» Joined game: ")) {
            if (inModVanish) {
                inModVanish = false;
                Flint.getUser().getPlayer().sendMessage(MODVANISHOFF, false);
            }
        }
        switch (string) {
            case "» Vanish disabled. You will now be visible to other players.":
                didModVanishCmd = false;
                didAdminVCmd = false;
                return EventResult.CANCEL;
            case "» Vanish enabled. You will not be visible to other players.":
                if (!didModVanishCmd && !didAdminVCmd) {
                    inModVanish = true;
                }
                didModVanishCmd = false;
                didAdminVCmd = false;
                return EventResult.CANCEL;
            case "[ADMIN] You are currently vanished!":
                inAdminVanish = true;
                break;
        }
        return EventResult.PASS;
    }

    @Override
    public void render(DrawContext draw, RenderTickCounter renderTickCounter) {
        TextRenderer textRenderer = Flint.getClient().textRenderer;
        if (inModVanish && inAdminVanish) {
            vanishDisplayData.text = INADMINMODV;
            vanishDisplayData.boxColor = 0x7Fb89120;
        } else if (inModVanish) {
            vanishDisplayData.text = INMODVANISH;
            vanishDisplayData.boxColor = 0x7F2a8f48;
        } else if (inAdminVanish) {
            vanishDisplayData.text = INADMINV;
            vanishDisplayData.boxColor = 0x7Fc82b51;
        } else {
            vanishDisplayData.expectedOffsetY = -textRenderer.fontHeight - 12;
        }
        if (inModVanish || inAdminVanish) {
            vanishDisplayData.expectedOffsetY = 0;
        }
    }

    long vanishCmdCooldown = System.currentTimeMillis();
    boolean didModVanishCmd = false;
    boolean didAdminVCmd = false;

    @Override
    public ReplacementEventResult<String> sendCommand(String s) {
        ClientPlayerEntity player = Flint.getUser().getPlayer();
        if (System.currentTimeMillis() - vanishCmdCooldown < 100) {
            return ReplacementEventResult.pass();
        }
        vanishCmdCooldown = System.currentTimeMillis();
        switch (s) {
            case "s", "spawn":
                spawnCmd(player);
                break;
            case "mod v", "mod vanish":
                modvCmd(player);
                break;
            case "adminv on":
                adminvOnCmd(player);
                break;
            case "adminv off":
                adminvOffCmd(player);
                break;
        }
        return ReplacementEventResult.pass();
    }

    private void spawnCmd(ClientPlayerEntity player) {
        if (inModVanish) {
            inModVanish = false;
            player.sendMessage(MODVANISHOFF, false);
        }
    }

    private void modvCmd(ClientPlayerEntity player) {
        didModVanishCmd = true;
        if (inModVanish) {
            inModVanish = false;
            player.sendMessage(MODVANISHOFF, false);
        } else {
            inModVanish = true;
            player.sendMessage(MODVANISHON, false);
            if (!inAdminVanish) {
                vanishDisplayData.finishAnimation("Mod Vanish");
            }
        }
    }

    private void adminvOnCmd(ClientPlayerEntity player) {
        if (!inAdminVanish) {
            player.sendMessage(ADMINVON, false);
            didAdminVCmd = true;
            if (!inModVanish) {
                vanishDisplayData.finishAnimation("Admin Vanish");
            }
        } else {
            player.sendMessage(ADMINVALREADYON, false);
            player.playSound(SoundEvents.ENTITY_SHULKER_HURT_CLOSED);
        }
        inAdminVanish = true;
    }

    private void adminvOffCmd(ClientPlayerEntity player) {
        if (!inAdminVanish) {
            player.sendMessage(ADMINVALREADYOFF, false);
            player.playSound(SoundEvents.ENTITY_SHULKER_HURT_CLOSED);
        } else {
            player.sendMessage(ADMINVOFF, false);
        }
        inAdminVanish = false;
    }

    @Override
    public boolean alwaysOn() {
        return true;
    }
}
