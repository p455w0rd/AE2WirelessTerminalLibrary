package p455w0rd.ae2wtlib.api;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import p455w0rd.ae2wtlib.api.networking.WTPacket;

/**
 * @author p455w0rd
 *
 */
public abstract class WTNetworkHandler {

	public abstract void sendToAll(final WTPacket message);

	public abstract void sendTo(final WTPacket message, final EntityPlayerMP player);

	public abstract void sendToAllAround(final WTPacket message, final NetworkRegistry.TargetPoint point);

	public abstract void sendToDimension(final WTPacket message, final int dimensionId);

	public abstract void sendToServer(final WTPacket message);

	public abstract WTPacket createAutoConsumeBoosterPacket(boolean value);

	public abstract WTPacket createEmptyTrashPacket();

	public abstract WTPacket createInfinityEnergySyncPacket(int energy, UUID playerID, boolean isBauble, int slot);

}
