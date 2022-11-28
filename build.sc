import mill._, scalalib._

object finagle extends ScalaModule {
  def scalaVersion = "2.13.10"
  def ivyDeps = Agg(ivy"com.twitter::finagle-http:22.4.0")
}

object datastructs extends ScalaModule {
  def scalaVersion = "3.2.1"
}
