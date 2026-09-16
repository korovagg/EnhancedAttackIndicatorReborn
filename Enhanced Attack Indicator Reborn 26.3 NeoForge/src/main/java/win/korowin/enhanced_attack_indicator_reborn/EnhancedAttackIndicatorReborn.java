package win.korowin.enhanced_attack_indicator_reborn;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(EnhancedAttackIndicatorReborn.MODID)
public final class EnhancedAttackIndicatorReborn {
    public static final String MODID = "enhanced_attack_indicator_reborn";

    // Registers the client config so NeoForge can create and persist the settings file.
    public EnhancedAttackIndicatorReborn(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }
}
