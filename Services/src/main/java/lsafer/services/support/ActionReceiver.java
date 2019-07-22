package lsafer.services.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.HashMap;

import lsafer.services.util.Arguments;

/**
 * to listen to targeted action.
 *
 * @author LSaefr
 * @version 2
 * @since 29 Jun 2019
 */
final public class ActionReceiver extends BroadcastReceiver {

    /**
     * all instances created by this.
     */
    private static HashMap<String, ActionReceiver> Receivers = new HashMap<>();

    /**
     * targeted action.
     */
    private String fAction = "";

    /**
     * application's context.
     */
    private Context fContext;

    /**
     * listeners linked to this.
     */
    private ArrayList<OnActionListener> mListeners = new ArrayList<>();

    /**
     * to initialize this with the passed arguments.
     *
     * @param context of application
     * @param action  to listen for
     */
    private ActionReceiver(Context context, String action) {
        fAction = action;
        fContext = context.getApplicationContext();

    }

    /**
     * for system :).
     */
    public ActionReceiver() {

    }

    /**
     * get the instance of this that targeting the given action
     * and creates one if not exist.
     *
     * @param context ot application
     * @param action  to listen for
     * @return an instance of this
     */
    public static ActionReceiver getInstance(Context context, String action) {
        boolean unregistered = Receivers.putIfAbsent(action, new ActionReceiver(context, action)) == null;
        ActionReceiver receiver = Receivers.get(action);

        if (unregistered)
            context.registerReceiver(receiver, new IntentFilter(action));

        return receiver;
    }

    /**
     * unregister the given listener to be called each time
     * this receiver receives it's target action.
     *
     * @param listener to register.
     */
    public void link(OnActionListener listener) {
        mListeners.add(listener);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null)
            if (intent.getAction().equals(fAction))
                for (OnActionListener listener : mListeners)
                    listener.onListen(Arguments.newInstance(context, intent));
    }

    /**
     * unregister the given listener.
     *
     * @param listener to unregister
     */
    public void unlink(OnActionListener listener) {
        mListeners.remove(listener);

        if (mListeners.size() < 1) {
            Receivers.remove(fAction, this);

            try {
                fContext.unregisterReceiver(this);
            } catch (Exception e) {
                //to avoid multiple unregistering.
            }
        }
    }

    /**
     * delegate to run on targeted action happened
     * and get information about the action from.
     */
    public interface OnActionListener {
        /**
         * get called when the targeted action happen.
         *
         * @param arguments context, intent, .
         */
        void onListen(Arguments arguments);
    }

}
