package pl.typosafe.demeter.caches

import java.nio.file.Path

import pl.typosafe.demeter.DemeterPlugin
import sbt._
import sbt.syntax._
import sbt.Keys._

trait StaticCache {
  self: DemeterPlugin =>

  val cacheLocation = SettingKey[Path]("demeterCacheLocation")

  def withStaticCache =

}
