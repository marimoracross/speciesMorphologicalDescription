  import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import opennlp.tools.util.InvalidFormatException;
import edu.upc.freeling.*;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import com.google.api.*;



public class TaxonDescriptionAnalyzer { 
  // FreeLing installation directory 
  private static final String FREELINGDIR = "/usr/local";
  private static final String DATA = FREELINGDIR + "/share/freeling/";
  private static final String LANG = "es";
  private static final ArrayList<Token> tokenList = new ArrayList<Token>(); // Token list 
  private static final ArrayList<TokenParser> tokenTreeList = new ArrayList<TokenParser>(); 
  
  
  public static void main( String argv[] ) throws IOException {
	  try{   
			/*Open database connection.  The connection is used for the all process */
			Connection conn = TextDatabase.OpenConnection();
		
		   // Used to implement the algorithm and test it.  It must not be used to process books. 
		   // theWholeTest (conn, 2);
			
			
			//Experiments (to process new books). 
			// Paramenters: connection, book_id
		    // theExperiment (conn, 7);
		   
			XMLGeneration(conn, 7 );	    

		    			
			/* Process new dictionary records, using the table TEXT.RAW_DICTIONARY */
			 //updateDictionary( conn);
			
			 /* Update dictionary.revision_level from OTO. If english_lemma is found in OTO then revision_level =2.
			  * Only records with revision_level = 2 are used by the algorithm.
			 */
			//TextDatabase.updateDictionaryRevisonLevelFromOTO( conn); 
			
            /* Close database connection*/
			TextDatabase.close(conn);

	   
		}
        catch(Exception es){
	           es.printStackTrace();
	    } 
  }
  
  
  public static void theExperiment(Connection conn, Integer p_book_id) {
	  /* - Description: Method used to run experiments. 
	 	* 
	 	*  Requirements:
	 	*   - Descriptions must be in table text.taxon_description.
	 	*   - Previous the process the user must replace abbreviation manually.
	 	*   - Table knowledge must include the token used for defining areas. 
	 	*   - Dictionary must include token translations.            
	 	*               
		* - Revision History:
		*     19/11/2015 - Maria Aux. Mora
		*     
		* - Arguments (input / output):
		*    conn               : database opened connection.
		*    p_book_id          : Book identifier. 
		*    
		* - Return Values:  
		*/
	  
	  String pContinue;
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      Integer minTaxonId, maxTaxonId;
	  
	  try{ 	 
			// STEP 1) Pre- processing: quotes replace, abbreviations replace (i.e: ca.)
			// Preprocesing (manually). 
		    //     
		    // Remove " and '
		    // Every description must have a "." at the end 
           preprocessingTaxonDescription(conn, p_book_id);  
		  
			/* STEP 2) Taxon description clause splitting using FreeLing. 
			*    Convert Taxon description into clauses and save the normalize result inside the database (table Clause).
			*    The method is applied to descriptions that are part of a book 
			*    Clause separators:  : ; . 
			*    Parameters: connection, book_id
			*/    
            processTaxonDescription(conn, p_book_id);  
          
			/* STEP 3) Pre-morphological analysis: upper case replace, 
			* 1. Ready - It is called in the previous process
			*  (update text.clause set contents = lower(contents);)
			* 2. Remove spaces between dashes and numbers.  It uses sed (operating system command). 
			* 3. Remove dashes between numbers and right parenthesis.   It uses sed (operating system command). 
			*/
		    removeSpacesBetweenNumbersAndDashInClausesAndRemoveADashBetweenNumbersAndRightParenthesis( conn, p_book_id); 
			
			
            /* STEP 4) Morphological analysis. It generates and saves the tokenList (Tokenization) 
            *   Update table Text.TOKEN
		    *   Parameters: connection, book_id
		    */
            analyzeClauses(conn, p_book_id);
			
			
			// STEP 5) Clause.POS calculation      	  
			// Creates a POS combination per clause using token.pos 
			//     (concatenating the first character per token)
		    //     POS in clauses is used in pre-bootstraping rules.
			//   Parameters: connection, book_id
			TextDatabase.updateClauseStatistics(conn, p_book_id);
			
	        /*==================================================================================*/	      
		    ///Process developed to translate and to learn characters and structures. 
			/* STEP 6) preBootstrapping 
			 * Uses simples rules to learn adjectives and names. 
			*   Parameters: connection, book_id 
			*   Requires: table text.clause with clauses associated with the book. 
			*             Table text.token filled with tokens related with the book.  
			*   */
			preBootstrapping(conn, p_book_id);
			/* 
			*/
				      
		     /* STEP 8) Bootstrapping to lean adjectives, names.  Uses text.TOKEN for the learning process.  
              *  Parameters: connection, book_id	*/		
		    Bootstrapping(conn, p_book_id);
		    correctKnowledge(conn ); 
		      
		    postBootstrapping (conn, p_book_id);
		    correctKnowledge(conn ); 


		    /*Before processing the system verifies that all token with type =(A) have a English translation.
		    * The English translation enable joining with OTO. If a token does not have an English translation \
		    * 
		    */
		    pContinue = "S";
		    
		    while (pContinue.equals("S")) {
		    
			    /*================================================================================= */
	      	    /* STEP 7)  Lemmas translation using the local Dictionary.				 */
			    updateTokenEnglishLemma(conn, p_book_id);
			      
	  
			    /* STEP 11) Correct token translation and update knowledge using OTO and token
			    *  Parameters: connection, book_id
				*/
			    correctTranslation(conn, p_book_id);
				//insertKnowledgeFromTokenJoinOTO(conn, p_book_id );
				
			     System.out.println("Desea continuar traduciendo los tokens no traducidos corectamente?(S/N) ");
		         pContinue = br.readLine();
		
				
	
		        /*==================================================================================*/	  
	            		  
		    }	
				
		    /* STEP 9) Generates chunks from each clause using comma as chunk delimiter.  
	             * Parameters: connection, book_id
	        */  
			createChunks (conn, p_book_id);                // Create chunk from clause using colon as separator.   
			TextDatabase.updateChunkPos(conn,p_book_id);   // Update chunk.pos using token.pos
						  
			/* STEP 10) Analyze text.chunk's contents and save results in text.token_tree */
	            //Parameters: connection, book_id
			parseChunk(conn, p_book_id);	
			     
			//Replace the field chunk.tree_pos using words relationship (trees) to label chunks. 
			TextDatabase.updateChunkTreePos(conn,p_book_id);
			     
				 
			// Delete results for a book.	
			TextDatabase.deleteCharacters(conn, p_book_id);
			TextDatabase.deleteBiologicalEntities(conn, p_book_id);
			TextDatabase.deleteRelations(conn, p_book_id);
			
			// STEP 12) Semantic analysis
			//Parameters: connection, book_id, initialTaxonDescriptionId, finalTaxonDescriptionId
			
			minTaxonId = TextDatabase.getMinTaxonId(conn, p_book_id);
			maxTaxonId = TextDatabase.getMaxTaxonId(conn, p_book_id);
			
			Integer maxProcessed = 50;
			Integer repetitions = (maxTaxonId - minTaxonId)/maxProcessed;
			Integer i;
			
			 // The semantic analysis algorithm  has memory problems processing many records.
     		for (i=1; i <= repetitions+ 1; i++){
			   semanticAnalisys(conn, p_book_id, minTaxonId, minTaxonId+maxProcessed);
			   minTaxonId = minTaxonId + maxProcessed+1;
			}
			
		    // STEP 13) XML Generation
			//Parameters: connection, book_id
			XMLGeneration(conn, p_book_id );	    
				
	  }catch(Exception es){
	         es.printStackTrace();
	    } 
  }


  
  
  
  public static void preprocessingTaxonDescription (Connection conn, Integer p_book_id) {
	  /* - Description: Method used to standardize taxon_description.description before being processed.  
	 	*                
		* - Revision History:
		*    17/12/2015 - Maria Aux. Mora
		*     
		* - Arguments (input / output):
		*    conn               : database opened connection.
		*    p_book_id          : Book identifier. 
		*    
		* - Return Values:  
		*/
	  
	  
	  try{ 	 
  
		TextDatabase.updateTaxonDescriptionAddFinalPoint(conn, p_book_id);   
		
	  }catch(Exception es){
	         es.printStackTrace();
	    } 
}  
  
  
  public static void updateTokenEnglishLemma (Connection conn, Integer p_book_id) {
	  /* - Description: Method used to translate all tokens using the local resources. 
	 	*                
		* - Revision History:
		*    23/1/2015 - Maria Aux. Mora
		*     
		* - Arguments (input / output):
		*    conn               : database opened connection.
		*    p_book_id          : Book identifier. 
		*    
		* - Return Values:  
		*/
	  
	  
	  try{ 	 
  
		TextDatabase.updateTokenEnglishLemmaFromKnowledge(conn, p_book_id);  
        TextDatabase.updateTokenEnglishLemmaFromDictionary(conn,  p_book_id, 2);
        TextDatabase.updateTokenEnglishLemmaFromDictionary(conn,  p_book_id, 0);
	  
	  }catch(Exception es){
	         es.printStackTrace();
	    } 
}
  
  public static void theWholeTest (Connection conn, Integer p_book_id) {
	  /* - Description: Method used to test the whole algorithm. 
	 	*                
		* - Revision History:
		*     5/10/2015 - Maria Aux. Mora
		*     
		* - Arguments (input / output):
		*    conn               : database opened connection.
		*    p_book_id          : Book identifier. 
		*    
		* - Return Values:  
		*/

	  String pContinue;
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      Integer minTaxonId, maxTaxonId;  
	  
	  try{ 	 
			// STEP 1) Pre- processing: quotes replace, abbreviations replace (i.e: ca.)
			// OJO falta, lo he hecho manualmente
			
			/* STEP 2) Taxon description clause splitting using FreeLing. 
			*    Convert Taxon description into clauses and save the normalize result inside the database (table Clause).
			*    The method is applied to descriptions that are part of a book 
			*    The method deletes all records related with the book before process it again.
			*    Clause separators:  : ; . 
			*    Parameters: connection, book_id
			*    
			*    Book 1 and book 2 are included.  
			*/    
             processTaxonDescription(conn, p_book_id);  
          
			/* STEP 3) Pre-morphological analysis: upper case replace, 
			* 1. Ready - It is called in the previous process
			*  (update text.clause set contents = lower(contents);)
			* 2. Remove spaces between dashes and numbers.  It uses sed (operating system command). 
			* 3. Remove dashes between numbers and right parenthesis.   It uses sed (operating system command).
			*/
		    removeSpacesBetweenNumbersAndDashInClausesAndRemoveADashBetweenNumbersAndRightParenthesis( conn, p_book_id); 
			
			
          /* STEP 4) Morphological analysis. It generates and saves the tokenList including POS 
           *   Update table Text.TOKEN
	       *   The method deletes all records related with the book before process it again.
	       * 	  Parameters: connection, book_id
		  */
            analyzeClauses(conn, p_book_id);
			
			
			// STEP 5) Clause.POS calculation      	  
			// Creates a POS combination per clause using token.pos 
			//     (concatenating the first character per token)
		    //     POS in clauses is used in pre-bootstraping rules.
			//   Parameters: connection, book_id
			TextDatabase.updateClauseStatistics(conn, p_book_id);
			
	        /*==================================================================================*/	      
		    ///Process developed to translate and to learn characters and structures. 
			/* STEP 6) preBootstrapping 
			 * Uses simples rules to learn adjectives and names. 
			*   Parameters: connection, book_id 
			*   Requires: table text.clause with clauses associated with the book. 
			*             Table text.token filled with tokens related with the book.  
			*   */
			preBootstrapping(conn, p_book_id);
			
			/* STEP 7)  Lemmas translation using the local Dictionary and Google Translation API
			 * The function is applied to a group of words by category (i.e. A = Adjectives) and associated to a book.
			 */
		      // Using the local dictionary.
		    // TextDatabase.updateTokenEnglishLemma(conn, p_book_id);
		      
		      //Using Google translator
			 /* translateTokens (conn, "A", p_book_id);
			  translateTokens (conn, "N",  p_book_id);
			  translateTokens (conn, "R", p_book_id);
			  translateTokens (conn, "D", p_book_id);
			  translateTokens (conn, "V", p_book_id);
			  translateTokens (conn, "P", p_book_id);
			  translateTokens (conn, "C", p_book_id);
			  translateTokens (conn, "I", p_book_id);
			  translateTokens (conn, "S", p_book_id);
			 * 
			 */
		     /* STEP 8) Bootstrapping to lean adjectives, names.  Uses text.TOKEN for the learning process.  
              *  Parameters: connection, book_id	*/		
		      Bootstrapping(conn, p_book_id);
		      correctKnowledge(conn ); 
		      
		      postBootstrapping (conn, p_book_id);
		      correctKnowledge(conn ); 

		      
		     /* STEP 11) Correct translation in token and update knowledge using OTO and token
			 *  Parameters: connection, book_id
			 */
			 correctTranslation(conn, p_book_id);
			 insertKnowledgeFromTokenJoinOTO(conn, p_book_id );
			 correctKnowledgeEnglishLemma(conn, p_book_id);
	        /*==================================================================================*/	  
		  
		  
			/* STEP 9) Generates chunks from each clause using comma as chunk delimiter.  
             * Parameters: connection, book_id
             */  
			 createChunks (conn, p_book_id);                // Create chunk from clause using colon as separator.   
			 TextDatabase.updateChunkPos(conn,p_book_id);   // Update chunk.pos using token.pos
					  
			/* STEP 10) Analyze text.chunk's contents and save results in text.token_tree */
            //Parameters: connection, book_id
		    parseChunk(conn, p_book_id);	
		     
			//Replace the field chunk.tree_pos using words relationship (trees) to label chunks. 
		    TextDatabase.updateChunkTreePos(conn,p_book_id);
		     
			 
			// STEP 12) Semantic analysis
			//Parameters: connection, book_id, initialTaxonDescriptionId, finalTaxonDescriptionId
			minTaxonId = TextDatabase.getMinTaxonId(conn, p_book_id);
			maxTaxonId = TextDatabase.getMaxTaxonId(conn, p_book_id);
			
			Integer maxProcessed = 100;
			Integer repetitions = (maxTaxonId - minTaxonId)/maxProcessed;
			Integer i;
			
			 // The semantic analysis algorithm  has memory problems processing more than 100 records.
			for (i=1; i <= repetitions+ 1; i++){
			   semanticAnalisys(conn, p_book_id, minTaxonId, minTaxonId+maxProcessed);
			   minTaxonId = minTaxonId + maxProcessed+1;
			}

		    
		    
			// STEP 13) XML Generation
			//Parameters: connection, book_id
		    XMLGeneration(conn, p_book_id);
			
	  }catch(Exception es){
	         es.printStackTrace();
	    } 
  }
  
  
  private static void updateDictionary(Connection conn) { 
		 /* 
		 * - Description: 
		 *    Uses table TEMP_DICTIONARY to insert new dictionary records.  
		 *    TEMP_DICTIONARY.string includes the English term and one o more Spanish translation.
		 *   
		 * - Revision History:
		 *     19/11/2015 - Maria Aux. Mora
		 *     
		 * - Arguments (input / output):
		 *    conn   : database opened connection.
		 * 
		 * - Return Values:
		 *     Table dictionary updated with new records.   
		*/	  
  
    String [] parts; 
    String vTerms = null;  // Terms separated by commas. The first one is the English term.  
    Integer i;
    String englishLemma=null;
    
	try{
		
		
		ResultSet rs = TextDatabase.selectTempDictionary(conn);
	    
		while (rs.next())
	    {  /*  */ 
	 	   
    		 vTerms = rs.getString(1);
			 
		     parts = vTerms.split(",");
			 
		     i = 1;
		     for(String s:parts){
		         System.out.println("Palabra " +s); 

				 if (s != null  && !s.trim().equals("") ) {
					if (i==1) englishLemma = s.trim();
					else TextDatabase.insertDictionary(conn, s.trim(),  s.trim(), englishLemma); 
				 }
			   i++;	 
	        }    
	      }
	    }     
	    
	    catch(Exception es){
	        es.printStackTrace();
	   }
}
  
  
  
//STEP 13) =====================================================================================================//
//XML generation

public static void XMLGeneration(Connection conn, Integer p_book_id) throws InvalidFormatException, IOException {
/* - Description: Generates XML files for all taxon descriptions included in a book.
  * - Revision History:
  *     29/09/2015 - Maria Aux. Mora
  *     
  * - Arguments (input / output):
  *    conn : database opened connection.
  *    p_book_id:  book that will be processed
  * 
  * - Return Values:
  *     . XML files.
*/

try{ 	  
 
 ResultSet rs = TextDatabase.SelectTaxonDescription(conn, null, p_book_id);

 while (rs.next())
 {  /* for each Record */ 

    Integer v_taxon_description_id = rs.getInt(1);   /*TAXON_DESCRIPTION.id*/
	  
    XMLGenerator.generateXML (conn, v_taxon_description_id,  "/home/mmora/textos/resultados/"+v_taxon_description_id.toString()+"-" , p_book_id);  

    
   }  
}
catch(Exception es){
    es.printStackTrace();
} 
}
  
  
  
// STEP 2) ===============================================================================================//
  
  public static void processTaxonDescription(Connection conn, Integer p_book_id) throws InvalidFormatException, IOException {
   /* - Description: Convert Taxon description into clauses and save the normalize result inside the database (table Clause).
    *       It concatenates tokens to form a clause. 
 	*                
	* - Revision History:
	*     10/12/2014 - Maria Aux. Mora
	*     
	* - Arguments (input / output):
	*    conn               : database opened connection.
	*    p_book_id          : Book identifier. 
	*    
	* - Return Values:  
	*/	  
	  
	  try{
		  TextDatabase.deleteClauses(conn,p_book_id );
		  
	      System.load( "/pkg/freeling-3.1/APIs/java/libfreeling_javaAPI.so" );  
	   	  
	      Util.initLocale( "default" );

	      // Create options set for maco analyzer.
	      MacoOptions op = new MacoOptions( LANG );

	      op.setActiveModules(false, true, true, true, 
	                                 true, true, true, 
	                                 true, true, true);
	      // Data files definition
	      op.setDataFiles(
	        "", 
	        DATA + LANG+"/locucions.dat", 
	        DATA + LANG + "/quantities.dat",
	        DATA + LANG + "/afixos.dat",
	        DATA + LANG + "/probabilitats.dat",
	        DATA + LANG + "/dicc.src",
	        DATA + LANG + "/np.dat",
	        DATA + "common/punct.dat");

	      System.out.print( "Procesando " );
	      // Create analyzers.
	      LangIdent lgid = new LangIdent(DATA + "/common/lang_ident/ident-few.dat");

	      Tokenizer tk = new Tokenizer( DATA + LANG + "/tokenizer.dat" );
	      Splitter sp = new Splitter( DATA + LANG + "/splitter.dat" );
    
     
	      System.out.print( sp );
      
	      // Access Clause in TEXT database. Null = process all clauses
	  	ResultSet rs = TextDatabase.SelectTaxonDescription(conn, null, p_book_id);
	  	
	      while (rs.next())
	      {  /* for each Record */ 

	         Integer v_id = rs.getInt(1);   /*Taxon description id*/
	  	   String  line =  rs.getString(6); /*Taxon description contents*/
	       
	         // Identify language of the text.  
	         String lg = lgid.identifyLanguage(line);
	         System.out.println( "-------- LANG_IDENT results -----------" );
	         System.out.println("Language detected (from first line in text): " + lg +" " + v_id);
	         System.out.println("Linea " + line);
	         if ( line != null ) {
	            // Extract the tokens from the line of text.
	            ListWord l = tk.tokenize( line );
	        
	            // Split the tokens into distinct sentences.
	            ListSentence ls = sp.split( l, false );
	            
	            // Convert Sentences to String, normalize clause text, and update database (TEXT.Clause)
	            saveSentences (conn, v_id, ls, p_book_id);
	   
	        }
	      }
	      TextDatabase.normalizingClauses (conn); 
	  }   
	    catch(Exception es){
	         es.printStackTrace();
	    } 
	  }  

  
// STEP 3) ===============================================================================================//

  private static void removeSpacesBetweenNumbersAndDashInClausesAndRemoveADashBetweenNumbersAndRightParenthesis (Connection conn, Integer p_book_id)  { 
	  /* - Description: Removes spaces between dashes and numbers in the string text and removes a dash between a number an a 
	   *    right parenthesis.  It uses sed (operating system command).
       * 
       * - Revision History:
       *     5/10/2015 - Maria Aux. Mora
       *     
       * - Arguments (input / output):
       *    conn : database opened connection.
       *    p_book_id : Book identifier. 
       *    
       * - Return Values:
       *     Table clause updated with contents standardize.
  */ 	  
	Integer clauseId;
	String clauseContents = null;  
	try{
	    ResultSet rs = TextDatabase.getClauseContentsAndId(conn, null, p_book_id);
	    while (rs.next())
	    { 
	       clauseId = rs.getInt(1); //Clause.taxon_description_id
           clauseContents = rs.getString(2);  // Clause.contents
	           	  
	       if (clauseContents != null && clauseContents.contains("-")) {
	    	   clauseContents = removeSpacesBetweenNumbersAndDash ( clauseContents );
	    	   clauseContents = removeADashBetweenNumbersAndRightParenthesis (clauseContents ) ;
	    	   TextDatabase.updateClauseContents (conn, clauseId, clauseContents);
	       }   
	    }    
	   }
	    
	    catch(Exception es){
	        es.printStackTrace();
	   } 
}  
  
  
  public static String removeSpacesBetweenNumbersAndDash ( String text)  {
  /* - Description: Remove spaces between dashes and numbers in the string text.  It uses sed (operating system command).
       * 
       * - Revision History:
       *     5/10/2015 - Maria Aux. Mora
       *     
       * - Arguments (input / output):
        *    text      : text that need to be standardized 
       *    
       * - Return Values:
       *     Text processed.
  */ 
	  
   String textProcessed = "";	
	  
   try{  
  
	  	String[] command ={ "bash", "-c", "echo '" + text +"' | sed 's/\\([0-9]\\) *-/\\1-/g' | sed 's/- *\\([0-9]\\)/-\\1/g'"};
	  	ProcessBuilder pb = new ProcessBuilder(command);
	  	pb.redirectErrorStream(true);
	  	Process process = pb.start();
	  	process.waitFor();
	  	
	  	
	    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); //Get the output
	  	
	    String line;
	    while((line=reader.readLine())!= null){
	    	  textProcessed = textProcessed +" " + line.trim();
	          System.out.println(line);
	          System.out.flush();
	    }
	    reader.close();
    }catch(Exception es){
       es.printStackTrace();
    }
    return (textProcessed);
  }
  
  public static String removeADashBetweenNumbersAndRightParenthesis ( String text)  {
  /* - Description: Remove dash between a number and a right parenthesis in the string text.  It uses the sed (operating system command).
       * 
       * - Revision History:
       *     11/10/2015 - Maria Aux. Mora
       *     
       * - Arguments (input / output):
        *    text      : text that need to be standardized 
       *    
       * - Return Values:
       *     Text processed.
  */ 
	  
   String textProcessed = "";	
	  
   try{  
  
	  	String[] command ={ "bash", "-c", "echo '" + text +"' |  sed 's/\\([0-9]\\) *- *)/\\1\\)/g'"};
	  	ProcessBuilder pb = new ProcessBuilder(command);
	  	pb.redirectErrorStream(true);
	  	Process process = pb.start();
	  	process.waitFor();
	  	
	  	
	    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); //Get the output
	  	
	    String line;
	    while((line=reader.readLine())!= null){
	    	  textProcessed = textProcessed +" " + line.trim();
	          System.out.println(line);
	          System.out.flush();
	    }
	    reader.close();
    }catch(Exception es){
       es.printStackTrace();
    }
    return (textProcessed);
  }
  
  
