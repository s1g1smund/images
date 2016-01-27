package com.example.manager.photos.takeorchoseimg.Maceks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class NetworkRequest extends AsyncTask<String, String, ApiResponse> {
    private NetworkRequestCallback callbackHandler;
    private NetworkRequestProperties requestProperties;

    public NetworkRequest(NetworkRequestProperties requestProperties, NetworkRequestCallback callback) {
        this.requestProperties = requestProperties;
        this.callbackHandler = callback;
    }

    @Override
    protected ApiResponse doInBackground(String... arg0) {
        try {
            return sendRequest();
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse response = new ApiResponse("", HttpURLConnection.HTTP_BAD_REQUEST, true);
            return response;
        }
    }

    private ApiResponse sendRequest() throws IOException {
        HttpURLConnection conn = createConnection();
        if (requestProperties.hasRequestOutput()) {
            writeToUrlConnection(conn);
        }

        int responseCode = conn.getResponseCode();

        String responseBody = readResponse(conn);
        conn.disconnect();

        boolean isError = false;
        if (responseCode != 200 && responseCode != 201) {
            isError = true;
        }

        ApiResponse response = new ApiResponse(responseBody, responseCode, isError);
        return response;
    }

    private HttpURLConnection createConnection() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) requestProperties.getRequestURL().openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        ArrayList<BasicNameValuePair> headerProperties = requestProperties.getHeaderProperties();
        if (!headerProperties.isEmpty()) {
            for (BasicNameValuePair propertyNameValue : headerProperties) {
                conn.setRequestProperty(propertyNameValue.getName(), propertyNameValue.getValue());
            }
        }

        if (requestProperties.isPostRequest()) {
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Connection", "Keep-Alive");
            if (requestProperties.isMultipartRequest()) {
                //TODO: przy wrzucaniu pliku długość powinna być podawana
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=".concat(requestProperties.getHttpBoundary()));
            } else {
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            }
        } else if (requestProperties.isGetRequest()){
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
        } else if (requestProperties.isPutRequest()) {
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        }

        return conn;
    }

    private void writeToUrlConnection(HttpURLConnection conn) throws IOException {
        OutputStream os = conn.getOutputStream();

        if (requestProperties.isMultipartRequest()) {
            writeMultipartEntity(os);
        } else {
            if (requestProperties.hasJsonBody()) {
                writeJsonBody(os);
            }
        }

        os.close();
    }

    private void writeMultipartEntity(OutputStream os) throws IOException {
        String httpBoundary = requestProperties.getHttpBoundary();
        Context context = requestProperties.getAppContext();

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entityBuilder.setBoundary(httpBoundary);
        entityBuilder.setCharset(Charset.forName("UTF-8"));

        //entityBuilder.addPart("uploadFile", new StringBody(requestProperties.getJsonBody(), ContentType.APPLICATION_JSON));

        Bitmap originalImage;
        ByteArrayOutputStream compressedImageStream;
        InputStream partInputStream;
        String fileName;
        int counter = 1;
        for (Uri imageUri:requestProperties.getFilesNamesToUpload()) {
            InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            originalImage = BitmapFactory.decodeStream(imageStream, null, options);

            compressedImageStream = new ByteArrayOutputStream();
            originalImage.compress(Bitmap.CompressFormat.JPEG, 50, compressedImageStream);

            partInputStream = new ByteArrayInputStream(compressedImageStream.toByteArray());
            fileName = "photo".concat(String.valueOf(counter)).concat(".jpg");
            entityBuilder.addPart("uploadFile", new InputStreamBody(partInputStream, fileName));
            counter++;
        }

        HttpEntity httpEntity = entityBuilder.build();
        httpEntity.writeTo(os);
    }

    private void writeJsonBody(OutputStream os) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(requestProperties.getJsonBody());
        writer.flush();
        writer.close();
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = br.readLine()) != null) {
            sb.append(line.concat("\n"));
        }
        br.close();
        return sb.toString();
    }

    @Override
    protected void onPostExecute(ApiResponse result) {
        callbackHandler.onComplete(result);
        super.onPostExecute(result);
    }
}