package org.romanowski.hoarder.actions

import java.nio.file.{Files, Path, Paths}

import org.romanowski.HoarderCommonSettings._
import org.romanowski.hoarder.core.HoarderEngine
import sbt.Def._
import sbt.Keys._
import sbt._
import sbt.compiler.MixedAnalyzingCompiler


object Stash extends HoarderEngine {

  val stashKey = InputKey[Unit]("stash", "Stash results of your current compilation")
  val stashApplyKey = InputKey[Unit]("stashApply", "Stash results of your current compilation")

  private val doStashKey = InputKey[Unit]("doStashKey", "Stash results of your current compilation")
  private val doStashApplyKey = InputKey[Unit]("doStashApplyKey", "Apply stashed results of your current compilation")

  def doStashApplyImpl = Def.inputTask {
    val location = globalCacheLocation.evaluated

    val setup = projectSetupFor.value(location)

    importCacheTaskImpl(setup)

    streams.value.log.info(s"Cache imported from ${setup.cacheLocation} to use with ${setup.classesRoot} to ${setup.cacheLocation}")
  }

  def doStashImpl = Def.inputTask {
    val setup = projectSetupFor.value(globalCacheLocation.evaluated)

    exportCacheTaskImpl(setup, compileIncremental.value)
    streams.value.log.info(s"Cache exported to ${setup.cacheLocation} from ${setup.classesRoot}")
  }

  private def perConfigSettings = Seq(doStashKey <<= doStashImpl, doStashApplyKey <<= doStashApplyImpl)

  def settings =
    inConfig(Compile)(perConfigSettings) ++ inConfig(Test)(perConfigSettings) ++ Seq(
      stashKey := {
        streams.value.log.info(s"Running export in ${name.value}")
        (doStashKey in Compile).evaluated
        (doStashKey in Test).evaluated
      },
      stashApplyKey := {
        streams.value.log.info(s"Running import in ${name.value}")

        doStashApplyKey.in(Compile).evaluated
        doStashApplyKey.in(Test).evaluated
      },
      aggregate.in(stashKey) := true,
      aggregate.in(stashApplyKey) := true
    )

}


