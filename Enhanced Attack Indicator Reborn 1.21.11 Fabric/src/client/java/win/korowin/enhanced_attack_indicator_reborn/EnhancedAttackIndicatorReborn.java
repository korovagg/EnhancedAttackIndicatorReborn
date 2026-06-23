package win.korowin.enhanced_attack_indicator_reborn;

import net.fabricmc.api.ClientModInitializer;
import win.korowin.enhanced_attack_indicator_reborn.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;

import java.util.List;

/**
 * Main client initializer for Enhanced Attack Indicator Reborn.
 */
public class EnhancedAttackIndicatorReborn implements ClientModInitializer {

	private static final MinecraftClient client = MinecraftClient.getInstance();
	public static final String MOD_ID = "enhanced_attack_indicator_reborn";

	@Override
	public void onInitializeClient() {
		// Initialize the configuration system
		Config.init(MOD_ID, Config.class);
	}

	/**
	 * Calculates the combined progress of various player actions to be shown on the attack indicator.
	 * 
	 * @param weaponProgress The original weapon attack cooldown progress.
	 * @return The progress value to display (0.0 to 1.0, or 2.0 for special indicators).
	 */
	public static float getProgress(float weaponProgress) {

		ClientPlayerEntity player = client.player;
		if (player == null) return 1.0F;

		ItemStack mainHand = player.getMainHandStack();
		ItemStack offHand = player.getOffHandStack();
		boolean weaponShouldShow = weaponProgress < 1 || (client.targetedEntity instanceof LivingEntity le && le.isAlive() && player.getAttackCooldownProgressPerTick() > 5);

		if (Config.weaponCoolDownImportance == Config.WeaponCoolDownImportance.FIRST && weaponShouldShow)
			return weaponCooldown(mainHand.getItem(), weaponProgress);

		if (Config.showSleep) {
			int sleep = player.getSleepTimer();
			if (sleep > 0 && sleep <= 100)
				return sleep == 100 ? 2.0F : sleep / 100.0F;
		}

		if (Config.showBlockBreaking) {
			float breakingProgress = client.interactionManager != null ? client.interactionManager.getBlockBreakingProgress() : 0;

			if (breakingProgress > 0)
				return breakingProgress / 10;
		}

		if (Config.showRangeWeaponDraw) {
			ItemStack stack = player.getActiveItem();
			Item item = stack.getItem();

			if (item == Items.BOW) {
				float progress = BowItem.getPullProgress(stack.getMaxUseTime(player) - player.getItemUseTimeLeft());
				return progress == 1.0F ? 2.0F : progress;
			}
			if (item == Items.CROSSBOW) {
				float progress = (stack.getMaxUseTime(player) - player.getItemUseTimeLeft()) / (float) CrossbowItem.getPullTime(stack, player);
				return progress >= 1.0F ? 2.0F : progress;
			}
			if (item == Items.TRIDENT) {
				float progress = (stack.getMaxUseTime(player) - player.getItemUseTimeLeft()) / 10.0F;
				return progress >= 1.0F ? 2.0F : progress;
			}
		}

		if (Config.showFoodAndPotions) {
			ItemStack stack = player.getActiveItem();
			Item item = stack.getItem();
			if (item.getComponents().contains(DataComponentTypes.FOOD) || item == Items.POTION) {
				float itemCooldown = (float) player.getItemUseTime() / stack.getMaxUseTime(player);
				return itemCooldown == 0.0F ? 1.0F : itemCooldown;
			}
		}

		if (Config.showItemContainerFullness) {
			ItemStack stack = player.getMainHandStack();
			var container = stack.get(DataComponentTypes.CONTAINER);
			if (container != null) {
				List<ItemStack> items = container.stream().toList();
				int total = 0;
				int maxTotal = 64 * (27 - items.size());
				for (ItemStack item : items) {
					total += item.getCount();
					maxTotal += item.getMaxCount();
				}
				float result = (float) total / maxTotal;
				return result == 1.0F ? 2.0F : result;
			}
			var bundle = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
			if (bundle != null) {
				int total = 0;
				for (ItemStack item : bundle.iterate())
					total += item.getCount();
				return total / 64F;
			}
		}

		if (Config.weaponCoolDownImportance == Config.WeaponCoolDownImportance.MIDDLE && weaponShouldShow)
			return weaponCooldown(mainHand.getItem(), weaponProgress);

		if (Config.showItemCooldowns) {
			float cooldown = player.getItemCooldownManager().getCooldownProgress(offHand, 0);
			if (cooldown != 0.0F)
				return cooldown;

			cooldown = player.getItemCooldownManager().getCooldownProgress(mainHand, 0);
			if (cooldown != 0.0F)
				return cooldown;
		}

		if (Config.showRangeWeaponDraw && (mainHand.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(mainHand)
		                                 || offHand.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(mainHand)))
			return 2.0F;

		if (Config.weaponCoolDownImportance == Config.WeaponCoolDownImportance.LAST && weaponShouldShow)
			return weaponCooldown(mainHand.getItem(), weaponProgress);

		return 1.0F;

	}

	/**
	 * Checks if the weapon cooldown should be displayed based on configuration and item type.
	 * 
	 * @param item The item in hand.
	 * @param weaponProgress Current weapon progress.
	 * @return Adjusted progress value.
	 */
	private static float weaponCooldown(Item item, float weaponProgress) {
		if (Config.disablePickaxesAndShovels && (item.getTranslationKey().contains("pickaxe") || item.getTranslationKey().contains("shovel")))
			return 1.0F;
		if (Config.disableAxes && item.getTranslationKey().contains("axe") && !item.getTranslationKey().contains("pickaxe"))
			return 1.0F;
		return weaponProgress == 1 ? 2 : weaponProgress;

	}

}
