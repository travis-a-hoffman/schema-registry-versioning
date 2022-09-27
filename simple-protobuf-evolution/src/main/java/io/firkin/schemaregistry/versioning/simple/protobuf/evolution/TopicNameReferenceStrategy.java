package io.firkin.schemaregistry.versioning.simple.protobuf.evolution;

import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.serializers.subject.strategy.ReferenceSubjectNameStrategy;

import java.util.Map;

public class TopicNameReferenceStrategy implements ReferenceSubjectNameStrategy {
  @Override
  public String subjectName(String refName, String topic, boolean isKey, ParsedSchema parsedSchema) {
    System.out.println("refName is \""+refName+"\", topic is \""+topic+"\"");
    return refName;
  }

  @Override
  public void configure(Map<String, ?> map) {

  }
}
