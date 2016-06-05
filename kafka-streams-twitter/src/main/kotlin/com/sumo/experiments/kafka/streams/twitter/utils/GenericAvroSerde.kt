package com.sumo.experiments.kafka.streams.twitter.utils


import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.Serializer

import java.util.Collections

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient

class GenericAvroSerde : Serde<GenericRecord> {

    private val inner: Serde<GenericRecord>

    /**
     * Constructor used by Kafka Streams.
     */
    constructor() {
        inner = Serdes.serdeFrom(GenericAvroSerializer(), GenericAvroDeserializer())
    }

    @JvmOverloads constructor(client: SchemaRegistryClient, props: Map<String, *> = emptyMap<String, Any>()) {
        inner = Serdes.serdeFrom(GenericAvroSerializer(client), GenericAvroDeserializer(client, props))
    }

    override fun serializer(): Serializer<GenericRecord> {
        return inner.serializer()
    }

    override fun deserializer(): Deserializer<GenericRecord> {
        return inner.deserializer()
    }

    override fun configure(configs: Map<String, *>, isKey: Boolean) {
        inner.serializer().configure(configs, isKey)
        inner.deserializer().configure(configs, isKey)
    }

    override fun close() {
        inner.serializer().close()
        inner.deserializer().close()
    }

}
