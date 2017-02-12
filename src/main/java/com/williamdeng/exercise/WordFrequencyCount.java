package com.williamdeng.exercise;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author William Deng - Sep 11, 2016
 */
public class WordFrequencyCount {

	String RecordFileName = "src/main/resources/records.txt";
	String QueryFileName = "src/main/resources/queries.txt";
	String OutputFileName = "src/main/resources/output.txt";
	List<List<String>> records = new ArrayList<List<String>>();
	List<List<String>> queries = new ArrayList<List<String>>();
	Map<List<String>, List<String>> outputs = new LinkedHashMap<List<String>, List<String>>();

	@SuppressWarnings("resource")
	public void loadRecords() throws IOException {
		double s_time = System.currentTimeMillis();
		System.out.println("Loading Records...");
		// Java 8 File Stream
		Stream<String> lines = Files.lines(Paths.get(RecordFileName));
		lines.map(record -> {
			String[] recWords = record.split(",");
			return Arrays.asList(recWords);
		}).forEach(records::add);
		lines.close();
		System.out.println(">>> Total Records:  " + records.size() + " Loaded in "
				+ (System.currentTimeMillis() - s_time) / 1000 + " Seconds  \n");

	}

	public void loadQueries() throws IOException {
		for (String query : java.nio.file.Files.readAllLines(Paths.get(QueryFileName), StandardCharsets.UTF_8)) {
			String[] queryWords = query.split(",");
			List<String> queryWordsList = new ArrayList<String>(Arrays.asList(queryWords));
			queries.add(queryWordsList);
		}
	}

	/**
	 * @param queryNewWords
	 * @return String
	 */
	public String formatOutput(List<String> queryNewWords) {
		String outputline = new String();
		// a) get counts of each new words
		Map<String, Long> map = queryNewWords.stream().collect(Collectors.groupingBy(m -> m, Collectors.counting()));
		// b) format results for output
		TreeMap<String, Long> treeMap = new TreeMap<String, Long>(map);
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Long> entry : treeMap.entrySet()) {
			sb.append("\"" + entry.getKey() + "\":" + " " + entry.getValue() + ", ");
		}
		outputline = "{" + sb.toString().substring(0, sb.toString().length() - 2) + "}";
		return outputline;
	}


	/**
	 * @param queryWordsList
	 * @return List<String>
	 */
	public List<String> getWordFrequency(List<String> queryWordsList) {
		// get new words per Query by Java 8 Stream
		List<String> queryNewWords = records.stream().filter(lst -> lst.containsAll(queryWordsList))
				.flatMap(lst -> lst.stream()).collect(Collectors.toList());
		queryNewWords.removeAll(queryWordsList);
		return queryNewWords;
	}

	public void wordFrequencyProc() {
		for (List<String> queryWordsList : queries) {
			outputs.put(queryWordsList, getWordFrequency(queryWordsList));
		}
	}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void sendOutput() throws FileNotFoundException, IOException {
		File outputFile = new File(OutputFileName);
		try (FileOutputStream fop = new FileOutputStream(new File(OutputFileName))) {
			// if file doesn't exists, then create it
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			outputs.forEach((k, v) -> {
				String outputline = formatOutput(v) + " \n";
				System.out.println("Query             : " + k + "\n" + "New Word Frequency: " + outputline);
				try {
					fop.write(outputline.getBytes());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			fop.flush();
			fop.close();
		}
	}

	public static void main(String[] args) throws IOException {

		double s_time = System.currentTimeMillis();

		WordFrequencyCount wfc = new WordFrequencyCount();

		wfc.RecordFileName = "src/main/resources/records.txt";
		wfc.QueryFileName = "src/main/resources/queries.txt";
		wfc.OutputFileName = "src/main/resources/output.txt";

		System.out.println("RecordsFile = " + Paths.get(wfc.RecordFileName));
		System.out.println("QueriesFile = " + Paths.get(wfc.QueryFileName));
		System.out.println("OutputFile = " + Paths.get(wfc.OutputFileName));

		// 1. Create Data Structure List<List<String>> for Records
		wfc.loadRecords();

		// 2. Get new words per Query
		wfc.loadQueries();

		// 3. get Word Frequency Counts
		wfc.wordFrequencyProc();

		// 4. set outout to file
		wfc.sendOutput();

		System.out.println(">>> Job took " + (System.currentTimeMillis() - s_time) / 1000 + " Seconds to complete.");

	}

}
