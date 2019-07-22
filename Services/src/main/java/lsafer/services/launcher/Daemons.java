package lsafer.services.launcher;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import lsafer.services.R;
import lsafer.services.Services;
import lsafer.services.io.TaskPart;
import lsafer.services.io.Tasks;

/**
 * a host for providers whom need to be within a service
 * to provider correctly.
 *
 * @author LSaferSE
 * @version 3
 * @since 14-Jun-19
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
final public class Daemons extends Service {

    /**
     * started tasks.
     */
    public Tasks tasks;

    /**
     * directory where targeted tasks have stored.
     */
    private String directory;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Services.initialize(this);

        directory = intent.getStringExtra("directory");
        tasks = Tasks.load(Tasks.class, Services.Storage.child(directory));
        tasks.run(TaskPart.$START, this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("daemons", getString(R.string.Daemons_Host), NotificationManager.IMPORTANCE_NONE);
            channel.setDescription(getString(R.string._textDaemonsHostNotificationChannelDescription));
            //noinspection ConstantConditions
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
        startForeground(1, new NotificationCompat.Builder(this, "daemons")
                .setSmallIcon(R.drawable.icon_sync)
                .setContentTitle(getString(R.string._textDaemonsRunningInBackgroundTitle, directory))
                .setContentText(getString(R.string._textDaemonsRunningInBackgroundText, tasks.map().size()))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build());
        Toast.makeText(this, getString(R.string._textDaemonsStarted,
                tasks.map().size()), Toast.LENGTH_LONG).show();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            v.vibrate(VibrationEffect.createOneShot(10, 1));
        else v.vibrate(10);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        tasks.run(TaskPart.$STOP, this);

        //notify user
        Toast.makeText(this, getString(R.string._textDaemonsKilled,
                tasks.map().size()), Toast.LENGTH_LONG).show();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            v.vibrate(VibrationEffect.createOneShot(10, 1));
        else v.vibrate(10);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        tasks.run(TaskPart.$UPDATE, this, newConfig);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

}
