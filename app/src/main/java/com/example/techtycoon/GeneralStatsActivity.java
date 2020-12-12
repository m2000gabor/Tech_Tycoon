package com.example.techtycoon;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class GeneralStatsActivity extends AppCompatActivity {
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_stats);

        linearLayout=findViewById(R.id.generalStatsLinearLayout);

        ArrayList<String> lables=getIntent().getStringArrayListExtra(FragmentAllCompanies.GENERAL_STATS_LABELS);
        ArrayList<String> values=getIntent().getStringArrayListExtra(FragmentAllCompanies.GENERAL_STATS_VALUES);

        for (int i=0;i<lables.size();i++) {
            //TextView tw = findViewById(textViewIDs[i]);
            String text=String.format(Locale.getDefault(),"%s: %s",
                    lables.get(i), values.get(i));
            //TextView tw=new TextView(linearLayout.getContext(),);
            //tw.setText();

            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin=(int) getResources().getDimension(R.dimen.text_margin);
            lparams.setMargins(margin,margin,margin,margin);
            TextView rowTextView = (TextView) getLayoutInflater().inflate(R.layout.general_stats_activity_list_item, null);
            rowTextView.setLayoutParams(lparams);
            rowTextView.setText(text);
            linearLayout.addView(rowTextView);

        }
    }


}
