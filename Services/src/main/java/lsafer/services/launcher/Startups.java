package lsafer.services.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import lsafer.services.R;
import lsafer.services.Services;
import lsafer.services.io.TaskPart;
import lsafer.services.io.Tasks;

/**
 * start all providers that stored at ~/Utilities/Startups .
 *
 * @author LSaferSE
 * @version 1
 * @since 15-Jun-19
 */
@SuppressWarnings("unused")
final public class Startups extends BroadcastReceiver {

    /**
     * startups tasks name.
     */
    final public static String directory = "Startups";

    /**
     * started tasks.
     */
    public Tasks tasks;

    @Override
    public void onReceive(Context context, Intent intent) {
        Services.initialize(context);

        tasks = Tasks.load(Tasks.class, Services.Storage.child(directory));
        tasks.run(TaskPart.$START, context, intent);

        //notify user
        Toast.makeText(context, context.getString(R.string
                ._textBootsStarted, tasks.map().size()), Toast.LENGTH_LONG).show();
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            v.vibrate(VibrationEffect.createOneShot(10, 1));
        else v.vibrate(10);
    }

}
