package p455w0rd.ae2wtlib.integration;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.container.SlotBauble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import p455w0rd.ae2wtlib.api.ICustomWirelessTerminalItem;
import p455w0rd.ae2wtlib.api.WTBaublesAccess;
import p455w0rd.ae2wtlib.api.base.ContainerWT;
import p455w0rd.ae2wtlib.container.slot.SlotAEBauble;
import p455w0rd.ae2wtlib.init.LibNetworking;
import p455w0rd.ae2wtlib.sync.packets.PacketBaubleSync;

/**
 * @author p455w0rd
 *
 */
public class Baubles extends WTBaublesAccess {

	@Override
	public Pair<Integer, ItemStack> getFirstWTBauble(EntityPlayer player) {
		if (!getAllWTBaubles(player).isEmpty()) {
			return getAllWTBaubles(player).stream().findFirst().get();
		}
		return Pair.of(-1, ItemStack.EMPTY);
	}

	@Override
	public Set<Pair<Integer, ItemStack>> getAllWTBaubles(EntityPlayer player) {
		Set<Pair<Integer, ItemStack>> list = Sets.newHashSet();
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

	@Override
	public Set<Pair<Integer, ItemStack>> getAllWTBaublesByType(EntityPlayer player, Class<? extends ICustomWirelessTerminalItem> type) {
		Set<Pair<Integer, ItemStack>> list = Sets.newHashSet();
		if (player.hasCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)) {
			IBaublesItemHandler baubles = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
			for (int i = 0; i < baubles.getSlots(); i++) {
				if (baubles.getStackInSlot(i).isEmpty()) {
					continue;
				}
				Set<Class<?>> applicableInterfaces = Sets.newHashSet(ClassUtils.getAllInterfaces(baubles.getStackInSlot(i).getItem().getClass()));
				if (applicableInterfaces.contains(type)) {
					list.add(Pair.of(i, baubles.getStackInSlot(i)));
				}
			}
		}
		return list;
	}

	@Override
	public Pair<Integer, ItemStack> getFirstWTBaubleByType(EntityPlayer player, Class<? extends ICustomWirelessTerminalItem> type) {
		return getAllWTBaublesByType(player, type).stream().findFirst().get();
	}

	@Override
	public void updateWTBauble(EntityPlayer player, ItemStack wirelessTerm, int slot) {
		if (player.hasCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)) {
			IBaublesItemHandler baubles = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
			if (!baubles.getStackInSlot(slot).isEmpty() && baubles.getStackInSlot(slot).getItem() instanceof ICustomWirelessTerminalItem) {
				baubles.setStackInSlot(slot, wirelessTerm);
				baubles.setChanged(slot, true);
			}
		}
	}

	@Override
	public ItemStack getWTBySlot(EntityPlayer player, int slot, Class<? extends ICustomWirelessTerminalItem> type) {
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
	public int getWTBaubleSlotIndex(EntityPlayer player) {
		if (!getWTBauble(player).getRight().isEmpty()) {
			return getWTBauble(player).getLeft();
		}
		return -1;
	}
	*/

	@Override
	public IBaublesItemHandler getBaubles(EntityPlayer player) {
		if (player.hasCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)) {
			return player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
		}
		return null;
	}

	@Override
	public void setBaublesItemStack(EntityPlayer player, int slot, ItemStack stack) {
		if (player.hasCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)) {
			IBaublesItemHandler baubles = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
			baubles.setStackInSlot(slot, stack);
		}
	}

	@Override
	public void addBaubleSlots(ContainerWT container, EntityPlayer player) {
		IBaublesItemHandler baubles = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
		for (int i = 0; i < 7; i++) {
			container.addSlotToContainer(new SlotAEBauble(baubles, i, 178, -62 + i * 18));
		}
	}

	@Override
	public boolean isBaubleItem(ItemStack stack) {
		return stack.getItem() instanceof IBauble;
	}

	@Override
	public boolean isAEBaubleSlot(Slot slot) {
		return slot instanceof SlotAEBauble;
	}

	@Override
	public boolean isBaubleSlot(Slot slot) {
		return slot instanceof SlotBauble;
	}

	@Override
	public void sync(EntityPlayer player, ItemStack stack, int slot) {
		if (player instanceof EntityPlayerMP) {
			IBaublesItemHandler inv = getBaubles(player);
			for (int i = 0; i < inv.getSlots(); i++) {
				ItemStack currentStack = inv.getStackInSlot(i);
				if (currentStack.getItem() instanceof IBauble) {
					//IBauble bauble = (IBauble) currentStack.getItem();
					//BaubleType type = bauble.getBaubleType(currentStack);
					//if (bauble.getBaubleType(currentStack) == BaubleType.HEAD && WTApi.instance().isAnyWT(currentStack)) {
					updateWTBauble(player, stack, slot);
					LibNetworking.instance().sendTo(new PacketBaubleSync(stack, slot), (EntityPlayerMP) player);
					//}
				}
			}
		}
	}

}
