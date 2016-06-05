package com.sumo.experiments.ml.nlp

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class ClassifierSpecs : Spek( {
    given("a classifier") {
        val classifier = Classifier()
        on("after training") {
             classifier.train("twitterTrainingData_clean.csv")
            it("should be able to predict as english") {
                val result = classifier.classify("hello")
                assertEquals(result, "english")
            }
        }
    }
})
