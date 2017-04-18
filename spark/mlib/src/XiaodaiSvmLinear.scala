
import org.apache.spark._
import org.apache.spark.rdd.RDD
import  scala.collection.mutable.Set
import  org.apache.spark.mllib.tree.RandomForest
import  org.apache.spark.mllib.regression.LabeledPoint
import  org.apache.spark.mllib.linalg.Vectors
import  org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.optimization.L1Updater


/**
 * @author Administrator
 */
object XiaodaiSvmLinear {
  val sparkConf = new SparkConf().setAppName("xiaodai")
  val sc = new SparkContext(sparkConf)

 var  city = sc.textFile("/data/chengqj/city.csv",1).collect()
 var  hash=Map[String,Double]()
 city.foreach{x  =>  hash +=(x.split(",")(1)  ->  x.split(",")(0).toDouble)}
 var  data:RDD[LabeledPoint]  = sc.textFile("/data/chengqj/myxiaodai.csv",10).mapPartitions{
    iter  => 
    var res = List[LabeledPoint]() 
    while (iter.hasNext){
       var  line =iter.next()
       println(line)
       val fields = line.split(",",-1)
       var length = fields.length;
       var vfields =new  Array[Double](56) 
       vfields(0)=fields(0).substring(0, 3).toDouble
       for(i<- 1 to 55){
         try{
           if(i==19 ||i==20 || i==21 |i==22)
             if(hash(fields(i+1))==null)
               vfields(i)=0L
             else
               vfields(i)=hash(fields(i+1))
           //else if(i==4||i==23||i==25||i==26)
            // vfields(i)=fields(i+1).substring(4).toDouble
           else
             vfields(i)=fields(i+1).toDouble
         
         }catch{                
              case  e:Exception => vfields(i)=0L
         }          
       }  
       res .::=(LabeledPoint(fields(1).toDouble, Vectors.dense( vfields)) )
    }
    res.iterator

    }
    
   
  /*
 var filename = "/data/mllib/svm_test_dyh"

 
 //load data
 
   var data = sc.textFile(filename).map{ line =>
   var tokens = line.split(" ")
 var label = tokens(0)
 var features = tokens.slice(1, tokens.size)
 LabeledPoint(label.toDouble, Vectors.dense(features.map(f => f.toDouble)))
 }
 * 
 */
       
data.count()
data.foreach { println(_) }
data.filter { x => x.label==1 }.count()

var splits = data.randomSplit(Array(0.7, 0.3))
var (trainingData, testData) = (splits(0), splits(1))
trainingData.cache()
var pdata=trainingData.filter { x => x.label==1 }
trainingData=trainingData.union(pdata)
// Run training algorithm to build the model
var numIterations = 10
//val svmAlg = new SVMWithSGD()
//svmAlg.optimizer.s

var svmAlg = new SVMWithSGD()
svmAlg.optimizer.
  setNumIterations(200).
  setRegParam(0.1).
  setUpdater(new L1Updater)
var model = svmAlg.run(trainingData)

//var model = SVMWithSGD.train(trainingData, numIterations)

//model.clearThreshold()

// Evaluate model on test instances and compute test error
var labelAndPreds = testData.map { point =>
  val prediction = model.predict(point.features)
    (prediction,point.label)
}
var testErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / testData.count()
var precision = labelAndPreds.filter(r=>r._1==r._2&&r._2==1).count.toDouble/(labelAndPreds.filter(r=>r._1==r._2&&r._2==1).count.toDouble+labelAndPreds.filter(r=>r._1==1&&r._2==0).count.toDouble)

var recall = labelAndPreds.filter(r=>r._1==r._2&&r._2==1).count.toDouble/(labelAndPreds.filter(r=>r._1==r._2&&r._2==1).count.toDouble+labelAndPreds.filter(r=>r._1==0&&r._2==1).count.toDouble)

//var precision = labelAndPreds.filter(r=>r._1==r._2&&r._2==1).count.toDouble/labelAndPreds.filter(r=>r._2==1).count
//var recall = labelAndPreds.filter(r=>r._1==r._2&&r._2==1).count.toDouble/(labelAndPreds.filter(r=>r._1==r._2&&r._2==1).count.toDouble+labelAndPreds.filter(r=>r._1==1&&r._2==1).count.toDouble)
var metrics = new BinaryClassificationMetrics(labelAndPreds)
var auROC = metrics.areaUnderROC()

println("AUC =" + auROC)
println("Test Error = " + testErr)
println("TP/(TP+FP)"+precision+"################################################")
println("TP/(TP+FN)"+recall+"################################################")
}