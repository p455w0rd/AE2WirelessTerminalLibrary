package p455w0rd.ae2wtlib.sync.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.api.container.ContainerWT;
import p455w0rd.ae2wtlib.api.networking.INetworkInfo;
import p455w0rd.ae2wtlib.api.networking.WTPacket;
import p455w0rd.ae2wtlib.init.LibConfig;

/**
 * @author p455w0rd
 *
 */
public class PacketSetAutoConsumeBoosters extends WTPacket {

	boolean mode;

	public PacketSetAutoConsumeBoosters(final ByteBuf stream) {
		mode = stream.readBoolean();
	}

	public PacketSetAutoConsumeBoosters(final boolean modeIn) {
		mode = modeIn;
		final ByteBuf data = Unpooled.buffer();
		data.writeInt(getPacketID());
		data.writeBoolean(modeIn);
		configureWrite(data);
	}

	@Override
	public void serverPacketData(final INetworkInfo manager, final WTPacket packet, final EntityPlayer player) {
		if (!LibConfig.USE_OLD_INFINTY_MECHANIC && player.openContainer instanceof ContainerWT) {
			final ItemStack wirelessTerminal = ((ContainerWT) player.openContainer).getWirelessTerminal();
			if (!wirelessTerminal.isEmpty()) {
				if (!wirelessTerminal.hasTagCompound()) {
					wirelessTerminal.setTagCompound(new NBTTagCompound());
				}
				wirelessTerminal.getTagCompound().setBoolean(WTApi.instance().getConstants().getNBTTagNames().autoConsumeBooster(), mode);
			}
		}
	}

}
