package com.example.techtycoon;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

//TODO view arrays

public class FragmentDeviceCreator extends Fragment {
    private static final int NUMBER_OF_CHOOSER_ACTIVITIES=2;
    private static final int CHOOSE_MEMORY_REQUEST = 1;
    private static final int CHOOSE_BODY_REQUEST = 2;
    static final String BODY_RESULTS="bodyRes";
    int ram;
    int memory;
    private int ramLevel;
    private int memoryLevel;
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

        //set up reset button
        root.findViewById(R.id.resetDeviceCreator).setOnClickListener(v -> reset(true));

        //start memorychooser
        RelativeLayout startMemoryChooserRelativelayout = root.findViewById(R.id.startMemoryChooserRelativeLayout);
        startMemoryChooserRelativelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCompanySet){
                    Intent chooseMemory = new Intent(getContext(),ChooseMemoryActivity.class);
                    chooseMemory.putExtra(MainActivity.RAM_LVL,ramLevel);
                    chooseMemory.putExtra(MainActivity.MEMORY_LVL,memoryLevel);
                    startActivityForResult(chooseMemory,CHOOSE_MEMORY_REQUEST);
                }else {
                    Toast.makeText(getContext(),"Choose a manufacturer",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //setUp bodyChooser
        RelativeLayout startBodyChooserRelativelayout = root.findViewById(R.id.startBodyChooserRelativeLayout);
        startBodyChooserRelativelayout.setOnClickListener(new View.OnClickListener() {
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
            Device device=new Device(deviceName,profit, getOverallCost(),maker.companyId,ram,memory);
            device.setBodyParams(bodyResults[1],bodyResults[2],bodyResults[3],bodyResults[4],bodyResults[5]);
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
                    ram=data.getIntExtra("amountOfRam",0);
                    memory=data.getIntExtra("amountOfMemory",0);
                    costs[0]=data.getIntExtra("costs",99);
                    currentCostField.setText(String.format(Locale.getDefault(),"The current cost is %d$", getOverallCost()));
                    isMemorySet = true;
                    isSetMemoryImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_check_green_24dp));
                    chosenMem.setText(String.format(Locale.getDefault(),"Memory: %dGB", memory));
                    chosenRam.setText(String.format(Locale.getDefault(),"Ram: %dGB", ram));
                    chosenMem.setVisibility(View.VISIBLE);
                    chosenRam.setVisibility(View.VISIBLE);
                    break;
                case CHOOSE_BODY_REQUEST:
                    bodyResults=data.getIntArrayExtra(BODY_RESULTS);
                    costs[1]=bodyResults[0];
                    currentCostField.setText(String.format(Locale.getDefault(),"The current cost is %d$", getOverallCost()));
                    isBodySet=true;
                    isSetBodyImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_check_green_24dp));
                    chosenDesign.setText(String.format(Locale.getDefault(),"Design: %dp", bodyResults[1]));
                    chosenMaterial.setText(String.format(Locale.getDefault(),"Material: %dp", bodyResults[2]));
                    chosenColors.setText(String.format(Locale.getDefault(),"Colors: %dp", bodyResults[3]));
                    chosenIp.setText(String.format(Locale.getDefault(),"Ip: %dp", bodyResults[4]));
                    chosenBezel.setText(String.format(Locale.getDefault(),"Bezel: %dp", bodyResults[5]));
                    chosenDesign.setVisibility(View.VISIBLE);
                    chosenMaterial.setVisibility(View.VISIBLE);
                    chosenColors.setVisibility(View.VISIBLE);
                    chosenBezel.setVisibility(View.VISIBLE);
                    chosenIp.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    public class MySpinnerAdapter implements
            AdapterView.OnItemSelectedListener {

        MySpinnerAdapter(Spinner spin) {
            String[] tmp=new String[companies.size()+1];
            tmp[0]="Choose a manufacturer";
            for(int i=1;i<=companies.size();i++){tmp[i]=companies.get(i-1).name;}


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
                reset(false);
                //Toast.makeText(getContext(),"Choose a manufacturer",Toast.LENGTH_SHORT).show();
            }else{
                Company selectedCompany=companies.get(position-1);
                isCompanySet=true;
                reset(false);
                levels=selectedCompany.getLevels_USE_THIS();
                ramLevel=levels[0];
                memoryLevel=levels[1];
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
        if(isFullReset){spin.setSelection(0);deviceNameField.setText("");}
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


}



