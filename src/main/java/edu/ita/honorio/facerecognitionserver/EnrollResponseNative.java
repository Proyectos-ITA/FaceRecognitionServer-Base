package edu.ita.honorio.facerecognitionserver;

public class EnrollResponseNative {
    public int code;
    public String template;
    public String thumbnail;
    
    public EnrollResponseNative() {
        code = Enums.FR_UNEXPECTED_ERROR;
        template = "";
        thumbnail = "";
    }
}
