package p455w0rd.ae2wtlib.container.slot;

import appeng.container.slot.AppEngSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.IItemHandler;
import p455w0rd.ae2wtlib.init.LibIntegration.Mods;
import p455w0rd.ae2wtlib.integration.Baubles;
import p455w0rd.ae2wtlib.util.WTUtils;

public class SlotPlayerHotBar extends AppEngSlot {

	public SlotPlayerHotBar(final IItemHandler inv, final int par2, final int par3, final int par4) {
		super(inv, par2, par3, par4);
	}

	@Override
	public boolean isPlayerSide() {
		return true;
	}

	@Override
	public boolean canTakeStack(final EntityPlayer player) {
		if (Mods.BAUBLES.isLoaded()) {
			if (!Baubles.getFirstWTBauble(player).getRight().isEmpty()) {
				return true;
			}
			if (!getStack().isEmpty() && WTUtils.isAnyWT(getStack())) {
				return super.canTakeStack(player);
			}
		}
		else {
			if (!getStack().isEmpty() && WTUtils.isAnyWT(getStack())) {
				return true;
			}
		}
		return true;
	}

}