//STEP 4) ===============================================================================================//
  
  public static void analyzeClauses(Connection conn, Integer p_book_id) throws InvalidFormatException, IOException {
	  /* - Description: create tokens (records) and save the result in the table text.token using text.clauses.  Additionally 
	   *                it processes each token using Freeling. 
	       * - Revision History:
	       *     10/01/2015 - Maria Aux. Mora
	       *     
	       * - Arguments (input / output):
	       *    conn : database opened connection.
	       *    p_book_id : Book identifier. 
	       *    
	       * - Return Values:
	       *     Table token updated with new records.
	  */

	  try{ 	  
	      Integer v_id ;   /*Clause.Taxon_description_id*/
		  String  line ; /* Clause.contents*/
		  Integer v_line_number ; // Clause.line_number 
		  String lg;       
		  
		  
		  TextDatabase.deleteTokens(conn,p_book_id );

		  
	      System.load( "/pkg/freeling-3.1/APIs/java/libfreeling_javaAPI.so" );  
	   	  
	      Util.initLocale( "default" );

	      // Create options set for maco analyzer.
	      MacoOptions op = new MacoOptions( LANG );

	      op.setActiveModules(false, true, true, true, 
	                                 true, true, true, 
	                                 true, true, true);
	      // Data files definition
	      op.setDataFiles(
	        "", 
	        DATA + LANG+"/locucions.dat", 
	        DATA + LANG + "/quantities.dat",
	        DATA + LANG + "/afixos.dat",
	        DATA + LANG + "/probabilitats.dat",
	        DATA + LANG + "/dicc.src",
	        DATA + LANG + "/np.dat",
	        DATA + "common/punct.dat");

	      System.out.print( "Processando " );
	      // Create analyzers.
	      LangIdent lgid = new LangIdent(DATA + "/common/lang_ident/ident-few.dat");

	      Tokenizer tk = new Tokenizer( DATA + LANG + "/tokenizer.dat" );
	      Splitter sp = new Splitter( DATA + LANG + "/splitter.dat" );
	      Maco mf = new Maco( op );
	      
	      
	      System.out.print( sp );

	      HmmTagger tg = new HmmTagger( DATA + LANG + "/tagger.dat", true, 2 );
	      ChartParser parser = new ChartParser(
	        DATA + LANG + "/chunker/grammar-chunk.dat" ); 
	      DepTxala dep = new DepTxala( DATA + LANG + "/dep/dependences.dat",
	        parser.getStartSymbol() );
	      Nec neclass = new Nec( DATA + LANG + "/nerc/nec/nec-ab-poor1.dat" );

	      Senses sen = new Senses(DATA + LANG + "/senses.dat" ); // sense dictionary
	      Ukb dis = new Ukb( DATA + LANG + "/ukb.dat" ); // sense disambiguator
	    
	      // Access Clause in TEXT database. Null = process all clauses
	  	ResultSet rs = TextDatabase.selectClause(conn, null, null , null, null, p_book_id);
	  	
	      while (rs.next())
	      {  /* for each Record */ 

	          v_id = rs.getInt(2);   /*Clause.Taxon_description_id*/
	  	     line =  rs.getString(5); /* Clause.contents*/
	  	     v_line_number = rs.getInt(8); // Clause.line_number 
	       
	         // Identify language of the text.  
	         lg = lgid.identifyLanguage(line);
	         System.out.println( "-------- LANG_IDENT results -----------" );
	         System.out.println("Language detected (from first line in text): " + lg +" " + v_id);
	         System.out.println("Linea " + line);
	         if ( line != null ) {
	            // Extract the tokens from the line of text.
	            ListWord l = tk.tokenize( line );
	        
	            // Split the tokens into distinct sentences.
	            ListSentence ls = sp.split( l, false );
	            
	            // Perform morphological analysis
	            mf.analyze( ls );

	            // Perform part-of-speech tagging.
	            tg.analyze( ls );

	            // Perform named entity (NE) classification.
	            neclass.analyze( ls );

	            sen.analyze( ls );
	            dis.analyze( ls );
	            
	            // Save tokens inside tokenList
	            saveResults( v_id,v_line_number, 0, ls, "tagged", p_book_id);

	        }
	      }
	      /* Insert result into the database */
	      TextDatabase.createTokenRecord(conn, tokenList , p_book_id);   
	   
	  }   
	    catch(Exception es){
	         es.printStackTrace();
	    } 
	  } 
  
// STEP 5) ===============================================================================================//
	// Creates a POS combination per clause using token.pos 
	//     (concatenating the first character per token)
	//It uses TextDatabase.updateClauseStatistics(conn);  

// STEP 6) ===============================================================================================//

  private static void preBootstrapping(Connection conn, Integer pBook ) { 
    /* pre - bootstrapping algorithm

     * - Description: 
     * Uses Freeling POS results to select the most simplest clause to learn
     * the initials N (structure names) and A (Adjectives)
     * clause where (pos like '%FAF%' or pos like 'NAF%' or pos like 'NZF%')                
     * 
     * Uses the fact that taxonomic description are made from clauses like:
     *       N (structure name) + <missing character> + JJ (character value) (Pattern = nb)  

     * - Revision History:
     *     10/01/2015 - Maria Aux. Mora
     *      2/05/2015 - Maria Mora -> parameter pBook added
     *       
     * - Arguments (input / output):
     *    conn : database opened connection.
     * 
     * - Return Values:
    *     Table Knowledge updated with new words.   
	*/
	
	/* Parameters:  
	 * 		Connection conn : database connection
	 * 		String pos      : pos to seach in table token
	 *      Integer init    : Initial token sequence  
	 *      Integer end     : final token sequence
	 *      Integer book    : book identifier
	 * */  	  
   //  analyzeSimpleClauses(conn,"NAF%",1, 3, pBook);  // H0 -> Learn E and A using simple clauses like NAF%  Ya no tiene sentido
   //  analyzeSimpleClauses(conn,"NZF%",1, 3, pBook);  // H0 -> NZF% despues de H1 ya no tiene sentido 
	
	  
   // Final rule list:		  
   analyzeSimpleClausesH1(conn,"N%",1, 1, pBook);  // H1 -> Learn structures names using 
                                                       //       the fact that initial word are structures names (if pos = 'N').
   //Parameters: database connection, book_id
   includeAdjectivesH0(conn, pBook);                   // H0 -> include all adjective as A                 	  
	
   includeWordwithDashAsAdjectiveH0 (conn,pBook);    //H0:  Process word with pos = N and have a dash as adjectives
    
   analyzeVerbParticipleH2(conn, pBook);             // H2: add new knowledge: include all participles as Adjectives. 
   
   
	  
  }
  
  
  private static void includeWordwithDashAsAdjectiveH0(Connection conn,  Integer p_book ) { 
	    /* Part of the pre - bootstrapping algorithm
		 * 
		 * - Description: 
		 * Process word with pos = N and have a dash as adjectives 
		 *   
		 * - Revision History:
		 *     7/05/2015 - Maria Aux. Mora
		 *     
		 * - Arguments (input / output):
		 *    conn   : database opened connection.
		 *    p_book : book identifier     
		 * 
		 * - Return Values:
		 *     Table Knowledge updated with new records.   
		*/	  
		  
	try{
	    ResultSet rs = TextDatabase.SelectTokenByToken(conn, "%-%", p_book);
	    while (rs.next())
	    {  /* The process must fetch 1 records and verify if pos is in (V, N) -> if it is true insert new Adjective (A) */ 
	 	   
	       Integer vId1 = rs.getInt(1);                //Token.id
	       Integer vTaxonDescriptionId = rs.getInt(2); //Token.taxon_description_id
	       Integer vLineNumber = rs.getInt(3);         //Token.line_number
	       String  vToken1 = rs.getString(4);          // Token.token   
	       String  vLemma1 = rs.getString(5);          // Token.lemma    
	       String  vPos1 = rs.getString(6);            // Token.pos 
	       String  vEnglishLemma1 = rs.getString(10);  // Token.english_lemma
	     
	       System.out.println("Token, pos, lemma " + vToken1 + ", " + vPos1.substring(0, 1)+", "+ vLemma1); 
	         	  
	       if (vPos1.substring(0, 1).equals("N") || vPos1.substring(0, 1).equals("V")) {
              TextDatabase.insertKnowledge(conn, vToken1, vLemma1, vEnglishLemma1, vPos1, "A", null, 1);

	       }   
	    }    
	   }
	    
	    catch(Exception es){
	        es.printStackTrace();
	   } 
}  
  
  
  private static void processWordsWithoutTypeUsingAdjectiveWithDashH0(Connection conn, Integer vBookId) { 
	    /* Part of the bootstrapping algorithm
		 * 
		 * - Description: 
		 *    Compare vToken with words with dash and return a sugested type.
		 *   
		 * - Revision History:
		 *     16/11/2015 - Maria Aux. Mora
		 *     
		 * - Arguments (input / output):
		 *    conn   : database opened connection.
		 * 
		 * - Return Values:
		 *     Table Knowledge updated with new records.   
		*/	  
  
  String vType = null;
  String  vToken = null;          // Token.token   
  String  vLemma = null;          // Token.lemma    
  String  vPos = null;            // Token.pos 
  String  vEnglishLemma = null;  // Token.english_lemma

	try{
		
		ResultSet rs = TextDatabase.SelectTokenByNullType(conn, vBookId);
	    
		while (rs.next())
	    {  /*  */ 
	    
	       vToken = rs.getString(1);          // Token.token   
	       vLemma = rs.getString(4);          // Token.lemma    
	       vPos = rs.getString(2);            // Token.pos 
	       vEnglishLemma = rs.getString(5);  // Token.english_lemma
	     

           if ( vToken.length()>5) 
	           vType = compareWithAdjectivesThatIncludeDashH0(conn,  vToken);
           else vType = null;

	       if (vType != null){
              
              System.out.println("Token, pos, type " + vToken + ", " + vPos.substring(0, 1)+", "+ vType); 
	    	   
		      if (vPos.substring(0, 1).equals("V")) vPos = "A";
		      
	          TextDatabase.insertKnowledge(conn, vToken, vLemma, vEnglishLemma, vPos, vType, null, 0);
	       }
	       
	    }    
	   }
	    
	    catch(Exception es){
	        es.printStackTrace();
	   }
} 
  
  
  private static void processTheRestOfTokensH8(Connection conn, Integer vBookId) { 
	    /* Part of the post-bootstrapping algorithm
		 * 
	     * - Description: For token without type:
	     *      If pos like 'N' => Structures
	     *      If pos like 'V' => Verbs
		 *   
		 * - Revision History:
		 *     16/11/2015 - Maria Aux. Mora
		 *     
		 * - Arguments (input / output):
		 *    conn   : database opened connection.
		 * 
		 * - Return Values:
		 *     Table Knowledge updated with new records.   
		*/	  

    String vType = null;  
    String  vToken = null;          // Token.token   
    String  vLemma = null;          // Token.lemma     
    String  vPos = null;            // Token.pos 
    String  vEnglishLemma = null;  // Token.english_lemma

	try{
		
		ResultSet rs = TextDatabase.SelectTokenByNullType(conn, vBookId);
	    
		while (rs.next())
	    {  /*  */ 
	    
	       vToken = rs.getString(1);          // Token.token   
	       vLemma = rs.getString(4);          // Token.lemma    
	       vPos = rs.getString(2);            // Token.pos 
	       vEnglishLemma = rs.getString(5);  // Token.english_lemma
	     
           
		   if (vPos.substring(0, 1).equals("N"))   
	          TextDatabase.insertKnowledge(conn, vToken, vLemma, vEnglishLemma, vPos, "E", null, 0);
		   else  if (vPos.substring(0, 1).equals("V"))   
		          TextDatabase.insertKnowledge(conn, vToken, vLemma, vEnglishLemma, vPos, "V", null, 0);
	     }
	         
	   }
	    
	    catch(Exception es){
	        es.printStackTrace();
	   }
} 

  
 private static String compareWithAdjectivesThatIncludeDashH0(Connection conn, String vToken) { 
	    /* Part of the bootstrapping algorithm
		 * 
		 * - Description: 
		 *    Compare vToken with words with dash and return a sugested type.
		 *   
		 * - Revision History:
		 *     16/11/2015 - Maria Aux. Mora
		 *     
		 * - Arguments (input / output):
		 *    conn   : database opened connection.
		 * 
		 * - Return Values:
		 *     Table Knowledge updated with new records.   
		*/	  
    
    String vType = null;
  /*  String [] parts; 
    String vPhrase = null;
*/    
	try{
		
		
		ResultSet rs = TextDatabase.selectKnowledgePhraseWithDashAndLike(conn, "%" + vToken + "%");
	    
		while (rs.next())
	    {  /*  */ 
	 	   
/*			 vPhrase = rs.getString(2);
			 
		     parts = vPhrase.split("-");
			  
		     for(String s:parts){
		           System.out.println("La SSSSS " +s); 

				 if (s.trim().equals(vToken.trim()) ) {
	*/
	         vType = rs.getString(4);             // Type 
	     }    
	   }
	    
	    catch(Exception es){
	        es.printStackTrace();
	   }
	return (vType);
}
    

  private static void includeAdjectivesH0(Connection conn,  Integer p_book ) { 
	    /* Part of the pre - bootstrapping algorithm
		 * 
		 * - Description: 
		 * Uses Freeling POS results to select token with pos = A% and include them as Adjectives. 
		 *   
		 * - Revision History:
		 *     2/05/2015 - Maria Aux. Mora
		 *     
		 * - Arguments (input / output):
		 *    conn   : database opened connection.
		 *    p_book : book identifier     
		 * 
		 * - Return Values:
		 *     Table Knowledge updated with new records.   
		*/	  
		  
	try{
	    ResultSet rs = TextDatabase.SelectTokenByPOS(conn, "A%", p_book);
	    while (rs.next())
	    {  /* The process must fetch 1 records and verify that pos = 'A' -> if true insert new Adjective (A) */ 
	 	   
	       String  vToken1 = rs.getString(4);          // Token.token   
	       String  vLemma1 = rs.getString(5);          // Token.lemma    
	       String  vPos1 = rs.getString(6);            // Token.pos 
	       String  vEnglishLemma1 = rs.getString(10);  // Token.english_lemma
	     
	       System.out.println("Token, pos, lemma " + vToken1 + ", " + vPos1.substring(0, 1)+", "+ vLemma1); 
	         	  
	       if (vPos1.substring(0, 1).equals("A")) {
              TextDatabase.insertKnowledge(conn, vToken1, vLemma1, vEnglishLemma1, vPos1, "A", null,1);

	       }   
	    }    
	   }
	    
	    catch(Exception es){
	        es.printStackTrace();
	   } 
}
  
   
  private static void analyzeVerbParticipleH2(Connection conn,  Integer p_book ) { 
		//

	    /* Part of the pre - bootstrapping algorithm
		 * 
		 * - Description: 
		 * Uses Freeling POS results to select token with pos = V to learn adjectives if token is a participle 
         * (verb ending in ado, ido, to, so, cho) -and its plural and female application. 
		 *   
		 * - Revision History:
		 *     2/05/2015 - Maria Aux. Mora
		 *     
		 * - Arguments (input / output):
		 *    conn   : database opened connection.
		 *    p_book : book identifier     
		 * 
		 * - Return Values:
		 *     Table Knowledge updated with new records.   
		*/	  
		  
	try{
	    ResultSet rs = TextDatabase.SelectTokenByPOS(conn, "V%", p_book);
	    while (rs.next())
	    {  /* The process must fetch 1 records and verify that pos = 'V' -> if true insert new Adjective (A) */ 
	 	   
//	       Integer vId1 = rs.getInt(1);                //Token.id
//	       Integer vTaxonDescriptionId = rs.getInt(2); //Token.taxon_description_id
//	       Integer vLineNumber = rs.getInt(3);         //Token.line_number
	       String  vToken1 = rs.getString(4);          // Token.token   
	       String  vLemma1 = rs.getString(5);          // Token.lemma    
	       String  vPos1 = rs.getString(6);            // Token.pos 
	       String  vEnglishLemma1 = rs.getString(10);  // Token.english_lemma
	     
	       System.out.println("Token, pos, lemma " + vToken1 + ", " + vPos1.substring(0, 1)+", "+ vLemma1); 
	         	  
	       if (vPos1.substring(0, 1).equals("V")) {
             TextDatabase.insertKnowledge(conn, vToken1, vLemma1, vEnglishLemma1, vPos1, "A", null,1);

	       }   
	    }    
	   }
	    
	    catch(Exception es){
	        es.printStackTrace();
	   } 
}
  
  
  
  private static void analyzeSimpleClausesH1(Connection conn, String p_pos, Integer p_init, Integer p_fin, Integer p_book ) { 
		//

	    /* Part of the pre - bootstrapping algorithm
		 * 
		 * - Description: 
		 * Uses Freeling POS results to select clauses that start with a N (name) to learn
		 * structure names. Uses clause where pos like 'N%'. 
		 * 
		 * - Revision History:
		 *     2/05/2015 - Maria Aux. Mora
		 *     
		 * - Arguments (input / output):
		 *    conn   : database opened connection.
		 *    p_pos  : pos to seach in table token
	     *    p_init : initial token sequence 
	     *    p_fin  : final token sequence
		 *    p_book : book identifier     
		 * 
		 * - Return Values:
		 *     Table Knowledge updated with new records.   
		*/	  
		  
	try{
		/*Select the first token of each clause that init with a p_pos */
	    ResultSet rs = TextDatabase.SelectTokenByPOS(conn,p_pos,p_init,p_fin, p_book);
	    while (rs.next())
	    {  /* The process must fetch 1 records and verify that pos = 'N' -> if true insert new Structure (E) */ 
	 	   
	  //     Integer vId1 = rs.getInt(1);                //Token.id
	       Integer vTaxonDescriptionId = rs.getInt(2); //Token.taxon_description_id
	       Integer vLineNumber = rs.getInt(3);         //Token.line_number
	       String  vToken1 = rs.getString(4);          // Token.token   
	       String  vLemma1 = rs.getString(5);          // Token.lemma    
	       String  vPos1 = rs.getString(6);            // Token.pos 
	       String  vEnglishLemma1 = rs.getString(10);  // Token.english_lemma
	     
	       System.out.println("Token, pos, lemma " + vToken1 + ", " + vPos1.substring(0, 1)+", "+ vLemma1); 
	         	  
	       if (vPos1.substring(0, 1).equals("N")) {
               TextDatabase.insertKnowledge(conn, vToken1, vLemma1, vEnglishLemma1, vPos1, "E", "n", 0);
               TextDatabase.annotateClause(conn, vTaxonDescriptionId, vLineNumber, vLemma1);

	       }   
	    }    
	   }
	    
	    catch(Exception es){
	        es.printStackTrace();
	   } 
  }
  
  private static void analyzeSimpleClauses (Connection conn, String p_pos, Integer p_init, Integer p_fin, Integer p_book) { 
	//

    /* Part of the pre - bootstrapping algorithm
	 * 
	 * - Description: 
	 * Uses Freeling POS results to select the most simplest clause to learn
	 * N(Names) and A(Adjectives). Uses clause where pos like 'NAF%' or pos like 'NZF%'. 
	 * 
	 * - Revision History:
	 *     17/01/2015 - Maria Aux. Mora
	 *     
	 * - Arguments (input / output):
	 *    conn : database opened connection.
	 * 
	 * - Return Values:
	 *     Table token field final_tag (N or A assigned) and role (n, m or b assigned) updated.
	 *     Table Knowledge updated with new words.   
	*/	  
	  
try{
    ResultSet rs = TextDatabase.SelectTokenByPOS(conn,p_pos,p_init,p_fin, p_book);
    while (rs.next())
    {  /* The process must fetch 3 records to verify:
          record 1.pos = 'N' and record 2.pos = 'A' o 'Z'
          The last token is a . : or ; */ 
 	   
       Integer vId1 = rs.getInt(1);                //Token.id
       Integer vTaxonDescriptionId = rs.getInt(2); //Token.taxon_description_id
       Integer vLineNumber = rs.getInt(3);         //Token.line_number
       String  vToken1 = rs.getString(4);          // Token.token   
       String  vLemma1 = rs.getString(5);          // Token.lemma    
       String  vPos1 = rs.getString(6);            // Token.pos 
       String  vEnglishLemma1 = rs.getString(10);  // Token.english_lemma
      
       if (rs.next()) {
           Integer vId2 = rs.getInt(1);            //Token.id
           String  vToken2 = rs.getString(4);      // Token.token   
           String  vLemma2 = rs.getString(5);      // Token.lemma    
           String  vPos2 = rs.getString(6);        // Token.pos 
           String  vEnglishLemma2 = rs.getString(10);  // Token.english_lemma    
  
           if (rs.next()) {
          	  System.out.println("Pos 1" + vPos1.substring(0, 1)+"XXX"); 
           	  System.out.println("Pos 2" + vPos2.substring(0, 1)+"XXX"); 
         	  
        	  if (vPos1.substring(0, 1).equals("N")) {
      		    TextDatabase.updateTokenFinalTagRole (conn,vId1, "N" , "n");
       		    TextDatabase.insertKnowledge(conn, vToken1, vLemma1, vEnglishLemma1, vPos1, "E", "n",1);
    		    TextDatabase.annotateClause(conn, vTaxonDescriptionId, vLineNumber, vLemma1);

       		    if (vPos2.substring(0, 1).equals("A")) {
        		    TextDatabase.updateTokenFinalTagRole (conn,vId2, "A", "b" );
        		    TextDatabase.insertKnowledge(conn, vToken2, vLemma2, vEnglishLemma2, vPos2,  "A", "b",1);
        		  } 
                }   
           }    
       }
    }   }
    catch(Exception es){
        es.printStackTrace();
   } 

    
  }

//STEP 7) ===============================================================================================//
//------- Lemmas translation using Google Translation API*
  
  private static void translateTokens(Connection conn, String p_pos, Integer p_book_id) throws Exception {

	    /* token.lemma translation into English using Google translator API and 
	     * the JAVA tool available in https://code.google.com/p/google-api-translate-java/
	     * 
	     * - Description: 
	     * Uses Google translator API to translate token.lemma into English.   
	     * The method uses the word apice to specialized the translation.                
	     * 
	     * - Revision History:
	     *     02/02/2015 - Maria Aux. Mora
	     *     
	     * - Arguments (input / output):
	     *    conn : database opened connection.
	     * 
	     * - Return Values:
	     *     Table token field english_lemma updated.
		*/
	  
	  // Set the HTTP referrer to your website address.

	    GoogleAPI.setHttpReferrer("HTTP://localhost");

	    // Set the Google Translate API key

	    // See: http://code.google.com/apis/language/translate/v2/getting_started.html

	    GoogleAPI.setKey("AIzaSyAo9UUKW6DpaqXaRxRSXJC2nOUG2u1JW8U");
	     
	    ResultSet rs = TextDatabase.SelectTokenLemmaByPOS(conn, p_pos+'%', p_book_id);
	  	
	    while (rs.next())
	      { 
	         String  vLemma = rs.getString(1);        //Toke.lemma   
	         String vLemmaTranslated ;
	         String translatedText;
	         
	         
	         if ((p_pos.equals("A") ||  p_pos.equals("N") || p_pos.equals("V") ))
	         {	 translatedText = Translate.DEFAULT.execute("ápice "+vLemma, Language.SPANISH, Language.ENGLISH);
	             vLemmaTranslated =  translatedText.trim().toLowerCase().replace("apex", ""); 
		         System.out.println(vLemma + " traduccion " +translatedText + " TIRA FINAL " + vLemmaTranslated);
}
	         else {
	        	 
	             vLemmaTranslated = Translate.DEFAULT.execute(vLemma, Language.SPANISH, Language.ENGLISH);

		         System.out.println(vLemma + " TIRA FINAL " + vLemmaTranslated);
	         }
	                  	         
	         TextDatabase.updateTokenEnglishLemma(conn, vLemma, vLemmaTranslated, p_book_id);
	    }
  }
  





