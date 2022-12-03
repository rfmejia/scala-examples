package app

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*
import scala.util.Random

@main
def LiveChart(): Unit =
  renderOnDomContentLoaded(dom.document.querySelector("#app"), Main.appElement())

object Models:
  final class DataItemID

  private val alphaChars = ('a' to 'z')
  def generateString(length: Int) =
    List.fill(length)(alphaChars(Random.nextInt(alphaChars.size))).mkString

  case class DataItem(id: DataItemID, label: String, value: Double)
  object DataItem:
    def apply(): DataItem = DataItem(DataItemID(), generateString(8), Math.random())

object Main:
  import Models.*

  val dataVar = {
    val initialState = List.empty[DataItem]
    Var.apply[List[DataItem]](initialState)
  }
  val dataSignal = dataVar.signal

  def addItem(item: DataItem): Unit = dataVar.update(_ :+ item)
  def removeItem(id: DataItemID): Unit = dataVar.update(_.filter(_.id != id))
  def allValues = dataSignal.map(_.map(_.value))

  def appElement(): Element =
    div(
      h1("Scala.js demo"),
      renderDataTable(),
      ul(
        li("Sum: ", child.text <-- allValues.map(_.sum)),
        li(
          "Minimum: ",
          child.text <-- allValues.map(vs => if vs.nonEmpty then vs.min.toString else "none")
        ),
        li(
          "Maximum: ",
          child.text <-- allValues.map(vs => if vs.nonEmpty then vs.max.toString else "none")
        ),
        li(
          "Average: ",
          child.text <-- allValues.map(vs =>
            if vs.size != 0 then (vs.sum / vs.size).toString else "none"
          )
        )
      )
    )

  def renderDataItem(id: DataItemID, item: DataItem): Element =
    tr(
      td(item.label),
      td(item.value),
      td(button("x", onClick --> (_ => removeItem(id))))
    )

  def renderDataTable(): Element =
    table(
      thead(tr(th("Label"), th("Value"), th("Action"))),
      tbody(
        children <-- dataSignal.map(data => data.map { item => renderDataItem(item.id, item) })
      ),
      tfoot(tr(td(button("Add item", onClick --> (_ => addItem(DataItem()))))))
    )

  def renderDataGraph(): HtmlElement =
    // import typings.chartJs.mod.*
    ???

end Main

object InputComponents:
  def labelInput(valueSignal: Signal[String], valueUpdater: Observer[String]): Input =
    input(
      typ := "text",
      controlled(
        value <-- valueSignal,
        onInput.mapToValue --> valueUpdater
      )
    )

  def valueInput(valueSignal: Signal[Double], valueUpdater: Observer[Double]): Input =
    val strValue = Var[String]("")
    input(
      typ := "text",
      controlled(
        value <-- strValue.signal,
        onInput.mapToValue --> strValue
      ),
      valueSignal --> strValue.updater[Double] { (prevStr, newValue) =>
        if prevStr.toDoubleOption.contains(newValue) then prevStr
        else newValue.toString
      },
      strValue.signal --> { valueStr => valueStr.toDoubleOption.foreach(valueUpdater.onNext) }
    )
end InputComponents
