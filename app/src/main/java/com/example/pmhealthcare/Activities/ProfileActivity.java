package com.example.pmhealthcare.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pmhealthcare.Fragments.ProfileFragment;
import com.example.pmhealthcare.R;
import com.example.pmhealthcare.Utils.JsonParser;
import com.example.pmhealthcare.database.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = "Profile Activity";
    ScrollView editModeScroll, viewScrollMode;
    CircleImageView userDp;

    //For Edit Mode
    TextInputEditText nameField, heightField, weightField, fatherNameField, motherNameField, pinCodeField, addressField;
    Spinner yearSpinner, monthSpinner, dateSpinner, stateSpinner, districtSpinner;
    MaterialButton saveButton;
    RadioButton maleRadio, femaleRadio, othersRadio;

    TextView genderErrorLabel;
    CheckBox[] specialDiseaseCheckboxes;


    String[] years = new String[122];
    String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUNE", "JULY", "AUG", "SEPT", "OCT", "NOV", "DEC"};
    String[] dates;

    String[] statesArray;
    String[] districtsArray;

    //For View Mode
    TextView nameTextField, dobTextField, heightTextField, weightTextField, genderTextField;
    TextView fatherNameTextField, motherNameTextField;
    TextView stateTextField, districtTextField, pinCodeTextField, addressTextField;
    TextView[] specialDiseasesTextFields;


    /* For both Modes */
    //name
    String userName;

    //date of birth
    int year = 2021;
    int month = 0;
    int date = 0;
    String DOB;

    //height and weight
    int height;
    int weight;

    //gender
    String gender;

    //father and mother name
    String fatherName;
    String motherName;

    //state and district name
    int statePosition=0;
    int districtPosition=0;

    //pin code and address
    int pinCode;
    String address;

    //special diseases
    String specialDiseases;
    int[] diseasesArray = {0, 0, 0, 0, 0, 0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editModeScroll = findViewById(R.id.edit_mode_scroll);
        viewScrollMode = findViewById(R.id.view_mode_scroll);
        userDp=findViewById(R.id.user_profile_section_dp);

        Intent intent = getIntent();

        getUserInformation();

        if (intent.getIntExtra(ProfileFragment.USER_DETAILS_MODE_KEY, -1) == 0) {
            editModeScroll.setVisibility(View.VISIBLE);
            viewScrollMode.setVisibility(View.GONE);

            InitEditModeUIElements();
            setEditModeUserInformation();
        }
        else if (intent.getIntExtra(ProfileFragment.USER_DETAILS_MODE_KEY, -1) == 1) {
            editModeScroll.setVisibility(View.GONE);
            viewScrollMode.setVisibility(View.VISIBLE);

            InitViewModeUIElements();
            setViewModeUserInformation();
        }

    }

    /**
     * ==================================== METHOD FOR INITIALIZE EDIT MODE UI ELEMENTS ===============================
     **/
    public void InitEditModeUIElements() {

        nameField = findViewById(R.id.name_field);
        heightField = findViewById(R.id.height_field);
        weightField = findViewById(R.id.weight_field);

        yearSpinner = findViewById(R.id.year_spinner);
        monthSpinner = findViewById(R.id.month_spinner);
        dateSpinner = findViewById(R.id.date_spinner);

        genderErrorLabel = findViewById(R.id.gender_error_label);
        maleRadio = findViewById(R.id.male_radio);
        femaleRadio = findViewById(R.id.female_radio);
        othersRadio = findViewById(R.id.others_radio);

        fatherNameField = findViewById(R.id.father_name_field);
        motherNameField = findViewById(R.id.mother_name_field);

        stateSpinner = findViewById(R.id.state_spinner);
        districtSpinner = findViewById(R.id.district_spinner);


        pinCodeField = findViewById(R.id.pincode_edit_field);
        addressField = findViewById(R.id.full_address);

        specialDiseaseCheckboxes = new CheckBox[7];
        specialDiseaseCheckboxes[0] = findViewById(R.id.weak_eyesight_checkbox);
        specialDiseaseCheckboxes[1] = findViewById(R.id.diabetic_checkbox);
        specialDiseaseCheckboxes[2] = findViewById(R.id.respiratory_disease_checkbox);
        specialDiseaseCheckboxes[3] = findViewById(R.id.alzheimer_checkbox);
        specialDiseaseCheckboxes[4] = findViewById(R.id.heart_disease_checkbox);
        specialDiseaseCheckboxes[5] = findViewById(R.id.overweight_checkbox);
        specialDiseaseCheckboxes[6] = findViewById(R.id.handicapped_checkbox);

        saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(this);

    }

    /**
     * ========================================== METHODS FOR DOB SPINNERS =========================================
     **/
    public void setDateSpinners(int year, int month, int date) {

        for (int i = 0; i <= 121; i++) {
            years[i] = String.valueOf(2021 - i);
        }
        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);
        yearSpinner.setAdapter(yearsAdapter);
        yearSpinner.setSelection(2021 - year);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setSelection(month);

        yearSpinner.setOnItemSelectedListener(this);
        monthSpinner.setOnItemSelectedListener(this);
        dateSpinner.setOnItemSelectedListener(this);

        this.date = date - 1;
    }

    public void setGenderRadios(String gender) {

        switch (gender) {
            case "Male":
                maleRadio.setChecked(true);
                break;
            case "Female":
                femaleRadio.setChecked(true);
                break;
            case "Others":
                othersRadio.setChecked(true);
                break;
        }

    }

    /**
     * ==================================== METHODS FOR STATE AND DISTRICTS SPINNERS ================================
     **/
    public void setStateSpinner(int state, int district) {

        ArrayAdapter<String> statesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statesArray);
        stateSpinner.setAdapter(statesAdapter);

        stateSpinner.setOnItemSelectedListener(this);
        stateSpinner.setSelection(state);

        this.districtPosition = district;

    }

    public void setSpecialDiseaseCheckboxes(int[] diseases) {

        for (int i = 0; i < 7; i++)
            if (diseases[i] == 1)
                specialDiseaseCheckboxes[i].setChecked(true);
    }

    /**
     * ========================================== SPINNERS ON ITEM SELECTED =========================================
     **/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent == monthSpinner || parent == yearSpinner) {
            int number = monthSpinner.getSelectedItemPosition();

            switch (months[number]) {
                case "JAN":
                case "MAR":
                case "MAY":
                case "JULY":
                case "AUG":
                case "OCT":
                case "DEC":
                    number = 31;
                    break;
                case "APR":
                case "JUNE":
                case "SEPT":
                case "NOV":
                    number = 30;
                    break;
                case "FEB":
                    number = Integer.parseInt(yearSpinner.getSelectedItem().toString());
                    if (number % 100 == 0 && number % 400 == 0)
                        number = 29;
                    else if (number % 4 == 0)
                        number = 29;
                    else number = 28;

                    break;
            }

            dates = new String[number];
            for (int i = 0; i < number; i++)
                dates[i] = String.valueOf(i + 1);

            ArrayAdapter<String> datesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dates);
            dateSpinner.setAdapter(datesAdapter);
            dateSpinner.setSelection(this.date);

        } else if (parent == stateSpinner) {

            ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, districtsArray);
            districtSpinner.setAdapter(districtAdapter);
            districtSpinner.setSelection(this.districtPosition);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (v == saveButton) {

            boolean allClear = true;

            if (nameField.getText().toString().equals("")) {
                allClear = false;
                nameField.setError("Required");
            } else nameField.setError(null);

            if (!(maleRadio.isChecked() || femaleRadio.isChecked() || othersRadio.isChecked())) {
                allClear = false;
                genderErrorLabel.setVisibility(View.VISIBLE);
            } else genderErrorLabel.setVisibility(View.GONE);

            if (fatherNameField.getText().toString().equals("")) {
                allClear = false;
                fatherNameField.setError("Required");
            } else fatherNameField.setError(null);

            if (motherNameField.getText().toString().equals("")) {
                allClear = false;
                motherNameField.setError("Required");
            } else motherNameField.setError(null);

            if (pinCodeField.getText().toString().equals("")) {
                allClear = false;
                pinCodeField.setError("Required");
            } else if (pinCodeField.getText().toString().length() < 6) {
                allClear = false;
                pinCodeField.setError("Invalid PinCode");
            } else
                pinCodeField.setError(null);

            if (addressField.getText().toString().equals("")) {
                allClear = false;
                addressField.setError("Required");
            } else
                addressField.setError(null);

            if (allClear)
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Save")
                        .setMessage("Do you want to Save current information")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            saveUserInformation();
                            finish();
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .show();
        }
    }

    public void saveUserInformation() {

        User.setName(this, nameField.getText().toString());
        User.setHeight(this, Integer.parseInt(heightField.getText().toString()));
        User.setWeight(this, Integer.parseInt(weightField.getText().toString()));
        User.setDOB(this, yearSpinner, monthSpinner, dateSpinner);
        User.setGender(this, maleRadio, femaleRadio, othersRadio);

        User.setFatherName(this, fatherNameField.getText().toString());
        User.setMotherName(this, motherNameField.getText().toString());

        User.setState(this, stateSpinner.getSelectedItemPosition());
        User.setDistrict(this, districtSpinner.getSelectedItemPosition());
        User.setPinCode(this, Integer.parseInt(pinCodeField.getText().toString()));
        User.setAddress(this, addressField.getText().toString());

        User.setSpecialDiseases(this, specialDiseaseCheckboxes);

        User.setDoctor(this, false);

        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

    }

    public void getUserInformation() {

        //Profile Pic
        Uri imageUri=User.getUserDp(this);
        userDp.setImageURI(imageUri);

        //personal details
        userName = User.getName(this);

        //date of birth
        year = 2021;
        month = 0;
        date = 0;
        DOB = User.getDOB(this);

        //converting date into readable format
        if (!TextUtils.isEmpty(DOB)) {
            String[] format = DOB.split("-");

            year = Integer.parseInt(format[2]);
            month = Integer.parseInt(format[1]);
            date = Integer.parseInt(format[0]);
        }

        //height and weight
        height = User.getHeight(this);
        weight = User.getWeight(this);

        //gender
        gender = User.getGender(this);

        //family details
        fatherName = User.getFatherName(this);
        motherName = User.getMotherName(this);


        //state and district
        statePosition = User.getState(this);
        statesArray = JsonParser.getStatesFromJSON(this);

        districtPosition = User.getDistrict(this);
        districtsArray = JsonParser.getDistrictsFromJSON(this, statesArray[statePosition]);

        //pinCode and address
        pinCode = User.getPinCode(this);
        address = User.getAddress(this);

        //special diseases
        specialDiseases = User.getSpecialDiseases(this);

        //breaking array to into readable format
        for (int i = 0; i < 7; i++)
            diseasesArray[i] = specialDiseases.charAt(i) - 48;
    }

    /**=================================== METHOD FOR SETTING USER INFO IN EDIT MODE UI =====================================**/
    public void setEditModeUserInformation(){
        nameField.setText(userName);

        if (height != 0)
            heightField.setText(height + "");
        if (weight != 0)
            weightField.setText(weight + "");

        setDateSpinners(year, month, date);
        setGenderRadios(gender);

        fatherNameField.setText(fatherName);
        motherNameField.setText(motherName);

        setStateSpinner(statePosition, districtPosition);

        if (pinCode != 0)
            pinCodeField.setText(pinCode + "");

        addressField.setText(address);

        setSpecialDiseaseCheckboxes(diseasesArray);
    }

    /**
     * ==================================== METHOD FOR INITIALIZE EDIT MODE UI ELEMENTS ===============================
     **/
    public void InitViewModeUIElements() {

        nameTextField = findViewById(R.id.name_text_field);
        dobTextField = findViewById(R.id.dob_text_field);
        heightTextField = findViewById(R.id.height_text_field);
        weightTextField = findViewById(R.id.weight_text_field);
        genderTextField = findViewById(R.id.gender_text_field);
        fatherNameTextField = findViewById(R.id.father_name_text_field);
        motherNameTextField = findViewById(R.id.mother_name_text_field);
        stateTextField = findViewById(R.id.state_text_field);
        districtTextField = findViewById(R.id.district_text_field);
        pinCodeTextField = findViewById(R.id.pincode_text_field);
        addressTextField = findViewById(R.id.address_text_field);

        specialDiseasesTextFields=new TextView[8];
        specialDiseasesTextFields[0]=findViewById(R.id.no_special_disease_text_view);
        specialDiseasesTextFields[1]=findViewById(R.id.weak_eyesight_text_field);
        specialDiseasesTextFields[2]=findViewById(R.id.diabetic_text_field);
        specialDiseasesTextFields[3]=findViewById(R.id.respiratory_text_field);
        specialDiseasesTextFields[4]=findViewById(R.id.alzheimer_text_field);
        specialDiseasesTextFields[5]=findViewById(R.id.heart_text_field);
        specialDiseasesTextFields[6]=findViewById(R.id.overweight_text_field);
        specialDiseasesTextFields[7]=findViewById(R.id.handicapped_text_field);


    }

    public void setViewModeUserInformation(){

        if(!userName.equals("")) {
            nameTextField.setText(userName);
            heightTextField.setText(height + " cm");
            weightTextField.setText(weight + " kg");

            dobTextField.setText(this.date + "-" + (this.month + 1) + "-" + this.year);
            genderTextField.setText(gender);

            fatherNameTextField.setText(fatherName);
            motherNameTextField.setText(motherName);

            stateTextField.setText(statesArray[statePosition]);
            districtTextField.setText(districtsArray[districtPosition]);

            pinCodeTextField.setText(pinCode + "");
            addressTextField.setText(address);

            boolean hasSpecialDisease=false;
            for(int i=0;i<7;i++){
                if(diseasesArray[i]==1)
                {
                    specialDiseasesTextFields[i+1].setVisibility(View.VISIBLE);
                    hasSpecialDisease=true;
                }
                else
                    specialDiseasesTextFields[i+1].setVisibility(View.GONE);
            }
            if(hasSpecialDisease)specialDiseasesTextFields[0].setVisibility(View.GONE);
            else specialDiseasesTextFields[0].setVisibility(View.VISIBLE);

        }
        else {

            nameTextField.setText("------");
            heightTextField.setText("------");
            weightTextField.setText("------");

            dobTextField.setText("------");
            genderTextField.setText("------");

            fatherNameTextField.setText("------");
            motherNameTextField.setText("------");

            stateTextField.setText("------");
            districtTextField.setText("------");

            pinCodeTextField.setText("------");
            addressTextField.setText("------");

            specialDiseasesTextFields[0].setText("------");

        }
    }
}