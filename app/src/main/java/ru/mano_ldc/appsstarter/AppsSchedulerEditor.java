package ru.mano_ldc.appsstarter;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.HashMap;

public class AppsSchedulerEditor extends AppCompatActivity implements View.OnClickListener {

    static final String ACTION_EDIT = "editApp";
    static final String ACTION_ADD = "addApp";
    static final String EXTRA_DATA_APP_ID = "id";
    private String action;
    private Spinner appChooser;
    private Spinner typeChooser;
    private EditText etTime;
    private EditText etFile;
    private LinearLayout llAppRow;
    private LinearLayout llFileRow;
    private ApplicationsScheduler scheduler;
    private HashMap<String, String> editableApp;

    View.OnClickListener mFileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(AppsSchedulerEditor.this, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.ARG_CLOSEABLE, true);
            intent.putExtra(FilePickerActivity.ARG_START_PATH, "/");
            startActivityForResult(intent, 1);
        }
    };

    View.OnClickListener mTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerDialogFragment timePicker = new TimePickerDialogFragment();
            timePicker.show(getSupportFragmentManager(), "Time picker");
        }
    };

    AdapterView.OnItemSelectedListener mTypeChooserListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (view instanceof TextView) {
                String selection = ((TextView) view).getText().toString();
                switch (selection) {
                    case ApplicationsScheduler.TYPE_APP :
                        llAppRow.setVisibility(View.VISIBLE);
                        llFileRow.setVisibility(View.GONE);
                        break;
                    case ApplicationsScheduler.TYPE_FILE :
                        llAppRow.setVisibility(View.GONE);
                        llFileRow.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_scheduler_editor);
        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.btnDelete).setOnClickListener(this);
        llAppRow = findViewById(R.id.llAppRow);
        llFileRow = findViewById(R.id.llFileRow);
        ArrayAdapter<String> typeChooserAdapter = new ArrayAdapter<String>(
                this,
                R.layout.type_chooser_row,
                new String[] {
                        ApplicationsScheduler.TYPE_FILE,
                        ApplicationsScheduler.TYPE_APP}
                        );
        typeChooser = findViewById(R.id.typeChooser);
        typeChooser.setAdapter(typeChooserAdapter);
        typeChooser.setOnItemSelectedListener(mTypeChooserListener);
        InstalledApps installedApps = new InstalledApps(this);
        String[] from = {
                InstalledApps.APP_NAME_KEY,
                InstalledApps.APP_PACKAGE_KEY};
        int[] to = {
                R.id.chooserRowName,
                R.id.chooserRowPackage};
        SimpleAdapter appChooserAdapter = new SimpleAdapter(
                this,
                installedApps.convertToArrayList(),
                R.layout.app_chooser_row,
                from,
                to);
        appChooser = findViewById(R.id.appChooser);
        appChooser.setAdapter(appChooserAdapter);
        etFile = findViewById(R.id.etFile);
        etFile.setOnClickListener(mFileClickListener);
        etTime = findViewById(R.id.etTime);
        //etTime.setOnClickListener(mTimeClickListener);
        scheduler = new ApplicationsScheduler(getApplicationContext());
        Intent intent = getIntent();
        action = intent.getAction();
        if (action == ACTION_EDIT) {
            editableApp = scheduler.getAppByID(intent.getStringExtra(EXTRA_DATA_APP_ID));
            String type = editableApp.get(ApplicationsScheduler.TYPE);
            setType(type);
            switch (type) {
                case ApplicationsScheduler.TYPE_APP :
                    fillAppData();
                    break;
                case ApplicationsScheduler.TYPE_FILE :
                    fillFileData();
                    break;
            }

        }
    }

    private void setType(String inType) {
        int typeChooserCount = typeChooser.getCount();
        for (int i = 0; i < typeChooserCount; i++) {
            String curType =(String) typeChooser.getItemAtPosition(i);
            if (curType.equals(inType)) {
                typeChooser.setSelection(i);
            }
        }
    }

     private void fillAppData() {
        String editablePackage =  editableApp.get(ApplicationsScheduler.APP_PACKAGE);
        int appChooserCount =  appChooser.getCount();
        for (int i = 0; i < appChooserCount; i++) {
            HashMap<String, String> curApp = (HashMap<String, String>) appChooser.getItemAtPosition(i);
            String curPackage = curApp.get(InstalledApps.APP_PACKAGE_KEY);
            if (curPackage.equals(editablePackage) ) {
                appChooser.setSelection(i);
                setTime();
                break;
            }
        }
    }

    private void fillFileData() {
        etFile.setText(editableApp.get(ApplicationsScheduler.FILE_NAME));
        setTime();
    }

    private void setTime() {
        etTime.setText(ApplicationsScheduler.millisToTimeString(editableApp.get(ApplicationsScheduler.APP_START_TIME)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave :
                HashMap<String, String> hm = (HashMap<String, String>) appChooser.getSelectedItem();
                String appName = hm.get(InstalledApps.APP_NAME_KEY);
                String appPackage = hm.get(InstalledApps.APP_PACKAGE_KEY);
                String type = typeChooser.getSelectedItem().toString();
                String fileName = etFile.getText().toString();
                String time = etTime.getText().toString();
                try {
                    switch (action) {
                        case ACTION_ADD :
                            switch (type) {
                                case ApplicationsScheduler.TYPE_APP :
                                    scheduler.addApp(appName, appPackage, time);
                                    Toast.makeText(this, "Приложение добавлено в расписание", Toast.LENGTH_LONG).show();
                                    break;
                                case ApplicationsScheduler.TYPE_FILE :
                                    scheduler.addFile(fileName, time);
                                    Toast.makeText(this, "Файл добавлен в расписание", Toast.LENGTH_LONG).show();
                                    break;
                            }
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            break;
                        case ACTION_EDIT :
                            String id = editableApp.get(ApplicationsScheduler.APP_ID);
                            switch (type) {
                                case ApplicationsScheduler.TYPE_APP :
                                    scheduler.updateApp(
                                            id,
                                            appName,
                                            appPackage,
                                            time);
                                    break;
                                case ApplicationsScheduler.TYPE_FILE :
                                    scheduler.updateFile(
                                            id,
                                            fileName,
                                            time);
                                    break;
                            }
                            Toast.makeText(this, "Данные обновлены", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            break;
                    }
                    break;
                }
                catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            case R.id.btnDelete :
                String id = editableApp.get(ApplicationsScheduler.APP_ID);
                try {
                    scheduler.deleteByID(id);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                catch (SQLException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            if (filePath != null) {
                etFile.setText(filePath);
            }
        }
    }

}


