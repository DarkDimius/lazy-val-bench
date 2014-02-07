

import annotation.{tailrec, switch}



package object example {

  class Cell(x: Int) {
    val value = 0
  }

  class LazyCell(x: Int) {
    lazy val value = 0
  }

  final class LazySimCell(x: Int) {
    @volatile var bitmap_0: Boolean = false
    var value_0: Int = _
    private def value_lzycompute(): Int = {
      // note that most field accesses translate to virtual calls
      // (the bytecode is otherwise identical to `LazyCell`)
      // this is probably the reason this is 25% slower
      this.synchronized {
        if (!bitmap_0) {
          value_0 = 0
          bitmap_0 = true
        }
      }
      value_0
    }
    def value = if (bitmap_0) value_0 else value_lzycompute()
  }

  final class LazySimCellByteBitmap(x: Int) {
    @volatile var bitmap_0: Byte = 0.toByte
    var value_0: Int = _
    private def value_lzycompute(): Int = {
      this.synchronized {
        if (bitmap_0 == 0.toByte) {
          value_0 = 0
          bitmap_0 = 2.toByte
        }
      }
      value_0
    }
    def value = if (bitmap_0 == 2.toByte) value_0 else value_lzycompute()
  }

  final class LazySimCellVersion2WithoutNotify(x: Int) {
    @volatile var bitmap_0: Byte = 0.toByte
    var value_0: Int = _
    private def value_lzycompute(): Int = {
      this.synchronized {
        if (bitmap_0 == 0.toByte) {
          bitmap_0 = 1.toByte
        } else {
          while (bitmap_0 == 1.toByte) {
            this.wait()
          }
          return value_0
        }
      }
      val result = 0
      this.synchronized {
        value_0 = result
        bitmap_0 = 2.toByte
      }
      value_0
    }
    def value = if (bitmap_0 == 2.toByte) value_0 else value_lzycompute()
  }

  final class LazySimCellVersion2(x: Int) {
    @volatile var bitmap_0: Byte = 0.toByte
    var value_0: Int = _
    private def value_lzycompute(): Int = {
      this.synchronized {
        if (bitmap_0 == 0.toByte) {
          bitmap_0 = 1.toByte
        } else {
          while (bitmap_0 == 1.toByte) {
            this.wait()
          }
          return value_0
        }
      }
      val result = 0
      this.synchronized {
        value_0 = result
        bitmap_0 = 2.toByte
        this.notifyAll()
      }
      value_0
    }
    def value = if (bitmap_0 == 2.toByte) value_0 else value_lzycompute()
  }

  final class LazySimCellVersion3(x: Int) {
    @volatile var bitmap_0: Byte = 0.toByte
    var value_0: Int = _
    private def value_lzycompute(): Int = {
      this.synchronized {
        val b = bitmap_0
        if (b == 0.toByte) {
          bitmap_0 = 1.toByte
        } else {
          if (b == 1.toByte) {
            bitmap_0 = 2.toByte
          }
          while (bitmap_0 == 2.toByte) {
            this.wait()
          }
          return value_0
        }
      }
      val result = 0
      this.synchronized {
        val oldstate = bitmap_0
        value_0 = result
        bitmap_0 = 3.toByte
        if (oldstate == 2.toByte) this.notifyAll()
      }
      value_0
    }
    def value = if (bitmap_0 == 3.toByte) value_0 else value_lzycompute()
  }

  final class LazySimCellVersion4(x: Int) extends java.util.concurrent.atomic.AtomicInteger {
    var value_0: Int = _
    @tailrec final def value(): Int = (get: @switch) match {
      case 0 =>
        if (compareAndSet(0, 1)) {
          val result = 0
          value_0 = result
          if (getAndSet(3) != 1) synchronized { notifyAll() }
          result
        } else value()
      case 1 =>
        compareAndSet(1, 2)
        synchronized {
          while (get != 3) wait()
        }
        value_0
      case 2 =>
        synchronized {
          while (get != 3) wait()
        }
        value_0
      case 3 => value_0
    }
  }

  final class LazySimCellVersion4General(x: Int) extends LazySimCellWithPublicBitmap {
    import LazySimCellWithPublicBitmap._
    var value_0: Int = _
    @tailrec final def value(): Int = (arfu_0.get(this): @switch) match {
      case 0 =>
        if (arfu_0.compareAndSet(this, 0, 1)) {
          val result = 0
          value_0 = result

          @tailrec def complete(): Unit = (arfu_0.get(this): @switch) match {
            case 1 =>
              if (!arfu_0.compareAndSet(this, 1, 3)) complete()
            case 2 =>
              if (arfu_0.compareAndSet(this, 2, 3)) {
                synchronized { notifyAll() }
              } else complete()
          }

          complete()
          result
        } else value()
      case 1 =>
        arfu_0.compareAndSet(this, 1, 2)
        synchronized {
          while (arfu_0.get(this) != 3) wait()
        }
        value_0
      case 2 =>
        synchronized {
          while (arfu_0.get(this) != 3) wait()
        }
        value_0
      case 3 => value_0
    }
  }

  final class LazySimCellVersion5(x: Int) extends LazySimCellWithPublicBitmap {
    import LazySimCellWithPublicBitmap._
    var value_0: Int = _
    var bitmap2: Int= _

    final def value():Int = {
      if(bitmap2 == 1) value_0
      else value0()
    }
    @tailrec final def value0(): Int = (arfu_0.get(this): @switch) match {
      case 0 =>
        if (arfu_0.compareAndSet(this, 0, 1)) {
          val result = 0
          value_0 = result
          bitmap2 = 1
          @tailrec def complete(): Unit = (arfu_0.get(this): @switch) match {
            case 1 =>
              if (!arfu_0.compareAndSet(this, 1, 3)) complete()
            case 2 =>
              if (arfu_0.compareAndSet(this, 2, 3)) {
                synchronized { notifyAll() }
              } else complete()
          }

          complete()
          result
        } else value0()
      case 1 =>
        arfu_0.compareAndSet(this, 1, 2)
        synchronized {
          while (arfu_0.get(this) != 3) wait()
        }
        value_0
      case 2 =>
        synchronized {
          while (arfu_0.get(this) != 3) wait()
        }
        value_0
      case 3 => value_0
    }
  }

  class LazyBitMap0 {
    var bitmap_0: Int = 0
  }


  final class LazySimCellVersionD0(x: Int) extends LazyBitMap0{
    import LazySimCellVersionD0._
    var value_0: Int = _
    @tailrec final def value(): Int = (bitmap_0: @switch) match {
      case 0 =>
        if (compareAndSet(this, 0, 1)) {
          val result = 0
          value_0 = result

          @tailrec def complete(): Unit = (get(this): @switch) match {
            case 1 =>
              if (!compareAndSet(this, 1, 3)) complete()
            case 2 =>
              if (compareAndSet(this, 2, 3)) {
                getMonitor(this).synchronized { notifyAll() }
              } else complete()
          }

          complete()
          result
        } else value()
      case 1 =>
        compareAndSet(this, 1, 2)
        getMonitor(this).synchronized {
          while (get(this) != 3) wait()
        }
        value_0
      case 2 =>
        getMonitor(this).synchronized {
          while (get(this) != 3) wait()
        }
        value_0
      case 3 => value_0
    }
  }

 // all this data is thought to be global for all lazy vals

  object LazySimCellVersionD0 {
    import sun.misc.Unsafe

    val unsafe = getUnsafe()
    val BITMAP_0_OFFSET = unsafe.objectFieldOffset(classOf[LazyBitMap0].getDeclaredField("bitmap_0"))
    @inline def compareAndSet(target: LazyBitMap0, expected: Int, newValue: Int) = 
      unsafe.compareAndSwapInt(target, BITMAP_0_OFFSET, expected, newValue)

    @inline def get(target: LazyBitMap0) = unsafe.getIntVolatile(target, BITMAP_0_OFFSET)

    val processors = java.lang.Runtime.getRuntime().availableProcessors()

    val base = processors * processors * 2
    val monitors = (0 to base).map{x => new Object()}.toArray

    def getMonitor(obj: Object, fieldId: Int = 0) = {
      var id = (java.lang.System.identityHashCode(obj) + fieldId) % base
      if (id < 0) id += base
      monitors(id)
    }

    def getUnsafe(): Unsafe = {
      if (this.getClass.getClassLoader == null) Unsafe.getUnsafe()
      try {
        val fld = classOf[Unsafe].getDeclaredField("theUnsafe")
        fld.setAccessible(true)
        return fld.get(this.getClass).asInstanceOf[Unsafe]
      } catch {
        case e: Throwable => throw new RuntimeException("Could not obtain access to sun.misc.Unsafe", e)
      }
    }
  }
}












