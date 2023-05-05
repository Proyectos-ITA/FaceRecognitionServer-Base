package edu.ita.honorio.facerecognitionserver;

public class EnrollRequest {
    public String key;
    public String firstName;
    public String lastName;
    public String secondLastName;
    public String image;
    
    public EnrollRequest() {
        this.key = null;
        this.firstName = null;
        this.lastName = null;
        this.secondLastName = null;
        this.image = null;
    }
    
    public EnrollRequest(String key, String firstName, String lastName, String secondLastName, String image) {
        this.key = key;
        this.firstName = firstName;
        this.lastName = lastName;
        this.secondLastName = secondLastName;
        this.image = image;
    }
}
