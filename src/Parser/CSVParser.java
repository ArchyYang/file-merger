package Parser;

import au.com.bytecode.opencsv.CSVReader;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVParser extends Parser {

    private CSVReader csvReader;
    private Reader reader;

    static
    {
        ParserFactory.registerParser("csv", new CSVParser());
    }

    public CSVParser() {

    }

    public CSVParser(String file) throws IOException {
        reader = Files.newBufferedReader(Paths.get(file));
        csvReader = new CSVReader(reader);

    }

    public Parser createParser(String file) {
        try {
            return new CSVParser(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> readNext() {
        try {
            String[] row = csvReader.readNext();
            if (row != null) {
                return new ArrayList<String>(Arrays.asList(row));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void closeReader() {
        try {
            reader.close();
            csvReader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getTitle() {

    }

}
