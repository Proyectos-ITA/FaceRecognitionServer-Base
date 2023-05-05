package edu.ita.honorio.facerecognitionserver;

public class RecognizeResponse {
    public int code;
    public String message;
    public float score;
    public String key;
    public String firstName;
    public String lastName;
    public String secondLastName;
    public String thumbnail;
    
    public RecognizeResponse() {
        this.code = Enums.FR_UNEXPECTED_ERROR;
        this.message = "";
        this.score =  0.f;
        this.key = "";
        this.firstName = "";
        this.lastName = "";
        this.secondLastName = "";
        this.thumbnail = "";
    }
    
    public RecognizeResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.score =  0.f;
        this.key = "";
        this.firstName = "";
        this.lastName = "";
        this.secondLastName = "";
        this.thumbnail = "";
    }
    
    public RecognizeResponse(int code, String message, float score) {
        this.code = code;
        this.message = message;
        this.score = score;
        this.key = "";
        this.firstName = "";
        this.lastName = "";
        this.secondLastName = "";
        this.thumbnail = "";
    }
    
    public RecognizeResponse(String message, float score, String key, String firstName, String lastName, String secondLastName, String thumbnail) {
        this.code = Enums.FR_OK;
        this.message = message;
        this.score = score;
        this.key = key;
        this.firstName = firstName;
        this.lastName = lastName;
        this.secondLastName = secondLastName;
        this.thumbnail = thumbnail;
    }
}
