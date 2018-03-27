import java.sql.*;

public class BiologicalEntity {
// Class to manage TEXT.BIOLOGICAL_ENTITY records.
// The class is used to generate XML files for species according to the schema proposed by Dr. Hong Cui (University of Arizona) 
// https://github.com/biosemantics/schemas/blob/master/semanticMarkupOutput.xsd 	
    	
	Integer   id;
	Integer   taxonDescriptionId;
	Integer   lineNumber;
	Integer   sequence;
	String    name;
	Integer   biologicalEntityTypeId;
	String    constraint;
	String    constraintId;
	String    geographicalConstraint;
	String    parallelismConstraint;
	String    taxonConstraint;
	Integer   inBrackets;
	String    alterName;
	String    nameOriginal;
	String    ontologyId;
	String    provenance;
	String    notes;
	String    pos;
	String    gender;
	String    number;
	String    knowledgeType;
	String    constraintPreposition;
	Timestamp lastModificationDatetime;
	String    otherConstraint;
	String    relation;
	String    verb;
	String    verbString;
	String    constraintConjunction;
	
	public BiologicalEntity(Integer pId, Integer pTaxonDescriptionId, Integer pLineNumber, Integer pSequence, String pName, 
			Integer pBiologicalEntityTypeId, String pConstraint, String pConstraintId, String pGeographicalConstraint,
	        String pParallelismConstraint, String pTaxonConstraint, Integer pInBrackets, String pAlterName, String pNameOriginal,
	        String  pOntologyId, String  pProvenance, String  pNotes, String  pPos, String  pGender, String  pNumber,
	        String  pKnowledgeType, String  pConstraintPreposition, Timestamp pLastModificationDatetime, String    pOtherConstraint,
	        String    pRelation, String    pVerb, String    pVerbString, String    pConstraintConjunction) {
		// TODO Auto-generated constructor stub
		
		id = pId;
		taxonDescriptionId = pTaxonDescriptionId;
		lineNumber =pLineNumber;
		sequence =pSequence;
		name = pName;
		biologicalEntityTypeId = pBiologicalEntityTypeId;
		constraint = pConstraint;
		constraintId = pConstraintId;
		geographicalConstraint = pGeographicalConstraint;
		parallelismConstraint = pParallelismConstraint;
		taxonConstraint = pTaxonConstraint;
		inBrackets = pInBrackets;
		alterName = pAlterName;
		nameOriginal = pNameOriginal;
		ontologyId = pOntologyId;
		provenance= pProvenance;
		notes = pNotes;
		pos = pPos;
		gender = pGender;
		number = pNumber;
		knowledgeType = pKnowledgeType;
		constraintPreposition = pConstraintPreposition;
		lastModificationDatetime = pLastModificationDatetime;
		otherConstraint = pOtherConstraint;
		relation = pRelation;
		verb = pVerb;
		verbString = pVerbString;
		constraintConjunction = pConstraintConjunction;
	}
	
	
	public void setId (Integer pId) {
		this.id = pId;
	}	
		
	public void setTaxonDescriptionId (Integer pTaxonDescriptionId) {
		this.taxonDescriptionId = pTaxonDescriptionId;
	}
		
	public void setLineNumber (Integer pLineNumber) {
		this.lineNumber = pLineNumber;
	}
	
	public void setSequence (Integer pSequence) {
		this.sequence = pSequence;
	}
	
	public void setName (String pName){
		this.name = pName;
	}
	
	public void setBiologicalEntityTypeId (Integer pBiologicalEntityTypeId) {
		this.biologicalEntityTypeId = pBiologicalEntityTypeId;
	}	
		
	public void setConstraint (String pConstraint){
			this.constraint = pConstraint;
		}
		
	public void setConstraintId  (String pConstraintId){
		this.constraintId = pConstraintId;
	}
	
	public void setGeographicalConstraint  (String pGeographicalConstraint){
		this.geographicalConstraint = pGeographicalConstraint;
	}
	
	public void setparallelismConstraint  (String pparallelismConstraint){
		this.parallelismConstraint = pparallelismConstraint;
	}
	
