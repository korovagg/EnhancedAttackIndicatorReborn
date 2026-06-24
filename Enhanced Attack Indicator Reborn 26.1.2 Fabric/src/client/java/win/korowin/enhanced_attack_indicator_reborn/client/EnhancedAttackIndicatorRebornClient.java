package win.korowin.enhanced_attack_indicator_reborn.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import win.korowin.enhanced_attack_indicator_reborn.client.config.Config;

import java.util.List;

public class EnhancedAttackIndicatorRebornClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Config.load();
	}

	public static float getProgress(float weaponProgress) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return 1.0F;

		ItemStack mainHand = player.getMainHandItem();
		ItemStack offHand = player.getOffhandItem();

		boolean weaponShouldShow = weaponProgress < 1
				|| Minecraft.getInstance().crosshairPickEntity instanceof LivingEntity le
				&& le.isAlive()
				&& player.getCurrentItemAttackStrengthDelay() > 5;

		if (Config.weaponCoolDownImportance == Config.WeaponCoolDownImportance.FIRST && weaponShouldShow) {
			return weaponCooldown(mainHand.getItem(), weaponProgress);
		}

		if (Config.showSleep) {
			int sleep = player.getSleepTimer();
			if (sleep > 0 && sleep <= 100) {
				return sleep == 100 ? 2.0F : sleep / 100.0F;
			}
		}

		if (Config.showBlockBreaking) {
			if (Minecraft.getInstance().gameMode != null) {
				float breakingProgress = Minecraft.getInstance().gameMode.getDestroyStage();
				if (breakingProgress > 0) return breakingProgress / 10;
			}
		}

		if (Config.showRangeWeaponDraw) {
			ItemStack stack = player.getUseItem();
			Item item = stack.getItem();

			if (item == Items.BOW) {
				float progress = BowItem.getPowerForTime(72000 - player.getUseItemRemainingTicks());
				return progress == 1.0F ? 2.0F : progress;
			}
			if (item == Items.CROSSBOW) {
				float progress = (stack.getUseDuration(player) - player.getUseItemRemainingTicks())
						/ (float) CrossbowItem.getChargeDuration(stack, player);
				return progress >= 1.0F ? 2.0F : progress;
			}
			if (item == Items.TRIDENT) {
				float progress = (stack.getUseDuration(player) - player.getUseItemRemainingTicks()) / 10.0F;
				return progress >= 1.0F ? 2.0F : progress;
			}
		}

		if (Config.showFoodAndPotions) {
			ItemStack stack = player.getUseItem();
			Item item = stack.getItem();
			if (item.components().has(DataComponents.FOOD) || item == Items.POTION) {
				int duration = stack.getUseDuration(player);
				if (duration <= 0) return 1.0F;
				int used = duration - player.getUseItemRemainingTicks();
				return used / (float) duration;
			}
		}

		if (Config.showItemContainerFullness) {
			ItemStack stack = player.getMainHandItem();
			var container = stack.get(DataComponents.CONTAINER);
			if (container != null) {
				List<ItemStack> items = container.allItemsCopyStream().toList();
				int total = 0;
				int maxTotal = 64 * (27 - items.size());
				for (ItemStack item : items) {
					if (item.isEmpty()) {
						maxTotal += 64;
						continue;
					}
					total += item.getCount();
					maxTotal += item.getMaxStackSize();
				}
				float result = (float) total / maxTotal;
				return result == 1.0F ? 2.0F : result;
			}
			var bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
			if (bundle != null) {
				int total = 0;
				for (ItemStack item : bundle.itemCopyStream().toList()) total += item.getCount();
				return total / 64F;
			}
		}

		if (Config.weaponCoolDownImportance == Config.WeaponCoolDownImportance.MIDDLE && weaponShouldShow) {
			return weaponCooldown(mainHand.getItem(), weaponProgress);
		}

		if (Config.showItemCooldowns) {
			float cooldown = player.getCooldowns().getCooldownPercent(offHand, 0);
			if (cooldown != 0.0F) return cooldown;

			cooldown = player.getCooldowns().getCooldownPercent(mainHand, 0);
			if (cooldown != 0.0F) return cooldown;
		}

		if (Config.showRangeWeaponDraw
				&& (mainHand.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(mainHand)
				|| offHand.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(mainHand))) {
			return 2.0F;
		}

		if (Config.weaponCoolDownImportance == Config.WeaponCoolDownImportance.LAST && weaponShouldShow) {
			return weaponCooldown(mainHand.getItem(), weaponProgress);
		}

		return 1.0F;
	}

	private static float weaponCooldown(Item item, float weaponProgress) {
		String id = item.getDescriptionId();
		if (Config.disablePickaxesAndShovels && (id.contains("pickaxe") || id.contains("shovel"))) return 1.0F;
		if (Config.disableAxes && id.contains("axe") && !id.contains("pickaxe")) return 1.0F;
		return weaponProgress == 1 ? 2 : weaponProgress;
	}
}
