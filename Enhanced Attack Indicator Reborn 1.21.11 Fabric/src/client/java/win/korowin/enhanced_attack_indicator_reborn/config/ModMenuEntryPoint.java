package win.korowin.enhanced_attack_indicator_reborn.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * ModMenu entry point for providing a configuration screen.
 */
public class ModMenuEntryPoint implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // Return the configuration screen provided by MidnightConfig
        return parent -> Config.getScreen(parent, "enhanced_attack_indicator_reborn");
    }

}
