import $ivy.`net.sourceforge.owlapi:owlapi-distribution:4.5.6`
import $ivy.`net.sourceforge.owlapi:owlexplanation:2.0.0`
import $ivy.`org.semanticweb.elk:elk-owlapi:0.4.3`

import org.semanticweb.elk.owlapi.ElkReasonerFactory
import org.semanticweb.owl.explanation.api.ExplanationManager
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.parameters.Imports
import org.semanticweb.owlapi.model.{IRI, OWLAxiom}
import org.semanticweb.owlapi.util.{InferredAxiomGenerator, InferredEquivalentClassAxiomGenerator, InferredOntologyGenerator, InferredSubClassAxiomGenerator}

import scala.collection.JavaConverters._

/**
  * Print an explanation for each inferred subclass and equivalence axiom resulting from new axioms.
  * Run: `amm explain_ontology_inference_diff.sc ont.owl axioms.owl`
  * You may need to set a reasonable JVM heap size: `export JAVA_OPTS=-Xmx10G`
  * @param ontology path to starting ontology
  * @param axioms path to ontology of new axioms (ideally without owl:imports statements)
  */

@main
def main(ontology: os.Path, axioms: os.Path): Unit = {
  val manager = OWLManager.createOWLOntologyManager()
  val fac = manager.getOWLDataFactory
  val ont = manager.loadOntology(IRI.create(ontology.toIO))
  val newAxiomsOnt = manager.loadOntology(IRI.create(axioms.toIO))
  val allAxioms = (newAxiomsOnt.getAxioms(Imports.INCLUDED).asScala ++ ont.getAxioms(Imports.INCLUDED).asScala).map(_.getAxiomWithoutAnnotations)
  val allAxiomsOnt = manager.createOntology(allAxioms.asJava)
  val reasonerPrev = new ElkReasonerFactory().createReasoner(ont)
  val axiomGenerators: List[InferredAxiomGenerator[_ <: OWLAxiom]] = List(new InferredSubClassAxiomGenerator(), new InferredEquivalentClassAxiomGenerator())
  val generatorPrev = new InferredOntologyGenerator(reasonerPrev, axiomGenerators.asJava)
  val previousInferencesOnt = manager.createOntology()
  generatorPrev.fillOntology(fac, previousInferencesOnt)
  reasonerPrev.dispose()
  val reasonerNew = new ElkReasonerFactory().createReasoner(allAxiomsOnt)
  val generatorNew = new InferredOntologyGenerator(reasonerNew, axiomGenerators.asJava)
  val newInferencesOnt = manager.createOntology()
  generatorNew.fillOntology(fac, newInferencesOnt)
  reasonerNew.dispose()
  val newInferences = newInferencesOnt.getAxioms().asScala.diff(allAxioms).diff(previousInferencesOnt.getAxioms().asScala).toSet
  println(s"Inferred axioms: ${newInferences.size}")
  val explainerFactory = ExplanationManager.createExplanationGeneratorFactory(new ElkReasonerFactory())
  val explainer = explainerFactory.createExplanationGenerator(allAxiomsOnt)
  for {
    inference <- newInferences
    explanation <- explainer.getExplanations(inference, 1).asScala
  } println(s"$explanation\n\n")
}
