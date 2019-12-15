package Builder;

public abstract class Builder {
    public abstract Builder createBuilder(String file);

    public abstract void close();
}
