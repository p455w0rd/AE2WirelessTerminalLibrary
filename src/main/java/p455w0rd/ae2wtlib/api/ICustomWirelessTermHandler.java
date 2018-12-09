package p455w0rd.ae2wtlib.api;

import appeng.api.features.IWirelessTermHandler;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.storage.IStorageChannel;
import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyContainerItem", modid = "redstoneflux")
public interface ICustomWirelessTermHandler extends IWirelessTermHandler, IAEItemPowerStorage, IEnergyContainerItem {

	IStorageChannel<?> getStorageChannel();

}
