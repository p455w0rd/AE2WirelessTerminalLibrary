package p455w0rd.ae2wtlib.init;

import appeng.api.config.Actionable;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.ae2wtlib.api.ICustomWirelessTerminalItem;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.api.item.ItemWT;

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
	@SideOnly(Side.CLIENT)
	public void displayAllRelevantItems(final NonNullList<ItemStack> stackList) {
		for (final Item item : Item.REGISTRY) {
			item.getSubItems(this, stackList);
		}
		for (final ICustomWirelessTerminalItem wirelessTerminal : WTApi.instance().getWirelessTerminalRegistry().getRegisteredTerminals()) {
			final ItemStack tmpStack = new ItemStack((ItemWT) wirelessTerminal);
			if (wirelessTerminal instanceof ItemWT) {
				stackList.add(tmpStack);
				if (!wirelessTerminal.isCreative()) {
					final ItemStack tmpStack2 = tmpStack.copy();
					((AEBasePoweredItem) tmpStack2.getItem()).injectAEPower(tmpStack2, LibConfig.WT_MAX_POWER, Actionable.MODULATE);
					stackList.add(tmpStack2);
				}
			}
		}
	}

	@Override
	public ItemStack getIconItemStack() {
		final ItemStack is = new ItemStack(LibItems.ULTIMATE_TERMINAL);
		((AEBasePoweredItem) is.getItem()).injectAEPower(is, WTApi.instance().getConfig().getWTMaxPower(), Actionable.MODULATE);
		return is;
	}

	@Override
	public ItemStack getTabIconItem() {
		return getIconItemStack();
	}

}