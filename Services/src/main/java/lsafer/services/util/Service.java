package lsafer.services.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import lsafer.annotation.Underdevelopment;
import lsafer.services.R;
import lsafer.services.io.Chain;

import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A service that is responsible for launching a {@link Process}.
 *
 * <ul>
 * <li>note: your service must be registered in your manifest as an exported service. And have {@link #CATEGORY_SERVICE}.</li>
 * </ul>
 *
 * @param <P> the type of the linked process
 * @author LSaferSE
 * @version 2 alpha (06-Sep-19)
 * @since 16-Aug-19
 */
@Service.Defaults
public class Service<P extends Process> extends android.app.Service {

    /**
     * Tells the service to just do invoke the target.
     */
    final public static String ACTION_INVOKE = "lsafer.services.intent.action.INVOKE";

    /**
     * Tells the service to return the properties of the process class of It. to the caller on a callBack.
     */
    final public static String ACTION_PROPERTIES = "lsafer.services.intent.action.PROPERTIES";

    /**
     * Tells the service to invoke the target. Then shutdown the service if no processes remaining on it.
     */
    final public static String ACTION_SHUTDOWN = "lsafer.services.intent.action.SHUTDOWN";

    /**
     * A category contains all AUS Services.
     */
    final public static String CATEGORY_SERVICE = "lsafer.services.intent.category.SERVICE";

    /**
     * Allows the granted caller to invoke methods on AUS Services.
     */
    final public static String PERMISSION_INVOKER = "lsafer.services.permission.INVOKER";

    /**
     * All still working processes that have been summoned by this.
     */
    final protected Map<String, P> processes = new HashMap<>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.notifyForeground();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.resolve(intent);
        return Service.START_STICKY;
    }

    /**
     * Invoke a method on a process depending on the extra data in the passed intent.
     *
     * <ul>
     * <li>{@link Process} "process": the targeted process.</li>
     * <li>{@link Chain} "chain": the chain of the targeted process.</li>
     * <li>{@link String} "method": the targeted method to be invoked to the targeted process.</li>
     * <li>{@link Arguments} "arguments": the arguments to be passed to the targeted method in the invocation of it.</li>
     * </ul>
     *
     * @param intent to get data from
     */
    public void invoke(Intent intent) {
        P process = (P) intent.getSerializableExtra("process");
        Chain chain = (Chain) intent.getSerializableExtra("chain");
        String method = intent.getStringExtra("method");
        Arguments arguments = (Arguments) intent.getSerializableExtra("arguments");

        if (process != null && chain != null && method != null && arguments != null)
            this.summon(chain, process).invoke(method, arguments);
    }

    /**
     * Notify foreground. To avoid system killing this service.
     */
    public void notifyForeground() {
        String channelId = this.getClass().getName();
        String channelName = this.getClass().getSimpleName();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN);
            channel.setDescription(this.getString(R.string.txt__description_service_channel));
            //noinspection ConstantConditions
            this.getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        this.startForeground(channelId.hashCode(), new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.icon_sync)
                .setContentTitle(this.getClass().getSimpleName())
                .setContentText(this.getString(R.string.txt__service_notification_text))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build());
    }

    /**
     * Callback the caller and pass to it the properties of the process class of this.
     *
     * <ul>
     * <li>{@link String} "callBackClass": the name of the caller class.</li>
     * <li>{@link String} "callBackPackage": the package of the caller class.</li>
     * <li>{@link String} "request": the request code.</li>
     * </ul>
     *
     * @param intent to get data from
     */
    @Underdevelopment(value = "not working well", state = "debugging")
    public void propertiesCallBack(Intent intent) {
        Defaults annotation = this.getClass().getAnnotation(Defaults.class);
        String callBackClass = intent.getStringExtra("callBackClass");
        String callBackPackage = intent.getStringExtra("callBackPackage");
        String request = intent.getStringExtra("request");

        if (annotation != null && callBackPackage != null && callBackClass != null && request != null)
            try {
                Process process = annotation.process().newInstance();

                process.service_package = this.getPackageName();
                process.service_class = this.getClass().getName();

                Intent callBackIntent = new Intent()
                        .setClassName(callBackPackage, callBackClass)
                        .putExtra("mode", "result")
                        .putExtra("request", request)
                        .putExtra("properties", process.properties(this.getResources(), annotation.R_string()));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    this.startForegroundService(callBackIntent);
                else
                    this.startService(callBackIntent);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
    }

    /**
     * Resolve what the caller meant to do by calling this service. Then do it.
     *
     * @param intent to resolve
     */
    public void resolve(Intent intent) {
        String action = intent.getAction();

        if (action != null)
            switch (action) {
                case Service.ACTION_INVOKE:
                    this.invoke(intent);
                    break;
                case ACTION_SHUTDOWN:
                    this.invoke(intent);
                    this.shutdown(intent);
                    break;
                case Service.ACTION_PROPERTIES:
                    this.propertiesCallBack(intent);
                    break;
            }
    }

    /**
     * Shutdown/Kill a process depending on the extra data in the passed intent.
     *
     * <ul>
     * <li>{@link Process} "process": the targeted process.</li>
     * </ul>
     *
     * @param intent to get data from
     */
    public void shutdown(Intent intent) {
        P process = ((P) intent.getSerializableExtra("process"));

        if (process != null) {
            this.processes.remove(process.key);

            if (this.processes.size() == 0)
                this.stopSelf();
        }
    }

    /**
     * Summon a {@link Process} that matches the given process.
     *
     * @param chain   to summon using
     * @param process to summon from
     * @return a process summoned by this from the given process
     */
    public P summon(Chain chain, P process) {
        return this.processes.computeIfAbsent(process.key, key -> {
            try {
                //noinspection unchecked, ConstantConditions
                return (P) this.getClass()
                        .getAnnotation(Defaults.class)
                        .process()
                        .newInstance()
                        .attach(this, chain, process);
            } catch (Exception e) {
                throw new IllegalStateException("unable to summon { " + process + " }", e);
            }
        });
    }

    /**
     * Set the default values for the targeted service.
     */
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Defaults {
        /**
         * The string resources IDs class.
         *
         * @return R.string
         */
        Class<?> R_string() default R.class;

        /**
         * The {@link Process} that the linked service is responsible for launching.
         *
         * @return the linked process
         */
        Class<? extends Process> process() default Process.class;
    }
}
