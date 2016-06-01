package com.sumo.experiments.kafka.connect.twitter

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import com.google.common.collect.Queues

public fun <T> Iterable<T>.batch(n: Int): Iterable<List<T>> {
    return BatchingSequence(this, n)
}
private class BatchingSequence<T>(val source: Iterable<T>, val batchSize: Int) : Iterable<List<T>> {
    override fun iterator(): Iterator<List<T>> = object : AbstractIterator<List<T>>() {
        val iterate = if (batchSize > 0) source.iterator() else emptyList<T>().iterator()
        override fun computeNext() {
            if (iterate.hasNext()) setNext(iterate.asSequence().take(batchSize).toList())
            else done()
        }
    }
}

public fun <T> LinkedBlockingQueue<T>.drainWithTimeoutTo(collection: MutableCollection<in T>, maxElements: Int, timeout: Long, unit: TimeUnit): Int {
        return Queues.drain<T>(this, collection, maxElements, timeout, unit)
}

