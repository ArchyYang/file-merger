package Parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class HTMLParser extends Parser {

    public static final String HTML = "html";
    public static final String HTML_TABLE = "table";
    public static final String HTML_TABLE_ROW = "tr";
    public static final String HTML_TABLE_COLUMN = "td";
    public static final String HTML_TABLE_HEADER = "th";
    public static final String TABLE_ID = "#directory";
    public static final String UTF_8 = "utf-8";

    private Iterator<Element> tableRowIterator;
    static
    {
        ParserFactory.registerParser(HTML, new HTMLParser());
    }

    public HTMLParser() {

    }

    public HTMLParser(Iterator<Element> tableRowIterator) {
        this.tableRowIterator = tableRowIterator;
    }

    public Parser createParser(String fileName) {
        try {
            Document doc = Jsoup.parse(new File(fileName), UTF_8);
            Element table = doc.select(HTML_TABLE + TABLE_ID).first();
            Elements rows = table.select(HTML_TABLE_ROW);
            tableRowIterator = rows.iterator();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new HTMLParser(tableRowIterator);
    }

    public List<String> readNext() {
        if (tableRowIterator.hasNext()) {
            Element row = tableRowIterator.next();
            Elements vals;
            if (!row.select(HTML_TABLE_HEADER).isEmpty()) {
                vals = row.select(HTML_TABLE_HEADER);
            }
            else {
                vals = row.select(HTML_TABLE_COLUMN);
            }
            Iterator<Element> iterator = vals.iterator();
            List<String> res = new ArrayList<>();
            while (iterator.hasNext()) {
                res.add(iterator.next().text());
            }
            return res;
        }
        return null;
    }

    public void closeReader() {
        return;
    }

}
