package com.spamdetector.util;

import com.spamdetector.domain.TestFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;



/**
 * TODO: This class will be implemented by you
 * You may create more methods to help you organize you strategy and make you code more readable
 *
 * THe way I did it, in order to save computation,
 * 1) all the total word count is counted using computation, the total words are then stored for
 * spam and ham, the methods used to count it are then commented out, aka not used
 * this is because I only need all the word count once
 * 2) all unique words are stored in an instance variable
 * 3) The unqiue words are mapped to their probabilities of being spam given the word
 * 4) an combination techique is used to calculate the probabilies
 * 5) the unique word and probability value pairs are stored in CSV
 * 6) an if is used to detect if the stored CSV is there
 * 7) if the store map is there, it uses the stored map to populate a variable call
 * latchprobMap. this way I read the CSV and put the precalculated probablities into
 * latchprobMap. without the need to calculate every time
 * else, if the csv file is not there, it recalculates and populates the map
 * 8) in other words, many values and maps are preculcalated to speed up time
 * however if prob values aren't there my methods to calculate the probs are used
 * in the comment section, methods to count words and frequencies are also there
 * 9) this way I can show/keep work, and use precalculated files.
 * loadProbabilitiesFromFile() loads the map from the file
 */
public class SpamDetector {
    private Map<String, Integer> spamWordCounts = new HashMap<>(); //used in probability word_i given spam
    private Map<String, Integer> hamWordCounts = new HashMap<>();//used in probability word_i given ham
    private double totalSpamWords = 0;
    private double totalHamWords = 0;
    double spamtotalWCopy = 295863.0;
    double hamtotalWCopy = 1455594.0;
    Map<String, Double> probloadMap;
    //private final String dataPath;
    // p (w_i given spam)= totalSpamWords/spamWordCounts
    /*for something like
    *   private double getWordProbabilityInSpam(String word) {
        return (spamWordCounts.getOrDefault(word, 0) + 1) / (double) (totalSpamWords + spamWordCounts.size());
    +1 is used to make sure it isn't 0.
    * }
    *
    *
    * */
    private Set<String> uniqueWords = new HashSet<>();//unique words from all our files

    public List<TestFile> trainAndTest(File mainDirectory) {
//        TODO: main method of loading the directories and files, training and testing the model

        return new ArrayList<TestFile>();
    }
    public static void main(String[] args) {
        // Assuming you have a class that handles spam detection
        SpamDetector detector = new SpamDetector();
        detector.loadData(); // Load your spam and ham datasets

        // Test word probabilities
        //String testWord = "money";
        // double spamProbability = detector.getSpamProbability(testWord);
        // double hamProbability = detector.getHamProbability(testWord);

        // System.out.println("Probabilities for word '" + testWord + "':");
        // System.out.println("Spam: " + spamProbability);
        // System.out.println("Ham: " + hamProbability);

        // You can extend this to test entire files or any other functionality
    }

    //private double getHamProbability(String testWord) {
    // }

    //private double getSpamProbability(String testWord) {
    //}

