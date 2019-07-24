package lsafer.services;

import lsafer.services.io.TaskPart;

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
    final public static String NAME = "lsafer.services.plugin.Index";

    /**
     * get all task parts in the aar file.
     *
     * @return all task parts in the aar file
     */
    public abstract Class<? extends TaskPart>[] getTaskParts();

}
