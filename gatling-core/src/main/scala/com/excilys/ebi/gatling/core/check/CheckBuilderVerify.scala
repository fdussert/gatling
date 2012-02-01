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

import com.excilys.ebi.gatling.core.check.CheckBuilderVerify.rangeToString
import com.excilys.ebi.gatling.core.util.StringHelper.interpolate

object CheckBuilderVerify {
	val SEPARATOR = ":"

	implicit def rangeToString(range: Range) = range.mkString(SEPARATOR)

	val exists = new CheckStrategy[WhatTheFuck, WhatTheFuck] {
		def apply(value: List[String], expected: List[String]) = !value.isEmpty
		override def toString = "exists"
	}
	val notExists = new CheckStrategy[WhatTheFuck, WhatTheFuck] {
		def apply(value: List[String], expected: List[String]) = value.isEmpty
		override def toString = "notExists"
	}
	val eq = new CheckStrategy[WhatTheFuck, WhatTheFuck] {
		def apply(value: List[String], expected: List[String]) = !value.isEmpty && !expected.isEmpty && value(0) == expected(0)
		override def toString = "eq"
	}
	val neq = new CheckStrategy[WhatTheFuck, WhatTheFuck] {
		def apply(value: List[String], expected: List[String]) = !value.isEmpty && value != expected
		override def toString = "neq"
	}
	val in = new CheckStrategy[WhatTheFuck, WhatTheFuck] {
		def apply(value: List[String], expected: List[String]) = !value.isEmpty && !expected.isEmpty && expected(0).split(SEPARATOR).contains(value(0))
		override def toString = "in"
	}
	val listEq = new CheckStrategy[WhatTheFuck, WhatTheFuck] {
		def apply(value: List[String], expected: List[String]) = !value.isEmpty && value == expected
		override def toString = "listEq"
	}
	val listSize = new CheckStrategy[WhatTheFuck, WhatTheFuck] {
		def apply(value: List[String], expected: List[String]) = value.size == expected(0).toInt
		override def toString = "listSize"
	}
}

trait CheckBuilderVerify[B <: CheckBuilder[B, _]] extends CheckBuilderSave[B] { this: CheckBuilder[B, _] with CheckBuilderSave[B] =>
	def verify(strategy: CheckStrategy[WhatTheFuck, WhatTheFuck]) = newInstanceWithVerify(strategy)
	def verify(strategy: CheckStrategy[WhatTheFuck, WhatTheFuck], expected: List[String]) = newInstanceWithVerify(strategy, expected.map(interpolate(_)))
	def verify(strategy: CheckStrategy[WhatTheFuck, WhatTheFuck], expected: String) = newInstanceWithVerify(strategy, List(interpolate(expected)))
}

trait CheckBuilderVerifyOne[B <: CheckBuilder[B, _]] extends CheckBuilderVerify[B] { this: CheckBuilder[B, _] with CheckBuilderSave[B] =>
	def exists = verify(CheckBuilderVerify.exists)
	def notExists = verify(CheckBuilderVerify.notExists)
	def eq(expected: String) = verify(CheckBuilderVerify.eq, expected)
	def neq(expected: String) = verify(CheckBuilderVerify.neq, expected)
	def in(range: Range) = verify(CheckBuilderVerify.in, range)
}

trait CheckBuilderVerifyAll[B <: CheckBuilder[B, _]] extends CheckBuilderVerify[B] { this: CheckBuilder[B, _] with CheckBuilderSave[B] =>
	def eq(expected: List[String]) = verify(CheckBuilderVerify.listEq, expected)
	def size(s: Int) = verify(CheckBuilderVerify.listSize, s.toString)
}