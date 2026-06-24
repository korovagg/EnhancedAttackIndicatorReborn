package win.korowin.enhanced_attack_indicator_reborn.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
	public enum WeaponCoolDownImportance {
		FIRST,
		MIDDLE,
		LAST
	}

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("enhanced_attack_indicator_reborn.json");

	public static WeaponCoolDownImportance weaponCoolDownImportance = WeaponCoolDownImportance.MIDDLE;
	public static boolean disablePickaxesAndShovels = true;
	public static boolean disableAxes = false;
	public static boolean showBlockBreaking = true;
	public static boolean showRangeWeaponDraw = true;
	public static boolean showItemCooldowns = true;
	public static boolean showFoodAndPotions = true;
	public static boolean showSleep = true;
	public static boolean showItemContainerFullness = true;

	private Config() {
	}

	public static void load() {
		if (!Files.exists(PATH)) {
			save();
			return;
		}
		try (var reader = Files.newBufferedReader(PATH)) {
			ConfigData data = GSON.fromJson(reader, ConfigData.class);
			if (data == null) return;
			weaponCoolDownImportance = data.weaponCoolDownImportance;
			disablePickaxesAndShovels = data.disablePickaxesAndShovels;
			disableAxes = data.disableAxes;
			showBlockBreaking = data.showBlockBreaking;
			showRangeWeaponDraw = data.showRangeWeaponDraw;
			showItemCooldowns = data.showItemCooldowns;
			showFoodAndPotions = data.showFoodAndPotions;
			showSleep = data.showSleep;
			showItemContainerFullness = data.showItemContainerFullness;
		} catch (Exception ignored) {
			save();
		}
	}

	public static void save() {
		try {
			Files.createDirectories(PATH.getParent());
			ConfigData data = new ConfigData();
			data.weaponCoolDownImportance = weaponCoolDownImportance;
			data.disablePickaxesAndShovels = disablePickaxesAndShovels;
			data.disableAxes = disableAxes;
			data.showBlockBreaking = showBlockBreaking;
			data.showRangeWeaponDraw = showRangeWeaponDraw;
			data.showItemCooldowns = showItemCooldowns;
			data.showFoodAndPotions = showFoodAndPotions;
			data.showSleep = showSleep;
			data.showItemContainerFullness = showItemContainerFullness;
			Files.writeString(PATH, GSON.toJson(data));
		} catch (Exception ignored) {
		}
	}

	private static final class ConfigData {
		WeaponCoolDownImportance weaponCoolDownImportance = WeaponCoolDownImportance.MIDDLE;
		boolean disablePickaxesAndShovels = true;
		boolean disableAxes = false;
		boolean showBlockBreaking = true;
		boolean showRangeWeaponDraw = true;
		boolean showItemCooldowns = true;
		boolean showFoodAndPotions = true;
		boolean showSleep = true;
		boolean showItemContainerFullness = true;
	}
}

