package example



import scala.reflect.ClassTag
import org.scalameter.api._



class ContendedBenchmark extends PerformanceTest.Regression {

  def persistor = new SerializationPersistor

  val repetitions = Gen.range("size")(1000000, 5000000, 2000000)
  def objects[T <: AnyRef: ClassTag](newCell: Int => T) = for (sz <- repetitions) yield {
    val array = new Array[T](sz)
    for (i <- 0 until array.length) array(i) = newCell(i)
    array
  }

  performance of "Contended" config (
    exec.minWarmupRuns -> 5,
    exec.maxWarmupRuns -> 50,
    exec.benchRuns -> 30,
    exec.independentSamples -> 1,
    exec.jvmflags -> "-Xms3072M -Xmx3072M"
  ) in {
    using(objects(i => new Cell(i))) curve("non-volatile") setUp {
      arr => for (i <- 0 until arr.length) arr(i) = new Cell(i)
    } tearDown {
      arr => for (i <- 0 until arr.length) arr(i) = null
    } in { array =>
      val threads = for (_ <- 0 until 4) yield new Thread {
        override def run() {
          var i = 0
          while (i < array.length) {
            array(i).value
            i += 1
          }
        }
      }
      threads.foreach(_.start())
      threads.foreach(_.join())
    }

    using(objects(i => new LazyCell(i))) curve("lazy-current") setUp {
      arr => for (i <- 0 until arr.length) arr(i) = new LazyCell(i)
    } tearDown {
      arr => for (i <- 0 until arr.length) arr(i) = null
    } in { array =>
      val threads = for (_ <- 0 until 4) yield new Thread {
        override def run() {
          var i = 0
          while (i < array.length) {
            array(i).value
            i += 1
          }
        }
      }
      threads.foreach(_.start())
      threads.foreach(_.join())
    }
/*
    using(objects(i => new LazySimCellVersion3(i))) curve("lazy-simulation-v3") setUp {
      arr => for (i <- 0 until arr.length) arr(i) = new LazySimCellVersion3(i)
    } tearDown {
      arr => for (i <- 0 until arr.length) arr(i) = null
    } in { array =>
      val threads = for (_ <- 0 until 4) yield new Thread {
        override def run() {
          var i = 0
          while (i < array.length) {
            array(i).value
            i += 1
          }
        }
      }
      threads.foreach(_.start())
      threads.foreach(_.join())
    }

    using(objects(i => new LazySimCellVersion4General(i))) curve("lazy-simulation-v4-general") setUp {
      arr => for (i <- 0 until arr.length) arr(i) = new LazySimCellVersion4General(i)
    } tearDown {
      arr => for (i <- 0 until arr.length) arr(i) = null
    } in { array =>
      val threads = for (_ <- 0 until 4) yield new Thread {
        override def run() {
          var i = 0
          while (i < array.length) {
            array(i).value
            i += 1
          }
        }
      }
      threads.foreach(_.start())
      threads.foreach(_.join())
    }

    using(objects(i => new LazySimCellVersion41(i))) curve("lazy-simulation-v41") setUp {
      arr => for (i <- 0 until arr.length) arr(i) = new LazySimCellVersion41(i)
    } tearDown {
      arr => for (i <- 0 until arr.length) arr(i) = null
    } in { array =>
      val threads = for (_ <- 0 until 4) yield new Thread {
        override def run() {
          var i = 0
          while (i < array.length) {
            array(i).value
            i += 1
          }
        }
      }
      threads.foreach(_.start())
      threads.foreach(_.join())
    }
 */

    using(objects(i => new LazySimCellVersionV5(i))) curve("lazy-simulation-V5") setUp {
      arr => for (i <- 0 until arr.length) arr(i) = new LazySimCellVersionV5(i)
    } tearDown {
      arr => for (i <- 0 until arr.length) arr(i) = null
    } warmUp { 
      val arr = new Array[LazySimCellVersionV5](10000)
      var a = 0
      for (i <- 0 until arr.length) 
        arr(i) = if(i%2==1) new LazySimCellVersionV5({sys.error("bla"); 2}) else new LazySimCellVersionV5(1)
      for(x<- arr) 
        try{ a += x.value} 
        catch{
          case e:Throwable => a += 1
        }
    } in { array =>
      val threads = for (_ <- 0 until 4) yield new Thread {
        override def run() {
          var i = 0
          while (i < array.length) {
            array(i).value
            i += 1
          }
        }
      }
      threads.foreach(_.start())
      threads.foreach(_.join())
    }

    using(objects(i => new LazySimCellVersionV5Spin(i))) curve("lazy-simulation-V5Spin") setUp {
      arr => for (i <- 0 until arr.length) arr(i) = new LazySimCellVersionV5Spin(i)
    } tearDown {
      arr => for (i <- 0 until arr.length) arr(i) = null
    } warmUp { 
      val arr = new Array[LazySimCellVersionV5Spin](10000)
      var a = 0
      for (i <- 0 until arr.length) 
        arr(i) = if(i%2==1) new LazySimCellVersionV5Spin({sys.error("bla"); 2}) else new LazySimCellVersionV5Spin(1)
      for(x<- arr) 
        try{ a += x.value} 
        catch{
          case e:Throwable => a += 1
        }
    } in { array =>
      val threads = for (_ <- 0 until 4) yield new Thread {
        override def run() {
          var i = 0
          while (i < array.length) {
            array(i).value
            i += 1
          }
        }
      }
      threads.foreach(_.start())
      threads.foreach(_.join())
    }

    using(objects(i => new LazySimCellVersionV6(i))) curve("lazy-simulation-v6") setUp {
      arr => for (i <- 0 until arr.length) arr(i) = new LazySimCellVersionV6(i)
    } tearDown {
      arr => for (i <- 0 until arr.length) arr(i) = null
    } in { array =>
      val threads = for (_ <- 0 until 4) yield new Thread {
        override def run() {
          var i = 0
          while (i < array.length) {
            array(i).value
            i += 1
          }
        }
      }
      threads.foreach(_.start())
      threads.foreach(_.join())
    }

    using(objects(i => new LazySimCellVersionV7(i))) curve("lazy-simulation-v7") setUp {
      arr => for (i <- 0 until arr.length) arr(i) = new LazySimCellVersionV7(i)
    } tearDown {
      arr => for (i <- 0 until arr.length) arr(i) = null
    } warmUp {
      val arr = new Array[LazySimCellVersionV7](10000)
      var a = 0
      for (i <- 0 until arr.length)
        arr(i) = if(i%2==1) new LazySimCellVersionV7({sys.error("bla"); 2}) else new LazySimCellVersionV7(1)
      for(x<- arr)
        try{ a += x.value}
        catch{
          case e:Throwable => a += 1
        }
    } in { array =>
      val threads = for (_ <- 0 until 4) yield new Thread {
        override def run() {
          var i = 0
          while (i < array.length) {
            array(i).value
            i += 1
          }
        }
      }
      threads.foreach(_.start())
      threads.foreach(_.join())
    }


    using(objects(i => new LazySimCellVersionD2NoCASNoTry(i))) curve("lazy-simulation-d2-noCASnoTry") setUp {
      arr => for (i <- 0 until arr.length) arr(i) = new LazySimCellVersionD2NoCASNoTry(i)
    } tearDown {
      arr => for (i <- 0 until arr.length) arr(i) = null
    } in { array =>
      val threads = for (_ <- 0 until 4) yield new Thread {
        override def run() {
          var i = 0
          while (i < array.length) {
            array(i).value
            i += 1
          }
        }
      }
      threads.foreach(_.start())
      threads.foreach(_.join())
    }


    using(objects(i => new LazySimCellVersionD3NoCASTry(i))) curve("lazy-simulation-d3-noCASTry") setUp {
      arr => for (i <- 0 until arr.length) arr(i) = new LazySimCellVersionD3NoCASTry(i)
    } tearDown {
      arr => for (i <- 0 until arr.length) arr(i) = null
    } in { array =>
      val threads = for (_ <- 0 until 4) yield new Thread {
        override def run() {
          var i = 0
          while (i < array.length) {
            array(i).value
            i += 1
          }
        }
      }
      threads.foreach(_.start())
      threads.foreach(_.join())
    }

/*   using(objects(i => new LazyValsHm(i))) curve("lazy-simulation-HM") setUp {
      arr => for (i <- 0 until arr.length) arr(i) = new LazyValsHm(i)
    } tearDown {
      arr => for (i <- 0 until arr.length) arr(i) = null
    } in { array =>
      val threads = for (_ <- 0 until 4) yield new Thread {
        override def run() {
          var i = 0
          while (i < array.length) {
            array(i).value
            i += 1
          }
        }
      }
      threads.foreach(_.start())
      threads.foreach(_.join())
    }
 */

  }

}







