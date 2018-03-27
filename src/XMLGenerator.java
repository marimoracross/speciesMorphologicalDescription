

import java.io.FileWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import java.sql.*;
import java.util.Date;



public class XMLGenerator {

	
	public static void prueba ()
	 { 
		   
	    try
	    {  
	    	String fileName = "/home/mmora/MyXML.xml";
	        XMLOutputFactory xof = XMLOutputFactory.newInstance();
	        XMLStreamWriter xtw = null;
	        xtw = xof.createXMLStreamWriter(new FileWriter(fileName));
	        xtw.writeStartDocument("utf-8", "1.0");
	        xtw.setPrefix("html", "http://www.w3.org/TR/REC-html40");
	        xtw.writeStartElement("http://www.w3.org/TR/REC-html40", "html");
	        xtw.writeNamespace("html", "http://www.w3.org/TR/REC-html40");
	        xtw.writeStartElement("http://www.w3.org/TR/REC-html40", "head");
	        xtw.writeStartElement("http://www.w3.org/TR/REC-html40", "title");
	        xtw.writeCharacters("character");
	        xtw.writeEndElement();
	        xtw.writeEndElement();
	        xtw.writeStartElement("http://www.w3.org/TR/REC-html40", "body");
	        xtw.writeStartElement("http://www.w3.org/TR/REC-html40", "p");
	        xtw.writeCharacters("another character");
	        xtw.writeStartElement("http://www.w3.org/TR/REC-html40", "a");
	        xtw.writeAttribute("href", "http://www.java2s.com");
	        xtw.writeCharacters("here");
	        xtw.writeEndElement();
	        xtw.writeEndElement();
	        xtw.writeEndElement();
	        xtw.writeEndElement();
	        xtw.writeEndDocument();
	        xtw.flush();
	        xtw.close();
	        System.out.println("Done"); 
	 	  
	     }
	     catch(Exception e)
	     {
	         e.printStackTrace();
	     }
	 }   
	    
	    
	    public static void generateXML (Connection conn, Integer taxonDescriptionId,  String pFileName, Integer bookId)
		 { /* - Description: create the XML file for a taxon_description_id using the schema available at:
		    *                http://raw.githubusercontent.com/biosemantics/schemas/0.0.1/semanticMarkupOutput.xsd
	  	   *                
		       * - Revision History:
		       *     1/10/2015 - Maria Aux. Mora
		       *     
		       * - Arguments (input / output):
		       *    conn               : database opened connection.
		       *    taxonDescriptionId :  taxon_description_id that will be processed. 
		       *    pFileName		   :  file name including the directory name and path.
		       *    p_book_id          : Book identifier. 
		       *    
		       * - Return Values:
		       *     Conversions to present results using the standard:
		       *     
		       *     For BIOLOGICAL_ENTITY: 
		       *     - Structure id = "T"+ taxon_description_id + "L"+line_number (clause id)+ "S" + sequence (chunk id) + BIOLOGICAL_ENTITY.id
		       *     - If in_brackets = 1 => present true
		       *     - If Notes includes "init with preposition" => a new structure must be created because chunks that start with a prepositions
		       *                are not related with previous structure.             
		       *     - Relations are not processed.  
		       *     
		       *     For CHARACTER:                      
		       *     - If in_brackets = 1 => present true
		       *       
		  */
	    	 	
			BiologicalEntity structure;   
			Character character;
			Relation relation;
		   	String fileName = pFileName;
	        XMLOutputFactory xof = XMLOutputFactory.newInstance();
	        XMLStreamWriter xtw = null;
	        ResultSet rs, rsC, rsRelation; 
	        Boolean initWithPreposition = false;
	        String newStructure = null;         // For chunks that initiate with a preposition.
	        String newRelation = "";
			String taxonName;
			String theOriginalNameofMainStructure; 
			String bookDocumentation;
		    String theClause;
		    String tempString;
		    Date today= new Date();
			
		    try
		    {  
		   
		    taxonName = TextDatabase.getTaxonScientificName(conn, taxonDescriptionId).trim();
		    
		    /* Table TEXT.BIOLOGICAL_ENTITY structure: 
		     * 1- Integer  id;    				  		2- Integer   taxonDescriptionId;
		   		3- Integer   lineNumber;				4- Integer   sequence;
		   		5- String    name;						6- Integer   biologicalEntityTypeId;
		   		7- String    constraint;				8- String    constraintId;
		   		9- String    geographicalConstraint;	10-String    parallelismConstraint;
		   		11-String    taxonConstraint;			12-Integer   inBrackets;
		   		13-String    alterName;					14-String    nameOriginal;
		   		15-String    ontologyId					16-String    provenance;
		   		17-String    notes;						18-String    pos;
		   		19-String    gender;					20-String    number;
		   		21-String    knowledgeType;				22-String    constraintPreposition;
		   		23-Timestamp lastModificationDatetime;	24-String    otherConstraint;
		   		25-String    relation;					26-String    verb;
		   		27-String    verbString;				28-String    constraintConjunction;
		      */  	         
		    
		     xtw = xof.createXMLStreamWriter(new FileWriter(fileName.trim() + taxonName + ".xml"));
		     xtw.writeStartDocument();
		     
		     //meta
		     xtw.writeStartElement("treatment");
		     xtw.writeAttribute("xmlns", "http://www.github.com/inbio");
		     xtw.writeAttribute( "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		     xtw.writeAttribute( "xsi:schemaLocation","http://raw.githubusercontent.com/biosemantics/schemas/0.0.1/semanticMarkupOutput.xsd");
		     
		     bookDocumentation = TextDatabase.getBookDocumentation(conn, bookId );
		     
		     
		     xtw.writeStartElement("meta");
		     xtw.writeAttribute("source", bookDocumentation);
		     xtw.writeStartElement("processor");
		     xtw.writeStartElement("processed_by");
		     xtw.writeAttribute("operator", "Maria Mora");
		     xtw.writeAttribute("date", today.toString());
		     
		     xtw.writeStartElement("resource");
		     xtw.writeAttribute("type", "Ontology Terms Organizer (OTO) Glossary");
		     xtw.writeEndElement();
		     
		     xtw.writeStartElement("resource");
		     xtw.writeAttribute("type", "Plant Ontology");
		     xtw.writeEndElement();
		     
		     xtw.writeStartElement("resource");
		     xtw.writeAttribute("type", "Language analysis tool: FreeLing");
		     xtw.writeAttribute("version", "3.1");
		     
		     
		     xtw.writeEndElement();
		     xtw.writeEndElement();
		     xtw.writeEndElement();
		     xtw.writeEndElement();


		     String vTaxonRank = TextDatabase.getTaxonRank(conn, taxonDescriptionId); 
		     
		     // taxon_identification
		     xtw.writeStartElement("taxon_identification");
		     xtw.writeAttribute("taxon_name", taxonName);
		     xtw.writeAttribute("rank", vTaxonRank);
		     
		     xtw.writeEndElement();
		     
             String vDescription = TextDatabase.getTaxonDescription(conn, taxonDescriptionId);  
		           
		     xtw.writeStartElement("description");
		     xtw.writeAttribute("taxon_description", vDescription);
		     xtw.writeAttribute("description_type", "morphology");

		     
		     ResultSet taxonClauses = TextDatabase.selectClause(conn, null,  taxonDescriptionId , null, null, null);
		      
		     
		      while (taxonClauses.next()) {
		    		 
			     xtw.writeStartElement("statement");
			     xtw.writeAttribute("id", "T"+taxonClauses.getInt(2)+"L"+taxonClauses.getInt(8));
			     xtw.writeAttribute("text", taxonClauses.getString(5));
			  
			     
				 // Access Chunks in TEXT database. (taxon_descriptionid=null, line_number= null, sequence=null) process all chunks.    
				 rs = TextDatabase.selectBiologicalEntity(conn, taxonDescriptionId, taxonClauses.getInt(8));

	  
			      while (rs.next()) {
			           structure = new BiologicalEntity (rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getString(5), rs.getInt(6),
		    		   rs.getString(7),  rs.getString(8),  rs.getString(9), rs.getString(10),  rs.getString(11), rs.getInt(12), 
		    		   rs.getString(13),  rs.getString(14), rs.getString(15), rs.getString(16),rs.getString(17) , rs.getString(18),
		    		   rs.getString(19), rs.getString(20), rs.getString(21), rs.getString(22), rs.getTimestamp(23), rs.getString(24),
		    		   rs.getString(25), rs.getString(26), rs.getString(27), rs.getString(28));   
			           
		               xtw.writeStartElement("biological_entity");
		               xtw.writeAttribute("id", "T" + structure.getTaxonDescriptionId().toString() +"L" + structure.getLineNumber().toString() 
		        		                       + "S" + structure.getSequence().toString() +"-"+ structure.getId().toString());
		               xtw.writeAttribute("name",structure.getNameOriginal());
		               
		    	       if ((structure.getConstraint() != null)&& (!structure.getConstraint().trim().equals("")) && 
		    	    		   (!structure.getConstraint().trim().equals("|"))){
		    	        	 xtw.writeAttribute("constraint", processString(structure.getConstraint()));
		    	       }
		    	/*       if ((structure.getInBrackets() != null)&& (structure.getInBrackets() !=0)) {
		    	        	 xtw.writeAttribute("in_brackets", structure.getInBrackets().toString());
		    	       }
		    	*/     
		  
		    	       if ((structure.getConstraintPreposition() != null)&& (!structure.getConstraintPreposition().trim().equals("")) &&
		    	        		  (!structure.getConstraintPreposition().trim().trim().equals("|"))) {
		    	        	   xtw.writeAttribute("constraint_preposition", processString(structure.getConstraintPreposition()));
		    	          }
		   	           
		    	       if ((structure.getRelation() != null)&& (!structure.getRelation().trim().equals(""))  &&
		       	        		(!structure.getRelation().trim().equals("|"))) {
		    	    	     String prueba = processString(structure.getRelation());
		    	        	 xtw.writeAttribute("relation", processString(structure.getRelation()));
		                  }
		                 
		      
		    	       if ((structure.getOtherConstraint() != null)&& (!structure.getOtherConstraint().trim().equals("")) &&
		    	    		   (!structure.getOtherConstraint().trim().equals("|"))) {
		    	        	 xtw.writeAttribute("other_constraint", processString(structure.getOtherConstraint()));
		    	       }		           		          	       
		
		    	      		           		           
		    /*	       if ((structure.getVerb() != null)&& (!structure.getVerb().trim().equals("")) && 
		    	    		   (!structure.getVerb().trim().equals("|"))) {
		    	        	 xtw.writeAttribute("verb", processString(structure.getVerb()));
		    	       }		           		           
		    */	       	  
		    	       
		    	       if ((structure.getVerbString() != null)&& (!structure.getVerbString().trim().equals("")) &&
		    	    		   (!structure.getVerbString().trim().equals("|"))) {
		    	        	 xtw.writeAttribute("verb_string", processString(structure.getVerbString()));
		    	       }		           		           
		    	       if ((structure.getConstraintConjunction() != null)&& (!structure.getConstraintConjunction().trim().equals("")) &&
		    	    		   (!structure.getConstraintConjunction().trim().equals("|")) ) {
		    	        	 xtw.writeAttribute("constraint_conjunction", processString(structure.getConstraintConjunction()));
		    	       }	    
		    	       xtw.writeAttribute("type","structure"); 	       

		    	       
		    	       
	     	           rsC = TextDatabase.selectCharacter(conn, taxonDescriptionId, structure.getId());
		       	  /*  Table CHARACTER structure:	
		       	   * 	1 Integer id							2 Integer biologicalEntityId;
		    			3 Integer taxonDescriptionId;			4 Integer lineNumber;
		    			5 Integer sequence;						6 String name;
		    			7 String modifier;						8 String value;
		    			9 String charType;						10 String from;
		    			11 Integer fromInclusive				12 String fromUnit;
		    			13 String to;							14 Integer toInclusive;
		    			15 String toUnit;						16 String type;
		    			17 String unit;							18 Integer upperRestricted;
		    			19 String constraint;					20 String constraintId;
		    			21 String geographicalConstraint;		22 String organConstraint;
		    			23 String otherConstraint;				24 String parallelismConstraint;
		    			25 String taxonConstraint;				26 Integer inBrackets;
		    			27 String ontologyId					28 String provenance;
		    			29 String notes;						30 Integer isModifier;
		    	  		31 Integer tokenTreeId;					32 String constraintPreposition;
		    			33 Timestamp lastModificationDatetime;  34 String constraintConjunction;
		    	*/
				             
		           while (rsC.next())
		           {  /* for each Record */ 
			           character = new Character(rsC.getInt(1), rsC.getInt(2), rsC.getInt(3), rsC.getInt(4), rsC.getInt(5), rsC.getString(6), 
	        		            rsC.getString(7), rsC.getString(8), rsC.getString(9), rsC.getString(10), rsC.getInt(11), rsC.getString(12),
	        		            rsC.getString(13), rsC.getInt(14), rsC.getString(15), rsC.getString(16), rsC.getString(17), rsC.getInt(18),
	        		            rsC.getString(19), rsC.getString(20), rsC.getString(21), rsC.getString(22), rsC.getString(23), 
	        		            rsC.getString(24), rsC.getString(25), rsC.getInt(26), rsC.getString(27), rsC.getString(28), rsC.getString(29),
	        		            rsC.getInt(30), rsC.getInt(31), rsC.getString(32), rsC.getTimestamp(33), rsC.getString(34), rsC.getString(35), 
	        		            rsC.getString(36)); 

		           
		   /* 	       System.out.println( "id" + "T" + structure.getTaxonDescriptionId().toString() +"L" + structure.getLineNumber().toString() 
   		                       + "S" + character.getSequence().toString() );
*/
			           
			           	 xtw.writeStartElement("character ");

			           	 if ((character.getName() != null)&& (!character.getName().trim().equals("")) ){
		    	             xtw.writeAttribute("name", character.getName());
			           	 }
			           	 
			           	 if ((character.getValue() != null)&& (!character.getValue().trim().equals("")) ){
		    	             xtw.writeAttribute("value", character.getValue());
			           	 }
			           	
		    	       
		     	         if ((character.getCharType() != null)&& (!processCharType(character.getCharType()).trim().equals("")) ){
		    	        	 xtw.writeAttribute("char_type", character.getCharType());
		    	         }
		     	         
		    	         if ((character.getModifier() != null)&& (!character.getModifier().trim().equals("")) &&
		    	        		 (!character.getModifier().trim().equals("|"))){
		    	        	 xtw.writeAttribute("modifier", character.getModifier());
		    	         }
		    	         
		    	         if ((character.getTo() != null)&& (!character.getTo().equals("")) ){
		    	        	 
			    	         if ((character.getFrom() != null)&& (!character.getFrom().trim().equals("")) ){
			    	        	 xtw.writeAttribute("from", character.getFrom());
			    	         }
			    
		/*	    	         if ((character.getFromInclusive() != null)&& (!character.getFromInclusive().equals("")) ){
			    	        	 xtw.writeAttribute("from_inclusive", character.getFromInclusive().toString());
			    	         }	    	         
			    */	         if ((character.getFromUnit() != null)&& (!character.getFromUnit().trim().equals("")) ){
			    	        	 xtw.writeAttribute("from_unit", character.getFromUnit());
			    	         }		    	         

		    	        	 
		    	        	 xtw.writeAttribute("to", character.getTo());
			    	         if ((character.getToUnit() != null)&& (!character.getToUnit().equals("")) ){
			    	        	 xtw.writeAttribute("to_unit", character.getToUnit());
			    	         }
			    	         
			    	    } else {
			    	        if ((character.getFromUnit() != null)&& (!character.getFromUnit().trim().equals("")) ){
			    	        	 xtw.writeAttribute("unit", character.getFromUnit());
			    	         }		
			    	    }
			    	    	
		    	         
		    	      /*   if ((character.getToInclusive() != null)&& (!character.getToInclusive().equals("")) ){
		    	        	 xtw.writeAttribute("to inclusive", character.getToInclusive().toString());
		    	         }	    	         
		    	       */
		    	         
		    	         if ((character.getConstraint() != null)&& (!character.getConstraint().trim().equals("")) && 
		    	        		 (!character.getConstraint().trim().equals("|"))){
		    	        	 xtw.writeAttribute("constraint", processString(character.getConstraint()));
		    	         }	
		    	         
		    	     /*    if ((character.getInBrackets() != null)&& (character.getInBrackets() != 0) ){
		    	        	 tempString = (character.getInBrackets() == 1) ? "true" : "false";
		    	        	 xtw.writeAttribute("in_brackets", tempString);
		    	         }	
		    	      */   	  
		    	         if ((character.getOtherConstraint() != null)&& (!character.getOtherConstraint().trim().equals("")) && 
		    	        		 (!character.getOtherConstraint().trim().equals("|")) ){
		    	        	 xtw.writeAttribute("other_constraint", processString(character.getOtherConstraint()));
		    	         }	
		    	         if ((character.getNotes() != null)&& (!character.getNotes().equals("")) ){
		    	        	 xtw.writeAttribute("notes", character.getNotes());
		    	         }	
		    	        
		    	         
		    	         if ((character.getVerbString() != null)&& (!character.getVerbString().trim().equals("")) &&
		    	        		  (!character.getVerbString().trim().equals("|"))){
		    	        	 tempString =  (character.getVerbString().substring(0, 1).equals("|"))? 
		    	        			           character.getVerbString().substring(1).trim() : character.getVerbString().trim();		    	        	 	    	        	 
		    	        	if (!tempString.equals(""))  xtw.writeAttribute("verb_string", processString(tempString));
		    	         }	
		    	         
		    	         if ((character.getConstraintConjunction() != null)&& (!character.getConstraintConjunction().trim().equals("")) &&
		    	        		  (!character.getConstraintConjunction().trim().equals("|"))){
		    	        	 tempString =  (character.getConstraintConjunction().substring(0, 1).equals("|"))? 
		    	        			           character.getConstraintConjunction().substring(1).trim() : character.getConstraintConjunction().trim();		    	        	 	    	        	 
		    	        	if (!tempString.equals(""))  xtw.writeAttribute("constraint_conjunction", processString(tempString));
		    	         }		

		    	         
		    	         if ((character.getConstraintPreposition() != null)&& (!character.getConstraintPreposition().trim().equals("")) &&
		    	        		 (!character.getConstraintPreposition().trim().equals("|")) ){   	        	 
		    	        	 tempString =  (character.getConstraintPreposition().substring(0, 1).equals("|"))? 
  	        			           character.getConstraintPreposition().substring(1).trim() : character.getConstraintPreposition().trim();		    	        	 	    	        	 
  	        			     if (!tempString.equals(""))  xtw.writeAttribute("constraint_preposition", processString(tempString));
		    	         }			
		    	    		    	         
		    	         xtw.writeEndElement();
		    	         

		           }
		           
		          
		           xtw.writeEndElement();  // Biological entity
	    	        
              } // End biological_entity result set
			      
			    // Process relations for this clause
			    // Select all relations for a taxonDescriptionId and lineNumber  
			    rsRelation = TextDatabase.SelectRelationByTaxonDescritionIdLineNumber ( conn, taxonDescriptionId, taxonClauses.getInt(8)) ; 
		        
			    /* A relation has the following attributes:
			     * Integer id;
	             *  Integer taxonDescriptionId;
	             * Integer lineNumber;
				 * Integer sequence;
				 *String name;
				 *String alterName;
				 *String modifier;
				 *Integer vFrom;
				 *Integer vTo;
				 *String geographicalConstraint;
				 *Integer inBrackets;
				 *String organConstraint;
				 *String parallelismConstraint;
				 *String taxonConstraint;
				 *String ontologyId;
				 *String provenance;
				 *String notes;
				 *Integer negation;
			     */
			    
			    while (rsRelation.next())
		        	   
		           {  /* for each Record */ 
			           relation = new Relation(rsRelation.getInt(1), rsRelation.getInt(2), rsRelation.getInt(3),
			        		   rsRelation.getInt(4), rsRelation.getString(5), rsRelation.getString(6), 
			        		   rsRelation.getString(7), rsRelation.getInt(8), rsRelation.getInt(9), 
			        		   rsRelation.getString(10), rsRelation.getInt(11),  rsRelation.getString(12),
			        		   rsRelation.getString(13), rsRelation.getString(14), rsRelation.getString(15), 
			        		   rsRelation.getString(16),  rsRelation.getString(17), rsRelation.getInt(18)); 

		           
		           
			           	 xtw.writeStartElement("relation");
		    
			           	 if ((relation.getId() != null)&& (relation.getId()>0) ){
		    	        	 xtw.writeAttribute("id", "r" + relation.getId().toString());
		    	         }
		   
			           	 if ((relation.getName() != null)&& (!relation.getName().trim().equals("")) ){
		    	             xtw.writeAttribute("name", relation.getName());
			           	 }
			           	 	         
		    	         if ((relation.getVTo() != null)&& (relation.getVTo()>0 )){
		    	        	 
			    	         if ((relation.getVFrom() != null)&& (relation.getVFrom()>0) ){
			    	        	 xtw.writeAttribute("from", relation.getVFrom().toString());
			    	         }
			    	         
			    	         if ((relation.getVTo() != null)&& (relation.getVTo()>0) ){
			    	        	 xtw.writeAttribute("to", relation.getVTo().toString());
			    	         }
			    
		    	         }
		    	         
				
		    	    		    	         
		    	         xtw.writeEndElement(); // relation
		    	         

		           }
			    xtw.writeEndElement(); //statement

		         }
		      
		        xtw.writeEndElement();  // description
	    	    xtw.writeEndElement();  // treatment
	    	    xtw.writeEndDocument();

		        xtw.flush();
		        xtw.close();
		        System.out.println("Done");
		     }	    	      
		   
		     catch(Exception e)
		     {
		         e.printStackTrace();
		     }
	   
	 }
	    
	 public static String processString(String text){
		 // to remove separators and string duplication.
		 
		 String resultString = null;
		 if (text != null && !text.trim().equals("")){		 
		     resultString = text.replace("|", "-").trim();
		 
		     if (resultString.substring(0, 1).equals("-")) resultString = resultString.substring(1).trim();
		 }
		 return (resultString);
	 }

	 
	 public static String processCharType(String text){
		 // If text.leght <2 return "". 
		 
		 String resultString = null;
		 
		 if (text.trim().length()<2) resultString = "";
		 else { resultString = text.replace("|", "-").trim();
		 
		        if (resultString.substring(0, 1).equals("-")) resultString = resultString.substring(1).trim();
		 }
		 return (resultString);
	 }
	 
}
