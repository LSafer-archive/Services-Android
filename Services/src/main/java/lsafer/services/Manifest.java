package lsafer.services;

import lsafer.services.io.TaskPart;

/**
 * manifest of properties in the plugin.
 *
 * @author LSaferSE
 * @version 1 alpha (19-Jul-19)
 * @since 19-Jul-19
 */
public abstract class Manifest {

    /**
     * class name that the plugin manifest should be.
     */
    final public static String NAME = "lsafer.services.Manifest";

    /**
     * get all task parts in the aar file.
     *
     * @return all task parts in the aar file
     */
    public abstract Class<? extends TaskPart>[] getTaskParts();

}