//STEP 8) ===============================================================================================//
//------- Boostrapping to lean adjectives

  private static void Bootstrapping(Connection conn, Integer pBook ) { 
	    /* Bootstrapping algorithm

	     * - Description: 

	     * - Revision History:
	     *     30/04/2015 - Maria Aux. Mora
	     *     
	     * - Arguments (input / output):
	     *    conn : database opened connection.
	     *    pBook : Book id. 
	     * 
	     * - Return Values:
	     *     Table Knowledge updated with new words.   
		*/
	  	  
	  
     	learnAjectivesH3(conn, pBook);  // H3 -> Adjectives concatenated by 'a'.   Process N, A, V
	      
	    learnAjectivesH4 (conn, pBook); // H4 -> Adjectives concatenated by 'u' or 'o'.  The procedure runs only one time,  
                                        //       repetitions are needed. 
	  
	    assignTypestoTokensH5 (conn, pBook); // H5 -> Join Token and knowledge using the filed lemma to assign a type to tokens 
	                                          //        that do not have it but the lemma is equal to other word that has it.
	                                          //        The procedure runs only one time, repetitions are needed after results evaluation.
   
	    learnStructuresFromAdjectivesH6(conn, pBook); //H6 name + adjective => that name is an structure.
	     
	    assignTypestoTokensH5 (conn, pBook);   // H5 -> Join Token and knowledge using the filed lemma to assign a type to tokens 
                                                //        that do not have it but the lemma is equal to other word that has it.
                                                //        The procedure runs only one time, repetitions are needed after results evaluation.
	     
	    learnStructuresH7(conn, pBook);        // H7 learn structures concatenated by 'o', 'u' 
	     
	    processWordsWithoutTypeUsingAdjectiveWithDashH0(conn,  pBook);
	  }
  
  private static void postBootstrapping(Connection conn, Integer pBook ) { 
	    /* Post-Bootstrapping algorithm

	     * - Description: For token without type:
	     *      If pos like 'N' => Structures
	     *      If pos like 'V' => Verbs

	     * - Revision History:
	     *     16/11/2015 - Maria Aux. Mora
	     *     
	     * - Arguments (input / output):
	     *    conn : database opened connection.
	     *    pBook : Book id. 
	     * 
	     * - Return Values:
	     *     Table Knowledge updated with new words.   
		*/
	  	  
	 	     
	     processTheRestOfTokensH8(conn,  pBook);  
	  }
  
  
  private static void correctKnowledge(Connection conn ) { 
	    /* Check the knowledge base (all records with revision_level = 0). 

	     * - Description: 
	     *          
	     * - Revision History:
	     *     15/11/2015 - Maria Aux. Mora
	     *     
	     * - Arguments (input / output):
	     *    conn : database opened connection.
	     * 
	     * - Return Values:
	     *     Table text.knowledge reviewed and updated).   
		*/
			  
	  try{
		   ResultSet rs = TextDatabase.selectKnowledgeRevisionLevel(conn, 0);  // Return knowledge with revision_level = 0.
 		    		    	    
		   Integer  vId ;           // Knowledge.id   
		   String   vPhrase= null;  // Knowledge.phrase    
		   String  vType = null;    // Knowledge.type
		   
	       BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		   
		   while (rs.next()) {  
		    	
			      vId = rs.getInt(1);                // Knowledge.id   
			      vPhrase = rs.getString(2);         // Knowledge.phrase    
			      vType = rs.getString(4);           // knowledge.type
		    	
		    	/* The process display the database contents.
		    	 * and read any change in tytpe.  
		    	*/
			     System.out.println("Verifique que el token token/type fue bien tipificado "+  vPhrase + " / "+ vType);
		         String s = br.readLine();
			     
		         if  (vType!= null &&  vType.equals(s)) {
		        	 System.out.println("SON IGUALES" + s); 
		         }	 
		         else { 
		        	 if (s != null && !s.equals("") && !s.equals(" ") ) {
		                System.out.println("Nuevo  type = " + s); 
		                TextDatabase.updateTokenType (conn, vId, s, 2);
		        	 } else {
			                System.out.println("Revision_level = 2" ); 
			                TextDatabase.updateTokenRevisionLevel (conn, vId, 2);
		        	 }
		         } 

	         }  // while   
			    
		    }
		    catch(Exception es){
		        es.printStackTrace();
		   } 
	  }

  
  
  private static void learnStructuresFromAdjectivesH6(Connection conn, Integer pBook) { 
	    /* bootstrapping algorithm - Learn Structures.
	     * If a name is next to an adjective then the name may be an structure

	     * - Description: 
	     *     H6: A name + an adjective (type = A) => the name as an structure.
	     *         The result must be checked by a user.
	     *                         
	     *   Revision History:
	     *     15/11/2015 - Maria Aux. Mora
	     *     
	     * - Arguments (input / output):
	     *    conn : database opened connection.
	     *    pBook: book identifier. 
	     * 
	     * - Return Values:
	     *     Table text.Knowledge updated with new words.   
		*/
	  try{
		    /* Access text.token joined text.knowledge and extract records with pos like 'N%' or pos like 'A%'. 
		    */
		    ResultSet rs = TextDatabase.SelectofTokenNamesAndAdjectives(conn, pBook);
		    		    
		    Integer totalLearnt = 0;
		    
		    // t.taxon_description_id, t.line_number, t.sequence, t.token, t.lemma, t.pos, k.type
		    
		    Integer vTaxonDescriptionId1 = null; //Taxon_description.id
		    Integer vLineNumber1 = null;         //Token.line_number
		    Integer vSequence1 = null;           //Token.sequence
		    String  vToken1 = null;           // Token.token   
		    String  vLemma1 = null;           // Token.lemma    
		    String  vPos1 = null;             // Token.pos
		    String  vType1 = null;            // knowledge.type 
		    String  vEnglishLemma1 = null; 
		    
		    Integer vTaxonDescriptionId2 = null ; //Taxon_description.id
		    Integer vLineNumber2= null;         //Token.line_number
		    Integer vSequence2 = null;           //Token.sequence
		    String  vToken2 = null;           // Token.token   
		    String  vLemma2 = null;           // Token.lemma    
		    String  vPos2 = null;             // Token.pos
		    String  vType2 = null;            // knowledge.type 
		    String  vEnglishLemma2 = null; 
		    
		    Boolean read1 = true;

		    
		    /* The process fetch a token and if the token is a Name verify if next record is an Adjective and 
		     * sequence1 +1 = sequence2 (the first is next to the second one).  */
		    // Record structure:
		    //  1) t.taxon_description_id:   Taxon description identifier
		    //  2) t.line_number:  taxon description line number.
		    //  3) t.sequence:  Token sequence inside a clause
		    //  4) t.token
		    //  5) t.lemma 
		    //  6) t.pos:  Part-of-speech tag assigned for Freeling to token 
		    //  7) k.type: Manually reviewed token type.  It comes from text.Knowledge.
		    //  8) k.english_lemma:  Lemma translation into English using Google translator API		    	
		    while (rs.next()) {  
		    	
		    	/* Which record to replace the last read or the one before. 
		    	*/
		    	if (read1) { 
			       vTaxonDescriptionId1 = rs.getInt(1); //Taxon_description.id
			       vLineNumber1 = rs.getInt(2);         //Token.line_number
			       vSequence1 = rs.getInt(3);           //Token.sequence
			       vToken1 = rs.getString(4);           // Token.token   
			       vLemma1 = rs.getString(5);           // Token.lemma    
			       vPos1 = rs.getString(6);             // Token.pos
			       vType1 = rs.getString(7);            // knowledge.type 
			       vEnglishLemma1 = rs.getString(8);  
		    	} else {
			         vTaxonDescriptionId2 = rs.getInt(1); //Taxon_description.id
			         vLineNumber2 = rs.getInt(2);         //Token.line_number
			         vSequence2 = rs.getInt(3);           //Token.sequence
			         vToken2 = rs.getString(4);           // Token.token   
			         vLemma2 = rs.getString(5);           // Token.lemma    
			         vPos2 = rs.getString(6);             // Token.pos
			         vType2 = rs.getString(7);            // knowledge.type 
			         vEnglishLemma2 = rs.getString(8);  
		    	}
		    		
		       if (vPos1 != null && vPos1.substring(0, 1).equals("N") && vType1 == null) {
		   
		    	   // Verify if next record is an adjective an if sequence2 = sequence1 + 1 (is next to current word). 
	
		    	   if (read1) {  
			    	   if (rs.next()) {
			    	   }  else break;   
			            vTaxonDescriptionId2 = rs.getInt(1); //Taxon_description.id
			            vLineNumber2 = rs.getInt(2);         //Token.line_number
			            vSequence2 = rs.getInt(3);           //Token.sequence
			            vToken2 = rs.getString(4);           // Token.token   
			            vLemma2 = rs.getString(5);           // Token.lemma    
			            vPos2 = rs.getString(6);             // Token.pos
			            vType2 = rs.getString(7);            // knowledge.type 
			            vEnglishLemma2 = rs.getString(8);  
		    		 }
		    	     read1 = true;
			         if (vType2 !=null && (vType2.equals("A") && vTaxonDescriptionId1.equals(vTaxonDescriptionId2) &&
			        		 (vLineNumber1.equals(vLineNumber2)) && (vSequence1 + 1 == vSequence2)) ) {
			        	 
					       System.out.println("Pos 1 :" + vPos1); 
				       	   System.out.println("Pos 2 :" + vPos2); 
				    	   System.out.println("Type 1 :" + vType1); 
				       	   System.out.println("Type 2 :" + vType2); 
				       	   System.out.println("Token 1 :" + vToken1); 
				       	   System.out.println("Token 2 :" + vToken2);
				       	       
				       	   System.out.println("TD Id 2 :" + vTaxonDescriptionId2);
				       	   System.out.println("TD Id 1 :" + vTaxonDescriptionId1);
				       	   System.out.println("Line 1 :" + vLineNumber1);
				       	   System.out.println("Line 2 :" + vLineNumber2);
				       	      
				           totalLearnt++;
		   		           TextDatabase.insertKnowledge(conn, vToken1, vLemma1, vEnglishLemma1, vPos1, "E", null,0);  	 
			         } else {
			        	 if (vPos2 != null && vPos2.substring(0, 1).equals("N")&& vType2 == null) {
			        	    vTaxonDescriptionId1 = vTaxonDescriptionId2; //Taxon_description.id
			        	    vLineNumber1 = vLineNumber2 ;         //Token.line_number
			        	    vSequence1 =  vSequence2;           //Token.sequence
			        	    vToken1 = vToken2;           // Token.token   
				            vLemma1 = vLemma2 ;           // Token.lemma    
				            vPos1 = vPos2 ;             // Token.pos
				            vType1 = vType2 ;            // knowledge.type 
				            vEnglishLemma1 = vEnglishLemma2 ;  
				         
				            read1 = false;
			        	 }
			         }
			         
			      
		       } 
		           	     	    
	            System.out.println("Total palabras aprendidas "+totalLearnt);
		    }
    	  }
 		catch(Exception es){
		        es.printStackTrace();
	   } 
  }
    
  
  private static void learnStructuresChunkInitH6(Connection conn, Integer pBook) { 
	    /* bootstrapping algorithm - Learn Structures

	     * - Description: 
	     *     H3: For each chunk that has a name at the beginig + an adjective (type = A) define this name as an structure.
	     *         Very good results.
	     *                         
	     *   Revision History:
	     *     3/05/2015 - Maria Aux. Mora
	     *     
	     * - Arguments (input / output):
	     *    conn : database opened connection.
	     *    pBook: book identifier. 
	     * 
	     * - Return Values:
	     *     Table text.Knowledge updated with new words.   
		*/
	  try{
		    /* Access text.token joined text.knowledge and extract the first and second words of every chunk. 
		    */
	        // Fill out rs with records to avoid compilation errors.  Inifficiently but temp.     	  
		    ResultSet rs = TextDatabase.SelectTokenByChunk(conn, pBook);
		    		    
		    Integer totalLearnt = 0;
		    Integer recordToReplace = 2; 
		    
		    Integer vTaxonDescriptionId1 = null; //Taxon_description.id
		    Integer vLineNumber1 = null;         //Token.line_number
		    Integer vSequence1 = null;           //Token.sequence
		    String  vToken1 = null;           // Token.token   
		    String  vLemma1 = null;           // Token.lemma    
		    String  vPos1 = null;             // Token.pos
		    String  vType1 = null;            // knowledge.type 
		    String  vEnglishLemma1 = null;    // Token.english_lemma 
		    
		    Integer vTaxonDescriptionId2 = null ; //Taxon_description.id
		    Integer vLineNumber2= null;         //Token.line_number
		    Integer vSequence2 = null;           //Token.sequence
		    String  vToken2 = null;           // Token.token   
		    String  vLemma2 = null;           // Token.lemma    
		    String  vPos2 = null;             // Token.pos
		    String  vType2 = null;            // knowledge.type 
		    String  vEnglishLemma2 = null;    // Token.english_lemma 
		    
		    /* The process initially fetch 2 token that are afetr a , */ 
		    /* them continues reading the next one and comparing it with the one read before
		     * until the end of the file.  */
		    // Record structure:
		    //  1) t.taxon_description_id:   Taxon description identifier
		    //  2) t.line_number:  taxon description line number.
		    //  3) t.sequence:  Token sequence inside a clause
		    //  4) t.token
		    //  5) t.lemma 
		    //  6) t.pos:  Part-of-speech tag assigned for Freeling to token 
		    //  7) k.type: Manually reviewed token type.  It comes from text.Knowledge.
		    //  8) k.english_lemma:  Lemma translation into English using Google translator API		    	
		    if (rs.next()) { 	
		       vTaxonDescriptionId1 = rs.getInt(1); //Taxon_description.id
		       vLineNumber1 = rs.getInt(2);         //Token.line_number
		       vSequence1 = rs.getInt(3);           //Token.sequence
		       vToken1 = rs.getString(4);           // Token.token   
		       vLemma1 = rs.getString(5);           // Token.lemma    
		       vPos1 = rs.getString(6);             // Token.pos
		       vType1 = rs.getString(7);            // knowledge.type 
		       vEnglishLemma1 = rs.getString(8);    // Token.english_lemma 
		    }
		    while (rs.next()) {  
		    	
		    	/* Which record to replace the last read or the one before. 
		    	*/
		       if (recordToReplace.equals(2)) {
		   
			       vTaxonDescriptionId2 = rs.getInt(1); //Taxon_description.id
			       vLineNumber2 = rs.getInt(2);         //Token.line_number
			       vSequence2 = rs.getInt(3);           //Token.sequence
			       vToken2 = rs.getString(4);           // Token.token   
			       vLemma2 = rs.getString(5);           // Token.lemma    
			       vPos2 = rs.getString(6);             // Token.pos
			       vType2 = rs.getString(7);            // knowledge.type 
			       vEnglishLemma2 = rs.getString(8);    // Token.english_lemma 
			       recordToReplace = 1; 
			       
		       } else {
		    	  vTaxonDescriptionId1 = rs.getInt(1); //Taxon_description.id
			      vLineNumber1 = rs.getInt(2);         //Token.line_number
			      vSequence1 = rs.getInt(3);           //Token.sequence
			      vToken1 = rs.getString(4);           // Token.token   
			      vLemma1 = rs.getString(5);           // Token.lemma    
			      vPos1 = rs.getString(6);             // Token.pos
			      vType1 = rs.getString(7);            // knowledge.type 
			      vEnglishLemma1 = rs.getString(8);    // Token.english_lemma 
		    	  recordToReplace = 2; 
		       }
			   if (vType1 == null) vType1 = " ";
			   if (vType2 == null) vType2 = " ";
			       
		       System.out.println("Pos 1 :" + vPos1); 
         	   System.out.println("Pos 2 :" + vPos2); 
      	       System.out.println("Type 1 :" + vType1); 
         	   System.out.println("Type 2 :" + vType2); 
         	   System.out.println("Token 1 :" + vToken1); 
         	   System.out.println("Token 2 :" + vToken2);
         	       
         	   System.out.println("TD Id 2 :" + vTaxonDescriptionId2);
         	   System.out.println("TD Id 1 :" + vTaxonDescriptionId1);
         	   System.out.println("Line 1 :" + vLineNumber1);
         	   System.out.println("Line 2 :" + vLineNumber2);
         	      
             /* Check if taxon_Description_id and line_number are equal to be sure that both words are at
              * the same clause.  Additionally verify the distance between word1 and word2 (the distance must be = 1)
              * */	
         	    Integer sequence1DistantSequence2 = vSequence1 - vSequence2;
         	    System.out.println("La distancia es: "+ Math.abs(sequence1DistantSequence2) );
			      
         	    if (vTaxonDescriptionId2.equals(vTaxonDescriptionId1) && vLineNumber2.equals(vLineNumber1) &&
			      	  (Math.abs(sequence1DistantSequence2) == 1)) {
			    	
         	    	System.out.println("Los codigo son iguales y la distancia es igual a 1" );
			    	  
			         // If (type1 or type2 are Adjectives) and (type1 and type2 are null)
		      	     if (((vType1.substring(0, 1).equals("A")) || (vType2.substring(0, 1).equals("A"))) &&
		      			(( vPos1.substring(0, 1).equals("N")) && (vType1.substring(0, 1).equals(" ")) ||
		      		     ( vPos2.substring(0, 1).equals("N")) && (vType2.substring(0, 1).equals(" "))))  {   	  
			    	  
		      	    	 System.out.println("Los tipos cumplen y pod cumplen" );
		              			    	  
		      		     System.out.println("Fin de evaluacion"); 
		           	      
		      		     totalLearnt++;
		      		     
		      		      if (vSequence1 < vSequence2){ 
	       		             TextDatabase.insertKnowledge(conn, vToken1, vLemma1, vEnglishLemma1, vPos1, "E", null,0);
		      		      } else {
	        		         TextDatabase.insertKnowledge(conn, vToken2, vLemma2, vEnglishLemma2, vPos2, "E", null,0);
		      		      } 
                         }
		      	        } // if taxon_description_id and line_number 1 and 2 are the same
		            
			        //Scanner keyboard = new Scanner(System.in);
	           	    //System.out.println("enter an integer");
	           	    // int myint = keyboard.nextInt();
	              }  // while   
			    
	        System.out.println("Total palabras aprendidas "+totalLearnt);
		    }
		    catch(Exception es){
		        es.printStackTrace();
		   } 
	  }
  
   
  
  
  private static void learnAjectivesH3(Connection conn, Integer pBook) { 
	    /* bootstrapping algorithm - Learn Adjectives

	     * - Description: 
	     *     H3: For each clause that includes preposition 'a' 
	     *     evaluate tokens that are before and after the connector:
	     *                    
	     *        if (knowledge.type of the first token or knowledge.type of the second token) = 'A' and
	     *           (knowledge.type of the first token and knowledge.type of the second token) not in ('N', 'V')
	     *           (at least one of them is an adjective and none of them is a verb or noun)
	     *           if (pos of token 1 and pos2 ) in ('A', 'N', 'V') 
	     *               (Freeling labeled them as Adjective, Name or Verb) 
	     *               addKnowledge(token1 as an Adjective)
	     *               addKnowledge(token2 as an Adjective)
	     *
	     *     Very good results for Names.           
	     * - Revision History:
	     *     24/04/2015 - Maria Aux. Mora
	     *     
	     * - Arguments (input / output):
	     *    conn : database opened connection.
	     * 
	     * - Return Values:
	     *     Table text.Knowledge updated with new words.   
		*/
	  try{
		  
		  // To repeat the process while TotalLearnt = 0 or if repetition > 20
		  Integer totalLearnt = 1;
		  Integer repetition = 0;
		  
		  while (totalLearnt >0 && repetition < 50 ) {
		    /* Access text.token joined text.knowledge and extract the clauses that include 
		     'a' 
		    */
	        // Fill out rs with records to avoid compilation errors.  Inifficiently but temp.     	  
		    ResultSet rs = TextDatabase.SelectTokenByS_a(conn, pBook);
		    		    
		    totalLearnt = 0;
		    Integer recordToReplace = 2; 
		    
		    Integer vTaxonDescriptionId1 = null; //Taxon_description.id
		    Integer vLineNumber1 = null;         //Token.line_number
		    Integer vSequence1 = null;           //Token.sequence
		    String  vToken1 = null;           // Token.token   
		    String  vLemma1 = null;           // Token.lemma    
		    String  vPos1 = null;             // Token.pos
		    String  vType1 = null;            // knowledge.type 
		    String  vEnglishLemma1 = null;    // Token.english_lemma 
		    
		    Integer vTaxonDescriptionId2 = null ; //Taxon_description.id
		    Integer vLineNumber2= null;         //Token.line_number
		    Integer vSequence2 = null;           //Token.sequence
		    String  vToken2 = null;           // Token.token   
		    String  vLemma2 = null;           // Token.lemma    
		    String  vPos2 = null;             // Token.pos
		    String  vType2 = null;            // knowledge.type 
		    String  vEnglishLemma2 = null;    // Token.english_lemma 
		    
		    /* The process initially fetch 2 token that are between 'a' */ 
		    /* them continues reading the next one and comparing it with the one read before
		     * until the end of the file.  */
		    // Record structure:
		    //  1) t.taxon_description_id:   Taxon description identifier
		    //  2) t.line_number:  taxon description line number.
		    //  3) t.sequence:  Token sequence inside a clause
		    //  4) t.token
		    //  5) t.lemma 
		    //  6) t.pos:  Part-of-speech tag assigned for Freeling to token 
		    //  7) k.type: Manually reviewed token type.  It comes from text.Knowledge.
		    //  8) k.english_lemma:  Lemma translation into English using Google translator API		    	
		    if (rs.next()) { 	
		       vTaxonDescriptionId1 = rs.getInt(1); //Taxon_description.id
		       vLineNumber1 = rs.getInt(2);         //Token.line_number
		       vSequence1 = rs.getInt(3);           //Token.sequence
		       vToken1 = rs.getString(4);           // Token.token   
		       vLemma1 = rs.getString(5);           // Token.lemma    
		       vPos1 = rs.getString(6);             // Token.pos
		       vType1 = rs.getString(7);            // knowledge.type 
		       vEnglishLemma1 = rs.getString(8);    // Token.english_lemma 
		    }
		    while (rs.next()) {  
		    	
		    	/* Which record to replace the last read or the one before.  The process compare two
		    	* records at a time but in some cases the one in the middle could be repeated.  For example:
		    	* in (3,5)"hojas simples , alternas , elípticas a lanceolado-elípticas o amplio-obovadas"
		    	* the adjective lanceolado-elípticas is in the middle of two connectors ('a','o'). 
		    	*/
		       if (recordToReplace.equals(2)) {
		   
			       vTaxonDescriptionId2 = rs.getInt(1); //Taxon_description.id
			       vLineNumber2 = rs.getInt(2);         //Token.line_number
			       vSequence2 = rs.getInt(3);           //Token.sequence
			       vToken2 = rs.getString(4);           // Token.token   
			       vLemma2 = rs.getString(5);           // Token.lemma    
			       vPos2 = rs.getString(6);             // Token.pos
			       vType2 = rs.getString(7);            // knowledge.type 
			       vEnglishLemma2 = rs.getString(8);    // Token.english_lemma 
			       recordToReplace = 1; 
			       
		       } else {
		    	  vTaxonDescriptionId1 = rs.getInt(1); //Taxon_description.id
			      vLineNumber1 = rs.getInt(2);         //Token.line_number
			      vSequence1 = rs.getInt(3);           //Token.sequence
			      vToken1 = rs.getString(4);           // Token.token   
			      vLemma1 = rs.getString(5);           // Token.lemma    
			      vPos1 = rs.getString(6);             // Token.pos
			      vType1 = rs.getString(7);            // knowledge.type 
			      vEnglishLemma1 = rs.getString(8);    // Token.english_lemma 
		    	  recordToReplace = 2; 
		       }
			   if (vType1 == null) vType1 = " ";
			   if (vType2 == null) vType2 = " ";
			       
		       System.out.println("Pos 1 :" + vPos1); 
         	   System.out.println("Pos 2 :" + vPos2); 
      	       System.out.println("Type 1 :" + vType1); 
         	   System.out.println("Type 2 :" + vType2); 
      	       System.out.println("Token 1 :" + vToken1); 
         	   System.out.println("Token 2 :" + vToken2);
         	       
         	   System.out.println("TD Id 2 :" + vTaxonDescriptionId2);
         	   System.out.println("TD Id 1 :" + vTaxonDescriptionId1);
         	   System.out.println("Line 1 :" + vLineNumber1);
         	   System.out.println("Line 2 :" + vLineNumber2);
         	      
             /* Check if taxon_Description_id and line_number are equal to be sure that both words are at
              * the same clause.  Additionally verify the distance between word1 and word2 (the distance must be = 2)
              * */	
         	    Integer sequence1DistantSequence2 = vSequence1 - vSequence2;
         	    System.out.println("La distancia es: "+ Math.abs(sequence1DistantSequence2) );
			      
         	    if (vTaxonDescriptionId2.equals(vTaxonDescriptionId1) && vLineNumber2.equals(vLineNumber1) &&
			      	  (Math.abs(sequence1DistantSequence2) == 2)) {
			    	
         	    	System.out.println("Los codigo son iguales y la distancia es igual a 1" );
			    	  
			         // If (type1 or type2 are Adjectives) and (type1 and type2 are not verbs or names)
		      	     if (((vType1.substring(0, 1).equals("A")) || (vType2.substring(0, 1).equals("A"))) &&
		      			(( vType1.substring(0, 1).equals(" ")) || (vType2.substring(0, 1).equals(" "))))  {   	  
			    	  
		      	    	 System.out.println("Los tipos cumplen" );
		      	    	 String str1 = vPos1.substring(0, 1).trim();
		      	    	 String str2 = vPos2.substring(0, 1).trim();
		      	    	
		      		     // If (pos1 and pos2) are V, A, or N. 
		         	     if ((str1.equals("A") || str1.equals("V") || str1.equals("N") ) &&  
                              (str2.equals("A") || str2.equals("V") || str2.equals("N") )){
		            			    	  
		      		       System.out.println("Fin de evaluacion"); 
		           	      
		      		       totalLearnt++;
		      		       
	       		          TextDatabase.insertKnowledge(conn, vToken1, vLemma1, vEnglishLemma1, vPos1, "A", null, 0);
	        		      TextDatabase.insertKnowledge(conn, vToken2, vLemma2, vEnglishLemma2, vPos2, "A", null, 0);
		        		   }
                       }
		      	     /* Same heuristic is applied to Structures (knowledge.type = E) but only if pos of the new word is equal to 'N'
		      	      * The code is commented because the heuristic did not work with structures.  
		      	      * */
		            /* else {
		      	    	if (vType1.substring(0, 1).equals("E") && (vType2.substring(0, 1).equals(" "))) {
		      	    		if (vPos2.substring(0, 1).equals("N")) {
			       		       TextDatabase.insertKnowledge(conn, vToken2, vLemma2, vEnglishLemma2, vPos2, "E", null);
			       		       System.out.println("Aprendi un nombre"); 
			      		       totalLearnt++;

		      	    		}
		      	    	}
		      	    	else if (vType2.substring(0, 1).equals("E") && vType1.substring(0, 1).equals(" ")) {
		      	    		if (vPos1.substring(0, 1).equals("N")) {
				       		      TextDatabase.insertKnowledge(conn, vToken1, vLemma1, vEnglishLemma1, vPos1, "E", null);
				       		      System.out.println("Aprendi un nombre"); 
				      		      totalLearnt++;
			      	    		}
		      	    		 
		      	    	     }
	                     }*/
		      	        } // if taxon_description_id and line_number 1 and 2 are the same
		            
			        //Scanner keyboard = new Scanner(System.in);
	           	    //System.out.println("enter an integer");
	           	    // int myint = keyboard.nextInt();
	              }  // while   
			    
	        System.out.println("Total palabras aprendidas "+totalLearnt);
	        repetition++;
		  } 
		 }
		 catch(Exception es){
		        es.printStackTrace();
		 } 
	  }
  
  
  
