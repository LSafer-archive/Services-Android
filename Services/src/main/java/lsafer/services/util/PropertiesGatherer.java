package lsafer.services.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import lsafer.annotation.Underdevelopment;
import lsafer.services.R;
import lsafer.util.IDFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A service to gather {@link Properties} from AUS Services installed in the device.
 *
 * @author LSaferSE
 * @version 2 alpha (06-Sep-19)
 * @since 27-Aug-19
 */
@Underdevelopment(value = "not working well", state = "debugging")
final public class PropertiesGatherer extends Service {
    /**
     * Results listeners mapped by request-code.
     */
    final private static Map<String, Consumer<Properties>> listeners = new HashMap<>();

    /**
     * A factory to get new unused request codes.
     */
    final private static IDFactory<Integer, Integer> requests = new IDFactory<>(0, (free, flavor) -> free + 1 + flavor);

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

        if (PropertiesGatherer.listeners.size() == 0)
            this.stopSelf();

        return START_NOT_STICKY;
    }

    /**
     * Gather the properties from the services matches the given filter.
     *
     * @param context  used to start the service
     * @param filter   to limit results
     * @param listener to invoke after the result callback
     */
    public static void gather(Context context, Intent filter, Consumer<Properties> listener) {
        String request = PropertiesGatherer.requests.newId(0).toString();
        PropertiesGatherer.listeners.put(request, listener);

        Intent intent = new Intent()
                .setClass(context, PropertiesGatherer.class)
                .putExtra("request", request)
                .putExtra("mode", "request")
                .putExtra("filter", filter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(intent);
        else
            context.startService(intent);
    }

    /**
     * Notify foreground. To avoid system killing this service.
     */
    public void notifyForeground() {
        String channelId = this.getClass().getSimpleName();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, this.getString(R.string.abs__Properties_Gatherer), NotificationManager.IMPORTANCE_MIN);
            channel.setDescription(this.getString(R.string.txt__description_service_channel));
            //noinspection ConstantConditions
            this.getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        this.startForeground(1, new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.icon_sync)
                .setContentTitle(this.getClass().getSimpleName())
                .setContentText(this.getString(R.string.txt__service_notification_text))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build());
    }

    /**
     * Request properties from all AUS Services. That matches the passed filter intent.
     *
     * <ul>
     * <li>{@link String} "request": the request code to be able to track results.</li>
     * <li>{@link Intent} "filter": to filter services that will be requested.</li>
     * </ul>
     *
     * @param intent to get data from
     */
    public void request(Intent intent) {
        String request = intent.getStringExtra("request");
        Intent filter = intent.getParcelableExtra("filter");

        if (request != null && filter != null) {
            intent.setAction(lsafer.services.util.Service.ACTION_INVOKE);
            intent.addCategory(lsafer.services.util.Service.CATEGORY_SERVICE);

            for (ResolveInfo info : this.getPackageManager().queryIntentServices(intent, 0)) {
                Intent intent1 = new Intent()
                        .setClassName(info.serviceInfo.packageName, info.serviceInfo.name)
                        .putExtra("callBackClass", this.getClass().getName())
                        .putExtra("callBackPackage", this.getPackageName())
                        .putExtra("request", request);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    this.startForegroundService(intent1);
                else
                    this.startService(intent1);
            }
        }
    }

    /**
     * Resolve what the caller meant to do by calling this service. Then do it.
     *
     * @param intent to get data from
     */
    private void resolve(Intent intent) {
        String mode = intent.getStringExtra("mode");

        if (mode != null)
            switch (mode) {
                case "request":
                    this.request(intent);
                    break;
                case "result":
                    this.result(intent);
                    break;
            }
    }

    /**
     * Get the results from the caller. Then call the listener with the passed request-code.
     *
     * <ul>
     * <li>{@link String} "request": request code to track what listener to trigger.</li>
     * <li>{@link Properties} "properties": the results that have been requested</li>
     * </ul>
     *
     * @param intent to get data from
     */
    public void result(Intent intent) {
        String request = intent.getStringExtra("request");
        Properties properties = (Properties) intent.getSerializableExtra("properties");

        Consumer<Properties> consumer = PropertiesGatherer.listeners.get(request);

        if (consumer != null && properties != null)
            consumer.accept(properties);
    }
}
