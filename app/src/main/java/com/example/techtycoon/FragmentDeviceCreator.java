package com.example.techtycoon;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

import com.example.techtycoon.Assistant.AppleBot;
import com.example.techtycoon.Assistant.ToolsForAssistants;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

//TODO store views in array

public class FragmentDeviceCreator extends Fragment {
    private static final int NUMBER_OF_CHOOSER_ACTIVITIES=2;
    private static final int CHOOSE_MEMORY_REQUEST = 1;
    private static final int CHOOSE_BODY_REQUEST = 2;
    static final String BODY_RESULTS="bodyRes";
    private int ram;
    private int ramMaxLevel;
    private int memory;
    private int memoryMaxLevel;
    private int[] bodyResults;
    private int[] costs;

    private int[] levels;

    //layout res
    private EditText deviceNameField;
    private EditText profitField;
    private TextView currentCostField;
    //for mem ram
    private TextView chosenMem;
    private TextView chosenRam;
    private ImageView isSetMemoryImage;
    //for the Body
    private TextView chosenDesign;
    private TextView chosenMaterial;
    private TextView chosenColors;
    private TextView chosenIp;
    private TextView chosenBezel;
    private ImageView isSetBodyImage;

    private List<Company> companies;
    private DeviceViewModel deviceViewModel;
    private Spinner spin;

    private boolean isMemorySet;
    private boolean isBodySet;
    private boolean isCompanySet=false;
    private int myCompanysIndex=-1;

    public static FragmentDeviceCreator newInstance() {
        FragmentDeviceCreator fragment = new FragmentDeviceCreator();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        companies=deviceViewModel.getAllCompaniesList();
        isMemorySet=false;
        isBodySet=false;
        costs=new int[NUMBER_OF_CHOOSER_ACTIVITIES];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device_creator, container, false);


        //find fields
        deviceNameField = root.findViewById(R.id.deviceNameInputField);
        profitField = root.findViewById(R.id.profitInputField);
        currentCostField = root.findViewById(R.id.currentCostTextView);
        chosenMem = root.findViewById(R.id.chosenMemory);
        chosenRam = root.findViewById(R.id.chosenRam);
        isSetMemoryImage = root.findViewById(R.id.isSetMemory);
        spin = root.findViewById(R.id.spinner);
        //for body
        chosenDesign=root.findViewById(R.id.chosenDesign);
        chosenMaterial=root.findViewById(R.id.chosenMaterial);
        chosenColors=root.findViewById(R.id.chosenColors);
        chosenIp=root.findViewById(R.id.chosenIp);
        chosenBezel=root.findViewById(R.id.chosenBezel);
        isSetBodyImage=root.findViewById(R.id.isSetBody);


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
                    Intent chooseMemory = new Intent(getContext(),ChooseMemoryActivity.class);
                    chooseMemory.putExtra(MainActivity.RAM_LVL,ramMaxLevel);
                    chooseMemory.putExtra(MainActivity.MEMORY_LVL,memoryMaxLevel);
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
                    Intent chooseBody = new Intent(getContext(),ChooseBodyActivity.class);
                    chooseBody.putExtra(MainActivity.LEVELS,levels);
                    startActivityForResult(chooseBody,CHOOSE_BODY_REQUEST);
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
        if (!isMemorySet || !isBodySet || !isCompanySet || TextUtils.isEmpty(profitField.getText())) {
            Toast.makeText(getContext(), "All params are required", Toast.LENGTH_LONG).show();
        } else {
            String deviceName = deviceNameField.getText().toString();
            int profit = Integer.parseInt(profitField.getText().toString());
            Company maker=companies.get(spin.getSelectedItemPosition()-1);
            maker.usedSlots++;
            Device device=new Device(deviceName,profit,0,maker.companyId,null);
            device.setFieldByNum(0,ram);
            device.setFieldByNum(1,memory);
            device.setBodyParams(bodyResults[0],bodyResults[1],bodyResults[2],bodyResults[3],bodyResults[4]);
            device.cost=DeviceValidator.getOverallCost(device);
            deviceViewModel.insertDevice(device);
            deviceViewModel.updateCompanies(maker);
            Toast.makeText(getContext(), "Saved", Toast.LENGTH_LONG).show();
            reset(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case CHOOSE_MEMORY_REQUEST:
                    int r=data.getIntExtra("amountOfRam",1);
                    int m=data.getIntExtra("amountOfMemory",1);
                    int c=data.getIntExtra("cost",99);
                    setupStorage(r,m,c);
                    break;
                case CHOOSE_BODY_REQUEST:
                    setupBody(data.getIntExtra("cost",99),data.getIntArrayExtra(BODY_RESULTS));
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
                }catch (NullPointerException ignored){};
                //Toast.makeText(getContext(),"Choose a manufacturer",Toast.LENGTH_SHORT).show();
            }else{
                Company selectedCompany=companies.get(position-1);
                isCompanySet=true;
                myCompanysIndex=position-1;
                reset(false);
                levels=selectedCompany.getLevels_USE_THIS();
                ramMaxLevel=levels[0];
                memoryMaxLevel=levels[1];
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
        int overallCost=0;
        for (int i=0;i<NUMBER_OF_CHOOSER_ACTIVITIES;i++){overallCost+=costs[i];}
        return overallCost; }

