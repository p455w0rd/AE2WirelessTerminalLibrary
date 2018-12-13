package p455w0rd.ae2wtlib.init;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author p455w0rd
 *
 */
public class LibLogger {

	private static Logger LOGGER = LogManager.getLogger(LibGlobals.NAME);
	public static String LOG_PREFIX = "==========[Start " + LibGlobals.NAME + " %s]========";
	public static String LOG_SUFFIX = "==========[End " + LibGlobals.NAME + " %s]==========";

	public static void warn(String msg) {
		LOGGER.warn(msg);
	}

	public static void error(String msg) {
		LOGGER.error(msg);
	}

	public static void infoBegin(String headerInfo) {
		String header = String.format(LOG_PREFIX, headerInfo);
		LOGGER.info(header);
	}

	public static void infoBegin(String headerInfo, String msg) {
		String header = String.format(LOG_PREFIX, headerInfo);
		LOGGER.info(header);
		LOGGER.info(msg);
	}

	public static void infoEnd(String footerInfo) {
		String footer = String.format(LOG_SUFFIX, footerInfo);
		LOGGER.info(footer);
	}

	public static void info(String msg) {
		LOGGER.info(msg);
	}

	public static void debug(String msg) {
		LOGGER.debug(msg);
	}

}