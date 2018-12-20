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

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import p455w0rd.ae2wtlib.api.ICustomWirelessTerminalItem;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.client.gui.GuiWT;
import p455w0rd.ae2wtlib.container.ContainerWT;
import p455w0rd.ae2wtlib.integration.Baubles;
import p455w0rd.ae2wtlib.sync.WTPacket;
import p455w0rd.ae2wtlib.sync.network.INetworkInfo;

/**
 * @author p455w0rd
 *
 */
public class PacketSyncInfinityEnergy extends WTPacket {

	int infinityEnergy;
	int slot;
	boolean isBauble;
	UUID playerID;

	public PacketSyncInfinityEnergy(final ByteBuf stream) {
		infinityEnergy = stream.readInt();
		slot = stream.readInt();
		isBauble = stream.readBoolean();
		playerID = UUID.fromString(ByteBufUtils.readUTF8String(stream));
	}

	public PacketSyncInfinityEnergy(int energy, UUID playerID, boolean isBauble, int slot) {
		infinityEnergy = energy;
		this.slot = slot;
		this.isBauble = isBauble;
		final ByteBuf data = Unpooled.buffer();
		data.writeInt(getPacketID());
		data.writeInt(infinityEnergy);
		data.writeInt(slot);
		data.writeBoolean(isBauble);
		ByteBufUtils.writeUTF8String(data, playerID.toString());
		configureWrite(data);
	}

	@Override
	public void serverPacketData(final INetworkInfo manager, final WTPacket packet, final EntityPlayer player) {

	}

	@Override
	public void clientPacketData(final INetworkInfo network, final WTPacket packet, final EntityPlayer player) {
		if (slot >= 0 && player.getEntityWorld() != null) {
			EntityPlayer targetPlayer = player.getEntityWorld().getPlayerEntityByUUID(playerID);
			if (targetPlayer != null) {
				ItemStack wirelessTerm = isBauble ? Baubles.getWTBySlot(targetPlayer, slot, ICustomWirelessTerminalItem.class) : WTApi.instance().getWTBySlot(targetPlayer, slot);
				if (!wirelessTerm.isEmpty()) {
					WTApi.instance().setInfinityEnergy(wirelessTerm, infinityEnergy);
					if (playerID.equals(player.getUniqueID())) {
						if (Minecraft.getMinecraft().currentScreen instanceof GuiWT) {
							GuiWT gui = (GuiWT) Minecraft.getMinecraft().currentScreen;
							ContainerWT container = (ContainerWT) gui.inventorySlots;
							int containerSlot = container.getWTSlot();
							boolean containerIsBauble = container.isWTBauble();
							if (slot == containerSlot && isBauble == containerIsBauble) {
								container.setWirelessTerminal(wirelessTerm);
							}
						}
					}
				}
			}
		}
	}

}