package p455w0rd.ae2wtlib.sync.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import p455w0rd.ae2wtlib.api.ICustomWirelessTerminalItem;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.api.networking.INetworkInfo;
import p455w0rd.ae2wtlib.api.networking.WTPacket;
import p455w0rd.ae2wtlib.init.LibNetworking;
import p455w0rd.ae2wtlib.items.ItemWUT;

/**
 * @author p455w0rd
 *
 */
public class PacketSwitchWutTerminalGui extends WTPacket {

	private int index, wtSlot;
	private boolean isBauble;

	public PacketSwitchWutTerminalGui(final ByteBuf stream) {
		index = stream.readInt();
		wtSlot = stream.readInt();
		isBauble = stream.readBoolean();
	}

	public PacketSwitchWutTerminalGui(int index, int wtSlot, boolean isBauble) {
		final ByteBuf data = Unpooled.buffer();
		data.writeInt(getPacketID());
		data.writeInt(index);
		data.writeInt(wtSlot);
		data.writeBoolean(isBauble);
		configureWrite(data);
	}

	@Override
	public void serverPacketData(final INetworkInfo manager, final WTPacket packet, final EntityPlayer player) {
		ItemStack wt = isBauble ? WTApi.instance().getBaublesUtility().getWTBySlot(player, wtSlot, ICustomWirelessTerminalItem.class) : WTApi.instance().getWTBySlot(player, wtSlot);
		if (!wt.isEmpty() && WTApi.instance().getWUTUtility().isWUT(wt)) {
			ItemWUT.setSelectedTerminal(wt, index);
			LibNetworking.instance().sendTo(new PacketSwitchWutTerminalGui(index, wtSlot, isBauble), (EntityPlayerMP) player);
			//player.closeScreen();
		}
	}

	@Override
	public void clientPacketData(final INetworkInfo network, final WTPacket packet, final EntityPlayer player) {
		ItemStack wt = isBauble ? WTApi.instance().getBaublesUtility().getWTBySlot(player, wtSlot, ICustomWirelessTerminalItem.class) : WTApi.instance().getWTBySlot(player, wtSlot);

		if (!wt.isEmpty() && WTApi.instance().getWUTUtility().isWUT(wt)) {
			ItemWUT.setSelectedTerminal(wt, index);
			((ICustomWirelessTerminalItem) wt.getItem()).openGui(player, isBauble, wtSlot);
		}
	}

}
