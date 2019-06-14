package p455w0rd.ae2wtlib.init;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import p455w0rd.ae2wtlib.api.WTNetworkHandler;
import p455w0rd.ae2wtlib.api.networking.IPacketHandler;
import p455w0rd.ae2wtlib.api.networking.WTPacket;
import p455w0rd.ae2wtlib.sync.network.WTClientPacketHandler;
import p455w0rd.ae2wtlib.sync.network.WTServerPacketHandler;
import p455w0rd.ae2wtlib.sync.packets.*;

public class LibNetworking extends WTNetworkHandler {

	private static final LibNetworking INSTANCE = new LibNetworking();
	private static final String CHANNEL_NAME = "ae2wtlib";
	private static final FMLEventChannel CHANNEL = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNEL_NAME);;

	private static final IPacketHandler clientHandler = WTClientPacketHandler.instance();
	private static final IPacketHandler serverHandler = WTServerPacketHandler.instance();

	private LibNetworking() {
	}

	public static void preInit() {
		MinecraftForge.EVENT_BUS.register(instance());
		getEventChannel().register(instance());
	}

	public static void postInit() {
		LibGuiHandler.register();
	}

	public static LibNetworking instance() {
		return INSTANCE;
	}

	public static FMLEventChannel getEventChannel() {
		return CHANNEL;
	}

	public IPacketHandler getClientHandler() {
		return clientHandler;
	}

	public IPacketHandler getServerHandler() {
		return serverHandler;
	}

	public String getChannel() {
		return CHANNEL_NAME;
	}

	@Override
	public void sendToAll(final WTPacket message) {
		getEventChannel().sendToAll(message.getProxy());
	}

	@Override
	public void sendTo(final WTPacket message, final EntityPlayerMP player) {
		getEventChannel().sendTo(message.getProxy(), player);
	}

	@Override
	public void sendToAllAround(final WTPacket message, final NetworkRegistry.TargetPoint point) {
		getEventChannel().sendToAllAround(message.getProxy(), point);
	}

	@Override
	public void sendToDimension(final WTPacket message, final int dimensionId) {
		getEventChannel().sendToDimension(message.getProxy(), dimensionId);
	}

	@Override
	public void sendToServer(final WTPacket message) {
		getEventChannel().sendToServer(message.getProxy());
	}

	@SubscribeEvent
	public void serverPacket(final ServerCustomPacketEvent ev) {
		final NetHandlerPlayServer srv = (NetHandlerPlayServer) ev.getPacket().handler();
		WTServerPacketHandler.instance().onPacketData(null, ev.getHandler(), ev.getPacket(), srv.player);
	}

	@SubscribeEvent
	public void clientPacket(final ClientCustomPacketEvent ev) {
		WTClientPacketHandler.instance().onPacketData(null, ev.getHandler(), ev.getPacket(), null);
	}

	@Override
	public WTPacket createAutoConsumeBoosterPacket(final boolean value) {
		return new PacketSetAutoConsumeBoosters(value);
	}

	@Override
	public WTPacket createEmptyTrashPacket() {
		return new PacketEmptyTrash();
	}

	@Override
	public WTPacket createInfinityEnergySyncPacket(final int energy, final UUID playerID, final boolean isBauble, final int slot) {
		return new PacketSyncInfinityEnergy(energy, playerID, isBauble, slot);
	}

	@Override
	public WTPacket createSetInRangePacket(final boolean isInRange, final boolean isBauble, final int wtSlot) {
		return new PacketSetInRange(isInRange, isBauble, wtSlot);
	}

}
