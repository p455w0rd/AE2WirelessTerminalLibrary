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
import p455w0rd.ae2wtlib.items.ItemWT;

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
	public void displayAllRelevantItems(NonNullList<ItemStack> stackList) {
		for (Item item : Item.REGISTRY) {
			item.getSubItems(this, stackList);
		}
		for (ICustomWirelessTerminalItem wirelessTerminal : WTApi.instance().getRegistry().getRegisteredTerminals()) {
			ItemStack tmpStack = new ItemStack((ItemWT) wirelessTerminal);
			if (wirelessTerminal instanceof ItemWT) {
				stackList.add(tmpStack);
				if (!wirelessTerminal.isCreative()) {
					ItemStack tmpStack2 = tmpStack.copy();
					((AEBasePoweredItem) tmpStack2.getItem()).injectAEPower(tmpStack2, LibConfig.WT_MAX_POWER, Actionable.MODULATE);
					stackList.add(tmpStack2);
				}
			}
		}
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