package com.sumo.experiments.kafka.streams.twitter.domain

import groovy.transform.CompileStatic
import groovy.transform.ToString

@ToString
@CompileStatic
public class Tweet {
    private String id;
    private String text;
    private String language;
}
