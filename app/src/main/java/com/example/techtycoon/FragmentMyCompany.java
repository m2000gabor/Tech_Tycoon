package com.example.techtycoon;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class FragmentMyCompany extends Fragment {
    private final int START_DEVELOPMENT_ACTIVITY=23;
    private DeviceViewModel deviceViewModel;
    private DeviceSmallListAdapter adapter;
    private LiveData<List<Device>> deviceList;
    private LiveData<Company> myCompanyLiveData;
    private Company myCompany;
    private Spinner spinner;

    private TextView nameOfMyCompanyTextView;
    private TextView moneyTextView;
    private TextView profitTextView;
    private TextView cashFlowTextView;
    private TextView marketingTextView;
    private TextView slotsTextView;

    public FragmentMyCompany() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentMyCompany.
     */
    public static FragmentMyCompany newInstance() {
        FragmentMyCompany fragment = new FragmentMyCompany();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get a new or existing ViewModel from the ViewModelProvider.
        deviceViewModel =new ViewModelProvider(this).get(DeviceViewModel.class);
        deviceViewModel.orderDevices_ByCode2(Device.DeviceAttribute.INCOME,true);
        deviceList = deviceViewModel.mutable_getAllDevices();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_company, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.myDevicesRecyclerView);
        spinner= root.findViewById(R.id.chooseCompanySpinner);
        //onclick
        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //number of clicked item
                int itemPosition = recyclerView.getChildLayoutPosition(v);

                //get device fileds with mutable
                Device currentDev=adapter.getDeviceFromCache(itemPosition);
                int id = currentDev.id;

                //make intent
                Intent intent = new Intent();
                intent.putExtra("ID", id);
                intent.setClass(getContext(), DetailsOfOneDevice.class);
                //start new activity
                startActivityForResult(intent, MainActivity.DISPLAY_DEVICES_REQUEST_CODE);
            }
        };
        adapter = new DeviceSmallListAdapter(getContext(), mOnClickListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        root.findViewById(R.id.buyMarketingButton).setOnClickListener(this::buyMarketing);
        root.findViewById(R.id.buyNewSlotButton).setOnClickListener(this::buyNewSlot);

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        // Update the cached copy of the words in the adapter.
        Observer<List<Device>> observer = new Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable final List<Device> devs) {
                // Update the cached copy of the words in the adapter.
                adapter.setDevices(devs);
            }
        };
        deviceList.observe(getViewLifecycleOwner(), observer);
        new ChooseCompanyAdapter();

        //find views
        nameOfMyCompanyTextView=root.findViewById(R.id.nameOfMyCompanyTextView);
        nameOfMyCompanyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make intent
                Intent intent = new Intent();
                intent.putExtra("ID", myCompany.companyId);
                intent.setClass(getContext(), DetailsOfOneCompany.class);

                //start new activity
                startActivityForResult(intent, MainActivity.DISPLAY_COMPANIES_REQUEST_CODE);
            }
        });
        profitTextView= root.findViewById(R.id.profitTextView);
        cashFlowTextView= root.findViewById(R.id.cashFlowTextView);
        moneyTextView= root.findViewById(R.id.moneyTextView);
        slotsTextView= root.findViewById(R.id.slotsTextView);
        marketingTextView= root.findViewById(R.id.marketingTextView);
        Button startDevelopmentActivityButton=root.findViewById(R.id.startDevelopmentActivity);
        startDevelopmentActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent();
                intent1.setClass(getContext(),DevelopmentActivity.class);
                intent1.putExtra("ID",myCompany.companyId);
                intent1.putExtra(MainActivity.LEVELS,myCompany.getLevels_USE_THIS());
                intent1.putExtra(MainActivity.MAIN_MONETARILY_INFO,myCompany.money);
                startActivityForResult(intent1,START_DEVELOPMENT_ACTIVITY);
            }
        });

        Button startDeviceCreatorButton=root.findViewById(R.id.startDeviceCreator);
        startDeviceCreatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myCompany!=null){
                    if(myCompany.hasFreeSlot()){
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        FragmentDeviceCreator newFr=new FragmentDeviceCreator();
                        Bundle args = new Bundle();
                        args.putInt("ID",myCompany.companyId);
                        newFr.setArguments(args);
                        fragmentTransaction.replace(R.id.fragment_my_company, newFr, "frag1");
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }else{
                        noFreeSlotAlert(myCompany);
                    }
                }
            }
        });

        return root;
    }

    //set up the company spinner
    public class ChooseCompanyAdapter implements
            AdapterView.OnItemSelectedListener {
        Company[] companies;

        ChooseCompanyAdapter() {
            //Getting the instance of Spinner and applying OnItemSelectedListener on it
            spinner.setOnItemSelectedListener(this);
            List<Company> companyList=deviceViewModel.getAllCompaniesList();
            companies=new Company[companyList.size()];
            companies=companyList.toArray(companies);
            String[] namesOfCompanies=new String[companyList.size()];
            for (int i=0;i<companyList.size();++i){namesOfCompanies[i]=companies[i].name;}


            //Creating the ArrayAdapter instance having the nameOfCompanies list
            ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,
                    namesOfCompanies );
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spinner.setAdapter(aa);
        }

        //Performing action onItemSelected and onNothing selected
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
            int companyID;
            if(position>=0){companyID=companies[position].companyId;
            }else{companyID=-1;}
            deviceViewModel.filterBy(Device.DeviceAttribute.OWNER_ID, Collections.singletonList(companyID));
            try {
                myCompanyLiveData.hasObservers();
                myCompanyLiveData.removeObservers(getViewLifecycleOwner());
            }catch (NullPointerException ignored){}

            myCompanyLiveData = deviceViewModel.getLiveCompany_byID(companyID);
            Observer<Company> companyObserver = new Observer<Company>() {
                @Override
                public void onChanged(@Nullable final Company c) {
                    if(c!=null){
                        myCompany=c;
                        nameOfMyCompanyTextView.setText(c.name);
                        profitTextView.setText(String.format(Locale.getDefault(),"%d$",c.lastProfit));
                        String cashFlowPos=String.valueOf(c.marketPosition);
                        switch (cashFlowPos) {
                            case "1":
                                cashFlowPos = "1st";
                                break;
                            case "2":
                                cashFlowPos = "2nd";
                                break;
                            case "3":
                                cashFlowPos = "3rd";
                                break;
                            default:
                                cashFlowPos = cashFlowPos + "th";
                                break;
                        }
                        cashFlowTextView.setText(String.format("Cash flow: %s",cashFlowPos));
                        moneyTextView.setText(String.format(Locale.getDefault(),"%d$",c.money));
                        marketingTextView.setText(String.format(Locale.getDefault(),"Marketing: %d",c.marketing));
                        slotsTextView.setText(String.format(Locale.getDefault(),"Slots: %d/%d",c.usedSlots,c.maxSlots));
                    }
                }
            };
            myCompanyLiveData.observe(getViewLifecycleOwner(),companyObserver);
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK && requestCode==START_DEVELOPMENT_ACTIVITY) {
            boolean isUpgrade = data.getBooleanExtra("IS_UPDATE", false);
            if (isUpgrade) {
                myCompany.setLevels_USE_THIS(data.getIntArrayExtra(MainActivity.LEVELS));
                myCompany.money=data.getIntExtra(MainActivity.MAIN_MONETARILY_INFO,0);
                deviceViewModel.updateCompanies(myCompany);
            }
        }else{super.onActivityResult(requestCode, resultCode, data);}

    }

    private void buyMarketing(View view){
        LinearLayout ll = view.getRootView().findViewById(R.id.buyMarketingConfirmation);
        TextView textView=ll.findViewById(R.id.buyMarketingConfirmationText);
        textView.setText(String.format(Locale.getDefault(),"Do you want to buy 10 marketing for %d$?",
                DevelopmentValidator.calculateMarketingCost(myCompany.marketing)));
        ll.setVisibility(View.VISIBLE);
        ImageButton confirm=ll.findViewById(R.id.confirmMarketingPurchase);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myCompany.money>=DevelopmentValidator.calculateMarketingCost(myCompany.marketing)){
                    myCompany.money-=DevelopmentValidator.calculateMarketingCost(myCompany.marketing);
                    myCompany.marketing+=10;
                    deviceViewModel.updateCompanies(myCompany);
                    textView.setText(String.format(Locale.getDefault(),"Do you want to buy 10 marketing for %d$?",
                            DevelopmentValidator.calculateMarketingCost(myCompany.marketing)));
                }else{
                    Toast.makeText(getContext(),"You don't have enough money!",Toast.LENGTH_SHORT).show();
                    ll.setVisibility(View.GONE);
                }
            }
        });
        ImageButton cancel=ll.findViewById(R.id.cancelMarketingPurchase);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll.setVisibility(View.GONE);
            }
        });
    }

    private void buyNewSlot(View view){
        LinearLayout ll = view.getRootView().findViewById(R.id.buyNewSlotConfirmation);
        TextView textView=ll.findViewById(R.id.buyNewSlotConfirmationText);
        textView.setText(String.format(Locale.getDefault(),"Do you want to buy a new slot for %d$?",
                DevelopmentValidator.nextSlotCost(myCompany.maxSlots)) );
        ll.setVisibility(View.VISIBLE);
        ImageButton confirm=ll.findViewById(R.id.confirmNewSlotPurchase);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DevelopmentValidator.nextSlotCost(myCompany.maxSlots) !=-1 &&
                        myCompany.money>=DevelopmentValidator.nextSlotCost(myCompany.maxSlots) ){
                    myCompany.money-=DevelopmentValidator.nextSlotCost(myCompany.maxSlots);
                    myCompany.maxSlots+=1;
                    deviceViewModel.updateCompanies(myCompany);
                    textView.setText(String.format(Locale.getDefault(),"Do you want to buy a new slot for %d$?",
                            DevelopmentValidator.nextSlotCost(myCompany.maxSlots)) );
                }else{
                    Toast.makeText(getContext(),"You don't have enough money!",Toast.LENGTH_SHORT).show();
                    ll.setVisibility(View.GONE);
                }
            }
        });
        ImageButton cancel=ll.findViewById(R.id.cancelNewSlotPurchase);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll.setVisibility(View.GONE);
            }
        });
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
    }


}