private static void learnAjectivesH4(Connection conn, Integer pBook ) { 
	    /* bootstrapping algorithm - Learn Adjectives

	     * - Description: 
	     *     H3: For each clause that includes connectors (conjunctions 'u', 'o')
	     *     evaluate tokens that are before and after the connector:
	     *                    
	     *        if (knowledge.type of the first token or knowledge.type of the second token) = 'A' and
	     *           (knowledge.type of the first token and knowledge.type of the second token) not in ('N', 'V')
	     *           (at least one of them is an adjective and none of them is a verb or noun)
	     *           if (pos of token 1 and pos2 ) in ('A', 'V') 
	     *               (Freeling labeled them as Adjective or Verb) 
	     *               addKnowledge(token1 as an Adjective)
	     *               addKnowledge(token2 as an Adjective)
	     *     
	     *              
	     * - Revision History:
	     *     24/04/2015 - Maria Aux. Mora
	     *     
	     * - Arguments (input / output):
	     *    conn : database opened connection.
	     * 
	     * - Return Values:
	     *     Table text.Knowledge updated with new words.   
		*/
	  try{
		    /* Access text.token joined text.knowledge and extract the clauses that include 
		     'o','u'. 
		    */
	       
		  // To repeat the process while TotalLearnt = 0 or if repetition > 20
		  Integer totalLearnt = 1;
		  Integer repetition = 0;
		  
		  while (totalLearnt >0 && repetition < 50 ) { 
		  
		   ResultSet rs = TextDatabase.SelectTokenByC(conn, pBook);
		    		    
		    totalLearnt = 0;
		    Integer recordToReplace = 2; 
		    
		    Integer vTaxonDescriptionId1 = null; //Taxon_description.id
		    Integer vLineNumber1 = null;         //Token.line_number
		    Integer vSequence1 = null;           //Token.sequence
		    String  vToken1 = null;           // Token.token   
		    String  vLemma1 = null;           // Token.lemma    
		    String  vPos1 = null;             // Token.pos
		    String  vType1 = null;            // knowledge.type 
		    String  vEnglishLemma1 = null;    // Token.english_lemma 
		    
		    Integer vTaxonDescriptionId2 = null ; //Taxon_description.id
		    Integer vLineNumber2= null;         //Token.line_number
		    Integer vSequence2 = null;           //Token.sequence
		    String  vToken2 = null;           // Token.token   
		    String  vLemma2 = null;           // Token.lemma    
		    String  vPos2 = null;             // Token.pos
		    String  vType2 = null;            // knowledge.type 
		    String  vEnglishLemma2 = null;    // Token.english_lemma 
		    
		    /* The process initially fetch 2 token that are between 'o','u' */ 
		    /* them continues reading the next one and comparing it with the one read before
		     * until the end of the file.  */
		    // Record structure:
		    //  1) t.taxon_description_id:   Taxon description identifier
		    //  2) t.line_number:  taxon description line number.
		    //  3) t.sequence:  Token sequence inside a clause
		    //  4) t.token
		    //  5) t.lemma 
		    //  6) t.pos:  Part-of-speech tag assigned for Freeling to token 
		    //  7) k.type: Manually reviewed token type.  It comes from text.Knowledge.
		    //  8) k.english_lemma:  Lemma translation into English using Google translator API		    	
		    if (rs.next()) { 	
		       vTaxonDescriptionId1 = rs.getInt(1); //Taxon_description.id
		       vLineNumber1 = rs.getInt(2);         //Token.line_number
		       vSequence1 = rs.getInt(3);           //Token.sequence
		       vToken1 = rs.getString(4);           // Token.token   
		       vLemma1 = rs.getString(5);           // Token.lemma    
		       vPos1 = rs.getString(6);             // Token.pos
		       vType1 = rs.getString(7);            // knowledge.type 
		       vEnglishLemma1 = rs.getString(8);    // Token.english_lemma 
		    }
		    while (rs.next()) {  
		    	
		    	/* Which record to replace the last read or the one before.  The process compare two
		    	* records at a time but in some cases the one in the middle could be repeated.  For example:
		    	* in (3,5)"hojas simples , alternas , elípticas a lanceolado-elípticas o amplio-obovadas"
		    	* the adjective lanceolado-elípticas is in the middle of two connectors ('a','o'). 
		    	*/
		       if (recordToReplace.equals(2)) {
		   
			       vTaxonDescriptionId2 = rs.getInt(1); //Taxon_description.id
			       vLineNumber2 = rs.getInt(2);         //Token.line_number
			       vSequence2 = rs.getInt(3);           //Token.sequence
			       vToken2 = rs.getString(4);           // Token.token   
			       vLemma2 = rs.getString(5);           // Token.lemma    
			       vPos2 = rs.getString(6);             // Token.pos
			       vType2 = rs.getString(7);            // knowledge.type 
			       vEnglishLemma2 = rs.getString(8);    // Token.english_lemma 
			       recordToReplace = 1; 
			       
		       } else {
		    	  vTaxonDescriptionId1 = rs.getInt(1); //Taxon_description.id
			      vLineNumber1 = rs.getInt(2);         //Token.line_number
			      vSequence1 = rs.getInt(3);           //Token.sequence
			      vToken1 = rs.getString(4);           // Token.token   
			      vLemma1 = rs.getString(5);           // Token.lemma    
			      vPos1 = rs.getString(6);             // Token.pos
			      vType1 = rs.getString(7);            // knowledge.type 
			      vEnglishLemma1 = rs.getString(8);    // Token.english_lemma 
		    	  recordToReplace = 2; 
		       }
			   if (vType1 == null) vType1 = " ";
			   if (vType2 == null) vType2 = " ";
			       
		       System.out.println("Pos 1 :" + vPos1); 
           	   System.out.println("Pos 2 :" + vPos2); 
        	   System.out.println("Type 1 :" + vType1); 
           	   System.out.println("Type 2 :" + vType2); 
        	   System.out.println("Token 1 :" + vToken1); 
           	   System.out.println("Token 2 :" + vToken2);
           	       
           	   System.out.println("TD Id 2 :" + vTaxonDescriptionId2);
           	   System.out.println("TD Id 1 :" + vTaxonDescriptionId1);
           	   System.out.println("Line 1 :" + vLineNumber1);
           	   System.out.println("Line 2 :" + vLineNumber2);
           	      
               /* Check if taxon_Description_id and line_number are equal to be sure that both words are at
                * the same clause.  Additionally verify the distance between word1 and word2 (the distance must be = 2)
                * */	
           	    Integer sequence1DistantSequence2 = vSequence1 - vSequence2;
           	    System.out.println("La distancia es: "+ Math.abs(sequence1DistantSequence2) );
			      
           	    if (vTaxonDescriptionId2.equals(vTaxonDescriptionId1) && vLineNumber2.equals(vLineNumber1) &&
			      	  (Math.abs(sequence1DistantSequence2) == 2)) {
			    	
           	    	System.out.println("Los codigo son iguales y la distancia es igual a 1" );
			    	  
			         // If (type1 or type2 are Adjectives) and (type1 and type2 are not verbs or names)
		      	     if (((vType1.substring(0, 1).equals("A")) || (vType2.substring(0, 1).equals("A"))) &&
		      			(( vType1.substring(0, 1).equals(" ")) || (vType2.substring(0, 1).equals(" "))))  {   	  
			    	  
		      	    	 System.out.println("Los tipos cumplen" );
		      	    	 String str1 = vPos1.substring(0, 1).trim();
		      	    	 String str2 = vPos2.substring(0, 1).trim();
		      	    	
		      		     // If (pos1 and pos2) are V or A. 
		         	     if ((str1.equals("A") || str1.equals("V")  ) &&  
                                (str2.equals("A") || str2.equals("V")  )){
		            			    	  
		      		       System.out.println("Fin de evaluacion"); 
		           	      
		      		       totalLearnt++;
		      		       
	       		          TextDatabase.insertKnowledge(conn, vToken1, vLemma1, vEnglishLemma1, vPos1, "A", null, 0);
	        		      TextDatabase.insertKnowledge(conn, vToken2, vLemma2, vEnglishLemma2, vPos2, "A", null, 0);
		        		   }
                         }
		      	        } // if taxon_description_id and line_number 1 and 2 are the same
		            
			        //Scanner keyboard = new Scanner(System.in);
	           	    //System.out.println("enter an integer");
	           	    // int myint = keyboard.nextInt();
	              }  // while   
			    
	        System.out.println("Total palabras aprendidas "+totalLearnt);
	        repetition ++;
		    }
	  }
      catch(Exception es){
    	  es.printStackTrace();
      } 
}





private static void assignTypestoTokensH5(Connection conn, Integer pBook ) { 
    /* bootstrapping algorithm - Learn Adjectives and Estructures

     * - Description: 
     *     H5: Join Token and knowledge using the filed lemma to assign a type to tokens 
	 *     that do not have it but the lemma is equal to other word that has it.
	 *     The procedure runs only one time, repetitions are needed after results evaluation.
     *          
     * - Revision History:
     *     1/05/2015 - Maria Aux. Mora
     *     
     * - Arguments (input / output):
     *    conn : database opened connection.
     *    pBook: book identifier. 
     * 
     * - Return Values:
     *     Table text.Knowledge updated with new words.   
	*/
	
  try{
	    /* Access text.token joined text.knowledge and extract all tokens.  The join is done using lemma.   
	    */
	  
	Integer totalLearnt = 0;
	  
	   ResultSet rs = TextDatabase.SelectTokenWithType(conn, pBook);
	    		    
	    
	   Integer vTaxonDescriptionId1 = null; //Taxon_description.id
	   Integer vLineNumber1 = null;         //Token.line_number
	   Integer vSequence1 = null;           //Token.sequence
	   String  vToken1 = null;           // Token.token   
	   String  vLemma1 = null;           // Token.lemma    
	   String  vPos1 = null;             // Token.pos
	   String  vType1 = null;            // knowledge.type 
	   String  vEnglishLemma1 = null;    // Token.english_lemma 
	    // Record structure:
	    //  1) t.taxon_description_id:   Taxon description identifier
	    //  2) t.line_number:  taxon description line number.
	    //  3) t.sequence:  Token sequence inside a clause
	    //  4) t.token
	    //  5) t.lemma 
	    //  6) t.pos:  Part-of-speech tag assigned for Freeling to token 
	    //  7) k.type: Manually reviewed token type.  It comes from text.Knowledge.
	    //  8) k.english_lemma:  Lemma translation into English using Google translator API		    	
	    while (rs.next()) {  
	    	
	    	  vTaxonDescriptionId1 = rs.getInt(1); //Taxon_description.id
		      vLineNumber1 = rs.getInt(2);         //Token.line_number
		      vSequence1 = rs.getInt(3);           //Token.sequence
		      vToken1 = rs.getString(4);           // Token.token   
		      vLemma1 = rs.getString(5);           // Token.lemma    
		      vPos1 = rs.getString(6);             // Token.pos
		      vType1 = rs.getString(7);            // knowledge.type 
		      vEnglishLemma1 = rs.getString(8);    // Token.english_lemma 
	    	
	    	/* The process save each new record in the table text.knowledge only if type is not null.  
	    	*/
	       if (( vType1 != null) ) {
	   
       		   TextDatabase.insertKnowledge(conn, vToken1, vLemma1, vEnglishLemma1, vPos1, vType1, null,0);
       		   totalLearnt ++;
       		   System.out.println("Token, lemma, type "+ vToken1 + ", "+ vLemma1 + ", " + vType1);
       		   
 	       }
               
		 //  Scanner keyboard = new Scanner(System.in);
         //  System.out.println("enter an integer");
         //  int myint = keyboard.nextInt();
         }  // while   
		    
        System.out.println("Total palabras identificadas "+totalLearnt);
      
     }
	 catch(Exception es){
		 es.printStackTrace();
	 } 
  }

private static void learnStructuresH7(Connection conn, Integer pBook ) { 
    /* bootstrapping algorithm - Learn structures

     * - Description: 
     *     H3: For each clause that includes connectors (conjunctions 'u', 'o')
     *     evaluate tokens that are before and after the connector:
     *                    
     *        if (knowledge.type of the first token or knowledge.type of the second token) = 'E' and
     *           (knowledge.type of the first token and knowledge.type of the second token)  in ('N')
     *           (at least one of them is an structure and none of them is a  noun)
     *           if (pos of token 1 and pos2 ) in ('N') 
     *               (Freeling labeled them as name) 
     *               addKnowledge(token1 as an Structure)
     *               addKnowledge(token2 as an Structure)
     *     
     *              
     * - Revision History:
     *     16/11/2015 - Maria Aux. Mora
     *     
     * - Arguments (input / output):
     *    conn : database opened connection.
     * 
     * - Return Values:
     *     Table text.Knowledge updated with new words.   
	*/
  try{
	    /* Access text.token joined text.knowledge and extract the clauses that include 
	     'o','u'. 
	    */
       
	  // To repeat the process while TotalLearnt = 0 or if repetition > 50
	  Integer totalLearnt = 1;
	  Integer repetition = 0;
	  
	  while (totalLearnt >0 && repetition < 50 ) { 
	  
	   ResultSet rs = TextDatabase.SelectTokenByC(conn, pBook);
	    		    
	    totalLearnt = 0;
	    Integer recordToReplace = 2; 
	    
	    Integer vTaxonDescriptionId1 = null; //Taxon_description.id
	    Integer vLineNumber1 = null;         //Token.line_number
	    Integer vSequence1 = null;           //Token.sequence
	    String  vToken1 = null;           // Token.token   
	    String  vLemma1 = null;           // Token.lemma    
	    String  vPos1 = null;             // Token.pos
	    String  vType1 = null;            // knowledge.type 
	    String  vEnglishLemma1 = null;    // Token.english_lemma 
	    
	    Integer vTaxonDescriptionId2 = null ; //Taxon_description.id
	    Integer vLineNumber2= null;         //Token.line_number
	    Integer vSequence2 = null;           //Token.sequence
	    String  vToken2 = null;           // Token.token   
	    String  vLemma2 = null;           // Token.lemma    
	    String  vPos2 = null;             // Token.pos
	    String  vType2 = null;            // knowledge.type 
	    String  vEnglishLemma2 = null;    // Token.english_lemma 
	    
	    /* The process initially fetch 2 token that are between 'o','u' */ 
	    /* them continues reading the next one and comparing it with the one read before
	     * until the end of the file.  */
	    // Record structure:
	    //  1) t.taxon_description_id:   Taxon description identifier
	    //  2) t.line_number:  taxon description line number.
	    //  3) t.sequence:  Token sequence inside a clause
	    //  4) t.token
	    //  5) t.lemma 
	    //  6) t.pos:  Part-of-speech tag assigned for Freeling to token 
	    //  7) k.type: Manually reviewed token type.  It comes from text.Knowledge.
	    //  8) k.english_lemma:  Lemma translation into English using Google translator API		    	
	    if (rs.next()) { 	
	       vTaxonDescriptionId1 = rs.getInt(1); //Taxon_description.id
	       vLineNumber1 = rs.getInt(2);         //Token.line_number
	       vSequence1 = rs.getInt(3);           //Token.sequence
	       vToken1 = rs.getString(4);           // Token.token   
	       vLemma1 = rs.getString(5);           // Token.lemma    
	       vPos1 = rs.getString(6);             // Token.pos
	       vType1 = rs.getString(7);            // knowledge.type 
	       vEnglishLemma1 = rs.getString(8);    // Token.english_lemma 
	    }
	    while (rs.next()) {  
	    	
	    	/* Which record to replace the last read or the one before.  The process compare two
	    	* records at a time but in some cases the one in the middle could be repeated.  For example:
	    	* in (3,5)"hojas simples , alternas , elípticas a lanceolado-elípticas o amplio-obovadas"
	    	* the adjective lanceolado-elípticas is in the middle of two connectors ('a','o'). 
	    	*/
	       if (recordToReplace.equals(2)) {
	   
		       vTaxonDescriptionId2 = rs.getInt(1); //Taxon_description.id
		       vLineNumber2 = rs.getInt(2);         //Token.line_number
		       vSequence2 = rs.getInt(3);           //Token.sequence
		       vToken2 = rs.getString(4);           // Token.token   
		       vLemma2 = rs.getString(5);           // Token.lemma    
		       vPos2 = rs.getString(6);             // Token.pos
		       vType2 = rs.getString(7);            // knowledge.type 
		       vEnglishLemma2 = rs.getString(8);    // Token.english_lemma 
		       recordToReplace = 1; 
		       
	       } else {
	    	  vTaxonDescriptionId1 = rs.getInt(1); //Taxon_description.id
		      vLineNumber1 = rs.getInt(2);         //Token.line_number
		      vSequence1 = rs.getInt(3);           //Token.sequence
		      vToken1 = rs.getString(4);           // Token.token   
		      vLemma1 = rs.getString(5);           // Token.lemma    
		      vPos1 = rs.getString(6);             // Token.pos
		      vType1 = rs.getString(7);            // knowledge.type 
		      vEnglishLemma1 = rs.getString(8);    // Token.english_lemma 
	    	  recordToReplace = 2; 
	       }
		   if (vType1 == null) vType1 = " ";
		   if (vType2 == null) vType2 = " ";
		       
	       System.out.println("Pos 1 :" + vPos1); 
       	   System.out.println("Pos 2 :" + vPos2); 
    	   System.out.println("Type 1 :" + vType1); 
       	   System.out.println("Type 2 :" + vType2); 
    	   System.out.println("Token 1 :" + vToken1); 
       	   System.out.println("Token 2 :" + vToken2);
       	       
       	   System.out.println("TD Id 2 :" + vTaxonDescriptionId2);
       	   System.out.println("TD Id 1 :" + vTaxonDescriptionId1);
       	   System.out.println("Line 1 :" + vLineNumber1);
       	   System.out.println("Line 2 :" + vLineNumber2);
       	      
           /* Check if taxon_Description_id and line_number are equal to be sure that both words are at
            * the same clause.  Additionally verify the distance between word1 and word2 (the distance must be = 2)
            * */	
       	    Integer sequence1DistantSequence2 = vSequence1 - vSequence2;
       	    System.out.println("La distancia es: "+ Math.abs(sequence1DistantSequence2) );
		      
       	    if (vTaxonDescriptionId2.equals(vTaxonDescriptionId1) && vLineNumber2.equals(vLineNumber1) &&
		      	  (Math.abs(sequence1DistantSequence2) == 2)) {
		    	
       	    	System.out.println("Los codigo son iguales y la distancia es igual a 1" );
		    	  
		         // If (type1 or type2 are Names) and (type1 and type2 are Structure)
	      	     if (((vType1.substring(0, 1).equals("E")) || (vType2.substring(0, 1).equals("E"))) &&
	      			(( vType1.substring(0, 1).equals(" ")) || (vType2.substring(0, 1).equals(" "))))  {   	  
		    	  
	      	    	 System.out.println("Los tipos cumplen" );
	      	    	 String str1 = vPos1.substring(0, 1).trim();
	      	    	 String str2 = vPos2.substring(0, 1).trim();
	      	    	
	      		     // If (pos1 and pos2) areN 
	         	     if ((str1.equals("N")  ) &&  
                            (str2.equals("N"))){
	            			    	  
	      		       System.out.println("Fin de evaluacion"); 
	           	      
	      		       totalLearnt++;
	      		       
       		          TextDatabase.insertKnowledge(conn, vToken1, vLemma1, vEnglishLemma1, vPos1, "E", null, 0);
        		      TextDatabase.insertKnowledge(conn, vToken2, vLemma2, vEnglishLemma2, vPos2, "E", null, 0);
	        		   }
                     }
	      	        } // if taxon_description_id and line_number 1 and 2 are the same
	            
		        //Scanner keyboard = new Scanner(System.in);
           	    //System.out.println("enter an integer");
           	    // int myint = keyboard.nextInt();
              }  // while   
		    
        System.out.println("Total palabras aprendidas "+totalLearnt);
        repetition ++;
	    }
  }
  catch(Exception es){
	  es.printStackTrace();
  } 
}




//STEP 9) ===============================================================================================//
//-------- Generates chunks from each clause using comma as chunk delimiter  

