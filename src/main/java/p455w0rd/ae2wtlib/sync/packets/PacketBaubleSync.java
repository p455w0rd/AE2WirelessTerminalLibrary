package p455w0rd.ae2wtlib.sync.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.api.networking.INetworkInfo;
import p455w0rd.ae2wtlib.api.networking.WTPacket;

/**
 * @author p455w0rd
 *
 */
public class PacketBaubleSync extends WTPacket {

	ItemStack wirelessTerm;
	int baubleSlot;

	public PacketBaubleSync(final ByteBuf stream) {
		baubleSlot = stream.readInt();
		wirelessTerm = ByteBufUtils.readItemStack(stream);
	}

	public PacketBaubleSync(ItemStack wirelessTerminal, int slot) {
		wirelessTerm = wirelessTerminal;
		baubleSlot = slot;
		final ByteBuf data = Unpooled.buffer();
		data.writeInt(getPacketID());
		data.writeInt(slot);
		ByteBufUtils.writeItemStack(data, wirelessTerm);
		configureWrite(data);
	}

	@Override
	public void serverPacketData(final INetworkInfo manager, final WTPacket packet, final EntityPlayer player) {

	}

	@Override
	public void clientPacketData(final INetworkInfo network, final WTPacket packet, final EntityPlayer player) {
		WTApi.instance().getBaublesUtility().updateWTBauble(player, wirelessTerm, baubleSlot);
	}

}
