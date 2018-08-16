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

import cats.effect.IO
import java.sql.Connection

import doobie._
import doobie.implicits._
import doobie.free.KleisliInterpreter
import io.circe.Json
import javax.xml.bind.ValidationException
import org.everit.json.schema.Validator
import org.everit.json.schema.loader.SchemaLoader
import org.flywaydb.core.api.callback.BaseFlywayCallback
import org.json.{ JSONObject, JSONTokener }

import scala.io.Source

class CheckHierarchyCallback extends BaseFlywayCallback {

  @SuppressWarnings(
    Array("org.wartremover.warts.Any",
          "org.wartremover.warts.NonUnitStatements",
          "org.wartremover.warts.Throw")
  )
  override def afterMigrate(connection: Connection): Unit = {

    val schemaSource    = Source.fromFile("/src/variables_schema.json").getLines().mkString("")
    val rawSchema       = new JSONObject(new JSONTokener(schemaSource))
    val schema          = SchemaLoader.load(rawSchema)
    val schemaValidator = Validator.builder().failEarly().build()

    val hierarchiesQuery: ConnectionIO[List[(String, Json)]] =
      sql"""SELECT source, hierarchy FROM meta_variables"""
        .query[(String, Json)]
        .to[List]

    // Creating an KleisliInterpreter for some Catchable: Suspendable
    val kleisliInt = KleisliInterpreter[IO]
    // Using the default ConnectionInterpreter:
    val nat = kleisliInt.ConnectionInterpreter
    // And then foldMap over this ConnectionInterpreter
    val result = hierarchiesQuery.foldMap(nat).run(connection).unsafeRunSync

    result.foreach {
      case (source, hierarchySource) =>
        println(s"Checking validity of $source...")
        try {
          schemaValidator
            .performValidation(schema, new JSONObject(hierarchySource.toString)) // throws a ValidationException if this object is invalid
        } catch {
          case e: ValidationException =>
            println(s"Invalid hierarchy: ${e.getMessage}")
            println(hierarchySource.toString)
            throw e
        }
    }
  }

}
