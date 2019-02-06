package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import io.gatling.core.session.Session
import com.paulgoldbaum.influxdbclient._
import scala.concurrent.ExecutionContext.Implicits.global

// object Predef {
//  def justDoIt(param: String): Session => Validation[Session] = s => s.set("some", param)
// }


class BasicSimulation extends Simulation {

  val influxdb = InfluxDB.connect("influx_hostname", 8086)
  val database = influxdb.selectDatabase("gatling_raw_requests")
  database.create()
  
  val scenarioName = "Mix"

  val scn = scenario(scenarioName)
    .exec(ws("connect ws").connect("/socket.io/?EIO=3&transport=websocket")
      .await(10)(ws.checkTextMessage("sid_received").check(regex(""""sid":"\w*"""").saveAs("name"))))
    .exec { session =>
      println(session("name").as[String])
      session
    } 
    .exec(ws("message2").sendText("""42["subscribe",{"url":"/sport/matches/1353783/odds","sendInitialUpdate":true}]""")
      .await(20)(ws.checkTextMessage("initial_odds_received").check(regex("""42\["update",\{"path":"/sport/matches/1353783/odds".*""").saveAs("initial_odds"))))
    .exec { session =>
      println(session("initial_odds").as[String])
      session
    }
    .exec(ws("Close WS").close)

  val httpProtocol = http
    .wsBaseUrl("wss://hostname")
    
    
  // val scn = scenario(scenarioName)
  //   // .exec(http("request_get")
  //   //   .get("/").check( responseTimeInMillis.saveAs("responseTime") )
  //   // )
  //   .exec(ws("connect ws").connect("/socket.io/?EIO=3&transport=websocket&sid=BpvegaGfaVW88hbVAVHm"))

  // val httpProtocol = http
  //   .baseUrl("https://hostname")
  //   .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
  //   .acceptEncodingHeader("gzip, deflate")
  //   .acceptLanguageHeader("en-US,en;q=0.5")
  //   .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
  //   .check(responseTimeInMillis.transform(responseTime => {
  //     database.write(new Point("gatling_requests")
  //         .addTag("scenarioName", scenarioName)
  //         //.addTag("name", )
  //         .addField("responseTime", responseTime))
  //         responseTime
  //     }
  //   ).saveAs("nevermind"))
  //   .wsBaseUrl("wss://hostname")
    //.header("Sec-WebSocket-Extensions", "permessage-deflate; client_max_window_bits")

  setUp(scn.inject(atOnceUsers(1)).protocols(httpProtocol))
}
