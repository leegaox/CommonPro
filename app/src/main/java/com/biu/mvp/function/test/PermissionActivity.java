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
import com.biu.mvp.R;

import static com.biu.modulebase.common.util.PermissionUtil.REQUEST_CAMERA;
import static com.biu.modulebase.common.util.PermissionUtil.REQUEST_CONTACTS;

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:

                // Check if the only required permission has been granted
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "相机权限请求成功", Toast.LENGTH_SHORT).show();
                } else {
                    if (PermissionUtil.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        Snackbar.make(findViewById(R.id.requestCamera), "相机权限被拒", Snackbar.LENGTH_SHORT).show();
                    } else {
                        PermissionUtil.showAppSettingsSnackBar(PermissionActivity.this, permissions[0]);
                    }
                    Log.i(TAG, "CAMERA permission was NOT granted.");

                }
                break;
            case REQUEST_CONTACTS:
                // We have requested multiple permissions for contacts, so all of them need to be checked.
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    // All required permissions have been granted, do next.
                    Toast.makeText(getApplicationContext(), "REQUEST_CONTACTS请求成功", Toast.LENGTH_SHORT).show();
                } else {
                    if (PermissionUtil.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                        Snackbar.make(findViewById(R.id.requestCamera), "联系人权限被拒", Snackbar.LENGTH_SHORT).show();
                    } else {
                        PermissionUtil.showAppSettingsSnackBar(this, permissions[0]);
                    }
                    Log.i(TAG, "Contacts permissions were NOT granted.");

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }
}
