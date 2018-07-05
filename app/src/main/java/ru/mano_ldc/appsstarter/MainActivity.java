package ru.mano_ldc.appsstarter;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private  ApplicationsScheduler appsScheduler;
    private ListView lwApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnAddApp).setOnClickListener(this);
        appsScheduler = new ApplicationsScheduler(this);

        ArrayList<HashMap<String, String>> apps = appsScheduler.getApps();
        lwApps = (ListView) findViewById(R.id.appsList);
        String[] from = appsScheduler.getColumnsForAdapter();
        @IdRes int to[] = {
                R.id.appRowID,
                R.id.appRowType,
                R.id.appRowName,
                R.id.appRowPackage,
                R.id.appRowFile,
                R.id.appRowTime
        };
        SimpleAdapter adapter = new SimpleAdapter(this, apps, R.layout.scheduled_elem_row, from, to);
        lwApps.setAdapter(adapter);
        lwApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout clickedRow = (LinearLayout) view;
                TextView appRowID = (TextView) clickedRow.findViewById(R.id.appRowID);
                String appID = appRowID.getText().toString();
                if (appID.isEmpty() == false) {
                    Intent intent = new Intent(getApplicationContext(), AppsSchedulerEditor.class);
                    intent.setAction(AppsSchedulerEditor.ACTION_EDIT);
                    intent.putExtra(AppsSchedulerEditor.EXTRA_DATA_APP_ID, appID);
                    startActivity(intent);
                }
            }
        });
        ComponentName name = startService(new Intent(this, AppsStarterService.class));
        Log.d("MainAct", name.toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddApp : {
                Intent intent = new Intent(getApplicationContext(), AppsSchedulerEditor.class);
                intent.setAction(AppsSchedulerEditor.ACTION_ADD);
                startActivity(intent);
                break;
            }
        }
    }

}