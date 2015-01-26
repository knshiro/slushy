package me.ugo.slushy.util

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try

/**
 * Created by Ugo Bataillard on 1/25/15.
 */
object FutureUtil {

  def materialize[T](f: Future[T])(implicit ec: ExecutionContext): Future[Try[T]] = {
    val p = Promise[Try[T]]()
    f onComplete p.success
    p.future
  }

  def waitForAll[T](futures: TraversableOnce[Future[T]])(implicit executor: ExecutionContext): Future[Unit] = {
    val mfutures = futures map materialize
    Future.fold(mfutures)(()) { (_, _) => ()}
  }

}
