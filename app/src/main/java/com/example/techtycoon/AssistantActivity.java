package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;

import com.example.techtycoon.Assistant.Assistant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class AssistantActivity extends AppCompatActivity {
    DeviceViewModel deviceViewModel;
    Company company;
    int id;

    EditText marketingInputField;
    Switch marketingSwitch;
    Switch generalAssistantSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getIntent
        Intent intent=getIntent();
        id=intent.getIntExtra("ID",0);

        deviceViewModel=new DeviceViewModel(getApplication());
        company = deviceViewModel.getCompany_byID(id);

        //findviews
        marketingInputField =findViewById(R.id.marketingInputField);
        marketingSwitch =findViewById(R.id.marketingAssistantSwitch);
        generalAssistantSwitch =findViewById(R.id.generalAssistantSwitch);
        TextView headerOfAssistants=findViewById(R.id.headerOfAssistants);

        //setvalues
        headerOfAssistants.setText(String.format(Locale.getDefault(),"%s's assistants",company.name));
        marketingSwitch.setChecked(company.hasAssistant);
        if(marketingSwitch.isChecked()){
            marketingInputField.setText(String.format(Locale.getDefault(),"%d",company.assistant.marketingGoal));
        }
        generalAssistantSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((Switch) v).isChecked()){
                    ((Switch) v).setText("Enabled");
                }else{((Switch) v).setText("Disabled");}
                marketingSwitch.setChecked(((Switch) v).isChecked());
            }
        });
        generalAssistantSwitch.setChecked(company.hasAssistant);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAssistNeeded= marketingSwitch.isChecked();
                int goal=0;
                if (isAssistNeeded){
                    if(TextUtils.isEmpty(marketingInputField.getText())){
                        Toast.makeText(view.getContext(),"Set a marketing goal",Toast.LENGTH_SHORT).show();
                        return;
                    }else {goal=Integer.parseInt(marketingInputField.getText().toString());}
                }
                company.hasAssistant=isAssistNeeded;
                if(isAssistNeeded){
                    company.assistant=new Assistant(company.companyId,goal);
                }
                deviceViewModel.updateCompanies(company);
                Toast.makeText(view.getContext(),"Saved",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
