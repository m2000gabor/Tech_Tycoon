package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;

import com.example.techtycoon.Assistant.AssistantManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Pair;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class AssistantActivity extends AppCompatActivity {
    DeviceViewModel deviceViewModel;
    Company myCompany;
    int id;
    int currentAssistantType;
    private int originalAssistantType;
    List<Pair<TextView,EditText>> inputFields;

    Switch headerAssistantSwitch;
    Spinner assistantSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getIntent
        Intent intent = getIntent();
        id = intent.getIntExtra("ID", 0);

        deviceViewModel = new DeviceViewModel(getApplication());
        myCompany = deviceViewModel.getCompany_byID(id);
        originalAssistantType=myCompany.assistantType;
        currentAssistantType = originalAssistantType;

        //findViews
        TextView headerOfAssistants = findViewById(R.id.headerOfAssistants);
        headerOfAssistants.setText(String.format(Locale.getDefault(), "%s's assistants", myCompany.name));

        headerAssistantSwitch = findViewById(R.id.headerAssistantSwitch);
        headerAssistantSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Switch) v).isChecked()) {
                    ((Switch) v).setText("Enabled");
                } else {
                    ((Switch) v).setText("Disabled");
                }
            }
        });
        headerAssistantSwitch.setChecked(myCompany.assistantType != -1);

        //input fields
        LinearLayout parentLayout=findViewById(R.id.assistantActivityRootLinearLayout);
        inputFields = new LinkedList<>();
        int i=0;
        while(i<15){
            TextView label=new TextView(AssistantActivity.this);
            label.setText("Label:");

            EditText e=new EditText(AssistantActivity.this);
            e.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

            LinearLayout row=new LinearLayout(AssistantActivity.this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            row.addView(label);
            row.addView(e);

            parentLayout.addView(row);
            inputFields.add(new Pair<>(label,e));
            i++;
        }

        //setup the spinner
        assistantSpinner = findViewById(R.id.chooseAssistantSpinner);
        new AssistantAdapter();
        assistantSpinner.setSelection(currentAssistantType +1);
        setAssistantInput(statusToInputFields(myCompany.assistantStatus));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean isAssistNeeded = headerAssistantSwitch.isChecked();
                    if (isAssistNeeded) {
                        List<String> inputs = getAssistantInput();
                        myCompany.assistantStatus = inputFieldsToStatus(inputs);
                        myCompany.assistantType = currentAssistantType;
                    } else {
                        myCompany.assistantType = -1;
                    }
                    deviceViewModel.updateCompanies(myCompany);
                    Toast.makeText(view.getContext(), "Saved", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (IllegalArgumentException e) {
                    Toast.makeText(view.getContext(), "All argument is required", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //go up button
    @Override
    public boolean navigateUpTo(Intent upIntent) {
        upIntent.putExtra("ID", id);
        return super.navigateUpTo(upIntent);
    }

    private class AssistantAdapter implements AdapterView.OnItemSelectedListener {

        AssistantAdapter() {
            //Getting the instance of Spinner and applying OnItemSelectedListener on it
            assistantSpinner.setOnItemSelectedListener(this);

            List<String> names = new LinkedList<>();
            names.add("Choose an assistant");
            names.addAll(AssistantManager.getAssistantNames());

            //Creating the ArrayAdapter instance having the nameOfCompanies list
            ArrayAdapter aa = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item,
                    names.toArray(new String[0]));
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            assistantSpinner.setAdapter(aa);
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            currentAssistantType = position - 1;
            if(currentAssistantType==originalAssistantType){
                setAssistantInput(statusToInputFields(myCompany.assistantStatus));
            }else{setAssistantInput(null);}
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void setAssistantInput(List<String> inputs) {
        if (currentAssistantType != -1) {
            headerAssistantSwitch.setChecked(true);
            List<String> labels = AssistantManager.getInputLabels(currentAssistantType);
            List<String> defaultValues = statusToInputFields(AssistantManager.getDefaultStatus(currentAssistantType));
            int i = 0;
            while (labels != null && i < labels.size()) {
                if(inputs==null || originalAssistantType!=currentAssistantType) {
                    inputFields.get(i).first.setText(String.format("%s:", labels.get(i)));
                    //inputFields.get(i).second.setText("");
                    inputFields.get(i).second.setText(defaultValues.get(i));
                }else{
                    inputFields.get(i).first.setText(String.format("%s:", labels.get(i)));
                    inputFields.get(i).second.setText(inputs.get(i));
                }
                inputFields.get(i).first.setVisibility(View.VISIBLE);
                inputFields.get(i).second.setVisibility(View.VISIBLE);
                i++;
            }
            while (i < inputFields.size()) {
                inputFields.get(i).first.setVisibility(View.GONE);
                inputFields.get(i).second.setVisibility(View.GONE);
                i++;
            }
        } else {
            headerAssistantSwitch.setChecked(false);
            int i = 0;
            while (i < inputFields.size()) {
                inputFields.get(i).first.setVisibility(View.GONE);
                inputFields.get(i).second.setVisibility(View.GONE);
                i++;
            }
        }
    }

    private List<String> getAssistantInput() {
        List<String> labels = AssistantManager.getInputLabels(currentAssistantType);
        List<String> inputs = new LinkedList<>();
        for (int i = 0; labels != null && i < labels.size(); i++) {
            String in = inputFields.get(i).second.getText().toString();
            if (in.equals("")) {
                throw new IllegalArgumentException();
            }
            inputs.add(in);
        }
        return inputs;
    }

    private String inputFieldsToStatus(List<String> inputs) {
        if(inputs==null || inputs.isEmpty()){return "";}
        StringBuilder stringBuilder=new StringBuilder();
        for (String s : inputs) {
            stringBuilder.append(s).append(";");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }

    @Nullable
    List<String> statusToInputFields(String status) {
        if(status==null||status.equals("")){return null;}
        return Arrays.asList(status.split(";"));
    }

}