    private void reset(boolean isFullReset){
        if(isFullReset){spin.setSelection(0);deviceNameField.setText("");myCompanysIndex=-1;isCompanySet=false;}
        companies=deviceViewModel.getAllCompaniesList();
        profitField.setText("");
        currentCostField.setText("The current cost is 0$");
        chosenMem.setVisibility(View.INVISIBLE);
        chosenRam.setVisibility(View.INVISIBLE);
        chosenDesign.setVisibility(View.INVISIBLE);
        chosenMaterial.setVisibility(View.INVISIBLE);
        chosenColors.setVisibility(View.INVISIBLE);
        chosenBezel.setVisibility(View.INVISIBLE);
        chosenIp.setVisibility(View.INVISIBLE);
        isMemorySet=false;
        isBodySet=false;
        isSetMemoryImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_cancel_red_24dp));
        isSetBodyImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_cancel_red_24dp));
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

    public void loadInADevice(int deviceID){
        Device device=deviceViewModel.getDevice_byID(deviceID);
        int storageCost=(int) Math.round(DeviceValidator.getCostOfMemory(0,device.memory))+
                (int) Math.round(DeviceValidator.getCostOfMemory(1,device.ram));
        int bodyCost=0;
        for(int i=0;i<Device.CHILDREN_OF_BUDGETS[1];i++){
            bodyCost+=DeviceValidator.getCostOfBody(i,device.getFieldByNum(i+Device.CHILDREN_OF_BUDGETS[0]));
        }
        setupStorage(device.ram,device.memory,storageCost);
        setupBody(bodyCost,device.getParams()[1]);
        deviceNameField.setText(device.name);
        profitField.setText(String.valueOf(device.profit));
    }

    private void setupStorage(int ram,int memory,int storageCost){
        this.ram=ram;
        this.memory=memory;
        costs[0]=storageCost;
        currentCostField.setText(String.format(Locale.getDefault(),"The current cost is %d$", getOverallCost()));
        isMemorySet = true;
        isSetMemoryImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_check_green_24dp));
        chosenMem.setText(String.format(Locale.getDefault(),"Memory: %dGB", (int) Math.round(Math.pow(2,memory)) ));
        chosenRam.setText(String.format(Locale.getDefault(),"Ram: %dGB", (int) Math.round(Math.pow(2,ram))));
        chosenMem.setVisibility(View.VISIBLE);
        chosenRam.setVisibility(View.VISIBLE);
    }

    private void setupBody(int bodyCost,int[] bodyParams){
        bodyResults=bodyParams;
        costs[1]=bodyCost;
        currentCostField.setText(String.format(Locale.getDefault(),"The current cost is %d$", getOverallCost()));
        isBodySet=true;
        isSetBodyImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_check_green_24dp));
        chosenDesign.setText(String.format(Locale.getDefault(),"Design: %dp", bodyResults[0]));
        chosenMaterial.setText(String.format(Locale.getDefault(),"Material: %dp", bodyResults[1]));
        chosenColors.setText(String.format(Locale.getDefault(),"Colors: %dp", bodyResults[2]));
        chosenIp.setText(String.format(Locale.getDefault(),"Ip: %dp", bodyResults[3]));
        chosenBezel.setText(String.format(Locale.getDefault(),"Bezel: %dp", bodyResults[4]));
        chosenDesign.setVisibility(View.VISIBLE);
        chosenMaterial.setVisibility(View.VISIBLE);
        chosenColors.setVisibility(View.VISIBLE);
        chosenBezel.setVisibility(View.VISIBLE);
        chosenIp.setVisibility(View.VISIBLE);
    }

}



