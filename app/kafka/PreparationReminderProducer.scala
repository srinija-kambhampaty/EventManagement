package kafka

import javax.inject._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties
import play.api.libs.json.Json
import models.Task

@Singleton
class PreparationReminderProducer @Inject()() {

  // Kafka configuration properties
  private val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  // Create Kafka producer
  private val producer = new KafkaProducer[String, String](props)

  // Method to send preparation reminders
  def sendPreparationReminder(task: Task): Unit = {
    val reminderMessage = Json.stringify(Json.toJson(task))
    val topic = "preparation_reminders"

    // Log and send the message to Kafka
    val record = new ProducerRecord[String, String](topic, "reminder", reminderMessage)
    producer.send(record)
  }
}
