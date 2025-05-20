package net.sapfii.modutils;

import dev.dfonline.flint.FlintAPI;
import net.fabricmc.api.ClientModInitializer;
import net.sapfii.modutils.config.ModUtilsConfig;
import net.sapfii.modutils.features.history.HistoryFeature;
import net.sapfii.modutils.features.log.LogFeature;
import net.sapfii.modutils.features.reportdisplay.ReportHandler;
import net.sapfii.modutils.features.servermute.ServerMuteFeature;
import net.sapfii.modutils.features.vanishdisplay.VanishDisplay;
import net.sapfii.modutils.features.vanishdisplay.VanishDisplayData;
import net.sapfii.modutils.features.vanishdisplay.VanishDisplayHandler;

public class ModUtils implements ClientModInitializer {
    public static final ModUtilsConfig CONFIG = ModUtilsConfig.createAndLoad();

    @Override
    public void onInitializeClient() {
        System.out.println("ModUtils Initializing...");
        VanishDisplayData vanishDisplayData = new VanishDisplayData();
        FlintAPI.registerFeatures(
            new VanishDisplayHandler(vanishDisplayData),
            new VanishDisplay(vanishDisplayData),
            new ServerMuteFeature(),
            new ReportHandler(),
            new LogFeature(),
            new HistoryFeature()
        );
        new ModUtilsSound("modutils", "report_dismiss");

    }
}
