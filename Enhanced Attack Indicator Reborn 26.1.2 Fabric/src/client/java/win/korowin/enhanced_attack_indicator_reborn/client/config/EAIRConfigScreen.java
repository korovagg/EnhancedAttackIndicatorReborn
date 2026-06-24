package win.korowin.enhanced_attack_indicator_reborn.client.config;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import win.korowin.enhanced_attack_indicator_reborn.EnhancedAttackIndicatorReborn;

import java.util.Arrays;
import java.util.List;

public final class EAIRConfigScreen extends OptionsSubScreen {
	private static final String PREFIX = EnhancedAttackIndicatorReborn.MOD_ID + ".configuration.";

	private final OptionInstance<Config.WeaponCoolDownImportance> weaponCoolDownImportance;
	private final OptionInstance<Boolean> disablePickaxesAndShovels;
	private final OptionInstance<Boolean> disableAxes;
	private final OptionInstance<Boolean> showBlockBreaking;
	private final OptionInstance<Boolean> showRangeWeaponDraw;
	private final OptionInstance<Boolean> showItemCooldowns;
	private final OptionInstance<Boolean> showFoodAndPotions;
	private final OptionInstance<Boolean> showSleep;
	private final OptionInstance<Boolean> showItemContainerFullness;

	public EAIRConfigScreen(Screen parent) {
		super(parent, Minecraft.getInstance().options, Component.translatable(PREFIX + "title"));

		Codec<Config.WeaponCoolDownImportance> importanceCodec = Codec.STRING.xmap(
				Config.WeaponCoolDownImportance::valueOf,
				Enum::name
		);
		List<Config.WeaponCoolDownImportance> importanceValues = Arrays.asList(Config.WeaponCoolDownImportance.values());
		OptionInstance.Enum<Config.WeaponCoolDownImportance> importanceValueSet = new OptionInstance.Enum<>(importanceValues, importanceCodec);

		this.weaponCoolDownImportance = new OptionInstance<>(
				PREFIX + "weaponCoolDownImportance",
				OptionInstance.noTooltip(),
				(caption, value) -> Component.translatable(PREFIX + "enum.WeaponCoolDownImportance." + value.name()),
				importanceValueSet,
				importanceCodec,
				Config.weaponCoolDownImportance,
				value -> Config.weaponCoolDownImportance = value
		);

		this.disablePickaxesAndShovels = OptionInstance.createBoolean(
				PREFIX + "disablePickaxesAndShovels",
				Config.disablePickaxesAndShovels,
				value -> Config.disablePickaxesAndShovels = value
		);
		this.disableAxes = OptionInstance.createBoolean(
				PREFIX + "disableAxes",
				Config.disableAxes,
				value -> Config.disableAxes = value
		);
		this.showBlockBreaking = OptionInstance.createBoolean(
				PREFIX + "showBlockBreaking",
				Config.showBlockBreaking,
				value -> Config.showBlockBreaking = value
		);
		this.showRangeWeaponDraw = OptionInstance.createBoolean(
				PREFIX + "showRangeWeaponDraw",
				Config.showRangeWeaponDraw,
				value -> Config.showRangeWeaponDraw = value
		);
		this.showItemCooldowns = OptionInstance.createBoolean(
				PREFIX + "showItemCooldowns",
				Config.showItemCooldowns,
				value -> Config.showItemCooldowns = value
		);
		this.showFoodAndPotions = OptionInstance.createBoolean(
				PREFIX + "showFoodAndPotions",
				Config.showFoodAndPotions,
				value -> Config.showFoodAndPotions = value
		);
		this.showSleep = OptionInstance.createBoolean(
				PREFIX + "showSleep",
				Config.showSleep,
				value -> Config.showSleep = value
		);
		this.showItemContainerFullness = OptionInstance.createBoolean(
				PREFIX + "showItemContainerFullness",
				Config.showItemContainerFullness,
				value -> Config.showItemContainerFullness = value
		);
	}

	@Override
	protected void addOptions() {
		this.list.addBig(this.weaponCoolDownImportance);
		this.list.addSmall(
				this.showBlockBreaking,
				this.showRangeWeaponDraw,
				this.showItemCooldowns,
				this.showFoodAndPotions,
				this.showSleep,
				this.showItemContainerFullness,
				this.disablePickaxesAndShovels,
				this.disableAxes
		);
	}

	@Override
	public void removed() {
		super.removed();
		Config.save();
	}
}

