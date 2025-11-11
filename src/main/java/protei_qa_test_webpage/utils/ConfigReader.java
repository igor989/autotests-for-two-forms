package protei_qa_test_webpage.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Не удается найти config.properties");
            }

            properties.load(input);

            for(Object key : properties.keySet()) {
                if(properties.getProperty(key.toString()).isEmpty())
                    throw new IllegalArgumentException("Поле "+key+" пустое");
            }

        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить файл config.properties", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}