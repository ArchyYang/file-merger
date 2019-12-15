package Builder;


import au.com.bytecode.opencsv.CSVWriter;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class CSVBuilder extends Builder {

    private Writer writer;
    private CSVWriter csvWriter;

    static
    {
        BuilderFactory.registerBuilder("csv", new CSVBuilder());
    }

    public CSVBuilder() {

    }

    public CSVBuilder(String filePath) throws IOException {
        writer = Files.newBufferedWriter(Paths.get(filePath));
        csvWriter = new CSVWriter(writer);
    }

    @Override
    public Builder createBuilder(String filePath) {
        try {
            return new CSVBuilder(filePath);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void writeNext(Collection<String > line) {
        csvWriter.writeNext(line.toArray(new String[line.size()]));
    }

    @Override
    public void close() {
        try {
            if (writer != null) {
                writer.close();
            }

            if (csvWriter != null) {
                csvWriter.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
