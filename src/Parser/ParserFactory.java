package Parser;

import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;
import java.util.Map;

public class ParserFactory {

    private static Map<String, Parser> registeredParser= new HashMap<>();

    public static void registerParser(String extension, Parser parser) {
        registeredParser.put(extension, parser);
    }

    public static Parser createParser(String fileName) {
        Parser parser = registeredParser.get(FilenameUtils.getExtension(fileName));
        if (parser != null ) {
            return parser.createParser(fileName);
        }
        return null;
    }
}
