package lsafer.services;

import lsafer.io.File;
import lsafer.services.util.Arguments;

/**
 * library main class.
 *
 * @author LSaferSE
 * @version 1
 * @since 14-Jun-19
 */
final public class Services {

    /**
     * private cache directory.
     */
    public static File PrivateCache;

    /**
     * to avoid repetitive initializes.
     */
    private static boolean mInitialized = false;

    /**
     * initialize the library.
     *
     * @param args to initialize with
     */
    public static void initialize(Object... args) {
        if (!mInitialized) {
            Arguments arguments = Arguments.parse(args);

            PrivateCache = new File(arguments.context.getApplicationContext().getCodeCacheDir());
            mInitialized = true;
        }
    }

}
