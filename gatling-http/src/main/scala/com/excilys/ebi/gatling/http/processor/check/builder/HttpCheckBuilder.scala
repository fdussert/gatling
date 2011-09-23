package com.excilys.ebi.gatling.http.processor.check.builder

import com.excilys.ebi.gatling.core.context.Context

import com.excilys.ebi.gatling.http.request.HttpPhase._
import com.excilys.ebi.gatling.http.processor.check.HttpCheck
import com.excilys.ebi.gatling.http.processor.builder.HttpProcessorBuilder

abstract class HttpCheckBuilder[B <: HttpCheckBuilder[B]](expressionFormatter: Option[Context => String], expected: Option[String], attrKey: Option[String], httpPhase: Option[HttpPhase])
    extends HttpProcessorBuilder {

  def newInstance(expressionFormatter: Option[Context => String], expected: Option[String], attrKey: Option[String], httpPhase: Option[HttpPhase]): B

  def in(attrKey: String): B = {
    newInstance(expressionFormatter, expected, Some(attrKey), httpPhase)
  }

  override def build: HttpCheck
}