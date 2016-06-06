package com.sumo.experiments.ml.nlp

import com.aliasi.classify.Classification
import com.aliasi.classify.Classified
import com.aliasi.classify.DynamicLMClassifier
import com.aliasi.classify.LMClassifier
import com.aliasi.lm.NGramBoundaryLM
import com.aliasi.lm.NGramProcessLM
import com.aliasi.stats.MultivariateEstimator
import com.aliasi.util.AbstractExternalizable
import java.io.File
import java.io.FileNotFoundException
import java.io.ObjectInputStream
import java.util.*


interface Classifier {
    fun train(filePath: String, delimiter: String = "#")
    fun saveModel(modelFilePath: String = "/tmp/polarity.model")
    fun loadModel(modelFilePath: String = "/tmp/polarity.model")
    fun classify(text: String): String
    fun reset()
}

open class LingPipeClassifier : Classifier {

    private lateinit var classifier: LMClassifier<NGramBoundaryLM, MultivariateEstimator>
    private val maxCharNGram = 3

    override fun train(filePath: String, delimiter: String) {
        val trainingData = ClassLoader.getSystemResourceAsStream(filePath)
        val categorySet = HashSet<String>()
        val annotatedData = ArrayList<Array<String>>()
        trainingData.bufferedReader().forEachLine {
            val data = it.split(delimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            categorySet.add(data[0])
            annotatedData.add(data)
        }

        classifier = DynamicLMClassifier.createNGramBoundary(categorySet.toTypedArray(), maxCharNGram)

        for (row in annotatedData) {
            val classified = Classified<CharSequence>(row[1], Classification(row[0]))
            (classifier as DynamicLMClassifier<NGramBoundaryLM>).handle(classified)
        }
    }

    override fun saveModel(modelFilePath: String) {
        AbstractExternalizable.compileTo(classifier as DynamicLMClassifier<NGramBoundaryLM>, File(modelFilePath));
    }

    override fun loadModel(modelFilePath: String) {
        classifier = try {
            AbstractExternalizable.readObject(File(modelFilePath)) as LMClassifier<NGramBoundaryLM, MultivariateEstimator>
        } catch(e: FileNotFoundException) {
            ObjectInputStream(ClassLoader.getSystemResourceAsStream(modelFilePath)).readObject() as LMClassifier<NGramBoundaryLM, MultivariateEstimator>
        }
    }

    override fun classify(text: String): String {
        return classifier.classify(text.trim()).bestCategory().toLowerCase()
    }

    override fun reset() {
        throw UnsupportedOperationException()
    }

}

class LanguageClassifier : LingPipeClassifier()

class SentimentClassifier : LingPipeClassifier() {
    private lateinit var classifier: LMClassifier<NGramProcessLM, MultivariateEstimator>
    private val maxCharNGram = 7; //the nGram level, any value between 7 and 12 works

    override fun train(filePath: String, delimiter: String) {
        val trainingData = ClassLoader.getSystemResourceAsStream(filePath)
        val categorySet = HashSet<String>()
        val annotatedData = ArrayList<Array<String>>()
        trainingData.bufferedReader().forEachLine {
            val data = it.split(delimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            try {
                categorySet.add(data[1])
                annotatedData.add(data)
            }catch(e : Exception) {
                println(data)
            }
        }

        classifier = DynamicLMClassifier.createNGramProcess(categorySet.toTypedArray(), maxCharNGram)

        for (row in annotatedData) {
            val classified = Classified<CharSequence>(row[4], Classification(row[1]))
            (classifier as DynamicLMClassifier<NGramProcessLM>).handle(classified)
        }
    }

    override fun saveModel(modelFilePath: String) {
        AbstractExternalizable.compileTo(classifier as DynamicLMClassifier<NGramProcessLM>, File(modelFilePath));
    }

    override fun loadModel(modelFilePath: String) {
        classifier = try {
            AbstractExternalizable.readObject(File(modelFilePath)) as LMClassifier<NGramProcessLM, MultivariateEstimator>
        } catch(e: FileNotFoundException) {
            ObjectInputStream(ClassLoader.getSystemResourceAsStream(modelFilePath)).readObject() as LMClassifier<NGramProcessLM, MultivariateEstimator>
        }
    }

    override fun classify(text: String): String {
        return classifier.classify(text.trim()).bestCategory().toLowerCase()
    }

}
