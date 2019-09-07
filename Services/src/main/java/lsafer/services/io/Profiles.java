package lsafer.services.io;

import android.content.Context;
import lsafer.io.FolderStructure;
import lsafer.services.util.Arguments;

/**
 * A manager for a collection of {@link Profile profiles}.
 *
 * @author LSaferSE
 * @version 2 release (07-Sep-2019)
 * @since 14-Jul-19
 */
@FolderStructure.Defaults(folder = Profile.class)
final public class Profiles extends FolderStructure {
    /**
     * Call all profiles in this. To call all chains on them. To call foreach process on the specified index on foreach chain in this.
     *
     * @param context   used to start each process's service
     * @param index     of the processes (in chains) to be called
     * @param action    to pass to each process's service
     * @param method    to be invoked on each process
     * @param arguments to pass to each targeted method
     * @param <P>       this
     * @return this
     */
    public <P extends Profiles> P callAll(Context context, int index, String action, String method, Arguments arguments) {
        this.map(String.class, Profile.class).forEach((name, profile) -> profile.callAll(context, index, action, method, arguments));
        return (P) this;
    }

    /**
     * Call all processes in this.
     *
     * @param context   used to start each process's service
     * @param action    to pass to each process's service
     * @param method    to be invoked on each process
     * @param arguments to pass to each targeted method
     * @param <P>       this
     * @return this
     */
    public <P extends Profiles> P callAll(Context context, String action, String method, Arguments arguments) {
        this.map(String.class, Profile.class).forEach((name, profile) -> profile.callAll(context, action, method, arguments));
        return (P) this;
    }
}
