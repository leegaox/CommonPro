package com.biu.mvp.function.test;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.biu.modulebase.common.util.PermissionUtil;
import com.biu.modulebase.common.util.SnackBarBuilder;
import com.biu.mvp.R;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.biu.modulebase.common.util.PermissionUtil.REQUEST_CAMERA;
import static com.biu.modulebase.common.util.PermissionUtil.REQUEST_CONTACTS;
import static com.biu.modulebase.common.util.PermissionUtil.REQUEST_PERMISSIONS;

public class PermissionActivity extends AppCompatActivity {

    public static final String TAG = "PermissionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        findViewById(R.id.requestContacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestContacts(Manifest.permission.READ_CONTACTS);
            }
        });
        findViewById(R.id.requestCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCamera(Manifest.permission.CAMERA);
            }
        });
        findViewById(R.id.requestPermissions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,WRITE_EXTERNAL_STORAGE,READ_CALENDAR,SEND_SMS,CALL_PHONE});
            }
        });
    }

    private void requestCamera(String permission) {
        if (!PermissionUtil.checkSelfPermission(this, permission)) {
            PermissionUtil.doRequest(this, permission, REQUEST_CAMERA);
        } else {

        }
    }


    private void requestContacts(String permission) {
        if (!PermissionUtil.checkSelfPermission(this, permission)) {
            PermissionUtil.doRequest(this, permission, REQUEST_CONTACTS);
        } else {

        }

    }

    private void requestPermissions(String []permissions){
        PermissionUtil.requestPermissions(this,permissions,REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                // Check if the only required permission has been granted
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Request Camera Success", Toast.LENGTH_SHORT).show();
                } else {
                    if (PermissionUtil.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        new SnackBarBuilder(this, getString(R.string.permission_not_granted), Snackbar.LENGTH_LONG).setBackgroundColor(R.color.white).show();
                    } else {
                        PermissionUtil.showAppSettingsSnackBar(PermissionActivity.this, REQUEST_CAMERA);
                    }
                    Log.i(TAG, "CAMERA permission was NOT granted.");

                }
                break;
            case REQUEST_CONTACTS:
                // We have requested multiple permissions for contacts, so all of them need to be checked.
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    // All required permissions have been granted, do next.
                    Toast.makeText(getApplicationContext(), "Request Contacts Success", Toast.LENGTH_SHORT).show();
                } else {
                    if (PermissionUtil.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                        new SnackBarBuilder(this, getString(R.string.permission_not_granted), Snackbar.LENGTH_LONG).setBackgroundColor(R.color.white).show();
                    } else {
                        PermissionUtil.showAppSettingsSnackBar(this, REQUEST_CONTACTS);
                    }
                    Log.i(TAG, "Contacts permissions were NOT granted.");

                }
                break;
            case  REQUEST_PERMISSIONS:
                // We have requested multiple permissions for contacts, so all of them need to be checked.
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    // All required permissions have been granted, do next.
                    Toast.makeText(getApplicationContext(), "Request Contacts Success", Toast.LENGTH_SHORT).show();
                } else {
                    //show Dialog
                    PermissionUtil.requestPermissions(this,permissions,PermissionUtil.REQUEST_PERMISSIONS);

                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }
}
