import java.io.*;
import java.util.*;

public class NaiveBayes {
	
	//	This function reads in a file and returns a 
	//	set of all the tokens. It ignores the subject line
	//
	//	If the email had the following content:
	//
	//	Subject: Get rid of your student loans
	//	Hi there ,
	//	If you work for us, we will give you money
	//	to repay your student loans . You will be 
	//	debt free !
	//	FakePerson_22393
	//
	//	This function would return to you
	//	[hi, be, student, for, your, rid, we, get, of, free, if, you, us, give, !, repay, will, loans, work, fakeperson_22393, ,, ., money, there, to, debt]
	public static HashSet<String> tokenSet(File filename) throws IOException {
		HashSet<String> tokens = new HashSet<String>();
		Scanner filescan = new Scanner(filename);
		filescan.next(); //Ignoring "Subject"
		while(filescan.hasNextLine() && filescan.hasNext()) {
			tokens.add(filescan.next());
		}
		filescan.close();
		return tokens;
	}


	private static Set<String> getAllWords(File spamFolder, File hamFolder) throws IOException {
		Set<String> result = new HashSet<>();

		for(File f : spamFolder.listFiles()){
			Set<String> tokens = tokenSet(f);
			for (String s : tokens) {
				result.add(s);
			}
		}

		for(File f : hamFolder.listFiles()){
			Set<String> tokens = tokenSet(f);
			for (String s : tokens) {
				result.add(s);
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException {
		String spamPath = "./data/train/spam";
		String hamPath = "./data/train/ham";

		File spamFolder = new File(spamPath);
		File hamFolder = new File(hamPath);

		//+2 for laplace smoothing
		int spamEmailCount = spamFolder.listFiles().length + 2;
		int hamEmailCount = hamFolder.listFiles().length + 2;

		Set<String> allWords = getAllWords(spamFolder,hamFolder);

		Map<String, Double> spamProbs = returnProbability(spamFolder, allWords, spamEmailCount);
		Map<String, Double> hamProbs = returnProbability(hamPath);
		//printMap(spamProbs);
		//printMap(hamProbs);
		double numSpam = (new File(spamPath).listFiles().length);
		double numHam = new File(hamPath).listFiles().length;
		double probSpam = numSpam/ (numHam+numSpam);
		double probHam = numHam / (numHam+numSpam);

		Set<String> masterWords = new HashSet<>();
		masterWords.addAll(spamProbs.keySet());
		masterWords.addAll(hamProbs.keySet());


		String testPath = "./data/test";
		File test = new File(testPath);
		for(File f: test.listFiles()) {
			Set<String> words = tokenSet(f);

			words.retainAll(masterWords);

			double prob = probSpam;

			for(String s : words) {
				prob *= spamProbs.get(s);
				prob /= (probSpam * spamProbs.get(s) + probHam * hamProbs.get(s));
			}
			if (prob > 0.5) {
				System.out.println("spam");

			} else {
				System.out.println("ham");
			}
		}

	}

	private static void printMap (Map<String, Double> m) {
		for(String s : m.keySet()) {
			System.out.println(s + " " + m.get(s));
		}
		System.out.println();
	}

	private static Map<String, Double> returnProbability (File folder, Set<String> allWords,
														  int emailCount) throws IOException {
		Map<String, Integer> words = new HashMap<>();

		//initialize all words to 1
		for(String s : allWords) {
			words.put(s, 1);
		}
		//count spam or ham words
		for( File f : folder.listFiles()){
			Set<String> tokens = tokenSet(f);
			for (String s : tokens) {
				if(words.containsKey(s)) {
					words.put(s, words.get(s)+1);
				}
			}
		}

		//calculate probabilities
		Map<String,Double> probabilities = new HashMap<>();
		for(String s : words.keySet()) {
			probabilities.put(s, (words.get(s).doubleValue())/(emailCount+2));
			//System.out.println(s + " " + probOfWordGivenSpam.get(s));
		}
		return probOfWordGivenSpam;
	}


}
