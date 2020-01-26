package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//TODO sort by

public class allCompanies extends AppCompatActivity {
    private DeviceViewModel deviceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_companies);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.companiesRecyclerView);


            // Get a new or existing ViewModel from the ViewModelProvider.
            deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
            LiveData<List<Company>> companies = deviceViewModel.getAllCompanies();

            //onclick
            View.OnClickListener mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //number of clicked item
                    int itemPosition = recyclerView.getChildLayoutPosition(v);

                    //get device fields
                    String nev = Objects.requireNonNull(companies.getValue()).get(itemPosition).name;
                    int id = companies.getValue().get(itemPosition).companyId;
                    int money = companies.getValue().get(itemPosition).money;

                    //make intent
                    Intent intent = new Intent();
                    intent.putExtra(MainActivity.NAME_FIELD, nev);
                    intent.putExtra(MainActivity.MAIN_MONETARIAL_INFO, money);
                    intent.putExtra("ID", id);
                    intent.setClass(getBaseContext(), DetailsOfOneCompany.class);

                    //start new activity
                    startActivityForResult(intent, MainActivity.DISPLAY_COMPANIES_REQUEST_CODE);
                }
            };
            final CompanyListAdapter adapter = new CompanyListAdapter(this, mOnClickListener);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));


            // Add an observer on the LiveData returned by getAlphabetizedWords.
            // The onChanged() method fires when the observed data changes and the activity is
            // in the foreground.

            deviceViewModel.getAllCompanies().observe(this, new Observer<List<Company>>() {
                @Override
                public void onChanged(@Nullable final List<Company> comps) {
                    // Update the cached copy of the words in the adapter.
                    adapter.setCompanies(comps);
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuitem_deleteALL) {
            deviceViewModel.deleteAll();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data.getBooleanExtra("IS_DELETE",false)) {
            deviceViewModel.delOneCompanyById(data.getIntExtra("ID",-1));
            Toast.makeText(getApplicationContext(), "SIKERULT torolni", Toast.LENGTH_LONG).show();}
    }

}
