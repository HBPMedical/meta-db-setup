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

import java.sql.Connection

import org.flywaydb.core.api.callback.{ Callback, Context, Event }
import doobie._
import doobie.implicits._
import doobie.free.KleisliInterpreter
import io.circe.Json
import io.circe.parser.parse
import gnieh.diffson.circe._
import cats.instances.all._
import cats.effect.IO
import javax.xml.bind.ValidationException
import org.json.JSONObject

import scala.io.Source

case class TaxonomyPatchDefinition(source: String,
                                   newSource: String,
                                   targetTable: String,
                                   histogramGroupings: List[String]) {

  def patch: String = Source.fromFile(s"/src/patches/$newSource.patch.json").mkString

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def jsonPatch: Json = parse(patch).left.map[Json](e => throw e).merge

}

case class Taxonomy(source: String,
                    taxonomy: Json,
                    targetTable: String,
                    histogramGroupings: List[String]) {

  def hierarchy: Json = taxonomy

}

class SetupTaxonomyPatchesCallback extends Callback with ValidateTaxonomySchema {

  override def supports(event: Event,
                        context: Context): Boolean = event == Event.AFTER_MIGRATE

  override def canHandleInTransaction(
                                       event: Event,
                                       context: Context
                                     ): Boolean = true

  override def handle(event: Event,
                      context: Context): Unit = event match {
    case Event.AFTER_MIGRATE => setupTaxonomyPatches(context.getConnection)
    case _ => ()

  }

  @SuppressWarnings(
    Array("org.wartremover.warts.Any",
          "org.wartremover.warts.NonUnitStatements",
          "org.wartremover.warts.Throw")
  )
  def setupTaxonomyPatches(connection: Connection): Unit = {

    implicit val ListMeta: Meta[List[String]] =
      Meta[String].xmap(_.split(",").toList, _.mkString(","))

    def queryTaxonomy(source: String): ConnectionIO[Json] =
      sql"""SELECT hierarchy
            FROM meta_variables
            WHERE source=$source"""
        .query[Json]
        .unique

    // Creating an KleisliInterpreter for some Catchable: Suspendable
    val kleisliInt = KleisliInterpreter[IO]
    // Using the default ConnectionInterpreter:
    val nat = kleisliInt.ConnectionInterpreter
    // And then foldMap over this ConnectionInterpreter

    val newTaxonomies: List[Taxonomy] = taxonomyPatches.map { patch =>
      val jsonPatch         = JsonPatch(patch.jsonPatch)
      val originalHierarchy = queryTaxonomy(patch.source).foldMap(nat).run(connection).unsafeRunSync
      val patchedVariables  = jsonPatch(originalHierarchy)

      println(s"Checking validity of ${patch.newSource}...")

      validateTaxonomy(new JSONObject(patchedVariables.toString))
        .recoverWith {
          case e: ValidationException =>
            println(s"Invalid hierarchy: ${e.getMessage}")
            println(patchedVariables.toString)
            throw e
        }

      Taxonomy(patch.newSource, patchedVariables, patch.targetTable, patch.histogramGroupings)
    }

    def deleteSources(ps: List[String]): ConnectionIO[Int] = {
      val sql = "DELETE FROM meta_variables WHERE source = ?"
      Update[String](sql).updateMany(ps)
    }

    def insertSources(ps: List[Taxonomy]): ConnectionIO[Int] = {
      val sql =
        "INSERT into meta_variables (source, hierarchy, target_table, histogram_groupings) VALUES (?, ?, ?, ?)"
      Update[Taxonomy](sql).updateMany(ps)
    }

    val regenerateMeta = for {
      rm    <- deleteSources(newTaxonomies.map(_.source))
      added <- insertSources(newTaxonomies)
    } yield (rm, added)

    regenerateMeta.foldMap(nat).run(connection).unsafeRunSync
    ()

  }

  lazy val taxonomyPatches: List[TaxonomyPatchDefinition] = {
    val env = Option(System.getenv("TAXONOMY_PATCHES")).getOrElse("")
    env
      .split(" ")
      .filter(_.nonEmpty)
      .map { rawDef =>
        val t = rawDef.split("\\|")
        if (t.length < 4) {
          throw new IllegalArgumentException(s"Invalid format for TAXONOMY_PATCHES environment variable. Found ${t.toList.mkString("|")}, expecting source|new_source|target_table|histogram_groupings")
        }
        TaxonomyPatchDefinition(t(0), t(1), t(2), t(3).split(",").toList)
      }
      .toList
  }

}
