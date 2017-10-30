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
	
	public static void main(String[] args) throws IOException {
		String spamPath = "./data/train/spam";
		String hamPath = "./data/train/ham";
		Map<String, Double> spamProbs = returnProbability(spamPath);
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

	private static Map<String, Double> returnProbability (String filePath) throws IOException {
		File actual = new File(filePath);

		Map<String, Integer> spamWords = new HashMap<>();
		int spamEmailCount = actual.listFiles().length;
		for( File f : actual.listFiles()){

			Set<String> tokens = tokenSet(f);
			for (String s : tokens) {
				if(spamWords.containsKey(s)) {
					spamWords.put(s, spamWords.get(s)+1);
				} else {
					spamWords.put(s, 1);
				}
			}
			//System.out.println( f.getName() );
		}

		Map<String,Double> probOfWordGivenSpam = new HashMap<>();
		for(String s : spamWords.keySet()) {
			probOfWordGivenSpam.put(s, (1 + spamWords.get(s).doubleValue())/(spamEmailCount+2));
			//System.out.println(s + " " + probOfWordGivenSpam.get(s));
		}
		return probOfWordGivenSpam;
	}


}
