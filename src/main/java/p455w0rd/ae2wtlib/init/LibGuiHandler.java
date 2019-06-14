package p455w0rd.ae2wtlib.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import p455w0rd.ae2wtlib.AE2WTLib;
import p455w0rd.ae2wtlib.client.gui.GuiWUTTermSelection;

/**
 * @author p455w0rd
 *
 */
public class LibGuiHandler implements IGuiHandler {

	public static void register() {
		NetworkRegistry.INSTANCE.registerGuiHandler(AE2WTLib.INSTANCE, new LibGuiHandler());
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;//new ContainerWUTTerminalSelect();
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new GuiWUTTermSelection(player.getHeldItemMainhand());
	}

}
