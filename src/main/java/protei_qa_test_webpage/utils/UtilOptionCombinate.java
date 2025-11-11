package protei_qa_test_webpage.utils;

import java.util.ArrayList;
import java.util.List;

public class UtilOptionCombinate {

    public static Object[][] combinate(Object[][] group1, Object[][] group2) {

        List<Object[]> combinations = new ArrayList<>();

        for (Object[] g1 : group1) {
            boolean check11 = (Boolean) g1[0];
            boolean check12 = (Boolean) g1[1];
            String expectedChoice1 = (String) g1[2];

            for (Object[] g2 : group2) {
                String option2Value = (String) g2[0];
                String expectedChoice2 = option2Value;

                combinations.add(new Object[]{
                        check11,
                        check12,
                        option2Value,
                        expectedChoice1,
                        expectedChoice2
                });
            }
        }

        return combinations.toArray(new Object[0][]);
    }
}
