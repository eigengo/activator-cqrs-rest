package org.eigengo.cqrsrest.router

import akka.actor.{ActorRefFactory, ActorSystem, ActorRef}
import akka.io.IO
import akka.pattern.AskSupport
import akka.util.Timeout
import spray.can.Http
import spray.http._
import spray.routing._

import scala.util.{Failure, Success}

/**
 * Spray routes for proxying requests
 *
 * Spray as of version 1.3.1 does not implement proxy
 * This is built on top of one of the proposed implementations (https://github.com/bthuillier/spray/compare/proxy-directives)
 * Which however only modifies URI, but does not modify host header
 * resulting correctly in 'Host' header value doesn't match request target authority
 * https://github.com/firehooper/spray/commit/71ff0e26968dab2431a58d32aa537948fcf50079 and
 * https://github.com/firehooper/spray/commit/13efd666fefa761204167ce9826bb3e1838169e2 fix that issue
 */
trait ProxyDirectives {

  private def proxyRequest(updateRequest: RequestContext => HttpRequest)(implicit system: ActorSystem): Route =
    ctx => IO(Http)(system) tell (updateRequest(ctx), ctx.responder)

  private def stripHostHeader(headers: List[HttpHeader] = Nil) =
    headers filterNot (header => header is (HttpHeaders.Host.lowercaseName))

  private val updateUri = (_: RequestContext, uri: Uri) => uri

  private val updateUriUnmatchedPath = (ctx: RequestContext, uri: Uri) => uri.withPath(uri.path ++ ctx.unmatchedPath)

  /**
   * Updates request (uri and headers) before sending it to proxy.
   * @param uri uri of the final host
   * @param updateUri function to convert uri in existing request to the new uri
   * @return function that updates uri and headers in ``RequestContext`` and returns the updated ``HttpRequest``
   */
  def updateRequest(uri: Uri, updateUri: (RequestContext, Uri) => Uri): RequestContext => HttpRequest =
    ctx => ctx.request.copy(
      uri = updateUri(ctx, uri),
      headers = stripHostHeader(ctx.request.headers))

  /**
   * Proxy the request to the specified uri
   * @param uri uri of the receiver
   * @param system actorsystem
   * @return a Route that proxies to the given uri
   */
  def proxyTo(uri: Uri)(implicit system: ActorSystem): Route =
    proxyRequest(updateRequest(uri, updateUri))


  /**
   * Proxy the request to the specified uri with the unmatched path
   * @param uri uri of the receiver. Unmatched part of the path is added to it
   * @param system actorsystem
   * @return a Route that proxies to the given uri + unmatched part of the request path
   */
  def proxyToUnmatchedPath(uri: Uri)(implicit system: ActorSystem): Route =
    proxyRequest(updateRequest(uri, updateUriUnmatchedPath))
}

