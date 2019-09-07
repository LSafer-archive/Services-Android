package lsafer.services.util;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import lsafer.services.R;
import lsafer.services.annotation.Controller;
import lsafer.services.annotation.Entry;
import lsafer.services.annotation.Invokable;
import lsafer.services.io.Chain;
import lsafer.util.Arrays;
import lsafer.util.HashStructure;

import java.lang.reflect.Method;

/**
 * A {@link HashStructure hash-structure} that can be invoked. Or called to be invoked across applications using {@link Service}.
 *
 * @param <S> the type of the service linked to this
 * @author LSaferSE
 * @version 3 alpha (07-Sep-2019)
 * @since 14-Jul-19
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
@Controller
public class Process<S extends Service> extends HashStructure {
    /**
     * The chain that this process is attached to.
     */
    public transient Chain chain;

    /**
     * The index of this.
     */
    @Destructed
    public Integer index;

    /**
     * The key of this process to be recognized from it's {@link Service service}.
     */
    @Destructed
    public String key;

    /**
     * The service that this process is attached to.
     */
    public transient S service;

    /**
     * The class name to dedicate what the service that launches this process.
     *
     * @see R.string#_Process__service_class__description
     */
    @Entry
    public String service_class;

    /**
     * The package/application name that contains the service that launches this process.
     *
     * @see R.string#_Process__service_package__description
     */
    @Entry
    public String service_package;

    /**
     * Attach this process to the given {@link Chain}. And define this process's {@link #index} with the passed one.
     *
     * @param chain to be attached to
     * @param index to set this process's index to
     * @param <P>   this
     * @return this
     */
    public <P extends Process<S>> P attach(Chain chain, int index) {
        this.chain = chain;
        this.index = index;

        this.key = chain.remote().toString() + index;

        return (P) this;
    }

    /**
     * Attach this process to the given {@link Service}. And clone the given process. And also attach this to the given chain.
     *
     * @param service to be attached to
     * @param chain   to be attached to
     * @param parent  to be cloned
     * @param <P>     this
     * @return this
     */
    public <P extends Process<S>> P attach(S service, Chain chain, Process parent) {
        this.index = parent.index;
        this.key = parent.key;

        this.service = service;
        this.chain = chain;

        this.service_package = service.getPackageName();
        this.service_class = service.getClass().getName();

        this.chain.processes.put(this.index, this);

        return this.putAll(parent);
    }

    /**
     * Start the service that suppose to launch this process (that matches the {@link #service_package} and {@link #service_class} of this).
     *
     * @param context   to start the service
     * @param action    what the service should do
     *                  {@link Service#ACTION_INVOKE invoke}
     *                  {@link Service#ACTION_SHUTDOWN shutdown}
     * @param method    what the process should invoke
     *                  {@link Invokable#start start}
     *                  {@link Invokable#stop stop}
     *                  {@link Invokable#update update}
     *                  {@link Invokable#get get}
     * @param arguments to pass to the targeted method
     */
    public void call(Context context, String action, String method, Arguments arguments) {
        Intent intent = new Intent(action);

        intent.putExtra("process", this);
        intent.putExtra("chain", this.chain);
        intent.putExtra("method", method);
        intent.putExtra("arguments", arguments);

        intent.setClassName(this.service_package, this.service_class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(intent);
        else
            context.startService(intent);
    }

    /**
     * Invoke method {@link #call(Context, String, String, Arguments)} on the next process of this.
     *
     * @param action    for the next process's service
     * @param method    to invoke
     * @param arguments to be passed; see {@link Arguments#Arguments(Object...)}
     */
    public void callNext(String action, String method, Object... arguments) {
        this.chain.call(this.service, this.index + 1, action, method, new Arguments(arguments));
    }

    /**
     * Invoke method {@link #call(Context, String, String, Arguments)} on previous process of this.
     *
     * @param action    for the next process's service
     * @param method    to invoke
     * @param arguments to be passed; see {@link Arguments#Arguments(Object...)}
     */
    public void callPrevious(String action, String method, Object... arguments) {
        this.chain.call(this.service, this.index - 1, action, method, new Arguments(arguments));
    }

    /**
     * Invoke a method contained in this with a matching name as presented.
     * The method should be annotated as an {@link Invokable}.
     *
     * @param method    to be invoked
     * @param arguments to be passed to the method
     */
    public void invoke(String method, Arguments arguments) {
        String TAG = this.get(String.class, "TAG", () -> this.getClass().getName());

        for (Method method1 : this.getClass().getMethods()) {
            Invokable annotation = method1.getAnnotation(Invokable.class);

            if (annotation != null && (method1.getName().equals(method) || Arrays.any(annotation.redirect(), method)))
                try {

                    Class<Object>[] types = (Class<Object>[]) method1.getParameterTypes();
                    String[] keys = annotation.value();
                    Object[] defaults = annotation.defaults();
                    Object[] values = new Object[types.length];

                    for (int[] i = {0}; i[0] < types.length; i[0]++)
                        values[i[0]] = arguments.get(types[i[0]],
                                keys.length > i[0] && !keys[i[0]].equals("") ? keys[i[0]] : null,
                                () -> defaults.length > i[0] && types.length > i[0]
                                      ? this.cast(types[i[0]], defaults[i[0]]) : null);

                    method1.invoke(this, values);
                    return;
                } catch (Exception e) {
                    Log.e(TAG, "invoke: unable to invoke method [" + method + "] with arguments " + arguments, e);
                    return;
                }
        }

        Log.e(TAG, "invoke: can't find method [" + method + "]");
    }

    /**
     * Get a {@link Properties} of this.
     *
     * @param resources to get strings from
     * @return a properties of this
     */
    public Properties properties(Resources resources) {
        return new Properties(resources, this);
    }
}
