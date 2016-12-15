package com.mb.myapplication;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Long oldCurrent = 0L;
    Long current = 0L;

    boolean isPermissionGranted;

    UsageStatsManager mUsageStatsManager;

    SimpleDateFormat dataFormatter;

    TextView mSheduleTasksView, packagesView, eventsView;

    ArrayList<Current> currentData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSheduleTasksView = (TextView) findViewById(R.id.textView);
        packagesView = (TextView) findViewById(R.id.packages);
        eventsView = (TextView) findViewById(R.id.uEvents);
//        oldCurrent = getTotalBatteryCurrent();

        mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        dataFormatter = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");

        TextView permissionsView = (TextView) findViewById(R.id.permissionsView);
        permissionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForPermissions();
            }
        });

        mSheduleTasksView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScheduleBatteryCurrent();
                mScheduleUsageEvents();
            }
        });
        eventsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUsageEvents(10, System.currentTimeMillis() - 1000*60*10);
            }
        });
        packagesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                packagesView.setText(formatUsageStats(10));
            }
        });
    }

    private void askForPermissions() {
        Calendar cal = Calendar.getInstance();
        long end = cal.getTimeInMillis();
        cal.set(Calendar.YEAR, -1);
        TextView permissionsView = (TextView) findViewById(R.id.permissionsView);
        long start = cal.getTimeInMillis();

        if( mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, start, end).isEmpty() ) {
            isPermissionGranted = false;
            permissionsView.setText("PERMISSION DENIED!");
            Log.d("LOGGER", "PYTAM!");
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        } else {
            isPermissionGranted = true;
            permissionsView.setText("PERMISSION GRANTED");
            Log.d("LOGGER", "MASZ POZWOLENIA");
        }
    }

    private void mScheduleUsageEvents() {
        Timer timer = new Timer();
        final TextView tv = (TextView) findViewById(R.id.uEvents);
        final long startTime = System.currentTimeMillis();
//        final android.os.Handler handler = new android.os.Handler();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
//                Log.d("LOGGER", "Starttime: " + startTime);
                final String text = getUsageEvents(1, startTime) ;
                new Thread(){
                    public void run() {
                        tv.post(new Runnable() {
                            public void run() {
                                tv.setText(text);
                            }
                        });
                    }
                }.start();
            }
        };
        timer.schedule(task, 0, 1000);
    }

    private void mScheduleUsageStats() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                final String text = formatUsageStats(1);

            }
        };
    }

    private void mScheduleBatteryCurrent() {
        Timer timer = new Timer();
        final android.os.Handler handler = new android.os.Handler();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                current = getTotalBatteryCurrent();
                if( !current.equals(oldCurrent) ) {
                    oldCurrent = current;
                    Long time = System.currentTimeMillis();
                    final double avg;
                    currentData.add(new Current(oldCurrent, time));
                    if( currentData.size() > 4 ) {
                        ArrayList<Current> c = new ArrayList<>();
                        for( int i = 5; i > 0; i-- ) {
                            c.add(currentData.get(currentData.size() - i));
                        }
                        avg = calculateAverage(c);
                    } else {
                        avg = 0L;
                    }

//                            avg = calculateAverage(currentData.subList(Math.max(currentData.size()-5, 0), currentData.size()-1));
                    String text = "Current(mA):\t" + oldCurrent + "\t, average(mA):\t" + avg + "\t, t(ms):\t" + (time - currentData.get(Math.max(currentData.size()-6,0)).timeInMilis) + "\n";
                    Log.d("LOGGER", text);
                    writeToFile(text, getApplicationContext());

                    new Thread(){
                        public void run() {
                            handler.post(new Runnable() {
                                public void run() {
                                    DecimalFormat df = new DecimalFormat("#.00");
                                    mSheduleTasksView.setText("Current " + df.format(oldCurrent) + " mA, avg: " + df.format(avg) + " mA, count: " + currentData.size());
                                }
                            });
                        }
                    }.start();

                }
            }
        };
        timer.schedule(task, 0, 1000);

    }

    private String getUsageEvents(int minutes, long startTime) {

//        Log.i("START", "START" + (startTime) + "current: " +  System.currentTimeMillis() + "\n");
        long totalBackTime = 1000*60*minutes;

        UsageEvents events = mUsageStatsManager.queryEvents(System.currentTimeMillis() - 1000*60*minutes , System.currentTimeMillis());
        UsageEvents.Event event = new UsageEvents.Event();


        SimpleDateFormat df = new SimpleDateFormat("kk:mm:ss:SSS");

        StringBuilder builder = new StringBuilder();

        String firstPackageName = null;
        Long firstPackageTime = null;

        String lastPackageName = null;
        Long lastPackageTime = null;

        Map<String, Long> foregroundTimeMap = new HashMap<>();

        while( events.hasNextEvent()) {
            events.getNextEvent(event);

            String packageName = event.getPackageName();

            final PackageManager pm = getApplicationContext().getPackageManager();
            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo( packageName, 0);
            } catch (final PackageManager.NameNotFoundException e) {
                ai = null;
            }
            final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

            Long timeStamp = event.getTimeStamp();
            if( timeStamp >=  System.currentTimeMillis() - 1000*60*minutes && (event.getEventType() == 1 || event.getEventType() == 2)) {
                if( event.getEventType() == 1 ) {
                    firstPackageName = packageName;
                    firstPackageTime = event.getTimeStamp();

                    Long timeDifference = System.currentTimeMillis() - event.getTimeStamp();
                    if( !events.hasNextEvent() ) {
//                        Log.d("LOGGER", "nie ma eventa!");
                        if( foregroundTimeMap.containsKey(applicationName) ) {
                            foregroundTimeMap.put(applicationName, foregroundTimeMap.get(applicationName) + timeDifference);
                        } else {
                            foregroundTimeMap.put(applicationName, timeDifference);
                        }
                    }
                } else if( event.getEventType() == 2 && packageName.equals(firstPackageName)) {
                    Long timeDifference = event.getTimeStamp() - firstPackageTime;

                    if( foregroundTimeMap.containsKey(applicationName) ) {
                        foregroundTimeMap.put(applicationName, foregroundTimeMap.get(applicationName) + timeDifference);
                    } else {
                        foregroundTimeMap.put(applicationName, timeDifference);
                    }
                }

                String text = "PACZKA! " + applicationName + " \t- " + df.format(new Date(timeStamp)) + ", \t event: " + event.getEventType() + "\n";
                builder.append(text);
//                Log.d("LOGGER", text);
            }
        }

