package p455w0rd.ae2wtlib.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.ae2wtlib.api.IModelHolder;
import p455w0rd.ae2wtlib.init.LibCreativeTab;

/**
 * @author p455w0rd
 *
 */
public class ItemBase extends Item implements IModelHolder {

	private String name = "";

	public ItemBase(String name) {
		this.name = name;
		setRegistryName(this.name);
		setUnlocalizedName(this.name);
		setMaxStackSize(64);
		setCreativeTab(LibCreativeTab.CREATIVE_TAB);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (isInCreativeTab(tab)) {
			subItems.add(new ItemStack(this));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, getModelResource());
	}

	@Override
	public ModelResourceLocation getModelResource() {
		return new ModelResourceLocation(getRegistryName(), "inventory");
	}
}
