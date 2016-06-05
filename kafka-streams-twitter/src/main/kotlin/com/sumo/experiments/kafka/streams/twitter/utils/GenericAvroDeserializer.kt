package com.sumo.experiments.kafka.streams.twitter.utils

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.Deserializer

class GenericAvroDeserializer : Deserializer<GenericRecord> {

    internal var inner: KafkaAvroDeserializer

    /**
     * Constructor used by Kafka Streams.
     */
    constructor() {
        inner = KafkaAvroDeserializer()
    }

    constructor(client: SchemaRegistryClient) {
        inner = KafkaAvroDeserializer(client)
    }

    constructor(client: SchemaRegistryClient, props: Map<String, *>) {
        inner = KafkaAvroDeserializer(client, props)
    }

    override fun configure(configs: Map<String, *>, isKey: Boolean) {
        inner.configure(configs, isKey)
    }

    override fun deserialize(s: String, bytes: ByteArray): GenericRecord {
        return inner.deserialize(s, bytes) as GenericRecord
    }

    override fun close() {
        inner.close()
    }
}
