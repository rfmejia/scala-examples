package datastructs

import munit.FunSuite
import datastructs.ArrayRingBuffer
import datastructs.RingBuffer.OverflowStrategy

trait ArrayRingBufferTests extends FunSuite:
  test("ArrayRingBuffer basic operations") {
    val cb = ArrayRingBuffer.ofSize[Int](3)
    assertEquals(cb.size, 0)
    assertEquals(cb.pop, None)
    assert(cb.push(1).isSuccess)
    assert(cb.push(2).isSuccess)
    assertEquals(cb.pop, Some(1))
  }

  test("ArrayRingBuffer drop overflow stategy") {
    val cb = ArrayRingBuffer.ofSize[Int](3, OverflowStrategy.Drop)
    for (i <- 1 to 5) do cb.push(i)
    assertEquals(cb.toList, List(1, 2, 3))
    assert(cb.push(6).isFailure)
  }

  test("ArrayRingBuffer overwrite overflow stategy") {
    val cb = ArrayRingBuffer.ofSize[Int](3, OverflowStrategy.Overwrite)
    for (i <- 1 to 5) do cb.push(i)
    assertEquals(cb.toList, List(3, 4, 5))
  }
