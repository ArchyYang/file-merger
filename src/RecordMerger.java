import Parser.ParserFactory;
import Parser.Parser;
import Parser.CSVParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RecordMerger {

	public static final String FILENAME_COMBINED = "combined.csv";
	public static final String DATA_DIR = "data/";
	public static final String ID_HEADER = "ID";
	public static final String PLACEHOLDER = "";

	static
	{
		try
		{
			Class.forName("Parser.CSVParser");
			Class.forName("Parser.HTMLParser");
		}
		catch (ClassNotFoundException any)
		{
			any.printStackTrace();
		}
	}

	/**
	 * Entry point of this test.
	 *
	 * @param args command line arguments: first.html and second.csv.
	 * @throws Exception bad things had happened.
	 */
	public static void main(final String[] args) throws Exception {

		if (args.length == 0) {
			System.err.println("Usage: java RecordMerger file1 [ file2 [...] ]");
			System.exit(1);
		}

		List<Parser> parsers = new ArrayList<Parser>();
		for(int i = 0; i < args.length; i++) {
			Parser parser = ParserFactory.createParser(DATA_DIR + args[i]);
			if (parser == null) {
				System.out.println("File type not supported for " + args[i]);
				continue;
			}
			parsers.add(parser);
		}
		Map<String, List> table = new HashMap<>();
		Set<String> headerSet = new LinkedHashSet<>();
		Map<String, Integer> headerIndexMap = new HashMap<>();
		for (Parser parser : parsers) {
			Map<String, List> rowsMap = new HashMap<>();
			List<String> headers = parser.readNext();
			if (headers == null) {
				continue;
			}
			int idIndex = headers.indexOf(ID_HEADER);
			List<String> newHeaders = headers.stream().filter(header -> !headerSet.contains(header)).collect(Collectors.toList());;
			List<String> line;
			while ((line = parser.readNext()) != null) {
				String id = line.get(idIndex);
				List<String> filteredLine = new ArrayList<>();

				// Filter out duplicate and id columns
				for (int i = 0; i < line.size(); i++) {
					String currentHeader = headers.get(i);
					if (!headerSet.contains(currentHeader)){
						filteredLine.add(line.get(i));
					}
					else {
						List<String> row = rowsMap.get(id);
						int currentColIndex = headerIndexMap.get(currentHeader);
						if (row != null && row.get(currentColIndex).isEmpty()) {
							row.set(currentColIndex, line.get(i));
						}
					}
				}
				// update row values based on id
				rowsMap.put(id, filteredLine);
			}

			//create a list of empty str as placeholder for Parsed ID
			List<String> emptyStrings = new ArrayList<>();
			for (int i = 0; i < newHeaders.size(); i++) {
				headerIndexMap.put(newHeaders.get(i), headerSet.size()+i);
				emptyStrings.add(PLACEHOLDER);
			}

			int existRowSize = 0;
			Set<String> keys = table.keySet();
			for (String key : keys) {
				List row = table.get(key);
				existRowSize = row.size();
				if (rowsMap.containsKey(key)) {
					row.addAll(rowsMap.get(key));
					table.put(key, row);
					rowsMap.remove(key);
				}
				else {
					row.addAll(emptyStrings);
					table.put(key, row);
				}
			}

			//create a list of empty str as placeholder
			emptyStrings = new ArrayList<>();
			for (int i = 0; i < existRowSize; i++) {
				emptyStrings.add(PLACEHOLDER);
			}
			keys = rowsMap.keySet();
			for (String key : keys) {
				List<String> row = new ArrayList<>();
				row.addAll(emptyStrings);
				row.addAll(rowsMap.get(key));
				table.put(key, row);
			}

			// update columns
			headerSet.addAll(newHeaders);

			parser.closeReader();
		}
		System.out.println(table);
		System.out.println(headerSet);

	}
}
