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
		String spamPath = args[0]+ "/train/spam";
		String hamPath = args[0]+ "/train/ham";

		File spamFolder = new File(spamPath);
		File hamFolder = new File(hamPath);

		int spamEmailCount = spamFolder.listFiles().length;
		int hamEmailCount = hamFolder.listFiles().length;

		Set<String> allWords = getAllWords(spamFolder,hamFolder);

		Map<String, Double> spamProbs = returnProbability(spamFolder, allWords, spamEmailCount);
		Map<String, Double> hamProbs = returnProbability(hamFolder, allWords, hamEmailCount);

//		System.out.println("Spam probs:");
//		printMap(spamProbs);
//		System.out.println("Ham probs:");
//		printMap(hamProbs);

		double probSpam = 1.0 * spamEmailCount/ (hamEmailCount+spamEmailCount);
		double probHam = 1.0 * hamEmailCount / (hamEmailCount+spamEmailCount);

//		System.out.println("Probham: " + probHam);
//		System.out.println("Probspam: " +probSpam);

		String testPath = "./data/test";
		File test = new File(testPath);

		//Set<String> wordsInAnEmail = new HashSet<>();


		for(File f: test.listFiles()) {
			Set<String> wordsInAnEmail = tokenSet(f);

			wordsInAnEmail.retainAll(allWords);

			double spamLog = Math.log(probSpam);
			double hamLog = Math.log(probHam);

			for(String s : wordsInAnEmail) {
				spamLog += Math.log(spamProbs.get(s));
				hamLog += Math.log(hamProbs.get(s));
			}
			if (spamLog > hamLog) {
				System.out.println(f.getName() + " spam");
			} else {
				System.out.println(f.getName() + " ham");
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

		for(String s : allWords) {
			//start 1 for laplace smoothing
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
		}
		return probabilities;
	}


}
