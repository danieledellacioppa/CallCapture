package com.example.callcapture;

//import static android.os.UserManager.DISALLOW_BLUETOOTH;
//import static android.os.UserManager.DISALLOW_CHANGE_WIFI_STATE;

import static android.os.UserManager.DISALLOW_BLUETOOTH;
import static android.os.UserManager.DISALLOW_CONFIG_BLUETOOTH;
import static android.os.UserManager.DISALLOW_CONFIG_WIFI;
import static android.os.UserManager.DISALLOW_UNINSTALL_APPS;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyAdminReceiver extends DeviceAdminReceiver {

void showToast(Context context, CharSequence msg) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
}

@Override
public void onPasswordFailed(Context context, Intent intent) {
    showToast(context, "Sample Device Admin: pw failed");
    Log.d("Hello", "onPasswordFailed");
    DevicePolicyManager mgr = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    int no = mgr.getCurrentFailedPasswordAttempts();


    if (no >= 3) {
        showToast(context, "3 failure");
        mgr.resetPassword("111111", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
        //mgr.lockNow();
    }
}

@Override
public void onEnabled(Context context, Intent intent)
{
    showToast(context, "Sample Device Admin: enabled");
}

    /**
     *
     * DISALLOW_CHANGE_WIFI_STATE doesn't work on Android 10(Acer ACTAB721) : users could still be
     * able to switch wifi on and off on Android10 but thanks to DISALLOW_CONFIG_WIFI they won't be
     * able to set any new connection.
     * Maybe deleting all known connections could be a trick? But OPEN wifi still represents an issue
     * @param context
     */
public void set_User_Restrictions(Context context)
{
    DevicePolicyManager mgr = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    ComponentName componentName1 = new ComponentName(context,MyAdminReceiver.class);
    mgr.addUserRestriction(componentName1,DISALLOW_BLUETOOTH);  //works on Android 10
    mgr.addUserRestriction(componentName1,DISALLOW_CONFIG_BLUETOOTH);   //is it really working on Android 10?
    mgr.addUserRestriction(componentName1,DISALLOW_UNINSTALL_APPS); //works on Android 10
    mgr.addUserRestriction(componentName1,DISALLOW_CONFIG_WIFI);    //works on Android 10
//    mgr.addUserRestriction(componentName1,DISALLOW_CONFIG_MOBILE_NETWORKS); //needs API 21. to test I need a 4g SIM card in

//    mgr.addUserRestriction(componentName1,DISALLOW_CHANGE_WIFI_STATE); //needs API 33. this one doesn't work
//    mgr.addUserRestriction(componentName1,DISALLOW_SMS); //needs API 21. without SIM card I can't test it
//    mgr.addUserRestriction(componentName1,DISALLOW_WIFI_DIRECT); //needs API 33. Doesn't work on Android 10

//    mgr.addUserRestriction(componentName1,DISALLOW_WIFI_TETHERING); //needs API 33. Doesn't work on Android 10
                                                                      //becomes unknown in android 8

}

    public void clear_User_Restrictions(Context context)
    {
        DevicePolicyManager mgr = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName1 = new ComponentName(context,MyAdminReceiver.class);
        mgr.clearUserRestriction(componentName1,DISALLOW_BLUETOOTH);  //works on Android 10
        mgr.clearUserRestriction(componentName1,DISALLOW_CONFIG_BLUETOOTH);   //is it really working on Android 10?
        mgr.clearUserRestriction(componentName1,DISALLOW_UNINSTALL_APPS); //works on Android 10
        mgr.clearUserRestriction(componentName1,DISALLOW_CONFIG_WIFI);    //works on Android 10
//    mgr.clearUserRestriction(componentName1,DISALLOW_CONFIG_MOBILE_NETWORKS); //needs API 21. to test I need a 4g SIM card in

//    mgr.clearUserRestriction(componentName1,DISALLOW_CHANGE_WIFI_STATE); //needs API 33. this one doesn't work
//    mgr.clearUserRestriction(componentName1,DISALLOW_SMS); //needs API 21. without SIM card I can't test it
//    mgr.clearUserRestriction(componentName1,DISALLOW_WIFI_DIRECT); //needs API 33. Doesn't work on Android 10

//    mgr.clearUserRestriction(componentName1,DISALLOW_WIFI_TETHERING); //needs API 33. Doesn't work on Android 10
                                                                        //becomes unknown in android 8
    }

@Override
public CharSequence onDisableRequested(Context context, Intent intent) {
    return "This is an optional message to warn the user about disabling.";
}

@Override
public void onDisabled(Context context, Intent intent) {
    showToast(context, "Sample Device Admin: disabled");
}

@Override
public void onPasswordChanged(Context context, Intent intent) {
    showToast(context, "Sample Device Admin: pw changed");
}



@Override
public void onPasswordSucceeded(Context context, Intent intent) {
    showToast(context, "Sample Device Admin: pw succeeded");
    }
}
