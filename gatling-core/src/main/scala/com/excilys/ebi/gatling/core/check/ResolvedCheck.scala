/**
 * Copyright 2011-2012 eBusiness Information, Groupe Excilys (www.excilys.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.excilys.ebi.gatling.core.check
import com.excilys.ebi.gatling.core.check.extractor.Extractor
import com.excilys.ebi.gatling.core.log.Logging
import com.excilys.ebi.gatling.core.util.StringHelper.EMPTY
import com.excilys.ebi.gatling.core.util.ClassSimpleNameToString
import com.excilys.ebi.gatling.core.check.extractor.MultiValuedExtractor
import scala.collection.mutable.HashMap
import com.excilys.ebi.gatling.core.session.Session
import com.excilys.ebi.gatling.core.check.extractor.ExtractorFactory

object ResolvedCheck {

	private def buildExtractors[T, XT, XP](where: T, checks: List[Check[T, XT, XP]]) = {
		val extractors = new HashMap[ExtractorFactory[T, XT], Extractor[XT]]
		checks.foreach { check =>
			val extractorFactory = check.how
			if (extractors.get(extractorFactory).isEmpty)
				extractors += extractorFactory -> extractorFactory.getExtractor(where)
		}
		extractors
	}

	private def resolveChecks[T](s: Session, where: T, checks: List[Check[T, AnyRef, AnyRef]]) = {

		val extractors = buildExtractors(where, checks)
		checks.map { check =>
			val what = check.what(s)
			val how = extractors.get(check.how).getOrElse(throw new IllegalArgumentException("Extractor should have been built"))
			val expected = check.expected(s)
			val strategy = check.strategy
			new ResolvedCheck(what, how, strategy, expected, check.saveAs)
		}
	}

	private def applyChecks(s: Session, resolvedChecks: List[ResolvedCheck[_, _]]): (Session, CheckResult) = {

		var newSession = s
		var lastCheckResult: CheckResult = null

		for (resolvedCheck <- resolvedChecks) {
			lastCheckResult = resolvedCheck.check
			if (!lastCheckResult.ok) {
				return (newSession, lastCheckResult)

			} else if (resolvedCheck.saveAs.isDefined) {
				newSession = newSession.setAttribute(resolvedCheck.saveAs.get, lastCheckResult.extractedValue)
			}
		}

		(newSession, lastCheckResult)
	}

	def resolveAndApplyChecks[T](s: Session, where: T, checks: List[Check[T, AnyRef, AnyRef]]) = {
		val resolvedChecks = resolveChecks(s, where, checks)
		applyChecks(s, resolvedChecks)
	}
}

class ResolvedCheck[XT, XP](val what: String, val extractor: Extractor[XT], val strategy: CheckStrategy[XT, XP], val expected: XP, val saveAs: Option[String])
		extends Logging with ClassSimpleNameToString {

	/**
	 * This method performs the check via the strategy used by this Check
	 *
	 * @param value the value extracted from the T
	 * @return a CheckResult that indicates whether the check succeeded or not
	 */
	def check = {
		val extractedValue = extractor.extract(what)

		if (strategy(extractedValue, expected)) {
			new CheckResult(true, extractedValue)
		} else {
			val message = new StringBuilder().append("Check failed : expected ").append(strategy).append("(").append(expected).append(") but found ").append(extractedValue).toString
			new CheckResult(false, extractedValue, Some(message))
		}
	}
}