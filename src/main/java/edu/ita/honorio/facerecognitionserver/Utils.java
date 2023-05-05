package edu.ita.honorio.facerecognitionserver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {
    public static String readFile(String path) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return content;
    }
    
    public static String getJarPath(final Class<?> c) {
        try {
            File file = urlToFile(getLocation(c));
            return file.getParent();
        } catch (Exception ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private static URL getLocation(final Class<?> c) {
        if (c == null) return null;
        
        try {
            final URL codeSourceLocation = c.getProtectionDomain().getCodeSource().getLocation();
            if (codeSourceLocation != null) return codeSourceLocation;
        } catch (final SecurityException | NullPointerException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        final URL classResource = c.getResource(c.getSimpleName() + ".class");
        if (classResource == null) return null;
        
        final String url = classResource.toString();
        final String suffix = c.getCanonicalName().replace('.', '/') + ".class";
        if (!url.endsWith(suffix)) return null;
        
        final String base = url.substring(0, url.length() - suffix.length());
        
        String path = base;

        if (path.startsWith("jar:")) path = path.substring(4, path.length() - 2);
        
        try {
            return new URL(path);
        } catch (final MalformedURLException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private static File urlToFile(final URL url) {
        return url == null ? null : urlToFile(url.toString());
    }
    
    private static File urlToFile(final String url) {
        String path = url;
        if (path.startsWith("jar:")) {
            final int index = path.indexOf("!/");
            path = path.substring(4, index);
        }
        
        try {
            if (isWindows() && path.matches("file:[A-Za-z]:.*")) {
                path = "file:/" + path.substring(5);
            }
            return new File(new URL(path).toURI());
        } 
        catch (final MalformedURLException | URISyntaxException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (path.startsWith("file:")) {
            path = path.substring(5);
            return new File(path);
        }
        throw new IllegalArgumentException();
    }
    
    private static boolean isWindows() {
        return osName().startsWith("Win");
    }
    
    private static String osName() {
        final String osName = System.getProperty("os.name");
        return osName == null ? "Unknown" : osName;
    }
}
