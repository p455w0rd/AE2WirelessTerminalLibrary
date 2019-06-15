package p455w0rd.ae2wtlib.api;

import appeng.api.features.IWirelessTermHandler;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.storage.IStorageChannel;
import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;
import p455w0rd.ae2wtlib.api.client.IBaubleItem;
import p455w0rdslib.api.client.IModelHolder;

/**
 * @author p455w0rd
 *
 */
@Optional.InterfaceList({ //@formatter:off
		@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyContainerItem", modid = "redstoneflux", striprefs = true),
		@Optional.Interface(iface = "p455w0rd.ae2wtlib.api.client.IBaubleItem", modid = "baubles", striprefs = true)
})//@formatter:on
public interface ICustomWirelessTerminalItem extends IWirelessTermHandler, IAEItemPowerStorage, IEnergyContainerItem, IBaubleItem, IModelHolder {

	/**
	 * Use {@link ICustomWirelessTerminalItem#hasInfiniteRange(ItemStack) hasInfiniteRange(ItemStack)}
	 */
	@Deprecated
	default boolean checkForBooster(final ItemStack wirelessTerminal) {
		return hasInfiniteRange(wirelessTerminal);
	}

	/**
	 * Checks if an Infinity Booster Card is installed on the WT
	 */
	default boolean hasInfiniteRange(final ItemStack wirelessTerminal) {
		return WTApi.instance().getConfig().isOldInfinityMechanicEnabled() ? WTApi.instance().isBoosterInstalled(wirelessTerminal) : WTApi.instance().hasInfiniteRange(wirelessTerminal) && WTApi.instance().hasInfinityEnergy(wirelessTerminal);
	}

	/**
	 * Checks if the terminal is a creative version
	 */
	default boolean isCreative() {
		return false;
	}

	/**
	 * opens the gui
	 */
	void openGui(EntityPlayer player, boolean isBauble, int playerSlot);

	/**
	 * gets the entityplayer, if any, holding the item
	 */
	EntityPlayer getPlayer();

	/**
	 * sets the player holding the item
	 */
	void setPlayer(EntityPlayer player);

	/**
	 * A theme color, currently used for glint (glow) effect. Defaults to vanilla.
	 */
	int getColor();

	/**
	 * The AE2 Store Channel this Teminal Will be accessing.
	 * If no channel needed, default to<br><br>
	 * {@code public IStorageChannel&lt;IAEItemStack&gt; getStorageChannel(ItemStack stack){<br>
	 *     return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);<br>
	 * }
	 */
	IStorageChannel<?> getStorageChannel(ItemStack wirelessTerminal);

	/**
	 * 16x16 image representing your terminal (this is usually the item's texture)
	 */
	ResourceLocation getMenuIcon();

}
