package controllers

import play.api._
import play.api.mvc._

import play.api.libs.iteratee._
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def socket = WebSocket.using[String] { request =>

    // Concurrent.broadcast returns (Enumerator, Concurrent.Channel)
    val (out, channel) = Concurrent.broadcast[String]

    // log the message to stdout and send response back to client
    val in = Iteratee.foreach[String] {
      msg => println(msg)
        // the Enumerator returned by Concurrent.broadcast subscribes to the channel and will
        // receive the pushed messages
        
        // channel push("I received your message: " + msg)
    }

    // Send a single 'Hello!' message and close
    // val out = Enumerator("Hello!").andThen(Enumerator.eof)


    (in,out)
  }

}
