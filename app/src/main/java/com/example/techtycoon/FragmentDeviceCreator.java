package com.example.techtycoon;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techtycoon.ui.activities.ChooseBodyActivity;
import com.example.techtycoon.ui.activities.ChooseDisplayActivity;
import com.example.techtycoon.ui.activities.ChooseMemoryActivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import static android.app.Activity.RESULT_OK;


public class FragmentDeviceCreator extends Fragment {
    private static final int CHOOSE_MEMORY_REQUEST = 1;
    private static final int CHOOSE_BODY_REQUEST = 2;
    private static final int CHOOSE_DISPLAY_REQUEST = 3;
    public static final String CHOOSER_RESULTS_as_intArray ="arrayResult";

    //layout res
    private EditText deviceNameField;
    private EditText profitField;
    private TextView currentCostField;
    private Map<Device.DeviceAttribute,TextView> attributeTextViewMap; //small labels like ram ?GB
    private Map<Device.DeviceBudget,ImageView> budgetImageViewMap; //checked or unchecked


    private List<Company> companies;
    private DeviceViewModel deviceViewModel;
    private Spinner spin;


    private HashMap<Device.DeviceBudget,Boolean> isBudgetSet;
    private boolean isCompanySet=false;
    private int myCompanysIndex=-1;//chosen company's position in the companies list

