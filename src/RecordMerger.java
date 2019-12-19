import Builder.BuilderFactory;
import Parser.ParserFactory;
import Parser.Parser;
import Builder.Builder;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RecordMerger {

	public static final String FILENAME_COMBINED = "combined.csv";
	public static final String DATA_DIR = "data/";
	public static final String ID_HEADER = "ID";

	static
	{
		try
		{
			Class.forName("Parser.CSVParser");
			Class.forName("Parser.HTMLParser");
			Class.forName("Parser.XMLParser");
			Class.forName("Builder.CSVBuilder");
		}
		catch (ClassNotFoundException any)
		{
			any.printStackTrace();
		}
	}

	/**
	 *
	 * Edit Run Configurations in IDE with input file names in program arguments.
	 *
	 * input files should be put under project/data. The merged file will also go under project/data.
	 *
	 * @param args command line arguments: first.html and second.csv.
	 * @throws Exception bad things had happened.
	 */
	public static void main(final String[] args) throws Exception {

		if (args.length == 0) {
			System.err.println("Usage: java RecordMerger file1 file2 ...");
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
		/**
		 *  Store id to all column values mapping
		 */
		Map<String, List> table = new HashMap<>();

		/**
		 *  Store all non-duplicate headers
		 */
		Set<String> headerSet = new LinkedHashSet<>();

		/**
		 *  Store old headers index to value mapping
		 */
		Map<String, Integer> headerIndexMap = new HashMap<>();

		// Loop all parsers. One parser points to one file.
		for (Parser parser : parsers) {
			List<String> headers = parser.readNext();
			// Skip the file without headers.
			if (headers == null) {
				continue;
			}
			// find ID header index in header list and filter out duplicates from header list.
			int idIndex = headers.indexOf(ID_HEADER);
			List<String> newHeaders = headers.stream().filter(header -> !headerSet.contains(header)).collect(Collectors.toList());

			// read line by line, or row by row in html file.
			List<String> line;
			while ((line = parser.readNext()) != null) {
				String id = line.get(idIndex);
				// For incoming new IDs, create a list of empty strings.
				// For incoming existing IDs, get a existing column values.
				List<String> placeholders = Utils.getEmptyStringsList(headerSet.size());
				List<String> existingLine = table.get(id);
				existingLine = existingLine == null ? placeholders : existingLine;

				// filter out duplicate headers line values
				List<String> filteredLine = new ArrayList<>();
				for (int i = 0; i < line.size(); i++) {
					String currentHeader = headers.get(i);
					if (!headerSet.contains(currentHeader)){
						filteredLine.add(line.get(i));
					}
					else {
						// if the duplicate header has empty string column value, update the new value.
						int index = headerIndexMap.get(currentHeader);
						if (existingLine.get(index) == Utils.PLACEHOLDER) {
							existingLine.set(index, line.get(i));
						}
					}
				}

				// merge new line and existing line by id.
				if (existingLine != null) {
					existingLine.addAll(filteredLine);
				}
				else {
					existingLine = filteredLine;
				}
				table.put(id, existingLine);
			}

			// update new headers index to value mapping.
			for (int i = 0; i < newHeaders.size(); i++) {
				headerIndexMap.put(newHeaders.get(i), headerSet.size()+i);
			}
			// update columns set
			headerSet.addAll(newHeaders);

			// put empty strings for old ids that does not have new column values.
			List<String> placeholders = Utils.getEmptyStringsList(newHeaders.size());
			for (String key : table.keySet()) {
				List<String> row = table.get(key);
				if (row.size() <  headerSet.size()) {
					row.addAll(placeholders);
					table.put(key, row);
				}
			}
			parser.closeReader();
		}

		Builder builder = BuilderFactory.createBuilder(DATA_DIR+FILENAME_COMBINED);
		builder.writeNext(headerSet);
		List<String> sortedKeys = Utils.getSortedMapKeys(table);
		for (String key : sortedKeys) {
			builder.writeNext(table.get(key));
		}
		builder.close();

	}
}
