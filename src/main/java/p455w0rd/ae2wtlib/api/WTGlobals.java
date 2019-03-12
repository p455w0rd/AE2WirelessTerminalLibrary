package p455w0rd.ae2wtlib.api;

/**
 * @author p455w0rd
 *
 */
public abstract class WTGlobals {

	public abstract String getModId();

	public abstract String getModName();

	public abstract String getModVersion();

	public abstract String getItemGroup();

	public abstract String boosterCard();

	public abstract WTTooltips getTooltips();

	public static abstract class WTTooltips {

		public abstract String infiniteRange();

		public abstract String infinityEnergy();

		public abstract String installed();

		public abstract String inWapRange();

		public abstract String outOf();

		public abstract String infinityEnergyLow();

		public abstract String creative();

		public abstract String infinite();

		public abstract String pressShift();

		public abstract String orPress();

		public abstract String press();

		public abstract String active();

		public abstract String inactive();

		public abstract String not();

		public abstract String units();

		public abstract String jeiCanBeWorn();

	}

}
