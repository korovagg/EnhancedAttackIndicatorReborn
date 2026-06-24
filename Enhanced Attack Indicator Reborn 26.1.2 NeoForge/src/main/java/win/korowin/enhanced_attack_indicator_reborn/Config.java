package win.korowin.enhanced_attack_indicator_reborn;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.TranslatableEnum;

public final class Config {
	public enum WeaponCoolDownImportance implements TranslatableEnum {
		FIRST,
		MIDDLE,
		LAST;

		@Override
		public Component getTranslatedName() {
			return Component.translatable(EnhancedAttackIndicatorReborn.MODID + ".configuration.enum.WeaponCoolDownImportance." + name());
		}
	}

	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

	public static final ModConfigSpec.EnumValue<WeaponCoolDownImportance> WEAPON_COOL_DOWN_IMPORTANCE = BUILDER
			.translation(EnhancedAttackIndicatorReborn.MODID + ".configuration.weaponCoolDownImportance")
			.defineEnum("weaponCoolDownImportance", WeaponCoolDownImportance.MIDDLE);

	public static final ModConfigSpec.BooleanValue DISABLE_PICKAXES_AND_SHOVELS = BUILDER
			.translation(EnhancedAttackIndicatorReborn.MODID + ".configuration.disablePickaxesAndShovels")
			.define("disablePickaxesAndShovels", true);

	public static final ModConfigSpec.BooleanValue DISABLE_AXES = BUILDER
			.translation(EnhancedAttackIndicatorReborn.MODID + ".configuration.disableAxes")
			.define("disableAxes", false);

	public static final ModConfigSpec.BooleanValue SHOW_BLOCK_BREAKING = BUILDER
			.translation(EnhancedAttackIndicatorReborn.MODID + ".configuration.showBlockBreaking")
			.define("showBlockBreaking", true);

	public static final ModConfigSpec.BooleanValue SHOW_RANGE_WEAPON_DRAW = BUILDER
			.translation(EnhancedAttackIndicatorReborn.MODID + ".configuration.showRangeWeaponDraw")
			.define("showRangeWeaponDraw", true);

	public static final ModConfigSpec.BooleanValue SHOW_ITEM_COOLDOWNS = BUILDER
			.translation(EnhancedAttackIndicatorReborn.MODID + ".configuration.showItemCooldowns")
			.define("showItemCooldowns", true);

	public static final ModConfigSpec.BooleanValue SHOW_FOOD_AND_POTIONS = BUILDER
			.translation(EnhancedAttackIndicatorReborn.MODID + ".configuration.showFoodAndPotions")
			.define("showFoodAndPotions", true);

	public static final ModConfigSpec.BooleanValue SHOW_SLEEP = BUILDER
			.translation(EnhancedAttackIndicatorReborn.MODID + ".configuration.showSleep")
			.define("showSleep", true);

	public static final ModConfigSpec.BooleanValue SHOW_ITEM_CONTAINER_FULLNESS = BUILDER
			.translation(EnhancedAttackIndicatorReborn.MODID + ".configuration.showItemContainerFullness")
			.define("showItemContainerFullness", true);

	static final ModConfigSpec SPEC = BUILDER.build();

	private Config() {
	}
}
