package win.korowin.enhanced_attack_indicator_reborn.config;

/**
 * Configuration options for Enhanced Attack Indicator Reborn.
 */
public class Config extends MidnightConfig {

    /**
     * Enum for weapon cooldown priority relative to other indicators.
     */
    public enum WeaponCoolDownImportance { FIRST, MIDDLE, LAST }

    @Entry public static WeaponCoolDownImportance weaponCoolDownImportance = WeaponCoolDownImportance.MIDDLE;
    @Entry public static boolean disablePickaxesAndShovels = true;
    @Entry public static boolean disableAxes = false;

    @Entry public static boolean showBlockBreaking = true;
    @Entry public static boolean showRangeWeaponDraw = true;
    @Entry public static boolean showItemCooldowns = true;
    @Entry public static boolean showFoodAndPotions = true;
    @Entry public static boolean showSleep = true;
    @Entry public static boolean showItemContainerFullness = true;

}
