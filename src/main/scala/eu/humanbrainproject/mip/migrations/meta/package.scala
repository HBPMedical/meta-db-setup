/*
 * Copyright 2017 LREN CHUV
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.humanbrainproject.mip.migrations

import doobie.imports.Meta
import io.circe.Json
import io.circe.parser.parse
import org.postgresql.util.PGobject

package object meta {

  type Traversable[+A] = scala.collection.immutable.Traversable[A]
  type Iterable[+A]    = scala.collection.immutable.Iterable[A]
  type Seq[+A]         = scala.collection.immutable.Seq[A]
  type IndexedSeq[+A]  = scala.collection.immutable.IndexedSeq[A]

  implicit val JsonMeta: Meta[Json] =
    Meta
      .other[PGobject]("jsonb")
      .xmap[Json](
        a => parse(a.getValue).left.map[Json](e => throw e).merge, // failure raises an exception
        a => {
          val o = new PGobject
          o.setType("jsonb")
          o.setValue(a.noSpaces)
          o
        }
      )

}
