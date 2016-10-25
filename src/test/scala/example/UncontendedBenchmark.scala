package example



import org.scalameter.api._



class UncontendedBenchmark extends PerformanceTest.Regression with Serializable {

  def persistor = new SerializationPersistor


}


