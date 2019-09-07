package lsafer.services.util;

import android.os.Bundle;
import lsafer.util.ExtraStructure;
import lsafer.util.HashStructure;

import java.io.Serializable;
import java.util.Locale;

/**
 * A hash-structure that uses as a bundle in Services library.
 *
 * @author LSaferSE
 * @version 5 release (07-Sep-2019)
 * @since 11 Jun 2019
 */
@SuppressWarnings("WeakerAccess")
public class Arguments extends HashStructure implements ExtraStructure, Serializable {
    /**
     * Initialize this with the given arguments. Each argument will be mapped accordingly to it's class's simple name (lower case).
     * unless it's an {@link Arguments arguments object} or {@link Bundle bundle object} then it'll be put all in this.
     *
     * <br><br><b>example:</b>
     * <pre>
     *     Integer 0    -> put("integer", 0)
     *     Boolean true -> put("boolean", true)
     * </pre>
     *
     * @param arguments to be mapped
     */
    public Arguments(Object... arguments) {
        for (Object argument : arguments)
            if (argument instanceof Arguments)
                this.putAll((Arguments) argument);
            else if (argument instanceof Bundle)
                this.putAll((Bundle) argument);
            else if (argument != null)
                this.put(argument.getClass().getSimpleName().toLowerCase(Locale.US), argument);
    }
}
