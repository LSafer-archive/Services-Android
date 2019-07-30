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
     * init this.
     *
     * @param args no-use
     */
    public Arguments(Object... args) {
        super(args);

    }

    /**
     * to get new pre-resolved fields instance of this.
     *
     * @param args to init this with
     * @return new of this with fields filled with the passed arguments
     */
    public static Arguments parse(Object... args) {
        Arguments arguments = new Arguments();

        for (Object arg : args) {
            if (arg instanceof Structure)
                arguments.putAll((Structure) arg);
            else if (arg instanceof Context)
                arguments.context = (Context) arg;

            if (arg != null)
                arguments.put(arg.getClass().getSimpleName().toLowerCase(), arg);
        }

        return arguments;
    }

}
