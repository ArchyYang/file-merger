package Parser;

import java.util.List;

public abstract class Parser {

    public abstract Parser createParser(String file);

    public abstract List<String> readNext();

    public abstract void closeReader();
}
