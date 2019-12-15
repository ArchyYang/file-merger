package Builder;

import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;
import java.util.Map;

public class BuilderFactory {

    private static Map<String, Builder> registeredBuilder = new HashMap<>();

    public static void registerBuilder(String extension, Builder writer) {
        registeredBuilder.put(extension, writer);
    }

    public static Builder createBuilder(String fileName) {
        Builder builder = registeredBuilder.get(FilenameUtils.getExtension(fileName));
        if (builder != null ) {
            return builder.createBuilder(fileName);
        }
        return builder;
    }
}
