package javaFiles;

import java.io.*;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class SearchEngine {
	int count;
	// Regex to find the URL in the text file
	String PATTERN = "(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";

	Map<Integer, String> sources;
	HashMap<String, HashSet<Integer>> index;

	public SearchEngine() {
		// TODO Auto-generated constructor stub
		sources = new HashMap<Integer, String>();
		index = new HashMap<String, HashSet<Integer>>();

	}

	// Function to build an Inverted index
	public void buildIndex(HttpServletRequest request) throws IOException {
		int fileNumber = 1;
		int i = 0;
		while (fileNumber < 1181) {
			String fName = "/WEB-INF/TextFolder/file" + fileNumber + ".txt";
			ServletContext cntxt = request.getServletContext();
			InputStream ins = cntxt.getResourceAsStream(fName);
			try (BufferedReader br = new BufferedReader(new InputStreamReader(ins))) {
				sources.put(i, "file" + fileNumber + ".txt");
				String ln;

				while ((ln = br.readLine()) != null) {
					String[] words = ln.split("\\W+");

					for (String word : words) {
						word = word.toLowerCase();
						if (!index.containsKey(word)) {
							index.put(word, new HashSet<Integer>());

						}
						index.get(word).add(i);

					}
				}
				fileNumber++;
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;

		}
	}

	public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());

		// Sort the URL list on basis of maximum number of occurrence of the search word
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// put data from sorted list to Hashmap
		HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	// Function to search the string entered in text box
	public String[] find(String phrase, HttpServletRequest request) throws IOException {
		String[] words = phrase.split("\\W+");
		if (index.get(words[0].toLowerCase()) == null) {
			String[] notFound = new String[1];
			notFound[0] = "No Results Found";
			return notFound;
		}
		HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()));

		HashMap<Integer, String> filesWhereWordExists = new HashMap<>();
		HashMap<String, Integer> countOfwordWithFileName = new HashMap<>();
		for (String word : words) {
			res.retainAll(index.get(word));
		}

		int i = 0;
		for (int num : res) {

			filesWhereWordExists.put(i, sources.get(num));
			i++;

		}

		String phraseNew = phrase.toLowerCase();
		for (int j = 0; j < res.size(); j++) {
			KMP kmp = new KMP(phraseNew);

			String fName = "/WEB-INF/TextFolder/" + filesWhereWordExists.get(j);
			ServletContext cntxt = request.getServletContext();
			InputStream ins = cntxt.getResourceAsStream(fName);
			BufferedReader br = new BufferedReader(new InputStreamReader(ins));
			String word;
			String fileText = "";
			while (br.readLine() != null) {
				word = br.readLine() + " ";
				fileText = word;
			}

			int finalOffset = 0;
			int count = 0;
			// Search performed using KMP algorithm
			int offset = kmp.search(fileText);
			if (offset == fileText.length()) {
				System.out.println(phraseNew + " is not found");
			} else {
				offset = 0;
				int newSP = 0;
				while (offset < fileText.length()) {
					fileText = fileText.substring(offset + newSP).toLowerCase();

					offset = kmp.search(fileText);
					if (offset != fileText.length()) {
						finalOffset = finalOffset + offset;
						count++;
						newSP = phraseNew.length();
					}

				}

			}

			countOfwordWithFileName.put(filesWhereWordExists.get(j), count);

		}
		// after sorting
		Map<String, Integer> sortedCountOfWordWithFileNAme = sortByValue(countOfwordWithFileName);

		int sizeOfSortedMap = sortedCountOfWordWithFileNAme.size();

		ArrayList<String> finalArrayList = new ArrayList<>();

		if (sizeOfSortedMap <= 10) {
			for (Map.Entry<String, Integer> en : sortedCountOfWordWithFileNAme.entrySet()) {
				finalArrayList.add(en.getKey());
			}

		} else {
			int countToBreakAfter10 = 1;
			for (Map.Entry<String, Integer> en : sortedCountOfWordWithFileNAme.entrySet()) {
				if (countToBreakAfter10 <= 10) {
					finalArrayList.add(en.getKey());
				} else {
					break;
				}
				countToBreakAfter10++;

			}
		}
		
		Collections.reverse(finalArrayList);

		String finalURLArray[] = new String[10];
		int idk = 0;
		for (String al : finalArrayList) {
			String fName = "/WEB-INF/TextFolder/" + al;
			ServletContext cntxt = request.getServletContext();
			InputStream ins = cntxt.getResourceAsStream(fName);
			BufferedReader br = new BufferedReader(new InputStreamReader(ins));
			String finalURL = br.readLine();
			finalURLArray[idk] = finalURL;
			idk++;

		}
		return finalURLArray;

	}

}
