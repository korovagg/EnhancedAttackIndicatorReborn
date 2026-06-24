package win.korowin.enhanced_attack_indicator_reborn;

import net.minecraft.resources.Identifier;

public final class EnhancedAttackIndicatorReborn {
	public static final String MOD_ID = "enhanced_attack_indicator_reborn";

	private EnhancedAttackIndicatorReborn() {
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
