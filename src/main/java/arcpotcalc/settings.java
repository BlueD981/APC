package arcpotcalc;

import java.io.*;
import java.util.Properties;

public class settings {
    private static File SETTINGS = new File(System.getProperty("user.home"), ".APC/settings.properties");

    private static Properties settingProperties = new Properties();

    static {
        loadSettings();
    }

    public static void loadSettings() {
        if (SETTINGS.exists()) {
            try (FileInputStream fis = new FileInputStream(SETTINGS)) {
                settingProperties.load(fis);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("실패");
            }
        }
    }

    public static void saveSettings() {
        File parentDir = SETTINGS.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(SETTINGS)) {
            settingProperties.store(fos, "Application Settings");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("실패");
        }
    }

    public static String get(String key, String defaultValue) {
        return settingProperties.getProperty(key, defaultValue);
    }

    public static void set(String key, String value) {
        settingProperties.setProperty(key, value);
        saveSettings();
    }
}
