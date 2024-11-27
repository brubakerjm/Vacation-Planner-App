package com.brubaker.d308.UI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.brubaker.d308.R;

public class ExcursionDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_excursion_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Initiate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.excursion_details_menu, menu);
        return true;
    }

    // Menu options selection logic
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_excursion) {
            Toast.makeText(ExcursionDetails.this, "Implement save excursion method", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.delete_excursion) {
            Toast.makeText(ExcursionDetails.this, "Implement delete excursion method", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.set_alarm) {
            Toast.makeText(ExcursionDetails.this, "Implement set alarm method", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.share_excursion) {
            Toast.makeText(ExcursionDetails.this, "Implement set share excursion method", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }
}