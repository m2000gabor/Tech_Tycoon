package com.example.techtycoon;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

public class DetailsOfOneDevice extends AppCompatActivity {
    DeviceViewModel deviceViewModel;
    int id;
    Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_of_one_device);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //find views
        TextView nevTextV =findViewById(R.id.name);
        TextView profitTextV =findViewById(R.id.profit);
        TextView deviceIDTextV =findViewById(R.id.deviceID);
        TextView ownerIDTextV =findViewById(R.id.ownerID);
        TextView priceTextV =findViewById(R.id.price);
        TextView ramTextV =findViewById(R.id.ram);
        TextView memoryTextV =findViewById(R.id.memory);
        TextView costTextV =findViewById(R.id.cost);
        TextView bodyTextV =findViewById(R.id.bodyInfos);
        TextView displayTextV =findViewById(R.id.displayInfos);
        TextView historySoldPiecesTextView =findViewById(R.id.historySoldPiecesTextView);

        id=getIntent().getIntExtra("ID",-1);
        deviceViewModel=new ViewModelProvider(this).get(DeviceViewModel.class);
        device=deviceViewModel.getDevice_byID(id);

        //display data
        nevTextV.setText("Name: "+device.name);
        priceTextV.setText(String.format(Locale.getDefault(),"Price: %d$",device.getPrice()));
        profitTextV.setText(String.format(Locale.getDefault(),"Profit: %d$",device.profit));
        deviceIDTextV.setText(String.format(Locale.getDefault(),"Device ID: %d",device.id));
        ownerIDTextV.setText(String.format(Locale.getDefault(),"Owner id: %d",device.ownerCompanyId));
        historySoldPiecesTextView.setText(String.format(Locale.getDefault(),"History - sold pieces: %d",device.history_SoldPieces));

        ramTextV.setText(String.format(Locale.getDefault(),"RAM: %d GB",(int) Math.pow(2,device.getFieldByAttribute(Device.DeviceAttribute.STORAGE_RAM))));
        memoryTextV.setText(String.format(Locale.getDefault(),"Memory: %d GB",(int) Math.pow(2,device.getFieldByAttribute(Device.DeviceAttribute.STORAGE_MEMORY))));
        costTextV.setText(String.format(Locale.getDefault(),"Cost: %d$",device.cost));
        //body
        StringBuilder sb=new StringBuilder("Body: ");
        for (Device.DeviceAttribute att:Device.getAllAttribute_InBudget(Device.DeviceBudget.BODY)){
            sb.append(device.getFieldByAttribute(att));
            sb.append(';');
        }
        sb.deleteCharAt(sb.length()-1);
        bodyTextV.setText(sb.toString());
        //display
        sb=new StringBuilder("Display: ");
        for (Device.DeviceAttribute att:Device.getAllAttribute_InBudget(Device.DeviceBudget.DISPLAY)){
            sb.append(device.getFieldByAttribute(att));
            sb.append(';');
        }
        sb.deleteCharAt(sb.length()-1);
        displayTextV.setText(sb.toString());

    }

    public void delThisOne(View view) {
        deviceViewModel.delOneDeviceById(id);
        finish();
    }

    public void editProfit(View v){
        TextView textView=findViewById(R.id.profit);
        if(textView.getVisibility()==View.VISIBLE) {
            EditText editText = findViewById(R.id.profitEditable);
            textView.setVisibility(View.GONE);
            editText.setText(String.valueOf(device.profit));
            editText.setVisibility(View.VISIBLE);
            ((Button) v).setText("Save");
        }else{
            EditText editText = findViewById(R.id.profitEditable);
            int newProfit = Integer.parseInt(editText.getText().toString());
            editText.setVisibility(View.GONE);
            textView.setText(String.format(Locale.getDefault(),"Profit: %d$",newProfit));
            textView.setVisibility(View.VISIBLE);
            ((Button) v).setText("Edit");

            //save
            device.profit=newProfit;
            deviceViewModel.updateDevices(device);
        }
    }

    public void editName(View v){
        TextView textView=findViewById(R.id.name);
        if(textView.getVisibility()==View.VISIBLE) {
            EditText editText = findViewById(R.id.nameEditable);
            textView.setVisibility(View.GONE);
            editText.setText(device.name);
            editText.setVisibility(View.VISIBLE);
            ((Button) v).setText("Save");
        }else{
            EditText editText = findViewById(R.id.nameEditable);
            String newName = editText.getText().toString();
            editText.setVisibility(View.GONE);
            textView.setText(String.format(Locale.getDefault(),"Name: %s",newName));
            textView.setVisibility(View.VISIBLE);
            ((Button) v).setText("Edit");

            device.name=newName;
            deviceViewModel.updateDevices(device);
        }
    }
}
