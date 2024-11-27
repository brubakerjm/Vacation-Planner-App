package com.brubaker.d308.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.brubaker.d308.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class VacationDetails extends AppCompatActivity {

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



    // Initiate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vacation_details_menu, menu);
        return true;
    }

    // Menu options selection logic
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_vacation) {
            Toast.makeText(VacationDetails.this, "Implement save vacation method", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.delete_vacation) {
            Toast.makeText(VacationDetails.this, "Implement delete vacation method", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.set_alarm) {
            Toast.makeText(VacationDetails.this, "Implement set alarm method", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.share_vacation) {
            Toast.makeText(VacationDetails.this, "Implement share method", Toast.LENGTH_LONG).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}