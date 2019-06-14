package p455w0rd.ae2wtlib.init;

import java.io.File;

import appeng.core.AEConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author p455w0rd
 *
 */
public class LibConfig {

	public static Configuration CONFIG;
	private static final String DEF_CAT = "General";
	public static final String CLIENT_CAT = "Client Configs";

	public static boolean WT_BOOSTER_ENABLED = true;
	public static boolean USE_OLD_INFINTY_MECHANIC = false;
	public static boolean WT_ENABLE_CONTROLLER_CHUNKLOADER = false;
	public static boolean WT_WITHER_DROPS_BOOSTER = true;
	public static boolean WT_DRAGON_DROPS_BOOSTER = true;
	public static int WT_BOOSTER_DROP_CHANCE = 30;
	public static int WT_MAX_POWER = AEConfig.instance().getWirelessTerminalBattery();
	public static boolean WT_DISABLE_BOOSTER_RECIPE = false;
	public static int INFINITY_ENERGY_PER_BOOSTER_CARD = 100;
	public static int INFINITY_ENERGY_DRAIN = 15;
	public static int INFINTY_ENERGY_LOW_WARNING_AMOUNT = 5000;
	public static boolean WT_ENDERMAN_DROP_BOOSTERS = true;
	public static int WT_ENDERMAN_BOOSTER_DROP_CHANCE = 5;
	public static boolean SHIFT_CLICK_BAUBLES = true;
	public static boolean ENABLE_COLORED_LIGHTING = true;

	@SubscribeEvent
	public void onConfigChange(final ConfigChangedEvent.OnConfigChangedEvent e) {
		if (e.getModID().equals(LibGlobals.MODID)) {
			preInit();
		}
	}

	public static void preInit() {
		if (CONFIG == null) {
			CONFIG = new Configuration(new File(LibGlobals.CONFIG_FILE));
			MinecraftForge.EVENT_BUS.register(new LibConfig());
		}
		WT_BOOSTER_ENABLED = CONFIG.getBoolean("EnableBooster", DEF_CAT, true, "Enable Infinity Booster Card");
		WT_ENABLE_CONTROLLER_CHUNKLOADER = CONFIG.getBoolean("EnableControllerChunkLoading", DEF_CAT, false, "If true, AE2 controller will chunk load itself");
		WT_BOOSTER_DROP_CHANCE = CONFIG.getInt("WitherBoosterDropChance", DEF_CAT, 30, 1, 100, "Percentage chance that booster card will drop upon killing a wither. (between 1 and 100)");
		WT_ENDERMAN_DROP_BOOSTERS = CONFIG.getBoolean("EndermanDropBoosters", DEF_CAT, true, "Will Enderman randomly drop infinity booster cards on death?");
		WT_ENDERMAN_BOOSTER_DROP_CHANCE = CONFIG.getInt("EndermanBoosterDropChance", DEF_CAT, 5, 1, 100, "Percentage chance that booster card will drop upon killing an Enderman. (between 1 and 100)");
		WT_WITHER_DROPS_BOOSTER = CONFIG.getBoolean("WitherDropsBooster", DEF_CAT, true, "Should Withers drop Infinity Booster Card?");
		WT_DRAGON_DROPS_BOOSTER = CONFIG.getBoolean("DragonDropsBooster", DEF_CAT, true, "Should Dragons drop Infinity Booster Card?");
		WT_DISABLE_BOOSTER_RECIPE = CONFIG.getBoolean("DisableBoosterRecipe", DEF_CAT, false, "Should Infinity Booster Card Recipe be disabled?");
		USE_OLD_INFINTY_MECHANIC = CONFIG.getBoolean("UseOldInfinityMechanic", DEF_CAT, false, "If true, then simply inserting 1 Infinity Booster Card into the slot, will give limitless infinite range.");
		INFINITY_ENERGY_PER_BOOSTER_CARD = CONFIG.getInt("InfinityEnergyPerBooster", DEF_CAT, 100, 5, 1000, "Amount of Infinity Energy 1 Infinity Booster Card will convert to");
		INFINITY_ENERGY_DRAIN = CONFIG.getInt("InfinityEnergyDrainAmount", DEF_CAT, 15, 5, 100, "Amount of Infinity Energy Consumed every 10 ticks when not in range of a WAP");
		INFINTY_ENERGY_LOW_WARNING_AMOUNT = CONFIG.getInt("InfinityEnergyWarningAmount", CLIENT_CAT, 5000, 10, 1000000, "Wireless Terminal will show a warning when Infinty Energy drops below this point and infinite range is active");
		SHIFT_CLICK_BAUBLES = CONFIG.getBoolean("ShiftClickBaubles", CLIENT_CAT, true, "Will shift-clicking a bauble try to put said bauble in a bauble slot before trying to store in system");
		ENABLE_COLORED_LIGHTING = CONFIG.getBoolean("EnableColoredLightSupport", CLIENT_CAT, true, "If enabled, colored light support will be enabled");
		if (CONFIG.hasChanged()) {
			CONFIG.save();
		}
	}

}
