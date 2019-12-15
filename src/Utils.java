import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Utils {
    public static final String PLACEHOLDER = "";

    public static List<String> getEmptyStringsList(int length) {
        if (length == 0) {
            return null;
        }
        List<String> res = new ArrayList<>();
        for (int i = 0; i<length; i++) {
            res.add(PLACEHOLDER);
        }
        return res;
    }

    public static List<String> getSortedMapKeys(Map<String, List> map) {
        List<String> keys=new ArrayList(map.keySet());
        Collections.sort(keys);
        return keys;
    }

}
