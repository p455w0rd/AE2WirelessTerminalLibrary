package p455w0rd.ae2wtlib.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/**
 * @author p455w0rd
 *
 */
public class LibCreativeTab extends CreativeTabs {

	public static CreativeTabs CREATIVE_TAB = new LibCreativeTab();

	private LibCreativeTab() {
		super(LibGlobals.MODID);
	}

	public static void preInit() {
	}

	@Override
	public ItemStack getIconItemStack() {
		ItemStack is = new ItemStack(LibItems.BOOSTER_CARD);
		//((AEBasePoweredItem) is.getItem()).injectAEPower(is, ModConfig.WT_MAX_POWER, Actionable.MODULATE);
		return is;
	}

	@Override
	public ItemStack getTabIconItem() {
		return getIconItemStack();
	}

}