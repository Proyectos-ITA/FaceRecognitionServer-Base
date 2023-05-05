package edu.ita.honorio.facerecognitionserver;

import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FaceRecognitionEngine {
    private static final String m_jarPath;
    private static boolean m_loaded = false;
    
    static {
        m_jarPath = Utils.getJarPath(FaceRecognitionEngine.class);
        if(m_jarPath != null) {
            try {
                System.load(Paths.get(m_jarPath, Enums.SRV_LIBFACE_KEY).toString());
                m_loaded = true;
            } catch (SecurityException | UnsatisfiedLinkError | NullPointerException ex) {
                Logger.getLogger(FaceRecognitionEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static boolean isLoaded() { return m_loaded; }
    
    public static EnrollResponse enroll(EnrollRequest request) {
        try (FaceRecognition fr = new FaceRecognition(m_jarPath)) {
            return fr.enroll(request);
        } catch (Exception ex) {
            return FaceRecognition.getErrorEnroll(Enums.FR_UNEXPECTED_ERROR, ex);
        }
    }
    
    public static RecognizeResponse recognize(RecognizeRequest request, float threshold) {
        try (FaceRecognition fr = new FaceRecognition(m_jarPath)) {
            return fr.recognize(request, threshold);
        } catch (Exception ex) { 
            return FaceRecognition.getErrorRecognize(Enums.FR_UNEXPECTED_ERROR, ex);
        }
    }
}
