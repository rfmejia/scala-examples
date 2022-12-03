package datastructs

import scala.reflect.ClassTag
import scala.util.{Try, Success, Failure}

sealed trait RingBuffer[A]:
  def isEmpty: Boolean
  def nonEmpty: Boolean
  def isFull: Boolean
  def peek: Option[A]
  def size: Int

  /** Returns an error if the buffer is full and overwrites are not allowed */
  def push(elem: A): Try[Unit]
  def pop: Option[A]

private[datastructs] class ArrayRingBuffer[A](
    arr: Array[Option[A]],
    overflowStrategy: RingBuffer.OverflowStrategy
) extends RingBuffer[A]:
  private var head = 0
  private var tail = 0

  private def next(idx: Int): Int =
    if idx + 1 == arr.size then 0 else idx + 1

  override def isEmpty: Boolean = head == tail
  override def nonEmpty: Boolean = !isEmpty
  override def isFull: Boolean = next(tail) == head
  override def peek: Option[A] = if isEmpty then None else arr(head)
  override def size: Int =
    if isEmpty then 0
    else if head < tail then tail - head
    else tail + arr.size - head

  override def push(elem: A): Try[Unit] =
    if isFull && overflowStrategy == RingBuffer.OverflowStrategy.Drop then
      Failure(sys.error("Cannot write to a full buffer, dropping"))
    else
      if isFull then head = next(head)
      arr.update(tail, Some(elem))
      tail = next(tail)
      arr.update(tail, None) // Keep the new tail clear to prevent dangling references
      Success(())

  override def pop: Option[A] =
    if isEmpty then None
    else
      val elem = arr(head)
      arr.update(head, None)
      head = next(head)
      elem

  override def toString = arr.view.zipWithIndex
    .map {
      case (elem, i) if i == head && i == tail => s"ht[$elem]"
      case (elem, i) if i == head              => s"h[$elem]"
      case (elem, i) if i == tail              => s"t[$elem]"
      case (elem, _)                           => elem.toString
    }
    .mkString(" ")

end ArrayRingBuffer

object RingBuffer:
  val DefaultSize = 20

  enum OverflowStrategy:
    case
      /** Overwrite the oldest data if attempting to write to a full buffer */
      Drop,
      /** Return an error if attempting to write to a full buffer */
      Overwrite

  def ofSize[A: ClassTag](
      size: Int = DefaultSize,
      strategy: OverflowStrategy = OverflowStrategy.Drop
  ) =
    new ArrayRingBuffer(Array.fill[Option[A]](size + 1)(None), strategy)
