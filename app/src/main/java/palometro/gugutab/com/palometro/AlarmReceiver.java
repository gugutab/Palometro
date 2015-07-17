package palometro.gugutab.com.palometro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // Do something
        //TODO
        Toast.makeText(arg0, "onReceive running", Toast.LENGTH_LONG).show();
        System.out.println("onReceive running");
    }

}