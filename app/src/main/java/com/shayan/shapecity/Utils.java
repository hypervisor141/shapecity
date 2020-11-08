package com.shayan.shapecity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import firestorm.FSActivity;
import vanguard.VLIOUtils;
import vanguard.VLSecurity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import javax.security.auth.DestroyFailedException;

@SuppressWarnings("unused")
public final class Utils {


    private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static SoundPool player;

    private static Calendar globalcal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private static Calendar localcal = Calendar.getInstance();
    private static Calendar tempglobalcal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private static Calendar templocalcal = Calendar.getInstance();

    private static int clicksid, click2sid, appearsid, enlargesid, transitionsid, reversesid, notifsid;
    private static int clickstid, click2stid, appearstid, enlargestid, transitionstid, reversestid, notifstid;
    private static int VIBRATE_LENGTH = 1000;
    private static float BASE_VOL = 0;
    private static final int NOTIFICATION_ID = 723842;
    private static final String CHANNEL_ID = "SMChannel";
    private static char[] hexArray = "0123456789ABCDEF".toCharArray();
    protected static final int PERMISSION_REQUEST_CODE = 1500;



    protected static boolean hasPermission(Context cxt, String permission){
        return ActivityCompat.checkSelfPermission(cxt, permission) == PackageManager.PERMISSION_GRANTED;
    }

    protected static void requestPermissionIfNeeded(Start m, String permission, Runnable post){
        requestPermissionsIfNeeded(m, new String[]{ permission }, post);
    }

    protected static void requestPermissionsIfNeeded(Start m, String[] permissions, Runnable post){
        ArrayList<Integer> l = new ArrayList<>();

        for(int i = 0; i < permissions.length; i++){
            if(!hasPermission(m, permissions[i])){
                l.add(i);
            }
        }

        String[] data = new String[l.size()];
        for(int i = 0; i < l.size(); i++){
            data[i] = permissions[l.get(i)];
        }

        if(data.length > 0){
            m.POSTPERMISSIONS = post;
            ActivityCompat.requestPermissions(m, data, PERMISSION_REQUEST_CODE);

        }else{
            if(post != null){
                post.run();
            }
        }
    }

    protected static void toast(final FSActivity act, final String text){
        if(act != null){
            act.runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    Toast.makeText(act, text, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    protected static boolean hasReadPermission(FSActivity act){
        return ContextCompat.checkSelfPermission(act, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    protected static boolean hasWritePermission(FSActivity act){
        return ContextCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    protected static void requestReadPermission(FSActivity act, int code){
        if(ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.READ_EXTERNAL_STORAGE)){
            toast(act, "\"Read Storage\" Permission is required to read your data from local database.");
        }

        ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE }, code);
    }

    protected static void requestWritePermission(FSActivity act, int code){
        if(ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            toast(act, "\"Write Storage\" Permission is required to save your data to local database.");
        }

        ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE }, code);
    }


    protected static void startHiveService(Start start, int profile){
//        if(!NetService.isStarted()){
//            NetService.initialize(start);
//            Intent intent = new Intent(start, NetService.class);
//            intent.putExtra("fromboot", false);
//            intent.putExtra("networkprofile", profile);
//            start.startService(intent);
//        }
    }


    protected static void startHiveServiceBoot(Context cxt, int flag){
//        if(!NetService.isStarted()){
//            Intent intent = new Intent(cxt, NetService.class);
//            intent.putExtra("fromboot", false);
//            intent.putExtra("flag", flag);
//            cxt.startService(intent);
//        }
    }


    protected static ArrayList<PhoneContact> getDefaultContacts(Context cxt){
        ContentResolver cr = cxt.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        PhoneNumberUtil formatter = PhoneNumberUtil.getInstance();
        String defaultcountry = Verify.getUserCountry(cxt).toUpperCase();
        String code = Verify.getCode(defaultcountry);
        int count = cursor.getCount();

        if(cursor != null && count > 0){
            ArrayList<PhoneContact> contacts = new ArrayList<>(count);

            int idindex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int nameindex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int numcheckindex = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);

            while(cursor.moveToNext()){
                String id = cursor.getString(idindex);
                String name = cursor.getString(nameindex);

                if(cursor.getInt(numcheckindex) > 0){
                    Cursor cursor2 = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{ id }, null);

                    int numindex = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int verindex = cursor2.getColumnIndex(ContactsContract.RawContacts.VERSION);

                    while(cursor2.moveToNext()){
                        String num = cursor2.getString(numindex);
                        Phonenumber.PhoneNumber number = null;

                        try{
                            number = formatter.parse(num, defaultcountry);
                        }catch(NumberParseException ex){
                            ex.printStackTrace();
                        }

                        if(number != null){
                            if(formatter.isValidNumberForRegion(number, defaultcountry)){
                                num = String.valueOf(number.getCountryCode()) + String.valueOf(number.getNationalNumber());

                            }else{
                                num = num.replace("+", "");
                            }

                            contacts.add(new PhoneContact(num, name, Integer.valueOf(id), cursor2.getInt(verindex)));
                        }
                    }

                    cursor2.close();
                }
            }

            cursor.close();
            return contacts;
        }

