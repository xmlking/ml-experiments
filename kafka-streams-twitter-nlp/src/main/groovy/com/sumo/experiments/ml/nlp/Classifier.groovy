package com.sumo.experiments.ml.nlp

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.lm.NGramBoundaryLM
import groovy.transform.CompileStatic;

@CompileStatic
public class Classifier {

    private DynamicLMClassifier<NGramBoundaryLM> classifier;
    private int maxCharNGram = 3;
    private String trainingDataDelimiter;

    public Classifier(String trainingDataDelimiter) {
        this.trainingDataDelimiter = trainingDataDelimiter;
    }

    public Classifier(){
        this("#");
    }

    public void train(String filePath)  {
        InputStream trainingData = this.class.classLoader.getResourceAsStream(filePath);
        Set<String> categorySet = new HashSet<>();
        List<String[]> annotatedData =  new ArrayList<>();
        fillCategoriesAndAnnotatedData(trainingData, categorySet, annotatedData);
        trainClassifier(categorySet, annotatedData);
    }

    private void trainClassifier(Set<String> categorySet, List<String[]> annotatedData){
        String[] categories = categorySet.toArray(new String[0]);
        classifier = DynamicLMClassifier.createNGramBoundary(categories,maxCharNGram);
        for (String[] row: annotatedData) {
            String actualClassification = row[0];
            String text = row[1];
            Classification classification = new Classification(actualClassification);
            Classified<CharSequence> classified = new Classified<>(text,classification);
            classifier.handle(classified);
        }
    }


    private void fillCategoriesAndAnnotatedData(InputStream trainingData,
                                                Set<String> categorySet,
                                                List<String[]> annotatedData) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(trainingData))
            String line = reader.readLine();
            while (line != null) {
                String[] data = line.split(trainingDataDelimiter);
                categorySet.add(data[0]);
                annotatedData.add(data);
                line = reader.readLine();
            }

        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    public String classify(String text){
        return  classifier.classify(text.trim()).bestCategory().toLowerCase();
    }

}
