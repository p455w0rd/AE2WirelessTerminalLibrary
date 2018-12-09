package p455w0rd.ae2wtlib.integration;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.container.SlotBauble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import p455w0rd.ae2wtlib.api.ICustomWirelessTerminalItem;
import p455w0rd.ae2wtlib.container.ContainerWT;
import p455w0rd.ae2wtlib.container.slot.SlotAEBauble;
import p455w0rd.ae2wtlib.init.LibNetworking;
import p455w0rd.ae2wtlib.sync.packets.PacketBaubleSync;
import p455w0rd.ae2wtlib.util.WTUtils;

/**
 * @author p455w0rd
 *
 */
public class Baubles {

	@Nonnull
	public static Pair<Integer, ItemStack> getFirstWTBauble(EntityPlayer player) {
		if (!getAllWTBaubles(player).isEmpty()) {
			return getAllWTBaubles(player).get(0);
		}
		return Pair.of(-1, ItemStack.EMPTY);
	}

	public static List<Pair<Integer, ItemStack>> getAllWTBaubles(EntityPlayer player) {
		List<Pair<Integer, ItemStack>> list = Lists.newArrayList();
		if (player.hasCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)) {
			IBaublesItemHandler baubles = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
			for (int i = 0; i < baubles.getSlots(); i++) {
				if (baubles.getStackInSlot(i).isEmpty()) {
					continue;
				}
				if (baubles.getStackInSlot(i).getItem() instanceof ICustomWirelessTerminalItem) {
					list.add(Pair.of(i, baubles.getStackInSlot(i)));
				}
			}
		}
		return list;
	}

	public static void updateWTBauble(EntityPlayer player, ItemStack wirelessTerm, int slot) {
		if (player.hasCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)) {
			IBaublesItemHandler baubles = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
			if (!baubles.getStackInSlot(slot).isEmpty() && baubles.getStackInSlot(slot).getItem() instanceof ICustomWirelessTerminalItem) {
				baubles.setStackInSlot(slot, wirelessTerm);
				baubles.setChanged(slot, true);
			}
		}
	}

	public static ItemStack getWTBySlot(EntityPlayer player, int slot, Class<? extends ICustomWirelessTerminalItem> type) {
		if (player.hasCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)) {
			IBaublesItemHandler baubles = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
			ItemStack wirelessTerminal = baubles.getStackInSlot(slot);
			if (!wirelessTerminal.isEmpty()) {
				Class<?> clazz = wirelessTerminal.getItem().getClass();
				Class<?>[] interfaces = clazz.getInterfaces();
				if (interfaces.length <= 0) {
					// this should only happen for creative versions
					clazz = clazz.getSuperclass();
					interfaces = clazz.getInterfaces();
				}
				List<Class<?>> applicableInterfaces = Lists.newArrayList(interfaces);
				if (wirelessTerminal.getItem() instanceof ICustomWirelessTerminalItem) {
					applicableInterfaces.add(ICustomWirelessTerminalItem.class);
				}
				if (!wirelessTerminal.isEmpty() && applicableInterfaces.contains(type)) {
					return wirelessTerminal;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	/*
	public static int getWTBaubleSlotIndex(EntityPlayer player) {
		if (!getWTBauble(player).getRight().isEmpty()) {
			return getWTBauble(player).getLeft();
		}
		return -1;
	}
	*/

	public static IBaublesItemHandler getBaubles(EntityPlayer player) {
		if (player.hasCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)) {
			return player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
		}
		return null;
	}

	public static void setBaublesItemStack(EntityPlayer player, int slot, ItemStack stack) {
		if (player.hasCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)) {
			IBaublesItemHandler baubles = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
			baubles.setStackInSlot(slot, stack);
		}
	}

	public static void addBaubleSlots(ContainerWT container, EntityPlayer player) {
		IBaublesItemHandler baubles = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
		for (int i = 0; i < 7; i++) {
			container.addSlotToContainer(new SlotAEBauble(baubles, i, 178, -62 + i * 18));
		}
	}

	public static boolean isBaubleItem(ItemStack stack) {
		return stack.getItem() instanceof IBauble;
	}

	public static boolean isAEBaubleSlot(Slot slot) {
		return slot instanceof SlotAEBauble;
	}

	public static boolean isBaubleSlot(Slot slot) {
		return slot instanceof SlotBauble;
	}

	public static void sync(EntityPlayer player, ItemStack stack, int slot) {
		if (player instanceof EntityPlayerMP) {
			IBaublesItemHandler inv = getBaubles(player);
			for (int i = 0; i < inv.getSlots(); i++) {
				ItemStack currentStack = inv.getStackInSlot(i);
				if (currentStack.getItem() instanceof IBauble) {
					IBauble bauble = (IBauble) currentStack.getItem();
					BaubleType type = bauble.getBaubleType(currentStack);
					if (bauble.getBaubleType(currentStack) == BaubleType.HEAD && WTUtils.isAnyWT(currentStack)) {
						updateWTBauble(player, stack, slot);
						LibNetworking.instance().sendTo(new PacketBaubleSync(stack, slot), (EntityPlayerMP) player);
					}
				}
			}
		}
	}

}
