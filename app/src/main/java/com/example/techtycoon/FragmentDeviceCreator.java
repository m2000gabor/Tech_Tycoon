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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

//TODO add reset button
//TODO monetary

public class FragmentDeviceCreator extends Fragment {
    private static final int CHOOSE_MEMORY_REQUEST = 1;
    int ram;
    int ramLevel;
    int memory;
    int memoryLevel;
    private int memoryCost=0;

    //from newdeviceBasics
    private EditText deviceNameField;
    private EditText profitField;
    private TextView currentCostField;
    private ImageView isSetMemoryImage;
    private String[] nameOfCompanies;
    private List<Company> companies;
    private DeviceViewModel deviceViewModel;
    private Spinner spin;

    private boolean isMemorySet;

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
        root.findViewById(R.id.resetDeviceCreator).setOnClickListener(v -> reset());

        //start memorychooser
        root.findViewById(R.id.startMemoryChooserRelativelayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseMemory = new Intent(getContext(),ChooseMemoryActivity.class);
                chooseMemory.putExtra(MainActivity.RAM_LVL,ramLevel);
                chooseMemory.putExtra(MainActivity.MEMORY_LVL,memoryLevel);
                startActivityForResult(chooseMemory,CHOOSE_MEMORY_REQUEST);
            }
        });


        String[] tmp=new String[companies.size()];
        for(int i=0;i<companies.size();i++){tmp[i]=companies.get(i).name;}
        nameOfCompanies =tmp;
        new MySpinnerAdapter(spin);

        // Inflate the layout for this fragment
        return root;
    }


    private void addDevice() {
        if (!isMemorySet || TextUtils.isEmpty(profitField.getText())) {
            Toast.makeText(getContext(), "profit is required", Toast.LENGTH_LONG).show();
        } else {
            String deviceName = deviceNameField.getText().toString();
            int profit = Integer.parseInt(profitField.getText().toString());
            int maker=companies.get(spin.getSelectedItemPosition()).companyId;

            deviceViewModel.insertDevice(new Device(deviceName,profit, getOverallCost(),maker,ram,memory));
            Toast.makeText(getContext(), "Saved", Toast.LENGTH_LONG).show();
            reset();
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
                    break;
            }
        }
    }

    public class MySpinnerAdapter implements
            AdapterView.OnItemSelectedListener {

        MySpinnerAdapter(Spinner spin) {
            //Getting the instance of Spinner and applying OnItemSelectedListener on it

            spin.setOnItemSelectedListener(this);

            //Creating the ArrayAdapter instance having the nameOfCompanies list
            ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, nameOfCompanies);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spin.setAdapter(aa);
        }

        //Performing action onItemSelected and onNothing selected
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
            ramLevel=companies.get(position).getLevels_USE_THIS()[0];
            memoryLevel=companies.get(position).getLevels_USE_THIS()[1];
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private int getOverallCost(){ return memoryCost; }

    private void reset(){
        spin.setSelection(0);
        companies=deviceViewModel.getAllCompaniesList();
        deviceNameField.setText("");
        profitField.setText("");
        currentCostField.setText("The current cost is 0$");
        isMemorySet=false;
        isSetMemoryImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_cancel_red_24dp));
    }
}