private static void createChunks (Connection conn, Integer p_book_id ) {
	/* - Description: Convert clauses into chunks using comas to split sentences by means of a regular expresion. 
 * - Revision History:
 *     03/04/2015 - Maria Aux. Mora
 *     
 * - Arguments (input / output):
 *    conn      : database opened connection.
 *    p_book_id :  book id used to select the clauses to be processed.
 * 
 * - Return Values:
 *     . Records saved into the text.Chunk table 
*/	
	
	try{
		
		TextDatabase.deleteChunks(conn, p_book_id);
		
	    ResultSet rs = TextDatabase.selectClause(conn, null,null, null, null, p_book_id);
	   
	    while (rs.next())
	    {  /* Clause data for the current record */ 
	 	       	
	       Integer vTaxonDescriptionId = rs.getInt(2); //Clause.taxon_description_id
	       String  vContents = rs.getString(5);        //Clause.contents   
	       Integer vLineNumber = rs.getInt(8);         //Clause.line_number
	       String[] chunkArray = vContents.split(",[^0-9]");
	       
	       System.out.println(vContents);
	       
	       for(String t : chunkArray) {
	            System.out.println("> "+t);
	        }
       
	      TextDatabase.createChunkRecords(conn,p_book_id, vTaxonDescriptionId, vLineNumber, chunkArray);
	    
	    } 
	}
catch(Exception es){
	       es.printStackTrace();
	  } 
}
  
//STEP 10) ===============================================================================================//
//--------

public static void parseClause(Connection conn, Integer p_book_id) throws InvalidFormatException, IOException {
/* - Description: uses Freeling to analize dependencies for each text.chunk record. 
 *                DEPRECATED. Update text.token_tree using CLAUSE information. 
 *				 The system is using chunks instead of clauses as the unit of processing. 
 *	
 * 
 * 
 * - Revision History:
 *     23/03/2015 - Maria Aux. Mora
 *     
 * - Arguments (input / output):
 *    conn : database opened connection.
 * 
 * - Return Values:
 *     . Fill the table text.token_tree with the results.
*/

try{ 	  
	System.load( "/pkg/freeling-3.1/APIs/java/libfreeling_javaAPI.so" );  
 	  
    Util.initLocale( "default" );

    // Create options set for maco analyzer.
    MacoOptions op = new MacoOptions( LANG );

    op.setActiveModules(false, true, true, true, 
                               true, true, true, 
                               true, true, true);
    // Data files definition
    op.setDataFiles(
      "", 
      DATA + LANG+"/locucions.dat", 
      DATA + LANG + "/quantities.dat",
      DATA + LANG + "/afixos.dat",
      DATA + LANG + "/probabilitats.dat",
      DATA + LANG + "/dicc.src",
      DATA + LANG + "/np.dat",
      DATA + "common/punct.dat");

    System.out.print( "Processando " );
    // Create analyzers.
    LangIdent lgid = new LangIdent(DATA + "/common/lang_ident/ident-few.dat");

    Tokenizer tk = new Tokenizer( DATA + LANG + "/tokenizer.dat" );
    Splitter sp = new Splitter( DATA + LANG + "/splitter.dat" );
    Maco mf = new Maco( op );
    
    
    System.out.print( sp );

    HmmTagger tg = new HmmTagger( DATA + LANG + "/tagger.dat", true, 2 );
    ChartParser parser = new ChartParser(
      DATA + LANG + "/chunker/grammar-chunk.dat" ); 
    DepTxala dep = new DepTxala( DATA + LANG + "/dep/dependences.dat",
      parser.getStartSymbol() );
    Nec neclass = new Nec( DATA + LANG + "/nerc/nec/nec-ab-poor1.dat" );

    Senses sen = new Senses(DATA + LANG + "/senses.dat" ); // sense dictionary
    Ukb dis = new Ukb( DATA + LANG + "/ukb.dat" ); // sense disambiguator
  
    // Access Clause in TEXT database. 
    // Parameters: connection, text_mining_result_id, taxon_description_id
    //  p_line_number,contents_type_id, p_book_id)
    
	ResultSet rs = TextDatabase.selectClause(conn, null, null, null, null, p_book_id);
	
    while (rs.next())
    {  /* for each Record */ 

       Integer v_id = rs.getInt(2);   /*Clause.Taxon_description_id*/
       Integer v_line_number = rs.getInt(8); // Clause.line_number 
       String  line =  rs.getString(5) + ":"; /* Clause.contents.  The : is needed for FreeLing to set the end of the chunk*/
	  
     
       // Identify language of the text.  
       String lg = lgid.identifyLanguage(line);
       System.out.println( "-------- LANG_IDENT results -----------" );
       System.out.println("Language detected (from first line in text): " + lg +" " + v_id );
       System.out.println("Linea " + line);
       if ( line != null ) {
          // Extract the tokens from the line of text.
          ListWord l = tk.tokenize( line );
      
          // Split the tokens into distinct sentences.
          ListSentence ls = sp.split( l, false );
          
          // Perform morphological analysis
          mf.analyze( ls );

          // Perform part-of-speech tagging.
          tg.analyze( ls );

          // Perform named entity (NE) classificiation.
          neclass.analyze( ls );

          sen.analyze( ls );
          dis.analyze( ls );
          
          printResults( ls, "tagged" );

          // Chunk parser
          parser.analyze( ls );
          printResults( ls, "parsed" );

          // Dependency parser
          dep.analyze( ls );
          printResults( ls, "dep" );
          
          
          // Save tokens inside tokenTreeList
         // saveResults( v_id,v_line_number, null, ls, "dep", p_book_id);
       }
       
      }  
    /* Insert result into the database */
    //TextDatabase.createTokenTreeRecords(conn, tokenTreeList );   
}
  catch(Exception es){
       es.printStackTrace();
  } 
}
  


public static void parseChunk(Connection conn, Integer p_book_id) throws InvalidFormatException, IOException {
/* - Description: uses Freeling to analize dependencies for each text.chunk record. 
     * - Revision History:
     *     23/03/2015 - Maria Aux. Mora
     *     
     * - Arguments (input / output):
     *    conn : database opened connection.
     * 
     * - Return Values:
     *     . Fill the table text.token_tree with the results.
*/

try{ 	 
	
	String  line;
	 Integer v_id ;   /*Chunk.Taxon_description_id*/
     Integer v_line_number ; // Chunk.line_number 
     Integer v_sequence;
	
	
	System.load( "/pkg/freeling-3.1/APIs/java/libfreeling_javaAPI.so" );  
 	  
    Util.initLocale( "default" );

    // Create options set for maco analyzer.
    MacoOptions op = new MacoOptions( LANG );

    op.setActiveModules(false, true, true, true, 
                               true, true, true, 
                               true, true, true);
    // Data files definition
    op.setDataFiles(
      "", 
      DATA + LANG+"/locucions.dat", 
      DATA + LANG + "/quantities.dat",
      DATA + LANG + "/afixos.dat",
      DATA + LANG + "/probabilitats.dat",
      DATA + LANG + "/dicc.src",
      DATA + LANG + "/np.dat",
      DATA + "common/punct.dat");

    System.out.print( "Processando " );
    // Create analyzers.
    LangIdent lgid = new LangIdent(DATA + "/common/lang_ident/ident-few.dat");

    Tokenizer tk = new Tokenizer( DATA + LANG + "/tokenizer.dat" );
    Splitter sp = new Splitter( DATA + LANG + "/splitter.dat" );
    Maco mf = new Maco( op );
    
    
    System.out.print( sp );

    HmmTagger tg = new HmmTagger( DATA + LANG + "/tagger.dat", true, 2 );
    ChartParser parser = new ChartParser(
      DATA + LANG + "/chunker/grammar-chunk.dat" ); 
    DepTxala dep = new DepTxala( DATA + LANG + "/dep/dependences.dat",
      parser.getStartSymbol() );
    Nec neclass = new Nec( DATA + LANG + "/nerc/nec/nec-ab-poor1.dat" );

    Senses sen = new Senses(DATA + LANG + "/senses.dat" ); // sense dictionary
    Ukb dis = new Ukb( DATA + LANG + "/ukb.dat" ); // sense disambiguator
  
    // Access Clause in TEXT database. Null = process all clauses
    
    
	ResultSet rs = TextDatabase.selectChunk(conn, p_book_id, null, null, null);
	
    while (rs.next())
    {  /* for each Record */ 

       v_id = rs.getInt(2);   /*Chunk.Taxon_description_id*/
       v_line_number = rs.getInt(3); // Chunk.line_number 
       v_sequence = rs.getInt(4); // Chunk.sequence 
       line =  rs.getString(5).trim(); // Chunk.contents.  

       // If the last character is not a puntuation mark then add : at the end of the string.  FreeLing needs a final punctuation mark.
       if (!(line.substring(line.length()-1).equals(",") ||line.substring(line.length()-1).equals(".") || 
    		   line.substring(line.length()-1).equals(";") || line.substring(line.length()-1).equals(":") ))
    	       line =  line + ":"; /* Chunk.contents.  The : is needed for FreeLing to set the end of the chunk*/
	     
       // Identify language of the text.  
       String lg = lgid.identifyLanguage(line);
       System.out.println( "-------- LANG_IDENT results -----------" );
       System.out.println("Language detected (from first line in text): " + lg +" " + v_id + "  sequence "+ v_sequence);
       System.out.println("Linea " + line);
       if ( line != null ) {
          // Extract the tokens from the line of text.
          ListWord l = tk.tokenize( line );
      
          // Split the tokens into distinct sentences.
          ListSentence ls = sp.split( l, false );
          
          // Perform morphological analysis
          mf.analyze( ls );

          // Perform part-of-speech tagging.
          tg.analyze( ls );

          // Perform named entity (NE) classificiation.
          neclass.analyze( ls );

          sen.analyze( ls );
          dis.analyze( ls );
          
          printResults( ls, "tagged" );

          // Chunk parser
          parser.analyze( ls );
          printResults( ls, "parsed" );

          // Dependency parser
          dep.analyze( ls );
          printResults( ls, "dep" );
          
          
          // Save tokens inside tokenTreeList
          saveResults( v_id,v_line_number, v_sequence, ls, "dep", p_book_id);
       }
       
      }  
    /* Insert result into the database */
    TextDatabase.createTokenTreeRecords(conn, tokenTreeList );   
}
  catch(Exception es){
       es.printStackTrace();
  } 
}


//STEP 11) ===============================================================================================//
/* Correct translation into English of Englis_lemma (table token) and update knowledge using OTO and token
 *  Parameters: connection, book_id
 */


private static void correctTranslation(Connection conn, Integer pBook ) { 
    /* Correct manually bad English translation. 

     * - Description: 
     *          
     * - Revision History:
     *     8/06/2015 - Maria Aux. Mora
     *     
     * - Arguments (input / output):
     *    conn : database opened connection.
     *    pBook: book identifier. 
     * 
     * - Return Values:
     *     Table text.token manually updated (english_lemma).   
	*/
	
  try{
	    /* Access text.token joined text.knowledge and OTO and extract all tokens which english_lemma does not match with OTO. 
	    */
	   ResultSet rs = TextDatabase.SelectTokenWhereOTONotMatch(conn, pBook);
	    		    	    
	   String  vToken1 = null;           // Token.token   
	   String  vLemma1 = null;           // Token.lemma    
	   String  vEnglishLemma1 = null;    // Token.english_lemma 
       BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
       	   
	    // Record structure:
	    //  1) t.token
	    //  2) t.lemma 
	    //  3) k.english_lemma:  Lemma translation into English using Google translator API		    	
	    while (rs.next()) {  
	    	
		      vToken1 = rs.getString(1);           // Token.token   
		      vLemma1 = rs.getString(2);           // Token.lemma    
		      vEnglishLemma1 = rs.getString(3);    // Token.english_lemma 
	    	
	    	/* The process display the database contents.
	    	 * and read any change in english_lemma contribute by the user  
	    	*/
		     System.out.println("Digite otra traduccion de Token/lema/English_lemma que ligue con OTO: "+  vToken1 + " / "+ vLemma1 + " / " + vEnglishLemma1);
	         String s = br.readLine();
		     
	         if  (vEnglishLemma1!= null &&  vEnglishLemma1.equals(s)) {
	        	 System.out.println("SON IGUALES" + s); 
	         }	 
	         else { if ( s!= null ) { 	 
	             System.out.println("Nuevo  English_lemma " + s); 
	             TextDatabase.updateTokenEnglishLemma (conn, vLemma1, s, pBook);
	             TextDatabase.updateKnowledgeEnglishLemmaFromLemma (conn, vLemma1.trim(), s.trim());
	           } 
	         }

         }  // while   
		    
	    }
	    catch(Exception es){
	        es.printStackTrace();
	   } 
  }

private static void insertKnowledgeFromTokenJoinOTO(Connection conn, Integer pBook ) { 
    /* Inserts records in table Knowledge from token join OTO. 

     * - Description: 
     *          
     * - Revision History:
     *     10/06/2015 - Maria Aux. Mora
     *     
     * - Arguments (input / output):
     *    conn : database opened connection.
     *    pBook: book identifier. 
     * 
     * - Return Values:
     *     New records in table text.knowledge.   
	*/
	
  try{
	    /* Access text.token joined text.knowledge and OTO and extract all tokens which english_lemma does not match with OTO. 
	    */
	   ResultSet rs = TextDatabase.SelectTokenJoinOTOWhereNotincludedInKnowledge(conn, pBook);  // Select records that ara not included in knowledge
	   
	
	   
	   String vToken1 = null;           // Token.token   
	   String vLemma1 = null;           // Token.lemma    
	   String vEnglishLemma1 = null;    // Token.english_lemma 
	   String vCategory = null;         // OTO 
	   String vPos = null;              // Token.pos 
	   
	   
   
	    // Record structure:
	    //  1) t.token       t=Token
	    //  2) t.lemma 
	    //  3) t.english_lemma:  
        //  4) k.term,       k=OTO
       //   5) k.category,   OTO
        //  6) g.type,       g= knowledge
        //  7) t.pos 	     token	
	    while (rs.next()) {  
	    	
		      vToken1 = rs.getString(1);           // Token.token   
		      vLemma1 = rs.getString(2);           // Token.lemma    
		      vEnglishLemma1 = rs.getString(3);    // Token.english_lemma 
			  vCategory = rs.getString(5);         // OTO.category 
			  vPos = rs.getString(7);              // token.pos
	    	
     
	         if  (vCategory.trim().equals("structure")) {
	        	 TextDatabase.insertKnowledge(conn, vToken1 , vLemma1, vEnglishLemma1, vPos, "E", null, 3); 
	         }	 
	         else { 	 
	
	             TextDatabase.insertKnowledge(conn, vToken1 , vLemma1, vEnglishLemma1, vPos, "A", null, 3); 
	         } 

         }  // while   
		    
	    }
	    catch(Exception es){
	        es.printStackTrace();
	   } 
  }



private static void correctKnowledgeEnglishLemma(Connection conn, Integer pBook ) { 
    /* Update table Knowledge.English_lemm from token.English_lemma  
     * DEPRECATED 
     * 
     * - Description: 
     *          
     * - Revision History:
     *     10/06/2015 - Maria Aux. Mora
     *     
     * - Arguments (input / output):
     *    conn : database opened connection.
     *    pBook: book identifier. 
     * 
     * - Return Values:
     *     Updated records in table text.knowledge.   
	*/
	
  try{
	    /* Access text.token joined text.knowledge and OTO and extract all tokens which english_lemma does not match with OTO. 
	    */
	   ResultSet rs = TextDatabase.SelectTokenJoinKnowledgeWhereEnglishLemmaisDifferent(conn, pBook);  // Select records that ara not included in knowledge
	   
	   
	   String vEnglishLemma1 = null;    // Token.english_lemma 
	   String vToken = null;
	       
	    // Record structure:
	    //  1) t.english_lemma:      Token 
        //  3)Token,       token
      	
	    while (rs.next()) {  
	    	
		      vEnglishLemma1 = rs.getString(1);    // Token.english_lemma 
	    	  vToken = rs.getString(3); 
     
              TextDatabase.updateKnowledgeEnglishLemma(conn,   vEnglishLemma1, vToken);
	
         }  // while   
		    
	    }
	    catch(Exception es){
	        es.printStackTrace();
	   } 
  }

// STEP 12) =====================================================================================================//
// Semantic Analisys XMLGeneration

public static void semanticAnalisys(Connection conn, Integer p_book_id, Integer initialTaxonDescriptionId, Integer finalTaxonDescriptionId) throws InvalidFormatException, IOException {
/* - Description: Semantic analysis of chunk simple chunks. Simples chunks don not include prepositions, conjunctions, verbs or nueric ranges.
     * - Revision History:
     *     17/06/2015 - Maria Aux. Mora
     *     
     * - Arguments (input / output):
     *    conn : database opened connection.
     *    p_book_id:  book that will be procesed
     * 
     * - Requirements:
     *     Table token_tree and knowledge must have data generated from chunk.
     *     Table Oto must have data.
     * 
     * - Return Values:
     *     . Fill tables text.biological_unit and character with the results.
*/

try{ 	  
	Boolean isArea = false;
	String initialPreposition = null;
	
	 Integer v_taxon_description_id;   /*Chunk.Taxon_description_id*/
     Integer v_line_number; // Chunk.line_number 
     Integer v_sequence; // Chunk.sequence 
     String  line; /* Chunk.contents.  The end of chunk (":") is needed for FreeLing to set the end of the chunk*/  
     String  pos ;   // Chunk tree_pos.
     String originalPos ;  // Chunk.pos
     String lg ;
	
	System.load( "/pkg/freeling-3.1/APIs/java/libfreeling_javaAPI.so" );  
 	  
    Util.initLocale( "default" );

    // Create options set for maco analyzer.
    MacoOptions op = new MacoOptions( LANG );

    op.setActiveModules(false, true, true, true, 
                               true, true, true, 
                               true, true, true);
    // Data files definition
    op.setDataFiles(
      "", 
      DATA + LANG+"/locucions.dat", 
      DATA + LANG + "/quantities.dat",
      DATA + LANG + "/afixos.dat",
      DATA + LANG + "/probabilitats.dat",
      DATA + LANG + "/dicc.src",
      DATA + LANG + "/np.dat",
      DATA + "common/punct.dat");

    System.out.print( "Processando " );
    // Create analyzers.
    LangIdent lgid = new LangIdent(DATA + "/common/lang_ident/ident-few.dat");

    Tokenizer tk = new Tokenizer( DATA + LANG + "/tokenizer.dat" );
    Splitter sp = new Splitter( DATA + LANG + "/splitter.dat" );
    Maco mf = new Maco( op );
    
    
    System.out.print( sp );

    HmmTagger tg = new HmmTagger( DATA + LANG + "/tagger.dat", true, 2 );
    ChartParser parser = new ChartParser(
      DATA + LANG + "/chunker/grammar-chunk.dat" ); 
    DepTxala dep = new DepTxala( DATA + LANG + "/dep/dependences.dat",
      parser.getStartSymbol() );
    Nec neclass = new Nec( DATA + LANG + "/nerc/nec/nec-ab-poor1.dat" );

    Senses sen = new Senses(DATA + LANG + "/senses.dat" ); // sense dictionary
    Ukb dis = new Ukb( DATA + LANG + "/ukb.dat" ); // sense disambiguator
  
    //Ways of seleccting the group of chunk that will be procesed:
       //1) Selection by taxon_description_id, Line_number and sequence. 
       // Access Chunks in TEXT database. null, null, null = process all chunks.    
      // ResultSet rs = TextDatabase.selectChunk(conn, p_book_id, 524, null, null);
    
       //2) Selection by chunk ranges.  Example: from taxon_description_id >=134 and taxob_decription_id <=200
       // Access a Chunks range in TEXT database. null, null, = process all chunks.    Esta es la que sirve
    ResultSet rs = TextDatabase.selectChunkRange(conn, p_book_id,  initialTaxonDescriptionId, finalTaxonDescriptionId);
   
       // 3)By chunk type
       // Just for testing.  Selection by tree_pos 
       //ResultSet rs = TextDatabase.SelectChunkByTreePos(conn, "ECMMSEF", p_book_id);
	
    while (rs.next())
    {  /* for each Record */ 
       // Get some information from table text.chunk
       v_taxon_description_id = rs.getInt(2);   /*Chunk.Taxon_description_id*/
       v_line_number = rs.getInt(3); // Chunk.line_number 
       v_sequence = rs.getInt(4); // Chunk.sequence 
       line =  rs.getString(5).trim(); // Chunk.contents.   
       pos = rs.getString(9);   // Chunk tree_pos.
       originalPos = rs.getString(6);  // Chunk.pos
       
       /* If the last character of line is not a puntuation mark then add : at the end of the string.  
          FreeLing needs a final punctuation mark.   */
       if (!(line.substring(line.length()-1).equals(",") ||line.substring(line.length()-1).equals(".") || 
    		   line.substring(line.length()-1).equals(";") || line.substring(line.length()-1).equals(":") ))
    	       line =  line + ":"; /* Chunk.contents.  The : is needed for FreeLing to set the end of the chunk*/
	  
 	  //To clean the indicator TEXT.INDICATOR.USE_PREVIOUS_ASSIGNED_STRUCTURE
       TextDatabase.setUsePreviousAssignedStructureIndicator(conn, 0);
       
       //To clean the indicator ANALYZE_THE_REST_OF_THE_CHUNK (1=true, 0 = false)
       TextDatabase.setAnalyzeTheRestOfTheChunkIndicator( conn, 1);
     
       // Identify language of the text.  
       lg = lgid.identifyLanguage(line);
       System.out.println( "-------- LANG_IDENT results -----------" );
       System.out.println("Language detected (from first line in text): " + lg +" " + v_taxon_description_id + "  sequence "+ v_sequence);
       System.out.println("Linea " + line);
       if ( line != null ) {
          // Extract the tokens from the line of text.
          ListWord l = tk.tokenize( line );
      
          // Split the tokens into distinct sentences.
          ListSentence ls = sp.split( l, false );
          
          // Perform morphological analysis
          mf.analyze( ls );

          // Perform part-of-speech tagging.
          tg.analyze( ls );

          // Perform named entity (NE) classificiation.
          neclass.analyze( ls );

          sen.analyze( ls );
          dis.analyze( ls );
          
          printResults( ls, "tagged" );

          // Chunk parser
          parser.analyze( ls );
          printResults( ls, "parsed" );

          // Dependency parser
          dep.analyze( ls );
          printResults( ls, "dep" );
          
          
          // Save tokens inside tokenTreeList
          if (pos != null){
             isArea = pos.indexOf("G")>=0;
             if (originalPos.indexOf('S')==0){
            	 initialPreposition = line.substring(0, line.indexOf(" ")).trim();
             } else initialPreposition = null;
          }
          evaluateDependencyTree( conn, v_taxon_description_id, v_line_number, v_sequence, ls, p_book_id, isArea, initialPreposition);
       }
       
      }  
}
  catch(Exception es){
       es.printStackTrace();
  } 
}

private static void evaluateDependencyTree(Connection conn, Integer p_taxon_description_id, Integer p_line_number, Integer p_sequence, 
		  ListSentence ls, Integer p_book_id, Boolean isArea, String initWithPreposition) {
	
    /* - Description: Evaluate each chunk depending of its tree_pos. 
	 *     Category 1) All chunks.
	 *     Category 2) Chunks with numerics areas.
	 *     Category 3) Chunks that init with a preposition.   This chunks mus be associated with the initial structure of the clause. 
	 *                 with name = the preposition, notes = preposition, constraint = the chunk.

     * - Revision History:
     *     20/06/2015 - Maria Aux. Mora
     *     
     * - Arguments (input / output):
     *    conn : database opened connection.
     *    p_book_id:  Book id.
     *    initWithPreposition  :  True when the chunk init with a preposition.
     * 
     * - Return Values:
     *     . Fill tables text.biological_unit and text.character with the results.
     */		
	

    System.out.println( "-------- DEPENDENCY PARSER results -----------" );

    ListSentenceIterator sIt = new ListSentenceIterator(ls);
    while (sIt.hasNext()) {
	    Sentence s = sIt.next();
      TreeDepnode tree = s.getDepTree();
      printDepTree( 0, tree);
      
      System.out.println( "Semantic Analisys -------- SEQUENCE -----------" + p_sequence );
      
   	  evaluateDepTreeChunkCategory1( conn, 0, tree, null, p_taxon_description_id, p_line_number, p_sequence, p_book_id, null, "", "",
    			  null, isArea, null, initWithPreposition);
      }
    
 }


