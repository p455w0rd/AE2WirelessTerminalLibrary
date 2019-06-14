package p455w0rd.ae2wtlib.api.item;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.Sets;

import appeng.api.config.Actionable;
import appeng.api.config.PowerUnits;
import appeng.api.util.IConfigManager;
import appeng.core.AEConfig;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.util.ConfigManager;
import appeng.util.Platform;
import baubles.api.BaubleType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.ae2wtlib.api.*;
import p455w0rd.ae2wtlib.api.client.IBaubleRender;
import p455w0rd.ae2wtlib.api.client.render.WTItemRenderer;
import p455w0rd.ae2wtlib.client.render.RenderLayerWT;
import p455w0rd.ae2wtlib.init.LibConfig;
import p455w0rd.ae2wtlib.integration.PwLib;
import p455w0rdslib.api.client.ICustomItemRenderer;
import p455w0rdslib.api.client.ItemLayerWrapper;

/**
 * @author p455w0rd
 *
 */
@Optional.InterfaceList(value = {
		@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles|API", striprefs = true)
})
public abstract class ItemWT extends AEBasePoweredItem implements ICustomWirelessTerminalItem {

	private EntityPlayer holder;

	@SideOnly(Side.CLIENT)
	ItemLayerWrapper wrappedModel;

	protected ItemWT(final ResourceLocation registryName) {
		super(LibConfig.WT_MAX_POWER);
		setRegistryName(registryName);
		setUnlocalizedName(registryName.toString());
		setMaxStackSize(1);
	}

	@Override
	public EntityPlayer getPlayer() {
		return holder;
	}

	@Override
	public void setPlayer(final EntityPlayer player) {
		holder = player;
	}

	@Override
	public int getColor() {
		return 0xFF8F15D4;
	}

