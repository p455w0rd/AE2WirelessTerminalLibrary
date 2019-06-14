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
import net.minecraft.item.ItemStack;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.api.networking.INetworkInfo;
import p455w0rd.ae2wtlib.api.networking.WTPacket;

/**
 * @author p455w0rd
 *
 */
public class PacketSetInRange extends WTPacket {

	int wtSlot;
	boolean isBauble;
	boolean isInRange;

	public PacketSetInRange(final ByteBuf stream) {
		isInRange = stream.readBoolean();
	}

	// api
	public PacketSetInRange(final boolean isInRange, final boolean isBauble, final int wtSlot) {
		this.isInRange = isInRange;
		this.isBauble = isBauble;
		this.wtSlot = wtSlot;
		final ByteBuf data = Unpooled.buffer();
		data.writeInt(getPacketID());
		data.writeBoolean(isInRange);
		configureWrite(data);
	}

	@Override
	public void serverPacketData(final INetworkInfo manager, final WTPacket packet, final EntityPlayer player) {
	}

	@Override
	public void clientPacketData(final INetworkInfo network, final WTPacket packet, final EntityPlayer player) {
		final ItemStack wirelessTerminal = WTApi.instance().getWTBySlot(player, isBauble, wtSlot);
		if (!wirelessTerminal.isEmpty()) {
			WTApi.instance().setInRange(wirelessTerminal, isInRange);
		}
	}

}