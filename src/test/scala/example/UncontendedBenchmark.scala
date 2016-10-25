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
        val c = new LazySimCellVersion41(i)
        cell = c
        c.value
        i += 1
      }
    }
 */
    using(repetitions) curve("lazy-simulation-v5") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellVersionV5(i)
        cell = c
        c.value
        i += 1
      }
    }

    using(repetitions) curve("lazy-simulation-V5Spin") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellVersionV5Spin(i)
        cell = c
        c.value
        i += 1
      }
    }

    using(repetitions) curve("lazy-simulation-v6") in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellVersionV6(i)
        cell = c
        c.value
        i += 1
      }
    }

    using(repetitions) curve("lazy-simulation-v7") warmUp { 
      val arr = new Array[LazySimCellVersionV7](100000)
      for (i <- 0 until arr.length) 
        arr(i) = if(i%2==1) new LazySimCellVersionV7({sys.error("bla"); 2}) else new LazySimCellVersionV7(1)
      for(x<- arr) 
        try{x.value} 
        catch{
          case e:Throwable => 1
        }
    } in { n =>
      var i = 0
      while (i < n) {
        val c = new LazySimCellVersionV7(i)
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


