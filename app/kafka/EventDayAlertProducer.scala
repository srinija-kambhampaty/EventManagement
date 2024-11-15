package kafka

import javax.inject._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties
import play.api.libs.json.Json
import models.Task

@Singleton
class EventDayAlertProducer @Inject()() {

  private val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  private val producer = new KafkaProducer[String, String](props)

  def sendEventDayAlert(task: Task): Unit = {
    val alertMessage = Json.stringify(Json.toJson(task))
    val topic = "event_day_alerts"

    val record = new ProducerRecord[String, String](topic, "event-day", alertMessage)
    producer.send(record)
  }
}
