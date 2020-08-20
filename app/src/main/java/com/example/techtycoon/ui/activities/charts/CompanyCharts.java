package com.example.techtycoon.ui.activities.charts;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.techtycoon.Company;
import com.example.techtycoon.Device;
import com.example.techtycoon.DeviceViewModel;
import com.example.techtycoon.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class CompanyCharts extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    List<Company> companyList;
    AnyChartView anyChartView;
    Cartesian cartesian;
    Column column;
    List<String> statAttributes=new ArrayList<>(Arrays.asList("Profit","Value","Max slots","Marketing",
            "Storage","Body"));

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_company_chart);

            anyChartView = findViewById(R.id.any_chart_view);
            anyChartView.setProgressBar(findViewById(R.id.progress_bar));


            //set up a spinner
            Spinner spinner = findViewById(R.id.companyStatSpinner);
            // Create an ArrayAdapter using the string array and a default spinner layout

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,statAttributes);
            // Specify the layout to use when the list of choices appears
            //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);

            //get the data
            DeviceViewModel deviceViewModel =  new ViewModelProvider(this).get(DeviceViewModel.class);
            companyList=deviceViewModel.getAllCompaniesList();

            List<DataEntry> data = new ArrayList<>();
            for(Company c : companyList){
                data.add(new ValueDataEntry(c.name,c.lastProfit));
            }
            /*
            pie = AnyChart.pie();
            pie.data(data);
            pie.title(statAttributes.get(0));
            pie.labels().position("outside");

            pie.legend()
                    .position("center-bottom")
                    .itemsLayout(LegendLayout.HORIZONTAL_EXPANDABLE)
                    .align(Align.CENTER);

            anyChartView.setChart(pie);*/
            cartesian = AnyChart.column();
            column = cartesian.column(data);

            column.tooltip()
                    .titleFormat("{%X}")
                    .position(Position.CENTER_BOTTOM)
                    .anchor(Anchor.CENTER_BOTTOM)
                    .offsetX(0d)
                    .offsetY(5d)
                    .format("${%Value}{groupsSeparator: }");

            cartesian.animation(true);
            cartesian.title(statAttributes.get(0));

            cartesian.legend(true);
            cartesian.legend()
                    .position("center-bottom")
                    .itemsLayout(LegendLayout.HORIZONTAL_EXPANDABLE)
                    .align(Align.CENTER);

            cartesian.yScale().minimum(0d);

            cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
            cartesian.interactivity().hoverMode(HoverMode.BY_X);

            cartesian.xAxis(0).title("Company");
            cartesian.yAxis(0).title(statAttributes.get(0));

            cartesian.legend(true);
            cartesian.legend()
                    .position("center-bottom")
                    .itemsLayout(LegendLayout.HORIZONTAL_EXPANDABLE)
                    .align(Align.CENTER);

            anyChartView.setChart(cartesian);
        }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        List<DataEntry> data = new ArrayList<>();
        List<List<DataEntry>> multipleDataList = new ArrayList<>();
        String chartName=statAttributes.get(position);
        List<String> seriesNames=new ArrayList<>();
        switch (position){
            default:
                for(Company c : companyList){
                    data.add(new ValueDataEntry(c.name,c.lastProfit));
                }
                break;
            case 1:
                for(Company c : companyList){
                    data.add(new ValueDataEntry(c.name,c.getMarketValue()));
                }
                break;
            case 2:
                for(Company c : companyList){
                    data.add(new ValueDataEntry(c.name,c.maxSlots));
                }
                break;
            case 3:
                for (Company c:companyList){
                    data.add(new ValueDataEntry(c.name,c.marketing));
                }
                break;
            case 4://storage
                for (Device.DeviceAttribute att : Device.getStorageAttributes()){
                    List<DataEntry> dataEntries=new ArrayList<>();
                    for(Company c:companyList){
                        dataEntries.add(new ValueDataEntry(c.name,c.getLevelByAttribute(att)));
                    }
                    multipleDataList.add(dataEntries);
                    seriesNames.add(Device.attributeToString(att));
                }
                break;
            case 5: //body
                for (Device.DeviceAttribute att : Device.getBodyAttributes()){
                    List<DataEntry> dataEntries=new ArrayList<>();
                    for(Company c:companyList){
                        dataEntries.add(new ValueDataEntry(c.name,c.getLevelByAttribute(att)));
                    }
                    multipleDataList.add(dataEntries);
                    seriesNames.add(Device.attributeToString(att));
                }
                break;
        }
        if(position>3){
            plotTheChart_multipleColumn(multipleDataList,chartName,seriesNames);
        }else {
            plotTheChart(data, chartName);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void plotTheChart(List<DataEntry> data,String chartName){
            cartesian.removeAllSeries();
        column = cartesian.column(data);
        column.name(chartName);
        cartesian.title(chartName);
        cartesian.xAxis(0).title("Company");
        cartesian.yAxis(0).title(chartName);
        if(chartName.equals("Income") || chartName.equals("Value")){
            cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");
        }else{
            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");
        }
        /*
        pie.data(data);
        pie.title(chartName);*/

    }

    public void plotTheChart_multipleColumn(List<List<DataEntry>> data,String chartName,List<String> seriesNames){
        cartesian.removeAllSeries();
        for (int i=0;i<data.size();i++){
            column = cartesian.column(data.get(i));
            column.name(seriesNames.get(i));
        }
        cartesian.title(chartName);
        cartesian.xAxis(0).title("Company");
        cartesian.yAxis(0).title(chartName);
        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

    }
}


