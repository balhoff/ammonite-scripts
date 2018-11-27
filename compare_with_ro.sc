import $ivy.`net.sourceforge.owlapi:owlapi-distribution:4.5.6`

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl
import org.semanticweb.owlapi.model.parameters.Imports
import org.semanticweb.owlapi.model._
import org.semanticweb.owlapi.util.{AnnotationValueShortFormProvider, ShortFormProvider}

import scala.collection.JavaConverters._


/*
Compare property axioms in GO to those in RO, for review.
 */

val manager = OWLManager.createOWLOntologyManager()

private class MarkdownLinkShortFormProvider(val labelProvider: ShortFormProvider) extends ShortFormProvider {
  override def getShortForm(entity: OWLEntity) = {
    val label = labelProvider.getShortForm(entity)
    "[" + label + "](" + entity.getIRI.toString + ")"
  }

  override def dispose(): Unit = {
  }
}

val labelProvider = new AnnotationValueShortFormProvider(List(OWLManager.getOWLDataFactory.getRDFSLabel).asJava, Map.empty[OWLAnnotationProperty, java.util.List[String]].asJava, manager)
private val linkProvider = new MarkdownLinkShortFormProvider(labelProvider)
val axiomRenderer = new ManchesterOWLSyntaxOWLObjectRendererImpl
axiomRenderer.setShortFormProvider(linkProvider)

def relevantAxioms(ont: OWLOntology, includeImports: Imports): Set[OWLAxiom] = (ont.getRBoxAxioms(includeImports).asScala ++
  ont.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN, includeImports).asScala ++
  ont.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE, includeImports).asScala).toSet[OWLAxiom].map(_.getAxiomWithoutAnnotations)

val OBO = "http://purl.obolibrary.org/obo"
val ro = manager.loadOntologyFromOntologyDocument(IRI.create(s"$OBO/ro.owl"))
val go = manager.loadOntologyFromOntologyDocument(IRI.create(s"$OBO/go/extensions/go-plus.owl"))
val roAxioms = relevantAxioms(ro, Imports.INCLUDED)
for {
  ont <- go.getImportsClosure.asScala
} {
  val ontAxioms = relevantAxioms(ont, Imports.EXCLUDED)
  println(ont.getOntologyID)
  println()
  val extra = ontAxioms -- roAxioms
  extra.foreach(ax => println(axiomRenderer.render(ax)))
}
