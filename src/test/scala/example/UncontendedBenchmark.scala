package example



import org.scalameter.api._



class UncontendedBenchmark extends PerformanceTest.Regression with Serializable {

  def persistor = new SerializationPersistor

  val repetitions = Gen.range("size")(100000, 500000, 200000)

  val tinyRepetitions = Gen.range("size")(1000, 5000, 10000)

  var cell: AnyRef = null

  performance of "LazyVals" config (
    exec.minWarmupRuns -> 30,
    exec.maxWarmupRuns -> 10,
    exec.benchRuns -> 25,
    exec.independentSamples -> 1,
    exec.jvmflags -> "-Xms3072M -Xmx3072M"
//    ,exec.jvmcmd -> "java8 -server"
  ) in {
    using(repetitions) curve("non-volatile") in { n =>
      var i = 0
      while (i < n) {
        val c = new Cell(i)
        cell = c
        c.value
        i += 1
      }
    }

    using(repetitions) curve("lazy-current") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazyCell(i)
        cell = c
        c.value
        i += 1
      }
    }
/*
    using(repetitions) curve("lazy-simulation-boolean-bitmap") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCell(i)
        cell = c
        c.value
        i += 1
      }
    }

    using(repetitions) curve("lazy-simulation-byte-bitmap") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellByteBitmap(i)
        cell = c
        c.value
        i += 1
      }
    }

    using(repetitions) curve("lazy-simulation-v2-without-notify") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellVersion2WithoutNotify(i)
        cell = c
        c.value
        i += 1
      }
    }

    using(repetitions) curve("lazy-simulation-v2-with-notify") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellVersion2(i)
        cell = c
        c.value
        i += 1
      }
    }

    using(repetitions) curve("lazy-simulation-v3") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellVersion3(i)
        cell = c
        c.value
        i += 1
      }
    }

    using(repetitions) curve("lazy-simulation-v4") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellVersion4(i)
        cell = c
        c.value
        i += 1
      }
    }

    using(repetitions) curve("lazy-simulation-v4-general") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellVersion4General(i)
        cell = c
        c.value
        i += 1
      }
    }
 */
/*
    using(repetitions) curve("lazy-simulation-v5") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellVersion5(i)
        cell = c
        c.value
        i += 1
      }
    }
 */
    using(repetitions) curve("lazy-simulation-d3Try") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellVersionD3Try(i)
        cell = c
        c.value
        i += 1
      }
    }

    using(repetitions) curve("lazy-simulation-d0") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellVersionD0(i)
        cell = c
        c.value
        i += 1
      }
    }

    using(repetitions) curve("lazy-simulation-d1Try") warmUp { 
      val arr = new Array[LazySimCellVersionD1Try](100000)
      for (i <- 0 until arr.length) 
        arr(i) = if(i%2==1) new LazySimCellVersionD1Try({sys.error("bla"); 2}) else new LazySimCellVersionD1Try(1)
      for(x<- arr) 
        try{x.value} 
        catch{
          case e:Throwable => 1
        }
    } in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellVersionD1Try(i)
        cell = c
        c.value
        i += 1
      }
    }

    using(repetitions) curve("lazy-simulation-d2-noCASnoTry") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellVersionD2NoCASNoTry(i)
        cell = c
        c.value
        i += 1
      }
    }

    using(repetitions) curve("lazy-simulation-d3-noCASTry") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellVersionD3NoCASTry(i)
        cell = c
        c.value
        i += 1
      }
    }

/*
    using(tinyRepetitions) curve("lazy-simulation-MH") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazyValsHm(i)
        cell = c
        c.value()
        i += 1
      }
    }
 */

  }

}


