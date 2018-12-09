/*
 * This file is part of Wireless Crafting Terminal. Copyright (c) 2017, p455w0rd
 * (aka TheRealp455w0rd), All rights reserved unless otherwise stated.
 *
 * Wireless Crafting Terminal is free software: you can redistribute it and/or
 * modify it under the terms of the MIT License.
 *
 * Wireless Crafting Terminal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the MIT License for
 * more details.
 *
 * You should have received a copy of the MIT License along with Wireless
 * Crafting Terminal. If not, see <https://opensource.org/licenses/MIT>.
 */
package p455w0rd.ae2wtlib.sync.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import p455w0rd.ae2wtlib.container.ContainerWT;
import p455w0rd.ae2wtlib.sync.WTPacket;
import p455w0rd.ae2wtlib.sync.network.INetworkInfo;

public class PacketEmptyTrash extends WTPacket {

	public PacketEmptyTrash(final ByteBuf stream) {
	}

	// api
	public PacketEmptyTrash() {
		final ByteBuf data = Unpooled.buffer();

		data.writeInt(getPacketID());

		configureWrite(data);
	}

	@Override
	public void serverPacketData(final INetworkInfo manager, final WTPacket packet, final EntityPlayer playerIn) {
		if (playerIn instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) playerIn;
			if (player.openContainer instanceof ContainerWT) {
				ContainerWT container = (ContainerWT) player.openContainer;
				if (container.getTrashSlot() != null) {
					container.getTrashSlot().clearStack();
					container.writeToNBT();
				}
			}
		}
	}

	@Override
	public void clientPacketData(final INetworkInfo network, final WTPacket packet, final EntityPlayer player) {

	}

}
