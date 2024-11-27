package com.brubaker.d308.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.brubaker.d308.R;
import com.brubaker.d308.database.Repository;
import com.brubaker.d308.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class VacationDetails extends AppCompatActivity {

    // UI
    private EditText vacationTitle;
    private EditText vacationHotel;
    private Button startDateButton;
    private Button endDateButton;

    // Non-UI
    private Date startDate;
    private Date endDate;
    private int vacationId = -1;

    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieving vacationId from VacationList if the user selected an existing vacation
        if (getIntent().hasExtra("vacationId")) {
            vacationId = getIntent().getIntExtra("vacationId", -1);
        }

        // Link UI components
        vacationTitle = findViewById(R.id.vacation_title_input);
        vacationHotel = findViewById(R.id.vacation_hotel_input);
        startDateButton = findViewById(R.id.start_date_btn);
        endDateButton = findViewById(R.id.end_date_btn);

        // Initialize Repository
        repository = new Repository(getApplication());

        // Start date selection
        startDateButton.setOnClickListener(v -> showDatePicker(startDate, startDateButton, true));

        // End date selection
        endDateButton.setOnClickListener(v -> showDatePicker(startDate, endDateButton, false));

        // Transition from VacationDetails to ExcursionDetails via vacation_details_fab
        FloatingActionButton fab = findViewById(R.id.vacation_details_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                startActivity(intent);
            }
        });
    }

    // Inflating vacation_details_menu for Save, Update, Share, Delete vacation options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vacation_details_menu, menu);
        return true;
    }

    // Menu options selection logic
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.save_vacation) {
            saveVacation();
            return true;
        } else if (itemId == R.id.delete_vacation) {
            deleteVacation();
            return true;
        } else if (itemId == R.id.set_alarm) {
            setAlarms();
            return true;
        } else if (itemId == R.id.share_vacation) {
            shareVacation();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    // Menu options methods
    private void saveVacation() {
        // Collecting user input
        String title = vacationTitle.getText().toString();
        String hotel = vacationHotel.getText().toString();

        // Validate input
        if (title.isEmpty() || startDate == null || endDate == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endDate.before(startDate)) {
            Toast.makeText(this, "The end date cannot before the start date", Toast.LENGTH_SHORT).show();
        }

        // Creating the vacation object
        Vacation vacation = new Vacation(vacationId, title, hotel, startDate, endDate);

        // Save the vacation object
        if (vacationId == -1) {
            repository.insertVacation(vacation); // New vacation
            Toast.makeText(this, "New vacation saved", Toast.LENGTH_SHORT).show();
        } else {
            repository.updateVacation(vacation); // Existing vacation
            Toast.makeText(this, "Vacation updated", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteVacation() {
        Toast.makeText(VacationDetails.this, "Delete Vacation button pressed", Toast.LENGTH_SHORT).show();
    }

    private void setAlarms() {
        Toast.makeText(VacationDetails.this, "Set Alarms button pressed", Toast.LENGTH_SHORT).show();
    }

    private void shareVacation() {
        Toast.makeText(VacationDetails.this, "Share Vacation button pressed", Toast.LENGTH_SHORT).show();
    }

    // Date picker methods for start and end date buttons
    private void showDatePicker(Date currentDate, Button buttonToUpdate, boolean isStartDate) {
        // Step 1: Use currentDate for default date, or fallback to today's date
        Calendar calendar = Calendar.getInstance();
        if (currentDate != null) {
            calendar.setTime(currentDate); // Use existing date
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Step 2: Create the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    // Step 3: Update the selected date
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, month1, dayOfMonth);
                    Date newDate = selectedDate.getTime();

                    // Update the appropriate field
                    if (isStartDate) {
                        startDate = newDate;
                    } else {
                        endDate = newDate;
                    }

                    // Step 4: Update the button text
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
                    buttonToUpdate.setText(dateFormat.format(newDate));
                }, year, month, day);

        // Step 5: Show the dialog
        datePickerDialog.show();
    }

}