package edu.ita.honorio.facerecognitionserver;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FaceRecognition implements AutoCloseable {
    private String m_jarPath = null;
    private long m_handle = 0;
    private boolean m_valid = false;
    
    public FaceRecognition(String jarPath) {
        m_jarPath = jarPath;
        
        if (!FaceRecognitionEngine.isLoaded()) {
            return;
        }
        
        try {
            m_handle = nativeConstructor(m_jarPath);
            m_valid = true;
        } catch (Exception ex) {
            Logger.getLogger(FaceRecognition.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void close() throws Exception {
        if (!m_valid) {
            return;
        }
        
        try {
            nativeDestructor(m_handle);
        } finally {
            m_handle = 0;
            m_valid = false;
        }
    }
    
    public EnrollResponse enroll(EnrollRequest request) {
        if (!m_valid) {
            return getErrorEnroll(Enums.FR_CANNOT_LOAD_LIBRARY);
        }
        
        try (Database db = new Database(m_jarPath)) {
            if (db.exists(request.key)) {
                return getErrorEnroll(Enums.FR_USER_EXISTS);
            }
        } catch (SQLException ex) { 
            Logger.getLogger(FaceRecognition.class.getName()).log(Level.SEVERE, null, ex);
            return getErrorEnroll(Enums.FR_DATABASE_ERROR, ex);
        }
        
        String json;
        try {
            json = nativeCreateTemplate(m_handle, request.image);
            if (json == null || json.isEmpty()) {
                return getErrorEnroll(Enums.FR_CANNOT_EXECUTE_LIBRARY);
            }
        } catch (Exception ex) {
            Logger.getLogger(FaceRecognition.class.getName()).log(Level.SEVERE, null, ex);
            return getErrorEnroll(Enums.FR_UNEXPECTED_ERROR, ex);
        }
        
        EnrollResponseNative responseNative;
        try {
            Gson gson = new Gson();
            responseNative = gson.fromJson(json, EnrollResponseNative.class);
        } catch (JsonSyntaxException ex) {
            Logger.getLogger(FaceRecognition.class.getName()).log(Level.SEVERE, null, ex);
            return getErrorEnroll(Enums.FR_UNEXPECTED_ERROR, ex);
        }
        
        if (responseNative.code != Enums.FR_OK) {
            return getErrorEnroll(responseNative.code);
        }
        
        try (Database db = new Database(m_jarPath)) {
            db.insert(request, responseNative);
        } catch (SQLException ex) { 
            Logger.getLogger(FaceRecognition.class.getName()).log(Level.SEVERE, null, ex);
            return getErrorEnroll(Enums.FR_DATABASE_ERROR, ex);
        }
        
        return new EnrollResponse(Enums.FR_ENROLL_MSG_KEY, responseNative.thumbnail);
    }
    
    public RecognizeResponse recognize(RecognizeRequest request, float threshold) {
        if (!m_valid) {
            return getErrorRecognize(Enums.FR_CANNOT_LOAD_LIBRARY);
        }
        
        List<Record> records;
        try (Database db = new Database(m_jarPath)) {
            records = db.selectAll();
        } catch (SQLException ex) { 
            Logger.getLogger(FaceRecognition.class.getName()).log(Level.SEVERE, null, ex);
            return getErrorRecognize(Enums.FR_DATABASE_ERROR, ex);
        }
        
        if (records.isEmpty()) {
            return getErrorRecognize(Enums.FR_EMPTY_DATABASE);
        }
        
        try {
            int code = nativeSetImage(m_handle, request.image);
            if (code != Enums.FR_OK) {
                return getErrorRecognize(code);
            }
        } catch (Exception ex) {
            Logger.getLogger(FaceRecognition.class.getName()).log(Level.SEVERE, null, ex);
            return getErrorRecognize(Enums.FR_UNEXPECTED_ERROR, ex);
        }
        
        int id = -1;
        float minScore = Float.MAX_VALUE;
        try {
            for(Record record : records) {
                float score = nativeCompareTemplate(m_handle, record.template);
                if (score < Enums.FR_OK) {
                    return getErrorRecognize((int)score);
                }
                
                if (score < minScore) {
                    id = record.id;
                    minScore = score;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(FaceRecognition.class.getName()).log(Level.SEVERE, null, ex);
            return getErrorRecognize(Enums.FR_UNEXPECTED_ERROR, ex);
        }
        
        if (minScore > threshold) {
            return new RecognizeResponse(Enums.FR_USER_NOT_FOUND, getErrorMessage(Enums.FR_USER_NOT_FOUND, null), minScore);
        }
        
        try (Database db = new Database(m_jarPath)) {
            return db.select(id, Enums.FR_RECOGNIZE_MSG_KEY, minScore);
        } catch (SQLException ex) { 
            Logger.getLogger(FaceRecognition.class.getName()).log(Level.SEVERE, null, ex);
            return getErrorRecognize(Enums.FR_DATABASE_ERROR, ex);
        }
    }
    
    public static EnrollResponse getErrorEnroll(int code) {
        return getErrorEnroll(code, null);
    }
    
    public static EnrollResponse getErrorEnroll(int code, Exception ex) {
        return new EnrollResponse(code, getErrorMessage(code, ex));
    }
    
    public static RecognizeResponse getErrorRecognize(int code) {
        return getErrorRecognize(code, null);
    }
    
    public static RecognizeResponse getErrorRecognize(int code, Exception ex) {
        return new RecognizeResponse(code, getErrorMessage(code, ex));
    }
    
    private static String getErrorMessage(int code, Exception ex) {
        String message;
        switch (code) {
            case Enums.FR_DATABASE_ERROR:
                message = "Error en la base de datos: " + (ex != null ? ex.getMessage() : "null");
                break;
                
            case Enums.FR_EMPTY_DATABASE:
                message = "La base de datos esta vacia";
                break;
            
            case Enums.FR_USER_EXISTS:
                message = "El usuario ya existe en la base de datos";
                break;
                
            case Enums.FR_USER_NOT_FOUND:
                message = "No se encontro una coincidencia del usuario en la base de datos";
                break;
                
            case Enums.FR_EMPTY_IMAGE:
                message = "La imagen esta vacia o tiene un formato incorrecto";
                break;
                
            case Enums.FR_FACE_NOT_FOUND:
                message = "No se pudo detectar el rostro";
                break;
                
            case Enums.FR_CANNOT_SAVE_TEMPLATE:
                message = "Error al guardar la plantilla facial";
                break;
                
            case Enums.FR_CANNOT_LOAD_TEMPLATE:
                message = "Error al cargar la plantilla facial";
                break;
                
            case Enums.FR_CANNOT_SAVE_THUMBNAIL:
                message = "Error al guardar la imagen thumbnail";
                break;
                
            case Enums.FR_CANNOT_LOAD_LIBRARY:
                message = "Error al cargar la libreria nativa";
                break;
                
            case Enums.FR_CANNOT_EXECUTE_LIBRARY:
                message = "Error al ejecutar la libreria nativa";
                break;
                
            default:
                message = "Error inesperado en el servidor: " + (ex != null ? ex.getMessage() : "null");
                break;
        }
        return message;
    }
    
    private static native long nativeConstructor(String path);
    private static native void nativeDestructor(long handle);
    private static native String nativeCreateTemplate(long handle, String image);
    private static native int nativeSetImage(long handle, String image);
    private static native float nativeCompareTemplate(long handle, String template);
}
