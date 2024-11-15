package kafka

import javax.inject._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties
import play.api.libs.json.Json
import models.Task

@Singleton
class ProgressCheckInProducer @Inject()() {

  private val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  private val producer = new KafkaProducer[String, String](props)

  def sendProgressCheckIn(task: Task): Unit = {
    val alertMessage = Json.stringify(Json.toJson(task))
    val topic = "progress_check_in"

    val record = new ProducerRecord[String, String](topic, "progress-check-in", alertMessage)
    producer.send(record)
  }
}
