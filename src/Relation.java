
public class Relation {

	Integer id;
	Integer taxonDescriptionId;
	Integer lineNumber;
	Integer sequence;
	String name;
	String alterName;
	String modifier;
	Integer vFrom;
	Integer vTo;
	String geographicalConstraint;
	Integer inBrackets;
	String organConstraint;
	String parallelismConstraint;
	String taxonConstraint;
	String ontologyId;
	String provenance;
	String notes;
	Integer negation;
	
	
	public Relation(Integer pid, Integer ptaxonDescriptionId, Integer plineNumber, Integer psequence, String pname, 
			      String palterName, String pmodifier, Integer pvFrom, Integer pvTo, String pgeographicalConstraint,
	              Integer pinBrackets, String porganConstraint, String pparallelismConstraint, String ptaxonConstraint,
	              String pontologyId, String pprovenance, String pnotes, Integer pnegation) {
		// TODO Auto-generated constructor stub
	
		id = pid;
		taxonDescriptionId = ptaxonDescriptionId;
		lineNumber = plineNumber;
		sequence = psequence;
		name = pname;
		alterName = palterName;
		modifier = pmodifier;
		vFrom = pvFrom;
		vTo = pvTo;
		geographicalConstraint = pgeographicalConstraint;
		inBrackets = pinBrackets;
		organConstraint = porganConstraint;
		parallelismConstraint = pparallelismConstraint;
		taxonConstraint = ptaxonConstraint;
		ontologyId = pontologyId;
		provenance = pprovenance;
		notes = pnotes;
	    negation = pnegation;
	}
	
	public void setId(Integer pid) {
		this.id =pid;
	}
	
	
	public void setTaxonDescriptionId(Integer ptaxonDescriptionId) {
		this.taxonDescriptionId =ptaxonDescriptionId;
	}
	
	public void setLineNumber(Integer plineNumber) {
		this.lineNumber = plineNumber;
	}
	
	public void setSequence(Integer psequence) {
		this.sequence =psequence;
	}
	
	public void setName(String pname) {
		this.name =pname;
	}
	
	public void setAlterName(String palterName) {
		this.alterName =palterName;
	}
	
	public void setModifier(String pmodifier) {
		this.modifier =pmodifier;
	}
	
	public void setVFrom(Integer pvFrom) {
		this.vFrom =pvFrom;
	}
	
	public void setVTo(Integer pvTo) {
		this.vTo =pvTo;
	}
	
	public void setGeographicalConstraint(String pgeographicalConstraint) {
		this.geographicalConstraint =pgeographicalConstraint;
	}
	
	public void setInBrackets(Integer pinBrackets) {
		this.inBrackets =pinBrackets;
	}
	
	public void setOrganConstraint(String porganConstraint) {
		this.organConstraint =porganConstraint;
	}
	
	public void setParallelismConstraint(String pparallelismConstraint) {
		this.parallelismConstraint =pparallelismConstraint;
	}
	
	public void setTaxonConstraint(String ptaxonConstraint) {
		this.taxonConstraint =ptaxonConstraint;
	}
	
	public void setOntologyId(String pontologyId) {
		this.ontologyId =pontologyId;
	}
	
	public void setProvenance(String pprovenance) {
		this.provenance =pprovenance;
	}
	
	public void setNotes(String pnotes) {
		this.notes =pnotes;
	}
	
	public void setNegation(Integer pnegation) {
		this.negation = pnegation;
	}
	
	/* GETs */
	
	public Integer getId() {
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
	
	public String getName () {
		return(this.name);
	}
	
	public String getAlterName () {
		return(this.alterName);
	}
	
	public String getModifier () {
		return(this.modifier);
	}
	
	public Integer getVFrom () {
		return(this.vFrom);
	}
	
	public Integer getVTo () {
		return(this.vTo);
	}
	
	public String getGeographicalConstraint () {
		return(this.geographicalConstraint);
	}
	
	public Integer getinBrackets () {
		return(this.inBrackets);
	}
	
	public String getOrganConstraint () {
		return(this.organConstraint);
	} 
	
	public String getParallelismConstraint () {
		return(this.parallelismConstraint);
	} 
	
	public String getTaxonConstraint () {
		return(this.taxonConstraint);
	}
	
	public String getOntologyId () {
		return(this.ontologyId);
	}
	
	public String getProvenance () {
		return(this.provenance);
	}
	
	public String getNotes () {
		return(this.notes);
	} 
	
	public Integer getNegation () {
		return(this.negation);
	} 
	

}
