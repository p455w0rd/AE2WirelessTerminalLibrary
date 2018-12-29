package p455w0rd.ae2wtlib.items;

import static p455w0rd.ae2wtlib.api.WTApi.Constants.NBT.WT_ENCRYPTION_KEY;
import static p455w0rd.ae2wtlib.api.WTApi.Constants.NBT.WT_INTERNAL_POWER;

import org.lwjgl.input.Keyboard;

import appeng.api.config.*;
import appeng.api.util.IConfigManager;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.util.ConfigManager;
import appeng.util.Platform;
import baubles.api.BaubleType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.ae2wtlib.api.*;
import p455w0rd.ae2wtlib.api.client.IBaubleRender;
import p455w0rd.ae2wtlib.client.render.ItemLayerWrapper;
import p455w0rd.ae2wtlib.client.render.RenderLayerWT;
import p455w0rd.ae2wtlib.init.LibConfig;
import p455w0rd.ae2wtlib.init.LibNetworking;
import p455w0rd.ae2wtlib.sync.packets.PacketSetInRange;

/**
 * @author p455w0rd
 *
 */
public abstract class ItemWT extends AEBasePoweredItem implements ICustomWirelessTerminalItem {

	private EntityPlayer player;

	@SideOnly(Side.CLIENT)
	ItemLayerWrapper wrappedModel;

	protected ItemWT(ResourceLocation registryName) {
		super(LibConfig.WT_MAX_POWER);
		setRegistryName(registryName);
		setUnlocalizedName(registryName.toString());
		setMaxStackSize(1);
	}

	protected EntityPlayer getPlayer() {
		return player;
	}

	protected void setPlayer(EntityPlayer player) {
		this.player = player;
	}

	protected int getSlotFor(InventoryPlayer inv, ItemStack stack) {
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			if (!inv.getStackInSlot(i).isEmpty() && stackEqualExact(stack, inv.getStackInSlot(i))) {
				return i;
			}
		}
		return -1;
	}

	private boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public boolean showDurabilityBar(ItemStack is) {
		double aeCurrPower = getAECurrentPower(is);
		double aeMaxPower = getAEMaxPower(is);
		if ((int) aeCurrPower >= (int) aeMaxPower - 2) {
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
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public ModelResourceLocation getModelResource() {
		return new ModelResourceLocation(getRegistryName(), "inventory");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemLayerWrapper getWrappedModel() {
		return wrappedModel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setWrappedModel(ItemLayerWrapper wrappedModel) {
		this.wrappedModel = wrappedModel;
	}

	@Override
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
		final ConfigManager out = new ConfigManager((manager, settingName, newValue) -> {
			final NBTTagCompound data = Platform.openNbtData(target);
			manager.writeToNBT(data);
		});
		out.registerSetting(Settings.SORT_BY, SortOrder.NAME);
		out.registerSetting(Settings.VIEW_MODE, ViewItems.ALL);
		out.registerSetting(Settings.SORT_DIRECTION, SortDir.ASCENDING);
		out.readFromNBT(Platform.openNbtData(target).copy());
		return out;
	}

	@Override
	public String getEncryptionKey(final ItemStack item) {
		final NBTTagCompound tag = Platform.openNbtData(item);
		return tag.getString(WT_ENCRYPTION_KEY);
	}

	@Override
	public void setEncryptionKey(final ItemStack item, final String encKey, final String name) {
		final NBTTagCompound tag = Platform.openNbtData(item);
		tag.setString(WT_ENCRYPTION_KEY, encKey);
		tag.setString("name", name);
	}

	@Override
	public boolean canHandle(ItemStack is) {
		return is.getItem() == this;
	}

	private double injectPower(PowerUnits inputUnit, final ItemStack is, final double amount, final boolean simulate) {
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
	public double injectAEPower(final ItemStack is, final double amount, Actionable mode) {
		final double maxStorage = getAEMaxPower(is);
		final double currentStorage = getAECurrentPower(is);
		final double required = maxStorage - currentStorage;
		final double overflow = Math.min(amount * 2 - required, amount - required);
		if (mode == Actionable.MODULATE) {
			final NBTTagCompound data = Platform.openNbtData(is);
			final double toAdd = Math.min(amount * 2, required);
			data.setDouble(WT_INTERNAL_POWER, currentStorage + toAdd);
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
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack is) {
		if (WTApi.instance().isWTCreative(is)) {
			return true;
		}
		if (LibConfig.USE_OLD_INFINTY_MECHANIC) {
			return checkForBooster(is);
		}
		return getEncryptionKey(is) != null && !getEncryptionKey(is).isEmpty() && WTApi.instance().hasInfiniteRange(is) && !WTApi.instance().isInRange(is);
	}

	@Override
	public void onUpdate(final ItemStack wirelessTerminal, final World w, final Entity e, int i, boolean f) {
		if (!(e instanceof EntityPlayer)) {
			return;
		}
		EntityPlayer p = (EntityPlayer) e;
		if (getPlayer() == null) {
			setPlayer(p);
		}
		if (wirelessTerminal == null || !(wirelessTerminal.getItem() instanceof ICustomWirelessTerminalItem)) {
			return;
		}
		if (p instanceof EntityPlayerMP) {
			rangeCheck(wirelessTerminal, (EntityPlayerMP) p);
		}
		WTApi.instance().isBoosterInstalled(wirelessTerminal);
	}

	private void rangeCheck(ItemStack wirelessTerm, EntityPlayerMP player) {
		boolean inRange = WTApi.instance().isInRangeOfWAP(wirelessTerm, player);
		WTApi.instance().setInRange(wirelessTerm, inRange);
		LibNetworking.instance().sendTo(new PacketSetInRange(inRange), player);
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.TRINKET;
	}

	@Override
	public IBaubleRender getRender() {
		return RenderLayerWT.getInstance();
	}

	@Override
	public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
		return false;
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase playerIn) {
		if (playerIn instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) playerIn;
			rangeCheck(itemstack, player);
		}
	}

}
