package com.sumo.experiments.kafka.connect.twitter.models

import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.data.SchemaBuilder
import org.apache.kafka.connect.data.Struct
import org.apache.kafka.connect.data.Timestamp
import twitter4j.Status
import twitter4j.User

class TwitterUser(user: User) : Struct(TwitterUser.SCHEMA) {

    companion object {

        val SCHEMA = SchemaBuilder.struct().name(TwitterUser::class.java.name).version(1)
            .field("id", Schema.INT64_SCHEMA)
            .field("name", Schema.STRING_SCHEMA)
            .field("screenName", Schema.STRING_SCHEMA)
            .build()
    }

    init {
        this.put("id", user.id)
            .put("name", user.name)
            .put("screenName", user.screenName)
    }

    override fun toString(): String {
        return "TwitterUser " + get("id") + "\t" + get("name") + "\t" + get("screenName")
    }

}

class TwitterStatus(status: Status) : Struct(TwitterStatus.SCHEMA) {

    companion object {

        val SCHEMA = SchemaBuilder.struct().name(TwitterStatus::class.java.name).version(1)
            .field("id", Schema.INT64_SCHEMA)
            .field("createdAt", Timestamp.builder().build())
            .field("favoriteCount", Schema.INT32_SCHEMA)
            .field("text", Schema.STRING_SCHEMA)
            .field("user", TwitterUser.SCHEMA)
            .build()
    }

    init {
        this.put("id", status.id)
            .put("createdAt", status.createdAt)
            .put("favoriteCount", status.favoriteCount)
            .put("text", status.text)
            .put("user", TwitterUser(status.user))
    }

    override fun toString(): String {
        return "TwitterStatus " + get("id") + "\t" + get("createdAt") + "\t" + get("favoriteCount") + "\t" + get("text") + "\t" + get("user")
    }

}

