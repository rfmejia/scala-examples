import $file.scalablytyped
import mill._
import mill.scalalib._, mill.scalalib.scalafmt._
import mill.scalajslib._, mill.scalajslib.api._

trait Scala213Module extends ScalaModule with ScalafmtModule {
  def scalaVersion = "2.13.10"
  def scalacOptions = Seq("-encoding", "utf8", "-deprecation", "-feature")
}

trait Scala3Module extends ScalaModule with ScalafmtModule {
  def scalaVersion = "3.2.0"
  def scalacOptions = Seq("-encoding", "utf8", "-deprecation", "-feature")
}

// SUBMODULES
object finagle extends Scala213Module {
  def ivyDeps = Agg(ivy"com.twitter::finagle-http:22.4.0")
}

object datastructs extends Scala3Module {
  object test extends Tests with TestModule.Utest {
    def ivyDeps = Agg(ivy"com.lihaoyi::utest:0.8.1")
  }
}

object algorithms extends Scala3Module {
  def moduleDeps = Seq(datastructs)
}

object lambdaweb4s extends Scala213Module {
  def ivyDeps = Agg(ivy"io.github.markosski::lambdaweb4s:0.1.3")
}

object scalajs_laminar extends ScalaJSModule with ScalafmtModule {
  def scalaVersion = "3.2.0"
  def scalaJSVersion = "1.12.0"
  def moduleKind = T { ModuleKind.ESModule }
  def moduleDeps = Seq(scalablytyped.`scalablytyped-module`)
  def ivyDeps = Agg(ivy"org.scala-js::scalajs-dom::2.3.0", ivy"com.raquo::laminar::0.14.5")
  def scalablyTypedBasePath = T { millSourcePath }
}
