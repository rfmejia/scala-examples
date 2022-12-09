package datastructs

import scala.reflect.ClassTag
import scala.util.{Try, Success, Failure}

trait RingBuffer[A]:
  def isEmpty: Boolean
  def nonEmpty: Boolean
  def isFull: Boolean
  def peek: Option[A]
  def size: Int

  /** Returns an error if the buffer is full and overwrites are not allowed */
  def push(elem: A): Try[Unit]
  def pop: Option[A]

  def toList: List[A]

object RingBuffer:
  val DefaultSize: Int = 20

  enum OverflowStrategy:
    case
      /** Overwrite the oldest data if attempting to write to a full buffer */
      Drop,
      /** Return an error if attempting to write to a full buffer */
      Overwrite
