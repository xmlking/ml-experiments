package com.sumo.experiments.ml.nlp

import com.aliasi.classify.Classification
import com.aliasi.classify.Classified
import com.aliasi.classify.DynamicLMClassifier
import com.aliasi.lm.NGramBoundaryLM
import groovy.transform.CompileStatic

@CompileStatic
public class LingPipeTester {

    public static void main(String[] args) throws  Exception {

        InputStream trainingData = LingPipeTester.class.classLoader.getResourceAsStream('twitterTrainingData_clean.csv');
        int maxCharNGram = 3;

        BufferedReader reader = new BufferedReader(new InputStreamReader(trainingData));
        Set<String> categorySet = new HashSet<>();
        List<String[]> annotatedData =  new ArrayList<>();
        String line = reader.readLine();
        while (line !=null){
            String[] data = line.split("#");
            categorySet.add(data[0]);
            annotatedData.add(data);
            line = reader.readLine();
        }
        System.out.println("read in all data");
        reader.close();
        String[] categories = categorySet.toArray(new String[0]);

        DynamicLMClassifier<NGramBoundaryLM> classifier = DynamicLMClassifier.createNGramBoundary(categories,maxCharNGram);
        for (String[] row: annotatedData) {
            String truth = row[0];
            String text = row[1];
            Classification classification = new Classification(truth);
            Classified<CharSequence> classified = new Classified<>(text,classification);
            classifier.handle(classified);
        }
        System.out.println("training complete");

        reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("enter text, I'll tell you the language");
        String text;
        while (!(text = reader.readLine()).equalsIgnoreCase("quit")) {
            Classification classification = classifier.classify(text);
            System.out.println("Entered -> " + text);
            System.out.println("lang -> " + classification.bestCategory());
        }
    }
}
