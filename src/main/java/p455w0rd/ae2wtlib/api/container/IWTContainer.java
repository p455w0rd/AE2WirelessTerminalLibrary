package p455w0rd.ae2wtlib.api.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author p455w0rd
 *
 */
public interface IWTContainer {

	ItemStack getWirelessTerminal();

	EntityPlayer getPlayer();

	int getWTSlot();

	boolean isWTBauble();

}
