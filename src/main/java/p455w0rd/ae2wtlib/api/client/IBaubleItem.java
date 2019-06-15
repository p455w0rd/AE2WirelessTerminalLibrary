package p455w0rd.ae2wtlib.api.client;

import baubles.api.IBauble;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles", striprefs = true)
public interface IBaubleItem extends IBauble {

	@Optional.Method(modid = "baubles")
	IBaubleRender getRender();

}
