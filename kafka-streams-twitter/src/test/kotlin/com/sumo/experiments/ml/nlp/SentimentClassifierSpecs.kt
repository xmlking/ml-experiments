package com.sumo.experiments.ml.nlp

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class SentimentClassifierSpecs : Spek({

    given("Sentiment Classifier") {
        val classifier = SentimentClassifier()
        on("after training and saving model") {
            classifier.train("twitter_sentiment_corpus.csv", delimiter = ",")
            classifier.saveModel("/tmp/test123")
            it("should be able to predict positive sentiment") {
                val result = classifier.classify("great")
                assertEquals(result, "neutral")
            }
            it("should be able to predict negative sentiment") {
                val result = classifier.classify("bad")
                assertEquals(result, "neutral")
            }
            it("should be able to predict neutral sentiment") {
                val result = classifier.classify("Kalifornia")
                assertEquals(result, "neutral")
            }
        }
    }

    given("Sentiment Classifier") {
        val classifier = SentimentClassifier()
        on("after loading model") {
            classifier.loadModel("classifier.txt")
            it("should be able to predict positive sentiment") {
                val result = classifier.classify("great")
                assertEquals(result, "pos")
            }
            it("should be able to predict negative sentiment") {
                val result = classifier.classify("bad")
                assertEquals(result, "neg")
            }
            it("should be able to predict neutral sentiment") {
                val result = classifier.classify("Kalifornia")
                assertEquals(result, "neu")
            }
        }
    }
})
