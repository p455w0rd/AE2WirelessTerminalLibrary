package p455w0rd.ae2wtlib.api;

import java.lang.reflect.Method;

import javax.annotation.Nullable;

public abstract class WTApi {

	protected static WTApi api = null;

	@Nullable
	public static WTApi instance() {
		if (WTApi.api == null) {
			try {
				Class<?> clazz = Class.forName("p455w0rd.ae2wtlib.init.LibApiImpl");
				Method instanceAccessor = clazz.getMethod("instance");
				WTApi.api = (WTApi) instanceAccessor.invoke(null);
			}
			catch (Throwable e) {
				return null;
			}
		}

		return WTApi.api;
	}

	public abstract boolean isInfinityBoosterCardEnabled();

	public abstract boolean isOldInfinityMechanicEnabled();

}