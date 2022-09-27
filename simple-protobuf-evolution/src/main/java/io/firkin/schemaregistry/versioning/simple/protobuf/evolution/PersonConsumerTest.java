package io.firkin.schemaregistry.versioning.simple.protobuf.evolution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
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
    String topic = kafkaProps.getProperty("person.topic");
    // --group groupID, -g groupID
    // --pretty

    final Consumer<String, DynamicMessage> consumer = new KafkaConsumer<>(kafkaProps);
    consumer.subscribe(Arrays.asList(topic));

    int nToConsume = 0;
    if (args.length == 0) {
      // Consume 1 with default consumer group
      nToConsume = 1;
    } else if (args.length == 1) {
      // --infinite or -∞
      nToConsume = -1;
    } else if (args.length == 2) {
      // -n 1234
      nToConsume = Integer.parseInt(args[1]);
    }
    nToConsume = Math.max(-1, nToConsume);
    try {
      OUTER: do {

        if (nToConsume == 0) break;
//        if (nToConsume > 0) nToConsume--;
        if (nToConsume < -1) break;

        Gson gson = new GsonBuilder().create();
        ConsumerRecords<String, DynamicMessage> records = consumer.poll(100);

//        System.out.println("Received "+records.count()+" records from "+consumer.listTopics().keySet().size()+" topics");
        for (ConsumerRecord<String, DynamicMessage> record: records) {
          nToConsume--;
//          System.out.println(nToConsume--);
          System.out.println(gson.toJson(new SimplifiedRecord(record)));
          if (nToConsume <= 0) break OUTER;
        }

//        consumer.poll(Duration.of(100, ChronoUnit.MILLIS))
//            .records(topic)
//            .forEach(r -> System.out.println(gson.toJson(new SimplifiedRecord(r))));

//        ConsumerRecords<String, PersonOuterClass.Person> records =
//            consumer.poll(Duration.of(1000, ChronoUnit.MILLIS));
//        records.forEach(r -> System.out.println(gson.toJson(new SimplifiedRecord(r))));

      } while (true);
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
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

    SimplifiedRecord(ConsumerRecord<String, DynamicMessage> record) throws InvalidProtocolBufferException {
      topic = record.topic();
      partition = record.partition();
      offset = record.offset();
      key = record.key();
      value = PersonOuterClass.Person.parseFrom(record.value().toByteArray());
    }
  }
}
