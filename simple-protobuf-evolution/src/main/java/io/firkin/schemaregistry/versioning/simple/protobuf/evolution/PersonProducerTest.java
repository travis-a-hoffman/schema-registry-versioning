package io.firkin.schemaregistry.versioning.simple.protobuf.evolution;

import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.SerializationException;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class PersonProducerTest {
  public static void main(String[] args) throws ExecutionException, InterruptedException {

    Properties kafkaProps = PersonTest.loadProperties(); // Throws NPE if missing ENV variables
    String topic = kafkaProps.getProperty("person.topic");

    PersonKeyGenerator keyGenerator = new PersonKeyGenerator();
    PersonGenerator personGenerator = new PersonGenerator();

    System.out.println("reference.subject.name.strategy=\""+kafkaProps.getProperty("reference.subject.name.strategy")+"\"");

    int nToProduce = 0;
    if (args.length == 0) {
      // Produce 1
      nToProduce = 1;
    } else if (args.length == 1) {
      // --infinite or -∞
      nToProduce = -1;
    } else if (args.length == 2) {
      // -n 1234
      nToProduce = Integer.parseInt(args[1]);
    }
    nToProduce = Math.max(-1, nToProduce);

    KafkaProducer<String, PersonOuterClass.Person> personKafkaProducer = new KafkaProducer<>(kafkaProps);
    try {
      do {

        if (nToProduce == 0) break;
        if (nToProduce > 0) nToProduce--;
        if (nToProduce < -1) break;

        String key = keyGenerator.getKey();
        PersonOuterClass.Person person = personGenerator.getPerson();
        ProducerRecord<String, PersonOuterClass.Person> personRecord = new ProducerRecord<>(topic, key, person);

        RecordMetadata recordMetadata = personKafkaProducer.send(personRecord).get();
        // Exit via Control-C Interrupt...
      } while (true);

    } catch (SerializationException e) {
      e.printStackTrace();
    } finally {
      personKafkaProducer.close();
    }
  }

  static class PersonKeyGenerator {
    Random r = new Random();

    String getKey() {
      byte[] bytes = new byte[24]; // Choose a multiple of 3 to avoid padding in the base64 below
      r.nextBytes(bytes);
      return "person-"+Base64.getEncoder().withoutPadding().encodeToString(bytes);
    }
  }

  static class PersonGenerator {
    Random r = new Random();

    PersonOuterClass.Person getPerson() {

      PersonOuterClass.Person.Builder builder = PersonOuterClass.Person.newBuilder();

      builder.setGivenName(given_names.get(r.nextInt(given_names.size())));
      builder.setFamilyName(family_names.get(r.nextInt(family_names.size())));

      builder.setDateOfBirth("1977-05-25");

      if (r.nextBoolean()) {
        builder.setDateOfDeath("2019-12-16");
      }

      builder.setPlanet(planets.get(r.nextInt(planets.size())));
      builder.setHomePlanet(builder.getPlanet().getName());
      return builder.build();
    }



    private List<String> given_names = List.of(
        "Anakin", "Luke", "Leia", "Han", "Ben", "Lando", "Jango", "Boba", "Mace", "Fennec", "Padme", "Owen", "Klieg",
        "Beru", "Firmus", "Wedge", "Nien", "Babu", "Max", "Sny", "Enfys", "Salacious", "Wicket", "Poe", "Qui-Gon",
        "Bib", "Obi-wan", "Kylo", "Chirrut", "Sheev"
    );

    private List<String> family_names = List.of(
        "Skywalker", "Organa", "Solo", "Calrissian", "Fett", "Windu", "Shand", "Amidala", "Lars", "Piett", "Antilles",
        "Nunb", "Frik", "Rebo", "Snoodles", "Nest", "Crumb", "Warrick", "Dameron", "Jinn", "Fortuna", "Kenobi", "Ren",
        "Imwe", "Palpatine"
    );

    {
      PlanetOuterClass.Planet.Builder builder = PlanetOuterClass.Planet.newBuilder();
      int id = 0;
      planets = List.of(
          builder.setId(id++).setName("Alderaan").build(),
          builder.setId(id++).setName("Bespin").build(),
          builder.setId(id++).setName("Corellia").build(),
          builder.setId(id++).setName("Coruscant").build(),
          builder.setId(id++).setName("Dagobah").build(),
          builder.setId(id++).setName("Dantooine").build(),
          builder.setId(id++).setName("Dathomir").build(),
          builder.setId(id++).setName("Eadu").build(),
          builder.setId(id++).setName("Endor").build(),
          builder.setId(id++).setName("Exegol").build(),
          builder.setId(id++).setName("Geonosis").build(),
          builder.setId(id++).setName("Hoth").build(),
          builder.setId(id++).setName("Jakku").build(),
          builder.setId(id++).setName("Jedha").build(),
          builder.setId(id++).setName("Kashyyyk").build(),
          builder.setId(id++).setName("Kamino").build(),
          builder.setId(id++).setName("Kessel").build(),
          builder.setId(id++).setName("Kuat").build(),
          builder.setId(id++).setName("Malastare").build(),
          builder.setId(id++).setName("Mandalore").build(),
          builder.setId(id++).setName("Mon Cala").build(),
          builder.setId(id++).setName("Mustafar").build(),
          builder.setId(id++).setName("Naboo").build(),
          builder.setId(id++).setName("Ord Mantell").build(),
          builder.setId(id++).setName("Rodia").build(),
          builder.setId(id++).setName("Tatooine").build(),
          builder.setId(id++).setName("Utapau").build(),
          builder.setId(id++).setName("Yavin").build()
      );
    }


    private List<PlanetOuterClass.Planet> planets;
  }

}
