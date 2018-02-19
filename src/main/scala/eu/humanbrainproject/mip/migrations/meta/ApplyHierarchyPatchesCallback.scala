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

import org.flywaydb.core.api.callback.BaseFlywayCallback
import doobie._, doobie.implicits._
import java.sql.Connection
import doobie.free.KleisliInterpreter
import io.circe.Json
import gnieh.diffson.circe._
import cats.instances.all._
import cats.effect.IO

case class Patch(newSource: String,
                 originalHierarchy: Json,
                 hierarchyPatch: Json,
                 targetTable: String,
                 histogramGroupings: List[String])

class ApplyHierarchyPatchesCallback extends BaseFlywayCallback {

  @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.NonUnitStatements"))
  override def afterMigrate(connection: Connection): Unit = {

    implicit val ListMeta: Meta[List[String]] =
      Meta[String].xmap(_.split(",").toList, _.mkString(","))

    val patchesQuery: ConnectionIO[List[Patch]] =
      sql"""SELECT new_source as newSource, v.hierarchy as originalHierarchy, p.hierarchy_patch as hierarchyPatch,
                   p.target_table as targetTable, p.histogram_groupings as histogramGroupings
            FROM hierarchy_patches p
            INNER JOIN meta_variables v on p.original_source = v.source """
        .query[Patch]
        .to[List]

    // Creating an KleisliInterpreter for some Catchable: Suspendable
    val kleisliInt = KleisliInterpreter[IO]
    // Using the default ConnectionInterpreter:
    val nat = kleisliInt.ConnectionInterpreter
    // And then foldMap over this ConnectionInterpreter
    val result = patchesQuery.foldMap(nat).run(connection).unsafeRunSync

    type HierarchySource = (String, Json, String, List[String])
    val patches: List[HierarchySource] = result.map { patch =>
      val jsonPatch        = JsonPatch(patch.hierarchyPatch)
      val patchedVariables = jsonPatch(patch.originalHierarchy)

      (patch.newSource, patchedVariables, patch.targetTable, patch.histogramGroupings)
    }

    def deleteSources(ps: List[String]): ConnectionIO[Int] = {
      val sql = "DELETE FROM meta_variables WHERE source = ?"
      Update[String](sql).updateMany(ps)
    }

    def insertSources(ps: List[HierarchySource]): ConnectionIO[Int] = {
      val sql =
        "INSERT into meta_variables (source, hierarchy, target_table, histogram_groupings) VALUES (?, ?, ?, ?)"
      Update[HierarchySource](sql).updateMany(ps)
    }

    val regenerateMeta = for {
      rm    <- deleteSources(patches.map(_._1))
      added <- insertSources(patches)
    } yield (rm, added)

    regenerateMeta.foldMap(nat).run(connection).unsafeRunSync
    ()

  }
}
