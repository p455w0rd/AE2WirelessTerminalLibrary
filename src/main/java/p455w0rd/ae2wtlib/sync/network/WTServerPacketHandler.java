package p455w0rd.ae2wtlib.sync.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketThreadUtil;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import p455w0rd.ae2wtlib.api.networking.*;

public final class WTServerPacketHandler extends WTPacketHandlerBase implements IPacketHandler {

	private static final WTServerPacketHandler INSTANCE = new WTServerPacketHandler();

	public static final WTServerPacketHandler instance() {
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
					appEngPacket.serverPacketData(manager, appEngPacket, player);
				}
			};

			pack.setCallParam(callState);
			PacketThreadUtil.checkThreadAndEnqueue(pack, handler, ((EntityPlayerMP) player).getServer());
			callState.call(pack);
		}
		catch (final Exception e) {
		}
	}
}
