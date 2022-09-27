package io.firkin.schemaregistry.versioning.simple.protobuf.evolution;

import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.serializers.subject.strategy.ReferenceSubjectNameStrategy;

import java.util.Map;

public class TopicNameReferenceStrategy implements ReferenceSubjectNameStrategy {
  @Override
  public String subjectName(String refName, String topic, boolean isKey, ParsedSchema parsedSchema) {
    if (refName.endsWith(".proto")) {
      String result = refName.substring(0, refName.length()-6);
      System.out.println("refName was \""+refName+"\", topic is \""+topic+"\", returning \""+result+"\"");
      return result;
    } else {
      return refName;
    }
  }

  @Override
  public void configure(Map<String, ?> map) {

  }
}
