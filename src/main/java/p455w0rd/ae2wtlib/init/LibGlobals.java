package p455w0rd.ae2wtlib.init;

import net.minecraft.util.ResourceLocation;
import p455w0rd.ae2wtlib.api.WTGlobals;

public class LibGlobals extends WTGlobals {

	public static final String MODID = "ae2wtlib";
	public static final String VERSION = "1.0.11";
	public static final String NAME = "AE2 Wireless Terminal Library (AE2WTLib)";
	public static final String SERVER_PROXY = "p455w0rd.ae2wtlib.proxy.CommonProxy";
	public static final String CLIENT_PROXY = "p455w0rd.ae2wtlib.proxy.ClientProxy";
	public static final String DEP_LIST = "required-after:appliedenergistics2@[rv6-stable-6,);" + p455w0rdslib.LibGlobals.REQUIRE_DEP + ";after:baubles";
	public static final String CONFIG_FILE = "config/AE2WirelessTerminals.cfg";
	public static final String REQUIRE_DEP = "required-after:" + MODID + "@[" + VERSION + ",);";
	private static final String ITEMGROUP_UNLOCALIZED_NAME = "itemGroup." + MODID;
	private static final String INFINITY_BOOSTER_CARD_UNLOCALIZED_NAME = "item." + MODID + ":infinity_booster_card.name";

	private static final ResourceLocation STATES_TEXTURE = new ResourceLocation(MODID, "textures/gui/states.png");

	private static final WTTooltips TOOLTIPS = new Tooltips();
	private static final WTNBTTagNames TAG_NAMES = new NBTTagNames();

	static class Tooltips extends WTTooltips {

		private static final String INFINITE_RANGE = "tooltip.infinite_range.desc";
		private static final String INFINITY_ENERGY = "tooltip.infinity_energy.desc";
		private static final String INSTALLED = "tooltip.installed.desc";
		private static final String IN_WAP_RANGE = "tooltip.in_wap_range.desc";
		private static final String OUT_OF = "tooltip.out_of.desc";
		private static final String INFINITY_ENERGY_LOW = "tooltip.infinity_energy_low.desc";
		private static final String CREATIVE = "tooltip.creative.desc";
		private static final String INFINITE = "tooltip.infinite.desc";
		private static final String PRESS_SHIFT = "tooltip.press_shift.desc";
		private static final String OR_PRESS = "tooltip.or_press.desc";
		private static final String PRESS = "tooltip.press.desc";
		private static final String ACTIVE = "tooltip.active.desc";
		private static final String INACTIVE = "tooltip.inactive.desc";
		private static final String NOT = "tooltip.not.desc";
		private static final String UNITS = "tooltip.units.desc";

		private static final String JEI_CANBEWORN = "jei.wt_bauble.desc";

		@Override
		public String infiniteRange() {
			return INFINITE_RANGE;
		}

		@Override
		public String infinityEnergy() {
			return INFINITY_ENERGY;
		}

		@Override
		public String installed() {
			return INSTALLED;
		}

		@Override
		public String inWapRange() {
			return IN_WAP_RANGE;
		}

		@Override
		public String outOf() {
			return OUT_OF;
		}

		@Override
		public String infinityEnergyLow() {
			return INFINITY_ENERGY_LOW;
		}

		@Override
		public String creative() {
			return CREATIVE;
		}

		@Override
		public String infinite() {
			return INFINITE;
		}

		@Override
		public String jeiCanBeWorn() {
			return JEI_CANBEWORN;
		}

		@Override
		public String pressShift() {
			return PRESS_SHIFT;
		}

		@Override
		public String orPress() {
			return OR_PRESS;
		}

		@Override
		public String press() {
			return PRESS;
		}

		@Override
		public String active() {
			return ACTIVE;
		}

		@Override
		public String inactive() {
			return INACTIVE;
		}

		@Override
		public String not() {
			return NOT;
		}

		@Override
		public String units() {
			return UNITS;
		}

	}

	private static class NBTTagNames extends WTNBTTagNames {

		private static final String INFINITY_ENERGY_NBT = "InfinityEnergy";
		private static final String BOOSTER_SLOT_NBT = "BoosterSlot";
		private static final String IN_RANGE_NBT = "IsInRange";
		private static final String AUTOCONSUME_BOOSTER_NBT = "AutoConsumeBoosters";
		private static final String WT_ENCRYPTION_KEY = "encryptionKey";
		private static final String WT_INTERNAL_POWER = "internalCurrentPower";

		@Override
		public String infinityEnergy() {
			return INFINITY_ENERGY_NBT;
		}

		@Override
		public String boosterSlot() {
			return BOOSTER_SLOT_NBT;
		}

		@Override
		public String inRange() {
			return IN_RANGE_NBT;
		}

		@Override
		public String autoConsumeBooster() {
			return AUTOCONSUME_BOOSTER_NBT;
		}

		@Override
		public String encryptionKey() {
			return WT_ENCRYPTION_KEY;
		}

		@Override
		public String internalCurrentPower() {
			return WT_INTERNAL_POWER;
		}

	}

	@Override
	public String getModId() {
		return MODID;
	}

	@Override
	public String getModName() {
		return NAME;
	}

	@Override
	public String getModVersion() {
		return VERSION;
	}

	@Override
	public String getItemGroup() {
		return ITEMGROUP_UNLOCALIZED_NAME;
	}

	@Override
	public WTTooltips getTooltips() {
		return TOOLTIPS;
	}

	@Override
	public WTNBTTagNames getNBTTagNames() {
		return TAG_NAMES;
	}

	@Override
	public String boosterCardUnlocalizedName() {
		return INFINITY_BOOSTER_CARD_UNLOCALIZED_NAME;
	}

	@Override
	public ResourceLocation getStatesTexture() {
		return STATES_TEXTURE;
	}

}