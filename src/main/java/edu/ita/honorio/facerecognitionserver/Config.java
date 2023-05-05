package edu.ita.honorio.facerecognitionserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {
    public float threshold = -1.f;
    public String databaseUrl = null;
    
    public static Config load(String path) {
        try {
            InputStream is = new FileInputStream(path);
            Properties prop = new Properties();
            prop.load(is);
            
            Config cfg = new Config();
            cfg.threshold = Float.parseFloat(prop.getProperty(Enums.CFG_THRESHOLD_KEY));
            if (cfg.threshold < 0 || cfg.threshold > 1) {
                return null;
            }
            
            cfg.databaseUrl = prop.getProperty(Enums.CFG_DATABASE_URL_KEY);
            if (cfg.databaseUrl == null) {
                return null;
            }
            return cfg;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | NumberFormatException | NullPointerException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }
}
