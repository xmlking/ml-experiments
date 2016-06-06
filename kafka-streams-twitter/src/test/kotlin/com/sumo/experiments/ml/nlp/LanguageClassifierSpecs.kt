package com.sumo.experiments.ml.nlp

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class LanguageClassifierSpecs : Spek({

    given("Language Classifier") {
        val classifier = LanguageClassifier()
        on("after training and saving model") {
            classifier.train("twitterTrainingData_clean.csv")
            classifier.saveModel("/tmp/smoooooo")
            it("should be able to predict as english") {
                val result = classifier.classify("hello")
                assertEquals(result, "english")
            }
            it("should be able to predict as french") {
                val result = classifier.classify("écustout")
                assertEquals(result, "french")
            }
            it("should be able to predict as spanish") {
                val result = classifier.classify("Bolsa")
                assertEquals(result, "spanish")
            }
            it("should be able to predict as german") {
                val result = classifier.classify("Weilchen")
                assertEquals(result, "german")
            }
        }
    }
    given("Language Classifier") {
        val classifier = LanguageClassifier()
        on("after loading model") {
            classifier.loadModel("/tmp/smoooooo")
            it("should be able to predict as english") {
                val result = classifier.classify("hello")
                assertEquals(result, "english")
            }
            it("should be able to predict as french") {
                val result = classifier.classify("écustout")
                assertEquals(result, "french")
            }
            it("should be able to predict as spanish") {
                val result = classifier.classify("Bolsa")
                assertEquals(result, "spanish")
            }
            it("should be able to predict as german") {
                val result = classifier.classify("Weilchen")
                assertEquals(result, "german")
            }
        }
    }

})
