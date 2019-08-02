package lsafer.services.util;

import android.content.Context;
import android.content.res.Configuration;

import lsafer.io.File;
import lsafer.util.AbstractStructure;
import lsafer.util.Structure;

/**
 * main passing arguments bundle.
 *
 * @author LSaferSE
 * @version 4
 * @since 11 Jun 2019
 */
public class Arguments extends AbstractStructure {

    /**
     * quick pass configuration.
     */
    public Configuration configuration;

    /**
     * quick pass context.
     */
    public Context context;

    /**
     * quick pass file.
     */
    public File file;

    /**
     * quick pass integer.
     */
    public Integer integer;

    /**
     * quick pass string.
     */
    public String string;

    /**
     * quick pass parent structure.
     */
    public Structure structure;

    /**
     * initialize this with the given arguments included
     * each argument will be mapped accordingly to it's
     * class's simple name (lower case) ex. Context -> context.
     *
     * @param arguments to be mapped
     */
    public Arguments(Object... arguments) {
        for (Object argument : arguments)
            if (argument != null) {
                if (argument instanceof Structure)
                    this.putAll((Structure) argument);

                if (argument instanceof Context)
                    this.put("context", argument);

                this.put(argument.getClass().getSimpleName().toLowerCase(), argument);
            }
    }

}
