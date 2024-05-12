package com.example.medicalappointment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.SearchView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AppointmentListActivity extends AppCompatActivity {
    private static final String LOG_TAG = AppointmentListActivity.class.getName().toString();
    private static final int REQUEST_CALL_PHONE_PERMISSION = 1;

    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ArrayList<DoctorItem> mItemList;
    private DoctorItemAdapter mAdapter;
    private int gridNumber = 1;
    private boolean viewRow = true;
    private NotificationHandler mNotificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Log.d(LOG_TAG, "Bejelentkezett felhasználó.");
        } else {
            Log.d(LOG_TAG, "Nincs felhasználó bejelentkezve.");
            finish();
        }

        mRecyclerView = findViewById(R.id.recycleView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();

        mAdapter = new DoctorItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mAdapter);

        initializeData();

        mNotificationHandler = new NotificationHandler(this);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initializeData() {
        String[] itemsList = getResources().getStringArray(R.array.doctor_item_names);
        String[] itemsInfo = getResources().getStringArray(R.array.doctor_item_desc);
        String[] itemsPhone = getResources().getStringArray(R.array.doctor_item_phones);
        // TypedArray itemsImageResource; // getResources().obtainTypedArray(R.array.doctor_item_images);
        TypedArray itemsRate = getResources().obtainTypedArray(R.array.doctor_item_rates);

        mItemList.clear();

        for (int i = 0; i < itemsList.length; i++) {
            mItemList.add(new DoctorItem(itemsList[i], itemsInfo[i], itemsPhone[i], itemsRate.getFloat(i, 0)));
            // Log.i(LOG_TAG, i + ". data");
        }

        mAdapter.notifyDataSetChanged();
        Log.i(LOG_TAG, "Data initialized!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.doctor_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.log_out_button) {
            Log.d(LOG_TAG, "Logout clicked!");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        } else if (itemId == R.id.settings_button) {
            Log.d(LOG_TAG, "Setting clicked!");
            return true;
        } else if (itemId == R.id.view_selector) {
            if (viewRow) {
                changeSpanCount(item, R.drawable.view_grid, 1);
            } else {
                changeSpanCount(item, R.drawable.view_row, 2);
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    public void call(View view) {
        initiateCall();
    }

    private void initiateCall(){
        if (ActivityCompat.checkSelfPermission(AppointmentListActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            String phoneNumber = getResources().getStringArray(R.array.doctor_item_phones)[0];

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(AppointmentListActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CALL_PHONE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initiateCall();
            } else {
                Toast.makeText(AppointmentListActivity.this, "Hívás engedély megtagadva", Toast.LENGTH_SHORT).show();
            }
        }
    }
}