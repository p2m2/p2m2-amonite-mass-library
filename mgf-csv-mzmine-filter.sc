#!/usr/bin/env amm
// Ammonite 2.5.2, scala 2.13

/*
import $ivy.`com.github.p2m2::discovery:v0.3.2`
import inrae.semantic_web.rdf.{IRI, URI}
import inrae.semantic_web.{SWDiscovery, StatementConfiguration}

import java.io.{BufferedWriter, File, FileWriter, StringWriter}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

import java.io.File;

import $file.Common
*/

import scala.io.Source
import java.io._

case class IonMGF(
    params:Map[String,Option[String]],
    originalMgfDescription : Seq[String]
    )

def getIndexLine(s: Seq[String],matchString : String) : Seq[Int] = 
    s
    .zipWithIndex
    .filter { case (line:String,index:Int) => line.contains(matchString) }
    .map( _._2 ).toSeq

def getParam(s: Seq[String],filterString:String): Option[String] = 
    s.filter( _.startsWith(filterString) )
    .map(_.split("=").last)
    .headOption

def getIonMGFDescription(s: Seq[String],start:Int,end:Int) : IonMGF = {
    val seqLocal = s.slice(start+1,end)
    val featureId : Option[String] = getParam(seqLocal,"FEATURE_ID")
    val pepmass : Option[String] = getParam(seqLocal,"PEPMASS")
    val rtInSeconds : Option[String] = getParam(seqLocal,"RTINSECONDS")
    val msLevel : Option[String] = getParam(seqLocal,"MSLEVEL")

    IonMGF(Map( 
        "FEATURE_ID" -> featureId,
        "PEPMASS" -> pepmass,
        "RTINSECONDS" -> rtInSeconds,
        "MSLEVEL" -> msLevel ),seqLocal)
}


@main
def main(mgfFile: String,csvFile:String, mode:String, rtThreshold: Double =50.0) : Unit = { 
    println("=================================")
    println(s"mgfile:$mgfFile")
    println(s"csvfile:$csvFile")
    println(s"mode:$mode (should POS or NEG)")
    println(s"threshold RT:$rtThreshold ms")
    println("=================================")

    if ( ! Seq("POS","NEG").contains(mode) ) {
        System.err.println("mode arg should be 'POS' or 'NEG'")
        System.exit(-1)
    }

    val mgfLines : Seq[String] = Source.fromFile(mgfFile).getLines().toSeq

    val indexesBeginIonsDescription : Seq[Int] = getIndexLine(mgfLines,"BEGIN")
    val indexesEndIonsDescription = getIndexLine(mgfLines,"END")

    if (indexesBeginIonsDescription.size != indexesEndIonsDescription.size) {
        System.err.println("Bad format file number BEGIN=${indexesBeginIonsDescription.size}, number END=${indexesEndIonsDescription.size}")
        System.exit(-1)
    }

    val nIons : Int = indexesBeginIonsDescription.size
    println(s"number of Ions in $mgfFile => $nIons")

    val ionsDescription : Seq[IonMGF] = 
        0.until(nIons).map( ionIndex => {
            getIonMGFDescription(
                mgfLines,
                indexesBeginIonsDescription(ionIndex),
                indexesEndIonsDescription(ionIndex)) } )

    println(s"size ionsDescription => ${ionsDescription.size}")

    println("======= reading CSV Header file =======")
    
    val header : Seq[String] = Source.fromFile(csvFile).getLines().toSeq.head.split("[,;\t]")

    val filterChar = if (mode == "POS") "p" else "n"
    
    val ionsOfInterest : Seq[String] = header.filter(_.startsWith(filterChar))
    
    println(s"Number of ions of interested (pick from CSV) : ${ionsOfInterest.size}")

    val bw = new BufferedWriter(new FileWriter(new File(s"Filter_th_${rtThreshold}_$mgfFile")))
    
    ionsOfInterest
        .map(
            id => id.split("_")
        )
        .filter(_.size == 3)
        .map(
           r => (r(0).replace(filterChar,""),r(1),r(2)) // feature_id, mz, RT
        )
        .map ( // For all ions 
            feature => {
                
                ionsDescription
                //First Condition FEATURE_ID should equal !
                    .filter(
                        ionMGF => ionMGF.params.getOrElse("FEATURE_ID",None) match {
                            case Some(id) => id == feature._1
                            case _ => false
                    })
                //Second condition PEPMASS should be nearly equal
                    .filter(
                        ionMGF => ionMGF.params.getOrElse("PEPMASS",None) match {
                            case Some(mz) => (mz.toDouble - feature._2.toDouble) < 0.01 
                            case _ => false
                    })
                //Thirs condition RT should be nearly equal
                    .filter(
                        ionMGF => ionMGF.params.getOrElse("RTINSECONDS",None) match {
                            case Some(rt) => ((rt.toDouble * 60.0) - (feature._3.toDouble * 60 * 60)) < rtThreshold 
                            case _ => false
                    })
            }
        )
        .filter(_.size>0)
        .flatten
        .foreach(
            ion => {
                bw.write("BEGIN IONS\n")
                ion.originalMgfDescription.foreach(
                    line => bw.write(s"$line\n")
                )
                bw.write("END IONS\n\n")
            }
        )
        bw.close()   
}
