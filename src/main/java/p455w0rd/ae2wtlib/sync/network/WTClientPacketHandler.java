package p455w0rd.ae2wtlib.sync.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketThreadUtil;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import p455w0rd.ae2wtlib.api.networking.*;

public class WTClientPacketHandler extends WTPacketHandlerBase implements IPacketHandler {

	private static final WTClientPacketHandler INSTANCE = new WTClientPacketHandler();

	public static final WTClientPacketHandler instance() {
		return INSTANCE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onPacketData(final INetworkInfo manager, final INetHandler handler, final FMLProxyPacket packet, final EntityPlayer player) {
		final ByteBuf stream = packet.payload();

		try {
			final int packetType = stream.readInt();
			final WTPacket pack = PacketTypes.getPacket(packetType).parsePacket(stream);

			final PacketCallState callState = new PacketCallState() {

				@Override
				public void call(final WTPacket appEngPacket) {
					appEngPacket.clientPacketData(manager, appEngPacket, Minecraft.getMinecraft().player);
				}
			};

			pack.setCallParam(callState);
			PacketThreadUtil.checkThreadAndEnqueue(pack, handler, Minecraft.getMinecraft());
			callState.call(pack);
		}
		catch (final Exception e) {
		}
	}
}