package p455w0rd.ae2wtlib.sync.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import p455w0rd.ae2wtlib.container.ContainerWT;
import p455w0rd.ae2wtlib.init.LibConfig;
import p455w0rd.ae2wtlib.sync.WTPacket;
import p455w0rd.ae2wtlib.sync.network.INetworkInfo;
import p455w0rd.ae2wtlib.util.WTUtils;

/**
 * @author p455w0rd
 *
 */
public class PacketSetAutoConsumeBoosters extends WTPacket {

	boolean mode;

	public PacketSetAutoConsumeBoosters(final ByteBuf stream) {
		mode = stream.readBoolean();
	}

	public PacketSetAutoConsumeBoosters(boolean modeIn) {
		mode = modeIn;
		final ByteBuf data = Unpooled.buffer();
		data.writeInt(getPacketID());
		data.writeBoolean(modeIn);
		configureWrite(data);
	}

	@Override
	public void serverPacketData(final INetworkInfo manager, final WTPacket packet, final EntityPlayer player) {
		if (!LibConfig.USE_OLD_INFINTY_MECHANIC && player.openContainer instanceof ContainerWT) {
			ItemStack wirelessTerminal = ((ContainerWT) player.openContainer).getWirelessTerminal();
			if (!wirelessTerminal.isEmpty()) {
				if (!wirelessTerminal.hasTagCompound()) {
					wirelessTerminal.setTagCompound(new NBTTagCompound());
				}
				wirelessTerminal.getTagCompound().setBoolean(WTUtils.AUTOCONSUME_BOOSTER_NBT, mode);
			}
		}
	}

}
