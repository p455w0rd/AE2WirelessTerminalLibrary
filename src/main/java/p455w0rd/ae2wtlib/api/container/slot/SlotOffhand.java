package p455w0rd.ae2wtlib.api.container.slot;

import appeng.container.slot.AppEngSlot;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.wrapper.InvWrapper;

/**
 * @author p455w0rd
 *
 */
public class SlotOffhand extends AppEngSlot {

	public SlotOffhand(final InventoryPlayer inventory, final int x, final int y) {
		super(new InvWrapper(inventory), 40, x, y);
	}

	@Override
	public boolean isItemValid(final ItemStack stack) {
		return super.isItemValid(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getSlotTexture() {
		return "minecraft:items/empty_armor_slot_shield";
	}

}
