package p455w0rd.ae2wtlib.init;

import p455w0rd.ae2wtlib.api.WTGlobals;

public class LibGlobals extends WTGlobals {

	public static final String MODID = "ae2wtlib";
	public static final String VERSION = "1.0.7";
	public static final String NAME = "AE2 Wireless Terminal Library (AE2WTLib)";
	public static final String SERVER_PROXY = "p455w0rd.ae2wtlib.proxy.CommonProxy";
	public static final String CLIENT_PROXY = "p455w0rd.ae2wtlib.proxy.ClientProxy";
	public static final String DEP_LIST = "required-after:appliedenergistics2@[rv6-stable-6,);required-after:p455w0rdslib@[2.0.36,);after:baubles";
	public static final String CONFIG_FILE = "config/AE2WirelessTerminals.cfg";

	private static final String ITEMGROUP = "itemGroup." + MODID;
	private static final String INFINITY_BOOSTER_CARD = "item." + MODID + ":infinity_booster_card.name";

	private static final WTTooltips TOOLTIPS = new Tooltips();

	public static class Tooltips extends WTTooltips {

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
		return ITEMGROUP;
	}

	@Override
	public WTTooltips getTooltips() {
		return TOOLTIPS;
	}

	@Override
	public String boosterCard() {
		return INFINITY_BOOSTER_CARD;
	}

}