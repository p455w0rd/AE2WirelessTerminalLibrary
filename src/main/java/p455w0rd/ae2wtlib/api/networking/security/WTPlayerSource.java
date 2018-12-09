package p455w0rd.ae2wtlib.api.networking.security;

import java.util.Optional;

import com.google.common.base.Preconditions;

import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import net.minecraft.entity.player.EntityPlayer;

public class WTPlayerSource implements IActionSource {

	public final EntityPlayer player;
	public final WTIActionHost via;

	public WTPlayerSource(final EntityPlayer p, final WTIActionHost v) {
		Preconditions.checkNotNull(p);
		player = p;
		via = v;
	}

	@Override
	public Optional<EntityPlayer> player() {
		return Optional.of(player);
	}

	@Override
	public Optional<IActionHost> machine() {
		return Optional.ofNullable(via);
	}

	@Override
	public <T> Optional<T> context(Class<T> key) {
		return Optional.empty();
	}
}
