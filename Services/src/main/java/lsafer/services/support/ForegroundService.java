package lsafer.services.support;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import lsafer.services.R;
import lsafer.services.util.Arguments;

/**
 * a class to support task-parts from plugins to be able to
 * invoke functions inside a foreground service.
 * <p>
 * all because the plugins couldn't generate
 * a service because it's manifest well be ignored
 * during the runtime
 *
 * @author LSaferSE
 * @version 1 alpha (24-Jul-19)
 * @since 24-Jul-19
 */
final public class ForegroundService extends Service {

    /**
     * map of the functions groups and it's ids.
     */
    final private static Map<String, List<Function<Arguments, ?>>> functions = new HashMap<>();

    /**
     * add the passed functions from the targeted functions group.
     *
     * @param group     the name of the targeted functions group to add the passed functions to
     * @param functions the functions list to add to the targeted functions group
     */
    @SafeVarargs
    public static void addFunctions(String group, Function<Arguments, ?>... functions) {
        List<Function<Arguments, ?>> list = ForegroundService.functions.get(group);

        if (list == null)
            ForegroundService.functions.put(group, list = new ArrayList<>());

        list.addAll(Arrays.asList(functions));
    }

    /**
     * remove the passed functions from the targeted functions group.
     *
     * @param group     the name of the targeted functions group to remove the passed functions from
     * @param functions the functions list to remove from the targeted functions group
     */
    @SafeVarargs
    public static void removeFunctions(String group, Function<Arguments, ?>... functions) {
        List<Function<Arguments, ?>> list = ForegroundService.functions.get(group);

        if (list != null) {
            list.removeAll(Arrays.asList(functions));

            if (list.size() == 0)
                ForegroundService.functions.remove(group);
        }
    }

    /**
     * start the service and run all functions in the passed group.
     *
     * @param context   context of application to start the service with
     * @param group     name of the functions group to be started
     * @param functions to add to the targeted functions group
     */
    @SafeVarargs
    public static void runFunctions(Context context, String group, Function<Arguments, ?>... functions) {
        if (ForegroundService.functions.containsKey(group))
            ForegroundService.functions.get(group).addAll(Arrays.asList(functions));
        else ForegroundService.functions.put(group, new ArrayList<>(Arrays.asList(functions)));

        Intent intent = new Intent(context, ForegroundService.class);
        intent.putExtra("group", group);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(intent);
        else context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String group = intent.getStringExtra("group");
        List<Function<Arguments, ?>> functions = ForegroundService.functions.get(group);

        if (functions != null) {
            for (Function<Arguments, ?> function : functions)
                function.apply(new Arguments(this, intent, group));
            ForegroundService.functions.remove(group);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("background-functions", "Background Functions", NotificationManager.IMPORTANCE_NONE);
            channel.setDescription(this.getString(R.string._functions_running_in_background_channel_description));
            //noinspection ConstantConditions
            this.getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
        this.startForeground(1, new NotificationCompat.Builder(this, "background-functions")
                .setSmallIcon(R.drawable.icon_sync)
                .setContentTitle(this.getString(R.string._functions_running_in_background_title))
                .setContentText(this.getString(R.string._functions_running_in_background_text))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build());

        Toast.makeText(this, this.getString(R.string._functions_running_in_background_toast, group), Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
