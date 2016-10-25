package example



import org.scalameter.api._

import org.scalameter.api._



class benchmarks extends PerformanceTest.Regression with Serializable {

  /* config */

  def persistor = new SerializationPersistor

  /* tests */

  include[ContendedBenchmark]
  include[UncontendedBenchmark]
  include[MemoryFootprint]
  include[SwitchPointBenchmark]
}
