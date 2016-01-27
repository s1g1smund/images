package com.example.manager.photos.takeorchoseimg;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manager.photos.takeorchoseimg.Maceks.ApiResponse;
import com.example.manager.photos.takeorchoseimg.Maceks.NetworkRequest;
import com.example.manager.photos.takeorchoseimg.Maceks.NetworkRequestCallback;
import com.example.manager.photos.takeorchoseimg.Maceks.NetworkRequestProperties;
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

//import retrofit.Call;
//import retrofit.Callback;
//import retrofit.Response;
//import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    private ImageView imgView;
    private TextView txtWelcome;

    private String path;
    private Uri pathUri;

    private static final int CHOOSE_FROM_GALLERY = 0;
    private static final int TAKE_NEW_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imgFullHd = (ImageView) findViewById(R.id.showImageInView1920);
        Picasso.with(getApplicationContext())
                .load(R.drawable.kylo_ren_1920x1080)
                .error(R.drawable.load_image_error)
                .resize(imgFullHd.getMaxWidth(), 700)
                .centerInside()
                .into(imgFullHd);

        imgView = (ImageView) findViewById(R.id.showImageInView);
        txtWelcome = (TextView) findViewById(R.id.welcomeLabel);
    }

    public void takeExistingImage(View view){
        txtWelcome.setText("BIBLIOTEKA!");
        Intent photoGalleryIntent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        Uri data = Uri.parse(pictureDirectoryPath);

        photoGalleryIntent.setDataAndType(data, "image/*");

        startActivityForResult(photoGalleryIntent, CHOOSE_FROM_GALLERY);
    }

    public void takeNewImage(View view){
        txtWelcome.setText("APARAT!");

        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(takePicture, TAKE_NEW_PHOTO);//zero can be replaced with any action code
    }

    public void uploadImage(View view){
        trytoupload();
    }

    private void trytoupload2(){
        File file = new File(path);
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http")
                .authority("vps212144.ovh.net")
                .appendPath("api")
                .appendPath("files");

        Uri uri = Uri.parse("http://vps212144.ovh.net:8080/api/files");

        NetworkRequestProperties.HTTP_METHOD httpMethod = NetworkRequestProperties.HTTP_METHOD.POST;
        try {
            NetworkRequestProperties properties = new NetworkRequestProperties(httpMethod, uri);
            properties.forceMultipartEntity();
            ArrayList<Uri> paths = new ArrayList<>();
            paths.add(pathUri);
            properties.setFilesNamesToUpload(paths, this);
            new NetworkRequest(properties, new NetworkRequestCallback() {
                @Override
                public void onComplete(ApiResponse result) {
                    Log.v("Upload", "success");
                    Toast.makeText(getApplicationContext(), "UPLOAD SUCCESS", Toast.LENGTH_LONG).show();
                }
            }).execute();
        } catch (MalformedURLException e) {
            Log.v("Upload", "success");
            Toast.makeText(getApplicationContext(), "UPLOAD SUCCESS", Toast.LENGTH_LONG).show();
        }

    }

    private void trytoupload(){
        txtWelcome.setText("UPLOAD!");

        FileUploadService service =
                ServiceGenerator.createService(FileUploadService.class);
        File file = new File(path);

        Map<String, RequestBody> map = new HashMap<>();//
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);//
        map.put("uploadFile\"; filename=\"" + file.getName() + "\"", requestBody);//

        //RequestBody requestBody =
                //RequestBody.create(MediaType.parse("multipart/form-data"), file);

        Call<FileRepresentation> call = service.upload(map);
        //Call<FileRepresentation> call = service.upload(requestBody);
        call.enqueue(new Callback<FileRepresentation>() {
            @Override
            public void onResponse(Response<FileRepresentation> response, Retrofit retrofit) {
                Log.v("Upload", "success");
                Toast.makeText(getApplicationContext(), "UPLOAD SUCCESS", Toast.LENGTH_LONG).show();
//                TextView nr1 = (TextView) findViewById(R.id.id1);
//                TextView nr2 = (TextView) findViewById(R.id.id2);
//                TextView nr3 = (TextView) findViewById(R.id.id3);
//                TextView nr4 = (TextView) findViewById(R.id.id4);
//                TextView nr5 = (TextView) findViewById(R.id.id5);
//                TextView nr6 = (TextView) findViewById(R.id.id6);
//                nr1.setText(response.body().getFileName());
//                nr2.setText(response.body().getUrl());
//                nr3.setText(response.body().getCreationDate());
//                nr4.setText(response.body().getFileSize().toString());
//                nr5.setText(response.body().getId().toString());
//                nr6.setText(response.body().getModificationDate());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Upload", t.getMessage());
                Toast.makeText(getApplicationContext(), "UPLOAD FAILURE", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == CHOOSE_FROM_GALLERY){
                Uri imgUri = data.getData();

                Picasso.with(getApplicationContext())
                        .load(imgUri)
                        .error(R.drawable.load_image_error)
                        //.onlyScaleDown() // the image will only be resized if it's bigger than resize value of pixels.
                        .resize(imgView.getMaxWidth(), 700)
                        .centerInside()
                        .into(imgView);
                String realUrl = getRealPathFromURI(imgUri);
                Toast.makeText(getApplicationContext(), realUrl, Toast.LENGTH_LONG).show();
                path = realUrl;
                pathUri = imgUri;

//                InputStream inputStream;
//
//                try {
//                    inputStream = getContentResolver().openInputStream(imgUri);
//                    Bitmap bitmapOfImage = BitmapFactory.decodeStream(inputStream);
//                    imgView.setImageBitmap(bitmapOfImage);
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
//                }
            }
            if(requestCode == TAKE_NEW_PHOTO){
                Uri selectedImage = data.getData();
                Picasso.with(getApplicationContext())
                        .load(selectedImage)
                        .error(R.drawable.load_image_error)
                                //.onlyScaleDown() // the image will only be resized if it's bigger than resize value of pixels.
                        .resize(imgView.getMaxWidth(), 700)
                        //.resize(100, 100)
                        .centerInside()
                        .into(imgView);
                String realUrl = getRealPathFromURI(selectedImage);
                Toast.makeText(getApplicationContext(), realUrl, Toast.LENGTH_LONG).show();
                String path = realUrl;
            }
        }
        else
            Toast.makeText(this, "RESULT IS NOT OK", Toast.LENGTH_LONG);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
