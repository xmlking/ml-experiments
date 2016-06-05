package com.sumo.experiments.kafka.streams.twitter.utils

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.Serializer

class GenericAvroSerializer : Serializer<GenericRecord> {

    internal var inner: KafkaAvroSerializer

    /**
     * Constructor used by Kafka Streams.
     */
    constructor() {
        inner = KafkaAvroSerializer()
    }

    constructor(client: SchemaRegistryClient) {
        inner = KafkaAvroSerializer(client)
    }

    override fun configure(configs: Map<String, *>, isKey: Boolean) {
        inner.configure(configs, isKey)
    }

    override fun serialize(topic: String, record: GenericRecord): ByteArray {
        return inner.serialize(topic, record)
    }

    override fun close() {
        inner.close()
    }
}
