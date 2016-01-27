package com.example.manager.photos.takeorchoseimg.Maceks;

import android.content.Context;
import android.net.Uri;

import org.apache.http.message.BasicNameValuePair;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NetworkRequestProperties {
    public enum HTTP_METHOD {
        GET,
        POST,
        PUT,
        DELETE
    }

    private HTTP_METHOD httpMethod;
    private URL requestURL;
    private String jsonBody;
    private ArrayList<Uri> filesToUploadPaths;
    private ArrayList<BasicNameValuePair> headerProperties;
    private Context appContext;
    private String httpBoundary;
    private boolean isMultipartRequest;

    public NetworkRequestProperties(HTTP_METHOD httpMethod, Uri requestAddress) throws MalformedURLException {
        this.httpMethod = httpMethod;
        requestURL = new URL(requestAddress.toString());
        filesToUploadPaths = new ArrayList<>();
        headerProperties = new ArrayList<>();
        isMultipartRequest = false;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    public void setFilesNamesToUpload(ArrayList<Uri> filesToUploadPaths, Context context) {
        this.filesToUploadPaths = filesToUploadPaths;
        appContext = context.getApplicationContext();
        httpBoundary = "-------" + String.valueOf(Math.random() * 10e10) + "-------";
        isMultipartRequest = true;
    }

    public void addHeaderProperty(String name, String value) {
        headerProperties.add(new BasicNameValuePair(name, value));
    }

    public boolean hasJsonBody() {
        return jsonBody != null;
    }

    public boolean hasFilesToUpload() {
        return !filesToUploadPaths.isEmpty();
    }

    public Context getAppContext() {
        return appContext;
    }

    public HTTP_METHOD getHttpMethod() {
        return httpMethod;
    }

    public URL getRequestURL() {
        return requestURL;
    }

    public String getJsonBody() {
        return jsonBody;
    }

    public ArrayList<Uri> getFilesNamesToUpload() {
        return filesToUploadPaths;
    }

    public String getHttpBoundary() {
        return httpBoundary;
    }

    public ArrayList<BasicNameValuePair> getHeaderProperties() {
        return headerProperties;
    }

    public void forceMultipartEntity() {
        isMultipartRequest = true;
    }

    public boolean isMultipartRequest() {
        return isMultipartRequest;
    }

    public boolean hasRequestOutput() {
        if (isPostRequest() || isPutRequest()) {
            return true;
        }
        return false;
    }

    public boolean isPostRequest() {
        if (httpMethod == HTTP_METHOD.POST)
            return true;
        return false;
    }

    public boolean isGetRequest() {
        if (httpMethod == HTTP_METHOD.GET)
            return true;
        return false;
    }

    public boolean isPutRequest() {
        if (httpMethod == HTTP_METHOD.PUT)
            return true;
        return false;
    }

    public boolean isDeleteRequest() {
        if (httpMethod == HTTP_METHOD.DELETE)
            return true;
        return false;
    }
}
