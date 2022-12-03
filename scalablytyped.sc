import mill._, mill.scalalib._, mill.scalajslib._
import $ivy.`com.github.lolgab::mill-scalablytyped::0.1.0`
import com.github.lolgab.mill.scalablytyped._

object `scalablytyped-module` extends ScalaJSModule with ScalablyTyped {
  def scalaVersion = "3.2.0"
  def scalaJSVersion = "1.12.0"
}
