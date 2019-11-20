package p455w0rd.ae2wtlib.api;

import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import appeng.api.implementations.tiles.IWirelessAccessPoint;
import appeng.bootstrap.FeatureFactory;
import appeng.container.slot.AppEngSlot;
import appeng.core.Api;
import appeng.core.ApiDefinitions;
import appeng.core.api.definitions.*;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import p455w0rd.ae2wtlib.api.client.IWTGuiScrollbar;
import p455w0rd.ae2wtlib.items.ItemInfinityBooster;

public abstract class WTApi {

	public static final String MODID = "ae2wtlib";
	public static final String VERSION = "1.0.33";
	public static final String AE2_DEP = "required-after:appliedenergistics2@[rv6-stable-7,);";
	public static final String AE2WTLIB_DEP = "required-after:" + MODID + "@[" + VERSION + ",);";
	public static final String BAUBLES_DEP = "after:baubles;";
	public static final String BASE_DEPS = AE2_DEP + BAUBLES_DEP + p455w0rdslib.LibGlobals.REQUIRE_DEP;
	public static final String BASE_DEPS_WITH_AE2WTLIB = AE2WTLIB_DEP + BASE_DEPS;

	protected static WTApi api = null;

	@Nullable
	public static WTApi instance() {
		if (WTApi.api == null) {
			try {
				final Class<?> clazz = Class.forName("p455w0rd.ae2wtlib.init.LibApiImpl");
				final Method instanceAccessor = clazz.getMethod("instance");
				WTApi.api = (WTApi) instanceAccessor.invoke(null);
			}
			catch (final Throwable e) {
				return null;
			}
		}

		return WTApi.api;
	}

	public abstract WTConfig getConfig();

	public abstract WTRegistry getWirelessTerminalRegistry();

	public abstract WTNetworkHandler getNetHandler();

	public abstract WTBaublesAccess getBaublesUtility();

	public abstract ItemInfinityBooster getBoosterCard();

	public abstract WTGlobals getConstants();

	public abstract WUTUtility getWUTUtility();

	public abstract Set<Pair<Integer, ItemStack>> getWirelessTerminals(EntityPlayer player);

	public abstract Set<Pair<Integer, ItemStack>> getWirelessTerminals(EntityPlayer player, boolean isBauble);

	// Parent pair contains a boolean which tells whether or not this is a bauble slot
	// Child pair gives the slot number and ItemStack
	public abstract Set<Pair<Boolean, Pair<Integer, ItemStack>>> getAllWirelessTerminals(EntityPlayer player);

	// get a specific type of wireless terminal
	public abstract Set<Pair<Boolean, Pair<Integer, ItemStack>>> getAllWirelessTerminalsByType(EntityPlayer player, Class<? extends ICustomWirelessTerminalItem> type);

	// used in cases where following method takes 'false' value for isBauble
	public abstract ItemStack getWTBySlot(EntityPlayer player, int slot, Class<? extends ICustomWirelessTerminalItem> type);

	public abstract ItemStack getWTBySlot(EntityPlayer player, boolean isBauble, int slot, Class<? extends ICustomWirelessTerminalItem> type);

	public abstract ItemStack getWTBySlot(EntityPlayer player, int slot);

	public abstract ItemStack getWTBySlot(final EntityPlayer player, boolean isBauble, final int slot);

	public abstract ItemStack getFirstWirelessTerminal(EntityPlayer player);

	public abstract boolean containsCreativeTerminal(ICustomWirelessTerminalItem... wirelessTerminals);

	public abstract boolean shouldConsumeBoosters(@Nonnull ItemStack wirelessTerminal);

	public abstract boolean isBoosterInstalled(@Nonnull final ItemStack wirelessTerminal);

	public abstract void setInRange(@Nonnull ItemStack wirelessTerm, boolean value);

	public abstract boolean isInRange(@Nonnull ItemStack wirelessTerm);

	public abstract ItemStack addInfinityBoosters(@Nonnull ItemStack wirelessTerm, @Nonnull ItemStack boosterCardStack);

