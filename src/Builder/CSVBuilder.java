package Builder;


import au.com.bytecode.opencsv.CSVWriter;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CSVBuilder extends Builder {

    private Writer writer;
    private CSVWriter csvWriter;

    static
    {
        BuilderFactory.registerBuilder("csv", new CSVBuilder());
    }

    @Override
    public Builder createBuilder(String file) {
        try {
            writer = Files.newBufferedWriter(Paths.get(file));
            csvWriter = new CSVWriter(writer);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
