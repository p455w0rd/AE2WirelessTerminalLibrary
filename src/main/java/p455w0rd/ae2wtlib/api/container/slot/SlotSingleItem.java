package p455w0rd.ae2wtlib.api.container.slot;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author p455w0rd
 *
 */
public class SlotSingleItem extends Slot {

	private final Slot delegate;

	public SlotSingleItem(Slot delegate) {
		super(delegate.inventory, delegate.getSlotIndex(), delegate.xPos, delegate.yPos);
		this.delegate = delegate;
	}

	@Nullable
	@Override
	public ItemStack getStack() {
		ItemStack orgStack = delegate.getStack();
		if (!orgStack.isEmpty()) {
			ItemStack modifiedStack = orgStack.copy();
			modifiedStack.setCount(1);
			return modifiedStack;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean getHasStack() {
		return delegate.getHasStack();
	}

	@Override
	public boolean isHere(IInventory inv, int slotIn) {
		return delegate.isHere(inv, slotIn);
	}

	@Override
	public int getSlotStackLimit() {
		return delegate.getSlotStackLimit();
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return delegate.getItemStackLimit(stack);
	}

	@Override
	@Nullable
	@SideOnly(Side.CLIENT)
	public String getSlotTexture() {
		return delegate.getSlotTexture();
	}

	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return delegate.canTakeStack(playerIn);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getBackgroundLocation() {
		return delegate.getBackgroundLocation();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getBackgroundSprite() {
		return delegate.getBackgroundSprite();
	}

	@Override
	public int getSlotIndex() {
		return delegate.getSlotIndex();
	}

	@Override
	public boolean isSameInventory(Slot other) {
		return delegate.isSameInventory(other);
	}
}