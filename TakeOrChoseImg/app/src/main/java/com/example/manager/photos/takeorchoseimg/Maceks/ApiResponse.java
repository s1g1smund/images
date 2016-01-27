package com.example.manager.photos.takeorchoseimg.Maceks;
import java.util.ArrayList;

public class ApiResponse {
    private String responseBody;
    private int responseCode;
    private ArrayList<? extends IGetResponse> decodedResponse;
    private boolean isError;

    public ApiResponse(String responseBody, int responseCode, boolean isError) {
        this.responseBody = responseBody;
        this.responseCode = responseCode;
        this.decodedResponse = new ArrayList<>();
        this.isError = isError;
    }

    public String getResponceBody() {
        return responseBody;
    }

    public int getResponceCode() {
        return responseCode;
    }

    public ArrayList<? extends IGetResponse> getDecodedResponse() {
        return decodedResponse;
    }

    public boolean isError() {
        return isError;
    }

    public void setDecodedResponse(ArrayList<? extends IGetResponse> decodedResponse) {
        this.decodedResponse = decodedResponse;
    }
}
