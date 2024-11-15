package kafka

import javax.inject._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties
import play.api.libs.json._
import models.Task

@Singleton
class KafkaProducerService @Inject()() {

  // Kafka configuration properties
  private val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  // Create Kafka producer
  private val producer = new KafkaProducer[String, String](props)

  // Method to send task details to Kafka
  def sendTaskCreationAlert(task: Task): Unit = {
    // Serialize the Task object to JSON
    val jsonMessage: String = Json.stringify(Json.toJson(task))

    // Send the message to Kafka topic "taskcreation_alert"
    val record = new ProducerRecord[String, String]("taskcreation_alert", "task-key", jsonMessage)
    producer.send(record)
  }
}
