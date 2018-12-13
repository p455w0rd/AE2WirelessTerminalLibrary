package p455w0rd.ae2wtlib.sync.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.container.ContainerWT;
import p455w0rd.ae2wtlib.sync.WTPacket;
import p455w0rd.ae2wtlib.sync.network.INetworkInfo;

/**
 * @author p455w0rd
 *
 */
public class PacketSyncInfinityEnergyContainer extends WTPacket {

	int infinityEnergy;

	public PacketSyncInfinityEnergyContainer(final ByteBuf stream) {
		infinityEnergy = stream.readInt();
	}

	public PacketSyncInfinityEnergyContainer(int energy, boolean isBauble, int slot) {
		infinityEnergy = energy;
		final ByteBuf data = Unpooled.buffer();
		data.writeInt(getPacketID());
		data.writeInt(infinityEnergy);
		data.writeInt(slot);
		data.writeBoolean(isBauble);
		configureWrite(data);
	}

	@Override
	public void serverPacketData(final INetworkInfo manager, final WTPacket packet, final EntityPlayer player) {

	}

	@Override
	public void clientPacketData(final INetworkInfo network, final WTPacket packet, final EntityPlayer player) {
		if (player.openContainer instanceof ContainerWT) {
			WTApi.instance().setInfinityEnergy(((ContainerWT) player.openContainer).getWirelessTerminal(), infinityEnergy);
		}
	}
}