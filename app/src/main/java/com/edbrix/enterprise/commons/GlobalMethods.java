package com.edbrix.enterprise.commons;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfRenderer;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StatFs;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class GlobalMethods {

//    ObjectMapper mapper;

    public static final int external_storage_permission_request_code = 102;

    public static final int audio_external_storage_permission_request_code = 103;

    public static final int camera_external_storage_permission_request_code = 104;

    public static final int camera_audio_external_storage_permission_request_code = 105;

    public static final int network_state_permission_request_code = 106;

    public static final int all_permission_request_code = 107;


    public GlobalMethods() {
//        mapper = new ObjectMapper();
    }

    public void writeFileToExternalStorage(String path, String sBody, String fileName) {
        try {

            File root = new File(path);
            boolean isDirectoryCreated = root.exists();
            if (!isDirectoryCreated) {
                isDirectoryCreated = root.mkdir();
            }
            if (isDirectoryCreated) {
                File gpxfile = new File(root, fileName);

                BufferedWriter buf = new BufferedWriter(new FileWriter(gpxfile, true));
                buf.append(sBody);
                buf.newLine();
                buf.close();

                Log.v("FileHanlder", "File saved");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String readFromFile(String path, Context mContext, String locationFileName) {

//        String ret = "";
//
//        try {
//            InputStream inputStream = mContext.openFileInput(locationFileName);
//
//            if (inputStream != null) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                String receiveString = "";
//                StringBuilder stringBuilder = new StringBuilder();
//
//                while ((receiveString = bufferedReader.readLine()) != null) {
//                    stringBuilder.append(receiveString);
//                }
//
//                inputStream.close();
//                ret = stringBuilder.toString();
//            }
//        } catch (FileNotFoundException e) {
//            Log.e("login activity", "File not found: " + e.toString());
//        } catch (IOException e) {
//            Log.e("login activity", "Can not read file: " + e.toString());
//        }
//
//        return ret;

        //Find the directory for the SD Card using the API
//*Don't* hardcode "/sdcard"

//Get the text file
        File file = new File(path, locationFileName);

//Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append("\n");
                System.gc();
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();
    }

    /**
     * Checking device has camera hardware or not
     */
    public static boolean isDeviceSupportCamera(Context context) {
        // this device has a camera
// no camera on this device
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }


    /**
     * Checking email id is in proper format
     */
    public static boolean validateEmail(EditText view) {

        String email = view.getText().toString().trim();
        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        System.out.println("Valid Mail:" + matcher.matches());
        return matcher.matches();

    }

    /**
     * validated EditText blank or not
     *
     * @param view
     * @return
     */
    public static boolean editTextIsblank(EditText view) {
        String valueofView = view.getText().toString().trim();
        if (valueofView.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * validated multiple EditText blank or not
     *
     * @return true/false
     * <p/>
     * if any of EditText view blank return false
     */
    public static boolean editTextIsNotblank(EditText... views) {
        boolean forReturn = true;
        for (EditText editView : views) {
            String valueofView = editView.getText().toString().trim();
            if (valueofView.isEmpty()) {
                editView.requestFocus();
                forReturn = false;
            }
        }
        return forReturn;
    }


    /**
     * @param dateOne
     * @param dateTwo (optional) if you send this date null will compare with current date
     * @param format  send "" if dont want costom format
     * @return following values
     * 1=given dateOne is after dateTwo
     * -1=given dateOne is before dateTwo
     * 0=dates are equal
     * -2= ParseException occures
     */
    public int compareDate(Date dateOne, Date dateTwo, String format) {
        int retunValue = -2;
        SimpleDateFormat formater = new SimpleDateFormat(format, Locale.US);
        if (dateTwo == null) {
            dateTwo = Calendar.getInstance().getTime();
        }
        try {

            if (!format.equalsIgnoreCase("")) {
                dateOne = formater.parse(dateOne.toString());
                dateTwo = formater.parse(dateTwo.toString());
            }

            retunValue = dateTwo.compareTo(dateOne);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retunValue;
    }


    public static long megabytesAvailable(File f) {
        StatFs stat = new StatFs(f.getPath());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return (long) stat.getAvailableBlocksLong() * (long) stat.getBlockSizeLong();
        } else {

            return (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
        }

    }

    public static Bitmap decodeFile(String src) {
        //Decode image size
        try {
            Log.e("src", src);

            File file = new File(src);
            FileInputStream fileInputStream;
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            fileInputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(fileInputStream, null, o);
            //Rect rect=new Rect(0,0,100,100);

            final int REQUIRED_SIZE = 70;

            //Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inJustDecodeBounds = false;
            o2.inSampleSize = scale;
            fileInputStream.close();
            //fileInputStream.reset();
            fileInputStream = new FileInputStream(file);
            Bitmap b = BitmapFactory.decodeStream(fileInputStream, null, o2);
            //input.close();
            //fileInputStream.close();
            return b;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
            return null;
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    public void previewImage(Context context, String path) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file:///" + path), "image/*");
        context.startActivity(i);
    }

    public void previewVideo(Context context, String path) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file:///" + path), "video/*");
        context.startActivity(i);
    }

    public String encodeBase64(byte[] originalBytes) {
        return new String(Base64.encode(originalBytes, 0));
    }

    public String decodeBase64(byte[] originalBytes) {
        return new String(Base64.decode(originalBytes, 0));
    }

    public static String ConvertMetersToMiles(double meters) {
        try {
            return String.format(Locale.US, "%.2f", meters / 1609.344);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return "";
    }

    public Bitmap roatedBitmap(String filePath, Bitmap bitmap) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        int orientation = 0;
        if (exif != null) {
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        }
        Matrix matrix = new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            //			if (!bmRotated.isRecycled())
//			{
//				bmRotated.recycle();
//			}
            //bitmap=null;
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        String temp = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] b = baos.toByteArray();
            temp = null;
            try {
                System.gc();
                temp = Base64.encodeToString(b, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                b = baos.toByteArray();
                temp = Base64.encodeToString(b, Base64.DEFAULT);
                try {
                    baos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Log.e("EWN", "Out of memory error catched");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return temp;
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand = new Random();
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        return rand.nextInt((max - min) + 1) + min;
    }

    public static boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else //return false if nothing matches the input
            return phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}");

    }

    public static void setImageBase64(Context context, String imageBase64, ImageView imageView, int alternetimage) {
        try {
            if (!imageBase64.isEmpty()) {
                byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, alternetimage));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String GetISOCode(Context context, int array) {
        String CountryID = "";
        String ContryISOcode = "";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = context.getResources().getStringArray(array);
        for (String aRl : rl) {
            String[] g = aRl.split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                ContryISOcode = g[0];
                break;
            }
        }
        if (ContryISOcode.isEmpty()) {
            return "";
        }
        return "+" + ContryISOcode;
    }

   /*
    /**
     * replace old fragment with new one
     * if no fragment in back-stack  create it
     *
     * @param fragment
     */
//    public void replaceFragment(Fragment fragment, FragmentActivity contextFragment) {
//
//        try {
//            String backStateName = fragment.getClass().getName();
//            String fragmentTag = backStateName;
//
//
//            FragmentManager manager = contextFragment.getSupportFragmentManager();
////            boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);
////
////            if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.
//            FragmentTransaction ft = manager.beginTransaction();
//            ft.replace(R.id.container, fragment, fragmentTag);
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//            ft.addToBackStack(null);
//            ft.commit();
//            //}
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }
/*
    /**
     * convert json to object
     *
     * @param jsonData
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException User userObject=toObjects(jsonString,User.class);
     */
   /* public <T> T toObjects(String jsonData, Class<T> clazz) throws IOException {
        return mapper.readValue(jsonData, clazz);
    }*/

    /*
    /**
     * convert json to list object
     *
     * @param jasondata
     * @param target
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassNotFoundException if type=List<User>
     *                                List<User> userlist=   convertJsonToPOJO(jsonString,User.class);
     */
     /*
   public <T> T convertJsonToPOJO(String jasondata, Class<?> target) throws IOException, ClassNotFoundException {
        return mapper.readValue(jasondata, mapper.getTypeFactory().constructCollectionType(List.class, Class.forName(target.getName())));
    }*/
    public static boolean isLocationServicesEnable(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.v("LocationService", "GPS Provider Enabled : " + gps_enabled);

        } catch (Exception ex) {
            Log.v("LocationService", ex.getMessage());
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.v("LocationService", "Network Provider Enabled : " + gps_enabled);

        } catch (Exception ex) {
            Log.v("LocationService", ex.getMessage());
        }

        if (!gps_enabled && !network_enabled) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isInternetConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static double convertMetersToMiles(double meter) {
        double miles = 0.0;
        miles = meter * 0.00062137;
        miles = Math.round(miles * 10);
        miles = miles / 10;
        return miles;
    }

    public static String getFileNameByUri(Context context, Uri uri) {
        String fileName = "unknown";//default fileName
        Uri filePathUri = uri;
        if (uri.getScheme().toString().compareTo("content") == 0) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
                filePathUri = Uri.parse(cursor.getString(column_index));
                fileName = filePathUri.getLastPathSegment().toString();
            }
        } else if (uri.getScheme().compareTo("file") == 0) {
            fileName = filePathUri.getLastPathSegment().toString();
        } else {
            fileName = fileName + "_" + filePathUri.getLastPathSegment();
        }
        return fileName;
    }

    public static void writeToFileExternalStorage(String sBody, String fileName) {
        try {
            String folderName = "BaseMapLog";

            File root = new File(Environment.getExternalStorageDirectory(), folderName);
            boolean isDirectoryCreated = root.exists();
            if (!isDirectoryCreated) {
                isDirectoryCreated = root.mkdir();
            }
            if (isDirectoryCreated) {
                File gpxfile = new File(root, fileName);

                BufferedWriter buf = new BufferedWriter(new FileWriter(gpxfile, true));
                buf.append(sBody);
                buf.newLine();
                buf.close();

                Log.v("FileHanlder", "File saved");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getImageBytesFromUri(Context context, Uri uri) throws IOException {
        InputStream iStream = context.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = iStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static String convertImageUrlToBase64String(Context context, String imageUrl) {
        String base64 = "";
        try {
//            Uri imageUri = Uri.parse(imageUrl);
//            byte[] inputData = getImageBytesFromUri(context, imageUri);
            byte[] inputData = getImageByteFromBitmap(getBitmapFromURL(imageUrl));
            base64 = Base64.encodeToString(inputData, Base64.DEFAULT);
        } catch (Exception e) {
            Log.v("Base64 Exception", e.getMessage());
            e.printStackTrace();
        }
        return base64;
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getImageByteFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension.toLowerCase());
            if (type == null) {
                type = extension;
            }
        }
        return type;
    }

    public static Bitmap getVideoThumbnailFromPath(String path) {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path,
                MediaStore.Images.Thumbnails.MINI_KIND);
        return thumb;
    }

    public static String requestReadWriteExternalStoragePermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                external_storage_permission_request_code);
        return "Requesting for Permission";
    }

    public static String requestAllPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_NETWORK_STATE},
                all_permission_request_code);
        return "Requesting for All Permission";
    }

    public static String requestAudioWithReadWriteExternalStoragePermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO},
                audio_external_storage_permission_request_code);
        return "Requesting for Permission";
    }

    public static String requestCameraWithReadWriteExternalStoragePermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA},
                camera_external_storage_permission_request_code);
        return "Requesting for Permission";
    }

    public static String requestCameraAudioWithReadWriteExternalStoragePermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO},
                camera_audio_external_storage_permission_request_code);
        return "Requesting for Permission";
    }

    public static String requestNetworkStatePermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{
                        Manifest.permission.ACCESS_NETWORK_STATE},
                network_state_permission_request_code);
        return "Requesting for Permission";
    }

    public static boolean checkCalendarPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkReadExternalStorage(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkWriteExternalStorage(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkReadWriteExternalStorage(Context context) {
        int resultR = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int resultW = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (resultR == PackageManager.PERMISSION_GRANTED && resultW == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkAudioRecordPermission(Context context) {

        int resultA = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        int resultR = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int resultW = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (resultR == PackageManager.PERMISSION_GRANTED && resultW == PackageManager.PERMISSION_GRANTED && resultA == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkCameraRecordPermission(Context context) {
        int resultC = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        int resultR = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int resultW = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (resultR == PackageManager.PERMISSION_GRANTED && resultW == PackageManager.PERMISSION_GRANTED && resultC == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkCameraAudioRecordPermission(Context context) {
        int resultA = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        int resultC = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        int resultR = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int resultW = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (resultR == PackageManager.PERMISSION_GRANTED && resultW == PackageManager.PERMISSION_GRANTED && resultC == PackageManager.PERMISSION_GRANTED && resultA == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkAllPermission(Context context) {
        int resultA = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        int resultC = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        int resultR = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int resultW = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int resultN = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE);

        if (resultR == PackageManager.PERMISSION_GRANTED && resultW == PackageManager.PERMISSION_GRANTED && resultC == PackageManager.PERMISSION_GRANTED && resultA == PackageManager.PERMISSION_GRANTED && resultN == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkNetworkPermission(Context context) {
        int resultN = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE);

        if (resultN == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * returning image / video
     */
    public static File getOutputMediaFile(Context context, File storageDire) {

        // External sdcard location
        File mediaStorageDir = storageDire;

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(context.getClass().getSimpleName(), "Oops! Failed create "
                        + "/VideoRecorder" + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile = null;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "VID" + timeStamp + ".mp4");
        return mediaFile;
    }

    /**
     * Return video file duration from File Object
     *
     * @param context   Context
     * @param uriOfFile URI of File
     * @return String with min:sec format
     */
    public String getVideoDuration(Context context, File uriOfFile) {
        MediaPlayer mp = MediaPlayer.create(context, Uri.fromFile(uriOfFile));
        int duration = mp.getDuration();
        mp.release();
/*convert millis to appropriate time*/
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }

    /**
     * Vibrate Phone on event
     *
     * @param context             Context
     * @param vibrateMilliSeconds times in milliseconds
     */
   /* public static void vibratePhone(Context context, int vibrateMilliSeconds) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(vibrateMilliSeconds);
    }*/

    /**
     * Example with output video duration set to 30 seconds with -t 30:
     * <p>
     * ffmpeg -loop 1 -i img.jpg -c:v libx264 -t 30 -pix_fmt yuv420p out.mp4
     *
     * @param imageFileAbsolutePath       Image file Absolute path
     * @param outputVideoFileAbsolutePath Output Video file Absolute path
     * @param durationInSec               Duration in seconds
     * @return command[] = {"-loop","1","-i", imageFileAbsolutePath,"-c:v", "libx264", "-t", durationInSec, "-pix_fmt", "yuv420p", outputVideoFileAbsolutePath};
     */
    public String[] commandOutputMuteVideoWithDuration(String imageFileAbsolutePath, String outputVideoFileAbsolutePath, String durationInSec) {
        String command[] = {"-loop", "1", "-i", imageFileAbsolutePath, "-c:v", "libx264", "-t", durationInSec + "", "-pix_fmt", "yuv420p", outputVideoFileAbsolutePath};
        return command;
    }

    /**
     * For merging audio and video where input video file contains audio ,you can use below command-
     *
     * @param videoFileAbsolutePath   Video file Absolute path
     * @param audioFileAbsolutePath   Audio file Absolute path
     * @param destinationAbsolutePath Destination file Absolute path
     * @return command[] = {"-i",videoFileAbsolutePath,"-i",audioFileAbsolutePath, "-c:v", "copy", "-c:a", "aac","-map", "0:v:0", "-map", "1:a:0","-shortest", destinationAbsolutePath};
     */
    public String[] commandMergingAudio_Video(String videoFileAbsolutePath, String audioFileAbsolutePath, String destinationAbsolutePath) {
        String command[] = {"-y", "-i", videoFileAbsolutePath, "-i", audioFileAbsolutePath, "-c:v", "copy", "-c:a", "aac", "-map", "0:v:0", "-map", "1:a:0", "-shortest", destinationAbsolutePath};
        return command;
    }

    public String[] commandCreateMP4FromImage(String imageFileAbsolutePath, String audioFileAbsolutePath, String outputVideoFileAbsolutePath, int durationInSec, int framePS) {
        int framesNo = durationInSec * framePS;
//        String command[] = {"-y","-loop","1","-i", imageFileAbsolutePath, "-i", audioFileAbsolutePath, "-r",""+framePS, "-b:v", "2500k", "-vframes", ""+framesNo, "-c:v", "libx264", "-vcodec", "mpeg4", outputVideoFileAbsolutePath};
        String command[] = {"-y", "-loop", "1", "-i", imageFileAbsolutePath, "-i", audioFileAbsolutePath, "-r", "" + framePS, "-b:v", "2500k", "-vframes", "" + framesNo, "-c:v", "libx264", "-vcodec", "mpeg4", "-pix_fmt", "yuv420p", outputVideoFileAbsolutePath};
        return command;
    }

    public String[] commandCreateAlbumFromImage(String inputTextFilePath, String outputVideoFilePath) {
        String command[] = {"-f", "concat", "-safe", "0", "-i",
                inputTextFilePath,
                "-c:v", "libx264", "-vf", "fps=30", "-pix_fmt", "yuv420p",
                outputVideoFilePath};
        return command;
    }

    public String[] concatenateAudioWithVideoAlbum(String videoAlbumFilePath, String audioFilePath, String outputVideoFilePath) {
        String command[] = {"-y", "-i", videoAlbumFilePath, "-i", audioFilePath, "-c:v", "copy", "-c:a", "aac", "-strict", "experimental", outputVideoFilePath};
        return command;
    }

    public String[] commandToAddWatermarkImageOnVideo(String videoAlbumFilePath, String imageFilePath, String outputVideoFilePath) {
        String command[] = {"-y", "-i", videoAlbumFilePath, "-i", imageFilePath, "-filter_complex", "overlay=main_w-overlay_w-10:main_h-overlay_h-10", outputVideoFilePath};
        return command;
    }

    // bottom left : overlay=10:main_h-overlay_h-10

    //bottom right with 5px of padding
    // overlay=main_w-overlay_w-5:main_h-overlay_h-5

    /**
     * Create text file on sdcard
     *
     * @param childDirectory
     * @param sFileName
     * @param sBody
     * @return object of created file.
     */
    public File generateNoteOnSD(String childDirectory, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), childDirectory);
            if (!root.exists()) {
                root.mkdirs();
            }
            File txtFile = new File(root, sFileName);
            FileWriter writer = new FileWriter(txtFile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            return txtFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*private Bitmap scaledBitmap(float width, float height, Uri originalImageUri, Context context) {
        String path = FileUtils.getPath(context, originalImageUri);
        Bitmap originalImage = (BitmapFactory.decodeFile(path));

        Bitmap background = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);

        float originalWidth = originalImage.getWidth();
        float originalHeight = originalImage.getHeight();

        Canvas canvas = new Canvas(background);

        float scale = width / originalWidth;

        float xTranslation = 0.0f;
        float yTranslation = (height - originalHeight * scale) / 2.0f;

        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        canvas.drawBitmap(originalImage, transformation, paint);

        return background;
    }*/

    public void saveImageFromBitmap(Bitmap finalBitmap, String fileId, File appStorageDir) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(appStorageDir + "/temp");
        if (!myDir.isDirectory())
            myDir.mkdirs();

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Img_" + fileId + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File saveImageFromBitmap(Bitmap finalBitmap, String fileId, String fileDirectoryPath) {
        File myDir = new File(fileDirectoryPath);
        if (!myDir.isDirectory())
            myDir.mkdirs();

        String fname = "img_" + fileId + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void playVideo(Context context, String uriStringPath) {
        if (uriStringPath != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(uriStringPath), "video/mp4");
            context.startActivity(intent);
        }
    }

    /**
     * Convert PDF file to Bitmap Collection.
     * Supports for Android Lollipop and above.
     *
     * @param context context.
     * @param pdfFile File object PDF file.
     * @return ArrayList<Bitmap> Bitmap collection.
     */
    public ArrayList<Bitmap> pdfFileToBitmapCollection(Context context, File pdfFile) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));

                Bitmap bitmap;
                final int pageCount = renderer.getPageCount();
                for (int i = 0; i < pageCount; i++) {
                    PdfRenderer.Page page = renderer.openPage(i);

                    int width = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                    int height = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    bitmaps.add(bitmap);

                    // close the page
                    page.close();
                }

                // close the renderer
                renderer.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bitmaps;
    }

    /**
     * Copy file from Application Asset to Local SD card
     *
     * @param context         context
     * @param fileNameWithExt fileName with extension
     * @param pathDir         sd card path where want to copy file
     * @return file object of copied file
     */
    public File copyFileFromAssets(Context context, String fileNameWithExt, File pathDir) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fileNameWithExt);
            out = new FileOutputStream(pathDir + File.separator + fileNameWithExt);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return new File(pathDir + File.separator + fileNameWithExt);
        } catch (IOException e) {
            Log.e("tag", "Failed to copy asset file: " + fileNameWithExt, e);
            return null;
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    /**
     * Check if this device has a camera front or back
     */
    public static boolean isCameraHardwareAvailable(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return true;
        } else if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Clear content from file directory
     *
     * @param fileDirectory
     */
    public void clearContentFromDirectory(File fileDirectory) {

        if (fileDirectory.isDirectory()) {
            for (File f : fileDirectory.listFiles()) {
                if (f.exists()) {
                    f.delete();
                }
            }
        }
    }

    /**
     * Here is Both function for encrypt and decrypt file in Sdcard folder. we
     * can not lock folder but we can encrypt file using AES in Android, it may
     * help you.
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */

    public void encrypt(String filePath, String outPath) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        // Here you read the cleartext.
        FileInputStream fis = new FileInputStream(filePath);
        // This stream write the encrypted text. This stream will be wrapped by
        // another stream.
        FileOutputStream fos = new FileOutputStream(outPath);
        // Length is 16 byte
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(),
                "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while ((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
    }

    public void decrypt(String outPath, String inPath) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        FileInputStream fis = new FileInputStream(outPath);
        FileOutputStream fos = new FileOutputStream(inPath);//oldfeelwasverynb
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(),
                "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8];
        while ((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    }

    private String encryptedFileName = "Enc_File2.txt";
    private static String algorithm = "AES";
    static SecretKey yourKey = null;

    public static SecretKey generateKey(char[] passphraseOrPin, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Number of PBKDF2 hardening rounds to use. Larger values increase
        // computation time. You should select a value that causes computation
        // to take >100ms.
        final int iterations = 1000;

        // Generate a 256-bit key
        final int outputKeyLength = 256;

        SecretKeyFactory secretKeyFactory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations,
                outputKeyLength);
        yourKey = secretKeyFactory.generateSecret(keySpec);
        return yourKey;
    }

    public static SecretKey generateSalt() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        final int outputKeyLength = 256;

        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static byte[] encodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] data = yourKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length,
                algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(fileData);

        return encrypted;
    }

    public static byte[] decodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] data = yourKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length,
                algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(fileData);

        return decrypted;
    }

    void saveFile(String stringToSave) {
        try {
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator, encryptedFileName);
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            char[] p = {'p', 'a', 's', 's'};
            SecretKey yourKey = generateKey(p, generateSalt().toString()
                    .getBytes());
            byte[] filesBytes = encodeFile(yourKey, stringToSave.getBytes());
            bos.write(filesBytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void decodeFile() {
        try {
            byte[] decodedData = decodeFile(yourKey, readFile());
            String str = new String(decodedData);
            System.out.println("DECODED FILE CONTENTS : " + str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] readFile() {
        byte[] contents = null;

        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator, encryptedFileName);
        int size = (int) file.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(
                    new FileInputStream(file));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }

    /*
    * BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(yourFile));
bos.write(fileBytes);
bos.flush();
bos.close();

//

FileOutputStream fos = new FileOutputStream(objFile);
fos.write(objFileBytes);
fos.close();
*/
}