    // Modify this method to read and display content from spam and ham files
    public void loadData() {


        String currentDir = System.getProperty("user.dir");
        System.out.println("Current working directory: " + currentDir);

        // Construct the relative path to target train
        String relativePath = "spamDetectorServer/target/classes/data/train";

        // Create a File object for a path to the training data
        File dataPath = new File(currentDir, relativePath);

        System.out.println("Main directory path: " + dataPath.getAbsolutePath());

        // Now you can use the path to target train as needed, it's called dataPath:

        File[] hamfiles_list = new File(dataPath, "/ham").listFiles();
        File[] ham2files_list = new File(dataPath, "/ham2").listFiles();
        File[] hamcombofiles_list = new File(dataPath, "/hamcombine").listFiles();

        File[] spamfiles_list = new File(dataPath, "/spam").listFiles();

        // Example: Print out the number of files found
        System.out.println("Spam files: " + (spamfiles_list != null ? spamfiles_list.length : 0));
        System.out.println("Ham files: " + (hamfiles_list != null ? hamfiles_list.length : 0));
        System.out.println("Ham2 files: " + (ham2files_list != null ? ham2files_list.length : 0));
        //testGettingWords(spamfiles_list[3]);
        //
        //now we calculate all the spam words and all the ham words. once, so we don't have to calculate it again

        //  try {
        //        totalHamWords=calculateTotalWords(hamcombofiles_list);
        //     totalSpamWords=calculateTotalWords(spamfiles_list);
        //   } catch (IOException e) {
        //       throw new RuntimeException(e);
        //  }

        //System.out.println("spam total words "+totalSpamWords);
        // System.out.println("ham words "+ totalHamWords);
        //spam total words 295863.0
        //ham words 1455022.0

        System.out.println("copyspam total words " + spamtotalWCopy);
        System.out.println("copyham words " + hamtotalWCopy);
///////////////////////////////////////////////////////////////
        //relativePath = "spamDetectorServer/target/classes/data/train/probMap";
        //dataPath = new File(currentDir, relativePath);

        File probMapFile = new File(dataPath, "probMap.csv");


        if (!probMapFile.exists()) {
            calculateAndSaveProbabilities(hamcombofiles_list, spamfiles_list, relativePath);
        } else {
            System.out.println("Probabilities file already exists. Skipping calculations.");
        }


        try {// loads our stored probmap
            probloadMap = loadProbabilitiesFromFile(probMapFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        double latch_prob;
        try {
            latch_prob = calculateSpamProbability(spamfiles_list[122], probloadMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
       // System.out.println("spam prbob is " + latch_prob);
        String relativePath2 = "spamDetectorServer/target/classes/data/test";

        // Create a File object for a path to the training data
        File dataPath2 = new File(currentDir, relativePath2);
        File[] hamtest_list = new File(dataPath2, "/ham").listFiles();
        File[] testSpam_list = new File(dataPath2, "/spam").listFiles();
      //  try {
       //     latch_prob = calculateSpamProbability(hamtest_list[176], probloadMap);
      //  } catch (IOException e) {
       //     throw new RuntimeException(e);
     //   }
        //System.out.println("spam probability from test is " + latch_prob);

        //////////////////////////////////////////////////////
        // Assume spamfiles_list and hamfiles_list are your test datasets
        int truePositives = 0;
        int falsePositives = 0;
        int trueNegatives = 0;
        int falseNegatives = 0;

// For spam files (assuming spam probability > 0.5 means it's classified as spam)
        for (File file : testSpam_list) {
            double spamProbability = 0;
            try {
                spamProbability = calculateSpamProbability(file, probloadMap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (spamProbability > 0.95) {
                truePositives++; // Correctly identified as spam
            } else {
                falseNegatives++; // Incorrectly identified as not spam
            }
        }

// For ham files
        for (File file : hamtest_list) {
            double spamProbability = 0;
            try {
                spamProbability = calculateSpamProbability(file, probloadMap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (spamProbability > 0.95) {
                falsePositives++; // Incorrectly identified as spam
            } else {
                trueNegatives++; // Correctly identified as not spam
            }
        }

// Calculate precision, recall, and accuracy
        double precision = truePositives / (double) (truePositives + falsePositives);
        double recall = truePositives / (double) (truePositives + falseNegatives);
        double accuracy = (truePositives + trueNegatives) / (double) (spamfiles_list.length + hamfiles_list.length);

        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("Accuracy: " + accuracy);

        //////////////////////////////////////////////////////


        // After calculating or retrieving the maps and totals


    }
    public double calculateSpamProbability(File file, Map<String, Double> probMap) throws IOException {
        // Extract unique words from the file
        Set<String> words = getWordsFromFile(file);
        double logSum = 0.0;

        for (String word : words) {
            if (probMap.containsKey(word)) {
                double wordSpamProbability = probMap.get(word);
                // Log-likelihood ratio for the word

                logSum += Math.log(1 - wordSpamProbability) - Math.log(wordSpamProbability);
            }
        }

        // Compute the log-likelihood for the entire file and convert it back
        double fileSpamLikelihood = 1 / (1 + Math.exp(logSum));
        return fileSpamLikelihood;
    }
    public Set<String> getWordsFromFile(File file) throws IOException {
        Set<String> words = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into words using a regular expression that matches spaces
                String[] splitWords = line.split("\\s+");
                for (String word : splitWords) {
                    if (!word.isEmpty()) {
                        // Normalize word to lowercase to ensure consistency with the probabilities map
                        words.add(word.toLowerCase());
                    }
                }
            }
        }
        return words;
    }
    public Map<String, Double> calculateProbabilitiesForAllUniqueWords() {
        // Define a map to hold the word probabilities
        Map<String, Double> wordProbabilities = new HashMap<>();

        // Iterate over each unique word
        for (String word : uniqueWords) {
            // Calculate the probability of the word being spam using the ensemble method
            double probability = calculateWordProbabilitiesWithEnsemble(word);

            // Store the word and its probability in the map
            wordProbabilities.put(word, probability);
        }

        return wordProbabilities;
    }
    private void calculateAndSaveProbabilities(File[] hamcombofiles_list,  File[] spamfiles_list, String savepath) {
        try {
            // These methods should be implemented according to your existing logic

            Map<String, Integer> bighamMap = getfolderFreq(hamcombofiles_list );
            Map<String, Integer> bigspamMap = getfolderFreq(spamfiles_list );

            try {
                bighamMap = getfolderFreq(hamcombofiles_list);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                bigspamMap = getfolderFreq(spamfiles_list);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //this get our frequency map and get all our unique words
            System.out.println("Number of unique words: " + uniqueWords.size());

            // Set the class variables accordingly
            spamWordCounts = bigspamMap; // Maps each word to its frequency in spam emails
            hamWordCounts = bighamMap; // Maps each word to its frequency in ham emails
            // Set totalSpamWords and totalHamWords based on the content of bigspamMap and bighamMap
            totalSpamWords = spamtotalWCopy; // Total count of words in all spam emails
            totalHamWords = hamtotalWCopy; // Total count of words in all ham emails
            calculateWordProbabilitiesWithEnsemble("money");

            System.out.println(bighamMap.get("money"));


            Map<String, Double> probUniWordsMap = calculateProbabilitiesForAllUniqueWords();
            saveMapToFile(probUniWordsMap, new File(savepath, "probMap.csv").getAbsolutePath());
            /*
        int count = 0;
        for (Map.Entry<String, Integer> entry : bighamMap.entrySet()) {
            if (count >= 15) break; // Stop after printing 5 entries
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            count++;
        }
        int totalWords = 0;
        for (Integer value : bigspamMap.values()) {
            totalWords += value;
        }
        System.out.println("Total words in bigspamMap: " + totalWords);
        int mylatch;
        try {
            mylatch = getTotalWordCountInFolder(spamfiles_list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Total words in latchh: " + mylatch);

         */
// helper method to test getting words from file

            //now to map the uniques words to the probabilities



// To print the first 10 entries of probUniWordsMap
            //  int count = 0;
            // for (Map.Entry<String, Double> entry : probUniWordsMap.entrySet()) {
            //   if (count >= 10) break; // Stop after printing 10 entries
            //   System.out.println("Word: " + entry.getKey() + ", Probability: " + entry.getValue());
            //    count++;
            //}

            //STORE PRE Calculated MAP!
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to calculate or save probabilities.", e);
        }
    }
    public Map<String, Double> loadProbabilitiesFromFile(String filename) throws IOException {
        Map<String, Double> probMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String word = parts[0];
                    Double probability = Double.parseDouble(parts[1]);
                    probMap.put(word, probability);
                }
            }
        }
        return probMap;
    }
    public void saveMapToFile(Map<String, Double> map, String filename) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                // Each map entry is written as "word,probability"
                out.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTotalWordCountInFolder(File[] filesList) throws IOException {
        //helper method to get total word count to verify totalwords in folder
        //matches all the values combined in a map.
        int totalWordCount = 0;

        // Loop through each file in the directory
        for (File file : filesList) {
            // Use getUniqueWordFrequencies to get word frequencies for the current file
            Map<String, Integer> wordFrequencies = getUniqueWordFrequencies(file);

            // Sum up the values in the word frequencies map for the current file
            for (int frequency : wordFrequencies.values()) {
                totalWordCount += frequency;
            }
        }

        return totalWordCount;
    }

    public double calculateTotalWords(File[] files_list) throws IOException {
        // Assuming these paths are correctly set to your spam and ham directories
        // Initialize counters

        double totalfolderWords = 0;

        // Process all spam files or Process all ham files
        for (File file : Objects.requireNonNull(files_list)) {
            Map<String, Integer> frequencies = getUniqueWordFrequencies(file);
            for (int count : frequencies.values()) {
                totalfolderWords += count;
            }
        }

        return (totalfolderWords);

    }
    public Map<String, Integer> getfolderFreq(File[] filesList) throws IOException {
        Map<String, Integer> bigwordfrqmap = new HashMap<>();
        //make use of private Set<String> uniqueWords = new HashSet<>();
        //this way I get frequency and all the unique words

        // Process all files in the provided list
        for (File file : Objects.requireNonNull(filesList)) {
            Map<String, Integer> fileFreqs = getUniqueWordFrequencies(file);

            // Merge the frequencies from fileFreqs into bigwordfrqmap
            for (Map.Entry<String, Integer> entry : fileFreqs.entrySet()) {
                String word = entry.getKey();
                Integer count = entry.getValue();
                // Add the word to the uniqueWords set
                uniqueWords.add(word);

                // If the word already exists in bigwordfrqmap, add to its count, otherwise put the new word with its count
                bigwordfrqmap.put(word, bigwordfrqmap.getOrDefault(word, 0) + count);
            }
        }

        return bigwordfrqmap;
    }
    /* private void testGettingWords(File filepath) {
         try {
             Set<String> words = makeUniqueWordSets(filepath);
             System.out.println("File: " + filepath.getName() + " | Words: " + words.size());

             // Print the first few words to the console
             int count = 0;
             for (String word : words) {
                 System.out.println(word);
                 if (++count == 100) break; // Adjust this number to change how many words are printed
             }

         } catch (IOException e) {
             System.err.println("Error reading file: " + filepath.getName());
         }
     }

     */
    public Map<String, Integer> getUniqueWordFrequencies(File file) throws IOException {
        /*
         *for each unique word, it would be like
         * ["uniqueword1": numberofoccurance, "money": 103, "uniqueword123": 89
         * this is the helper method that gives the words in the left side.
         *
         * */
        Map<String, Integer> wordFrequencies = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split on whitespace and iterate through the resulting words
                String[] words = line.split("\\s+");
                for (String word : words) {
                    if (!word.isEmpty()) { // Check to ensure the word isn't empty
                        // Convert to lowercase to ensure case insensitivity
                        word = word.toLowerCase();
                        wordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0) + 1);
                    }
                }
            }
        }
        return wordFrequencies;
    }



