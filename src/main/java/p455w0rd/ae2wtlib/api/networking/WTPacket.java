package p455w0rd.ae2wtlib.api.networking;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.*;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import p455w0rd.ae2wtlib.init.LibNetworking;

public abstract class WTPacket implements Packet<INetHandler> {

	private PacketBuffer p;
	private PacketCallState caller;

	public void serverPacketData(final INetworkInfo manager, final WTPacket packet, final EntityPlayer player) {
		throw new UnsupportedOperationException("This packet ( " + getPacketID() + " does not implement a server side handler.");
	}

	public final int getPacketID() {
		return WTPacketHandlerBase.PacketTypes.getID(this.getClass()).ordinal();
	}

	public void clientPacketData(final INetworkInfo network, final WTPacket packet, final EntityPlayer player) {
		throw new UnsupportedOperationException("This packet ( " + getPacketID() + " does not implement a client side handler.");
	}

	protected void configureWrite(final ByteBuf data) {
		data.capacity(data.readableBytes());
		p = new PacketBuffer(data);
	}

	public FMLProxyPacket getProxy() {
		if (p.array().length > 2 * 1024 * 1024) // 2k walking room :)
		{
			throw new IllegalArgumentException("Sorry AE2WTLib made a " + p.array().length + " byte packet by accident!");
		}

		final FMLProxyPacket pp = new FMLProxyPacket(p, LibNetworking.instance().getChannel());

		return pp;
	}

	@Override
	public void readPacketData(final PacketBuffer buf) throws IOException {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void writePacketData(final PacketBuffer buf) throws IOException {
		throw new RuntimeException("Not Implemented");
	}

	public ByteArrayInputStream getPacketByteArray(ByteBuf stream, int readerIndex, int readableBytes) {
		final ByteArrayInputStream bytes;
		if (stream.hasArray()) {
			bytes = new ByteArrayInputStream(stream.array(), readerIndex, readableBytes);
		}
		else {
			byte[] data = new byte[stream.capacity()];
			stream.getBytes(readerIndex, data, 0, readableBytes);
			bytes = new ByteArrayInputStream(data);
		}
		return bytes;
	}

	public ByteArrayInputStream getPacketByteArray(ByteBuf stream) {
		return this.getPacketByteArray(stream, 0, stream.readableBytes());
	}

	public void setCallParam(final PacketCallState call) {
		caller = call;
	}

	@Override
	public void processPacket(final INetHandler handler) {
		caller.call(this);
	}

}
