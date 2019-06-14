/*
 * This file is part of AE2WTLib. Copyright (c) 2017, p455w0rd
 * (aka TheRealp455w0rd), All rights reserved unless otherwise stated.
 *
 * AE2WTLib is free software: you can redistribute it and/or
 * modify it under the terms of the MIT License.
 *
 * AE2WTLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the MIT License for
 * more details.
 *
 * You should have received a copy of the MIT License along with Wireless
 * Crafting Terminal. If not, see <https://opensource.org/licenses/MIT>.
 */
package p455w0rd.ae2wtlib.api.networking;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public interface IPacketHandler {

	void onPacketData(INetworkInfo manager, INetHandler handler, FMLProxyPacket packet, EntityPlayer player);
}