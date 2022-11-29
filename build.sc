import mill._, mill.scalalib._

object finagle extends ScalaModule {
  def scalaVersion = "2.13.10"
  def ivyDeps = Agg(ivy"com.twitter::finagle-http:22.4.0")
}

object datastructs extends ScalaModule {
  def scalaVersion = "3.2.1"
}


object lambdaweb4s extends ScalaModule {
  def scalaVersion = "2.13.10"
  def ivyDeps = Agg(ivy"io.github.markosski::lambdaweb4s:0.1.3")
}
