package com.brubaker.d308.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.brubaker.d308.R;
import com.brubaker.d308.database.Repository;
import com.brubaker.d308.entities.Excursion;
import com.brubaker.d308.entities.Vacation;
import com.brubaker.d308.receivers.VacationAlertReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {
    // UI components
    private EditText titleEditText;
    private Button dateButton;

    // Non UI
    private Date excursionDate;
    private Date vacationStartDate;
    private Date vacationEndDate;
    private int excursionId = -1;
    private int vacationId = -1;

    // Repository
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_excursion_details);

        // Handle system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize repository
        repository = new Repository(getApplication());

        // Retrieve passed data
        excursionId = getIntent().getIntExtra("excursionId", -1);
        vacationId = getIntent().getIntExtra("vacationId", -1);

        // Confirming that vacationId was received from VacationDetails
        // TODO: Remove this for production build
        if (vacationId == -1) {
            Toast.makeText(this, "Vacation ID is not available. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Retrieve and cache vacation dates
        repository.getVacationById(vacationId).observe(this, vacation -> {
            if (vacation != null) {
                vacationStartDate = vacation.getStart();
                vacationEndDate = vacation.getEnd();
            } else { // Confirming that vacation information was cached properly // TODO: Remove this for production build
                Toast.makeText(this, "Vacation dates are not available", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Link UI components
        titleEditText = findViewById(R.id.excursion_name_input);
        dateButton = findViewById(R.id.excursion_date_btn);

        // Load existing excursion data if editing an excursion
        if (excursionId != -1) {
            loadExcursionDetails();
        }

        // Date picker for excursion date
        dateButton.setOnClickListener(v -> showDatePicker());
    }

    // Method for loading excursion details
    private void loadExcursionDetails() {
        repository.getExcursionById(excursionId).observe(this, excursion -> {
            if (excursion != null) {
                titleEditText.setText(excursion.getTitle());
                excursionDate = excursion.getDate();
                updateDateButton(excursionDate);
            }
        });
    }

    // Show DatePickerDialog for selecting a date
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (excursionDate != null) {
            calendar.setTime(excursionDate);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);
            excursionDate = selectedDate.getTime();
            updateDateButton(excursionDate);
        }, year, month, day).show();
    }

    // Update the date button text
    private void updateDateButton(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        dateButton.setText(dateFormat.format(date));
    }

    // Validate excursion fields
    private boolean areFieldsValid() {
        if (titleEditText.getText().toString().isEmpty() || excursionDate == null) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (vacationStartDate == null || vacationEndDate == null) {
            Toast.makeText(this, "Vacation dates are not available.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isDateWithinRange(excursionDate, vacationStartDate, vacationEndDate)) {
            Toast.makeText(this, "Excursion date must be within the vacation dates.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Check if the date is within the vacation date range
    private boolean isDateWithinRange(Date date, Date start, Date end) {
        return !date.before(start) && !date.after(end);
    }

    // Menu setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.excursion_details_menu, menu);
        return true;
    }

    // Handle menu item selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_excursion) {
            saveExcursion();
            return true;
        } else if (id == R.id.delete_excursion) {
            deleteExcursion();
            return true;
        } else if (id == R.id.set_alarm) {
            setAlarmForExcursion();
            return true;
        } else if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Save or update the excursion
    private void saveExcursion() {
        if (!areFieldsValid()) return;

        String title = titleEditText.getText().toString();

        Excursion excursion = new Excursion(
                (excursionId == -1 ? 0 : excursionId), // Auto-generate ID for new excursions
                title,
                excursionDate,
                vacationId
        );

        if (excursionId == -1) { // New excursion
            repository.insertExcursion(excursion);
            Toast.makeText(this, "New excursion saved.", Toast.LENGTH_SHORT).show();
        } else { // Update existing excursion
            repository.updateExcursion(excursion);
            Toast.makeText(this, "Excursion updated.", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
//    private void saveExcursion() {
//        if (!areFieldsValid()) return;
//
//        String title = titleEditText.getText().toString();
//
//        // Set excursionId to 0 for new excursions to allow auto-generation
//        int idToSave = (excursionId == -1) ? 0 : excursionId;
//
//        Excursion excursion = new Excursion(idToSave, title, excursionDate, vacationId);
//
//        if (excursionId == -1) { // New excursion
//            repository.insertExcursion(excursion);
//            Toast.makeText(this, "New excursion saved.", Toast.LENGTH_SHORT).show();
//        } else { // Update existing excursion
//            repository.updateExcursion(excursion);
//            Toast.makeText(this, "Excursion updated.", Toast.LENGTH_SHORT).show();
//        }
//
//        finish();
//    }

    // Delete the excursion
    private void deleteExcursion() {
        if (excursionId != -1) {
            Log.d("ExcursionDetails", "Deleting excursion with ID: " + excursionId);
            Excursion excursion = new Excursion(excursionId, null, null, vacationId);
            repository.deleteExcursion(excursion);
            Toast.makeText(this, "Excursion deleted.", Toast.LENGTH_SHORT).show();
        }
    }


    // Set an alarm for the excursion
    private void setAlarmForExcursion() {
        if (excursionDate == null || titleEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please ensure all fields are filled before setting an alarm.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, VacationAlertReceiver.class);
        intent.putExtra("title", titleEditText.getText().toString());
        intent.putExtra("type", "Excursion");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, excursionDate.getTime(), pendingIntent);
        }

        Toast.makeText(this, "Alarm set for the excursion date.", Toast.LENGTH_SHORT).show();
    }
}