private static void evaluateDepTreeChunkCategory1(Connection conn,  int depth, TreeDepnode tr , TreeDepnode trParent, Integer v_id, Integer v_line_number, 
		  Integer v_sequence, Integer p_book_id, TokenParser previousToken, String tokenConstraint, String adjectiveModifier, String measureUnit,
		  Boolean isArea, String otherConstraint, String initialPreposition) {

/* - Description: Evaluate the dependency tree to estructure the chunk with key = (v_id, v_line_numbre, v_sequence) 
     * - Revision History:
     *     20/06/2015 - Maria Aux. Mora
     *     
     * - Arguments (input / output):
     *    conn : database opened connection.
     *    int depth:  tree depth  
     *    TreeDepnode tr: tree node that is being processed.  
     *    Integer v_id :  taxon_description_id 
     *    Integer v_line_number:  clause id (line sequence in a taxon_description)
	 *	  Integer v_sequence: chunk id (chunk sequence in a clause) 
	 *	  Integer p_book_id:  book being procesed
	 *	  TokenParser parentToken : information about the token being processed.
     * 
     * - Return Values:
     *     . Fill tables text.biological_unit and text.character with the results.
*/
	
	  
  TreeDepnode child = null;
  TreeDepnode fchild = null;
  Depnode childnode;
  long nch;
  int last, min;
  Boolean trob;
  String tag1, tag2;
  
  Boolean hasParenthesis = false;
  String rightNumber, leftNumber;
  TokenParser tokenChild, tokenSibling;
  String vName = null;
  
  Timestamp characterTimestamp, structureTimestamp;
  Integer structureId;                           // ID for the last structure included in the table BIOLOGICAL_ENTITY
  Integer characterId;
  String prepositionString, verbString;          // String with part of the chunk that include from the porposition or verb until the end of the chunk.
  String conjunctionString;
  String genderAndNumber;                        // Gender and number of a chunk (the complete frase).
  
  Integer howManyChildren;                       // Number of children of a node wthiout including puntuation sings. 
  
  Integer tempStructureId;                       // To compare current structureId with the previous structure asigned to a character.
  
  TokenParser childrenNotIncluded;                   // To verify if a string includes all childred (verb, nouns, adjectives and adverd) of a node 

  for( int i = 0; i < depth; i++ ) {
    System.out.print( "  " );
  }
  
  tag1 = tr.getInfo().getLinkRef().getInfo().getLabel();
  tag2 = tr.getInfo().getLabel();

  System.out.print(
     tag1 + "/" + tag2 + "/" );

  Word w = tr.getInfo().getWord();
  
  System.out.print(
    "(" + w.getForm() + " " + w.getLemma() + " " + w.getTag() );
  printSenses( w );
  System.out.print( ")" );

  // Current token Information
  // Parameters Integer p_token_id, Integer p_taxon_descrition_id, Integer  p_line_number, 
  //            String p_form, String p_lemma, String p_tag, String p_tag1, String p_tag2, Integer p_sequence
  //            book_id, knowledgeType, ontologyCategory, ontologyId
  
  
  TokenParser currentToken = TextDatabase.selectCurrentTokenTreeRecord(conn, v_id, v_line_number,v_sequence, w.getForm(), w.getTag(), 
		                                   tag1, tag2, p_book_id, previousToken);
 
  System.out.print( currentToken.getTaxonDescriptionId()  + "|" + currentToken.getLineNumber() + "|" + currentToken.getSequence() );
  
  // If the chunk init with a preposition the preposition must be asociated to the first structure of the clause.   
  if (initialPreposition != null && !(initialPreposition.trim().equals("sin")||initialPreposition.trim().equals("con") || 
		      initialPreposition.trim().equals("por")) ) {

//  if (initialPreposition != null  ) { Si no quiero procesar ninguna preposicion//
	  // If init with preposition => get the whole chunk
	  prepositionString = TextDatabase.selectChunkContentsByToken(conn, currentToken);
	  structureId = TextDatabase.selectMainStructureId (conn,  currentToken.getTaxonDescriptionId(),  currentToken.getLineNumber())  ; 
	  TextDatabase.updateStructureConstraintPrepositionById(conn, structureId, prepositionString, initialPreposition, currentToken, tokenConstraint);  
	  
      //To set the indicator ANALYZE_THE_REST_OF_THE_CHUNK = 0  (1=true, 0 = false)
      TextDatabase.setAnalyzeTheRestOfTheChunkIndicator( conn, 0); 
      initialPreposition = null;
      
      // Process all structures inside the prepositionString 
      processStructuresInsidePrepositionString(conn, currentToken, prepositionString);
  }  
  
  
  if (TextDatabase.getAnalyzeTheRestOfTheChunkIndicator(conn)==1)  {
	   /* The method will analyze current token if INDICATOR.ANALYZE_THE_REST_OF_THE_CHUNK = 1.  
	    * The indicator is cero when a preposition, conjuction or verb is processed.  This rule is applyed because this type of chunk are complex
	    * and structure its contents make the result lose meaning.
	    */
		  //Token evaluation
		  switch (currentToken.getWordTag().substring(0, 1)) {
		     case "D":   // Determinants.   Tag I = (Indefinidos), A = Articulos 
		    	 tokenChild = tokenLeftmostChild(conn, currentToken,  tr,  p_book_id, previousToken); //To verify if tokenChild is adjective => a new structure must be created.

		    	 if ((currentToken.getWordTag().substring(1, 2).equals("I")) ||currentToken.getWordTag().substring(1, 2).equals("A") ){
		    		 currentToken.processDeterminant(conn, previousToken, otherConstraint, tokenChild);
		    	 }
				 if (TextDatabase.tokenIsAfterConjunction(conn, currentToken)){ 
		  	 	      TextDatabase.setUsePreviousAssignedStructureIndicator(conn, 0);
				 }   
		    	 
		        break; 
		  case "P":   //Pronoun Tag I = (Indeterminados).  It is important to considere other pronoun types to process other biological groups.
			  	 if ((currentToken.getWordTag().substring(1, 2).equals("I")) ){
			  		adjectiveModifier = adjectiveModifier + " " +currentToken.getWordForm(); 
			  		}
		      break; 
		      
		  case "S":    // Preposition 
			  
			  // This indicator  USE_PREVIOUS_ASSIGNED_STRUCTURE is used to asign second adjective to the same structure after a conjuction.
			  //TextDatabase.setUsePreviousAssignedStructureIndicator(conn, 0);
			  
			  prepositionString = fromPrepositionToEndofChunk(conn, currentToken, tr); 
			 
		   	  // For all types of prepositions do:
		   	  characterTimestamp = TextDatabase.selectPreviousCharacterTimestampWithSameChunkKey (conn, currentToken);
		   	  structureTimestamp = TextDatabase.selectPreviousStructureTimestampWithSameChunkKey(conn, currentToken);
		   	  structureId = TextDatabase.selectPreviousStructureNoMatterGenusAndNumber(conn, currentToken);
	    	  characterId = TextDatabase.selectPreviousCharacterIdWithSameChunkKey(conn, currentToken);
	    	  tokenChild = tokenLeftmostChild(conn, currentToken,  tr,  p_book_id, previousToken); //To verify if tokenChild is structure => a relation must be created.
	    	
	    	  
	    	  // Verify if TEXT.INDICATOR.USE_PREVIOUS_ASSINED_STRUCTURE is true/1. To relate current preposition with 
	    	  // previous structure asigned.
		      // For future development:  the system must create a relation of tempStructureId and next structure.
	    	  tempStructureId = TextDatabase.setStructureIdAccordingWithUsePreviousAssignedStructure(conn, structureId, currentToken); 
	    	  
	    	  currentToken.processPreposition(conn, prepositionString,  structureTimestamp,  characterTimestamp, 
	    			  tempStructureId, characterId, initialPreposition, currentToken, tokenConstraint);
		          
		      tokenConstraint = "";    
		      
		   // ojo El if lo debo quitar si no quiero procesar ninguna preposicion    
		      // Process all structures inside the prepositionString 
		      if (!( currentToken.getWordForm().trim().equals("sin")||currentToken.getWordForm().trim().equals("con") || 
		         currentToken.getWordForm().trim().equals("por"))){ 
		          processStructuresInsidePrepositionString(conn, currentToken, prepositionString);
		      }
		      
			  //To avoid the method analyze the rest of the chunk.
		      TextDatabase.setAnalyzeTheRestOfTheChunkIndicator( conn, 0);
		
			  if (currentToken.isRoot()) {
				  // If this is root => the system must apply the preposition to the first structure 
				  // (i.e. include the complete phrase into the CONSTRAINT_PREPOSITION field).
				  structureId = TextDatabase.selectMainStructureId (conn,  currentToken.getTaxonDescriptionId(),  currentToken.getLineNumber()) ; 		
			  }
			  
		      // For each type of preposition do:
		  	  switch (currentToken.getWordForm()) {
			  
		    	  case "hasta": case "a":
				    
		    		  // "hasta"/"a"  preposition must modify the prevoius record included in the table CHARACTER or STRUCTURE (update field OTHER_CONSTRAINT).
				  
		    		  currentToken.processPrepositionAsOtherConstraint(conn, structureId,  characterId, structureTimestamp,  characterTimestamp);
				  
		          break;
			     	 
		    	  case "sin": case "con":  case "por": 
		    		  if (tokenChild != null && tokenChild.isStructure()){
		    	    	  // Verify if TEXT.INDICATOR.USE_PREVIOUS_ASSINED_STRUCTURE is true/1. To relate current preposition with previous structure asigned.
		    	          // For future development:  the system must create a relation of tempStructureId and next structure.
		   // 	          if (tempStructureId == structureId) {

		    			  TextDatabase.updateStructureRelationById(conn, structureId, currentToken.getWordForm()); 
		    			  TextDatabase.insertRelationWithNextStructure(conn, currentToken, structureId);
		    	          
				      }   
		    		  //To analyze the rest of the chunk in case of prepossition in (sin, con, por).
				      // Ojo Esto lo debo comentariar si no quiero procesar ninguna preposicion
		    		  TextDatabase.setAnalyzeTheRestOfTheChunkIndicator( conn, 1);
			  	  
				  break;
		       }


	           break;
		
			  
		  case "C":    //  Conjuntion
			  /* For all types of conjuntions do:
			   * 
			   * if conjunction node:
			   *    Has 2 children (without including punctuation sing): => apply conjunction to leftmost child (using otherConstraint). 
			   *        The conjunction must be apply to the leftmost child (i.e. only if INDICATOR.conjunction_indicator = 0 ).  Apply
			   *        means to update the field constraint_conjuction (tables CHARACTER or STRUCTURE).
			   *    Has 1 child:  Apply conjunction to the last object processed that has the same type (structure or character) of the child.
			   *    Has 0 children: Apply conjunction to the last object processed that has the same type of the rigth sibling of currentToken. 
			   *    
			   *  The second adjective (after the conjunction) must be associated to the same structure than the first one.       
			   */
		
			  howManyChildren = howManyChildrenHas( conn, currentToken,  tr);
			  
		      structureId = null;
			  characterId = null;
			  conjunctionString = TextDatabase.fromTextToEndString(conn, currentToken);
			  
			  /*To indicate that the second adjective (after the conjunction) must be associated to the same structure than the first one.
			   * Only if the conjunction does not have a leftmost child that is an structure (case book =1, 14,9,2).  In this type of chunk 
			   * the structure at each side of the conjunction must recieve the attributes. 
			  */				  
			  tokenChild = tokenLeftmostChild(conn, currentToken,  tr, p_book_id, previousToken);
			  if (tokenChild!= null && ( tokenChild.isStructure() ||
					                     TextDatabase.selectTokenParserRoot(conn, currentToken, p_book_id).isStructure() )) {
   			     TextDatabase.setUsePreviousAssignedStructureIndicator(conn, 0);
			  } else TextDatabase.setUsePreviousAssignedStructureIndicator(conn, 1);
			  if ( (currentToken.getWordForm().equals("y")) || (currentToken.getWordForm().equals("o")) || (currentToken.getWordForm().equals("u") )
					  || (currentToken.getWordForm().equals("e") )) {
				  conjunctionString = currentToken.getWordForm();
			  }
		
			  if (howManyChildren >=2) {
				  otherConstraint = conjunctionString; 
				  TextDatabase.setConjunctionIndicator(conn, 0);
				  
			  } else if (howManyChildren ==1 ) {
				  
				  structureId = TextDatabase.selectPreviousStructureNoMatterGenusAndNumber(conn, currentToken);
				  structureId = TextDatabase.setStructureIdAccordingWithUsePreviousAssignedStructure(conn, structureId, currentToken); 
			
				  if (tokenChild != null) {
					 if (tokenChild.isStructure() || tokenChild.isStructureModifier()) {
					    
					  	currentToken.processConjunctionOneChild(conn, tokenChild, structureId, characterId, true, conjunctionString);
					 }   
					 else{    
				    	characterId = TextDatabase.selectPreviousCharacterIdWithSameChunkKey(conn, currentToken);
				        currentToken.processConjunctionOneChild(conn, tokenChild, structureId, characterId, false, conjunctionString);
					     }
				   }
				  	 	
			  } else if (howManyChildren == 0){ 
				        tokenSibling = rigthSibling (conn,  trParent, currentToken, p_book_id, previousToken);
				        
				        if (tokenSibling !=null && (tokenSibling.isStructure() && tokenSibling.isStructureModifier() )){		
					        //structureId = TextDatabase.selectPreviousStructureNoMatterGenusAndNumber(conn, currentToken);
		  		            currentToken.processConjunctionNoChildren(conn, structureId,  null, conjunctionString);
				        } else { 
				        	if (tokenSibling !=null ) {
						        characterId = TextDatabase.selectPreviousCharacterIdWithSameChunkKey(conn, currentToken);
			  		            currentToken.processConjunctionNoChildren(conn, null,  characterId, conjunctionString);
				            } 	
				        }
		  	   }
			  
			  break; 
		  
		  case "I":  // Interjection 
		      break; 
		  
		  case "F":   // Puntuation 
			  
		     currentToken.processPuntuationMark(conn, previousToken, tokenConstraint);
		
		     break;
		
		  case "Z":   // Numbers
			  
			  hasParenthesis = leftChildIsParentesis(tr) || leftSiblingIsParentesis(tr, trParent, currentToken.getWordForm());
			  measureUnit = closetUnitOfMeasure(conn, currentToken, tr, trParent, p_book_id, previousToken); 
			  
			  if (isArea){
			     vName = areaPartLengthOrWidth(conn, tr, trParent, previousToken, currentToken, p_book_id, hasParenthesis);
			  }  		 
			  
			  if (hasParenthesis){
				 leftNumber = leftNumber(conn, tr, trParent, currentToken.getWordForm(), currentToken, previousToken,  p_book_id); 
				 rightNumber = rightNumber(conn, tr, trParent, currentToken, previousToken, p_book_id );  
				
				 
				 
				 currentToken.processNumberParentesis(conn,  measureUnit, previousToken, isArea, leftNumber, rightNumber, vName, 
						 tokenConstraint, otherConstraint, currentToken);
				 
			  } else {  
			    currentToken.processNumber(conn,  measureUnit, previousToken, isArea, vName, tokenConstraint, otherConstraint, currentToken);
			  }  
		      
			  tokenConstraint = "";
			  otherConstraint = "";
		
			  
		      break; 
		  
		  case "W":   // Date and hours
		      break; 
		  
		      
		  default: // A = Adjectives, N= Names,  V= Verbs, null => value="[" or "]",  M = Structure modifier, R = Adverb
			  
		   	   if (currentToken.isAdverb()) {
				  /* If current adverd has a child that is an adjective, verb, num or unit of measure 
				   *                      => It must be applied to its child.
				  *  or if current adverb has a sibling that is an adjective, verb, num or unit of measure 
				  *                       => It must be applied to it. 
				  *  or if current adverb has a parent that is and adjective, verb, num or unit of measure 
				  *                       => it must be applied to it.
				  */
				  tokenChild = tokenHasAdjectiveOrVerborNumChild(conn, currentToken, tr, p_book_id, previousToken);
				  if (tokenChild != null){
					 // Save the adverd an pass it as a parameter to the firts child. 
						  tokenConstraint = tokenConstraint + " " +currentToken.getWordForm(); 
				  }      
				  else { //  tokenSibling = tokenHasAdjectiveOrVerborNumChild (conn, previousToken, trParent, p_book_id, previousToken);
					  tokenSibling = tokenHasAdjectiveOrVerborNumChild (conn, currentToken, trParent, p_book_id, previousToken);
					if (tokenSibling != null ) {
					  /* If currentToken has adjective, verb, num or unit of measure siblings  
						        = if token parent has adjective or verb child. */
						currentToken.processAdverbAppliedToSibling(conn, tokenSibling, tokenConstraint, adjectiveModifier, otherConstraint);
						tokenConstraint = "";
						adjectiveModifier = "";
						otherConstraint = "";
						
				       }
				    else   
				      //The token's father is an adjective 
				      if ( !(previousToken == null) ) {
				    	 // Apply adverb to parent character or estructure.  
				         if (!(currentToken.processAdverbAppliedToPreviousCharacter(conn, previousToken)) )
				          tokenConstraint = tokenConstraint + " " + currentToken.getWordForm();
				         
				      } else tokenConstraint = tokenConstraint + " " +currentToken.getWordForm();
				  }
			  }
			  else {  
			  
		  	   if (currentToken.isAdjective() ) {
		  		   currentToken.processAdjective(conn, previousToken, tokenConstraint, adjectiveModifier, otherConstraint);
		  		   tokenConstraint = "";
		  		   adjectiveModifier = "";
		  		   otherConstraint = "";
		  	   } else {
		  	  	   if (currentToken.isStructure() ) {
		  	  		   currentToken.processStructure(conn, previousToken, tokenConstraint, otherConstraint);
		  	  		   tokenConstraint = "";
					   // if between a conjuction and characters (adjectives, numbers, etc) there is an structure
		  	  		   // characters must be associated to this structure.
					   if (TextDatabase.tokenIsAfterConjunction(conn, currentToken)){ 
		  	  	   	      TextDatabase.setUsePreviousAssignedStructureIndicator(conn, 0);
					   }   
					   
		  	  	   } else if (currentToken.isStructureModifier()) { 
		  	  		       tokenChild = tokenLeftmostChild(conn, currentToken, tr, p_book_id, previousToken);
		  	  		       // If the leftmost child of current token is a determinant then a new estructure is needed 
		  	  		       if (tokenChild != null) {
		  	  		           currentToken.processStructureModifier(conn, previousToken, tokenChild.isDeterminant(), tokenConstraint, otherConstraint); 
		  	  		       } else  currentToken.processStructureModifier(conn, previousToken, false, tokenConstraint, otherConstraint);   
		  	  	   } else if (currentToken.isVerb()){
		  	  		  if (currentToken.getTag2().trim().equals("top")) {
		  	  			// Verb is the top of the tree verbString must get the whole chunk.
		  	  			 verbString = TextDatabase.SelectChunkContents (conn, currentToken.getTaxonDescriptionId(), currentToken.getLineNumber(),
		  	  					                   currentToken.getSequence());
		  	  		  } else {
		  	  		     verbString = TextDatabase.fromTextToEndString(conn, currentToken); 
		  	  		    
		  	  		     
		  	  		  }   
		  	  		//  genderAndNumber = childrenGenderAndNumber(conn, currentToken, tr, trParent, p_book_id, previousToken);
		  			  currentToken.processVerb(conn, verbString, currentToken.getWordForm(), otherConstraint);
                      // To avoid the analisys of the rest of chunk  
		  			  TextDatabase.setAnalyzeTheRestOfTheChunkIndicator( conn, 0);
		  			  
		  			  // Process all structures inside the verbString
		  	  	      processStructuresInsidePrepositionString(conn, currentToken, verbString);

	
		  	  	   }
		  	     }	
			 }
		  
		  	break;
		 }
	} // 
  nch = tr.numChildren();

  if( nch > 0 ) {
    System.out.println( " [" );
    
    for( int i = 0; i < nch; i++ ) {
      child = tr.nthChildRef( i );

      if( child != null ) {
        if( !child.getInfo().isChunk() ) {
          evaluateDepTreeChunkCategory1(conn,  depth + 1, child, tr,  v_id,  v_line_number, v_sequence, p_book_id, currentToken, tokenConstraint.trim(),
        		         adjectiveModifier, measureUnit, isArea, otherConstraint, initialPreposition);
        }
      }
      else {
        System.err.println( "ERROR: Unexpected NULL child." );
      }
    }

    // Print chunks (in order)
    last = 0;
    trob = true;

    // While an unprinted chunk is found, look for the one with lower
    // chunk_ord value.
    while( trob ) {
      trob = false;
      min = 9999;

      for( int i = 0; i < nch; i++ ) {
        child = tr.nthChildRef( i );
        childnode = child.getInfo();

        if( childnode.isChunk() ) {
          if( (childnode.getChunkOrd() > last) &&
              (childnode.getChunkOrd() < min) ) {
            min = childnode.getChunkOrd();
            fchild = child;
            trob = true;
          }
        }
      }
      if( trob && (child != null) ) {
        evaluateDepTreeChunkCategory1(conn, depth + 1, fchild, tr, v_id, v_line_number, v_sequence, p_book_id, currentToken, tokenConstraint.trim(),
        		  adjectiveModifier, measureUnit, isArea, otherConstraint, initialPreposition);
      }

      last = min;
    }

    for( int i = 0; i < depth; i++ ) {
      System.out.print( "  " );
    }

    System.out.print( "]" );
  
//    tokenTreeList.add(new TokenParser(null, v_id, v_line_number, "]", null, null, null, null, v_sequence, p_book_id));
  }

  System.out.println( "" );
}


public static void processStructuresInsidePrepositionString(Connection conn, TokenParser currentToken, 
		   String prepositionOrVerbString)  {
/* - Description: Process all structures inside the prepositionString. 
 *    To be sure that characteres could be associated with structures inside no processed string
 * 
     * - Revision History:
     *     16/12/2015 - Maria Aux. Mora
     *     
     * - Arguments (input / output):
     *    conn : database opened connection.
     *    currentToken:  use to extract taxon_description_id, line_number and sequence
     * 
     * - Return Values:
     *     null
*/
	/* For table Structure:
	 * 1) notes = “init with preposition”
	*/
 //   TextDatabase.createNewStructureWithRecordsWithInitPreposition(conn, p_book_id);

  TokenParser previousToken = null;
  TokenParser tempToken;
  
  if (prepositionOrVerbString != null) {	
	
   	 String[] parts = prepositionOrVerbString.split(" ");
	  
     for(String tokenString:parts){
    	 
    	 tempToken = TextDatabase.selectCurrentTokenTreeRecordByTokenString (conn, currentToken, tokenString); 
    	 
    	 if (tempToken != null && tempToken.isStructure() ) {
    		 tempToken.processStructure(conn, previousToken, "", "");
    	 }	    	 
    	 previousToken = tempToken;
    }
  }  	
}



public static void postSemanticAnalisys(Connection conn, Integer p_book_id) throws InvalidFormatException, IOException {
/* - Description: Post semantic analysis of chunk of chunks.
 *  For table CHARACTER and BIOLOGYCAL_ENTITY do;
 *  
 *  For table BIOLOGYCAL_ENTITY:
	 * 1) For all structures that notes = “init with preposition”. Clone the record and update name = main.
	 *     Update the oringinal record with constraint_preposition = "".
	 *     IT DID NOT WORK, BECAUSE THE NEW STRUCTURE GOT AN ID AT THE END OF THE CLAUSE.
 *  For CHARACTER:
 * 
 * 
     * - Revision History:
     *     2/10/2015 - Maria Aux. Mora
     *     
     * - Arguments (input / output):
     *    conn : database opened connection.
     *    p_book_id:  book that will be procesed
     * 
     * - Return Values:
     *     . Update tables text.biological_unit and character.
*/
	/* For table Structure:
	 * 1) notes = “init with preposition”
	*/
 //   TextDatabase.createNewStructureWithRecordsWithInitPreposition(conn, p_book_id);
    
}

