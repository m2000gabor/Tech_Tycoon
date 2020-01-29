package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
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

//TODO monetary

public class FragmentDeviceCreator extends Fragment {
    private static final int CHOOSE_MEMORY_REQUEST = 1;
    int ram;
    private int ramLevel;
    int memory;
    private int memoryLevel;
    private int memoryCost=0;

    //layout res
    private EditText deviceNameField;
    private EditText profitField;
    private TextView currentCostField;
    private TextView chosenMem;
    private TextView chosenRam;
    private ImageView isSetMemoryImage;

    private List<Company> companies;
    private DeviceViewModel deviceViewModel;
    private Spinner spin;

    private boolean isMemorySet;
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
        new MySpinnerAdapter(spin);

        // Inflate the layout for this fragment
        return root;
    }


    private void addDevice() {
        if (!isMemorySet || !isCompanySet|| TextUtils.isEmpty(profitField.getText())) {
            Toast.makeText(getContext(), "All params are required", Toast.LENGTH_LONG).show();
        } else {
            String deviceName = deviceNameField.getText().toString();
            int profit = Integer.parseInt(profitField.getText().toString());
            int maker=companies.get(spin.getSelectedItemPosition()).companyId;

            deviceViewModel.insertDevice(new Device(deviceName,profit, getOverallCost(),maker,ram,memory));
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
                    memoryCost=data.getIntExtra("costs",99);
                    currentCostField.setText(String.format(Locale.getDefault(),"The current cost is %d$", getOverallCost()));
                    isMemorySet = true;
                    isSetMemoryImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_check_green_24dp));
                    chosenMem.setText(String.format(Locale.getDefault(),"Memory: %dGB", memory));
                    chosenRam.setText(String.format(Locale.getDefault(),"Memory: %dGB", ram));
                    chosenMem.setVisibility(View.VISIBLE);
                    chosenRam.setVisibility(View.VISIBLE);
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
                isCompanySet=true;
                reset(false);
                ramLevel=companies.get(position-1).getLevels_USE_THIS()[0];
                memoryLevel=companies.get(position-1).getLevels_USE_THIS()[1];
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private int getOverallCost(){ return memoryCost; }

    private void reset(boolean isFullReset){
        if(isFullReset){spin.setSelection(0);}
        companies=deviceViewModel.getAllCompaniesList();
        deviceNameField.setText("");
        profitField.setText("");
        currentCostField.setText("The current cost is 0$");
        chosenMem.setVisibility(View.INVISIBLE);
        chosenRam.setVisibility(View.INVISIBLE);
        isMemorySet=false;
        isSetMemoryImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_cancel_red_24dp));
    }

}



