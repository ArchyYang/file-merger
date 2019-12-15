import Parser.ParserFactory;
import Parser.Parser;
import Parser.CSVParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		for (Parser parser : parsers) {
			Map<String, List> rowsMap = new HashMap<>();
			List<String> headers = parser.readNext();
			if (headers == null) {
				continue;
			}

			int idIndex = -1;
			List<String> line;
			List<String> filteredHeader = new ArrayList<>(headers);
			while ((line = parser.readNext()) != null) {
				String id = null;
				List<String> filteredLine = new ArrayList<>();

				// Filter out duplicate and id columns
				for (int i = 0; i < line.size(); i++) {
					if (id == null && ID_HEADER.equals(headers.get(i))) {
						id = line.get(i);
						filteredHeader.remove(ID_HEADER);
						continue;
					}
					if (!headerSet.contains(headers.get(i))){
						filteredLine.add(line.get(i));
					}
					else {
						filteredHeader.remove(headers.get(i));
					}
				}

				// update row values based on id
				rowsMap.put(id, filteredLine);


				/*if (rowsMap.containsKey(id)) {
					List existingVals = rowsMap.get(id);
					existingVals.addAll(filteredLine);
					rowsMap.put(id, existingVals);
				} else {
					rowsMap.put(id, filteredLine);
				}*/

			}

			//create a list of empty str as placeholder for Parsed ID
			List<String> emptyStrings = new ArrayList<>();
			for (int i = 0; i < filteredHeader.size(); i++) {
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
			headerSet.addAll(filteredHeader);
			parser.closeReader();
		}
		System.out.println(table);
		System.out.println(headerSet);

	}
}
