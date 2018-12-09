package p455w0rd.ae2wtlib.init.recipefactoryconditions;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;

import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import p455w0rd.ae2wtlib.init.LibConfig;

/**
 * @author p455w0rd
 *
 */
public class BoosterRecipeEnabled implements IConditionFactory {

	@Override
	public BooleanSupplier parse(JsonContext jsonContext, JsonObject jsonObject) {
		return () -> LibConfig.WT_BOOSTER_ENABLED && !LibConfig.WT_DISABLE_BOOSTER_RECIPE;
	}

}