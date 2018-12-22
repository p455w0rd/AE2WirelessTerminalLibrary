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
import p455w0rd.ae2wtlib.api.networking.WTPacket;
import p455w0rd.ae2wtlib.container.ContainerWT;
import p455w0rd.ae2wtlib.sync.network.INetworkInfo;

public class PacketSwapSlots extends WTPacket {

	private final int slotA;
	private final int slotB;

	// automatic.
	public PacketSwapSlots(final ByteBuf stream) {
		slotA = stream.readInt();
		slotB = stream.readInt();
	}

	// api
	public PacketSwapSlots(final int slotA, final int slotB) {
		final ByteBuf data = Unpooled.buffer();

		data.writeInt(getPacketID());
		data.writeInt(this.slotA = slotA);
		data.writeInt(this.slotB = slotB);

		configureWrite(data);
	}

	@Override
	public void serverPacketData(final INetworkInfo manager, final WTPacket packet, final EntityPlayer player) {
		if (player != null) {
			if (player.openContainer instanceof ContainerWT) {
				((ContainerWT) player.openContainer).swapSlotContents(slotA, slotB);
			}
		}
	}
}
