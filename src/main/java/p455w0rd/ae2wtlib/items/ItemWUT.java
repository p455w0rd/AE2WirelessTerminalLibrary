package p455w0rd.ae2wtlib.items;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import appeng.api.features.IWirelessTermHandler;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.storage.IStorageChannel;
import appeng.api.util.IConfigManager;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.ae2wtlib.AE2WTLib;
import p455w0rd.ae2wtlib.api.ICustomWirelessTerminalItem;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.api.item.ItemWT;
import p455w0rd.ae2wtlib.helpers.IWirelessUniversalItem;
import p455w0rd.ae2wtlib.init.*;

/**
 * @author p455w0rd
 *
 */
public class ItemWUT extends ItemWT implements IWirelessUniversalItem {

	private static final String STOREDTERMINALS_KEY = "StoredTerminals";
	private static final String SELECTEDTERMINAL_KEY = "SelectedTerminal";
	private ICustomWirelessTerminalItem selectedTerminal;

	public ItemWUT() {
		super(new ResourceLocation(LibGlobals.MODID, "wut"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addCheckedInformation(final ItemStack wut, final World world, final List<String> list, final ITooltipFlag advancedTooltips) {
		final ItemStack selectedTerminal = getSelectedTerminalStack(wut).getLeft();
		final String title = list.get(0);
		list.clear();
		if (!title.isEmpty()) {
			list.add(title);
		}
		if (!selectedTerminal.isEmpty()) {
			list.add(TextFormatting.WHITE + "" + TextFormatting.ITALIC + "" + selectedTerminal.getDisplayName());
			if (((ICustomWirelessTerminalItem) selectedTerminal.getItem()).getPlayer() == null && getPlayer() != null) {
				((ICustomWirelessTerminalItem) selectedTerminal.getItem()).setPlayer(getPlayer());
			}
			final List<Pair<ItemStack, Integer>> installedTerminals = WTApi.instance().getWUTUtility().getStoredTerminals(wut);
			if (!installedTerminals.isEmpty()) {
				list.add(1, TextFormatting.UNDERLINE + "      Installed Modules      ");
				if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
					list.add(2, "Press Shift");
				}
				else {
					for (int i = 0; i < installedTerminals.size(); i++) {
						final String underline = i == installedTerminals.size() - 1 ? TextFormatting.UNDERLINE.toString() : "";
						list.add(2 + i, TextFormatting.ITALIC + "" + underline + " " + installedTerminals.get(i).getLeft().getDisplayName());
					}
				}
			}
			((AEBasePoweredItem) selectedTerminal.getItem()).addCheckedInformation(wut, world, list, advancedTooltips);
		}
	}

	@Override
	public IConfigManager getConfigManager(final ItemStack wut) {
		final ItemStack selectedTerminal = getSelectedTerminalStack(wut).getLeft();
		if (!selectedTerminal.isEmpty()) {
			return ((IWirelessTermHandler) selectedTerminal.getItem()).getConfigManager(wut);
		}
		return null;
	}

	@Override
	public IStorageChannel<?> getStorageChannel(final ItemStack wut) {
		final ItemStack selectedStack = getSelectedTerminalStack(wut).getLeft();
		if (!selectedStack.isEmpty()) {
			return ((ICustomWirelessTerminalItem) selectedStack.getItem()).getStorageChannel(selectedStack);
		}
		return null;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
		final ItemStack item = player.getHeldItem(hand);
		if (hand == EnumHand.MAIN_HAND && !item.isEmpty() && getAECurrentPower(item) > 0) {
			if (!player.isSneaking()) {
				if (world.isRemote) {
					openGui(player, false, player.inventory.currentItem);
				}
			}
			else {
				if (world.isRemote) {
					player.openGui(AE2WTLib.INSTANCE, 0, world, 0, 0, 0);
				}
				/*
				cycleSelectedTerminal(item);
				Pair<ICustomWirelessTerminalItem, Integer> selectedItem = getSelectedTerminalHandler(item);
				if (!world.isRemote && selectedItem != null) {
					ItemStack selectedTerm = getSelectedTerminalStack(item).getLeft();
					player.sendMessage(new TextComponentString(TextFormatting.ITALIC + "" + selectedTerm.getDisplayName() + " Selected"));
				}
				*/
			}
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, item);
	}

	@Override
	public void openGui(final EntityPlayer player, final boolean isBauble, final int playerSlot) {
		final ItemStack heldStack = player.inventory.getCurrentItem();
		if (heldStack.getItem() instanceof ItemWUT) {
			final ICustomWirelessTerminalItem terminalItem = getSelectedTerminalHandler(heldStack).getLeft();
			if (terminalItem != null) {
				selectedTerminal = terminalItem;
			}
		}
		selectedTerminal.openGui(player, isBauble, playerSlot);
	}

	public static List<Pair<ICustomWirelessTerminalItem, Integer>> getStoredTerminalHandlers(final ItemStack wut) {
		final List<Pair<ItemStack, Integer>> t = getStoredTerminalStacks(wut);
		final List<Pair<ICustomWirelessTerminalItem, Integer>> c = new ArrayList<>();
		for (int i = 0; i < t.size(); i++) {
			c.add(Pair.of((ICustomWirelessTerminalItem) t.get(i).getLeft().getItem(), t.get(i).getRight()));
		}
		return c;
	}

	public static List<Pair<ItemStack, Integer>> getStoredTerminalStacks(final ItemStack wut) {
		if (wut.hasTagCompound() && wut.getTagCompound().hasKey(STOREDTERMINALS_KEY, NBT.TAG_LIST)) {
			final NBTTagList terminalList = wut.getTagCompound().getTagList(STOREDTERMINALS_KEY, NBT.TAG_COMPOUND);
			if (terminalList.tagCount() > 0) {
				final List<Pair<ItemStack, Integer>> returnList = new ArrayList<>();
				for (int i = 0; i < terminalList.tagCount(); i++) {
					final ItemStack newStack = new ItemStack(terminalList.getCompoundTagAt(i));
					if (newStack.hasTagCompound()) {
						newStack.setTagCompound(null);
					}
					returnList.add(Pair.of(newStack, i));
				}
				return returnList;
			}
		}
		return Lists.newArrayList(Pair.of(ItemStack.EMPTY, -1));
	}

	public static Pair<ItemStack, Integer> getSelectedTerminalStack(final ItemStack wut) {
		final List<Pair<ItemStack, Integer>> terminals = getStoredTerminalStacks(wut);
		if (terminals.size() > 0 && wut.hasTagCompound() && wut.getTagCompound().hasKey(SELECTEDTERMINAL_KEY, NBT.TAG_INT)) {
			final int index = wut.getTagCompound().getInteger(SELECTEDTERMINAL_KEY);
			if (index < terminals.size()) {
				return terminals.get(index);
			}
		}
		return Pair.of(ItemStack.EMPTY, -1);
	}

	public static Pair<ICustomWirelessTerminalItem, Integer> getSelectedTerminalHandler(final ItemStack wut) {
		final List<Pair<ICustomWirelessTerminalItem, Integer>> terminals = getStoredTerminalHandlers(wut);
		if (terminals.size() > 0 && wut.hasTagCompound() && wut.getTagCompound().hasKey(SELECTEDTERMINAL_KEY, NBT.TAG_INT)) {
			final int index = wut.getTagCompound().getInteger(SELECTEDTERMINAL_KEY);
			if (index < terminals.size()) {
				return Pair.of(terminals.get(index).getLeft(), index);
			}
		}
		return null;
	}

	public static void setSelectedTerminal(final ItemStack wut, final int index) {
		if (!wut.hasTagCompound()) {
			wut.setTagCompound(new NBTTagCompound());
		}
		final List<Pair<ICustomWirelessTerminalItem, Integer>> storedTerminals = getStoredTerminalHandlers(wut);
		if (storedTerminals.size() > 0 && index < storedTerminals.size()) {
			wut.getTagCompound().setInteger(SELECTEDTERMINAL_KEY, index);
			return;
		}
		wut.getTagCompound().setInteger(SELECTEDTERMINAL_KEY, -1);
	}

	public static ItemStack getStoredTerminalByIndex(final ItemStack wut, final int index) {
		final List<Pair<ItemStack, Integer>> storedList = getStoredTerminalStacks(wut);
		if (index < storedList.size()) {
			return storedList.get(index).getLeft();
		}
		return ItemStack.EMPTY;
	}

	public static void cycleSelectedTerminal(final ItemStack wut) {
		cycleSelectedTerminal(wut, false);
	}

	public static void cycleSelectedTerminal(final ItemStack wut, final boolean reverse) {
		final List<Pair<ICustomWirelessTerminalItem, Integer>> storedList = getStoredTerminalHandlers(wut);
		if (storedList.size() > 0) {
			if (storedList.size() == 2) {
				final Pair<ICustomWirelessTerminalItem, Integer> currentSelection = getSelectedTerminalHandler(wut);
				setSelectedTerminal(wut, currentSelection.getRight() == 0 ? 1 : 0);
				return;
			}
			final Pair<ICustomWirelessTerminalItem, Integer> currentSelection = getSelectedTerminalHandler(wut);
			if (currentSelection != null) {
				setSelectedTerminal(wut, !reverse ? getPreviousIndex(wut) : getNextIndex(wut));
			}
		}
	}

	public static int getNextIndex(final ItemStack wut) {
		final Pair<ICustomWirelessTerminalItem, Integer> currentSelection = getSelectedTerminalHandler(wut);
		if (currentSelection != null) {
			int previousIndex = 0;
			previousIndex = currentSelection.getRight() - 1;
			if (previousIndex < 0) {
				previousIndex = getStoredTerminalHandlers(wut).size() - 1;
			}
			return previousIndex;
		}
		return -1;
	}

	public static int getPreviousIndex(final ItemStack wut) {
		final Pair<ICustomWirelessTerminalItem, Integer> currentSelection = getSelectedTerminalHandler(wut);
		if (currentSelection != null) {
			int nextIndex = 0;
			nextIndex = currentSelection.getRight() + 1;
			if (nextIndex >= getStoredTerminalHandlers(wut).size()) {
				nextIndex = 0;
			}
			return nextIndex;
		}
		return -1;
	}

	public static ItemStack addTerminal(final ItemStack wut, final ItemStack otherTerminal) {
		final ItemStack newWut = wut.copy();
		final NBTTagList nbtList = newWut.getTagCompound().getTagList(STOREDTERMINALS_KEY, NBT.TAG_COMPOUND);
		final ItemStack emptyOtherTerm = otherTerminal.copy();
		emptyOtherTerm.setTagCompound(null);
		nbtList.appendTag(emptyOtherTerm.writeToNBT(new NBTTagCompound()));
		if (otherTerminal.hasTagCompound()) {
			newWut.getTagCompound().merge(otherTerminal.getTagCompound());
		}
		final IAEItemPowerStorage powerItem = (IAEItemPowerStorage) wut.getItem();
		final double maxPower = powerItem.getAEMaxPower(wut);
		double totalPower = powerItem.getAECurrentPower(wut);
		int infinityEnergy = WTApi.instance().getInfinityEnergy(wut);
		infinityEnergy += WTApi.instance().getInfinityEnergy(otherTerminal);
		infinityEnergy = infinityEnergy > Integer.MAX_VALUE ? Integer.MAX_VALUE : infinityEnergy;
		//WTApi.instance().setInfinityEnergy(wut, 0);
		//WTApi.instance().setInfinityEnergy(otherTerminal, 0);
		totalPower += powerItem.getAECurrentPower(otherTerminal);
		final double newPower = totalPower > maxPower ? maxPower : totalPower;
		newWut.getTagCompound().setDouble("internalCurrentPower", newPower);
		WTApi.instance().setInfinityEnergy(newWut, infinityEnergy);

		//wut.getTagCompound().setTag(STOREDTERMINALS_KEY, nbtList);
		//if (!wut.getTagCompound().hasKey(SELECTEDTERMINAL_KEY, NBT.TAG_INT)) {
		//	wut.getTagCompound().setInteger(SELECTEDTERMINAL_KEY, 0);
		//}
		return newWut;
	}

	public static boolean isTypeInstalled(final ItemStack wut, final Class<?> type) {
		/*
		Set<Pair<ItemStack, Integer>> storedStacks = Sets.newHashSet(getStoredTerminalStacks(wut));
		boolean x = storedStacks.stream().map(Pair::getLeft).map(ItemStack::getItem).map(Item::getClass).anyMatch(ClassUtils.getAllInterfaces(type)::contains);
		return x;
		*/
		final List<Pair<ItemStack, Integer>> storedStacks = getStoredTerminalStacks(wut);
		if (wut.getItem() == LibItems.ULTIMATE_TERMINAL) {
			if (storedStacks.size() > 0) {
				for (final Pair<ItemStack, Integer> currentStack : storedStacks) {
					if (!currentStack.getLeft().isEmpty()) {
						final Class<?> itemClass = currentStack.getLeft().getItem().getClass();
						final List<Class<?>> interfaces = ClassUtils.getAllInterfaces(itemClass);
						if (interfaces.contains(type)) {
							return true;
						}
					}
				}
			}
		}
		return false;

	}

	public static boolean isTypeInstalled(final ItemStack wut, final Item type) {
		return Sets.newHashSet(getStoredTerminalStacks(wut)).stream().map(Pair::getLeft).map(ItemStack::getItem).anyMatch(type::equals);
	}

	public static List<ICustomWirelessTerminalItem> getIntegratableTypes() {
		final List<ICustomWirelessTerminalItem> list = new ArrayList<>();
		for (final ICustomWirelessTerminalItem wt : WTApi.instance().getWirelessTerminalRegistry().getRegisteredTerminals()) {
			if (!LibRecipes.isCreative(new ItemStack((Item) wt)) && !LibRecipes.isWut(new ItemStack((Item) wt))) {
				list.add(wt);
			}
		}
		return list;
	}

	public static ItemStack createNewWUT(final ItemStack terminalA, final ItemStack terminalB) {
		if (terminalA.getItem() instanceof ICustomWirelessTerminalItem && terminalB.getItem() instanceof ICustomWirelessTerminalItem && terminalA.getItem() != terminalB.getItem()) {
			final ItemStack wut = new ItemStack(LibItems.ULTIMATE_TERMINAL);
			final NBTTagCompound nbt = new NBTTagCompound();
			final NBTTagList terminalList = new NBTTagList();
			String encKey = "";
			final IAEItemPowerStorage powerItem = (IAEItemPowerStorage) wut.getItem();
			final double maxPower = powerItem.getAEMaxPower(wut);
			double totalPower = powerItem.getAECurrentPower(terminalA);
			int infinityEnergy = WTApi.instance().getInfinityEnergy(terminalA);
			infinityEnergy += WTApi.instance().getInfinityEnergy(terminalB);
			infinityEnergy = infinityEnergy > Integer.MAX_VALUE ? Integer.MAX_VALUE : infinityEnergy;
			WTApi.instance().setInfinityEnergy(terminalA, 0);
			WTApi.instance().setInfinityEnergy(terminalB, 0);
			totalPower += powerItem.getAECurrentPower(terminalB);
			final double newPower = totalPower > maxPower ? maxPower : totalPower;
			final ItemStack emptyTermA = terminalA.copy();
			final ItemStack emptyTermB = terminalB.copy();
			emptyTermA.setTagCompound(null);
			emptyTermB.setTagCompound(null);
			terminalList.appendTag(emptyTermA.writeToNBT(new NBTTagCompound()));
			encKey = ((ItemWT) terminalA.getItem()).getEncryptionKey(terminalA);
			terminalList.appendTag(emptyTermB.writeToNBT(new NBTTagCompound()));
			if (encKey.isEmpty()) {
				encKey = ((ItemWT) terminalB.getItem()).getEncryptionKey(terminalB);
			}
			nbt.merge(terminalA.getTagCompound());
			nbt.merge(terminalB.getTagCompound());
			nbt.setTag(STOREDTERMINALS_KEY, terminalList);
			nbt.setInteger(SELECTEDTERMINAL_KEY, 0);
			nbt.setDouble("internalCurrentPower", newPower);
			wut.setTagCompound(nbt);
			WTApi.instance().setInfinityEnergy(wut, infinityEnergy);
			if (!encKey.isEmpty()) {
				((ItemWT) wut.getItem()).setEncryptionKey(wut, encKey, "");
			}
			return wut;
		}
		return ItemStack.EMPTY;
	}

	public static ResourceLocation[] getMenuIcons(final ItemStack wut) {
		final List<Pair<ItemStack, Integer>> stacks = getStoredTerminalStacks(wut);
		final ResourceLocation[] icons = new ResourceLocation[stacks.size()];
		for (int i = 0; i < stacks.size(); i++) {
			icons[i] = ((ICustomWirelessTerminalItem) stacks.get(i).getLeft().getItem()).getMenuIcon();
		}
		return icons;
	}

	@Override
	public ResourceLocation getMenuIcon() {
		return new ResourceLocation(LibGlobals.MODID, "textures/items/wut");
	}

	@Override
	public int getColor() {
		return 0xFF4E9912;
	}

}