private static String closetUnitOfMeasure(Connection conn, TokenParser currentToken, TreeDepnode tr, TreeDepnode trParent, 
		Integer p_book_id, TokenParser previousToken){

	String unitOfMeasure = null;
	
	unitOfMeasure =  parentOrSiblingUnitOfMeasure(conn, currentToken, tr, trParent, p_book_id, previousToken); 
	
	if (unitOfMeasure == null) {
		unitOfMeasure = TextDatabase.getClosetUnitofMeasure(conn, currentToken, p_book_id);
	}

  return(unitOfMeasure);

}

// WORD TREE OPERATIONS ) ===============================================================================================//

private static String childrenGenderAndNumber(Connection conn, TokenParser currentToken, TreeDepnode tr, TreeDepnode trParent, Integer p_book_id,
		TokenParser previousToken){
	// Devueve el genero y numero (en una sola hilera con dos caracteres) que son el genero y numero del primer adjetivo, 
	// determinante o pronombre  que encuentra como hijo del nodo actual.  Si el nodo actual no tiene hijo se evaluan los hijos del papa.

	  String genderAndNumber = null;

  	  TreeDepnode child = null;
	  Long nch ;
	  TokenParser tokenTemp;
	  String vTag1, vTag2;
	  Word w ;
	  Integer theFirst = 1;
	  String vGender, vNumber;
	  
	  
		
	  if ((currentToken != null ) || (tr != null) ) {	
	 	
	    nch = tr.numChildren();
	    theFirst = 1;
	    
	    if( nch > 0 ) {
	    
	      for( int i = 0; i < nch; i++ ) {
	        child = tr.nthChildRef( i );
	        
	        if (( child != null ) && (theFirst <= 1)) {
	            vTag1 = child.getInfo().getLinkRef().getInfo().getLabel();
	            vTag2 = child.getInfo().getLabel();
	            w = child.getInfo().getWord();
	        
	            System.out.println(
	             "( Palabra, lema, y POS:  " + w.getForm() + " " + w.getLemma() + " " + w.getTag() );
	     
	            if( w.getTag() != null && w.getForm() != null) {
	        	  tokenTemp = TextDatabase.selectCurrentTokenTreeRecord(conn,currentToken.getTaxonDescriptionId(), currentToken.getLineNumber(), 
	        			                                currentToken.getSequence(), w.getForm(), w.getTag(), vTag1, vTag2, p_book_id, 
	        			                                previousToken); 
	        	  
	        	  if (tokenTemp != null) {
	        	             		  
	        	     if  ((tokenTemp.isAdjective() || (tokenTemp.isDeterminant())) || tokenTemp.isPronoun()  || tokenTemp.isStructure()
	        	    		 || tokenTemp.isStructureModifier()){
	        	    	 
	        	    	 vGender = tokenTemp.getGender();
	        	    	 vNumber = tokenTemp.getNumber();		 
	        	         
	        	    	 if ((vGender != null ) && (vNumber != null)) {
	        	    	    theFirst++;
	        	    	    genderAndNumber = vGender + vNumber ; 
	        	    	 }   
	        	     }  
	        	  } 
	          }
	        }  
	      }
	    } 
	    //  If currentToken does not have any child => evaluate the parent node's children.
	    else {
	    	child = null;
	   	    nch = trParent.numChildren();
	   	    theFirst = 1;
	   	    
	   	    if( nch > 0 ) {
	   	    
	   	      for( int i = 0; i < nch; i++ ) {
	   	        child = trParent.nthChildRef( i );
	   	        
	   	        if (( child != null ) && (theFirst <= 1)) {
	   	            vTag1 = child.getInfo().getLinkRef().getInfo().getLabel();
	   	            vTag2 = child.getInfo().getLabel();
	   	            w = child.getInfo().getWord();
	   	        
	   	            System.out.println(
	   	             "( Palabra, lema, y POS:  " + w.getForm() + " " + w.getLemma() + " " + w.getTag() );
	   	     
	   	            if( w.getTag() != null && w.getForm() != null) {
	   	        	  tokenTemp = TextDatabase.selectCurrentTokenTreeRecord(conn,currentToken.getTaxonDescriptionId(), currentToken.getLineNumber(), 
	   	        			                                currentToken.getSequence(), w.getForm(), w.getTag(), vTag1, vTag2, p_book_id, previousToken); 
	   	        	  
	   	        	  if (tokenTemp != null) {
	   	        	             		  
	   	        	     if  ((tokenTemp.isAdjective() || (tokenTemp.isDeterminant())) || tokenTemp.isPronoun()){
	   	        	    	 
	   	        	    	 vGender = tokenTemp.getGender();
	   	        	    	 vNumber = tokenTemp.getNumber();		 
	   	        	         
	   	        	    	 if ((vGender != null ) && (vNumber != null)) {
	   	        	    	    theFirst++;
	   	        	    	    genderAndNumber = vGender + vNumber ; 
	   	        	    	 }   
	   	        	     }  
	   	        	  } 
	   	          }
	   	        }  
	   	      }
	   	    }
	    }
	    
	    
	  }   
		return (genderAndNumber);
	 }


private static TokenParser tokenHasAdjectiveOrVerborNumChild(Connection conn, TokenParser currentToken, TreeDepnode tr, Integer p_book_id,
		TokenParser previousToken){
// Devueve el primer tokenParser hijo si este es un adjectivo, verbo, numero o unidad de medida.  
// Si existe un parentesis o una conjuncion (y,e,o,u) entre eses hijo y el current token empieza de nuevo en la busqueda.  
//    Esto para tomar en cuenta el caso "80,12,1: semillas 1 (rara vez 2-3) y el (238,2)" 

  TokenParser tokenResult = null;

  if ((currentToken != null ) && (tr != null) ) {	
 	
    TreeDepnode child = null;
    Long nch= tr.numChildren();
    
    TokenParser tokenTemp;             // Token being processed.
    String vTag1, vTag2;
    Word w ;
    Integer theFirst = 1;
    Boolean isEqualtoCurrentToken = false; 

    if(nch != null && nch > 0 ) {
    
      for( int i = 0; i < nch; i++ ) {
        child = tr.nthChildRef( i );
        
        if( child != null ) {
            vTag1 = child.getInfo().getLinkRef().getInfo().getLabel();
            vTag2 = child.getInfo().getLabel();
            w = child.getInfo().getWord();
        
            System.out.println(
             "( Palabra, lema, y POS:  " + w.getForm() + " " + w.getLemma() + " " + w.getTag() );
     
            if( w.getTag() != null && w.getForm() != null) {
        	  tokenTemp = TextDatabase.selectCurrentTokenTreeRecord(conn,currentToken.getTaxonDescriptionId(), currentToken.getLineNumber(), 
        			                                currentToken.getSequence(), w.getForm(), w.getTag(), vTag1, vTag2, p_book_id, previousToken); 
        	  
        	  if (tokenTemp != null) {
        	      
        		 if (tokenTemp.getWordForm().equals(currentToken.getWordForm())) isEqualtoCurrentToken = true; 
        		  
        	     if (  (theFirst ==1) && (tokenTemp.isAdjective() || tokenTemp.isVerb() || tokenTemp.isNumber() ||
        			(tokenTemp.isUnitofMeasurement()) )){
        		     // It must be the first adjective or verb
        	         tokenResult = tokenTemp; 		
        	         theFirst++;
        	        
        	         // If tokenTemp is a unit of measure then the token_tree_id of the left most child must be included in tokenResult.
        	    	 // This correspond to the next token that will be associated with current adverb.
        	         if (tokenTemp.isUnitofMeasurement()) { //If it is a unit of measure => get the left most child
         		    	   tokenTemp = tokenLeftmostChild(conn, tokenTemp,  child, p_book_id, previousToken);
         		    	   tokenResult.setTokenTreeId(tokenTemp.getTokenTreeId());;
         		     }
        	         
        	      }  else if ((tokenTemp.getWordForm().equals("(") && !isEqualtoCurrentToken ) || 
        	    		  (tokenTemp.getWordForm().equals("o") && !isEqualtoCurrentToken ) ||
        	    		  (tokenTemp.getWordForm().equals("y") && !isEqualtoCurrentToken ) ||
        	    		  (tokenTemp.getWordForm().equals("u") && !isEqualtoCurrentToken ) ||
        	    		  (tokenTemp.getWordForm().equals("e") && !isEqualtoCurrentToken ))  {
        		     // If there is a parenthesis or a conjuction (y,o,e,u) before current token the process must select another token.  
        		     theFirst = 1; 
        	      }  else if ((tokenTemp.getWordForm().equals("(") && isEqualtoCurrentToken ) || 
        	    		  (tokenTemp.getWordForm().equals("o") && isEqualtoCurrentToken ) ||
        	    		  (tokenTemp.getWordForm().equals("y") && isEqualtoCurrentToken ) ||
        	    		  (tokenTemp.getWordForm().equals("u") && isEqualtoCurrentToken ) ||
        	    		  (tokenTemp.getWordForm().equals("e") && isEqualtoCurrentToken ))  {
        		     // If there is a parenthesis or a conjuction (y,o,e,u) after current token the process does not found a token.  
        		     theFirst ++; 
        	      } 
        	  } 
          }
        }  
      }
      if ((tokenResult != null) && (currentToken != null) && (tokenResult.getWordForm().equals(currentToken.getWordForm()))) {
    	  tokenResult = null;
      }
    }
  }   
	return (tokenResult);
 }



private static TokenParser childredNotIncludedInString(Connection conn, TokenParser currentToken, TreeDepnode tr, String verbString, 
		Integer p_book_id, TokenParser previousToken){
//NO FUNCIONA pero no se requirio. Devueve el primer tokenParser hijo que no este incluido en la hilera si es verbo, adverbio, adjetivo o nombre

  TokenParser tokenResult = null;

  if ((currentToken != null ) || (tr != null) ) {	
 	
    TreeDepnode child = null;
    Long nch = tr.numChildren();
    TokenParser tokenTemp;             // Token being processed.
    String vTag1, vTag2;
    Word w ;
    Integer theFirst = 1;

    if( nch > 0 ) {
    
      for( int i = 0; i < nch; i++ ) {
        child = tr.nthChildRef( i );
        
        if( child != null ) {
            vTag1 = child.getInfo().getLinkRef().getInfo().getLabel();
            vTag2 = child.getInfo().getLabel();
            w = child.getInfo().getWord();
        
            System.out.println(
             "( Palabra, lema, y POS:  " + w.getForm() + " " + w.getLemma() + " " + w.getTag() );
     
            if( w.getTag() != null && w.getForm() != null) {
        	  tokenTemp = TextDatabase.selectCurrentTokenTreeRecord(conn,currentToken.getTaxonDescriptionId(), currentToken.getLineNumber(), 
        			                                currentToken.getSequence(), w.getForm(), w.getTag(), vTag1, vTag2, p_book_id, previousToken); 
        	  
        	  if (tokenTemp != null && verbString.indexOf(tokenTemp.getWordForm())< 0) {
        	      // If tokenTemp.wordForm was not included in verbString.         		  
        	     if (  (theFirst ==1) && (tokenTemp.isAdjective() || tokenTemp.isVerb() || tokenTemp.isAdverb() ||
        			(tokenTemp.isUnitofMeasurement()) || tokenTemp.isStructure() || tokenTemp.isStructureModifier() )){
        		     // It must be the first one
        	         tokenResult = tokenTemp; 		
        	         theFirst++;
  
        	         
        	      }  
        	  } 
          }
          // 
         if( !child.getInfo().isChunk() ) {
        	 tokenTemp = childredNotIncludedInString(conn,  currentToken, child, verbString, p_book_id,  previousToken);
         }  
            
        }  
      }
    }
  }   
	return (tokenResult);
 }



private static Integer  howManyChildrenHas(Connection conn, TokenParser currentToken, TreeDepnode tr){
	// Devueve el numero de hijos de un token que no sean signos de puntiuacion.

      Integer howMany = 0;

		
	  if ((currentToken != null ) || (tr != null) ) {	
	 	
	    TreeDepnode child = null;
	    Long nch = tr.numChildren();
	    TokenParser tokenTemp;
	    String vTag1;
	    Word w ;
	    
	    if( nch > 0 ) {
	    
	      for( int i = 0; i < nch; i++ ) {
	        child = tr.nthChildRef( i );
	        
	        if( child != null ) {
	          
	            w = child.getInfo().getWord();
	            vTag1 =  w.getTag();
	        
	            System.out.println(
	             "( Palabra, lema, y POS:  " + w.getForm() + " " + w.getLemma() + " " + w.getTag() );
	     
	            if( (vTag1 != null) && !(vTag1.substring(0, 1).equals("F"))) {
	            	howMany ++;
	        	}
	        }  
	      }
	    }
	  }   
		return (howMany);
	 }




private static TokenParser tokenLeftmostChild(Connection conn, TokenParser currentToken,  TreeDepnode tr, Integer p_book_id, 
		       TokenParser previousToken){
	// Devueve el primer tokenParser hijo.

	  TokenParser tokenResult = null;

		
	  if (tr != null)  {	
	 	
	    TreeDepnode child = null;
	    Long nch = tr.numChildren();
	    String vTag1, vTag2;
	    Word w ;

	    if( nch > 0 ) {
	    
	       child = tr.nthChildRef( 0 );
	       if( child != null ) {
	          vTag1 = child.getInfo().getLinkRef().getInfo().getLabel();
	          vTag2 = child.getInfo().getLabel();
	          w = child.getInfo().getWord();
	        
	          System.out.println(
	          "( Palabra, lema, y POS:  " + w.getForm() + " " + w.getLemma() + " " + w.getTag() )  ;
	  
	          if( w.getTag() != null && w.getForm() != null) {
	        	  tokenResult = TextDatabase.selectCurrentTokenTreeRecord(conn,currentToken.getTaxonDescriptionId(), currentToken.getLineNumber(), 
	        			                                currentToken.getSequence(), w.getForm(), w.getTag(), vTag1, vTag2, p_book_id, previousToken); 
	        	  }   
	          }
	        }  
	      
	    }
	    
		return (tokenResult);
	 }



private static String leftNumber(Connection conn, TreeDepnode tr, TreeDepnode trParent, String currentWordForm,
		TokenParser currentToken, TokenParser previousToken, Integer p_book_id) {
	
	String tokenNum = null;
	String tokenPos = null;
    String[] parts; 
    String v_leftNum = null;
    TreeDepnode child = null;
    Boolean isAlreadyEquals = false;
    String vTag1, vTag2;
    Word w ;
    TokenParser tempToken;

    

	if (trParent != null) {	 
	      // If left sibling is a number return it
  	      Long nch = trParent.numChildren();

	      if( nch > 0 ) {
	        
	        for( int i = 0; i < nch; i++ ) {
	          child = trParent.nthChildRef( i );
	      
	          if( child != null ) {
	             tokenNum = child.getInfo().getWord().getForm();
		         vTag1 = child.getInfo().getLinkRef().getInfo().getLabel();
		         vTag2 = child.getInfo().getLabel();
		         w = child.getInfo().getWord();

	             
	             
	             if (tokenNum != null) {
	               if (tokenNum.equals(currentWordForm)) {
	            	  isAlreadyEquals = true;  // The wordForm is equal to currentWordForm => se paso
	            	  
	               } else {
	            	   
	    	           tempToken = TextDatabase.selectCurrentTokenTreeRecord(conn,currentToken.getTaxonDescriptionId(), 
	     	        	           currentToken.getLineNumber(),currentToken.getSequence(), w.getForm(), w.getTag(), vTag1, vTag2,
	     	        	           p_book_id, previousToken); 
	           		   tokenPos =  child.getInfo().getWord().getTag();
	            	   if (!isAlreadyEquals &&  tokenPos.substring(0, 1).equals("Z"))  // It is a number 
	           		      if ( (tokenNum.contains("-")) ){
	     			         parts = tokenNum.split("-");
	     			         v_leftNum = parts[1];
	     			    } else v_leftNum = tokenNum;
	            	   if (!isAlreadyEquals &&  tempToken.isArea()){  // The area separator must be identified to clean the left number
	            		   v_leftNum = ""; 
	            	   }
	                                 
	               }
	             }  
	          }
	        }
	    } 
	  	// If tokenParent is a number => return it  
	   if (v_leftNum == null) {	   

		tokenNum = trParent.getInfo().getWord().getForm();
		tokenPos =  trParent.getInfo().getWord().getTag();
		if (tokenPos != null && tokenPos.substring(0, 1).equals("Z")) {  // tokenParent is a number
		    if ( (tokenNum != null &&  tokenNum.contains("-")) ){
			       parts = tokenNum.split("-");
			       v_leftNum = parts[1];
			    } else v_leftNum = tokenNum;
		}  
	   
	  } 
	   
	}	
	return (v_leftNum);
}



private static String parentOrSiblingUnitOfMeasure (Connection conn, TokenParser currentToken, TreeDepnode tr, TreeDepnode trParent, 
		Integer p_book_id, TokenParser previousToken){
	// Devueve la primer unidad de medida que encuentre en el padre, los hijos o los hermanos en ese orden.  Si no encuentra devuelve null. 
	// Se usa primero para hilar fino si existe mas de una unidad de medida. 

	  String unitofMeasureResult = null;
	  TokenParser tokenTemp;
	  String vTag1, vTag2;
	  Word w ;

	  

	  // If trParent is a unit of measure = returnt it.
	  if ((trParent != null) ) {	
         vTag1 = trParent.getInfo().getLinkRef().getInfo().getLabel();
	     vTag2 = trParent.getInfo().getLabel();
	     w = trParent.getInfo().getWord();
    	 
	     tokenTemp = TextDatabase.selectCurrentTokenTreeRecord(conn,currentToken.getTaxonDescriptionId(), currentToken.getLineNumber(), 
                  currentToken.getSequence(), w.getForm(), w.getTag(), vTag1, vTag2, p_book_id, previousToken); 

	     if ( (tokenTemp != null) && tokenTemp.isUnitofMeasurement()){
	    
   	      unitofMeasureResult = tokenTemp.getWordForm(); 		
	     
	     }
	  }
	  
	  // If there is a siblins that is unit of measure => return it 
	  if ((unitofMeasureResult == null ) && ((trParent != null) )) {	
	 	
	    TreeDepnode child = null;
	    Long nch = trParent.numChildren();


	    if( nch > 0 ) {
	    
	      for( int i = 0; i < nch; i++ ) {
	        child = trParent.nthChildRef( i );
        
	        if ((child != null ) && (unitofMeasureResult == null )) {
		        vTag1 = child.getInfo().getLinkRef().getInfo().getLabel();
		        vTag2 = child.getInfo().getLabel();
    	        w = child.getInfo().getWord();
		        
		        System.out.println(
		          "( Palabra, lema, y POS:  " + w.getForm() + " " + w.getLemma() + " " + w.getTag() );
			    
		        tokenTemp = TextDatabase.selectCurrentTokenTreeRecord(conn,currentToken.getTaxonDescriptionId(), currentToken.getLineNumber(), 
		                  currentToken.getSequence(), w.getForm(), w.getTag(), vTag1, vTag2, p_book_id, previousToken); 

			    if ( (tokenTemp != null) && tokenTemp.isUnitofMeasurement()){
			    
		   	       unitofMeasureResult = tokenTemp.getWordForm(); 		
			     
			     }
	        }  
	      }
	    }
	  }   
	  
	  // If there is a child that is unit of measure => return it 
	  if ((unitofMeasureResult == null ) && ((tr != null) )) {	
	 	
	    TreeDepnode child = null;
	    Long nch = tr.numChildren();


	    if( nch > 0 ) {
	    
	      for( int i = 0; i < nch; i++ ) {
	        child = tr.nthChildRef( i );
        
	        if ((child != null ) && (unitofMeasureResult == null )) {
		        vTag1 = child.getInfo().getLinkRef().getInfo().getLabel();
		        vTag2 = child.getInfo().getLabel();
    	        w = child.getInfo().getWord();
		        
		        System.out.println(
		          "( Palabra, lema, y POS:  " + w.getForm() + " " + w.getLemma() + " " + w.getTag() );
			    
		        tokenTemp = TextDatabase.selectCurrentTokenTreeRecord(conn,currentToken.getTaxonDescriptionId(), currentToken.getLineNumber(), 
		                  currentToken.getSequence(), w.getForm(), w.getTag(), vTag1, vTag2, p_book_id, previousToken); 

			    if ( (tokenTemp != null) && tokenTemp.isUnitofMeasurement()){
			    
		   	       unitofMeasureResult = tokenTemp.getWordForm(); 		
			     
			     }
	        }  
	      }
	    }
	  }
	  
	  
	  
	 return (unitofMeasureResult);
}		


private static String rightNumber(Connection conn, TreeDepnode tr, TreeDepnode trParent, 
		             TokenParser currentToken, TokenParser previousToken, Integer p_book_id) {
	// Devuelve el hermano de la derecha si es numero o si es unidad de medida devuelve el hijo izquierdo de la unidad de medida.
	
	
	String tokenNum = null;
    String[] parts; 
    String v_rightNum = null;
    TreeDepnode child = null;
    Boolean isAlreadyEquals = false;
    Long nch;
    Integer theFirst;
    String vTag1, vTag2;
    Word w ;
    TokenParser tokenTemp;
    String currentWordForm = currentToken.getWordForm();
    

  if (trParent != null) {
    
	nch = trParent.numChildren();

	if( nch > 0 ) {
	        
	   for( int i = 0; i < nch; i++ ) {
	      child = trParent.nthChildRef( i );
	      
	      if( child != null ) {
  		         vTag1 = child.getInfo().getLinkRef().getInfo().getLabel();
		         vTag2 = child.getInfo().getLabel();
       	         w = child.getInfo().getWord();
	             tokenNum = child.getInfo().getWord().getForm();
	             if (tokenNum != null) {
	               if (tokenNum.equals(currentWordForm)) {
	            	  isAlreadyEquals = true;  // The wordForm is equal to currentWordForm => se paso
	            	  
	               } else {
	   		           tokenTemp = TextDatabase.selectCurrentTokenTreeRecord(conn,currentToken.getTaxonDescriptionId(), currentToken.getLineNumber(), 
			                  currentToken.getSequence(), w.getForm(), w.getTag(), vTag1, vTag2, p_book_id, previousToken); 

	   		           if (isAlreadyEquals &&  (tokenTemp.isNumber() || tokenTemp.isUnitofMeasurement()) 
	   		        		   && v_rightNum ==null) {  // It is a number or unit of measure
	           		       
	   		        	   if (tokenTemp.isUnitofMeasurement()) { //If it is a unit of measure => get the left most child
	           		    	   tokenTemp = tokenLeftmostChild(conn, tokenTemp,  child, p_book_id, previousToken);
	           		    	   if (tokenTemp != null) tokenNum = tokenTemp.getWordForm();
	           		       }
	           		      
	           		       if ( (tokenNum.contains("-")) ){
		     			       parts = tokenNum.split("-");
		     			        v_rightNum = parts[0];
		     			   } else v_rightNum = tokenNum; 
	   		           }    
	               
	               }
	             }  
	          }
	        }
	    }
   } else { // If current token is root.
		nch = tr.numChildren();

		if( nch > 0 ) {
		   
		   theFirst = 1;	
		   for( int i = 0; i < nch; i++ ) {
		      child = tr.nthChildRef( i );
		      
		      if( child != null ) {
		    	     vTag1 = child.getInfo().getLinkRef().getInfo().getLabel();
			         vTag2 = child.getInfo().getLabel();
	       	         w = child.getInfo().getWord();
		             tokenNum = child.getInfo().getWord().getForm();
		             if (tokenNum != null) {
		               if (tokenNum.equals(currentWordForm)) {
		            	  isAlreadyEquals = true;  // The wordForm is equal to currentWordForm => se paso
		            	  
		               } else {
		            	   tokenTemp = TextDatabase.selectCurrentTokenTreeRecord(conn,currentToken.getTaxonDescriptionId(), currentToken.getLineNumber(), 
					                  currentToken.getSequence(), w.getForm(), w.getTag(), vTag1, vTag2, p_book_id, previousToken); 

		            	   if (theFirst == 1 && ( tokenTemp.isNumber() || tokenTemp.isUnitofMeasurement() )) {  // It is a number or unit of measure
		           		       
		   		        	   if (tokenTemp.isUnitofMeasurement()) { //If it is a unit of measure => get the left most child
		           		    	   tokenTemp = tokenLeftmostChild(conn, tokenTemp,  child, p_book_id, previousToken);
		           		    	   tokenNum = tokenTemp.getWordForm();
		           		       }
		            		   // The first child that is a number
		            		   theFirst++;
		            		   if ( (tokenNum.contains("-")) ){
		     			         parts = tokenNum.split("-");
		     			         v_rightNum = parts[0];
		     			    } else v_rightNum = tokenNum;
		            	   }   
		               
		               }
		             }  
		          }
		        }
		    }
   
	   
   }
	
	return (v_rightNum);
}


