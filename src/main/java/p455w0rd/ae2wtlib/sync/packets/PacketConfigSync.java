package p455w0rd.ae2wtlib.sync.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import p455w0rd.ae2wtlib.init.LibConfig;
import p455w0rd.ae2wtlib.sync.WTPacket;
import p455w0rd.ae2wtlib.sync.network.INetworkInfo;

public class PacketConfigSync extends WTPacket {

	int wirelessTermMaxPower;
	boolean boosterEnabled;

	public PacketConfigSync(final ByteBuf stream) {
		wirelessTermMaxPower = stream.readInt();
		boosterEnabled = stream.readBoolean();
	}

	// api
	public PacketConfigSync(int power, boolean booster) {
		wirelessTermMaxPower = power;
		boosterEnabled = booster;

		final ByteBuf data = Unpooled.buffer();
		data.writeInt(getPacketID());
		data.writeInt(wirelessTermMaxPower);
		data.writeBoolean(boosterEnabled);
		configureWrite(data);
	}

	@Override
	public void serverPacketData(final INetworkInfo manager, final WTPacket packet, final EntityPlayer player) {

	}

	@Override
	public void clientPacketData(final INetworkInfo network, final WTPacket packet, final EntityPlayer player) {
		LibConfig.WT_MAX_POWER = wirelessTermMaxPower;
		LibConfig.WT_BOOSTER_ENABLED = boosterEnabled;
	}

}
