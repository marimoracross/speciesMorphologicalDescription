import java.sql.*;

public class Character {

	Integer id;
	Integer biologicalEntityId;
	Integer taxonDescriptionId;
	Integer lineNumber;
	Integer sequence;
	String name;
	String modifier;
	String value;
	String charType;
	String from;
	Integer fromInclusive;
	String fromUnit;
	String to;
	Integer toInclusive;
	String toUnit;
	String type;
	String unit;
	Integer upperRestricted;
	String constraint;
	String constraintId;
	String geographicalConstraint;
	String organConstraint;
	String otherConstraint;
	String parallelismConstraint;
	String taxonConstraint;
	Integer inBrackets;
	String ontologyId;
	String provenance;
	String notes;
	Integer isModifier;
	Integer tokenTreeId;
	String constraintPreposition;
	Timestamp lastModificationDatetime;
	String constraintConjunction;
	String verb;
	String verbString;
	
	
	public Character(Integer pid, Integer pbiologicalEntityId, Integer ptaxonDescriptionId, Integer plineNumber, Integer psequence,
					String pname, String pmodifier, String pvalue, String pcharType, String pfrom, Integer pfromInclusive,
					String pfromUnit, String pto, Integer ptoInclusive, String ptoUnit, String ptype, String punit, Integer pupperRestricted,
					String pconstraint, String pconstraintId, String pgeographicalConstraint, String porganConstraint, String potherConstraint,
					String pparallelismConstraint, String ptaxonConstraint, Integer pinBrackets, String pontologyId, String pprovenance,
					String pnotes, Integer pisModifier, Integer ptokenTreeId, String pconstraintPreposition, Timestamp plastModificationDatetime,
					String pconstraintConjunction, String pverb, String pverbString) {
		// TODO Auto-generated constructor stub
		
		id =pid;
		biologicalEntityId = pbiologicalEntityId;
		taxonDescriptionId = ptaxonDescriptionId;
		lineNumber = plineNumber;
		sequence = psequence;
		name = pname;
		modifier = pmodifier;
		value = pvalue; 
		charType = pcharType;
		from = pfrom;
		fromInclusive = pfromInclusive;
		fromUnit = pfromUnit;
		to = pto;
		toInclusive = ptoInclusive;
		toUnit = ptoUnit;
		type = ptype;
		unit = punit;
		upperRestricted = pupperRestricted;
		constraint = pconstraint;
		constraintId = pconstraintId;
		geographicalConstraint = pgeographicalConstraint;
		organConstraint = porganConstraint;
		otherConstraint = potherConstraint;
		parallelismConstraint = pparallelismConstraint;
		taxonConstraint = ptaxonConstraint;
		inBrackets = pinBrackets;
		ontologyId = pontologyId;
		provenance = pprovenance;
		notes = pnotes;
		isModifier = pisModifier;
		tokenTreeId = ptokenTreeId;
		constraintPreposition = pconstraintPreposition;
		lastModificationDatetime = plastModificationDatetime;
		constraintConjunction = pconstraintConjunction;		
		verb = pverb;
		verbString = pverbString;
		
	}

	
	public void setId(Integer pid) {
		this.id =pid;
	}
	
