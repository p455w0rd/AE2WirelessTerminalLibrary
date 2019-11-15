package p455w0rd.ae2wtlib.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.items.ItemWUT;

/**
 * @author p455w0rd
 *
 */
public class LibCreativeTab {

	public static CreativeTabs CREATIVE_TAB = new CreativeTabs(WTApi.MODID) {

		@Override
		public ItemStack getIconItemStack() {
			return ItemWUT.getFullyStockedWut(true);
		}

		@Override
		public ItemStack getTabIconItem() {
			return getIconItemStack();
		}
	};

}