    public double calculateWordProbabilitiesWithEnsemble(String word) {
        //I am not sure which techique is good, so I use original, no smoothing,
        //and Laplace smoothing
        //and add K smoothing
        //then average it. I heard it's called ensemble method.
        double prW_S_NoSmoothing = getWordProbabilityInSpamNoSmoothing(word);
        double prW_H_NoSmoothing = getWordProbabilityInHamNoSmoothing(word);

        double prW_S_Laplace = getWordProbabilityInSpamLaplace(word);
        double prW_H_Laplace = getWordProbabilityInHamLaplace(word);

        double prW_S_AddK = getWordProbabilityInSpamAddKSmoothing(word, 0.5); // Example k=0.5
        double prW_H_AddK = getWordProbabilityInHamAddKSmoothing(word, 0.5);

        // Average probabilities for spam and ham across techniques
        double prW_S_Avg = (prW_S_NoSmoothing + prW_S_Laplace + prW_S_AddK) / 3;
        double prW_H_Avg = (prW_H_NoSmoothing + prW_H_Laplace + prW_H_AddK) / 3;

        // Use averaged probabilities in Bayesian calculation
        double prS_W_Avg = calculateBayesProbabilitySpam(prW_S_Avg, prW_H_Avg);
        System.out.println("Ensemble Probability of word givne spam prW_S_Avg '" + word + "' is: " + prW_S_Avg );
        System.out.println("Ensemble Probability of word given ham prW_H_Avg'" + word + "' is: " + prW_H_Avg);
        System.out.println("Ensemble Probability of word '" + word + "' being spam: " + prS_W_Avg);
        return (prS_W_Avg);
    }

