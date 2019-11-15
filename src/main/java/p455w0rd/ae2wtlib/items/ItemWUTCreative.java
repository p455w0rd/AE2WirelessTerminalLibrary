package p455w0rd.ae2wtlib.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.init.LibItems;

/**
 * @author p455w0rd
 *
 */
public class ItemWUTCreative extends ItemWUT {

	public ItemWUTCreative() {
		super(new ResourceLocation(WTApi.MODID, "wut_creative"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, getModelResource(this));
	}

	@Override
	public ModelResourceLocation getModelResource(final Item item) {
		return new ModelResourceLocation(LibItems.ULTIMATE_TERMINAL.getRegistryName(), "inventory");
	}

	@Override
	public double getAECurrentPower(final ItemStack wirelessTerm) {
		return WTApi.instance().getConfig().getWTMaxPower();
	}

	@Override
	public EnumRarity getRarity(final ItemStack wirelessTerm) {
		return EnumRarity.RARE;
	}

	@Override
	public boolean isCreative() {
		return true;
	}

}
