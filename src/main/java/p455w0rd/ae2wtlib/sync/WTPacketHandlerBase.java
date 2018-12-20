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
package p455w0rd.ae2wtlib.sync;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import p455w0rd.ae2wtlib.sync.packets.PacketBaubleSync;
import p455w0rd.ae2wtlib.sync.packets.PacketConfigSync;
import p455w0rd.ae2wtlib.sync.packets.PacketEmptyTrash;
import p455w0rd.ae2wtlib.sync.packets.PacketSetAutoConsumeBoosters;
import p455w0rd.ae2wtlib.sync.packets.PacketSetInRange;
import p455w0rd.ae2wtlib.sync.packets.PacketSwapSlots;
import p455w0rd.ae2wtlib.sync.packets.PacketSyncInfinityEnergy;

public class WTPacketHandlerBase {
	private static final Map<Class<? extends WTPacket>, PacketTypes> REVERSE_LOOKUP = new HashMap<Class<? extends WTPacket>, WTPacketHandlerBase.PacketTypes>();

	public enum PacketTypes {
			PACKET_SWAP_SLOTS(PacketSwapSlots.class),

			PACKET_EMPTY_TRASH(PacketEmptyTrash.class),

			PACKET_SYNC_CONFIGS(PacketConfigSync.class),

			PACKET_SYNC_INFINITY_ENERGY(PacketSyncInfinityEnergy.class),

			PACKET_SET_IN_RANGE(PacketSetInRange.class),

			PACKET_SET_AUTOCONSUME_BOOSTERS(PacketSetAutoConsumeBoosters.class),

			PACKET_BAUBLE_SYNC(PacketBaubleSync.class);

		private final Class<? extends WTPacket> packetClass;
		private final Constructor<? extends WTPacket> packetConstructor;

		PacketTypes(final Class<? extends WTPacket> c) {
			packetClass = c;

			Constructor<? extends WTPacket> x = null;
			try {
				x = packetClass.getConstructor(ByteBuf.class);
			}
			catch (final NoSuchMethodException ignored) {
			}
			catch (final SecurityException ignored) {
			}
			catch (final DecoderException ignored) {
			}

			packetConstructor = x;
			REVERSE_LOOKUP.put(packetClass, this);

			if (packetConstructor == null) {
				throw new IllegalStateException("Invalid Packet Class " + c + ", must be constructable on DataInputStream");
			}
		}

		public static PacketTypes getPacket(final int id) {
			return (values())[id];
		}

		static PacketTypes getID(final Class<? extends WTPacket> c) {
			return REVERSE_LOOKUP.get(c);
		}

		public WTPacket parsePacket(final ByteBuf in) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			return packetConstructor.newInstance(in);
		}
	}
}