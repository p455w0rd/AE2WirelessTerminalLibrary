package p455w0rd.ae2wtlib.init;

import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import appeng.tile.misc.TileCharger;
import appeng.tile.networking.TileController;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.ae2wtlib.AE2WTLib;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.api.networking.WTPacket;
import p455w0rd.ae2wtlib.client.render.BaubleRenderDispatcher;
import p455w0rd.ae2wtlib.integration.PwLib;
import p455w0rd.ae2wtlib.sync.packets.PacketConfigSync;
import p455w0rd.ae2wtlib.sync.packets.PacketSyncInfinityEnergy;
import p455w0rdslib.LibGlobals.Mods;
import p455w0rdslib.capabilities.CapabilityChunkLoader;
import p455w0rdslib.capabilities.CapabilityChunkLoader.ProviderTE;

/**
 * @author p455w0rd
 *
 */
@EventBusSubscriber(modid = WTApi.MODID)
public class LibEvents {

	@SubscribeEvent
	public static void onItemRegistryReady(final RegistryEvent.Register<Item> event) {
		LibItems.register(event);
	}

	@SubscribeEvent
	public static void onRecipeRegistryReady(final RegistryEvent.Register<IRecipe> event) {
		LibRecipes.register(event);
	}

	@SubscribeEvent
	public static void attachCapabilities(final AttachCapabilitiesEvent<TileEntity> event) {
		if (event.getObject() instanceof TileController && LibConfig.WT_ENABLE_CONTROLLER_CHUNKLOADER) {
			final TileController controller = (TileController) event.getObject();
			event.addCapability(new ResourceLocation(WTApi.MODID, "chunkloader"), new ProviderTE(controller));
		}
		if (WTApi.instance() != null && WTApi.instance().getConfig() != null && WTApi.instance().getConfig().areShadersEnabled() && event.getObject() instanceof TileCharger) {
			final TileCharger charger = (TileCharger) event.getObject();
			event.addCapability(new ResourceLocation(WTApi.MODID, "pw_light"), PwLib.getChargerProvider(charger));
		}
	}

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void onPlace(final BlockEvent.PlaceEvent e) {
		final World world = e.getWorld();
		final BlockPos pos = e.getPos();
		if (world != null && pos != null && world.getTileEntity(pos) != null && !world.isRemote && LibConfig.WT_ENABLE_CONTROLLER_CHUNKLOADER) {
			if (world.getTileEntity(pos) instanceof TileController) {
				final TileEntity tile = world.getTileEntity(pos);
				if (tile.hasCapability(CapabilityChunkLoader.CAPABILITY_CHUNKLOADER_TE, null)) {
					tile.getCapability(CapabilityChunkLoader.CAPABILITY_CHUNKLOADER_TE, null).attachChunkLoader(AE2WTLib.INSTANCE);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBreak(final BlockEvent.BreakEvent e) {
		final World world = e.getWorld();
		final BlockPos pos = e.getPos();
		if (world != null && pos != null && world.getTileEntity(pos) != null && !world.isRemote && LibConfig.WT_ENABLE_CONTROLLER_CHUNKLOADER) {
			if (world.getTileEntity(pos) instanceof TileController) {
				final TileEntity tile = world.getTileEntity(pos);
				if (tile.hasCapability(CapabilityChunkLoader.CAPABILITY_CHUNKLOADER_TE, null)) {
					tile.getCapability(CapabilityChunkLoader.CAPABILITY_CHUNKLOADER_TE, null).detachChunkLoader(AE2WTLib.INSTANCE);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onMobDrop(final LivingDropsEvent event) {
		final ItemStack stack = new ItemStack(LibItems.BOOSTER_CARD);
		final EntityItem drop = new EntityItem(event.getEntityLiving().getEntityWorld(), event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, stack);
		if (event.getEntity() instanceof EntityDragon && LibConfig.WT_BOOSTER_ENABLED && LibConfig.WT_DRAGON_DROPS_BOOSTER) {
			event.getDrops().add(drop);
		}

		if (event.getEntity() instanceof EntityWither && LibConfig.WT_BOOSTER_ENABLED && LibConfig.WT_WITHER_DROPS_BOOSTER) {
			final Random rand = event.getEntityLiving().getEntityWorld().rand;
			final int n = rand.nextInt(100);
			if (n <= LibConfig.WT_BOOSTER_DROP_CHANCE) {
				event.getDrops().add(drop);
			}
		}

		if (event.getEntity() instanceof EntityEnderman && LibConfig.WT_BOOSTER_ENABLED && LibConfig.WT_ENDERMAN_DROP_BOOSTERS) {
			final Random rand = event.getEntityLiving().getEntityWorld().rand;
			final int n = rand.nextInt(100);
			if (n <= LibConfig.WT_ENDERMAN_BOOSTER_DROP_CHANCE) {
				event.getDrops().add(drop);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onPlayerRenderPre(final RenderPlayerEvent.Pre event) {
		if (Mods.BAUBLES.isLoaded() && !BaubleRenderDispatcher.getRegistry().contains(event.getRenderer())) {
			event.getRenderer().addLayer(new BaubleRenderDispatcher(event.getRenderer()));
			BaubleRenderDispatcher.getRegistry().add(event.getRenderer());
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onTextureStitch(final TextureStitchEvent.Pre event) {
		event.getMap().registerSprite(new ResourceLocation(WTApi.MODID, "gui/booster_slot"));
	}

	@SideOnly(Side.SERVER)
	@SubscribeEvent
	public void onPlayerLogin(final PlayerLoggedInEvent e) {
		if (e.player instanceof EntityPlayerMP) {
			final PacketConfigSync p = new PacketConfigSync(LibConfig.WT_MAX_POWER, LibConfig.WT_BOOSTER_ENABLED);
			LibNetworking.instance().sendTo((WTPacket) p, (EntityPlayerMP) e.player);
		}
	}

	@SubscribeEvent
	public static void tickEvent(final TickEvent.PlayerTickEvent e) {
		final EntityPlayer player = e.player;
		if (!(player instanceof EntityPlayerMP)) {
			return;
		}
		final NonNullList<ItemStack> playerInv = player.inventory.mainInventory;
		final Set<Pair<Boolean, Pair<Integer, ItemStack>>> terminals = WTApi.instance().getAllWirelessTerminals(player);
		ItemStack wirelessTerm = ItemStack.EMPTY;
		boolean isBauble = false;
		int wctSlot = -1;
		for (final Pair<Boolean, Pair<Integer, ItemStack>> termPair : terminals) {
			if (WTApi.instance().shouldConsumeBoosters(termPair.getRight().getRight())) {
				wirelessTerm = termPair.getRight().getRight();
				isBauble = termPair.getLeft();
				wctSlot = termPair.getRight().getLeft();
				break;
			}
		}
		final int invSize = playerInv.size();
		if (invSize <= 0) {
			return;
		}
		if (!LibConfig.USE_OLD_INFINTY_MECHANIC && !wirelessTerm.isEmpty() && WTApi.instance().shouldConsumeBoosters(wirelessTerm)) {
			for (int currentSlot = 0; currentSlot < invSize; currentSlot++) {
				final ItemStack slotStack = playerInv.get(currentSlot);
				if (!slotStack.isEmpty() && slotStack.getItem() == LibItems.BOOSTER_CARD) {
					playerInv.set(currentSlot, WTApi.instance().addInfinityBoosters(wirelessTerm, slotStack));
					LibNetworking.instance().sendToDimension(new PacketSyncInfinityEnergy(WTApi.instance().getInfinityEnergy(wirelessTerm), player.getUniqueID(), isBauble, wctSlot), player.getEntityWorld().provider.getDimension());
				}
			}
		}
	}

}
