import $ivy.`net.sourceforge.owlapi:owlapi-distribution:4.5.6`
import $ivy.`org.phenoscape::scowl:1.3.1`

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.expression.OWLEntityChecker
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxInlineAxiomParser
import org.semanticweb.owlapi.model._
import org.phenoscape.scowl._

val checker = new OWLEntityChecker {

  override def getOWLClass(s: String): OWLClass = ??? //Class(s)

  override def getOWLObjectProperty(s: String): OWLObjectProperty = ObjectProperty(s)

  override def getOWLDataProperty(s: String): OWLDataProperty = ???

  override def getOWLIndividual(s: String): OWLNamedIndividual = ???

  override def getOWLDatatype(s: String): OWLDatatype = ???

  override def getOWLAnnotationProperty(s: String): OWLAnnotationProperty = ???
}

private lazy val axiomParser = new ManchesterOWLSyntaxInlineAxiomParser(OWLManager.getOWLDataFactory, checker)