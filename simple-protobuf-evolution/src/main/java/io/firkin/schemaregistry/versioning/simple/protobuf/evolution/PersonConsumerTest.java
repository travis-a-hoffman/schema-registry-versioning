package io.firkin.schemaregistry.versioning.simple.protobuf.evolution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Properties;

public class PersonConsumerTest {
  public static void main(String[] args) {
    Properties kafkaProps = PersonTest.loadProperties(); // Throws NPE if missing ENV variables
    String topic = System.getProperty("FIRKIN_TOPIC", "person");
    // --group groupID, -g groupID
    // --pretty

    final Consumer<String, PersonOuterClass.Person> consumer = new KafkaConsumer<>(kafkaProps);
    consumer.subscribe(Arrays.asList(topic));

    try {
      while (true) {
        ConsumerRecords<String, PersonOuterClass.Person> records = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
        Gson gson = new GsonBuilder().create();
        for (ConsumerRecord<String, PersonOuterClass.Person> record: records) {
          System.out.println(gson.toJson(new SimplifiedRecord(record)));
        }
      }
    } finally {
      consumer.close();
    }
  }

  // Just to print (as json) only the fields of Record we want to print
  private static class SimplifiedRecord {
    String topic;
    int partition;
    long offset;

    String key;
    PersonOuterClass.Person value;

    SimplifiedRecord(ConsumerRecord<String, PersonOuterClass.Person> record) {
      topic = record.topic();
      partition = record.partition();
      offset = record.offset();
      key = record.key();
      value = record.value();
    }
  }
}