    // No smoothing
    private double getWordProbabilityInSpamNoSmoothing(String word) {
        return spamWordCounts.containsKey(word) ?
                (double) spamWordCounts.get(word) / totalSpamWords :
                0; // Return 0 if word not in spam
    }

    private double getWordProbabilityInHamNoSmoothing(String word) {
        return hamWordCounts.containsKey(word) ?
                (double) hamWordCounts.get(word) / totalHamWords :
                0; // Return 0 if word not in ham
    }

    // Laplace smoothing (already in your method)
    private double getWordProbabilityInSpamLaplace(String word) {
        return (spamWordCounts.getOrDefault(word, 0) + 1) / (double) (2+totalSpamWords + spamWordCounts.size());
    }

    private double getWordProbabilityInHamLaplace(String word) {
        return (hamWordCounts.getOrDefault(word, 0) + 1) / (double) (2+totalHamWords + hamWordCounts.size());
    }

    // Add-k smoothing
    private double getWordProbabilityInSpamAddKSmoothing(String word, double k) {
        return (spamWordCounts.getOrDefault(word, 0) + k) / (double) (totalSpamWords + k * spamWordCounts.size());
    }

    private double getWordProbabilityInHamAddKSmoothing(String word, double k) {
        return (hamWordCounts.getOrDefault(word, 0) + k) / (double) (totalHamWords + k * hamWordCounts.size());
    }

    // Assuming equal prior probabilities for spam and ham
    private double calculateBayesProbabilitySpam(double prW_S, double prW_H) {
        return prW_S / (prW_S + prW_H);
    }
}