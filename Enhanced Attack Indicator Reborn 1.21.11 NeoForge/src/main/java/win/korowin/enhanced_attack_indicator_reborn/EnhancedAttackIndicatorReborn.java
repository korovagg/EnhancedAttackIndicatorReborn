package win.korowin.enhanced_attack_indicator_reborn;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.List;

@Mod(EnhancedAttackIndicatorReborn.MODID)
public class EnhancedAttackIndicatorReborn {
    public static final String MODID = "enhanced_attack_indicator_reborn";

    public EnhancedAttackIndicatorReborn(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
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

        if (Config.WEAPON_COOL_DOWN_IMPORTANCE.get() == Config.WeaponCoolDownImportance.FIRST && weaponShouldShow) {
            return weaponCooldown(mainHand.getItem(), weaponProgress);
        }

        if (Config.SHOW_SLEEP.get()) {
            int sleep = player.getSleepTimer();
            if (sleep > 0 && sleep <= 100) {
                return sleep == 100 ? 2.0F : sleep / 100.0F;
            }
        }

        if (Config.SHOW_BLOCK_BREAKING.get()) {
            if (Minecraft.getInstance().gameMode != null) {
                float breakingProgress = Minecraft.getInstance().gameMode.getDestroyStage();
                if (breakingProgress > 0) return breakingProgress / 10;
            }
        }

        if (Config.SHOW_RANGE_WEAPON_DRAW.get()) {
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

        if (Config.SHOW_FOOD_AND_POTIONS.get()) {
            ItemStack stack = player.getUseItem();
            Item item = stack.getItem();
            if (item.components().has(DataComponents.FOOD) || item == Items.POTION) {
                float itemCooldown = (float) player.getUseItemRemainingTicks() / stack.getUseDuration(player);
                return itemCooldown == 0.0F ? 1.0F : itemCooldown;
            }
        }

        if (Config.SHOW_ITEM_CONTAINER_FULLNESS.get()) {
            ItemStack stack = player.getMainHandItem();
            var container = stack.get(DataComponents.CONTAINER);
            if (container != null) {
                List<ItemStack> items = container.stream().toList();
                int total = 0;
                int maxTotal = 64 * (27 - items.size());
                for (ItemStack item : items) {
                    total += item.getCount();
                    maxTotal += item.getMaxStackSize();
                }
                float result = (float) total / maxTotal;
                return result == 1.0F ? 2.0F : result;
            }
            var bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
            if (bundle != null) {
                int total = 0;
                for (ItemStack item : bundle.items()) total += item.getCount();
                return total / 64F;
            }
        }

        if (Config.WEAPON_COOL_DOWN_IMPORTANCE.get() == Config.WeaponCoolDownImportance.MIDDLE && weaponShouldShow) {
            return weaponCooldown(mainHand.getItem(), weaponProgress);
        }

        if (Config.SHOW_ITEM_COOLDOWNS.get()) {
            float cooldown = player.getCooldowns().getCooldownPercent(offHand, 0);
            if (cooldown != 0.0F) return cooldown;

            cooldown = player.getCooldowns().getCooldownPercent(mainHand, 0);
            if (cooldown != 0.0F) return cooldown;
        }

        if (Config.SHOW_RANGE_WEAPON_DRAW.get()
                && (mainHand.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(mainHand)
                || offHand.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(mainHand))) {
            return 2.0F;
        }

        if (Config.WEAPON_COOL_DOWN_IMPORTANCE.get() == Config.WeaponCoolDownImportance.LAST && weaponShouldShow) {
            return weaponCooldown(mainHand.getItem(), weaponProgress);
        }

        return 1.0F;
    }

    private static float weaponCooldown(Item item, float weaponProgress) {
        String id = item.getDescriptionId();
        if (Config.DISABLE_PICKAXES_AND_SHOVELS.get() && (id.contains("pickaxe") || id.contains("shovel"))) return 1.0F;
        if (Config.DISABLE_AXES.get() && id.contains("axe") && !id.contains("pickaxe")) return 1.0F;
        return weaponProgress == 1 ? 2 : weaponProgress;
    }
}

