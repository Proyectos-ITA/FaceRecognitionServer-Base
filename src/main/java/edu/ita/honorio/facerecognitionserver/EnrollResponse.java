package edu.ita.honorio.facerecognitionserver;

public class EnrollResponse {
    public int code;
    public String message;
    public String thumbnail;
    
    public EnrollResponse() {
        this.code = Enums.FR_UNEXPECTED_ERROR;
        this.message = "";
        this.thumbnail = "";
    }
    
    public EnrollResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.thumbnail = "";
    }
    
    public EnrollResponse(String message, String thumbnail) {
        this.code = Enums.FR_OK;
        this.message = message;
        this.thumbnail = thumbnail;
    }
}
