package p455w0rd.ae2wtlib.items;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.ae2wtlib.api.client.IModelHolder;
import p455w0rd.ae2wtlib.init.LibCreativeTab;

/**
 * @author p455w0rd
 *
 */
public class ItemBase extends Item implements IModelHolder {

	public ItemBase(ResourceLocation registryName) {
		setRegistryName(registryName);
		setUnlocalizedName(registryName.toString());
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

	@SideOnly(Side.CLIENT)
	public static boolean isShiftKeyDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}

}
