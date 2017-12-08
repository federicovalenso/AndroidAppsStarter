package ru.mano_ldc.appsstarter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;

public class AppsSchedulerEditor extends AppCompatActivity implements View.OnClickListener {

    static final String ACTION_EDIT = "editApp";
    static final String ACTION_ADD = "addApp";
    static final String EXTRA_DATA_APP_ID = "id";
    private String action;
    private Spinner appChooser;
    private EditText etTime;
    private ApplicationsScheduler scheduler;
    private HashMap<String, String> editableApp;

    View.OnClickListener mTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerDialogFragment timePicker = new TimePickerDialogFragment();
            timePicker.show(getSupportFragmentManager(), "Time picker");
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_scheduler_editor);
        findViewById(R.id.btnSave).setOnClickListener(this);
        InstalledApps installedApps = new InstalledApps(this);
        appChooser = findViewById(R.id.appChooser);
        String[] from = {
                InstalledApps.APP_NAME_KEY,
                InstalledApps.APP_PACKAGE_KEY};
        int[] to = {
                R.id.chooserRowName,
                R.id.chooserRowPackage};
        SimpleAdapter adapter = new SimpleAdapter(this, installedApps.convertToArrayList(), R.layout.app_chooser_row, from, to);
        appChooser.setAdapter(adapter);
        etTime = findViewById(R.id.etTime);
        etTime.setOnClickListener(mTimeClickListener);
        scheduler = new ApplicationsScheduler(getApplicationContext());
        Intent intent = getIntent();
        action = intent.getAction();
        if (action == ACTION_EDIT) {
            editableApp = scheduler.getAppByID(intent.getStringExtra(EXTRA_DATA_APP_ID));
            String editablePackage =  editableApp.get(SQLiteOpener.APP_PACKAGE_COL);
            int appChooserCount =  appChooser.getCount();
            for (int i = 0; i < appChooserCount; i++) {
                HashMap<String, String> curApp = (HashMap<String, String>) appChooser.getItemAtPosition(i);
                String curPackage = curApp.get(InstalledApps.APP_PACKAGE_KEY);
                if (curPackage.equals(editablePackage) ) {
                    appChooser.setSelection(i);
                    etTime.setText(editableApp.get(SQLiteOpener.APP_START_TIME));
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave :
                HashMap<String, String> hm = (HashMap<String, String>) appChooser.getSelectedItem();
                String appName = hm.get(InstalledApps.APP_NAME_KEY);
                String appPackage = hm.get(InstalledApps.APP_PACKAGE_KEY);
                try {
                    switch (action) {
                        case ACTION_ADD :
                            scheduler.addApp(appName, appPackage, etTime.getText().toString());
                            Toast.makeText(this, "Приложение добавлено в расписание", Toast.LENGTH_LONG);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            break;
                        case ACTION_EDIT :
                            scheduler.updateApp(
                                    editableApp.get(SQLiteOpener.APP_ID),
                                    appName,
                                    appPackage,
                                    etTime.getText().toString());
                            Toast.makeText(this, "Данные обновлены", Toast.LENGTH_LONG);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            break;
                    }
                    break;
                }
                catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
                }

        }
    }
}