//        Log.d("EVENT_SIZE", "Size: " + foregroundTimeMap.size());

        return mapToString(foregroundTimeMap);
    }

    private String mapToString(Map<String, Long> map) {
        StringBuilder builder = new StringBuilder();
        long size = 0;
        for ( String key : map.keySet() ) {
            builder.append( key + " : " + map.get(key) + "\n");
            size += map.get(key);
        }
        builder.append("size: " + ((double) size /(1000*60)) + "\n");
        return builder.toString();
    }

    private String formatUsageStats(int seconds) {

        List<UsageStats> stats = getUsageStatistics(UsageStatsManager.INTERVAL_BEST);

        StringBuilder builder = new StringBuilder();
        builder.append("\r\n");
        int c = 0;
        for ( UsageStats _stats : stats ) {
            if (_stats.getLastTimeUsed() > (System.currentTimeMillis() - (1000*seconds)) ) {
                String dateString = dataFormatter.format(new Date(_stats.getLastTimeUsed()));
                builder.append("app: ");
                builder.append(_stats.getPackageName());
                builder.append("\n\t\ttime: ");
                builder.append(dateString);
                builder.append("\t - total in fg: \t");
                builder.append(_stats.getTotalTimeInForeground()/1000);
                builder.append(" s\r\n");

                Log.i("LOGGER", "TIME NOW: " + System.currentTimeMillis() + ", LAST STAMP: " + _stats.getLastTimeStamp() + ", LAST USED: " + _stats.getLastTimeUsed() + ", PACKAGE: " + _stats.getPackageName() );

                c++;
            }
        }
//        ((TextView) findViewById(R.id.packages)).setText(builder.toString());

        Log.d("LOGGER", "size: " + c);
        return builder.toString();
    }

    private void writeToFile(String data,Context context) {
        File path = getExternalFilesDir(null);
        File file = new File(path, "log_current.txt");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file, true);
//            Log.d("PATH", file.getAbsolutePath());
            try {
                fos.write(data.getBytes());
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
            finally {
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Long getTotalBatteryCurrent() {
        Long current = CurrentReader.getValue();
//        System.out.println(current);
        return current;
    }

    private double calculateAverage(ArrayList<Current> marks) {
//        Log.d("LOGGER", marks.size()+"");
        Long sum = 0L;
        if(!marks.isEmpty()) {
            for (Current mark : marks) {
                sum += mark.current;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    public List<UsageStats> getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        List<UsageStats> queryUsageStats = mUsageStatsManager
                .queryUsageStats(intervalType, System.currentTimeMillis() - 60 * 1000,
                        System.currentTimeMillis());

        return queryUsageStats;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LOGGER", "PAUSE");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LOGGER", "RESUME");
        askForPermissions();
    }
}

class Current {
    Long current;
    Long timeInMilis;

    public Current(Long c, Long t) {
        current = c;
        timeInMilis = t;
    }
}
