package p455w0rd.ae2wtlib.api.item;

import java.util.*;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import appeng.api.config.Actionable;
import appeng.api.config.PowerUnits;
import appeng.api.util.IConfigManager;
import appeng.core.AEConfig;
import appeng.core.localization.GuiText;
import appeng.core.localization.PlayerMessages;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.util.ConfigManager;
import appeng.util.Platform;
import baubles.api.BaubleType;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.ae2wtlib.api.*;
import p455w0rd.ae2wtlib.api.client.IBaubleRender;
import p455w0rd.ae2wtlib.api.client.ItemStackSizeRenderer;
import p455w0rd.ae2wtlib.api.client.render.WTItemRenderer;
import p455w0rd.ae2wtlib.client.render.RenderLayerWT;
import p455w0rd.ae2wtlib.init.LibConfig;
import p455w0rd.ae2wtlib.init.LibCreativeTab;
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
		setCreativeTab(LibCreativeTab.CREATIVE_TAB);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addCheckedInformation(final ItemStack is, final World world, final List<String> list, final ITooltipFlag advancedTooltips) {
		if (hasValidGuiObject(is)) {
			addTooltipSeparator(list);
			addTooltipEnergyInfo(is, list);
			addTooltipLinkInfo(is, list);
			addTooltipInfinityInfo(is, list);
		}
	}

	@Override
	public void getCheckedSubItems(final CreativeTabs tab, final NonNullList<ItemStack> stacks) {
		if (!isCreative()) {
			final ItemStack emptyStack = new ItemStack(this);
			final ItemStack fullStack = new ItemStack(this);
			((AEBasePoweredItem) fullStack.getItem()).injectAEPower(fullStack, LibConfig.WT_MAX_POWER, Actionable.MODULATE);
			WTApi.instance().setInfinityEnergy(emptyStack, 0);
			WTApi.instance().setInfinityEnergy(fullStack, Integer.MAX_VALUE);
			stacks.addAll(Lists.newArrayList(emptyStack, fullStack));
		}
		else {
			stacks.add(new ItemStack(this));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
		final ItemStack item = player.getHeldItem(hand);
		if (world.isRemote && hand == EnumHand.MAIN_HAND && !item.isEmpty() && getAECurrentPower(item) > 0) {
			openGui(player, false, player.inventory.currentItem);
			return new ActionResult<>(EnumActionResult.SUCCESS, item);
		}
		else if (!world.isRemote) {
			if (getAECurrentPower(item) <= 0) {
				player.sendMessage(PlayerMessages.DeviceNotPowered.get());
				return new ActionResult<>(EnumActionResult.FAIL, item);
			}
			if (!WTApi.instance().isTerminalLinked(item)) {
				player.sendMessage(PlayerMessages.DeviceNotLinked.get());
				return new ActionResult<>(EnumActionResult.FAIL, item);
			}
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, item);
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

	@SideOnly(Side.CLIENT)
	protected void addTooltipInfinityInfo(final ItemStack wirelessTerminal, final List<String> tooltip) {
		if (WTApi.instance().getConfig().isInfinityBoosterCardEnabled()) {
			if (WTApi.instance().getConfig().isOldInfinityMechanicEnabled()) {
				tooltip.add(I18n.format("item.ae2wtlib:infinity_booster_card.name") + ": " + (hasInfiniteRange(wirelessTerminal) ? TextFormatting.GREEN + "" : TextFormatting.RED + "" + I18n.format("tooltip.not.desc")) + " " + I18n.format("tooltip.installed.desc"));
			}
			else {
				final int infinityEnergyAmount = WTApi.instance().getInfinityEnergy(wirelessTerminal);
				final String amountColor = infinityEnergyAmount < WTApi.instance().getConfig().getLowInfinityEnergyWarningAmount() ? TextFormatting.RED.toString() : TextFormatting.GREEN.toString();
				String reasonString = "";
				final boolean outsideOfWAPRange = !WTApi.instance().isInRange(wirelessTerminal);
				if (!outsideOfWAPRange && GuiScreen.isShiftKeyDown()) {
					reasonString = I18n.format("tooltip.in_wap_range.desc");
				}
				else if (infinityEnergyAmount <= 0 && GuiScreen.isShiftKeyDown()) {
					reasonString = "(" + I18n.format("tooltip.out_of.desc") + " " + I18n.format("tooltip.infinity_energy.desc") + ")";
				}
				final String activeString = infinityEnergyAmount > 0 && outsideOfWAPRange ? TextFormatting.GREEN + "" + I18n.format("tooltip.active.desc") : TextFormatting.GRAY + "" + I18n.format("tooltip.inactive.desc") + " " + reasonString;
				tooltip.add(I18n.format("tooltip.infinite_range.desc") + ": " + activeString);
				final String infinityEnergyString = WTApi.instance().isWTCreative(wirelessTerminal) ? I18n.format("tooltip.infinite.desc") : isShiftKeyDown() ? "" + infinityEnergyAmount + "" + TextFormatting.GRAY + " " + I18n.format("tooltip.units.desc") : ItemStackSizeRenderer.getInstance().getConverter().toSlimReadableForm(infinityEnergyAmount);
				tooltip.add(I18n.format("tooltip.infinity_energy.desc") + ": " + amountColor + "" + infinityEnergyString);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void addTooltipEnergyInfo(final ItemStack wirelessTerminal, final List<String> tooltip) {
		String pctTxtColor = TextFormatting.WHITE + "";
		final double aeCurrPower = getAECurrentPower(wirelessTerminal);
		final double aeCurrPowerPct = (int) Math.floor(aeCurrPower / getAEMaxPower(wirelessTerminal) * 1e4) / 1e2;
		if ((int) aeCurrPowerPct >= 75) {
			pctTxtColor = TextFormatting.GREEN + "";
		}
		if ((int) aeCurrPowerPct <= 5) {
			pctTxtColor = TextFormatting.RED + "";
		}
		if (WTApi.instance().isWTCreative(wirelessTerminal)) {
			tooltip.add(GuiText.StoredEnergy.getLocal() + ": " + TextFormatting.GREEN + "" + I18n.format("tooltip.infinite.desc"));
		}
		else {
			tooltip.add(GuiText.StoredEnergy.getLocal() + ": " + pctTxtColor + (int) aeCurrPower + " AE - " + aeCurrPowerPct + "%");
		}
	}

	@SideOnly(Side.CLIENT)
	protected void addTooltipLinkInfo(final ItemStack wirelessTerminal, final List<String> tooltip) {
		final String encKey = getEncryptionKey(wirelessTerminal);
		String linked = TextFormatting.RED + GuiText.Unlinked.getLocal();
		if (encKey != null && !encKey.isEmpty()) {
			linked = TextFormatting.BLUE + GuiText.Linked.getLocal();
		}
		tooltip.add(I18n.format("tooltip.link_status") + ": " + linked);
	}

	@SideOnly(Side.CLIENT)
	protected void addTooltipSeparator(final List<String> tooltip) {
		tooltip.add(TextFormatting.AQUA + "==============================");
	}

	protected boolean hasValidGuiObject(final ItemStack wirelessTerminal) {
		return getPlayer() != null && WTApi.instance().getGUIObject(wirelessTerminal, getPlayer()) != null;
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

}