private static Boolean leftSiblingIsParentesis(TreeDepnode tr, TreeDepnode trParent, String currentWordForm){
		// Verifica si el hermano izquierdo de tr es parentesis izquierdo	
	
    TreeDepnode child = null;
    String vWordForm;
    Boolean isParentesis = false;  
    Boolean isAlreadyEquals = false;
    
    // If tr.child is parentesis return true
    Long nch = tr.numChildren();

    if( nch > 0 ) {
        
        for( int i = 0; i < nch; i++ ) {
          child = tr.nthChildRef( i );
      
          if( child != null ) {
             vWordForm = child.getInfo().getWord().getForm();
             if (vWordForm != null) {
               if (vWordForm.equals(currentWordForm)) {
            	  isAlreadyEquals = true;  // The wordForm is equal to currentWordForm => se paso
            	  
               } else {
            	   if (!isAlreadyEquals &&  vWordForm.equals("("))
            		   isParentesis = true;
               }
             }  
          }
        }
    }
    return (isParentesis);
}

private static TokenParser rigthSibling (Connection conn, TreeDepnode trParent, TokenParser currentToken, Integer p_book_id, 
		TokenParser previousToken){
	//Return the rigth sibling of tr/currentToken.	

TreeDepnode child = null;
String vWordForm;
TokenParser  theRigthSibling = null;  
Boolean isAlreadyEquals = false;
Integer theFirst = 0;
String vTag1, vTag2;
Word w ;


Long nch = trParent.numChildren();

if( nch > 0 ) {
    
    for( int i = 0; i < nch; i++ ) {
      child = trParent.nthChildRef( i );
  
      if( child != null ) {
         vWordForm = child.getInfo().getWord().getForm();
         if (vWordForm != null) {
           if (vWordForm.equals(currentToken.getWordForm())) {
        	  isAlreadyEquals = true;  // The wordForm is equal to currentWordForm => the next token is the rigth sibling.
        	  
           } else { //Only consider the first node after currentNode.
        	   if (isAlreadyEquals && theFirst ==0){
        		  theFirst ++;
  		          vTag1 = child.getInfo().getLinkRef().getInfo().getLabel();
  		          vTag2 = child.getInfo().getLabel();
      	          w = child.getInfo().getWord();
        		
  		          // Search in the table TOKEN_TREE the corresponding information associated to the current node.  
        		  theRigthSibling = TextDatabase.selectCurrentTokenTreeRecord(conn,currentToken.getTaxonDescriptionId(), currentToken.getLineNumber(), 
		                  currentToken.getSequence(), w.getForm(), w.getTag(), vTag1, vTag2, p_book_id, previousToken); 

        	   }
           }
         }  
      }
    }
}
return (theRigthSibling);
}



private static Boolean leftChildIsParentesis(TreeDepnode tr){
// Verifica si el hijo de tr es parentesis derecho 
		 	
    TreeDepnode child = null;
    String vWordForm;
    Boolean isParentesis = false;    
    
    // If tr.child is parentesis
    Long nch = tr.numChildren();

    if( nch > 0 ) {
		    
	   child = tr.nthChildRef( 0 );
		            
	   if( child != null ) {
		  vWordForm = child.getInfo().getWord().getForm();  
	      System.out.println(
		  "( Token izquierdo :  " +vWordForm );	
		        	
		  if( vWordForm != null && vWordForm.equals("(")) {
		     // It must be the first adjective or verb
		     isParentesis = true; 		
		        	      
		   }  
	   }
    }    
		   
    return (isParentesis);
}


public static String areaPartLengthOrWidth (Connection conn, TreeDepnode tr, TreeDepnode trParent, TokenParser parentToken, TokenParser currentToken, 
		Integer p_book_id, Boolean hasParenthesis){
	/* Si el numero esta antes de la "x" (Knowledge.type = G) entonces corresponde a length 
	   If number is left child of G (area indicator) => name = length
	   If G is the leftmost child of number=> name = length
	   If number is left sibling of G => name = length
	   Other cases => name = width
    */
	  String theName= "width";
      TreeDepnode child = null;
      Long nch ;
      String vTag1, vTag2;
      
      TokenParser tokenTemp;
	  Boolean pasedG = false;
	 	  
	  Word w ;
	  
	  // If current node is root => the name = length 
	  if (trParent == null) theName = "length";  
	  

	  // If number is the leftmost child of G => then Name = length
	  if ((trParent != null) && (tr != null) && (trParent.numChildren()>0) && (parentToken.getKnowledgeType()!=null) &&(parentToken.getKnowledgeType().equals("G")) ) {
		 child = trParent.nthChildRef( 0 ); 
         if ((child != null )) {
	          vTag1 = child.getInfo().getLinkRef().getInfo().getLabel();
	          vTag2 = child.getInfo().getLabel();
	              w = child.getInfo().getWord();
             tokenTemp = TextDatabase.selectCurrentTokenTreeRecord(conn,currentToken.getTaxonDescriptionId(), currentToken.getLineNumber(), 
                 currentToken.getSequence(), w.getForm(), w.getTag(), vTag1, vTag2, p_book_id, parentToken); 
	 
	     if ( tokenTemp.getWordForm().equals(currentToken.getWordForm())) {
	    	theName = "length";	 
	     }  
	   }
	  }   
	  
	  // If G (area indicator) is the leftmost child of current token (number) => name = length
	  if ( (theName.equals("width")) && (tr != null) && (tr.numChildren() >0) ) {

	      child =  tr.nthChildRef( 0 );
	      if ((child != null ) && (theName.equals("width"))) {
		     vTag1 = child.getInfo().getLinkRef().getInfo().getLabel();
		     vTag2 = child.getInfo().getLabel();
  	         w = child.getInfo().getWord();
	         tokenTemp = TextDatabase.selectCurrentTokenTreeRecord(conn,currentToken.getTaxonDescriptionId(), currentToken.getLineNumber(), 
	                  currentToken.getSequence(), w.getForm(), w.getTag(), vTag1, vTag2, p_book_id, parentToken); 
		 
     	     if (tokenTemp.isArea()) {
	    	       theName = "length";	 
	         }  
	      }
	  }   
	  
	  // If number is left sibling of G => name = length
	  if ((theName.equals("width")) && ((trParent != null) )) {	
        nch = trParent.numChildren();

	    if( nch > 0 ) {
	    
	      for( int i = 0; i < nch; i++ ) {
	        child = trParent.nthChildRef( i );
      
	        if ((child != null ) && (!pasedG))  {
		        vTag1 = child.getInfo().getLinkRef().getInfo().getLabel();
		        vTag2 = child.getInfo().getLabel();
  	            w = child.getInfo().getWord();
		        
		        tokenTemp = TextDatabase.selectCurrentTokenTreeRecord(conn,currentToken.getTaxonDescriptionId(), currentToken.getLineNumber(), 
		                  currentToken.getSequence(), w.getForm(), w.getTag(), vTag1, vTag2, p_book_id, parentToken); 

			    if ( (tokenTemp != null) && tokenTemp.isArea()){
			     // The number must be before the area indicator (G).  
			         pasedG = true;
			    }
			    
			    if ( (tokenTemp != null) && (tokenTemp.getWordForm() != null) && (currentToken.getWordForm()  != null) 
			    	 &&  (tokenTemp.getWordForm().equals(currentToken.getWordForm()))){ 
			    	theName = "length";	
	            }  
	        }
	     }
	     if (!pasedG) theName = "width";  // If G (area indicator) is not a sibling but the grand father.
	      
	     }  
	  }
	
	
	 if (hasParenthesis) {
	    // If the number is beetwen parenthesis	 => is an atypical range
		 if (theName.equals("width")) theName= "atypical_width";
		 else  theName= "atypical_length";	 
     }
  
	  
	  
	return (theName);
}




private static String fromPrepositionToEndofChunk(Connection conn, TokenParser currentToken, TreeDepnode tr){
	// Devuelve la hilera desde la el currentToken.getWordForm hasta el final del chunk
	// Envia como parametro el hijo izquierdo del nodo actual por si hay mas de una preposicion igual en el mismos chunk.
			 	
	    TreeDepnode child = null;
	    String vCloseWord = null;
	    String prepositionString;
	    
	    Long nch = tr.numChildren();

	    if( nch > 0 ) {
			    
		   child = tr.nthChildRef( 0 );
			            
		   if( child != null ) {
			  vCloseWord = child.getInfo().getWord().getForm();  
		   }
	    }
	    
		prepositionString = TextDatabase.fromPrepositiontoEndString(conn, currentToken, vCloseWord);
	    
	    return (prepositionString);
	}



// OTHER METHODS) ===============================================================================================//

private static void saveSentences(Connection conn, Integer p_id,  ListSentence p_ls , Integer p_book_id) {
//
    /* - Description:  Concatenates tokens that are part of a sentence and save the normalize result inside the database (table Clause).
	*                
	* - Revision History:
	*     10/12/2014 - Maria Aux. Mora
	*     
	* - Arguments (input / output):
	*    conn               : database opened connection.
	*    Integer p_id,      : taxon_description_id
	*    ListSentence p_ls  : Freeling list sentence.
	*    p_book_id          : Book identifier. 
	*    
	* - Return Values:  
	*/		
	
	
	String temp_sentence;
	Integer i = 1;  // Clause number inside a taxon description  
	
	temp_sentence = "";
	System.out.println("Entre a parsear");
	ListSentenceIterator sIt = new ListSentenceIterator(p_ls);
      while (sIt.hasNext()) {
        Sentence s = sIt.next();
        ListWordIterator wIt = new ListWordIterator(s);
        while (wIt.hasNext()) {
          Word w = wIt.next();
          temp_sentence = temp_sentence + " " + w.getForm();
           }
       TextDatabase.CreateClauseRecord(conn, p_id , 1, 1, temp_sentence, null, null, i, p_book_id);
       System.out.println(temp_sentence);
       temp_sentence = "";
       i++;
      }	
}


  private static void printSenses( Word w ) {
    String ss = w.getSensesString();

    // The senses for a FreeLing word are a list of
    // pair<string,double> (sense and page rank). From java, we
    // have to get them as a string with format
    // sense:rank/sense:rank/sense:rank
    // which will have to be splitted to obtain the info.
    //
    // Here, we just output it:
    System.out.print( " " + ss );
  }

  private static void printResults( ListSentence ls, String format ) {

	    if (format == "parsed") {
	      System.out.println( "-------- CHUNKER results -----------" );

	      ListSentenceIterator sIt = new ListSentenceIterator(ls);
	      while (sIt.hasNext()) {
		Sentence s = sIt.next();
	        TreeNode tree = s.getParseTree();
	        printParseTree( 0, tree );
	      }
	    }
	    else if (format == "dep") {
	      System.out.println( "-------- DEPENDENCY PARSER results -----------" );

	      ListSentenceIterator sIt = new ListSentenceIterator(ls);
	      while (sIt.hasNext()) {
		Sentence s = sIt.next();
	        TreeDepnode tree = s.getDepTree();
	        printDepTree( 0, tree);
	      }
	    }
	    else
	    {
	      System.out.println( "-------- TAGGER results -----------" );
	       
	      // get the analyzed words out of ls.
	      ListSentenceIterator sIt = new ListSentenceIterator(ls);
	      while (sIt.hasNext()) {
	        Sentence s = sIt.next();
	       
	        ListWordIterator wIt = new ListWordIterator(s);
	        while (wIt.hasNext()) {
	          Word w = wIt.next();

	          System.out.print(
	            w.getForm() + " " + w.getLemma() + " " + w.getTag() );
	          printSenses( w );
	         System.out.println();
	        }
	       System.out.println();
	      }
	    }
	  }  
 
  
  private static void saveResults(Integer v_id, Integer v_line_number, Integer v_sequence, 
		  ListSentence ls, String format , Integer p_book_id) {

    if (format == "parsed") {
      System.out.println( "-------- CHUNKER results -----------" );

      ListSentenceIterator sIt = new ListSentenceIterator(ls);
      while (sIt.hasNext()) {
	Sentence s = sIt.next();
        TreeNode tree = s.getParseTree();
        printParseTree( 0, tree );
      }
    }
    else if (format == "dep") {
      System.out.println( "-------- DEPENDENCY PARSER results -----------" );

      ListSentenceIterator sIt = new ListSentenceIterator(ls);
      while (sIt.hasNext()) {
	    Sentence s = sIt.next();
        TreeDepnode tree = s.getDepTree();
        printDepTree( 0, tree);
        
        System.out.println( "-------- SEQUENCE -----------" + v_sequence );
        
        saveDepTree( 0, tree, v_id, v_line_number, v_sequence, p_book_id);
      }
    }
    else
    {
      System.out.println( "-------- TAGGER results -----------" );

      // get the analyzed words out of ls.
      ListSentenceIterator sIt = new ListSentenceIterator(ls);
      Integer i;
      
      while (sIt.hasNext()) {
        Sentence s = sIt.next();
        ListWordIterator wIt = new ListWordIterator(s);
        i =1;
        while (wIt.hasNext()) {
          Word w = wIt.next();

          System.out.print(
        		  w.getForm() + " " + w.getLemma() + " " + w.getTag() );
          printSenses( w );
               
          
          tokenList.add(new Token(null, v_id, v_line_number, w.getForm(), w.getLemma(),  w.getTag(), null, i, p_book_id));
          i++;
                    
          /* System.out.println(); */
          }
        /* System.out.println(); */
      }
      
    }
  }

  private static void printParseTree( int depth, TreeNode tr ) {
	   Word w;
	    TreeNode child;
	    long nch;

	    // Indentation
	    for( int i = 0; i < depth; i++ ) {
	      System.out.print( "  " );
	    }

	    nch = tr.numChildren();

	    if( nch == 0 ) {
	      // The node represents a leaf
	      if( tr.getInfo().isHead() ) {
	        System.out.print( "+" );
	      }
	      w = tr.getInfo().getWord();
	      System.out.print(
	        "(" + w.getForm() + " " + w.getLemma() + " " + w.getTag() );
	      printSenses( w );
	      System.out.println( ")" );
	    }
	    else
	    {
	      // The node probably represents a tree
	      if( tr.getInfo().isHead() ) {
	        System.out.print( "+" );
	      }

	      System.out.println( tr.getInfo().getLabel() + "_[" );

	      for( int i = 0; i < nch; i++ ) {
	        child = tr.nthChildRef( i );

	        if( child != null ) {
	          printParseTree( depth + 1, child );
	        }
	        else {
	          System.err.println( "ERROR: Unexpected NULL child." );
	        }
	      }
	      for( int i = 0; i < depth; i++ ) {
	        System.out.print( "  " );
	      }

	      System.out.println( "]" );
	    }
	  }

  private static void saveDepTreeClause( int depth, TreeDepnode tr , Integer v_id, Integer v_line_number, 
		   Integer p_book_id) {
	  
	//  Version del metod que procesaba clausulas pero ya no se usa.   
	  
    TreeDepnode child = null;
    TreeDepnode fchild = null;
    Depnode childnode;
    long nch;
    int last, min;
    Boolean trob;
    String tag1, tag2;
   

    for( int i = 0; i < depth; i++ ) {
      System.out.print( "  " );
    }
    
    tag1 = tr.getInfo().getLinkRef().getInfo().getLabel();
    tag2 = tr.getInfo().getLabel();

    System.out.print(
       tag1 + "/" + tag2 + "/" );

    Word w = tr.getInfo().getWord();

    System.out.print(
      "(" + w.getForm() + " " + w.getLemma() + " " + w.getTag() );
    printSenses( w );
    System.out.print( ")" );

    // Insert new token in tokenTreeList 
    // Parameters Integer p_token_id, Integer p_taxon_descrition_id, Integer  p_line_number, 
    //            String p_form, String p_lemma, String p_tag, String p_tag1, String p_tag2, Integer p_sequence
    
       tokenTreeList.add(new TokenParser(null, v_id, v_line_number, w.getForm(), w.getLemma(),  w.getTag(), tag1, tag2, p_book_id));
    
    nch = tr.numChildren();

    if( nch > 0 ) {
      System.out.println( " [" );
       tokenTreeList.add(new TokenParser(null, v_id, v_line_number, "[", null, null, null, null, p_book_id));
      
      for( int i = 0; i < nch; i++ ) {
        child = tr.nthChildRef( i );

        if( child != null ) {
          if( !child.getInfo().isChunk() ) {
            saveDepTreeClause( depth + 1, child,   v_id,  v_line_number,  p_book_id);
          }
        }
        else {
          System.err.println( "ERROR: Unexpected NULL child." );
        }
      }

      // Print chunks (in order)
      last = 0;
      trob = true;

      // While an unprinted chunk is found, look for the one with lower
      // chunk_ord value.
      while( trob ) {
        trob = false;
        min = 9999;

        for( int i = 0; i < nch; i++ ) {
          child = tr.nthChildRef( i );
          childnode = child.getInfo();

          if( childnode.isChunk() ) {
            if( (childnode.getChunkOrd() > last) &&
                (childnode.getChunkOrd() < min) ) {
              min = childnode.getChunkOrd();
              fchild = child;
              trob = true;
            }
          }
        }
        if( trob && (child != null) ) {
          saveDepTreeClause( depth + 1, fchild, v_id, v_line_number, p_book_id );
        }

        last = min;
      }

      for( int i = 0; i < depth; i++ ) {
        System.out.print( "  " );
      }

      System.out.print( "]" );
    
      tokenTreeList.add(new TokenParser(null, v_id, v_line_number, "]", null, null, null, null, p_book_id));
    }

    System.out.println( "" );
  }
  
  private static void saveDepTree( int depth, TreeDepnode tr , Integer v_id, Integer v_line_number, 
		  Integer v_sequence, Integer p_book_id) {
   // Method in use to save chunks into the token_Tree table.
	  
	  
    TreeDepnode child = null;
    TreeDepnode fchild = null;
    Depnode childnode;
    long nch;
    int last, min;
    Boolean trob;
    String tag1, tag2;
   

    for( int i = 0; i < depth; i++ ) {
      System.out.print( "  " );
    }
    
    tag1 = tr.getInfo().getLinkRef().getInfo().getLabel();
    tag2 = tr.getInfo().getLabel();

    System.out.print(
       tag1 + "/" + tag2 + "/" );

    Word w = tr.getInfo().getWord();

    
    System.out.print(
      "(" + w.getForm() + " " + w.getLemma() + " " + w.getTag() );
    printSenses( w );
    System.out.print( ")" );

    // Save information about token father
    // Parameters Integer p_token_id, Integer p_taxon_descrition_id, Integer  p_line_number, 
    //            String p_form, String p_lemma, String p_tag, String p_tag1, String p_tag2, Integer p_sequence
    //            book_id, knowledgeType, ontologyCategory, ontologyId
    
    
    
    tokenTreeList.add(new TokenParser(null, v_id, v_line_number, w.getForm(), w.getLemma(),  w.getTag(), tag1, tag2, v_sequence, p_book_id));
    
    nch = tr.numChildren();

    if( nch > 0 ) {
      System.out.println( " [" );
       tokenTreeList.add(new TokenParser(null, v_id, v_line_number, "[", null, null, null, null, v_sequence, p_book_id));
      
      for( int i = 0; i < nch; i++ ) {
        child = tr.nthChildRef( i );

        if( child != null ) {
          if( !child.getInfo().isChunk() ) {
            saveDepTree( depth + 1, child,   v_id,  v_line_number, v_sequence, p_book_id);
          }
        }
        else {
          System.err.println( "ERROR: Unexpected NULL child." );
        }
      }

      // Print chunks (in order)
      last = 0;
      trob = true;

      // While an unprinted chunk is found, look for the one with lower
      // chunk_ord value.
      while( trob ) {
        trob = false;
        min = 9999;

        for( int i = 0; i < nch; i++ ) {
          child = tr.nthChildRef( i );
          childnode = child.getInfo();

          if( childnode.isChunk() ) {
            if( (childnode.getChunkOrd() > last) &&
                (childnode.getChunkOrd() < min) ) {
              min = childnode.getChunkOrd();
              fchild = child;
              trob = true;
            }
          }
        }
        if( trob && (child != null) ) {
          saveDepTree( depth + 1, fchild, v_id, v_line_number, v_sequence, p_book_id );
        }

        last = min;
      }

      for( int i = 0; i < depth; i++ ) {
        System.out.print( "  " );
      }

      System.out.print( "]" );
    
      tokenTreeList.add(new TokenParser(null, v_id, v_line_number, "]", null, null, null, null, v_sequence, p_book_id));
    }

    System.out.println( "" );
  }
  
  
  private static void printDepTree( int depth, TreeDepnode tr ) {
	// no hice ningun cambio mas que pasar los parametros	  
		  
		  
	    TreeDepnode child = null;
	    TreeDepnode fchild = null;
	    Depnode childnode;
	    long nch;
	    int last, min;
	    Boolean trob;

	    for( int i = 0; i < depth; i++ ) {
	      System.out.print( "  " );
	    }

	    System.out.print(
	      tr.getInfo().getLinkRef().getInfo().getLabel() + "/" +
	      tr.getInfo().getLabel() + "/" );

	    Word w = tr.getInfo().getWord();

	    System.out.print(
	      "(" + w.getForm() + " " + w.getLemma() + " " + w.getTag() );
	    printSenses( w );
	    System.out.print( ")" );

	    nch = tr.numChildren();

	    if( nch > 0 ) {
	      System.out.println( " [" );

	      for( int i = 0; i < nch; i++ ) {
	        child = tr.nthChildRef( i );

	        if( child != null ) {
	          if( !child.getInfo().isChunk() ) {
	            printDepTree( depth + 1, child );
	          }
	        }
	        else {
	          System.err.println( "ERROR: Unexpected NULL child." );
	        }
	      }

	      // Print chunks (in order)
	      last = 0;
	      trob = true;

	      // While an unprinted chunk is found, look for the one with lower
	      // chunk_ord value.
	      while( trob ) {
	        trob = false;
	        min = 9999;

	        for( int i = 0; i < nch; i++ ) {
	          child = tr.nthChildRef( i );
	          childnode = child.getInfo();

	          if( childnode.isChunk() ) {
	            if( (childnode.getChunkOrd() > last) &&
	                (childnode.getChunkOrd() < min) ) {
	              min = childnode.getChunkOrd();
	              fchild = child;
	              trob = true;
	            }
	          }
	        }
	        if( trob && (child != null) ) {
	          printDepTree( depth + 1, fchild);
	        }

	        last = min;
	      }

	      for( int i = 0; i < depth; i++ ) {
	        System.out.print( "  " );
	      }

	      System.out.print( "]" );
	    }

	    System.out.println( "" );
	  }
  
}

