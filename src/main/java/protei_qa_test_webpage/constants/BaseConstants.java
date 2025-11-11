package protei_qa_test_webpage.constants;

import protei_qa_test_webpage.utils.ConfigReader;

public class BaseConstants {

    public static final String BASE_URL = ConfigReader.getProperty("base.url");
    public static final String EMAIL = ConfigReader.getProperty("login.email");
    public static final String PASSWORD = ConfigReader.getProperty("login.password");
}
