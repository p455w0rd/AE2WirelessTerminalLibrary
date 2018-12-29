package p455w0rd.ae2wtlib.container.slot;

import javax.annotation.Nonnull;

import appeng.container.slot.AppEngSlot;
import baubles.api.IBauble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import p455w0rd.ae2wtlib.api.ICustomWirelessTerminalItem;
import p455w0rd.ae2wtlib.api.base.ContainerWT;

/**
 * @author p455w0rd
 *
 */
public class SlotAEBauble extends AppEngSlot {

	public SlotAEBauble(IItemHandler inv, int idx, int x, int y) {
		super(inv, idx, x, y);
	}

	@Override
	public boolean canTakeStack(final EntityPlayer player) {
		return (!(!(player.openContainer instanceof ContainerWT)) && (!(getStack().getItem() instanceof ICustomWirelessTerminalItem)));
	}

	@Override
	public boolean isItemValid(@Nonnull final ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() instanceof IBauble) {
			IBauble bauble = (IBauble) stack.getItem();
			return bauble.getBaubleType(stack).hasSlot(getSlotIndex());
		}
		return false;
	}

}
