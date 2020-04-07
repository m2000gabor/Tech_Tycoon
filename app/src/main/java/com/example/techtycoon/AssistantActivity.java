package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;

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
    //todo Choose assistant from a list and customize below
    DeviceViewModel deviceViewModel;
    Company company;
    int id;

    //only add the switch to this array
    //assistantType will be the switch's position in this array+1
    int[] ResID_switch={R.id.marketingGoalAssistantSwitch,
            R.id.marketingAvgAssistantSwitch,
            R.id.general1AssistantSwitch,
            R.id.general2AssistantSwitch,
            R.id.appleBot2Switch,
            R.id.fullAssistant1Switch,
            R.id.fullAssistant2Switch
    };
    EditText marketingInputField;
    Switch headerAssistantSwitch;

    Switch[] switches=new Switch[ResID_switch.length];

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
        marketingInputField =findViewById(R.id.marketingGoalInputField);
        headerAssistantSwitch =findViewById(R.id.headerAssistantSwitch);
        TextView headerOfAssistants=findViewById(R.id.headerOfAssistants);

        for(int i=0;i<ResID_switch.length;++i){
            switches[i]=findViewById(ResID_switch[i]);
        }

        //setvalues
        headerOfAssistants.setText(String.format(Locale.getDefault(),"%s's assistants",company.name));
        if(company.assistantType!=-1){
            switches[company.assistantType-1].setChecked(true);
            switch (company.assistantType){
                case 1:
                    marketingInputField.setText(String.format(Locale.getDefault(),"%s",company.assistantStatus));
                    break;
                default:break;
            }
        };

        headerAssistantSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((Switch) v).isChecked()){
                    ((Switch) v).setText("Enabled");
                }else{
                    ((Switch) v).setText("Disabled");
                    for (Switch s : switches) { s.setChecked(false);}
                }
            }
        });
        headerAssistantSwitch.setChecked(company.assistantType!=-1);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAssistNeeded= headerAssistantSwitch.isChecked();
                int goal=0;
                int type=0;
                if(isAssistNeeded) {
                    if (switches[0].isChecked()) {
                        if (TextUtils.isEmpty(marketingInputField.getText())) {
                            Toast.makeText(view.getContext(), "Set a marketing goal", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            goal = Integer.parseInt(marketingInputField.getText().toString());
                        }
                    }
                    while (type < ResID_switch.length && !switches[type].isChecked()) {
                        type++;
                    }

                    type++;
                }
                if(isAssistNeeded){
                    company.assistantType=type;
                    company.assistantStatus=String.valueOf(goal);
                }else {company.assistantType=-1;}
                deviceViewModel.updateCompanies(company);
                Toast.makeText(view.getContext(),"Saved",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean navigateUpTo (Intent upIntent){
        upIntent.putExtra("ID", id);
        return super.navigateUpTo(upIntent);
    }

}
