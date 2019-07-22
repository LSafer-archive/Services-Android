package lsafer.services;

import android.os.Environment;

import lsafer.io.File;
import lsafer.services.util.Arguments;

/**
 * library main class.
 *
 * @author LSaferSE
 * @version 1
 * @since 14-Jun-19
 */
@SuppressWarnings({"unused"})
final public class Services {

    /**
     * private cache directory.
     */
    public static File PrivateCache;

    /**
     * storage where all this library's data get stored at.
     */
    public static File Storage;

    /**
     * to avoid repetitive initializes.
     */
    private static boolean mInitialized = false;

    /**
     * initialize the library.
     *
     * @param args to initialize with
     */
    @SuppressWarnings("deprecation")
    public static void initialize(Object... args) {
        if (!mInitialized) {
            Arguments arguments = Arguments.newInstance(args);

            Storage = new File(Environment.getExternalStoragePublicDirectory("Services"));
            PrivateCache = new File(arguments.context.getApplicationContext().getCodeCacheDir());

            mInitialized = true;
        }
    }

}
