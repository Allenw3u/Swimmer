package allenw3u.swimmer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import allenw3u.swimmer.data.dataContact;

/**
 * Created by Allenw3u on 2017/5/25.
 * Developer Mode . this activity is for developer to input train data imformation ,such as name,swim style,pool distance
 */

public class InfoActivity extends AppCompatActivity{

    /** EditText field to enter user name */
    private EditText mUsernameEditText;

    /** Spinner field to enter swim style */
    private Spinner mSwimStyleSpinner;

    /** Spinner field to enter lap distance */
    private Spinner mLapDistanceSpinner;

    /** Button to go next */
    private Button mNextButton;

    /** Swim Style of swimmer */
    private String mSwimStyle = dataContact.STYLE_UNKNOWN;

    /** Lap distance of that data of swimmer */
    private String mLapDistance = dataContact.LAP_UNKNOW;

    /** User name of editText */
    private String mUserName = dataContact.NO_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //Find all relevant views that we will need to  read user input from
        mUsernameEditText = (EditText)findViewById(R.id.username_input);
        mSwimStyleSpinner = (Spinner)findViewById(R.id.spinner_swimStyle);
        mLapDistanceSpinner = (Spinner)findViewById(R.id.spinner_lapDistance);

        //Find button to go next
        mNextButton = (Button)findViewById(R.id.button_info_next) ;
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Read from input fields
                // Use trim to eliminate leading or trailing white space
                mUserName = mUsernameEditText.getText().toString().trim();
                if (mUserName.isEmpty()){
                    mUserName = dataContact.NO_NAME;
                }
                //initial a Intent object
                Intent intent = new Intent(InfoActivity.this,SensorActivity.class);
                //put key:values to Intent object String[]
                String Infodata[] = {mUserName,mSwimStyle,mLapDistance};
                intent.putExtra("infodata",Infodata);
                startActivity(intent);
            }
        });

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the swim style and lap distance
     */
    private void setupSpinner(){
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter swimstyleSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_swimstyle_options, android.R.layout.simple_spinner_item);
        ArrayAdapter lapswimSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_lapdistance_options,android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        swimstyleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        lapswimSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSwimStyleSpinner.setAdapter(swimstyleSpinnerAdapter);
        mLapDistanceSpinner.setAdapter(lapswimSpinnerAdapter);

        // Set the integer mSwimStyle to the constant values
        mSwimStyleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String)parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals(getString(R.string.freestyle))){
                        mSwimStyle = dataContact.STYLE_FREESTYLE;
                    }else if (selection.equals(getString(R.string.breaststroke))){
                        mSwimStyle = dataContact.STYLE_BREASTSTROKE;
                    }else if (selection.equals(getString(R.string.butterfly))){
                        mSwimStyle = dataContact.STYLE_BUTTERFLY;
                    }else if (selection.equals(getString(R.string.backstroke))){
                        mSwimStyle = dataContact.STYLE_BACKSTROKE;
                    }else {
                        mSwimStyle = dataContact.STYLE_UNKNOWN;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSwimStyle = dataContact.STYLE_UNKNOWN;
            }
        });

        // Set the integer mLapDistance to the constant values
        mLapDistanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String)parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals(getString(R.string.meter25))){
                        mLapDistance = dataContact.LAP_25M;
                    }else if (selection.equals(getString(R.string.meter50))){
                        mLapDistance = dataContact.LAP_50M;
                    }else {
                        mLapDistance = dataContact.LAP_UNKNOW;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mLapDistance = dataContact.LAP_UNKNOW;
            }
        });
    }
}
