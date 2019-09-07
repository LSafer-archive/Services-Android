package lsafer.services.io;

import android.content.Context;
import lsafer.io.FolderStructure;
import lsafer.io.JSONFileStructure;
import lsafer.services.util.Arguments;

/**
 * A manager for a collection of {@link Chain Chains}.
 *
 * @author LSaferSE
 * @version 2 alpha (06-Sep-2019)
 * @since 14-Jul-19
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
@FolderStructure.Defaults(file = Chain.class)
final public class Profile extends FolderStructure {
    /**
     * Temporary file.
     */
    public Temp temp = new Temp();

    /**
     * Call all chains in this. To call foreach process on the specified index on foreach chain in this.
     *
     * @param context   used to start each process's service
     * @param index     of the processes (in chains) to be called
     * @param action    to pass to each process's service
     * @param method    to be invoked on each process
     * @param arguments to pass to each targeted method
     * @param <P>       this
     * @return this
     */
    public <P extends Profile> P callAll(Context context, int index, String action, String method, Arguments arguments) {
        this.temp.method = method;
        this.temp.action = action;
        this.temp.save();

        this.map(String.class, Chain.class).forEach((name, chain) -> chain.call(context, index, action, method, arguments));
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
    public <P extends Profile> P callAll(Context context, String action, String method, Arguments arguments) {
        this.temp.action = action;
        this.temp.method = method;
        this.temp.save();

        this.map(String.class, Chain.class).forEach((name, chain) -> chain.callAll(context, action, method, arguments));
        return (P) this;
    }

    /**
     * Temporary file of the targeted profile.
     */
    public static class Temp extends JSONFileStructure {
        /**
         * The last action have been called using this profile.
         */
        public String action = "";

        /**
         * The last method have been called using this profile.
         */
        public String method = "";
    }
}
