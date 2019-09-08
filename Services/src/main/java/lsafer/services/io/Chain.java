package lsafer.services.io;

import android.content.Context;
import android.content.res.Resources;
import lsafer.io.FileStructure;
import lsafer.io.JSONFileStructure;
import lsafer.services.annotation.Controller;
import lsafer.services.annotation.Entry;
import lsafer.services.util.Arguments;
import lsafer.services.util.Process;
import lsafer.services.util.Properties;
import lsafer.util.ArrayStructure;

/**
 * A manager of a chain of processes.
 *
 * @author LSaferSE
 * @version 2 release (06-Sep-2019)
 * @since 14-Jul-19
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "FieldCanBeLocal", "unused"})
@Controller
final public class Chain extends JSONFileStructure {
    /**
     * Whether this task is activated or not.
     */
    @Entry(value = {"true", "false"})
    public Boolean activated = false;

    /**
     * The chained processes.
     */
    public ArrayStructure processes = new ArrayStructure();

    @Override
    public <F extends FileStructure> F load() {
        super.<Chain>load();
        this.processes.castAll(Process.class).map(Integer.class, Process.class).forEach((index, process) -> process.attach(this, index));
        return (F) this;
    }

    /**
     * Call a specific process by it's index.
     *
     * @param context   used to start the process's service
     * @param index     of the process
     * @param action    to pass to the process's service
     * @param method    to be invoked on the targeted process
     * @param arguments to pass to the targeted method
     * @param <C>       this
     * @return this
     */
    public <C extends Chain> C call(Context context, int index, String action, String method, Arguments arguments) {
        if (index > -1 && index < this.processes.size())
            this.processes.<Process>get(index).call(context, this, action, method, arguments);
        return (C) this;
    }

    /**
     * Call all processes contained in this.
     *
     * @param context   used to start each process's service
     * @param action    to pass to each process's service
     * @param method    to be invoked on each process
     * @param arguments to pass to each targeted method
     * @param <C>       this
     * @return this
     */
    public <C extends Chain> C callAll(Context context, String action, String method, Arguments arguments) {
        this.processes.list(Process.class).forEach(process -> process.call(context, this, action, method, arguments));
        return (C) this;
    }

    /**
     * Get the a {@link Properties} of this.
     *
     * @param resources to get strings from
     * @return this chain's properties
     */
    public Properties properties(Resources resources) {
        return new Properties(resources, this);
    }
}
