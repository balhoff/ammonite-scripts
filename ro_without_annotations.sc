import java.io.File

import $ivy.`net.sourceforge.owlapi:owlapi-distribution:4.5.6`
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl
import org.semanticweb.owlapi.model.parameters.Imports
import org.semanticweb.owlapi.model._
import org.semanticweb.owlapi.util.{AnnotationValueShortFormProvider, ShortFormProvider}

import scala.collection.JavaConverters._


def relevantAxioms(ont: OWLOntology, includeImports: Imports): Set[OWLAxiom] = (ont.getRBoxAxioms(includeImports).asScala ++
  ont.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN, includeImports).asScala ++
  ont.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE, includeImports).asScala).toSet[OWLAxiom].map(_.getAxiomWithoutAnnotations)

val OBO = "http://purl.obolibrary.org/obo"
val manager = OWLManager.createOWLOntologyManager()
val ro = manager.loadOntologyFromOntologyDocument(IRI.create(s"$OBO/ro.owl"))
val roAxioms = relevantAxioms(ro, Imports.INCLUDED)
val newRO = manager.createOntology(roAxioms.asJava)
manager.saveOntology(newRO, IRI.create(new File("ro-no-annotations.owl")))
