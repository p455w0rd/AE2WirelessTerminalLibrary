package p455w0rd.ae2wtlib.api;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * @author p455w0rd
 *
 */
public abstract class WUTUtility {

	public abstract boolean doesWUTSupportType(ItemStack wut, Class<?> type);

	public abstract Pair<ItemStack, Integer> getSelectedTerminal(ItemStack wut);

	public abstract boolean isWUT(ItemStack stack);

	public abstract ResourceLocation[] getMenuIcons(ItemStack wut);

	public abstract List<Pair<ItemStack, Integer>> getStoredTerminals(ItemStack wut);

}
