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
package com.excilys.ebi.gatling.core.check.extractor

import com.excilys.ebi.gatling.core.log.Logging

/**
 * This class acts as model for extractors
 *
 * Extractors are objects responsible for extracting elements in others
 * Typically, we can think of Regular Expressions.
 */
abstract class Extractor[+XT] extends Logging {

	/**
	 * this method does the actual extraction of what is designed by the expression
	 *
	 * @param expression the expression that defines the extraction
	 * @return the result of the search, being None if nothing was found or Some(something)
	 */
	def extract(expression: String): XT
}

trait MultiValuedExtractor
