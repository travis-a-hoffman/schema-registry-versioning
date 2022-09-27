package io.firkin.schemaregistry.versioning.simple.protobuf.evolution;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Map;
import java.util.Properties;

public class PersonTest {

  static Properties loadProperties() {
    Map<String, String> ENVS = System.getenv();
    String KAFKA_BOOTSTRAP_URL = ENVS.get("FIRKIN_KAFKA_BOOTSTRAP_URL");
    String KAFKA_BOOTSTRAP_USERNAME = ENVS.get("FIRKIN_KAFKA_BOOTSTRAP_USERNAME");
    String KAFKA_BOOTSTRAP_PASSWORD = ENVS.get("FIRKIN_KAFKA_BOOTSTRAP_PASSWORD");

    String SCHEMA_REGISTRY_URL = ENVS.get("FIRKIN_SCHEMA_REGISTRY_URL");
    String SCHEMA_REGISTRY_USERNAME = ENVS.get("FIRKIN_SCHEMA_REGISTRY_USERNAME");
    String SCHEMA_REGISTRY_PASSWORD = ENVS.get("FIRKIN_SCHEMA_REGISTRY_PASSWORD");

    boolean missingEnvVariable = false;

    if (KAFKA_BOOTSTRAP_URL == null) {
      System.err.println("Please ensure ENV variable FIRKIN_KAFKA_BOOTSTRAP_URL is set.");
      missingEnvVariable |= true;
    }

    if (KAFKA_BOOTSTRAP_USERNAME == null) {
      System.err.println("Please ensure ENV variable FIRKIN_KAFKA_BOOTSTRAP_USERNAME is set.");
      missingEnvVariable |= true;
    }

    if (KAFKA_BOOTSTRAP_PASSWORD == null) {
      System.err.println("Please ensure ENV variables FIRKIN_KAFKA_BOOTSTRAP_PASSWORD is set.");
      missingEnvVariable |= true;
    }

    if (SCHEMA_REGISTRY_URL == null) {
      System.err.println("Please ensure ENV variables FIRKIN_SCHEMA_REGISTRY_URL are all set.");
      missingEnvVariable |= true;
    }

    if (SCHEMA_REGISTRY_USERNAME == null ) {
      System.err.println("Please ensure ENV variables FIRKIN_SCHEMA_REGISTRY_USERNAME are all set.");
      missingEnvVariable |= true;
    }

    if (SCHEMA_REGISTRY_PASSWORD == null ) {
      System.err.println("Please ensure ENV variables FIRKIN_SCHEMA_REGISTRY_PASSWORD are all set.");
      missingEnvVariable |= true;
    }

    // For Debugging the ENV Variables
    ENVS.keySet().stream().sorted().filter(k -> k.startsWith("FIRKIN")).forEach(k -> System.out.println("  "+k+"=\""+ENVS.get(k)+"\""));

    if (missingEnvVariable) {
      throw new Error("Cannot run due to missing ENV Variable(s)...");
    }

    Properties props = new Properties();
    String PERSON_TOPIC = ENVS.get("FIRKIN_TOPIC");

    // Kafka Client configurations
    props.put("security.protocol","SASL_SSL");
    props.put("sasl.jaas.config","org.apache.kafka.common.security.plain.PlainLoginModule   required username='"+KAFKA_BOOTSTRAP_USERNAME+"'   password='"+ KAFKA_BOOTSTRAP_PASSWORD +"';");
    props.put("sasl.mechanism","PLAIN");

    // Schema Registry Client configurations
    props.put("schema.registry.url", SCHEMA_REGISTRY_URL);
    props.put("basic.auth.credentials.source","USER_INFO");
    props.put("basic.auth.user.info",SCHEMA_REGISTRY_USERNAME+":"+SCHEMA_REGISTRY_PASSWORD);

    // Producer/Consumer configurations
    props.put("person.topic", PERSON_TOPIC != null? PERSON_TOPIC: "person");
    props.put("reference.subject.name.strategy", TopicNameReferenceStrategy.class.getName());

    // Producer-specific configurations
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_URL);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer");
    props.put("auto.register.schemas", false);

    // Consumer-specific configurations
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_URL);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer");

    // Consumer-group configurations
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "group1-"+System.currentTimeMillis());
//    props.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "group1.1");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // or latest, or none, or "exception"

    return props;
  }

}