	protected int getSlotFor(final InventoryPlayer inv, final ItemStack stack) {
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			if (!inv.getStackInSlot(i).isEmpty() && stackEqualExact(stack, inv.getStackInSlot(i))) {
				return i;
			}
		}
		return -1;
	}

	private boolean stackEqualExact(final ItemStack stack1, final ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	static final Set<String> validEnchantNames = Sets.newHashSet("soulbound", "soul_bound");

	@Override
	public boolean isBookEnchantable(final ItemStack stack, final ItemStack book) {
		final Map<Enchantment, Integer> bookEnchants = EnchantmentHelper.getEnchantments(book);
		final Map<Enchantment, Integer> itemEnchants = EnchantmentHelper.getEnchantments(stack);
		final boolean isBookValid = bookEnchants.size() == 1 && bookEnchants.keySet().stream().map(Enchantment::getRegistryName).map(ResourceLocation::getResourcePath).allMatch(validEnchantNames::contains);
		final boolean isItemValid = itemEnchants.size() == 0 || !itemEnchants.keySet().stream().map(Enchantment::getRegistryName).map(ResourceLocation::getResourcePath).allMatch(validEnchantNames::contains);
		return isBookValid && isItemValid;
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public boolean showDurabilityBar(final ItemStack is) {
		final double aeCurrPower = getAECurrentPower(is);
		final double aeMaxPower = getAEMaxPower(is);
		final double tenPct = aeMaxPower * 0.1d;
		if (aeCurrPower != 0 && (int) aeCurrPower >= (int) aeMaxPower - tenPct) {
			return false;
		}
		if (WTApi.instance().isWTCreative(is)) {
			return false;
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	public static boolean isShiftKeyDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, getModelResource(this));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemLayerWrapper getWrappedModel() {
		return wrappedModel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setWrappedModel(final ItemLayerWrapper wrappedModel) {
		this.wrappedModel = wrappedModel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldUseInternalTEISR() {
		return true;
	}

	@Override
	public boolean usePower(final EntityPlayer player, final double amount, final ItemStack is) {
		return extractAEPower(is, amount, Actionable.MODULATE) >= amount - 0.5;
	}

	@Override
	public boolean hasPower(final EntityPlayer player, final double amt, final ItemStack is) {
		return getAECurrentPower(is) >= amt;
	}

	@Override
	public IConfigManager getConfigManager(final ItemStack target) {
		return new ConfigManager((manager, settingName, newValue) -> {
			final NBTTagCompound data = Platform.openNbtData(target);
			manager.writeToNBT(data);
		});
	}

	@Override
	public String getEncryptionKey(final ItemStack item) {
		final NBTTagCompound tag = Platform.openNbtData(item);
		return tag.getString(WTApi.instance().getConstants().getNBTTagNames().encryptionKey());
	}

	@Override
	public void setEncryptionKey(final ItemStack item, final String encKey, final String name) {
		final NBTTagCompound tag = Platform.openNbtData(item);
		tag.setString(WTApi.instance().getConstants().getNBTTagNames().encryptionKey(), encKey);
		tag.setString("name", name);
	}

	@Override
	public boolean canHandle(final ItemStack is) {
		return is.getItem() == this;
	}

	private double injectPower(final PowerUnits inputUnit, final ItemStack is, final double amount, final boolean simulate) {
		if (simulate) {
			final int requiredExt = (int) PowerUnits.AE.convertTo(inputUnit, getAEMaxPower(is) - getAECurrentPower(is));
			if (amount < requiredExt) {
				return 0;
			}
			return amount - requiredExt;
		}
		else {
			final double powerRemainder = injectAEPower(is, inputUnit.convertTo(PowerUnits.AE, amount), simulate ? Actionable.SIMULATE : Actionable.MODULATE);
			return PowerUnits.AE.convertTo(inputUnit, powerRemainder);
		}
	}

	@Override
	public double injectAEPower(final ItemStack is, final double amount, final Actionable mode) {
		final double maxStorage = getAEMaxPower(is);
		final double currentStorage = getAECurrentPower(is);
		final double required = maxStorage - currentStorage;
		final double overflow = Math.min(amount * 2 - required, amount - required);
		if (mode == Actionable.MODULATE) {
			final NBTTagCompound data = Platform.openNbtData(is);
			final double toAdd = Math.min(amount * 2, required);
			data.setDouble(WTApi.instance().getConstants().getNBTTagNames().internalCurrentPower(), currentStorage + toAdd);
		}
		return Math.max(0, overflow);
	}

	@Optional.Method(modid = "redstoneflux")
	@Override
	public int receiveEnergy(final ItemStack is, final int maxReceive, final boolean simulate) {
		return maxReceive - (int) injectPower(PowerUnits.RF, is, maxReceive, simulate);
	}

	@Optional.Method(modid = "redstoneflux")
	@Override
	public int extractEnergy(final ItemStack container, final int maxExtract, final boolean simulate) {
		return 0;
	}

	@Optional.Method(modid = "redstoneflux")
	@Override
	public int getEnergyStored(final ItemStack is) {
		return (int) PowerUnits.AE.convertTo(PowerUnits.RF, getAECurrentPower(is));
	}

	@Optional.Method(modid = "redstoneflux")
	@Override
	public int getMaxEnergyStored(final ItemStack is) {
		return (int) PowerUnits.AE.convertTo(PowerUnits.RF, getAEMaxPower(is));
	}

	@Override
	public boolean shouldCauseReequipAnimation(final ItemStack oldStack, final ItemStack newStack, final boolean slotChanged) {
		return slotChanged;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(final ItemStack is) {
		if (WTApi.instance().isWTCreative(is)) {
			return true;
		}
		if (LibConfig.USE_OLD_INFINTY_MECHANIC) {
			return hasInfiniteRange(is);
		}
		return getAECurrentPower(is) > AEConfig.instance().getWirelessTerminalDrainMultiplier() && getEncryptionKey(is) != null && !getEncryptionKey(is).isEmpty() && (WTApi.instance().hasInfiniteRange(is) || WTApi.instance().isInRange(is));
	}

	@Override
	public void onUpdate(final ItemStack wirelessTerminal, final World w, final Entity e, final int slot, final boolean isSelected) {
		if (!(e instanceof EntityPlayer)) {
			return;
		}
		final EntityPlayer p = (EntityPlayer) e;
		if (getPlayer() == null || !getPlayer().getUniqueID().equals(p.getUniqueID())) {
			setPlayer(p);
		}
		if (wirelessTerminal == null || !(wirelessTerminal.getItem() instanceof ICustomWirelessTerminalItem)) {
			return;
		}
		if (p instanceof EntityPlayerMP) {
			rangeCheck(wirelessTerminal, (EntityPlayerMP) p, slot, false);
		}
		WTApi.instance().isBoosterInstalled(wirelessTerminal);
	}

	private void rangeCheck(final ItemStack wirelessTerm, final EntityPlayerMP player, final int wtSlot, final boolean isBauble) {
		final boolean inRange = WTApi.instance().isInRangeOfWAP(wirelessTerm, player);
		final boolean currentRangeValue = WTApi.instance().isInRange(wirelessTerm);
		if (inRange != currentRangeValue) {
			WTApi.instance().setInRange(wirelessTerm, inRange);
			//LibNetworking.instance().sendTo(new PacketSetInRange(inRange), player);
			final WTNetworkHandler n = WTApi.instance().getNetHandler();
			n.sendTo(n.createSetInRangePacket(inRange, isBauble, wtSlot), player);
		}
	}

	@Override
	public BaubleType getBaubleType(final ItemStack itemstack) {
		return BaubleType.TRINKET;
	}

	@Override
	public IBaubleRender getRender() {
		return RenderLayerWT.getInstance();
	}

	@Override
	public boolean willAutoSync(final ItemStack itemstack, final EntityLivingBase player) {
		return false;
	}

	@Override
	public void onWornTick(final ItemStack wirelessTerminal, final EntityLivingBase playerIn) {
		if (playerIn instanceof EntityPlayerMP) {
			final EntityPlayerMP player = (EntityPlayerMP) playerIn;
			final Pair<Boolean, Integer> p = getSlotInfo(wirelessTerminal, player);
			if (p.getRight() >= 0) {
				rangeCheck(wirelessTerminal, player, p.getRight(), p.getLeft());
			}
		}
	}

	private Pair<Boolean, Integer> getSlotInfo(final ItemStack wirelessTerminal, final EntityPlayer player) {
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			if (player.inventory.getStackInSlot(i) == wirelessTerminal) {
				return Pair.of(false, i);
			}
		}
		final WTBaublesAccess b = WTApi.instance().getBaublesUtility();
		for (int i = 0; i < 7; i++) {
			final ItemStack s = b.getWTBySlot(player, i, ICustomWirelessTerminalItem.class);
			if (!s.isEmpty() && s == wirelessTerminal) {
				return Pair.of(true, i);
			}
		}
		return Pair.of(false, -1);
	}

	@Override
	public ICustomItemRenderer getRenderer() {
		return WTItemRenderer.getRendererForItem(this);
	}

	@Override
	public ICapabilityProvider initCapabilities(final ItemStack stack, final NBTTagCompound nbt) {
		return new ICapabilityProvider() {
			@Override
			public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
				return PwLib.checkCap(capability);
			}

			@Override
			public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
				if (hasCapability(capability, facing)) {
					if (PwLib.checkCap(capability)) {
						return PwLib.getStackCapability(stack);
					}
				}
				return null;
			}

		};
	}

}