    private Device device;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        companies=deviceViewModel.getAllCompaniesList();
        isBudgetSet=new HashMap<>();
        for (Device.DeviceBudget bud: Device.DeviceBudget.values()) {
            isBudgetSet.put(bud,false);
        }
        device=Device.getMinimalDevice("noName",0,-1);
        attributeTextViewMap=new HashMap<>();
        budgetImageViewMap=new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device_creator, container, false);


        //find fields
        deviceNameField = root.findViewById(R.id.deviceNameInputField);
        profitField = root.findViewById(R.id.profitInputField);
        currentCostField = root.findViewById(R.id.currentCostTextView);
        spin = root.findViewById(R.id.spinner);

        //belongs to storage
        attributeTextViewMap.put(Device.DeviceAttribute.STORAGE_MEMORY,root.findViewById(R.id.chosenMemory));
        attributeTextViewMap.put(Device.DeviceAttribute.STORAGE_RAM,root.findViewById(R.id.chosenRam));
        budgetImageViewMap.put(Device.DeviceBudget.STORAGE,root.findViewById(R.id.isSetMemory));

        //belongs to body
        attributeTextViewMap.put(Device.DeviceAttribute.BODY_DESIGN,root.findViewById(R.id.chosenDesign));
        attributeTextViewMap.put(Device.DeviceAttribute.BODY_MATERIAL,root.findViewById(R.id.chosenMaterial));
        attributeTextViewMap.put(Device.DeviceAttribute.BODY_COLOR,root.findViewById(R.id.chosenColors));
        attributeTextViewMap.put(Device.DeviceAttribute.BODY_IP,root.findViewById(R.id.chosenIp));
        attributeTextViewMap.put(Device.DeviceAttribute.BODY_BEZEL,root.findViewById(R.id.chosenBezel));
        budgetImageViewMap.put(Device.DeviceBudget.BODY,root.findViewById(R.id.isSetBody));

        //belongs to display
        attributeTextViewMap.put(Device.DeviceAttribute.DISPLAY_RESOLUTION,root.findViewById(R.id.chosenResolution));
        attributeTextViewMap.put(Device.DeviceAttribute.DISPLAY_BRIGHTNESS,root.findViewById(R.id.chosenBrightness));
        attributeTextViewMap.put(Device.DeviceAttribute.DISPLAY_REFRESH_RATE,root.findViewById(R.id.chosenRefreshRate));
        attributeTextViewMap.put(Device.DeviceAttribute.DISPLAY_TECHNOLOGY,root.findViewById(R.id.chosenDisplayTechnology));
        budgetImageViewMap.put(Device.DeviceBudget.DISPLAY,root.findViewById(R.id.isSetDisplay));


        //set up the save button
        root.findViewById(R.id.saveDevice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDevice();
            }
        });

        //set up exit button
        root.findViewById(R.id.exitDeviceCreator).setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

        //set up clone button
        root.findViewById(R.id.cloneButton).setOnClickListener(v->{
            if(isCompanySet && myCompanysIndex!=-1){
                showAlertDialog();
            }else{
                Toast.makeText(getContext(), "Set a company", Toast.LENGTH_LONG).show();
            }
        });

        //setUp insertBasicButton
        root.findViewById(R.id.insertBasic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCompanySet || !companies.get(spin.getSelectedItemPosition()-1).hasFreeSlot()) {
                    Toast.makeText(getContext(), "Set a company with free slots", Toast.LENGTH_LONG).show();
                } else {
                    Company maker=companies.get(spin.getSelectedItemPosition()-1);
                    String deviceName;
                    if(deviceNameField.getText().toString().equals("")){
                        deviceName= "test by "+maker.name;
                    }else{deviceName=deviceNameField.getText().toString();}
                    int profit = 100;
                    maker.usedSlots++;
                    int[] params=new int[Device.NUMBER_OF_ATTRIBUTES];
                    for (int i = 0; i <Device.NUMBER_OF_ATTRIBUTES; i++) {params[i]=1;}
                    Device device=new Device(deviceName,profit,0,maker.companyId,params);
                    device.cost=DeviceValidator.getOverallCost(device);
                    deviceViewModel.insertDevice(device);
                    deviceViewModel.updateCompanies(maker);
                    Toast.makeText(getContext(), "Saved", Toast.LENGTH_LONG).show();
                    reset(true);
                }
            }
        });

        //start memorychooser
        RelativeLayout startMemoryChooserRelativeLayout = root.findViewById(R.id.startMemoryChooserRelativeLayout);
        startMemoryChooserRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCompanySet){
                    Intent chooseMemory = new Intent(getContext(), ChooseMemoryActivity.class);
                    chooseMemory.putExtra(MainActivity.LEVELS,companies.get(myCompanysIndex).getLevelByBudget(Device.DeviceBudget.STORAGE));
                    startActivityForResult(chooseMemory,CHOOSE_MEMORY_REQUEST);
                }else {
                    Toast.makeText(getContext(),"Choose a manufacturer",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //setUp bodyChooser
        RelativeLayout startBodyChooserRelativeLayout = root.findViewById(R.id.startBodyChooserRelativeLayout);
        startBodyChooserRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCompanySet){
                    Intent chooseBody = new Intent(getContext(), ChooseBodyActivity.class);
                    chooseBody.putExtra(MainActivity.LEVELS,companies.get(myCompanysIndex).getLevelByBudget(Device.DeviceBudget.BODY));
                    startActivityForResult(chooseBody,CHOOSE_BODY_REQUEST);
                }else {
                    Toast.makeText(getContext(),"Choose a manufacturer",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //displayChooser
        RelativeLayout startDisplayChooserRelativeLayout = root.findViewById(R.id.startDisplayChooserRelativeLayout);
        startDisplayChooserRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCompanySet){
                    Intent chooseDisplay = new Intent(getContext(), ChooseDisplayActivity.class);
                    chooseDisplay.putExtra(MainActivity.LEVELS,companies.get(myCompanysIndex).getLevelByBudget(Device.DeviceBudget.DISPLAY));
                    startActivityForResult(chooseDisplay,CHOOSE_DISPLAY_REQUEST);
                }else {
                    Toast.makeText(getContext(),"Choose a manufacturer",Toast.LENGTH_SHORT).show();
                }
            }
        });

        new MySpinnerAdapter(spin);
        if(myCompanysIndex!=-1){spin.setSelection(myCompanysIndex+1);}

        // Inflate the layout for this fragment
        return root;
    }


    private void addDevice() {
        if (isBudgetSet.containsValue(Boolean.FALSE) || TextUtils.isEmpty(profitField.getText()) || TextUtils.isEmpty(deviceNameField.getText()) ) {
            Toast.makeText(getContext(), "All params are required", Toast.LENGTH_LONG).show();
        } else {
            String deviceName = deviceNameField.getText().toString();
            int profit = Integer.parseInt(profitField.getText().toString());
            Company maker=companies.get(spin.getSelectedItemPosition()-1);
            maker.usedSlots++;
            device.name=deviceName;
            device.ownerCompanyId=maker.companyId;
            device.profit=profit;
            device.cost=DeviceValidator.getOverallCost(device);
            deviceViewModel.insertDevice(device);
            deviceViewModel.updateCompanies(maker);
            Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
            reset(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case CHOOSE_MEMORY_REQUEST:
                    setup_a_budget(Device.DeviceBudget.STORAGE,data.getIntArrayExtra(CHOOSER_RESULTS_as_intArray) );
                    break;
                case CHOOSE_BODY_REQUEST:
                    setup_a_budget(Device.DeviceBudget.BODY,data.getIntArrayExtra(CHOOSER_RESULTS_as_intArray));
                    break;
                case CHOOSE_DISPLAY_REQUEST:
                    setup_a_budget(Device.DeviceBudget.DISPLAY,data.getIntArrayExtra(CHOOSER_RESULTS_as_intArray));
                    break;
            }
        }
    }

    public class MySpinnerAdapter implements
            AdapterView.OnItemSelectedListener {

        MySpinnerAdapter(Spinner spin) {
            String[] tmp=new String[companies.size()+1];
            tmp[0]="Choose a manufacturer";
            for(int i=1;i<=companies.size();i++){
                tmp[i]=companies.get(i-1).name;
                if(getArguments()!=null && getArguments().getInt("ID",-1) !=-1 &&  getArguments().getInt("ID",-1)==companies.get(i-1).companyId){
                    myCompanysIndex=i-1;
                    isCompanySet=true;
                }
            }

            //Getting the instance of Spinner and applying OnItemSelectedListener on it
            spin.setOnItemSelectedListener(this);

            //Creating the ArrayAdapter instance having the nameOfCompanies list
            ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, tmp);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spin.setAdapter(aa);
        }

        //Performing action onItemSelected and onNothing selected
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
            if(position==0){
                isCompanySet=false;
                try{
                    reset(false);
                }catch (NullPointerException ignored){}
                //Toast.makeText(getContext(),"Choose a manufacturer",Toast.LENGTH_SHORT).show();
            }else{
                Company selectedCompany=companies.get(position-1);
                isCompanySet=true;
                myCompanysIndex=position-1;
                reset(false);
                if(selectedCompany.maxSlots==companies.get(position-1).usedSlots){
                    noFreeSlotAlert(selectedCompany);
                }
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private int getOverallCost(){
        return DeviceValidator.getOverallCost(device);
    }

    private void reset(boolean isFullReset){
        if(isFullReset){spin.setSelection(0);deviceNameField.setText("");myCompanysIndex=-1;isCompanySet=false;}
        companies=deviceViewModel.getAllCompaniesList();
        profitField.setText("");
        currentCostField.setText("The current cost is 0$");
        for(TextView tv:attributeTextViewMap.values()){
            tv.setVisibility(View.GONE);
        }
        for (Device.DeviceBudget att: Device.DeviceBudget.values()) {
            isBudgetSet.put(att,false);
        }
        for(ImageView iv :budgetImageViewMap.values()){
            iv.setImageDrawable(getActivity().getDrawable(R.drawable.ic_cancel_red_24dp));
        }
    }

    private void noFreeSlotAlert(Company company){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("No free slot is available at "+company.name)
                .setTitle("Error");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        spin.setSelection(0);
    }

    private void showAlertDialog(){
        List<Device> producible=new LinkedList<>();
        List<Device> allDev=deviceViewModel.getAllDevicesList();
        for (Device d : allDev) {
            if(companies.get(myCompanysIndex).producibleByTheCompany(d)){producible.add(d);}
        }
        DialogFragment dialog=new ChooseADeviceDialogFragment(producible);
        dialog.show(getChildFragmentManager(),"cloneDevice");
    }

    void loadInADevice(int deviceID){
        Device d=deviceViewModel.getDevice_byID(deviceID);

        for(Device.DeviceBudget budget:Device.DeviceBudget.values()){
            int[] p=new int[Device.getAllAttribute_InBudget(budget).size()];
            for(int i=0;i<Device.getAllAttribute_InBudget(budget).size();i++){
                p[i]=d.getFieldByAttribute(Device.getAllAttribute_InBudget(budget).get(i));
            }
            setup_a_budget(budget,p);
        }

        deviceNameField.setText(d.name);
        profitField.setText(String.valueOf(d.profit));
    }


    public void setup_a_budget(Device.DeviceBudget budget,int[] params){
        for(int i=0;i<Device.getAllAttribute_InBudget(budget).size();i++){
            device.setFieldByAttribute(
                    Device.getAllAttribute_InBudget(budget).get(i),params[i]);
        }
        currentCostField.setText(String.format(Locale.getDefault(),"The current cost is %d$", getOverallCost()));
        isBudgetSet.put(budget,Boolean.TRUE);
        budgetImageViewMap.get(budget).setImageDrawable(getActivity().getDrawable(R.drawable.ic_check_green_24dp));
        for(int i=0;i<Device.getAllAttribute_InBudget(budget).size();i++){
            Device.DeviceAttribute attribute=Device.getAllAttribute_InBudget(budget).get(i);
            TextView t=attributeTextViewMap.get(attribute);
            StringBuilder sb =new StringBuilder(Device.attributeToString(attribute));
            sb.append(": ");
            sb.append(Device.getStringFromAttributeLevel(attribute,device.getFieldByAttribute(attribute)));
            t.setText(sb.toString());
            t.setVisibility(View.VISIBLE);
        }
    }



}



