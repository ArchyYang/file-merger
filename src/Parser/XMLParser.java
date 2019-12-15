package Parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XMLParser extends Parser {
    private static final String XML ="xml";
    private static final String XML_TABLE ="table";
    private static final String XML_PERSON ="person";
    private static final String UTF_8 ="utf-8";

    private Iterator<Element> tableRowIterator;
    private List<String> headers;
    private List<String> firstRow;
    static
    {
        ParserFactory.registerParser(XML, new XMLParser());
    }

    public XMLParser() {

    }

    public XMLParser(Iterator<Element> tableRowIterator) {
        this.tableRowIterator = tableRowIterator;
    }

    public Parser createParser(String fileName) {
        try {
            Document doc = Jsoup.parse(new FileInputStream(fileName), UTF_8, "", org.jsoup.parser.Parser.xmlParser());
            Element table = doc.select(XML_TABLE).first();
            Elements rows = table.select(XML_PERSON);
            tableRowIterator = rows.iterator();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new XMLParser(tableRowIterator);
    }

    public List<String> readNext() {
        if (headers != null && firstRow != null) {
            List<String> res = new ArrayList<>(firstRow);
            firstRow = null;
            return res;
        }
        if (tableRowIterator.hasNext()) {
            Element row = tableRowIterator.next();
            Iterator<Element> iterator = row.children().iterator();
            if (headers == null) {
                return getHeaders(iterator);
            }
            return getValues(iterator);
        }
        return null;
    }

    public void closeReader() {
        return;
    }

    private List<String> getHeaders(Iterator<Element> iterator){
        headers = new ArrayList<>();
        firstRow = new ArrayList<>();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            headers.add(e.tagName().toUpperCase());
            firstRow.add(e.text());
        }
        return headers;
    }

    private List<String> getValues(Iterator<Element> iterator) {
        List<String> res = new ArrayList<>();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            res.add(e.text());
        }
        return res;
    }
}
