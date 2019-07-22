package lsafer.services.lang;

import dalvik.system.DexClassLoader;
import lsafer.services.Services;

/**
 * an extender class of {@link lsafer.lang.Reflect}
 * to support android {@link DexClassLoader}.
 *
 * @author LSaferSE
 * @version 1
 * @since 14-Jul-19
 */
final public class Reflect {

    /**
     * get a class that matches the given name and
     * stored at the given APK path.
     *
     * @param name   of the class
     * @param path   of the apk that contains the class
     * @param <TYPE> of the class
     * @return the class that matches the given parameters
     */
    public static <TYPE> Class<TYPE> getClass(String name, String path) {
        return (Class<TYPE>) lsafer.lang.Reflect.getClass(name, (n1) -> {
            try {
                return lsafer.lang.Reflect.getClassLoader(path, (n2) ->
                        new DexClassLoader(path, Services.PrivateCache.getAbsolutePath(),
                                null, Services.class.getClassLoader())).loadClass(name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

}
