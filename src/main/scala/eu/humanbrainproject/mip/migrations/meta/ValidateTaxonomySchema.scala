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

package eu.humanbrainproject.mip.migrations.meta
import org.everit.json.schema.{ Schema, Validator }
import org.everit.json.schema.loader.SchemaLoader
import org.json.{ JSONObject, JSONTokener }

import scala.io.Source
import scala.util.Try

trait ValidateTaxonomySchema {

  val schemaValidator: Validator = Validator.builder().failEarly().build()

  lazy val schema: Schema = {
    val schemaSource = Source.fromFile("/src/variables_schema.json").getLines().mkString("")
    val rawSchema    = new JSONObject(new JSONTokener(schemaSource))
    SchemaLoader.load(rawSchema)
  }

  def validateTaxonomy(taxonomy: JSONObject): Try[Unit] =
    Try {
      // throws a ValidationException if this object is invalid
      schemaValidator.performValidation(schema, taxonomy)
    }

}
