package edu.ita.honorio.facerecognitionserver;

public class Enums {
    public static final int SRV_OK                        = 0;
    public static final int SRV_CHECK_RESOURCES           = -1;
    public static final int SRV_CREATE_DATABASE           = -2;
    public static final int SRV_INIT_FIREBASE             = -3;
    public static final int SRV_UNEXPECTED_ERROR          = -4;
    public static final long SRV_TIMEOUT                  = 60;
    public static final String SRV_ERROR_MSG_KEY          = "Error";
    public static final String SRV_TITLE_INIT_MSG_KEY     = "Iniciando servidor...";
    public static final String SRV_TITLE_EXIT_MSG_KEY     = "Deteniendo servidor...";
    public static final String SRV_LABEL_MSG_KEY          = "Servidor iniciado. Recibiendo peticiones...";
    public static final String SRV_EXIT_MSG_KEY           = "Servidor detenido...";
    public static final String SRV_POOL_MSG_KEY           = "Pool did not terminate";
    public static final String SRV_RESOURCES_KEY          = "resources";
    public static final String SRV_HAARCASCADE_KEY        = "haarcascade_frontalface_alt2.xml";
    public static final String SRV_FACENET_KEY            = "nn4.small2.v1.t7";
    public static final String SRV_SHAPE_PREDICTOR_KEY    = "shape_predictor_68_face_landmarks.dat";
    public static final String SRV_CONFIG_KEY             = "config.txt";
    public static final String SRV_SERVICE_ACCOUNT_KEY    = "serviceAccountKey.json";
    public static final String SRV_LIBFACE_KEY            = "libfacerecognition.dll";
    public static final String SRV_ENROLL_REQUEST_KEY     = "enroll/request";
    public static final String SRV_ENROLL_RESPONSE_KEY    = "enroll/response";
    public static final String SRV_RECOGNIZE_REQUEST_KEY  = "recognize/request";
    public static final String SRV_RECOGNIZE_RESPONSE_KEY = "recognize/response";
    
    public static final String DB_DATABASE_KEY               = "database.db";
    public static final String DB_JOURNAL_MODE_KEY           = "journal_mode";
    public static final String DB_WAL_KEY                    = "WAL";
    public static final String DB_UNEXPECTED_EMPTY_KEY       = "Unexpected empty field";
    public static final String DB_TABLE_KEY                  = "data";
    public static final String DB_FIELD_ID_KEY               = "id";
    public static final String DB_FIELD_KEY_KEY              = "key";
    public static final String DB_FIELD_FIRST_NAME_KEY       = "firstName";
    public static final String DB_FIELD_LAST_NAME_KEY        = "lastName";
    public static final String DB_FIELD_SECOND_LAST_NAME_KEY = "secondLastName";
    public static final String DB_FIELD_TEMPLATE_KEY         = "template";
    public static final String DB_FIELD_THUMBNAIL_KEY        = "thumbnail";
    
    public static final String CFG_THRESHOLD_KEY    = "THRESHOLD";
    public static final String CFG_DATABASE_URL_KEY = "DATABASE_URL";
    
    public static final int FR_OK                     = 0;
    public static final int FR_DATABASE_ERROR         = -1;
    public static final int FR_EMPTY_DATABASE         = -2;
    public static final int FR_USER_EXISTS            = -3;
    public static final int FR_USER_NOT_FOUND         = -4;
    public static final int FR_EMPTY_IMAGE            = -5;
    public static final int FR_FACE_NOT_FOUND         = -6;
    public static final int FR_CANNOT_SAVE_TEMPLATE   = -7;
    public static final int FR_CANNOT_LOAD_TEMPLATE   = -8;
    public static final int FR_CANNOT_SAVE_THUMBNAIL  = -9;
    public static final int FR_CANNOT_LOAD_LIBRARY    = -10;
    public static final int FR_CANNOT_EXECUTE_LIBRARY = -11;
    public static final int FR_UNEXPECTED_ERROR       = -12;
    public static final String FR_ENROLL_MSG_KEY      = "Usuario registrado correctamente";
    public static final String FR_RECOGNIZE_MSG_KEY   = "Usuario encontrado correctamente";
}