	public void setBiologicalEntityId (Integer pBiologicalEntityId) {
		this.biologicalEntityId = pBiologicalEntityId;
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
	
	public void setName (String pName) {
		this.name = pName;
	}
	
	public void setModifier(String pModifier) {
		this.modifier = pModifier;
	}
	
	public void setValue(String pValue) {
		this.value = pValue;
	}
	
	public void setCharType(String pCharType) {
		this.charType = pCharType;
	}
	
	public void setFrom(String pFrom) {
		this.from = pFrom;
	}
	
	public void setFromInclusive (Integer pFromInclusive) {
		this.fromInclusive = pFromInclusive;
	}
	
	public void setFromUnit(String pFromUnit) {
		this.fromUnit = pFromUnit;
	}
	
	public void setTo(String pTo) {
		this.to = pTo;
	}
	
	public void setToInclusive (Integer pToInclusive) {
		this.toInclusive = pToInclusive;
	}
	
	public void setToUnit(String pToUnit) {
		this.toUnit = pToUnit;
	}
	
	public void setType(String pType) {
		this.type = pType;
	}
	
	public void setUnit(String pUnit) {
		this.unit = pUnit;
	}
	
	public void setUpperRestricted(Integer pUpperRestricted) {
		this.upperRestricted = pUpperRestricted;
	}
	
	public void setConstraint(String pConstraint) {
		this.constraint = pConstraint;
	}
	
	public void setConstraintId(String pConstraintId) {
		this.constraintId = pConstraintId;
	}
	
	public void setGeographicalConstraint(String pGeographicalConstraint) {
		this.geographicalConstraint = pGeographicalConstraint;
	}
	
	public void setOrganConstraint(String pOrganConstrain) {
		this.organConstraint = pOrganConstrain;
	}
	
	public void setOtherConstraint(String pOtherConstraint) {
		this.otherConstraint = pOtherConstraint;
	}
	
	public void setParallelismConstraint(String pParallelismConstraint) {
		this.parallelismConstraint = pParallelismConstraint;
	}
	
	public void setTaxonConstraint(String pTaxonConstraint) {
		this.taxonConstraint = pTaxonConstraint;
	}
	
	public void setInBrackets (Integer pInBrackets) {
		this.inBrackets = pInBrackets;
	}
	
	public void setOntologyId(String pOntologyId) {
		this.ontologyId = pOntologyId;
	}
	
	public void setProvenance(String pProvenance) {
		this.provenance = pProvenance;
	}
	
	public void setNotes(String pNotes) {
		this.notes = pNotes;
	}
	
	public void setIsModifier(Integer pIsModifier) {
		this.isModifier = pIsModifier;
	}
	
	public void setTokenTreeId (Integer pTokenTreeId) {
		this.tokenTreeId = pTokenTreeId;
	}
	
	public void setConstraintPreposition(String pConstraintPreposition) {
		this.constraintPreposition = pConstraintPreposition;
	}
	
	public void setLastModificationDatetime(Timestamp pLastModificationDatetime) {
		this.lastModificationDatetime = pLastModificationDatetime;
	}
	
	public void setConstraintConjunction(String pConstraintConjunction) {
		this.constraintConjunction = pConstraintConjunction;
	}

	public void setVerb(String pVerb) {
		this.verb = pVerb;
	}
	
	public void setVerbString(String pVerbString) {
		this.verbString = pVerbString;
	}
	
	
	
	// Gets

	public Integer getId() {
		return(this.id);
	}
	
	public Integer getBiologicalEntityId () {
		return(this.biologicalEntityId);
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
	
	public String getName () {
		return(this.name);
	}
	
	public String getModifier() {
		return(this.modifier );
	}
	
	public String getValue() {
		return(this.value);
	}
	
	public String getCharType() {
		return(this.charType);
	}
	
	public String getFrom() {
		return(this.from);
	}
	
	public Integer getFromInclusive () {
		return(this.fromInclusive );
	}
	
	public String getFromUnit() {
		return(this.fromUnit);
	}
	
	public String getTo() {
		return(this.to);
	}
	
	public Integer getToInclusive () {
		return(this.toInclusive);
	}
	
	public String getToUnit() {
		return(this.toUnit);
	}
	
	public String getType() {
		return(this.type );
	}
	
	public String getUnit() {
		return(this.unit);
	}
	
	public Integer getUpperRestricted() {
		return(this.upperRestricted);
	}
	
	public String getConstraint() {
		return(this.constraint);
	}
	
	public String getConstraintId() {
		return(this.constraintId);
	}
	
	public String getGeographicalConstraint() {
		return(this.geographicalConstraint);
	}
	
	public String getOrganConstraint() {
		return(this.organConstraint );
	}
	
	public String getOtherConstraint() {
		return(this.otherConstraint);
	}
	
	public String getParallelismConstraint() {
		return(this.parallelismConstraint);
	}
	
	public String getTaxonConstraint() {
		return(this.taxonConstraint);
	}
	
	public Integer getInBrackets () {
		return(this.inBrackets );
	}
	
	public String getOntologyId() {
		return(this.ontologyId );
	}
	
	public String getProvenance() {
		return(this.provenance );
	}
	
	public String getNotes() {
		return(this.notes);
	}
	
	public Integer getIsModifier() {
		return(this.isModifier);
	}
	
	public Integer getTokenTreeId () {
		return(this.tokenTreeId);
	}
	
	public String getConstraintPreposition() {
		return(this.constraintPreposition);
	}
	
	public Timestamp getLastModificationDatetime() {
		return(this.lastModificationDatetime);
	}
	
	public String getConstraintConjunction() {
		return(this.constraintConjunction );
	}
	
	public String getVerb() {
		return(this.verb );
	}
	
	public String getVerbString() {
		return(this.verbString );
	}
	
	
}









