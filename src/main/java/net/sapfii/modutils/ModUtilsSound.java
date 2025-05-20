package net.sapfii.modutils;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModUtilsSound {
    String namespace;
    String path;
    public ModUtilsSound (String soundNamespace, String soundPath) {
        namespace = soundNamespace;
        path = soundPath;
        Registry.register(Registries.SOUND_EVENT, Identifier.of(namespace, path),
                SoundEvent.of(Identifier.of(namespace, path)));
    }
}