	public void setTaxonConstraint(String pTaxonConstraint){
		this.taxonConstraint = pTaxonConstraint;
	}
	
	public void setInBrackets (Integer pInBrackets) {
		this.inBrackets = pInBrackets;
	}
		
	public void setAlterName  (String pAlterName){
			this.alterName = pAlterName;
		}
		
	public void setNameOriginal  (String pNameOriginal){
		this.nameOriginal = pNameOriginal;
	}
	
	public void setOntologyId  (String pOntologyId){
		this.ontologyId = pOntologyId;
	}
	
	public void setProvenance (String pProvenance){
		this.provenance = pProvenance;
	}
	
	public void setNotes (String pNotes){
		this.notes =pNotes;
	}
	
	public void setPos (String pPos){
		this.pos = pPos;
	}
	
	public void setGender (String pGender){
		this.gender = pGender;
	}
	
	public void setNumber (String pNumber){
		this.number = pNumber;
	}
	
	public void setKnowledgeType (String pKnowledgeType){
		this.knowledgeType = pKnowledgeType;
	}
	
	public void setConstraintPreposition (String pConstraintPreposition){
		this.constraintPreposition = pConstraintPreposition;
	}
	
	public void setLastModificationDatetime (Timestamp pLastModificationDatetime){
		this.lastModificationDatetime = pLastModificationDatetime;
	}
	
	public void setOtherConstraint (String pOtherConstraint){
		this.otherConstraint = pOtherConstraint;
	}
	
	public void setRelation (String pRelation){
		this.relation = pRelation;
	}
	
	public void setVerb (String pVerb){
		this.verb = pVerb;
	}
	
	public void setVerbString (String pVerbString){
		this.verbString = pVerbString;
	}
	
	public void setConstraintConjunction (String pConstraintConjunction){
		this.constraintConjunction = pConstraintConjunction;
	}
	



	public Integer getId () {
		return(this.id);
	}	
		
	public Integer getTaxonDescriptionId () {
		return(this.taxonDescriptionId);
	}
		
	public Integer getLineNumber () {
		return(this.lineNumber);
	}
	
	public Integer getSequence () {
		return(this.sequence);
	}
	
	public String getName (){
		return(this.name);
	}
	
	public Integer getBiologicalEntityTypeId () {
		return(this.biologicalEntityTypeId);
	}	
		
	public String getConstraint (){
		return(this.constraint);
		}
		
	public String getConstraintId  (){
		return(this.constraintId);
	}
	
	public String getGeographicalConstraint  (){
		return(this.geographicalConstraint);
	}
	
	public String getparallelismConstraint  (){
		return(this.parallelismConstraint);
	}
	
	public String getTaxonConstraint(){
		return(this.taxonConstraint);
	}
	
	public Integer getInBrackets () {
		return(this.inBrackets);
	}
		
	public String getAlterName  (){
		return(this.alterName);
		}
		
	public String getNameOriginal  (){
		return(this.nameOriginal);
	}
	
	public String getOntologyId  (){
		return(this.ontologyId);
	}
	
	public String getProvenance (){
		return(this.provenance);
	}
	
	public String getNotes (){
		return(this.notes);
	}
	
	public String getPos (){
		return(this.pos);
	}
	
	public String getGender (){
		return(this.gender);
	}
	
	public String getNumber (){
		return(this.number);
	}
	
	public String getKnowledgeType (){
		return(this.knowledgeType);
	}
	
	public String getConstraintPreposition (){
		return(this.constraintPreposition);
	}
	
	public Timestamp getLastModificationDatetime (){
		return(this.lastModificationDatetime);
	}
	
	public String getOtherConstraint (){
		return(this.otherConstraint);
	}
	
	public String getRelation (){
		return(this.relation);
	}
	
	public String getVerb (){
		return(this.verb);
	}
	
	public String getVerbString (){
		return(this.verbString);
	}
	
	public String getConstraintConjunction (){
		return(this.constraintConjunction);
	}
	
	
}



	
	
	
	
	
	
	
	
	
	
	
	









