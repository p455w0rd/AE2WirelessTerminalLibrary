package p455w0rd.ae2wtlib.init;

import java.util.List;

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

	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllRelevantItems(final NonNullList<ItemStack> stackList) {
		for (final Item item : Item.REGISTRY) {
			item.getSubItems(this, stackList);
		}
		final List<? extends ICustomWirelessTerminalItem> list = WTApi.instance().getWirelessTerminalRegistry().getRegisteredTerminals();
		//if (list.size() > 1) {
		for (final ICustomWirelessTerminalItem wirelessTerminal : list) {
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
		//}
	}

	@Override
	public ItemStack getIconItemStack() {
		ItemStack is = ItemStack.EMPTY;
		is = new ItemStack(LibItems.ULTIMATE_TERMINAL);
		((AEBasePoweredItem) is.getItem()).injectAEPower(is, WTApi.instance().getConfig().getWTMaxPower(), Actionable.MODULATE);
		return is;
	}

	@Override
	public ItemStack getTabIconItem() {
		return getIconItemStack();
	}

}