        return null;
    }


    protected static Signature[] getAPKSignatures(Context cxt){
        try{
            return cxt.getPackageManager().getPackageInfo(cxt.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
        }catch(PackageManager.NameNotFoundException ex){
            ex.printStackTrace();
        }

        return null;
    }

    protected static void showContactCard(Activity act, int id){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(id));
        intent.setData(uri);
        act.startActivity(intent);
    }

    protected static String requestCrop(FSActivity act, String root, Uri src, int outw, int outh, int code) throws IOException{
        File newfile = new File(root, "temp.jpg");
        newfile.createNewFile();
        Uri dst = Uri.fromFile(newfile);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(src, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outw);
        intent.putExtra("outputY", outh);
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, dst);

        act.startActivityForResult(intent, code);
        return newfile.getPath();
    }

    protected static Uri getBitmapURI(Context cxt, Bitmap b){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(cxt.getContentResolver(), b, "Title", null);
        return Uri.parse(path);
    }

    protected static String insertBitmapInMedia(Context cxt, Bitmap b){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return MediaStore.Images.Media.insertImage(cxt.getContentResolver(), b, "Title", null);
    }

    protected static String getPathFromURI(Context cxt, Uri uri){
        Cursor cursor = null;

        try{
            cursor = cxt.getContentResolver().query(uri, new String[]{ MediaStore.Images.Media.DATA }, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            return cursor.getString(column_index);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        if(cursor != null){
            cursor.close();
        }

        return null;
    }

    protected static void loadPlayer(Context cxt){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            SoundPool.Builder b = new SoundPool.Builder();
            b.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build());
            b.setMaxStreams(2);
            player = b.build();

        }else{
            player = new SoundPool(2, AudioManager.STREAM_SYSTEM,0);
        }

        clicksid = player.load(cxt, R.raw.click, 1);
        click2sid = player.load(cxt, R.raw.click2, 0);
        appearsid = player.load(cxt, R.raw.appear, 1);
        transitionsid = player.load(cxt, R.raw.transition, 0);
        enlargesid = player.load(cxt, R.raw.enlarge, 0);
        reversesid = player.load(cxt, R.raw.reversespin, 0);
    }

    protected static boolean isPlayerReady(){
        return player != null;
    }

    protected static void loadNotificationForPlayer(String path){
        notifsid = player.load(path, 0);
    }

    protected static void playClickSound(){
        clickstid = player.play(clicksid, BASE_VOL + 0.2f, BASE_VOL + 0.2f, 0, 0, 1.0f);
    }

    protected static void playClick2Sound(){
        click2stid = player.play(click2sid, BASE_VOL + 0.2f, BASE_VOL + 0.2f, 0, 0, 1.0f);
    }

    protected static void playAppearSound(){
        appearstid = player.play(appearsid, BASE_VOL + 0.6f, BASE_VOL + 0.6f, 0, 0, 1.0f);
    }

    protected static void playEnlargeSound(){
        enlargestid = player.play(enlargesid, BASE_VOL + 0.6f, BASE_VOL + 0.6f, 0, 0, 1.0f);
    }

    protected static void playTransitionSound(){
        transitionstid = player.play(transitionsid, BASE_VOL + 0.4f, BASE_VOL + 0.4f, 0, 0, 1.0f);
    }

    protected static void playReverseSpinSound(){
        reversestid = player.play(reversesid, BASE_VOL + 0.6f, BASE_VOL + 0.6f, 0, 0, 1.0f);
    }

    protected static void playActiveNotification(){
        notifstid = player.play(notifsid, BASE_VOL + 0.6f, BASE_VOL + 0.6f, 0, 0, 1.0f);
    }

    protected static void loopWaveSound(){
        click2stid = player.play(click2sid, BASE_VOL + 0.2f, BASE_VOL + 0.2f, 0, -1, 1.3f);
    }

    protected static void loopWaveSound(float speed){
        click2stid = player.play(click2sid, BASE_VOL + 0.2f, BASE_VOL + 0.2f, 0, -1, speed);
    }

    protected static void stopWaveLoop(){
        player.setLoop(click2stid, 0);
        player.stop(click2stid);
    }

    protected static void destroyPlayer(){
        if(player != null){
            player.release();
            player = null;
        }
    }

    protected static int getNewSMSCode(){
        return 123456 + new SecureRandom().nextInt(999999 - 123456);
    }

    protected static void sendSMS(FSActivity act, String num, String text){
        try{
            SmsManager sms = SmsManager.getDefault();
            sms.sendDataMessage(num, null, (short)0, text.getBytes("UTF-8"), null, null);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    protected static void showNotification(Activity act, String text, int location, Object material) throws IOException{
        NotificationManager manager = (NotificationManager)act.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notif = createNotification(act, text, location, material);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.notify(NOTIFICATION_ID, notif);

        }else{
            manager.notify(0, notif);
        }
    }

    protected static Notification createNotification(Activity act, String text, int location, Object material) throws IOException{
        Context cxt = act.getBaseContext();
        String appname = cxt.getString(R.string.app_name);

        Intent intent = new Intent(act, act.getClass());

        if(location != -1){
            intent.putExtra("location", location);
            intent.putExtra("material", VLIOUtils.convertToBytes(material));
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager manager = (NotificationManager)cxt.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, appname, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(appname + " Notification Channel");
            manager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(cxt, CHANNEL_ID);
            builder.setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(appname)
                    .setOnlyAlertOnce(true)
                    .setContentText(text)
                    .setSound(getDefaultSound())
                    .setContentIntent(PendingIntent.getActivity(cxt, 0, intent, PendingIntent.FLAG_ONE_SHOT));

            Notification notif = builder.build();
            return notif;

        }else{
            NotificationCompat.Builder builder = new NotificationCompat.Builder(cxt);
            builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(appname).setContentText(text)
                    .setContentIntent(PendingIntent.getActivity(cxt, 0, intent, PendingIntent.FLAG_ONE_SHOT)).setAutoCancel(true);


            Notification notif = builder.build();
            notif.sound = getDefaultSound();
            notif.defaults |= Notification.DEFAULT_VIBRATE;

            return notif;
        }
    }

    private static Uri getDefaultSound(){
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }


    protected static void vibrate(Context cxt, long duration){
        Vibrator v = (Vibrator)cxt.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(duration);
    }

    protected static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; j++){
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    protected static long getUTCTime(){
        return globalcal.getTimeInMillis();
    }

    protected static long getLocalTime(){
        return localcal.getTimeInMillis();
    }

    protected static long convertLocalTimeToUTC(long time){
        return time + (globalcal.getTimeInMillis() - localcal.getTimeInMillis());
    }

    protected static long convertUTCTimeToLocal(long time){
        return time + (localcal.getTimeInMillis() - globalcal.getTimeInMillis());
    }

    protected static String getLocalTimeStamp(long time){
        return getTimeStamp(templocalcal, time);
    }

    protected static String getUTCTimeStamp(long time){
        return getTimeStamp(tempglobalcal, time);
    }

    private static String getTimeStamp(Calendar cal, long time){
        cal.setTimeInMillis(time);
        int min = cal.get(Calendar.MINUTE);
        String smin;

        if(min < 10){
            smin = "0" + min;
        }else{
            smin = min + "";
        }

        return cal.get(Calendar.HOUR) + ":" + smin + " "
                + (cal.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
    }

    protected static void destroy() throws DestroyFailedException{
        destroyPlayer();
        VLSecurity.destroy();
    }

    protected static void createInputDialog(Context cxt, String title, String hint, int editorid, DialogInterface.OnClickListener listener){
        final EditText input = new EditText(cxt);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setId(editorid);
        input.setHint(hint);

        new AlertDialog.Builder(cxt).setTitle(title).setView(input)
                .setPositiveButton("Confirm", listener)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }

                }).show();
    }

    protected static void createConfirmationDialog(Context cxt, String title, DialogInterface.OnClickListener yeslistener,
                                                   DialogInterface.OnClickListener nolistener){
        if(nolistener == null){
            nolistener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }

        new AlertDialog.Builder(cxt).setTitle(title)
                .setPositiveButton("Yes", yeslistener)
                .setNegativeButton("No", nolistener).show();
    }

    protected static void createChoiceDialog(Context cxt, String title, String[] items, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(cxt).setTitle(title).setItems(items, listener).show();
    }

    protected static String normalizeString(String input){
        return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    protected static int convertColorFloatToInt(float[] color){
        return Color.argb((int)(color[3] * 255), (int)(color[0] * 255),
                (int)(color[1] * 255), (int)(color[2] * 255));
    }

    protected static float[] convertColorIntToFloat(int color){
        return new float[]{ (float)Color.alpha(color) / 255f, (float)Color.red(color) / 255f,
                (float)Color.green(color) / 255f, (float)Color.blue(color) / 255f };
    }
}
