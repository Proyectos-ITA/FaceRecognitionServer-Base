package edu.ita.honorio.facerecognitionserver;

public class RecognizeRequest {
    public String image;
    
    public RecognizeRequest() {
        this.image = null;
    }
    
    public RecognizeRequest(String image) {
        this.image = image;
    }
}
