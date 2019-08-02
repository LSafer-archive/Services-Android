package lsafer.services.util;

import lsafer.services.io.Profile;
import lsafer.services.io.Task;

/**
 * manifest of properties in the plugin.
 *
 * @author LSaferSE
 * @version 1 alpha (19-Jul-19)
 * @since 19-Jul-19
 */
public abstract class Index {

    /**
     * class name that the plugin's index class should have.
     */
    final public static String NAME = "lsafer.services.Index";

    /**
     * get all task parts in the apk file of the plugin that implements the services library.
     *
     * @return all task parts in the apk file
     */
    public abstract Task.Part[] getTaskParts();

    /**
     * get all tasks in the apk file of the plugin that implements the services library.
     *
     * @return all tasks in the apk file
     */
    public abstract Task[] getTasks();

    /**
     * get all profiles in the apk file of the plugin that implements the services library.
     *
     * @return all profiles in the apk file
     */
    public abstract Profile[] getProfiles();

}
