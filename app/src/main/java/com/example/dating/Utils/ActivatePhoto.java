//package com.example.dating.Utils;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.provider.Settings;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//public class ActivatePhoto {
//    private String[] permissionsRequired = new String[]{Manifest.permission.CAMERA,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE};
//
////    protected ImageView imageView;
//    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
//    private static final int REQUEST_PERMISSION_SETTING = 101;
//
//    private Bitmap myBitmap;
//    private SharedPreferences permissionStatus;
//    private boolean sentToSettings = false;
//    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
//
//
//    private void requestMultiplePermissions() {
//        if (ActivityCompat.checkSelfPermission(ActivatePhoto.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(ActivatePhoto.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(ActivatePhoto.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivatePhoto.this, permissionsRequired[0])
//                    || ActivityCompat.shouldShowRequestPermissionRationale(ActivatePhoto.this, permissionsRequired[1])
//                    || ActivityCompat.shouldShowRequestPermissionRationale(ActivatePhoto.this, permissionsRequired[2])) {
//                //Show Information about why you need the permission
//                AlertDialog.Builder builder = new AlertDialog.Builder(ActivatePhoto.this);
//                builder.setTitle("C???p quy???n cho ???ng d???ng");
//                builder.setMessage("???ng d???ng c???n ???????c c???p quy???n Camera v?? V??? tr??.");
//                builder.setPositiveButton("?????ng ??", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                        ActivityCompat.requestPermissions(ActivatePhoto.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
//                    }
//                });
//                builder.setNegativeButton("H???y", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
//            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
//                //Previously Permission Request was cancelled with 'Dont Ask Again',
//                // Redirect to Settings after showing Information about why you need the permission
//                AlertDialog.Builder builder = new AlertDialog.Builder(ActivatePhoto.this);
//                builder.setTitle("C???p quy???n cho ???ng d???ng");
//                builder.setMessage("???ng d???ng c???n ???????c c???p quy???n Camera v?? V??? tr??.");
//                builder.setPositiveButton("?????ng ??", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                        sentToSettings = true;
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", getPackageName(), null);
//                        intent.setData(uri);
//                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
//                        Toast.makeText(getBaseContext(), "C???p quy???n Camera v?? V??? tr??.", Toast.LENGTH_LONG).show();
//                    }
//                });
//                builder.setNegativeButton("H???y", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
//            } else {
//                //just request the permission
//                ActivityCompat.requestPermissions(ActivatePhoto.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
//            }
//
//            // txtPermissions.setText("Permissions Required");
//
//            SharedPreferences.Editor editor = permissionStatus.edit();
//            editor.putBoolean(permissionsRequired[0], true);
//            editor.commit();
//        } else {
//            //You already have the permission, just go ahead.
//            //proceedAfterPermission();
//        }
//    }
//
//    private void proceedAfterPermission() {
//
//
//        final CharSequence[] options = {"Ch???p ???nh", "Ch???n ???nh t??? b??? s??u t???p", "H???y"};
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(ActivatePhoto.this);
//
//        builder.setTitle("T???i ???nh!");
//
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//
//            @Override
//
//            public void onClick(DialogInterface dialog, int item) {
//
//                if (options[item].equals("Ch???p ???nh")) {
//
//                    cameraIntent();
//
//                } else if (options[item].equals("Ch???n ???nh t??? b??? s??u t???p")) {
//
//                    galleryIntent();
//
//
//                } else if (options[item].equals("H???y")) {
//
//                    dialog.dismiss();
//
//                }
//
//            }
//
//        });
//
//        builder.show();
//
//    }
//
//
//    private void galleryIntent() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);//
//        startActivityForResult(Intent.createChooser(intent, "Ch???n ???nh"), SELECT_FILE);
//    }
//
//    private void cameraIntent() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, REQUEST_CAMERA);
//    }
//
////    @Override
////    public void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        if (requestCode == REQUEST_PERMISSION_SETTING) {
////            if (ActivityCompat.checkSelfPermission(ActivatePhoto.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
////                //Got Permission
////                proceedAfterPermission();
////            }
////        }
////        if (resultCode == Activity.RESULT_OK) {
////            if (requestCode == SELECT_FILE)
////                onSelectFromGalleryResult(data);
////            else if (requestCode == REQUEST_CAMERA)
////                onCaptureImageResult(data);
////        }
////    }
//
//    private Bitmap onCaptureImageResult(Intent data) {
//        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//
//        File destination = new File(Environment.getExternalStorageDirectory(),
//                System.currentTimeMillis() + ".jpg");
//
//        FileOutputStream fo;
//        try {
//            destination.createNewFile();
//            fo = new FileOutputStream(destination);
//            fo.write(bytes.toByteArray());
//            fo.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return thumbnail;
////        imageView.setImageBitmap(thumbnail);
//    }
//
//    @SuppressWarnings("deprecation")
//    private Bitmap onSelectFromGalleryResult(Intent data) {
//
//        Bitmap bm = null;
//        if (data != null) {
//            try {
//                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return bm;
////        imageView.setImageBitmap(bm);
//    }
//
//
//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//        if (sentToSettings) {
//            if (ActivityCompat.checkSelfPermission(ActivatePhoto.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
//                //Got Permission
//                proceedAfterPermission();
//            }
//        }
//    }
//
//}
