package p455w0rd.ae2wtlib.container.slot;

import javax.annotation.Nonnull;

import appeng.container.AEBaseContainer;
import appeng.container.slot.AppEngSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.api.base.ContainerWT;
import p455w0rd.ae2wtlib.init.*;
import p455w0rd.ae2wtlib.sync.packets.PacketSyncInfinityEnergy;

/**
 * @author p455w0rd
 *
 */
public class SlotBoosterEnergy extends AppEngSlot {

	private AEBaseContainer thisContainer;

	public SlotBoosterEnergy(int xPos, int yPos) {
		super(null, 0, xPos, yPos);
	}

	@Override
	public int getSlotStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValid(ItemStack is) {
		return !is.isEmpty() && (is.getItem() == LibItems.BOOSTER_CARD);
	}

	@Override
	public boolean canTakeStack(final EntityPlayer player) {
		return false;
	}

	@Override
	@Nonnull
	public ItemStack getStack() {
		return ItemStack.EMPTY;
	}

	@Override
	public void setContainer(final AEBaseContainer myContainer) {
		thisContainer = myContainer;
		super.setContainer(myContainer);
	}

	@Override
	public void putStack(final ItemStack stack) {
		if (thisContainer != null) {
			if (thisContainer instanceof ContainerWT) {
				ContainerWT c = (ContainerWT) thisContainer;
				WTApi.instance().addInfinityBoosters(c.getWirelessTerminal(), stack);
				c.detectAndSendChanges();
				for (IContainerListener listener : c.getListeners()) {
					if (listener instanceof EntityPlayerMP) {
						LibNetworking.instance().sendTo(new PacketSyncInfinityEnergy(WTApi.instance().getInfinityEnergy(((ContainerWT) thisContainer).getWirelessTerminal()), ((EntityPlayerMP) listener).getUniqueID(), false, -1), (EntityPlayerMP) listener);
					}
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getBackgroundLocation() {
		return new ResourceLocation(LibGlobals.MODID, "textures/gui/booster_slot.png");
	}

}
