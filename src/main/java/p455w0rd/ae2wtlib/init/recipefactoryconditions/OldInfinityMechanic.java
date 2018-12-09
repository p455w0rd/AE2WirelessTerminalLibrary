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
public class OldInfinityMechanic implements IConditionFactory {

	@Override
	public BooleanSupplier parse(JsonContext jsonContext, JsonObject jsonObject) {
		return () -> LibConfig.USE_OLD_INFINTY_MECHANIC;
	}

}