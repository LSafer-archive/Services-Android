package lsafer.services.support;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import lsafer.services.util.Arguments;

/**
 * to listen to targeted action.
 *
 * @author LSaefr
 * @version 2
 * @since 29 Jun 2019
 */
final public class BroadcastReceiver extends android.content.BroadcastReceiver {

    /**
     * all instances created by this.
     */
    private static Map<String, BroadcastReceiver> Receivers = new HashMap<>();

    /**
     * functions linked to this.
     */
    final private List<Function<Arguments, ?>> functions = new ArrayList<>();

    /**
     * targeted action.
     */
    private String action = "";

    /**
     * application's context.
     */
    private Context context;

    /**
     * whether this receiver is registered or not.
     */
    private boolean status = true;

    /**
     * to initialize this with the passed arguments.
     *
     * @param context of application
     * @param action  to listen for
     */
    private BroadcastReceiver(Context context, String action) {
        this.action = action;
        this.context = context.getApplicationContext();
    }

    /**
     * for system :).
     */
    public BroadcastReceiver() {

    }

    /**
     * get the instance of this that targeting the given action
     * and creates one if not exist.
     *
     * @param context ot application
     * @param action  to listen for
     * @return an instance of this
     */
    public static BroadcastReceiver getInstance(Context context, String action) {
        BroadcastReceiver receiver = Receivers.get(action);

        if (receiver == null) {
            receiver = new BroadcastReceiver(context, action);
            context.registerReceiver(receiver, new IntentFilter(action));
            Receivers.put(action, receiver);
        }

        return receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null)
            if (intent.getAction().equals(this.action))
                for (Function<Arguments, ?> function : this.functions)
                    function.apply(Arguments.parse(context, intent, this, this.action));
    }

    /**
     * add the given functions to be called each time
     * this receiver receives it's target action.
     *
     * @param functions to add.
     */
    @SafeVarargs
    public final void addFunctions(Function<Arguments, ?>... functions) {
        if (this.status)
            this.functions.addAll(Arrays.asList(functions));
        else//redirect
            getInstance(this.context, this.action).addFunctions(functions);
    }

    /**
     * remove the given function.
     *
     * @param functions to remove
     */
    @SafeVarargs
    public final void removeFunctions(Function<Arguments, ?>... functions) {
        this.functions.removeAll(Arrays.asList(functions));

        if (this.functions.size() == 0 && this.status) {
            Receivers.remove(action, this);
            this.context.unregisterReceiver(this);
            this.status = false;
        } else {
            Receivers.get(action).removeFunctions(functions);
        }
    }

}
