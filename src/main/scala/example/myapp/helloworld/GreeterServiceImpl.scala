package example.myapp.helloworld

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import com.google.protobuf.timestamp.Timestamp
import example.myapp.helloworld.grpc.{GreeterService, HelloReply, HelloRequest}
import example.myapp.helloworld.grpc._

import scala.concurrent.Future

class GreeterServiceImpl(implicit mat: Materializer) extends GreeterService {
  import mat.executionContext

  override def sayHello(in: HelloRequest): Future[HelloReply] = {
    println(s"Say Hello to ${in.name}")
    Future.successful(HelloReply(s"Hello, ${in.name}", Some(Timestamp.apply(123456, 123))))
  }

  override def itKeepsTalking(in: Source[HelloRequest, NotUsed]): Future[HelloReply] = {
    println(s"say Hello to in stream...")
    in.runWith(Sink.seq).map(elements => HelloReply(s"Hello, ${elements.map(_.name).mkString(", ")}"))
  }

  override def itKeepsReplying(in: HelloRequest): Source[HelloReply, NotUsed] = {
    println(s"say Hello to ${in.name} with a stream of chars...")
    Source(s"Hello, ${in.name}".toList).map(character => HelloReply(character.toString))
  }

  override def streamHellos(in: Source[HelloRequest, NotUsed]): Source[HelloReply, NotUsed] = {
    println(s"say Hello to stream...")
    in.map(request => HelloReply(s"Hello, ${request.name}"))
  }
}
