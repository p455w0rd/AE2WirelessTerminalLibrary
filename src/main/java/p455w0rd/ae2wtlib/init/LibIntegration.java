package p455w0rd.ae2wtlib.init;

import net.minecraftforge.fml.common.Loader;

/**
 * @author p455w0rd
 *
 */
public class LibIntegration {

	public static enum Mods {

			JEI("jei", "Just Enough Items"),
			BAUBLES("baubles", "Baubles"),
			BAUBLESAPI("Baubles|API", "Baubles API");

		private String modid, name;

		Mods(String modidIn, String nameIn) {
			modid = modidIn;
			name = nameIn;
		}

		public String getId() {
			return modid;
		}

		public String getName() {
			return name;
		}

		public boolean isLoaded() {
			return Loader.isModLoaded(getId());
		}
	}

}
