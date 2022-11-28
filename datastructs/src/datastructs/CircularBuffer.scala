package datastructs

import scala.reflect.ClassTag

class CircularBuffer[A] private (arr: Array[A]):
  private var head = 0
  private var tail = 0

  def peek: Option[A] = if head == tail then None else Some(arr(head))

  def size: Int =
    if head == tail then 0
    else if head < tail then tail - head
    else tail + arr.size - head

  def push(elem: A): Unit =
    arr.update(tail, elem)
    tail += 1
    if tail == arr.size then tail = 0
    if head == tail then head + 1
    if head == arr.size then head = 0

  def pop: Option[A] =
    if head == tail then
      if arr(head) == null then None
      else
        val elem = Some(arr(head))
        head += 1
        tail += 1
        if head == arr.size then head = 0
        if tail == arr.size then tail = 0
        elem
    else
      val elem = Some(arr(head))
      // arr.update(head, null: A) // FIXME How do I remove this dangling reference?
      head += 1
      if head == arr.size then head = 0
      elem

  override def toString = arr.view.zipWithIndex
    .map {
      case (elem, i) if i == head && i == tail => s"ht $elem"
      case (elem, i) if i == head              => s"h  $elem"
      case (elem, i) if i == tail              => s" t $elem"
      case (elem, _)                           => s"   $elem"
    }
    .mkString("\n")

end CircularBuffer

object CircularBuffer:
  val DefaultSize = 20

  def ofSize[A: ClassTag](size: Int) = new CircularBuffer(Array.ofDim[A](size))
  def empty[A: ClassTag] = new CircularBuffer(Array.ofDim[A](DefaultSize))
