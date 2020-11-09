/*
 * Copyright (C) 2016-2019, Roberto Casadei, Mirko Viroli, and contributors.
 * See the LICENSE file distributed with this work for additional information regarding copyright ownership.
*/

package it.unibo.scafi.lib

trait StdLib_BuildingBlocks {
  self: StandardLibrary.Subcomponent =>

  trait BuildingBlocks extends
         SimpleGradients_ScafiStandard
    with FieldUtils
    with BlockG
    with BlockC_ScafiStandard
    with BlockS
    with TimeUtilsInterface
    with BlocksWithGC
    with StateManagement
    with LanguageDependant_ScafiStandard {
    self: ScafiStandardLanguage with ScafiBaseLanguage with StandardSensors =>
  }
}
