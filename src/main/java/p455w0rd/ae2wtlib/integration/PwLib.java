package p455w0rd.ae2wtlib.integration;

import java.util.List;

import appeng.tile.misc.TileCharger;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import p455w0rd.ae2wtlib.api.ICustomWirelessTerminalItem;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.init.LibConfig;
import p455w0rdslib.api.client.shader.Light;
import p455w0rdslib.capabilities.CapabilityLightEmitter;
import p455w0rdslib.capabilities.CapabilityLightEmitter.StackLightEmitter;
import p455w0rdslib.capabilities.CapabilityLightEmitter.TileLightEmitter;
import p455w0rdslib.handlers.BrightnessHandler;
import p455w0rdslib.util.RenderUtils;

/**
 * @author p455w0rd
 *
 */
public class PwLib {

	public static <T> T getStackCapability(final ItemStack stack) {
		return CapabilityLightEmitter.LIGHT_EMITTER_CAPABILITY.cast(

				new StackLightEmitter(stack) {

					@Override
					public List<Light> emitLight(final List<Light> lights, final Entity entity) {
						if (LibConfig.ENABLE_COLORED_LIGHTING && WTApi.instance().isAnyWT(stack)) {
							final ItemStack lightStack = stack.copy();
							if (!lightStack.isEmpty()) {
								final Vec3i c = RenderUtils.hexToRGB(((ICustomWirelessTerminalItem) lightStack.getItem()).getColor());
								lights.add(Light.builder().pos(entity).color(c.getX(), c.getY(), c.getZ(), BrightnessHandler.getBrightness(entity).value() * 0.001f).radius(2f).intensity(2.5f).build());
							}
							else {
								BrightnessHandler.getBrightness(entity).reset();
							}
						}
						return lights;
					}
				});
	}

	public static ICapabilityProvider getChargerProvider(final TileCharger charger) {
		return new CapabilityLightEmitter.DummyLightProvider(getChargerCapability(charger));
	}

	public static <T> T getChargerCapability(final TileCharger charger) {
		return CapabilityLightEmitter.LIGHT_EMITTER_CAPABILITY.cast(

				new TileLightEmitter(charger) {

					@Override
					public List<Light> emitLight(final List<Light> lights, final TileEntity tile) {
						ItemStack lightStack = ItemStack.EMPTY;
						if (tile instanceof TileCharger) {
							final TileCharger charger = (TileCharger) tile;
							lightStack = charger.getInternalInventory().getStackInSlot(0);
						}
						if (LibConfig.ENABLE_COLORED_LIGHTING && WTApi.instance().isAnyWT(lightStack)) {
							final Vec3i c = RenderUtils.hexToRGB(((ICustomWirelessTerminalItem) lightStack.getItem()).getColor());
							lights.add(Light.builder().pos(tile.getPos().add(0.5d, 0.5d, 0.5d)).color(c.getX(), c.getY(), c.getZ(), (float) (BrightnessHandler.getBrightness(tile).value() * 0.001)).radius(3f).intensity(5).build());
						}
						else {
							final Vec3i c = RenderUtils.hexToRGB(0xFFFFFFFF);
							lights.add(Light.builder().pos(tile.getPos().add(0.5d, 0.5d, 0.5d)).color(c.getX(), c.getY(), c.getZ(), 1.0f).radius(1f).intensity(10).build());
							//BrightnessHandler.getBrightness(tile).reset();
						}
						return lights;
					}
				});
	}

	public static boolean checkCap(final Capability<?> capability) {
		return CapabilityLightEmitter.checkCap(capability) && WTApi.instance().getConfig().areShadersEnabled();
	}

}
