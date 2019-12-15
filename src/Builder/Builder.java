package Builder;

import java.util.Collection;

public abstract class Builder {

    public abstract Builder createBuilder(String file);

    public abstract void writeNext(Collection<String> line);

    public abstract void close();
}
