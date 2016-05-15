package com.sumo.experiments.ml.nlp

import spock.lang.Specification

class ClassifierTest extends Specification{
    def "someLibraryMethod returns true"() {
        setup:
        Classifier classifier = new Classifier()
        when:
        def result = classifier.train('twitterTrainingData_clean.csv')
        then:
        result == null
    }
}