	public abstract boolean hasInfiniteRange(@Nonnull ItemStack wirelessTerm);

	public abstract boolean hasInfinityEnergy(@Nonnull ItemStack wirelessTerm);

	// Is the ItemStack a Wireless Terminal registered through this library?
	public abstract boolean isAnyWT(@Nonnull ItemStack wirelessTerm);

	// Is the EntityPlayer holding the Wireless Terminal in range of a valid AE2 Wireless Access Point
	public abstract boolean isInRangeOfWAP(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player);

	// Get the distance to the nearest valid AE2 Wireless Access Point
	public abstract double getDistanceToWAP(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player);

	// Get the range (including booster cards) the the nearest AE2 Wireless Access Point covers
	public abstract double getWAPRange(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player);

	public abstract IWirelessAccessPoint getClosestWAPToPlayer(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player);

	// Get a list of WAPs on the linked network
	public abstract Set<IWirelessAccessPoint> getWAPs(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player);

	public abstract WTGuiObject<?> getGUIObject(EntityPlayer player);

	public abstract WTGuiObject<?> getGUIObject(@Nullable ItemStack wirelessTerm, @Nonnull EntityPlayer player);

	public abstract WTGuiObject<?> getGUIObject(ICustomWirelessTerminalItem wth, @Nonnull ItemStack wirelessTerm, EntityPlayer player);

	// Set Infinity Energy directly on a Wireless Terminal
	public abstract void setInfinityEnergy(@Nonnull ItemStack wirelessTerm, int amount);

	// Get a Wireless Terminals current Infinity Energy amount
	public abstract int getInfinityEnergy(@Nonnull ItemStack wirelessTerm);

	// Drains Infinity Energy from a Wireless Terminal (automatically syncs)
	public abstract void drainInfinityEnergy(@Nonnull ItemStack wirelessTerm, EntityPlayer player, boolean isBauble, int slot);

	// Is the Wireless Terminal a Creative version?
	public abstract boolean isWTCreative(ItemStack wirelessTerm);

	// Creates a new instance of the single item (old) Booster Card slot
	public abstract AppEngSlot createOldBoosterSlot(IItemHandler inventory, int xPos, int yPos);

	// Creates a new instance of the new Infinity Energy version of the Booster Card Slot
	public abstract AppEngSlot createInfinityBoosterSlot(int posX, int posY);

	// Creates a Null slot which is used in place of the Booster Card slot when the Booster Card is disabled
	public abstract AppEngSlot createNullSlot();

	// Creates a new instance of an armor-only slot
	public abstract AppEngSlot createArmorSlot(EntityPlayer player, IItemHandler inventory, int slot, int posX, int posY, EntityEquipmentSlot armorSlot);

	// Creates a new instance of a trash slot
	public abstract AppEngSlot createTrashSlot(IItemHandler inv, int posX, int posY);

	// Creates a new instance of the Booster Card inventory
	public abstract AppEngInternalInventory createBoosterInventory(IAEAppEngInventory inventory);

	// Ensures the stack has a NBT Tag Compound
	public abstract NBTTagCompound ensureTag(ItemStack stack);

	// Creates a new instance of a GUI scrollbar widget
	public abstract IWTGuiScrollbar createScrollbar();

	// Checks if terminal is linked via Security Terminal
	public abstract boolean isTerminalLinked(ItemStack wirelessTerminal);

	// Gets text color via its string name (might remove)
	@SideOnly(Side.CLIENT)
	public abstract String color(String color);

	public static class AE2Access {

		public static Api getApi() {
			return Api.INSTANCE;
		}

		public static ApiDefinitions getDefinitions() {
			return getApi().definitions();
		}

		public static FeatureFactory getRegistry() {
			return getDefinitions().getRegistry();
		}

		public static ApiBlocks getBlocks() {
			return getDefinitions().blocks();
		}

		public static ApiItems getItems() {
			return getDefinitions().items();
		}

		public static ApiMaterials getMaterials() {
			return getDefinitions().materials();
		}

		public static ApiParts getParts() {
			return getDefinitions().parts();
		}

	}

}