package controllers

import play.api._
import play.api.libs.iteratee._
import play.api.libs.iteratee.Concurrent.Channel
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  var connections : List[Channel[String]] = Nil

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def socket = WebSocket.using[String] {
    request =>

    // Concurrent.broadcast returns (Enumerator, Concurrent.Channel)
    val (out, channel) = Concurrent.broadcast[String]

    // register
    connections ::= channel

    // log the message to stdout and send response back to client
    val in = Iteratee.foreach[String] (msg => {
      println("received message is " + msg)

      // the Enumerator returned by Concurrent.broadcast subscribes to the channel and will
      // receive the pushed messages
      // push is called for each connections
      connections.foreach(_ push("I received your message: " + msg))
    }).map(_ => {
      // unregister
      connections = connections.filter(_ != channel)
      println("disconnected")
    })

    (in,out)
  }

}
