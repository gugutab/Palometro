package palometro.gugutab.com.palometro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ArrayList<CountdownInfo> countdowns;
    private PendingIntent pendingIntent;
    private AlarmManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Defining countdowns
        countdowns = new ArrayList<CountdownInfo>();
        countdowns.add(new CountdownInfo("Debug", 25, 5));
        countdowns.add(new CountdownInfo("Treino", 150, 5));
        countdowns.add(new CountdownInfo("Teste", 300, 5));

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MainTabsAdapter(getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

//      TODO: Service code commented cause it's not ready yet
/*      Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        startServiceAlarm();*/
    }

    public void startServiceAlarm() {
        //TODO: bypass android limit to setRepeating wakeups in less than 60 seconds on devices > kitkat
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 10000; // 10 seconds
        System.out.println("startServiceAlarm running");
        Toast.makeText(this, "startServiceAlarm running", Toast.LENGTH_SHORT).show();
        manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, pendingIntent);

    }

    public void cancelServiceAlarm(View view) {
        System.out.println("cancelServiceAlarm running");
        Toast.makeText(this, "cancelServiceAlarm running", Toast.LENGTH_SHORT).show();

        if (manager != null) {
            manager.cancel(pendingIntent);

        }
    }

    public class MainTabsAdapter extends FragmentPagerAdapter {

        public MainTabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return TITLES[position];
            return countdowns.get(position).getName();
        }

        @Override
        public int getCount() {
            //return TITLES.length;
            return countdowns.size();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f1 = TabFragment.newInstance(position);
            Bundle args1 = new Bundle();
            args1.putString("name", countdowns.get(position).getName());
            args1.putInt("time", countdowns.get(position).getTime());
            args1.putInt("divs", countdowns.get(position).getDivs());
            f1.setArguments(args1);
            return f1;
        }
        public CountdownInfo getCountdown(int position){
            return countdowns.get(position);
        }
    }
}
