

import annotation.{tailrec, switch}



package object example {

  class Cell(x: =>Int) {
    var bitmap_0 = 0
    var value_0: Int = 0
    def value = if (bitmap_0 == 1) value_0 else {
      value_0 = x
      bitmap_0 = 1
      value_0
    }
  }

  class LazyCell(x: =>Int) {
    lazy val value = x
  }

  final class LazySimCell(x: =>Int) {
    @volatile var bitmap_0: Boolean = false
    var value_0: Int = _
    private def value_lzycompute(): Int = {
      // note that most field accesses translate to virtual calls
      // (the bytecode is otherwise identical to `LazyCell`)
      // this is probably the reason this is 25% slower
      this.synchronized {
        if (!bitmap_0) {
          value_0 = x
          bitmap_0 = true
        }
      }
      value_0
    }
    def value = if (bitmap_0) value_0 else value_lzycompute()
  }

  final class LazySimCellByteBitmap(x: =>Int) {
    @volatile var bitmap_0: Byte = 0.toByte
    var value_0: Int = _
    private def value_lzycompute(): Int = {
      this.synchronized {
        if (bitmap_0 == 0.toByte) {
          value_0 = x
          bitmap_0 = 2.toByte
        }
      }
      value_0
    }
    def value = if (bitmap_0 == 2.toByte) value_0 else value_lzycompute()
  }

  final class LazySimCellVersion2WithoutNotify(x: =>Int) {
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
      val result = x
      this.synchronized {
        value_0 = result
        bitmap_0 = 2.toByte
      }
      value_0
    }
    def value = if (bitmap_0 == 2.toByte) value_0 else value_lzycompute()
  }

  final class LazySimCellVersion2(x: =>Int) {
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
      val result = x
      this.synchronized {
        value_0 = result
        bitmap_0 = 2.toByte
        this.notifyAll()
      }
      value_0
    }
    def value = if (bitmap_0 == 2.toByte) value_0 else value_lzycompute()
  }

  final class LazySimCellVersion3(x: =>Int) {
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
      val result = x
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

  final class LazySimCellVersion4(x: =>Int) extends java.util.concurrent.atomic.AtomicInteger {
    var value_0: Int = _
    @tailrec final def value(): Int = (get: @switch) match {
      case 0 =>
        if (compareAndSet(0, 1)) {
          val result = x
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

  final class LazySimCellVersion4General(x: =>Int) extends LazySimCellWithPublicBitmap {
    import LazySimCellWithPublicBitmap._
    @volatile var value_0: Int = _
    @tailrec final def value(): Int = (arfu_0.get(this): @switch) match {
      case 0 =>
        if (arfu_0.compareAndSet(this, 0, 1)) {
          val result = x
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

  final class LazySimCellVersion41(x: =>Int) extends LazySimCellWithPublicBitmap {
    import LazySimCellWithPublicBitmap._
    @volatile var value_0: Int = _
    @volatile var bitmap2: Int= _

    final def value():Int = {
      if(bitmap2 == 3) value_0
      else value0()
    }
    @tailrec final def value0(): Int = (arfu_0.get(this): @switch) match {
      case 0 =>
        if (arfu_0.compareAndSet(this, 0, 1)) {
          val result = x
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

  final class LazySimCellVersionV5(x: =>Int) extends LazyBitMap0 { // original name: D3Try
    import LazySimCellVersionD0._
    @volatile var value_0: Int = _

    @tailrec final def value(): Int = (get(this): @switch) match {
      case 0 =>
        if (compareAndSet(this, 0, 1)) {
          @tailrec def complete(value: Int): Unit = (get(this): @switch) match {
            case 1 =>
              if (!compareAndSet(this, 1, value)) complete(value)
            case 2 =>
              if (compareAndSet(this, 2, value)) {
                val monitor = getMonitor(this)
                monitor.synchronized { monitor.notifyAll() }
              } else complete(value)
          }

          val result = try {
            x
          }
          catch {
            case e: Throwable =>
              complete(0)
              throw e
          }

          value_0 = result

          complete(3)
          result
        } else value()
      case 1 =>
        compareAndSet(this, 1, 2)
        val monitor = getMonitor(this)
        monitor.synchronized {
          while (get(this) == 2) monitor.wait()
        }
        value
      case 2 =>
        val monitor = getMonitor(this)
        monitor.synchronized {
          while (get(this) == 2) monitor.wait()
        }
        value
      case 3 => value_0
    }
  }


  final class LazySimCellVersionV5Spin(x: =>Int) extends LazyBitMap0 { // Original name: D3TrySpin
    import LazySimCellVersionD0._
    @volatile var value_0: Int = _

    @tailrec final def value(): Int = (get(this): @switch) match {
      case 3 => value_0
      case 0 =>
        if (compareAndSet(this, 0, 1)) {
          @tailrec def complete(value: Int): Unit = (get(this): @switch) match {
            case 1 =>
              if (!compareAndSet(this, 1, value)) complete(value)
            case 2 =>
              if (compareAndSet(this, 2, value)) {
                val monitor = getMonitor(this)
                monitor.synchronized { monitor.notifyAll() }
              } else complete(value)
          }

          val result = try {
            x
          }
          catch {
            case e: Throwable =>
              complete(0)
              throw e
          }

          value_0 = result

          complete(3)
          result
        } else value()
      case _ =>
        var r = 1
        var spins = 1 << 8
        var state = 1
        while (spins >= 0 && (state != 3)) {
          r ^= r << 1; r ^= r >>> 3; r ^= r << 10;
          if (r >= 0) spins -=1
          state = get(this)
        }
        (state: @switch) match {
          case 0 => value
          case 1 =>  
            compareAndSet(this, 1, 2)
            val monitor = getMonitor(this)
            monitor.synchronized {
              while (get(this) == 2) monitor.wait()
            }
            value
          case 2 =>
            val monitor = getMonitor(this)
            monitor.synchronized {
              while (get(this) == 2) monitor.wait()
            }
            value
          case 3 => value_0
        }
    }
  }

  class LazyBitMap0 {
    var bitmap_0: Int = 0
  }


  final class LazySimCellVersionV6(x: => Int) extends LazyBitMap0{ // OriginalName: D0
    import LazySimCellVersionD0._
    @volatile var value_0: Int = _
    @tailrec final def value(): Int = (bitmap_0: @switch) match {
      case 0 =>
        if (compareAndSet(this, 0, 1)) {
          val result = x
          value_0 = result

          @tailrec def complete(): Unit = (get(this): @switch) match {
            case 1 =>
              if (!compareAndSet(this, 1, 3)) complete()
            case 2 =>
              if (compareAndSet(this, 2, 3)) {
                val monitor = getMonitor(this)
                monitor.synchronized {  monitor.notifyAll() }
              } else complete()
          }

          complete()
          result
        } else value()
      case 1 =>
        compareAndSet(this, 1, 2)
        val monitor = getMonitor(this)
        monitor.synchronized {
          while (get(this) != 3) monitor.wait()
        }
        value_0
      case 2 =>
        val monitor = getMonitor(this)
       monitor.synchronized {
          while (get(this) != 3) monitor.wait()
        }
        value_0
      case 3 => value_0
    }
  }

  final class LazySimCellVersionV7(x: => Int) extends LazyBitMap0{ // Original name: D1Try
    import LazySimCellVersionD0._
    @volatile var value_0: Int = _
    @tailrec final def value(): Int = (bitmap_0: @switch) match {
      case 0 =>
        if (compareAndSet(this, 0, 1)) {
          try {
            val result = x
            value_0 = result
          }
          catch {
            case e: Throwable=> 
              complete(0); //allow other threads to continue computation
              throw e
          }

          @tailrec def complete(newState: Int): Unit = (get(this): @switch) match {
            case 1 =>
              if (!compareAndSet(this, 1, newState)) complete(newState)
            case 2 =>
              if (compareAndSet(this, 2, newState)) {
                val monitor = getMonitor(this)
                monitor.synchronized {  monitor.notifyAll() }
              } else complete(newState)
          }

          complete(3)
          value_0
        } else value()
      case 1 =>
        if(compareAndSet(this, 1, 2)) {
          val monitor = getMonitor(this)
          monitor.synchronized {
            while (get(this) == 2) monitor.wait()
          }
        }
        value()
      case 2 =>
        val monitor = getMonitor(this)
        monitor.synchronized {
          while (get(this) == 2) monitor.wait()
        }
        value
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

  final class LazySimCellVersionD2NoCASNoTry(x: =>Int) {
    import LazySimCellVersionD0._
    var value_0: Int = _
    var bitmap_0: Int = 0
    @tailrec final def value(): Int = (bitmap_0: @switch) match {
      case 0 =>
        var aquired = true
        val monitor = getMonitor(this)
        monitor.synchronized {
          if (bitmap_0 == 0) {
            bitmap_0 = 1
          } else {
            aquired = false
            if (bitmap_0 == 1) {
              bitmap_0 = 2
            }
            while (bitmap_0 == 2) monitor.wait()
          }
        }
        if(aquired) {
          val result = x
          value_0 = result
          monitor.synchronized {
            if (bitmap_0 == 2)  monitor.notifyAll()
            bitmap_0 = 3
          }
          value_0
        }
        else value()
      case 1 =>
        val monitor = getMonitor(this)
        monitor.synchronized {
          if (bitmap_0 == 1) {
            bitmap_0 = 2
          }
          while (bitmap_0 == 2) monitor.wait()
        }
        value_0
      case 2 =>
        val monitor = getMonitor(this)
        monitor.synchronized {
          while (bitmap_0 == 2) monitor.wait()
        }
        value_0
      case 3 => value_0
    }
  }

  final class LazySimCellVersionD3NoCASTry(x: =>Int) {
    import LazySimCellVersionD0._
    var value_0: Int = _
    var bitmap_0: Int = 0
    @tailrec final def value(): Int = (bitmap_0: @switch) match {
      case 0 =>
        var aquired = true
        val monitor = getMonitor(this)
        monitor.synchronized {
          if (bitmap_0 == 0) {
            bitmap_0 = 1
          } else {
            aquired = false
            if (bitmap_0 == 1) {
              bitmap_0 = 2
            }
            while (bitmap_0 == 2) monitor.wait()
          }
        }
        if(aquired) {
          val monitor = getMonitor(this)
          try {
            val result = x
            value_0 = result
            monitor.synchronized {
              if (bitmap_0 == 2) monitor.notifyAll()
              bitmap_0 = 3
            }
            value_0
          }
          catch {
            case e: Throwable=>
              monitor.synchronized {
                if (bitmap_0 == 2) monitor.notifyAll()
                bitmap_0 = 0
              }
              throw e
          }
        }
        else value()
      case 1 =>
        val monitor = getMonitor(this)
        monitor.synchronized {
          if (bitmap_0 == 1) {
            bitmap_0 = 2
          }
          while (bitmap_0 == 2) monitor.wait()
        }
        value()
      case 2 =>
        val monitor = getMonitor(this)
        monitor.synchronized {
          while (bitmap_0 == 2) monitor.wait()
        }
        value()
      case 3 => value_0
    }
  }

}












