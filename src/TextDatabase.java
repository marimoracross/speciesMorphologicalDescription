import java.sql.*; 
import java.sql.Date;
import java.util.*;

public class TextDatabase {

	
  public static Connection OpenConnection() throws SQLException {
	
	  String URL = "jdbc:postgresql://localhost:5432/textmining";
      String USER = "postgres";
      String PASS = "inbio2014";
      Connection conn = DriverManager.getConnection(URL, USER, PASS);
	   
	  return conn;
	  
  }
  
/* BOOK -----------------------------------------------------------------------------------------------------------*/

  public static String getBookDocumentation(Connection conn, Integer pBookId)
 	    /* Database 
		 * 
		 * - Description: 
		 *    Returns a book documnetation.
		 *   
		 * - Revision History:
		 *     3/10/2015 - Maria Aux. Mora
		 *     
		 * - Arguments (input / output):
		 *    conn   : database opened connection.
		 *    pBookId: book id.
		 * 
		 * - Return Values:
		 *     book documnetation.   
		*/	 
  
  
  { 
 	   ResultSet rs = null;
 	   String bookDocumentation = "";
     try
     {   
  	   PreparedStatement st ;

  	   if (pBookId != null){
  	      st = conn.prepareStatement("SELECT name, description, authors, publication_date FROM TEXT.BOOK WHERE ID = ? ");
  	      st.setInt(1,pBookId);
  			   
       	  rs = st.executeQuery();
       	  
   	      if (rs.next()){
   	    	bookDocumentation = rs.getString(3) + ". " + rs.getString(1) + ". " + rs.getString(4) ;
  	      }
  	   }  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (bookDocumentation);
  } 
  
/* TAXON_DESCRIPTION --------------------------------------------------------------------------------------------- */	
  

  public static String getTaxonScientificName(Connection conn, Integer p_id)
  { 
 	   ResultSet rs = null;
 	   String scientificName = "";
     try
     {   
  	   PreparedStatement st ;

  	   if (p_id != null){
  	      st = conn.prepareStatement("SELECT scientific_name FROM TEXT.TAXON_DESCRIPTION WHERE ID = ? ");
  	      st.setInt(1,p_id);
  			   
       	  rs = st.executeQuery();
       	  
   	      if (rs.next()){
  		     scientificName = rs.getString(1);
  	      }
  	   }  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (scientificName);
  }   
  
  public static Integer getMinTaxonId(Connection conn, Integer pBookId)
  { 
 	   ResultSet rs = null;
       Integer taxonId =null;
 	 try
     {   
  	   PreparedStatement st ;

  	   if (pBookId != null){
  	      st = conn.prepareStatement("SELECT min(id) FROM TEXT.TAXON_DESCRIPTION WHERE book_id = ? ");
  	      st.setInt(1,pBookId);
  			   
       	  rs = st.executeQuery();
       	  
   	      if (rs.next()){
  		     taxonId = rs.getInt(1);
  	      }
  	   }  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (taxonId);
  }   
  
  public static Integer getMaxTaxonId(Connection conn, Integer pBookId)
  { 
 	   ResultSet rs = null;
       Integer taxonId =null;
 	 try
     {   
  	   PreparedStatement st ;

  	   if (pBookId != null){
  	      st = conn.prepareStatement("SELECT max(id) FROM TEXT.TAXON_DESCRIPTION WHERE book_id = ? ");
  	      st.setInt(1,pBookId);
  			   
       	  rs = st.executeQuery();
       	  
   	      if (rs.next()){
  		     taxonId = rs.getInt(1);
  	      }
  	   }  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (taxonId);
  } 
  

  
  
  
  public static String getTaxonDescription(Connection conn, Integer p_id)
  { 
 	   ResultSet rs = null;
 	   String vDescription = "";
     try
     {   
  	   PreparedStatement st ;

  	   if (p_id != null){
  	      st = conn.prepareStatement("SELECT description FROM TEXT.TAXON_DESCRIPTION WHERE ID = ? ");
  	      st.setInt(1,p_id);
  			   
       	  rs = st.executeQuery();
       	  
   	      if (rs.next()){
   	    	vDescription = rs.getString(1);
  	      }
  	   }  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (vDescription);
  }   
  
  public static String getTaxonRank (Connection conn, Integer p_id)
  { 
 	   ResultSet rs = null;
 	   String vtaxonRank = "";
     try
     {   
  	   PreparedStatement st ;

  	   if (p_id != null){
  	      st = conn.prepareStatement("SELECT taxon_rank FROM TEXT.TAXON_DESCRIPTION WHERE ID = ? ");
  	      st.setInt(1,p_id);
  			   
       	  rs = st.executeQuery();
       	  
   	      if (rs.next()){
   	    	vtaxonRank = rs.getString(1);
  	      }
  	   }  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (vtaxonRank);
  }   
  
  
  
public static ResultSet SelectTaxonDescription(Connection conn, Integer p_id, Integer p_book_id)
 { 
	   ResultSet rs = null;
    try
    {   
 	   PreparedStatement st ;

 	   if (p_id != null){
 		   if (p_book_id != null) { 
 		      st = conn.prepareStatement("SELECT * FROM TEXT.TAXON_DESCRIPTION WHERE ID = ? AND BOOK_ID = ? ORDER BY ID");
 	          st.setInt(1,p_id);
 	          st.setInt(2,p_book_id);
 		   } else {
  		      st = conn.prepareStatement("SELECT * FROM TEXT.TAXON_DESCRIPTION WHERE ID = ? ORDER BY ID");
 	          st.setInt(1,p_id); 
 		   }
        }
 	   else 
 		   if (p_book_id != null) { 
 		       st = conn.prepareStatement("SELECT * FROM TEXT.TAXON_DESCRIPTION WHERE BOOK_ID = ? ORDER BY ID");
 		       st.setInt(1,p_book_id);
 		   } else {
 			   st = conn.prepareStatement("SELECT * FROM TEXT.TAXON_DESCRIPTION ORDER BY ID");
 		   }
 			   
 	    	         
 	   rs = st.executeQuery();
 	//   st.close();
 	  
     }
     catch(SQLException e)
     {
         e.printStackTrace();
     }
    return (rs);
 } 

public static void updateTaxonDescriptionAddFinalPoint(Connection conn, Integer p_book_id)
{/* - Description: Update taxon_descripition.description with a '.' at the end to standardize description.  Freeling needs an end of text. 
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
   try
   {   
	
       /* Statement prepare*/
	   String addRecord = "update text.taxon_description set description = description || '.' " +
                          " where id in ( select id from text.taxon_description " +
                          " where book_id = ? and (substr(trim(description), length(trim(description)))) <> '.'); ";
	   
       PreparedStatement query = conn.prepareStatement(addRecord);
      
       query.setInt(1, p_book_id);
       
       query.executeUpdate();
       
       close(query);
    }
    catch(SQLException e)
    {
        e.printStackTrace();
    }
} 
 
  
/* CLAUSE -------------------------------------------------------------------------------------------------------- */	

  public static void CreateClauseRecord(Connection conn, Integer p_taxon_description_id , Integer p_process_type_id, 
		  Integer p_contents_type_id, String p_contents, Date p_date_processed, String p_annotation, Integer p_line_number,
		  Integer p_book_id)
    {
       try
       {      	     
           /* Statement */
    	   String addRecord = "INSERT INTO text.CLAUSE ( taxon_description_id, process_type_id, CONTENTS_TYPE_ID, contents, date_processed, annotation, line_number, book_id) VALUES (? , ? , ? , ? , ? , ?, ?, ?);";
    	     
    	   PreparedStatement query = conn.prepareStatement(addRecord);     	      
    	           
           query.setInt(1, p_taxon_description_id);
           query.setInt(2, p_process_type_id);
           query.setInt(3 , p_contents_type_id);
           query.setString(4, p_contents);
           query.setDate(5, p_date_processed);
           query.setString(6 , p_annotation);
           query.setInt(7 ,  p_line_number);
           query.setInt(8, p_book_id);
           
           query.executeUpdate();
           close(query);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
       }
    } 
  
  
 
  
  
  public static void updateClauseStatistics(Connection conn, Integer p_book_id)
    {  /* - Description: Update Clause.POS using the first POS character of each token that is part of the clause. 
	 	*                
		* - Revision History:
		*    10/01/2015 - Maria Aux. Mora
		*     
		* - Arguments (input / output):
		*    conn               : database opened connection.
		*    p_book_id          : Book identifier. 
		*    
		* - Return Values:  
		*/
	  
	  
	  
       try
       {   // Select token in clauses with p_numClauses 
    	   ResultSet rs = null;
           PreparedStatement st ;
           String clausePOS;         // Concatenating of individual Token.pos[1]
           int i;
 	       int vTaxonDescriptionId ; //Token.taxon_description_id
 	       int vLineNumber;         //Token.line_number
 	       String  vPos;            // Token.pos 

 	      if (p_book_id != null) { 
      	     st = conn.prepareStatement("SELECT * FROM text.token WHERE book_id = ? ORDER BY taxon_description_id, " +
 	                                    " line_number, id;");
      	     st.setInt(1, p_book_id);
      	     rs = st.executeQuery();    	  
 	      } else {
     	     st = conn.prepareStatement("SELECT * FROM text.token ORDER BY taxon_description_id, line_number, id;");
     	     rs = st.executeQuery();
 	      }
 	      
     	  i= 1;
     	  clausePOS = "";
          int vTaxonDescriptionIdAnt = 0; //Token.taxon_description_id
	      int vLineNumberAnt = 0;         //Token.line_number

     	  // Create the Clause.POS concatenating individual token POS[1]
     	  while (rs.next())
     	    {  /* The process must fetch p_numClauses records to verify: */ 
       		   if (i==1) {
      	          vTaxonDescriptionIdAnt = rs.getInt(2); //Token.taxon_description_id
      		      vLineNumberAnt = rs.getInt(3);         //Token.line_number   
     		   }
     		   vTaxonDescriptionId = rs.getInt(2); //Token.taxon_description_id
     	       vLineNumber = rs.getInt(3);         //Token.line_number
     	       vPos = rs.getString(6);            // Token.pos 
     	       
     	       if ((vTaxonDescriptionIdAnt == vTaxonDescriptionId) && 
     	          (vLineNumberAnt == vLineNumber)) {
     	             clausePOS = clausePOS +  vPos.substring(0, 1);
     	             i++;
     			      System.out.println (" clausePOS + 1 :" + clausePOS + " " + i); 
     	       } else {
     	    	   
     	    	  // Update text.CLAUSE.POS  
     	          /* Statement prepare*/
     	    	  String addRecord = "UPDATE text.CLAUSE set POS = ?, num_characters = ? where taxon_description_id = ? and line_number =?;";  	    	  
     	          PreparedStatement query = conn.prepareStatement(addRecord);
     	          query.setString(1, clausePOS);
     	          query.setInt(2,i);
     	          query.setInt(3,vTaxonDescriptionIdAnt);
     	          query.setInt(4,vLineNumberAnt);
     	          
     	          query.executeUpdate(); 
     	         
     	          // Clean vars
     	     	  i= 1;
     	     	  clausePOS = "";
     		      
     		      // Use the current record before changing
     		      vPos = rs.getString(6);            // Token.pos 
     		      clausePOS =  vPos.substring(0, 1);
     		     System.out.println (" Id anteriores taxon_id + line_number :" + vTaxonDescriptionIdAnt + " " + vLineNumberAnt); 
     		      System.out.println (" clausePOS + 1 :" + clausePOS + " " + i); 
     	       }
     	       
     	    } // While 
     	    // Update last record
     	     if (vTaxonDescriptionIdAnt != 0) {
	    	   String addRecord = "UPDATE text.CLAUSE set POS = ?, num_characters = ? where taxon_description_id = ? and line_number =?;";  	    	  
 	           PreparedStatement query = conn.prepareStatement(addRecord);
 	           query.setString(1, clausePOS);
 	           query.setInt(2,i);
 	           query.setInt(3,vTaxonDescriptionIdAnt);
 	           query.setInt(4,vLineNumberAnt);
 	          
 	           query.executeUpdate(); 
            }
 
        }
        catch(SQLException e)
        {
            e.printStackTrace();
       }
    } 
  

  public static void normalizingClauses (Connection conn)
  { //Update text.Clause removing upper cases.  
	  	  
	  try
  {      	     
      /* Statement prepare*/
	  String addRecord = "UPDATE text.CLAUSE set CONTENTS = lower(contents);";
      PreparedStatement query = conn.prepareStatement(addRecord);
      query.executeUpdate();
   }
   catch(SQLException e)
   {
       e.printStackTrace();
   }
	  
  }
  
  
  public static void deleteClauses(Connection conn, Integer pBookId)
  { //Delete text.Clause of a book.  
	 
	Statement stmt = null;
	   
	  try
  {      	      
      stmt = conn.createStatement();
      String sql = "DELETE from text.CLAUSE WHERE book_id =  " + pBookId.toString();
      stmt.executeUpdate(sql);
      
   }
   catch(SQLException e)
   {
       e.printStackTrace();
   }
	  
  }
  
  
  
  public static void annotateClause (Connection conn, Integer pTaxonDescriptionId, Integer pLineNumber, String pAnnotation)
  { //Update a record text.Clause.  Annotate a clause   
	  	  
	  try
  {      	     
      PreparedStatement st = conn.prepareStatement("UPDATE TEXT.CLAUSE SET clause_annotation = ? " + 
                               " where taxon_description_id = ? and line_number = ?;");
	  st.setString(1, pAnnotation);
	  st.setInt(2, pTaxonDescriptionId);
	  st.setInt(3, pLineNumber);
	  
	  st.executeUpdate();
   }
   catch(SQLException e)
   {
       e.printStackTrace();
   }
	  
  }
  
  
  
  public static ResultSet selectClause(Connection conn, Integer p_text_mining_result_id, 
		  Integer p_taxon_description_id , Integer p_line_number, 
		  Integer p_contents_type_id, Integer p_book_id)
    { 
	   ResultSet rs = null;
       try
       {   
    	   /* Condition construction using parameters (WHERE CLAUSE)*/
    	   String condition = null;   
    	   PreparedStatement st ;
    	   
    	   if (p_text_mining_result_id != null) 
               condition = "id = ?";
          
    	   if (p_taxon_description_id != null) {
    		   if (condition != null)
                  condition = condition + "and taxon_description_id = ?";
    		   else               
    			  condition=  "taxon_description_id = ?";
    	   } 
    	   if (p_line_number != null) {
    		   if (condition != null)
                  condition = condition + "and line_number = ?";
    		   else               
    			  condition = "line_number = ?";
    	   } 
    	   if (p_contents_type_id != null) {
    		   if (condition != null)
                  condition = condition + "and contents_type_id = ?";
    		   else               
    			  condition = "contents_type_id = ?";
    	   } 
    	   if (p_book_id != null) {
    		   if (condition != null)
                  condition = condition + "and book_id = ?";
    		   else               
    			  condition = "book_id = ?";
    	   } 
    	   
    	   if (condition != null)
    		   st = conn.prepareStatement("SELECT * FROM TEXT.CLAUSE WHERE " + condition + " ORDER BY LINE_NUMBER");
    	   else 
    		   st = conn.prepareStatement("SELECT * FROM TEXT.CLAUSE  ORDER BY LINE_NUMBER");
    	   
    	   int i = 1;
    	   
    	   if (p_text_mining_result_id != null) {
               st.setInt(i,p_text_mining_result_id);
               i++;
           } 
    	   if (p_taxon_description_id != null) {
               st.setInt(i,p_taxon_description_id);
               i++;
    	   } 
    	   if (p_line_number != null) {
               st.setInt(i,p_line_number);
               i++;
    	   } 
    	   if (p_contents_type_id != null) {
               st.setInt(i,p_contents_type_id);
               i++;
    	   } 
    	   
    	   if (p_book_id != null) {
               st.setInt(i,p_book_id);
               i++;
    	   } 
    	   
    	   rs = st.executeQuery();
    	 //  st.close();
    	  
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
       return (rs);
    } 
  
  



  public static ResultSet SelectClause(Connection conn, Integer p_id)
  { 
	 ResultSet rs = null;
     
	 try
     {   
  	   PreparedStatement st ;

  	   if (p_id != null){
  		   st = conn.prepareStatement("SELECT * FROM TEXT.CLAUSE WHERE ID = ? ");
  	       st.setInt(1,p_id);
  	     }
  	   else 
  		   st = conn.prepareStatement("SELECT * FROM TEXT.CLAUSE ORDER BY ID");
  	    	         
  	   rs = st.executeQuery();
  	 /*  st.close();*/
  	  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (rs);
  } 
  
  public static String getClauseContents(Connection conn, Integer taxonDescriptionId, Integer lineNumber)
  { 
	 String theClause = "";
	 ResultSet rs;
     
	 try
     {   


  	   if (taxonDescriptionId != null && lineNumber != null){
  	  	   PreparedStatement st ;
  		   
  		   st = conn.prepareStatement("SELECT contents FROM TEXT.CLAUSE WHERE TAXON_DESCRIPTION_ID = ? AND LINE_NUMBER = ? ");
  	       st.setInt(1,taxonDescriptionId);
  	       st.setInt(2, lineNumber);
  	  	   rs = st.executeQuery();
  	       
  	     }
 	    	         
  	  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (theClause);
  } 
  
  
  
  public static ResultSet SelectClause(Connection conn, String p_pos)
  { 
	 ResultSet rs = null;
     
	 try
     {   
  	   PreparedStatement st ;

 	   st = conn.prepareStatement("SELECT * FROM TEXT.CLAUSE WHERE pos like ? ");
       st.setString(1,p_pos);
  	    	         
  	   rs = st.executeQuery();
  	 /*  st.close();*/
  	  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (rs);
  } 
  
  public static ResultSet getClauseContentsAndId(Connection conn, Integer taxonDescriptionId, Integer bookId)
  { 
	 ResultSet rs=null;  
     PreparedStatement st ;
  	 
	 try
     {   

  	   if (taxonDescriptionId != null){	   
  		   st = conn.prepareStatement("SELECT id, contents FROM TEXT.CLAUSE WHERE TAXON_DESCRIPTION_ID = ? AND " +
  		                              " BOOK_ID = ? "   );
  	       st.setInt(1,taxonDescriptionId);
  	       st.setInt(2, bookId);    
  	     } else {	   
    	   st = conn.prepareStatement("SELECT id, contents FROM TEXT.CLAUSE WHERE " +
                                        " BOOK_ID = ? "   );
           st.setInt(1, bookId);    
       }
  	   
  	   rs = st.executeQuery();  	 
  	  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (rs);
  } 
  
  public static void updateClauseContents (Connection conn, Integer pClauseId, String pContents)
  { //Update CLAUSE.contents with .  
	  	  
	  try    
  {      	     
      PreparedStatement st = conn.prepareStatement("UPDATE text.CLAUSE SET CONTENTS = ? where id = ?;");
	  st.setString(1, pContents);
	  st.setInt(2, pClauseId);
	  
	  st.executeUpdate();
   }
   catch(SQLException e)
   {
       e.printStackTrace();
   }
	  
  }
  
/* TOKEN --------------------------------------------------------------------------------------------------------- */	

  public static void createTokenRecord(Connection conn,  
		  ArrayList<Token> p_tokenList, Integer p_book_id)
    {
       try
       {      	     
           /* Statement prepare*/
    	   String addRecord = "INSERT INTO text.TOKEN (taxon_description_id, line_number, token, lemma,pos, final_tag, role, sequence, book_id) VALUES ( ?,? , ? , ? , ? , ?, ?,?, ?);";
           PreparedStatement query = conn.prepareStatement(addRecord);
          
           for (Token wordToken : p_tokenList) {

              query.setInt(1, wordToken.TaxonDescriptionId);
              query.setInt(2, wordToken.lineNumber);
              query.setString(3 , wordToken.wordForm);
              query.setString(4, wordToken.wordLemma);
              query.setString(5, wordToken.wordTag);
              query.setString(6 , null);
              query.setString(7 , null);
              query.setInt(8, wordToken.sequence);
              query.setInt(9, wordToken.book_id);
              
              query.executeUpdate();
           }
           close(query);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    } 
  
  public static void deleteTokens(Connection conn, Integer pBookId)
  { //Delete text.Tokens of a book.  
	
   Statement stmt = null;
  
	  
   try
    {      	     
      /* Statement prepare*/
	  
	  
	     stmt = conn.createStatement();
	     String sql = "DELETE from text.token WHERE book_id =  " + pBookId.toString();
	     stmt.executeUpdate(sql);
	     
	     stmt = conn.createStatement();
	     sql = "DELETE from text.token_tree WHERE book_id =  " + pBookId.toString();
	     stmt.executeUpdate(sql);
	      
   }
   catch(SQLException e)
   {
       e.printStackTrace();
   }
	  
  }
  
  public static void deleteChunks(Connection conn, Integer pBookId)
  { //Delete text.Chunks of a book.  
	  	  
    Statement stmt = null;
	   
	try
	{      	     
	 /* Statement prepare*/
		 	  
	  stmt = conn.createStatement();
	  String sql = "DELETE from text.chunk WHERE book_id =  " + pBookId.toString();
      stmt.executeUpdate(sql);
   }
   catch(SQLException e)
   {
       e.printStackTrace();
   }
	  
  }
  
  

  public static void updateTokenFinalTagRole (Connection conn, Integer p_id, String p_tag, String p_role)
  { //Update a record text.Token.  
	  	  
	  try
  {      	     
      PreparedStatement st = conn.prepareStatement("UPDATE text.TOKEN set final_tag = ?, role = ?  where id = ?;");
	  st.setString(1, p_tag);
	  st.setString(2, p_role);
	  st.setInt(3, p_id);
	  
	  st.executeUpdate();
   }
   catch(SQLException e)
   {
       e.printStackTrace();
   }
	  
  }

  public static void updateTokenEnglishLemma (Connection conn,  String p_lemma, String p_EnglishLemma, Integer p_book_id)
  { //Update a record text.Token.  
	  	  
  try
  { if (p_EnglishLemma != null)  {
	   if (!p_lemma.equals(p_EnglishLemma)) {		  
         PreparedStatement st = conn.prepareStatement("UPDATE text.TOKEN set english_lemma = ?  where lemma = ? and book_id = ?;");
	     st.setString(1, p_EnglishLemma);
	     st.setString(2, p_lemma);
	     st.setInt(3, p_book_id);
	  
	     st.executeUpdate();
     }
   }
  }
   catch(SQLException e)
   {
       e.printStackTrace();
   }
	  
  }  
  
  public static void updateKnowledgeEnglishLemmaFromLemma (Connection conn,  String p_lemma, String p_EnglishLemma)
  { //Update a record text.knowledge.  
	  	  
  try
  { if (p_EnglishLemma != null)  {
         PreparedStatement st = conn.prepareStatement("UPDATE text.knowledge set english_lemma = ?  where lemma = ?;");
	     st.setString(1, p_EnglishLemma);
	     st.setString(2, p_lemma);
	  
	     st.executeUpdate();
     }
  }
   catch(SQLException e)
   {
       e.printStackTrace();
   }
	  
  }  
  
  
  
  public static ResultSet SelectTokenByNumberperClause (Connection conn, Integer p_number)
  /* Description: return all tokens in a clause that has p_number tokens.
   *  
   * - Revision History:
   *     10/01/2015 - Maria Aux. Mora
   *     
   * - Arguments (input):
   *    conn : database opened connection.
   *    p_number: numbers of tokens in a clause
   *    
   * - Return Values:
   *     a resultSet with token records.
  */
  { 
	   ResultSet rs = null;
     try
     {   
  	   PreparedStatement st ;

  	   if (p_number != null){
  		   st = conn.prepareStatement("SELECT * FROM text.token WHERE (taxon_description_id, line_number,?) IN (select taxon_description_id, line_number, count(*) from text.token group by taxon_description_id, line_number) ORDER BY taxon_description_id, line_number, id;");
  	       st.setInt(1, p_number);
         }
  	   else 
  		   st = conn.prepareStatement("SELECT * FROM TEXT.TOKEN");
  	    	         
  	   rs = st.executeQuery();
  	//   st.close();
  	  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (rs);
  } 
    
  public static ResultSet SelectTokenByPOS (Connection conn, String p_pos, Integer p_init, Integer p_last, Integer p_book)
   /* - Description: select clauses where pos like p_pos and token number >= p_init and 
     *     token number <= p_last.
     *    
     * - Revision History:
     *     10/01/2015 - Maria Aux. Mora
     *      2/05/2015 - Maria Mora: p_book added as parameter
     *     
     * - Arguments (input / output):
     *    conn : database opened connection.
     *    p_pos: substring with clause's pos that will be searched.
     *    p_init: initial token position inside the clause that will be return.  
     *    p_last: final token  position inside the clause that will be return
     *    p_book: book identifier
     *    
     * - Return Values:
     *     A ResultSet with token's records that fulfill the condition. 
	*/
  
  {   
	  ResultSet rs = null;
       try
       {   
    	   PreparedStatement st ;
  
  		   st = conn.prepareStatement("SELECT * FROM text.token WHERE (taxon_description_id, line_number) " + 
  		                              " IN (select taxon_description_id, line_number from text.clause where pos like ?)  " +
                                      " and sequence >=? and sequence <=? and book_id = ? ORDER BY taxon_description_id, line_number, id; ");
   	       st.setString(1, p_pos);
   	       st.setInt(2, p_init);
   	       st.setInt(3, p_last);
   	       st.setInt(4, p_book);
    	    	         
    	   rs = st.executeQuery();
    	//   st.close();
    	  
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
       return (rs);
    } 

  
  public static ResultSet SelectTokenByS_a (Connection conn, Integer pBook) 
  
  /*  SelectTokenByCS (C=conjunctions and S= prepositions)
   * - Description: select all tokens which has an 'a','o','u', or 'y' between them.
    *    
    * - Revision History:
    *     24/04/2015 - Maria Aux. Mora
    *     
    * - Arguments (input / output):
    *    conn : database opened connection.
    *    pBook: Book identifer.

    * - Return Values:
    *     A ResultSet with token's records that fulfill the condition. 
	*/
 
 {   
	  ResultSet rs = null;
      try
      {   
   	   PreparedStatement st ;
 
 		   st = conn.prepareStatement("select  t.taxon_description_id, t.line_number, t.sequence, t.token, t.lemma, t.pos, k.type, t.english_lemma "
 			   + " from text.token t left join text.knowledge k  on trim(k.phrase) = trim(t.token)" 
 		       + " where book_id = ? and ( (taxon_description_id, line_number, sequence) in "
 			   + " (select taxon_description_id, line_number, sequence-1 from text.token where trim(token) = 'a')"
 			   + " or (taxon_description_id, line_number, sequence) in "
 		       + " (select taxon_description_id, line_number, sequence+1 from text.token where  trim(token) = 'a'))"
 			   + " order by t.taxon_description_id,t.line_number, t.sequence;"); 

 		   st.setInt(1, pBook);
        
   	   rs = st.executeQuery();
   		    	  
       }
       catch(SQLException e)
       {
           e.printStackTrace();
       }
      return (rs);
   } 
  
  	   
 public static ResultSet SelectTokenWithType (Connection conn, Integer pBook) 
	   
	   /*  SelectTokenwithType 
	    * - Description: Join table token + table knowledge to associate a type to each token 
	     *               the join is done using the field lemma.
	     *                     
	     * - Revision History:
	     *     1/05/2015 - Maria Aux. Mora
	     *     
	     * - Arguments (input / output):
	     *    conn : database opened connection.
	     *    pBook: book identifer.

	     * - Return Values:
	     *     A ResultSet with token's records that fulfill the condition. 
	 	*/
	  
	  {   
	 	  ResultSet rs = null;
	       try
	       {   
	    	   PreparedStatement st ;
	  
	  		   st = conn.prepareStatement("select  t.taxon_description_id, t.line_number, t.sequence, t.token, t.lemma, t.pos, k.type, t.english_lemma "
	  			   + " from text.token t left join text.knowledge k  on trim(k.lemma) = trim(t.lemma)" 
	  		       + " where book_id = ? "
	  			   + " order by t.taxon_description_id,t.line_number, t.sequence;"); 
	  		   st.setInt(1, pBook);
	         
	    	   rs = st.executeQuery();
	    		    	  
	        }
	        catch(SQLException e)
	        {
	            e.printStackTrace();
	        }
	       return (rs);
	    } 
	   
 
 
 
public static ResultSet SelectTokenJoinOTOWhereNotincludedInKnowledge (Connection conn, Integer pBook) 
 
 /*  
  * - Description: select all token where english_lemma was bad translated and it does not match with OTO.
   *    
   * - Revision History:
   *     8/06/2015 - Maria Aux. Mora
   *     
   * - Arguments (input / output):
   *    conn : database opened connection.
   *    pBook: book identifier.

   * - Return Values:
   *     A ResultSet with token's records that fulfill the condition. 
	*/

{   
	  ResultSet rs = null;
     try
     {   
  	   PreparedStatement st ;

		   st = conn.prepareStatement("select distinct t.token,  t.lemma, t.english_lemma, k.term, k.category, g.type, t.pos " +
                                      " from text.token t left join text.oto k  on trim(k.term) = trim(t.english_lemma) " +
                                      " left join text.knowledge g on trim(g.phrase) = trim(t.token) "  +
                                      " where book_id = ?  and k.term is not null and g.type is null " +
                                      "   order by t.lemma;"); 

		   st.setInt(1, pBook);
       
  	   rs = st.executeQuery();
  		    	  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (rs);
  } 
 

public static ResultSet SelectTokenJoinKnowledgeWhereEnglishLemmaisDifferent(Connection conn, Integer pBook) 

/*  
 * - Description: select all token.english_lemma where english_lemma does not match with knowledge.English_lemma.
  *    
  * - Revision History:
  *     10/06/2015 - Maria Aux. Mora
  *     
  * - Arguments (input / output):
  *    conn : database opened connection.
  *    pBook: book identifier.

  * - Return Values:
  *     A ResultSet with token's records that fulfill the condition. 
	*/

{   
	  ResultSet rs = null;
    try
    {   
 	   PreparedStatement st ;

		   st = conn.prepareStatement("select distinct  trim(t.english_lemma),k.english_lemma ,trim(t.token), k.phrase from text.token t , text.knowledge k "+ 
                                      " where book_id = ? and trim(k.phrase) = trim(t.token) and t.english_lemma <> k.english_lemma;"); 

		   st.setInt(1, pBook);
      
 	   rs = st.executeQuery();
 		    	  
     }
     catch(SQLException e)
     {
         e.printStackTrace();
     }
    return (rs);
 }  
 
 
 public static ResultSet SelectTokenWhereOTONotMatch (Connection conn, Integer pBook) 
 
 /*  
  * - Description: select all token where english_lemma was bad translated and it does not match with OTO.
   *    
   * - Revision History:
   *     8/06/2015 - Maria Aux. Mora
   *     
   * - Arguments (input / output):
   *    conn : database opened connection.
   *    pBook: book identifier.

   * - Return Values:
   *     A ResultSet with token's records that fulfill the condition. 
	*/

{   
	  ResultSet rs = null;
     try
     {   
  	   PreparedStatement st ;

		   st = conn.prepareStatement("select distinct t.token, t.lemma, g.english_lemma,  k.category, count(*)" +
                          " from text.token t " +
                          " left join text.knowledge g on trim(g.lemma) = trim(t.lemma) " +
                          " left join text.oto k  on trim(k.term) = trim(g.english_lemma) "+
                          " where book_id = ? and  k.category is null and (g.type = 'A' ) "+
                          " group by 1,2,3,4 order by 1,2,3;"); 

		   st.setInt(1, pBook);
	   
  	   rs = st.executeQuery();
  		    	  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (rs);
  } 
 
 public static ResultSet SelectTokenByChunk (Connection conn, Integer pBook) 
 
 /*  SelectTokenByChunk
  * - Description: select the first and second words after ",:.;" from table token joined with knowledge.
   *    
   * - Revision History:
   *     3/0/2015 - Maria Aux. Mora
   *     
   * - Arguments (input / output):
   *    conn : database opened connection.
   *    pBook: book identifier.

   * - Return Values:
   *     A ResultSet with token's records that fulfill the condition. 
	*/

{   
	  ResultSet rs = null;
     try
     {   
  	   PreparedStatement st ;

		   st = conn.prepareStatement("select  t.taxon_description_id, t.line_number, t.sequence, t.token,t.lemma, t.pos, k.type, t.english_lemma " +
		              " from text.token t left join text.knowledge k  on trim(k.phrase) = trim(t.token) " +
                      " where book_id = ? and ((taxon_description_id, line_number, sequence) in (select taxon_description_id, line_number, " +
		              " sequence+1 from text.token where trim(token)  in ( ',' , '.', ';', ':')) or "+
                      " (taxon_description_id, line_number, sequence) in (select taxon_description_id, line_number, sequence+2 " +
		              " from text.token where  trim(token) in (',' , '.', ';', ':') )) order by 1,2,3;"); 

		   st.setInt(1, pBook);
       
  	   rs = st.executeQuery();
  		    	  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (rs);
  } 
 
 public static ResultSet SelectofTokenNamesAndAdjectives (Connection conn, Integer pBook) 
 
 /*  SelectTokenByChunk
  * - Description: select name and adjectives from table token joined with knowledge.
   *    
   * - Revision History:
   *     3/0/2015 - Maria Aux. Mora
   *     
   * - Arguments (input / output):
   *    conn : database opened connection.
   *    pBook: book identifier.

   * - Return Values:
   *     A ResultSet with token's records that fulfill the condition. 
	*/

{   
	  ResultSet rs = null;
     try
     {   
  	   PreparedStatement st ;

		   st = conn.prepareStatement("select  t.taxon_description_id, t.line_number, t.sequence, t.token, t.lemma, t.pos, k.type, t.english_lemma " +
                          " from text.token t left join text.knowledge k  on trim(k.phrase) = trim(t.token) " +
                          " where book_id = ? and ((t.pos like 'N%' and k.type is null) or k.type = 'A') " +
                          "  order by t.taxon_description_id,t.line_number, t.sequence;" ); 

	   st.setInt(1, pBook);
  	   rs = st.executeQuery();
  		    	  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (rs);
  } 
 
 
 
 public static ResultSet SelectTokenByC (Connection conn, Integer pBook) 
 
 /*  SelectTokenByCS (C=conjunctions)
  * - Description: select all tokens which has an 'o','u', or 'y' between them.
   *    
   * - Revision History:
   *     24/04/2015 - Maria Aux. Mora
   *     30/04/-2015 - Maria Mora preposition 'a' was removed
   *     
   * - Arguments (input / output):
   *    conn : database opened connection.
   *    p_pos: substring with the clause.pos that will be searched.

   * - Return Values:
   *     A ResultSet with token's records that fulfill the condition. 
	*/

{   
	  ResultSet rs = null;
     try
     {   
  	   PreparedStatement st ;

		   st = conn.prepareStatement("select  t.taxon_description_id, t.line_number, t.sequence, t.token, t.lemma, t.pos, k.type, t.english_lemma "
			   + " from text.token t left join text.knowledge k  on trim(k.phrase) = trim(t.token)" 
		       + " where book_id = ? and ( (taxon_description_id, line_number, sequence) in "
			   + " (select taxon_description_id, line_number, sequence-1 from text.token where trim(token) in ( 'o', 'u'))"
			   + " or (taxon_description_id, line_number, sequence) in "
		       + " (select taxon_description_id, line_number, sequence+1 from text.token where  trim(token) in ( 'o', 'u')))"
			   + " order by t.taxon_description_id,t.line_number, t.sequence;"); 

		   st.setInt(1, pBook);
       
  	   rs = st.executeQuery();
  		    	  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (rs);
  } 
 
 

 public static ResultSet SelectTokenByToken (Connection conn, String p_token, Integer p_book) 
 
 /* - Description: select all tokens where token like p_token and book_id = p_book 
   *    
   * - Revision History:
   *     7/05/2015 - Maria Aux. Mora
   *     
   * - Arguments (input / output):
   *    conn : database opened connection.
   *    p_token: substring with the clause.pos that will be searched.
   *    p_book : book identifier

   * - Return Values:
   *     A ResultSet with token's records that fulfill the condition. 
	*/

{   
	  ResultSet rs = null;
     try
     {   
  	   PreparedStatement st ;

		   st = conn.prepareStatement("SELECT * FROM text.token WHERE  token like ? and book_id = ?;");
 	       st.setString(1, p_token);
 	       st.setInt(2, p_book);
  	    	         
  	   rs = st.executeQuery();
  	//   st.close();
  	  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (rs);
  } 
 
  
  public static ResultSet SelectTokenByPOS (Connection conn, String p_pos, Integer p_book) 
  
  /* - Description: select all tokens where pos like p_pos and book_id = p_book 
    *    
    * - Revision History:
    *     18/01/2015 - Maria Aux. Mora
    *     
    * - Arguments (input / output):
    *    conn : database opened connection.
    *    p_pos: substring with the clause.pos that will be searched.
    *    p_book : book identifier

    * - Return Values:
    *     A ResultSet with token's records that fulfill the condition. 
	*/
 
 {   
	  ResultSet rs = null;
      try
      {   
   	   PreparedStatement st ;
 
 		   st = conn.prepareStatement("SELECT * FROM text.token WHERE  pos like ? and book_id = ?;");
  	       st.setString(1, p_pos);
  	       st.setInt(2, p_book);
   	    	         
   	   rs = st.executeQuery();
   	//   st.close();
   	  
       }
       catch(SQLException e)
       {
           e.printStackTrace();
       }
      return (rs);
   } 
  
public static ResultSet SelectTokenByNullType (Connection conn, Integer p_book) 
  
  /* - Description: select all tokens where type is null (type is part of table knowledge) 
    *    
    * - Revision History:
    *     16/11/2015 - Maria Aux. Mora
    *     
    * - Arguments (input / output):
    *    conn : database opened connection.
    *    p_pos: substring with the clause.pos that will be searched.
    *    p_book : book identifier

    * - Return Values:
    *     A ResultSet with token's records that fulfill the condition. 
	*/
 
 {   
	  ResultSet rs = null;
      try
      {   
   	   PreparedStatement st ;
 
 		   st = conn.prepareStatement("select  distinct (t.token), t.pos,  k.type, t.lemma, t.english_lemma " +
                 " from text.token t left join text.knowledge k  on trim(k.phrase) = trim(t.token) " +
                 " where t.book_id = ? and k.type is null and (t.pos like 'N%' or t.pos like 'V%')  " +
                 " order by pos;");
  	       st.setInt(1, p_book);
   	    	         
   	   rs = st.executeQuery();
   	//   st.close();
   	  
       }
       catch(SQLException e)
       {
           e.printStackTrace();
       }
      return (rs);
   } 

  
  
  public static ResultSet SelectTokenByClauseIDandPosition (Connection conn, Integer p_taxon_description_id, 
		  Integer p_line_number, Integer p_init, Integer p_last)		  
  /* - Description: select all tokens where pos like p_pos and sequence<=p_last and sequence >=p_init
    *    
    * - Revision History:
    *     18/01/2015 - Maria Aux. Mora
    *     
    * - Arguments (input / output):
    *    conn : database opened connection.
    *    p_taxon_description_id: 
    *    p_line_number: a clause identified by taxon description_id + line number.
    *    p_init - p_last : Word range.

    * - Return Values:
    *     A ResultSet with token's records that fulfill the condition. 
	*/
 
 {   
	  ResultSet rs = null;
      try
      {   
   	   PreparedStatement st ;
 
 		   st = conn.prepareStatement("SELECT * FROM text.token WHERE taxon_description_id = ? and line_number = ? and sequence >=? and sequence <=?;");
  	       st.setInt(1, p_taxon_description_id);
  	       st.setInt(2, p_line_number);
   	       st.setInt(3, p_init);	
   	       st.setInt(4, p_last);
  	       
   	       System.out.println("Estoy dentro de llamado base de datos: "+ p_taxon_description_id + " linea " +
   	                              p_line_number + " inicio "+ p_init + " Final " +p_last );
   	       
   	       rs = st.executeQuery();
   	//   st.close();
   	  
       }
       catch(SQLException e)
       {
           e.printStackTrace();
       }
      return (rs);
   } 

  
public static ResultSet SelectTokenLemmaByPOS (Connection conn, String p_pos, Integer p_book_id) 
  
  /* - Description: select all tokens where pos like p_pos 
    *    
    * - Revision History:
    *     18/01/2015 - Maria Aux. Mora
    *     
    * - Arguments (input / output):
    *    conn : database opened connection.
    *    p_pos: substring with the clause.pos that will be searched.
    *    p_book_id :  Book identifier.

    * - Return Values:
    *     A ResultSet with token's records that fulfill the condition. 
	*/
 
 {   
	  ResultSet rs = null;
      try
      {   
   	   PreparedStatement st ;
 
 		   st = conn.prepareStatement("SELECT distinct (lemma) FROM text.token WHERE  pos like ? and book_id = ? order by lemma;");
  	       st.setString(1, p_pos);
  	       st.setInt(2, p_book_id);
   	    	         
   	   rs = st.executeQuery();
   	//   st.close();
   	  
       }
       catch(SQLException e)
       {
           e.printStackTrace();
       }
      return (rs);
   } 

public static void updateTokenEnglishLemmaFromDictionary(Connection conn, Integer p_book_id, Integer p_revision_level)
{/* - Description: Update TOKEN.English_lemma the local Dictionary using word approved by a user. 
 	*                
	* - Revision History:
	*    05/10/2015 - Maria Aux. Mora
	*    23/11/2015 - Maria Mora  Revision level is received as a parameter to apply different word categories. 
	*     
	* - Arguments (input / output):
	*    conn               : database opened connection.
	*    p_book_id          : Book identifier. 
	*    p_revision_level   : level of revision of the dictionary contents.  A word with revision_level = 0 => the user has not approved it.  
	*    
	* - Return Values:
	*      TOKEN.english_Lemma updated for records whose book_id = p_book_id.  
	*/
   try
   {   
	
       /* Statement prepare*/
	   String addRecord = "UPDATE text.token t SET english_lemma = "+ 
			              " (SELECT max( english_lemma) FROM text.dictionary d WHERE t.lemma = d.lemma and revision_level = ?) " +
			              " WHERE t.book_id= ? and t.english_lemma is null; ";
	   
       PreparedStatement query = conn.prepareStatement(addRecord);
      
       query.setInt(1, p_revision_level);
       query.setInt(2, p_book_id);

       query.executeUpdate();
       
       close(query);
    }
    catch(SQLException e)
    {
        e.printStackTrace();
    }
} 

public static void updateTokenEnglishLemmaFromKnowledge(Connection conn, Integer p_book_id)
{/* - Description: Update TOKEN.English_lemma using the  knowledge. 
 	*                
	* - Revision History:
	*    23/11/2015 - Maria Aux. Mora
	*     
	* - Arguments (input / output):
	*    conn               : database opened connection.
	*    p_book_id          : Book identifier. 
	*    
	* - Return Values:
	*      TOKEN.english_Lemma updated for records whose book_id = p_book_id.  
	*/
   try
   {   
	
       /* Statement prepare*/
	   String addRecord = "UPDATE text.token t SET english_lemma = "+ 
			              " (SELECT max( english_lemma) FROM text.knowledge d WHERE t.lemma = d.lemma) " +
			              " WHERE t.book_id= ? and t.english_lemma is null; ";
	   
       PreparedStatement query = conn.prepareStatement(addRecord);
      
       query.setInt(1, p_book_id);
       
       query.executeUpdate();
       
       close(query);
    }
    catch(SQLException e)
    {
        e.printStackTrace();
    }
} 


public static Boolean tokenIsAfterConjunction(Connection conn, TokenParser currentToken)
{  /* - Description: Verify if current token is after a preposition using TEXT.TOKEN. 
 	*                
	* - Revision History:
	*    8/10/2015 - Maria Aux. Mora
	*     
	* - Arguments (input / output):
	*    conn               : database opened connection.
	*    currentToken       : token being processed. 
	*    
	* - Return Values:  
	*/
  
  	ResultSet rs = null;
    PreparedStatement st ;
    Boolean TokenIsEqual = false;
    Boolean conjunctionFound = false;
    String  vPos;            // Token.pos 
    Integer colons = 0 ;

   try
   { 
              
	 
	   if (currentToken != null && currentToken.getWordForm()!=null) { 
  	       st = conn.prepareStatement("SELECT * FROM text.token WHERE taxon_description_id = ? and line_number = ?  "
  	     	                	+ " ORDER BY id;");
  	       st.setInt(1, currentToken.getTaxonDescriptionId());
  	       st.setInt(2, currentToken.getLineNumber());
  	       rs = st.executeQuery();    	  
	    

          while (rs.next())
 	        {  /* The process must fetch p_numClauses records to verify: */ 
   		     vPos = rs.getString(6);            // Token.pos 
   		     System.out.print( rs.getString(4));
   		     
   		     if (rs.getString(4).trim().equals(",")){
                 colons ++;
   		     }
 	         
   		     if (colons == currentToken.getSequence() - 1) {
   	     	     if (conjunctionFound && rs.getString(4).trim().equals(currentToken.getWordForm())){
   		        	TokenIsEqual = true;
   		         }
   		     
   		         if ( vPos.substring(0, 1).equals("C")){
   		    	    conjunctionFound = true;  
   		        }
   		     }    

            }
	   }
   }   
    catch(SQLException e)
    {
        e.printStackTrace();
   }
   
   return (TokenIsEqual);
   
} 

 
/* CHUNK --------------------------------------------------------------------------------------------------------- */	
  
  public static void createChunkRecords(Connection conn,Integer p_book_id,  Integer vTaxonDescriptionId, Integer vLineNumber,
		  String[] p_chunkArray)
    {  
	  Integer i; 
	  
       try
       {  
            
           /* Prepare statement*/
    	   String addRecord = "INSERT INTO text.chunk (book_id, taxon_description_id, line_number, sequence, contents) VALUES ( ?,? , ? , ?, ? );";
           PreparedStatement query = conn.prepareStatement(addRecord);
           i = 1 ;
           for (String vChunk : p_chunkArray) {
              query.setInt(1,p_book_id);
              query.setInt(2,vTaxonDescriptionId);
              query.setInt(3, vLineNumber);
              query.setInt(4 , i);
              query.setString(5, vChunk.trim());
  
              query.executeUpdate();
              i++;
           }
           close(query);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    } 
  
  public static void updateChunkPos(Connection conn, Integer p_book_id)
  {   /* - Description: Update chunk.pos using token.pos[1].  The procedure concatenate token.pos[1] until pos[1] = "F" and token ="," or ":" or ";" or "." 
	    * 
	    * - Revision History:
	    *     23/07/2015 - Maria Aux. Mora
	    *     
	    * - Arguments (input / output):
	    *    conn : database opened connection.
	    *    p_book_id :  Book identifier.

	    * - Return Values:
	    *     Chunk.pos updates for the whole table. 
		*/	  
     try
     {   // Select token in clauses with p_numClauses 
  	     ResultSet rs = null;
         PreparedStatement st ;
         String chunkPOS;         // Concatenating of individual Token.pos[1]
     //    int i;
	     int vTaxonDescriptionId ; //Token.taxon_description_id
	     int vLineNumber;         //Token.line_number
	     String  vPos;            // Token.pos 
	     String vToken;           // Token.token
         int chunkCount;          // Chunk sequence   
         
    //     BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

         
	     // (2,1).pos = NASRAFZNSNF
	      if (p_book_id != null) { 
    	     st = conn.prepareStatement("SELECT * FROM text.token WHERE book_id = ?  ORDER BY taxon_description_id, " +
	                                    " line_number, id;");
    	     st.setInt(1, p_book_id);
    	     rs = st.executeQuery();    	  
	      } else {
     	     st = conn.prepareStatement("SELECT * FROM text.token ORDER BY taxon_description_id, line_number, id;");
   	         rs = st.executeQuery();
	      }
	  
	  // Init local variables.    
   	  chunkPOS = "";
   	  chunkCount = 1;
      int vTaxonDescriptionIdAnt = 0; //Token.taxon_description_id
	  int vLineNumberAnt = 0;         //Token.line_number

   	  // Create the chunk.POS concatenating individual token POS[1]
   	  while (rs.next())
   	    {  if (vTaxonDescriptionIdAnt==0) {
    	       vTaxonDescriptionIdAnt = rs.getInt(2); //Token.taxon_description_id
    		   vLineNumberAnt = rs.getInt(3);         //Token.line_number   
    		   chunkCount = 1;
   		   }
   		   vTaxonDescriptionId = rs.getInt(2); //Token.taxon_description_id
   	       vLineNumber = rs.getInt(3);         //Token.line_number
   	       vPos = rs.getString(6);            // Token.pos 
   	       vToken = rs.getString(4);          // Token.token
   	       
   	       if ((vTaxonDescriptionIdAnt == vTaxonDescriptionId) && 
   	          (vLineNumberAnt == vLineNumber) && (!vToken.trim().equals(".")) && 
   	          (!vToken.trim().equals(":")) && (!vToken.trim().equals(";")) && (!vToken.trim().equals(",") ) ) {
   	             chunkPOS = chunkPOS +  vPos.substring(0, 1);
   			     System.out.println (" chunkPOS + 1 :" + chunkPOS); 
   	       } else { 
   	    	   // Update text.chunk
   	    	   if ((vToken.trim().equals(".")) || 
   	   	          (vToken.trim().equals(":")) || (vToken.trim().equals(";")) || (vToken.trim().equals(",") ) ) {
   	   	             chunkPOS = chunkPOS +  vPos.substring(0, 1);
   	   			     System.out.println (" chunkPOS + 1 :" + chunkPOS ); 
   	   	       }
   	    	  // Update text.chunk.POS  
   	          /* Statement prepare*/
   	    	  if (chunkPOS != null && !chunkPOS.isEmpty()) { 
   	    	      String addRecord = "UPDATE text.chunk set POS = ? where taxon_description_id = ? and line_number =? and sequence = ?;";  	    	  
   	              PreparedStatement query = conn.prepareStatement(addRecord);
   	              query.setString(1, chunkPOS.trim());
   	              query.setInt(2,vTaxonDescriptionIdAnt);
   	              query.setInt(3,vLineNumberAnt);
   	              query.setInt(4,chunkCount);
   	          
   	              query.executeUpdate(); 
   
  	        	  System.out.println("Chunk.POS "+  chunkPOS + " Llave "+ vTaxonDescriptionIdAnt + " " + vLineNumberAnt + " " + chunkCount );
   //	          String s = br.readLine();

   	    	  }
   	          // Clean variables
   	     	  chunkPOS = "";
   	     	  chunkCount ++;	  
   	     	  
   	     	  
   	     	  if (!((vTaxonDescriptionIdAnt == vTaxonDescriptionId) && (vLineNumberAnt == vLineNumber))) {
   		          chunkPOS = vPos.substring(0, 1);
   		          vTaxonDescriptionIdAnt = 0;
   	       }
   	       
   	     }
   	    }   // While 
   	    // Update last record
   	     if (vTaxonDescriptionIdAnt != 0 && chunkPOS != null && !chunkPOS.isEmpty()) {
	    	   String addRecord = "UPDATE text.chunk set POS = ? where taxon_description_id = ? and line_number =? and sequence = ?;";  	    	  
	           PreparedStatement query = conn.prepareStatement(addRecord);
	           query.setString(1, chunkPOS);
	           query.setInt(2,vTaxonDescriptionIdAnt);
	           query.setInt(3,vLineNumberAnt);
	           query.setInt(4,chunkCount);
           
	           query.executeUpdate(); 
          }

      }
     catch(Exception es){
	        es.printStackTrace();
     }
  } 
  
  public static void updateChunkTreePos(Connection conn, Integer p_book_id)
  {   /* - Description: Update chunk.tree_pos using token_tree.pos[1]. 
        *  The procedure concatenates token_tree.pos[1] until pos[1] = "F" and token ="," or ":" or ";" or "." 
	    * 
	    * - Revision History:
	    *     28/07/2015 - Maria Aux. Mora
	    *     
	    * - Arguments (input / output):
	    *    conn : database opened connection.
	    *    p_book_id :  Book identifier.

	    * - Return Values:
	    *     Chunk.tree_pos updates for the whole table. 
		*/	  
     try
     {   // Select token in clauses with p_numClauses 
  	     ResultSet rs = null;
         PreparedStatement st ;
         String chunkPOS;         // Concatenating of individual Token_tree.pos[1]
        // int i;
	     int vTaxonDescriptionId ; //Token.taxon_description_id
	     int vLineNumber;         //Token.line_number
	     String vToken; 		  //Token.token
	     String  vPos;            // Token.pos 
	     String vType ;            // knowledde.type
	     String vCategory;         // oto.ctegory
         int chunkCount;          // Chunk sequence   
         
      //   BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

         
	     // Ejemplo (2,1).pos = NASRAFZNSNF
	      if (p_book_id != null) { 
    	     st = conn.prepareStatement("select t.id, t.taxon_description_id, t.line_number, t.sequence, t.token, t.lemma, t.pos, t.tag1, t.tag2, k.type, k.english_lemma, o.category " +
                                        "  from text.token_tree t left join text.knowledge k  on trim(k.phrase) = trim(t.token) "+
                                        "  left join text.oto o on trim(k.english_lemma) = trim(o.term) "+
                                        " where t.book_id = ? and t.token <> '[' and t.token <> ']'  " +
                                        " order by 2,3, 1;");
    	     st.setInt(1, p_book_id);
    	     rs = st.executeQuery();    	  
	      } else {
     	     st = conn.prepareStatement("select t.id, t.taxon_description_id, t.line_number, t.sequence, t.token, t.lemma, t.pos, t.tag1, t.tag2, k.type, k.english_lemma, o.category " +
                                        "  from text.token_tree t left join text.knowledge k  on trim(k.phrase) = trim(t.token) "+
                                        "  left join text.oto o on trim(k.english_lemma) = trim(o.term) "+
                                        " where t.token <> '[' and t.token <> ']' " +
                                        " order by 2,3, 1;");
   	         rs = st.executeQuery();
	      }
	  
	  // Set local variables.    
   	  chunkPOS = "";
   	  chunkCount = 1;
      int vTaxonDescriptionIdAnt = 0; //Token.taxon_description_id
	  int vLineNumberAnt = 0;         //Token.line_number
	  String vTokenAnt = "";               //Token.token

   	  // Create the chunk.tree_POS concatenating individual token_tree POS[1]
   	  while (rs.next())
   	    {  if (vTaxonDescriptionIdAnt==0) {
    	       vTaxonDescriptionIdAnt = rs.getInt(2); //Token.taxon_description_id
    		   vLineNumberAnt = rs.getInt(3);         //Token.line_number 
    		//   vTokenAnt = rs.getString(5);           //Token.token
    		   chunkCount = 1;
   		   }
   		   vTaxonDescriptionId = rs.getInt(2); //Token.taxon_description_id
   	       vLineNumber = rs.getInt(3);         //Token.line_number
   	       vToken = rs.getString(5);           //Token.token    
   	       vPos = rs.getString(7);            // Token.pos 
   	       vType =rs.getString(10);           //knowledge.type
   	       vCategory =rs.getString(12);       //oto.category
   	       
   	       if ((vTaxonDescriptionIdAnt == vTaxonDescriptionId) && 
   	          (vLineNumberAnt == vLineNumber) && (!vToken.trim().equals(".")) && 
   	          (!vToken.trim().equals(":")) && (!vToken.trim().equals(";")) && (!vToken.trim().equals(",") )
   	            && (!vToken.trim().equals(vTokenAnt))) {
   	    	   
   	    	     if (vCategory != null && vCategory.trim().equals("structure")) {
   	   	             chunkPOS = chunkPOS +  "E";
   	    	     } 
   	    	     else {
   	    	    	 if  (vCategory != null && vCategory.trim().equals("growth_form"))
   	    	    	 {chunkPOS = chunkPOS +  "H";}
   	    	         else {
   	    	      //  	 if  (vCategory != null && vCategory.trim().equals("quantity"))
   	   	    	  //  	 {chunkPOS = chunkPOS +  "R";}
   	    	      //  	 else { 
   	    	    	        if  (!(vType == null)){
  	   	   	                   chunkPOS = chunkPOS + vType.trim() ;}
   	    	                else {
   	    	    	           chunkPOS = chunkPOS +  vPos.substring(0, 1);
   	    	                }
   	    	           // } 
   	    	    	 }
   	    	     }	 
   	    	    	            
   			     System.out.println (" chunkPOS + 1 :" + chunkPOS + " " + " Los tokens "+ vToken + " " + vTokenAnt); 
   	       } else { 
   	    	   
   	    	   if ((!vToken.trim().equals(vTokenAnt))) {
   	    	      // Update text.chunk
   	    	      if ((vToken.trim().equals(".")) || 
   	   	             (vToken.trim().equals(":")) || (vToken.trim().equals(";")) || (vToken.trim().equals(",") ) ) {
   	   	                chunkPOS = chunkPOS +  vPos.substring(0, 1);
   	   			        System.out.println (" chunkPOS + 1 :" + chunkPOS + " " ); 
   	   	          }
   	    	      // Update text.chunk.POS  
   	              /* Statement prepare*/
   	        	  if (chunkPOS != null && !chunkPOS.isEmpty() && !chunkPOS.trim().equals("F") ) { 
   	    	         String addRecord = "UPDATE text.chunk set tree_pos = ? where taxon_description_id = ? and line_number =? and sequence = ?;";  	    	  
   	                 PreparedStatement query = conn.prepareStatement(addRecord);
   	                 query.setString(1, chunkPOS.trim());
   	                 query.setInt(2,vTaxonDescriptionIdAnt);
   	                 query.setInt(3,vLineNumberAnt);
   	                 query.setInt(4,chunkCount);
   	          
   	                 query.executeUpdate(); 
   
  	        	     System.out.println("Chunk.POS "+  chunkPOS + " Llave "+ vTaxonDescriptionIdAnt + " " + vLineNumberAnt + " " + chunkCount );
   //	             String s = br.readLine();

   	    	      }
   	             // Clean variables
   	     	     chunkPOS = "";
   	     	     chunkCount ++; 
   	     	     	     	  
   	     	     if (!((vTaxonDescriptionIdAnt == vTaxonDescriptionId) && (vLineNumberAnt == vLineNumber))) {
   	     	    	  vTaxonDescriptionIdAnt= 0;  
   	   	    	      if (vCategory != null && vCategory.trim().equals("structure")) {
   	   	   	             chunkPOS = chunkPOS +  "E";
   	   	    	      } 
   	   	    	      else {
   	   	    	    	 if  (vCategory != null && vCategory.trim().equals("growth_form"))
   	   	    	    	 {chunkPOS = chunkPOS +  "H";}
   	   	    	         else {
   	   	    	    	    if  (vType != null && !vType.isEmpty()){
   	  	   	   	                chunkPOS = chunkPOS + vType.trim() ;}
   	   	    	            else {
   	   	    	    	       chunkPOS = chunkPOS +  vPos.substring(0, 1);
   	   	    	            }
   	   	    	    	 }
   	   	    	     }
   	             }
   	       
   	           }
   	      }   
	      vTokenAnt = vToken.trim();
  
   	    }   // While 
   	    // Update last record
   	     if (vTaxonDescriptionIdAnt != 0 && chunkPOS != null && !chunkPOS.isEmpty()) {
	    	   String addRecord = "UPDATE text.chunk set tree_pos = ? where taxon_description_id = ? and line_number =? and sequence = ?;";  	    	  
	           PreparedStatement query = conn.prepareStatement(addRecord);
	           query.setString(1, chunkPOS);
	           query.setInt(2,vTaxonDescriptionIdAnt);
	           query.setInt(3,vLineNumberAnt);
	           query.setInt(4,chunkCount);
           
	           query.executeUpdate(); 
          }

      }
     catch(Exception es){
	        es.printStackTrace();
     }
  } 
  
  
  
  
  public static void updateChunkPosFromClause(Connection conn, Integer p_book_id)
    {
	  /* - Description: Update chunk.pos using clause.pos.  The procedure segment clause.pos using each char "F"=",". 
	    *  Not in use - SE PUEDE BORRAR  
	    * - Revision History:
	    *     22/06/2015 - Maria Aux. Mora
	    *     
	    * - Arguments (input / output):
	    *    conn : database opened connection.
	    *    p_book_id :  Book identifier.

	    * - Return Values:
	    *     Chunk.pos updates for the whole table. 
		*/	  
	  
       try
       {   // Select all records in clause 
    	   ResultSet rs = null;
           PreparedStatement st ;
           String chunkPos;         // chunk.pos 
           int i;
 	       int vTaxonDescriptionId ; //Clause.taxon_description_id
 	       int vLineNumber;         // Clause.line_number
 	       String  vPos;            // clause.pos 
 	       int chunkSequence;

 	      if (p_book_id != null) { 
      	     st = conn.prepareStatement("SELECT * FROM text.clause WHERE book_id = ?  ORDER BY taxon_description_id, " +
 	                                    " line_number ;");
      	     st.setInt(1, p_book_id);
      	     rs = st.executeQuery();    	  
 	      } else {
     	     st = conn.prepareStatement("SELECT * FROM text.clause ORDER BY taxon_description_id, line_number;");
     	     rs = st.executeQuery();
 	      }
 	      
     	  //NASRAFZNSNF
 
     	  // Create the chunk.POS extracting it form clause.pos
     	  while (rs.next())
     	    {  /* Fetch next clause record */ 
       		  
     		   vTaxonDescriptionId = rs.getInt(2); //Current clause.taxon_description_id
     	       vLineNumber = rs.getInt(8);         //clause.line_number
     	       vPos = rs.getString(10);            // clause.pos 
     	       chunkSequence = 1;
     	       i=0;
           	  chunkPos = "";

     	     
     	       while ( i < (vPos.length()))
     	       {  
     	    	   
     	    	   chunkPos = vPos.substring(i, vPos.indexOf("F", i)+1);
     	    	   i = vPos.indexOf("F", i)+1;
     	    	   
     	    	  System.out.println (" ChunkPos :" + chunkPos + " " + i + " largo "+ vPos.length() +" Record "+ vTaxonDescriptionId+ " " + vLineNumber + " " + chunkSequence ); 
     	    	  
     	    	  // Update chunk.pos
     	    	  String addRecord = "UPDATE text.chunk set POS = ? where taxon_description_id = ? and line_number =? and sequence = ?;";  	    	  
    	          PreparedStatement query = conn.prepareStatement(addRecord);
    	          query.setString(1, chunkPos);
    	          query.setInt(2,vTaxonDescriptionId);
    	          query.setInt(3,vLineNumber);
    	          query.setInt(4, chunkSequence);

    	          query.executeUpdate();   
    	          chunkSequence ++;
     	    	  
     	    	   
     	       } // end while vPos.length
     	    	   
         }}
        catch(SQLException e)
        {
            e.printStackTrace();
       }
    } 
    

public static ResultSet selectChunk(Connection conn, Integer p_book_id, 
		  Integer p_taxon_description_id , Integer p_line_number, 
		  Integer p_sequence)
    { 
	   ResultSet rs = null;
       try
       {   
    	   /* Where condition construction*/
    	   String condition = null;   
    	   PreparedStatement st ;
    	   
    	   if (p_book_id != null) 
               condition = "book_id = ?";
          
    	   if (p_taxon_description_id != null) {
    		   if (condition != null)
                  condition = condition + "and taxon_description_id = ?";
    		   else               
    			  condition=  "taxon_description_id = ?";
    	   } 
    	   if (p_line_number != null) {
    		   if (condition != null)
                  condition = condition + "and line_number = ?";
    		   else               
    			  condition = "line_number = ?";
    	   } 
    	   if (p_sequence != null) {
    		   if (condition != null)
                  condition = condition + "and sequence = ?";
    		   else               
    			  condition = "sequence = ?";
    	   } 
    	   if (condition != null)
    		   st = conn.prepareStatement("SELECT * FROM TEXT.CHUNK WHERE " + condition + " " + "order by taxon_description_id, line_number, sequence");
    	   else 
    		   st = conn.prepareStatement("SELECT * FROM TEXT.CHUNK order by taxon_description_id, line_number, sequence");
    	   
    	   int i = 1;
    	   
    	   if (p_book_id != null) {
               st.setInt(i,p_book_id);
               i++;
           } 
    	   if (p_taxon_description_id != null) {
               st.setInt(i,p_taxon_description_id);
               i++;
    	   } 
    	   if (p_line_number != null) {
               st.setInt(i,p_line_number);
               i++;
    	   } 
    	   if (p_sequence != null) {
               st.setInt(i,p_sequence);
               i++;
    	   } 
    	   
    	   rs = st.executeQuery();
    	 //  st.close();
    	  
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
       return (rs);
    } 
  
public static ResultSet selectChunkRange(Connection conn, Integer p_book_id, 
		  Integer p_taxonDescriptionIdInitial , Integer p_TaxonDescriptionIdFinal)
  { 
	   ResultSet rs = null;
     try
     {   
  	   /* Where condition construction*/
  	   String condition = null;   
  	   PreparedStatement st ;
  	   
  	   if (p_book_id != null) 
             condition = "book_id = ?";
        
  	   if (p_taxonDescriptionIdInitial != null) {
  		   if (condition != null)
                condition = condition + "and taxon_description_id >= ?";
  		   else               
  			  condition=  "taxon_description_id >= ?";
  	   } 
  	   if (p_TaxonDescriptionIdFinal != null) {
  		   if (condition != null)
                condition = condition + "and taxon_description_id <= ?";
  		   else               
  			  condition = "taxon_Description_Id <= ?";
  	   } 
   
  	   if (condition != null)
  		   st = conn.prepareStatement("SELECT * FROM TEXT.CHUNK WHERE " + condition + " " + "order by taxon_description_id, line_number, sequence");
  	   else 
  		   st = conn.prepareStatement("SELECT * FROM TEXT.CHUNK order by taxon_description_id, line_number, sequence");
  	   
  	   int i = 1;
  	   
  	   if (p_book_id != null) {
             st.setInt(i,p_book_id);
             i++;
         } 
  	   if (p_taxonDescriptionIdInitial != null) {
             st.setInt(i,p_taxonDescriptionIdInitial);
             i++;
  	   } 
  	   if (p_TaxonDescriptionIdFinal != null) {
             st.setInt(i,p_TaxonDescriptionIdFinal);
             i++;
  	   } 

  	   
  	   rs = st.executeQuery();
  	  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
     return (rs);
  } 


  
public static ResultSet SelectChunkByTreePos (Connection conn, String p_pos, Integer p_book_id) 

/* - Description: select all chunks where tree_pos is like p_pos 
  *    
  * - Revision History:
  *     21/06/2015 - Maria Aux. Mora
  *     
  * - Arguments (input / output):
  *    conn : database opened connection.
  *    p_pos: substring with the clause.pos that will be searched.
  *    p_book_id :  Book identifier.

  * - Return Values:
  *     A ResultSet with token's records that fulfill the condition. 
	*/

{   
	  ResultSet rs = null;
    try
    {   
 	   PreparedStatement st ;

	   st = conn.prepareStatement("SELECT * FROM TEXT.CHUNK WHERE book_id = ? and tree_pos = ? order by taxon_description_id, line_number, sequence;");
	     
       st.setInt(1, p_book_id);
       st.setString(2, p_pos);
 	    	         
       rs = st.executeQuery();
 	  
     }
     catch(SQLException e)
     {
         e.printStackTrace();
     }
    return (rs);
 } 

public static String SelectChunkContents (Connection conn, Integer p_taxonDescriptionId, Integer p_lineNumber, Integer p_sequence) 

/* - Description: select a chunk contents by taxon_description_id, line_number, and sequence 
  *    
  * - Revision History:
  *     29/07/2015 - Maria Aux. Mora
  *     
  * - Arguments (input / output):
  *    conn : database opened connection.
  *    p_pos: substring with the clause.pos that will be searched.
  *    p_book_id :  Book identifier.

  * - Return Values:
  *     A ResultSet with token's records that fulfill the condition. 
	*/

{   
	  ResultSet rs = null;
	  String vContents = null;
    try
    {   
 	   PreparedStatement st ;

	   st = conn.prepareStatement("SELECT contents FROM TEXT.CHUNK WHERE taxon_description_id = ? and " +
	                              " line_number = ? and  sequence = ?;");
	     
       st.setInt(1, p_taxonDescriptionId);
       st.setInt(2, p_lineNumber);
       st.setInt(3, p_sequence);

 	    	         
       rs = st.executeQuery();
       
  	   if (rs.next()){
  		   vContents = rs.getString(1);
  	   }

 	  
     }
     catch(SQLException e)
     {
         e.printStackTrace();
     }
    return (vContents);
 } 



public static TokenParser selectTokenParserRoot (Connection conn, TokenParser currentToken, Integer p_book_id) 

/* - Description: Return the first token parser of a tree (the root). 
  *    
  * - Revision History:
  *     29/10/2015 - Maria Aux. Mora
  *     
  * - Arguments (input / output):
  *    conn : database opened connection.
  *    currentToken: token being processsed.

  * - Return Values:
  *    
	*/

{   
	  ResultSet rs = null;
	  TokenParser tokenResult = null;
	  
    try
    {   
	   	PreparedStatement st ;
		 
	    st = conn.prepareStatement("select t.id, t.taxon_description_id, t.line_number, t.sequence, t.token, t.lemma, t.pos, t.tag1, t.tag2, k.type, " +
	    		                   " k.english_lemma, o.category, o.term_id " +
                                   " from text.token_tree t left join text.knowledge k  on trim(k.phrase) = trim(t.token) " +
                                   " left join text.oto o on trim(k.english_lemma) = trim(o.term) " +
                                   " where  t.taxon_description_id = ? and  t.line_number = ? and t.sequence = ? and t.tag2= 'top' " +
                                   " order by t.taxon_description_id, t.line_number,t.id ;");
	    st.setInt(1, currentToken.getTaxonDescriptionId());
	  	st.setInt(2, currentToken.getLineNumber());
	  	st.setInt(3, currentToken.getSequence());

	   	    	         
	   	rs = st.executeQuery();
	   	
  	   if (rs.next()){
  		   
   	      Integer vTokenTreeId = rs.getInt(1);
          Integer vTaxonDescriptionId = rs.getInt(2); //CurrentToken.taxon_description_id
     	  Integer vLineNumber = rs.getInt(3);         //currentToken.line_number
     	  Integer vSequence = rs.getInt(4);
     	  String  vToken = rs.getString(5);
     	  String vLemma = rs.getString(6);
     	  String vPos = rs.getString(7);
     	  String vTag1 = rs.getString(8);
     	  String vTag2 = rs.getString(9);
     	  String vType = rs.getString(10);
     	  String vCategory = rs.getString(12);
     	  String vOntoId = rs.getString(13);
     	
     	  tokenResult = new TokenParser(null, vTaxonDescriptionId, vLineNumber, vToken, vLemma,  vPos, 
     	        		    vTag1, vTag2, vSequence, p_book_id, vType, vCategory, vOntoId, vTokenTreeId);
  	   
       }
     }  
     catch(SQLException e)
     {
         e.printStackTrace();
     }
    return (tokenResult);
 } 


/* TOKEN_TREE ---------------------------------------------------------------------------------------------------- */	

  public static void createTokenTreeRecords(Connection conn,  
		  ArrayList<TokenParser> p_tokenList)
    {
       try
       {   
    	   
           /* Statement prepare*/
    	   String addRecord = "INSERT INTO text.TOKEN_TREE (taxon_description_id, line_number, token, lemma,pos, tag1, tag2, sequence, book_id) VALUES (?, ?, ? , ? , ? , ? , ?, ?,?);";
           PreparedStatement query = conn.prepareStatement(addRecord);
          
           for (TokenParser wordToken : p_tokenList) {

              query.setInt(1, wordToken.taxonDescriptionId);
              query.setInt(2, wordToken.lineNumber);
              query.setString(3 , wordToken.wordForm);
              query.setString(4, wordToken.wordLemma);
              query.setString(5, wordToken.wordTag);
              query.setString(6 , wordToken.tag1);
              query.setString(7 , wordToken.tag2);
              if (wordToken.sequence != null)
                 query.setInt(8, wordToken.sequence);
              else
                  query.setInt(8, 0);
 
              query.setInt(9, wordToken.bookId);
                            
              query.executeUpdate();
           }
           close(query);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    } 
    
  
public static  TokenParser selectCurrentTokenTreeRecord (Connection conn, Integer p_taxon_description_id, Integer p_line_number, Integer p_sequence, String p_wordForm, 
		String p_pos, String p_tag1, String p_tag2, Integer p_book_id, TokenParser previousToken) 
		  
    /* - Description: return a token_tree record with additional information form text.knowledge and the ontology. 
     *    
     * - Revision History:
     *     18/06/2015 - Maria Aux. Mora
	 *     
     * - Arguments (input / output):
	 *    conn : database opened connection.
	 *    
	 *    p_book_id :  Book identifier.

	 * - Return Values:
	 *     A ResultSet with token's records that fulfill the condition. 
	 */
		 
	{   
	    ResultSet rs = null;
	    Integer i;
	    Integer vTaxonDescriptionId, vLineNumber, vSequence, vTokenTreeId;
	    String vToken, vLemma, vPos, vTag1, vTag2, vType, vEnglishLemma, vCategory, vOntoId;
	    TokenParser tokenResult = null;
	    Boolean foundParent = false;
	    Boolean ready = false;
	    
	    String parentTokenWord = "";
		try
		   {   
			if (previousToken != null && previousToken.getWordForm() != null && !previousToken.getWordForm().trim().equals("") ){
			   parentTokenWord =  	previousToken.getWordForm().trim();
			} else {
				foundParent = true;
			}
			
		   	PreparedStatement st ;
		 
		    st = conn.prepareStatement("select t.id, t.taxon_description_id, t.line_number, t.sequence, t.token, t.lemma, t.pos, t.tag1, t.tag2, k.type, " +
		    		                   " k.english_lemma, o.category, o.term_id " +
                                       " from text.token_tree t left join text.knowledge k  on trim(k.phrase) = trim(t.token) " +
                                       " left join text.oto o on trim(k.english_lemma) = trim(o.term) " +
                                       " where t.book_id = ? and t.taxon_description_id = ? and  t.line_number = ? and t.sequence = ? " +
                                       " order by t.taxon_description_id, t.line_number,t.id ;");
		  	st.setInt(1, p_book_id);
		    st.setInt(2, p_taxon_description_id);
		  	st.setInt(3, p_line_number);
		  	st.setInt(4, p_sequence);

		   	    	         
		   	rs = st.executeQuery();
		    i = 1;   	/* If there are more than one record => One record match with more than one category in OTO. 
		                 * and the tokenResult includes all categories separated by colon. 
		                 */
		   	
		    
		   	  while (rs.next())
	     	    {  /* Fetch next clause record */ 
	       		   
		   	       vTokenTreeId = rs.getInt(1);
		           vTaxonDescriptionId = rs.getInt(2); //Current clause.taxon_description_id
		     	   vLineNumber = rs.getInt(3);         //clause.line_number
		     	   vSequence = rs.getInt(4);
		     	   vToken = rs.getString(5);
		     	   vLemma = rs.getString(6);
		     	   vPos = rs.getString(7);
		     	   vTag1 = rs.getString(8);
		     	   vTag2 = rs.getString(9);
		     	   vType = rs.getString(10);
		     	   vEnglishLemma = rs.getString(11);
		     	   vCategory = rs.getString(12);
		     	   vOntoId = rs.getString(13);
		     	
		     	   
		   		  
		   		   if (foundParent) {
	   		
		     	       
		     	      if (!ready && (vToken.trim().equals(p_wordForm) && vPos.trim().equals(p_pos) && vTag1.trim().equals(p_tag1)
		     	    		  && vTag2.trim().equals(p_tag2))) {
		     	    	  if (i==1) {
		     	              tokenResult = new TokenParser(null, vTaxonDescriptionId, vLineNumber, vToken, vLemma,  vPos, 
		     	        		    vTag1, vTag2, vSequence, p_book_id, vType, vCategory, vOntoId, vTokenTreeId);
		     	              i++;
		     	           }
		     	           else {
		     	    	      tokenResult.setOntologyCategory(tokenResult.getOntologyCategory() + "," + vCategory); 
		     	           }  
		     	       } else if (i>1)  ready = true;
		   		   } else if (!ready && vToken.trim().equals(parentTokenWord)) {
		   			   foundParent = true;
		   		   }
	     	    }  
			   	
		   	  
		    }
		    catch(SQLException e)
		    {
		        e.printStackTrace();
		    }
		    return (tokenResult);
	} 

public static  TokenParser selectCurrentTokenTreeRecordByTokenString (Connection conn,  TokenParser currentToken, String tokenString) 
		  
    /* - Description: return a token_tree record with additional information form text.knowledge and the ontology.   The search
     *     uses the tokenString.  It is used to process structures.  
     *    
     * - Revision History:
     *     16/12/2015 - Maria Aux. Mora
	 *     
     * - Arguments (input / output):
	 *    conn : database opened connection.
	 *    curentToken: used to extract keys (taxon_description_id, line_number, sequence).  
	 *    
	 * - Return Values:
	 *     A ResultSet with token's records that fulfill the condition. 
	 */
		 
	{   
	    ResultSet rs = null;
	    Integer i;
	    Integer vTaxonDescriptionId, vLineNumber, vSequence, vTokenTreeId;
	    String vToken, vLemma, vPos, vTag1, vTag2, vType, vCategory, vOntoId;
	    TokenParser tokenResult = null;

		try
		   {   
			if (currentToken != null ) {
		   	   PreparedStatement st ;
		 
		       st = conn.prepareStatement("select t.id, t.taxon_description_id, t.line_number, t.sequence, t.token, t.lemma, t.pos, t.tag1, t.tag2, k.type, " +
		    		                   " k.english_lemma, o.category, o.term_id " +
                                       " from text.token_tree t left join text.knowledge k  on trim(k.phrase) = trim(t.token) " +
                                       " left join text.oto o on trim(k.english_lemma) = trim(o.term) " +
                                       " where  t.taxon_description_id = ? and  t.line_number = ? and t.sequence = ? and " +
                                       " t.token = ? " +
                                       " order by t.taxon_description_id, t.line_number,t.id ;");
		       st.setInt(1, currentToken.getTaxonDescriptionId());
		       st.setInt(2, currentToken.getLineNumber());
		       st.setInt(3, currentToken.getSequence());
		       st.setString(4, tokenString);

		   	    	         
		   	rs = st.executeQuery();		   	
		    
		   if (rs.next())
	     	    {  /* Fetch the firts  record */ 
	       		   
		   	       vTokenTreeId = rs.getInt(1);
		           vTaxonDescriptionId = rs.getInt(2); //Current clause.taxon_description_id
		     	   vLineNumber = rs.getInt(3);         //clause.line_number
		     	   vSequence = rs.getInt(4);
		     	   vToken = rs.getString(5);
		     	   vLemma = rs.getString(6);
		     	   vPos = rs.getString(7);
		     	   vTag1 = rs.getString(8);
		     	   vTag2 = rs.getString(9);
		     	   vType = rs.getString(10);
		     	   vCategory = rs.getString(12);
		     	   vOntoId = rs.getString(13);
	
	               tokenResult = new TokenParser(null, vTaxonDescriptionId, vLineNumber, vToken, vLemma,  vPos, 
		     	        		    vTag1, vTag2, vSequence, currentToken.getBookId(), vType, vCategory, vOntoId, vTokenTreeId);
		       }    	
			 } 
		    }
       catch(SQLException e)
	   {
          e.printStackTrace();
	   }
	   return (tokenResult);
	} 


  
public static  TokenParser selectCurrentTokenTreeRecordBACKOLDNOSIRVE (Connection conn, Integer p_taxon_description_id, Integer p_line_number, Integer p_sequence, String p_wordForm, 
		String p_pos, String p_tag1, String p_tag2, Integer p_book_id, TokenParser previousToken) 
		  
    /* - Description: return a token_tree record with additional information form text.knowledge and the ontology. 
     *    
     * - Revision History:
     *     18/06/2015 - Maria Aux. Mora
	 *     
     * - Arguments (input / output):
	 *    conn : database opened connection.
	 *    
	 *    p_book_id :  Book identifier.

	 * - Return Values:
	 *     A ResultSet with token's records that fulfill the condition. 
	 */
		 
	{   
	    ResultSet rs = null;
	    Integer i;
	    Integer vTaxonDescriptionId, vLineNumber, vSequence, vTokenTreeId;
	    String vToken, vLemma, vPos, vTag1, vTag2, vType, vEnglishLemma, vCategory, vOntoId;
	    TokenParser tokenResult = null;
	    
		try
		   {   
		   	PreparedStatement st ;
		 
		    st = conn.prepareStatement("select t.id, t.taxon_description_id, t.line_number, t.sequence, t.token, t.lemma, t.pos, t.tag1, t.tag2, k.type, " +
		    		                   " k.english_lemma, o.category, o.term_id " +
                                       " from text.token_tree t left join text.knowledge k  on trim(k.phrase) = trim(t.token) " +
                                       " left join text.oto o on trim(k.english_lemma) = trim(o.term) " +
                                       " where t.book_id = ? and t.taxon_description_id = ? and  t.line_number = ? and t.sequence = ? and " +
                                       " trim(t.token) = ? and  trim(t.pos) = ?  and trim(t.tag1) = ?  and trim(t.tag2) = ? ;");
		  	st.setInt(1, p_book_id);
		    st.setInt(2, p_taxon_description_id);
		  	st.setInt(3, p_line_number);
		  	st.setInt(4, p_sequence);
		  	st.setString(5, p_wordForm );
		  	st.setString(6, p_pos);
		  	st.setString(7, p_tag1);
		  	st.setString(8, p_tag2);

		   	    	         
		   	rs = st.executeQuery();
		    i = 1;   	/* If there are more than one record => One record match with more than one category in OTO. 
		                 * and the tokenResult includes all categories separated by colon. 
		                 */
		   	
		    
		   	  while (rs.next())
	     	    {  /* Fetch next clause record */ 
	       		  
		   		   vTokenTreeId = rs.getInt(1);
	     		   vTaxonDescriptionId = rs.getInt(2); //Current clause.taxon_description_id
	     	       vLineNumber = rs.getInt(3);         //clause.line_number
	     	       vSequence = rs.getInt(4);
	     	       vToken = rs.getString(5);
	     	       vLemma = rs.getString(6);
	     	       vPos = rs.getString(7);
	     	       vTag1 = rs.getString(8);
	     	       vTag2 = rs.getString(9);
	     	       vType = rs.getString(10);
	     	       vEnglishLemma = rs.getString(11);
	     	       vCategory = rs.getString(12);
	     	       vOntoId = rs.getString(13);
	     	       
	     	      if (i==1) {
	     	         tokenResult = new TokenParser(null, vTaxonDescriptionId, vLineNumber, vToken, vLemma,  vPos, 
	     	        		    vTag1, vTag2, vSequence, p_book_id, vType, vCategory, vOntoId, vTokenTreeId);
	     	         i++;
	     	       }
	     	      else {
	     	    	 tokenResult.setOntologyCategory(tokenResult.getOntologyCategory() + "," + vCategory); 
	     	      }  
	     	    }  
		   	
		   	  
		    }
		    catch(SQLException e)
		    {
		        e.printStackTrace();
		    }
		    return (tokenResult);
	}   
	

public static  String getClosetUnitofMeasure (Connection conn, TokenParser currentToken, Integer p_book_id) 
		  
    /* - Description: return the closer unit of measure of a token.  If tokenTreedId of current token is null return the first record.  
     *    
     * - Revision History:
     *     13/08/2015 - Maria Aux. Mora
	 *     
     * - Arguments (input / output):
	 *    conn :         database opened connection.
	 *    currentToken:  token being processed as reference token.   
	 *    p_book_id :    Book identifier.

	 * - Return Values:
	 *     A String with the unit of measure. 
	 */
		 
	{   
	    ResultSet rs = null;
	    Integer i, vTreeId;
	    String vToken = null;
	    Integer vDistance = 9999;
	    int tempDistance = 0;
	    Integer vcurrentTokenTreeId = currentToken.getTokenTreeId(); 
	    String vUnitOfMeasure = null;
	  
	    
		try
		   {   
		   	PreparedStatement st ;
		 
		    st = conn.prepareStatement("select t.id, t.taxon_description_id, t.line_number, t.sequence, t.token, k.type, " +
		    		                   " o.category, o.term_id " +
                                       " from text.token_tree t left join text.knowledge k  on trim(k.phrase) = trim(t.token) " +
                                       " left join text.oto o on trim(k.english_lemma) = trim(o.term) " +
                                       " where t.book_id = ? and t.taxon_description_id = ? and  t.line_number = ? and t.sequence = ? and k.type = ? " +
                                       " order by id  ;");
		  	st.setInt(1, p_book_id);
		    st.setInt(2, currentToken.getTaxonDescriptionId());
		  	st.setInt(3, currentToken.getLineNumber());
		  	st.setInt(4, currentToken.getSequence());
		  	st.setString(5, "U");
		   	    	         
		   	rs = st.executeQuery();
		    i = 1;   	/* If there are more than one record
		                 */
		    
		   	while (rs.next())
	     	    {  /* Fetch next clause record */ 
	       		  
		   		  vTreeId = rs.getInt(1);       
		   		  vToken = rs.getString(5);
		   		   
		   		  if ((vcurrentTokenTreeId != null) && (vTreeId != null) ){
		   			
		   			  tempDistance =  Math.abs(vcurrentTokenTreeId - vTreeId);
		   		  
		   			 if  (tempDistance < vDistance){
		   				  vDistance = tempDistance ;
		   				  vUnitOfMeasure=vToken;
		   			 }	  
		   		/*     else if (i==1) {
	     	              vUnitOfMeasure=vToken;
	     	              i++;
	     	       } */
	     	    }
	     	 }		  
		   	
		   	  
		    }
		    catch(SQLException e)
		    {
		        e.printStackTrace();
		    }
		    return (vUnitOfMeasure);
	} 

  
  
  /* KNOWLEDGE --------------------------------------------------------------------------------------------------------------- */	
  
  public static void insertKnowledge(Connection conn,  
		  String pPhrase, String pLemma, String pEnglishLemma, String pPos, String pType, String pRole, Integer pRevisionLevel)
    {
       try
       {  ResultSet rs = null;
          PreparedStatement st ;
          boolean continueInserting = false;
          
          if (pType.equals("A") && pPos.substring(0, 1).equals("V")){
        	  /* IF pType is an Adjective and token is a verb then check if token is a participle 
        	   * (verb ending in ado, ido, to, so, cho) -and its plural and female application. 
        	   * */        	  
        	  if (pPhrase.endsWith("ado") || pPhrase.endsWith("ados") || pPhrase.endsWith("ada") || pPhrase.endsWith("adas")||
        	      pPhrase.endsWith("ido") || pPhrase.endsWith("idos") || pPhrase.endsWith("ida") || pPhrase.endsWith("idas")||
        	      pPhrase.endsWith("to") || pPhrase.endsWith("tos") || pPhrase.endsWith("ta") || pPhrase.endsWith("tas")||
        	      pPhrase.endsWith("so") || pPhrase.endsWith("sos") || pPhrase.endsWith("sa") || pPhrase.endsWith("sas")||
        	      pPhrase.endsWith("cho") || pPhrase.endsWith("chos") || pPhrase.endsWith("cha") || pPhrase.endsWith("chas")||
        	      pPhrase.endsWith("ído") || pPhrase.endsWith("ídos") || pPhrase.endsWith("ída") || pPhrase.endsWith("ídas")||
        	      pPhrase.endsWith("ádo") || pPhrase.endsWith("ádos") || pPhrase.endsWith("áda") || pPhrase.endsWith("ádas") 
        			  )  {
        		 continueInserting = true;
        	  }
        	  
          } else continueInserting = true;
          
          if (continueInserting){
              // verify that the token does not exist in the text.knowledge table 
    	     st = conn.prepareStatement("SELECT * FROM TEXT.KNOWLEDGE WHERE phrase = ? ");
    	     st.setString(1,pPhrase);
    	     // st.setString(2,pType); I am assuming that a word only has a type if 
    	     // we talk about Structures, Adjectives and Modifiers.
    	  
    	     rs = st.executeQuery();
    	 
    	     /* If the record does not exist then insert this new knowledge*/
    	     
    	     if (!rs.next()) {   
             
    		  
    	        String addRecord = "INSERT INTO text.KNOWLEDGE (phrase, lemma, type, role, english_lemma, pos, revision_level) "
    	    		            +  " VALUES (  ? , ? , ? , ?, ?, ?, ?);";
                PreparedStatement query = conn.prepareStatement(addRecord);
          
                query.setString(1 ,pPhrase);
                query.setString(2, pLemma);
                query.setString(3, pType);
                query.setString(4 , pRole);
                query.setString(5, pEnglishLemma);
                query.setString(6, pPos);
                query.setInt(7, pRevisionLevel);
                query.executeUpdate();
           
                close(query);
    	     } 
          }
       }  
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }   
  
  public static void updateKnowledgeEnglishLemma(Connection conn, String finalEnglishLemma, String pToken)
    {
       try
       {   
    	   
           /* Statement prepare*/
    	   String addRecord = "update text.knowledge set english_lemma = ? "+
                               " where trim(phrase) =  ?;";
    	   
           PreparedStatement query = conn.prepareStatement(addRecord);
          
           query.setString(1, finalEnglishLemma);
           query.setString(2, pToken);
     
           
           query.executeUpdate();
           
           close(query);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    } 
    
  public static void updateTokenType(Connection conn,Integer vId,  String newType, Integer newLevel)
  {
     try
     {   
  	   
         /* Statement prepare*/
  	   String addRecord = "update text.knowledge set type = ?, revision_level = ? "+
                             " where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setString(1, newType);
         query.setInt(2,newLevel);
         query.setInt(3, vId);
   
         
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  

  public static void updateTokenRevisionLevel(Connection conn,Integer vId,  Integer newLevel)
  {
     try
     {   
  	   
         /* Statement prepare*/
  	   String addRecord = "update text.knowledge set revision_level = ? "+
                             " where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setInt(1,newLevel);
         query.setInt(2, vId);
   
         
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  
  
   public static ResultSet selectKnowledgeRevisionLevel(Connection conn,  Integer revisionLevel )
	// Return all knowledge records with revision_level = 0. 	  
		  
    { 
	   ResultSet rs = null;
       try
       {   
    	   PreparedStatement st ;
    	   
    	   st = conn.prepareStatement("SELECT * FROM TEXT.KNOWLEDGE WHERE revision_level = ? " + 
                                      " order by id");
    	   
    	   st.setInt(1,revisionLevel);
           rs = st.executeQuery();
    	  
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
       return (rs);
    } 
   
   
   public static ResultSet selectKnowledgePhraseLike(Connection conn,  String likeString )
 /* - Description: return records form table text.knowledge whose phrases are like likeString.
   *    
   * - Revision History:
   *     16/11/2015 - Maria Aux. Mora
	 *     
   * - Arguments (input / output):
	 *    conn       : database opened connection.
	 *    likeString : to compare with phrase.  The comparison uses like.  The string must include % requiered by like. 
	 *    
	 * - Return Values:
	 *      a resultset with knowledge records.
	 */  		  
      { 
  	   ResultSet rs = null;
         try
         {   
      	   PreparedStatement st ;
      	   
      	   st = conn.prepareStatement("SELECT * FROM TEXT.KNOWLEDGE WHERE phrase like ? " + 
                                        " order by id");
      	   
      	   st.setString(1,likeString);
             rs = st.executeQuery();
      	  
          }
          catch(SQLException e)
          {
              e.printStackTrace();
          }
         return (rs);
      } 
   
   public static ResultSet selectKnowledgePhraseWithDashAndLike(Connection conn,  String likeString )
   /* - Description: return records form table text.knowledge whose phrases are like likeString and include a dash.
     *    
     * - Revision History:
     *     16/11/2015 - Maria Aux. Mora
  	 *     
     * - Arguments (input / output):
  	 *    conn       : database opened connection.
  	 *    likeString : to compare with phrase.  The comparison uses like.  The string must include % requiered by like. 
  	 *    
  	 * - Return Values:
  	 *      a resultset with knowledge records.
  	 */  		  
        { 
    	   ResultSet rs = null;
           try
           {   
        	   PreparedStatement st ;
        	   
        	   st = conn.prepareStatement("SELECT * FROM TEXT.KNOWLEDGE WHERE phrase like '%-%' and phrase like ? " + 
                                          " order by id");
        	   
        	   st.setString(1,likeString);
               rs = st.executeQuery();
        	  
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
           return (rs);
        }
   
   
  
  /* CHARACTER and STRUCTURE ------------------------------------------------------------------------------------------------------------*/

   public static void deleteCharacters(Connection conn, Integer pBookId)
   { //Delete text.character of a book.  
 	  	  
     Statement stmt = null;
 	   
 	try
 	{      	     
 	 /* Statement prepared*/
 		 	  
 	  stmt = conn.createStatement();
 	  String sql = "delete from text.character where taxon_description_id in (select id from text.taxon_description t where book_id = " +
 	                pBookId.toString() + ");";
       stmt.executeUpdate(sql);
    }
    catch(SQLException e)
    {
        e.printStackTrace();
    }
 	  
   }
   
   
   public static void deleteBiologicalEntities(Connection conn, Integer pBookId)
   { //Delete text.biological_entity of a book.  
 	  	  
     Statement stmt = null;
 	   
 	try
 	{      	     
 	 /* Statement prepared*/
 		 	  
 	  stmt = conn.createStatement();
 	  String sql = "delete from text.biological_entity where taxon_description_id in (select id from text.taxon_description t where book_id = " +
 	                pBookId.toString() + ");";
       stmt.executeUpdate(sql);
    }
    catch(SQLException e)
    {
        e.printStackTrace();
    }
 	  
   } 
   
  
   
   
   
  public static Integer setStructureIdAccordingWithUsePreviousAssignedStructure(Connection conn, Integer p_structureId, TokenParser p_token)  
  /* - Description: return a structure_id according to:  
   * 		- If TEXT.INDICATOR.USE_PREVIOUS_ASSINGNED_STRUCTURE = 1 then return the last structure associated with a character that has 
   *  		  the same key of token
   *        - Else return p_structureId
   *   The procedure was developed to work with conjunction.  The structure assigned to the second adjective must be equal to the one asigned 
   *   to the first.      
   *    
   * - Revision History:
   *     1/10/2015 - Maria Aux. Mora
	 *     
   * - Arguments (input / output):
	 *    conn :         database opened connection.


	 * - Return Values:
	 *     a structureId
	 */
  
		{   
		 ResultSet rs = null;
		 Integer v_Id = null;
		    
		 Integer indicatorUseStructure = getUsePreviousAssignedStructureIndicator(conn);
		    
		 if (indicatorUseStructure!= null && indicatorUseStructure == 1){
		    	
		    try
			   {   
			   	PreparedStatement st ;
			 
			   	// To get the record_id.  The first structure related current chunk.  
		        st = conn.prepareStatement(" select min(id) from text.character  " +
	                                       " where taxon_description_id = ? and  line_number = ? and sequence = ?;" );
		        
		        st.setInt(1, p_token.getTaxonDescriptionId() );
			  	st.setInt(2, p_token.getLineNumber() );
			  	st.setInt(3, p_token.getSequence());
      
             	rs = st.executeQuery();
			    
               if (rs.next())   { 
            	   v_Id = rs.getInt(1);
		       }  
               
               // To get the biological_entity_id
               
		       st = conn.prepareStatement(" select biological_entity_id from text.character  " +
                        " where id = ?;" );

               st.setInt(1, v_Id);
               
               rs = st.executeQuery();
    
               if (rs.next())   { 
            	   v_Id = rs.getInt(1);
		       }  
                                                    
               if (v_Id !=null && v_Id > 0) p_structureId = v_Id;
               
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
		}
	   return (p_structureId) ;
	  
  }
  
  
  public static void insertAdjective(Connection conn, TokenParser p_tokenAdjective, Integer p_structureId, String p_tokenConstraint,
		    String p_adjectiveModifier, String otherConstraint){
   /* Inserta un adjetivo no verifica si este ya existe ya en la tabla text.Character
	*  
	*  1/10/2015 Verifica antes el indicador TEXT.INDICATOR.USE_PREVIOUS_ASSIGNED_STRUCTURE if este indicador es 1 implcia que se debe usar como 
	*  estructura la que fua asignada al caracter previo.  Este cambio se introdujo con el manejo de conjunciones. 
	 */ 
   try
      {  
	   
	  // Verify if TEXT.INDICATOR.USE_PREVIOUS_ASSINED_STRUCTURE is true/1.
	  p_structureId = setStructureIdAccordingWithUsePreviousAssignedStructure(conn, p_structureId, p_tokenAdjective); 
	  
	  String v_constraint; 
	   
	  // Verify if otherConstraint must be apply.  Just if INDICATOR.conjunction_indicator = 0
	  if ((otherConstraint != null) && (!otherConstraint.equals("")) && (conjunctionIndicatorIsCero(conn))) {
		  setConjunctionIndicator(conn, 1);		  
	  } else otherConstraint = "";
	  
	  // To assign " " to v_constraint instead of null just in case a vconstraint update is needed (null + String = null). 
	  if  (p_tokenConstraint == null)  v_constraint = " "; 
	  else v_constraint = p_tokenConstraint;
	 
	  if ( !(p_tokenAdjective.getOntologyCategory() ==null ) &&  p_tokenAdjective.getOntologyCategory().contains(",")) {
	  
		 /* In case a token match with more than one category in OTO this method includes one record for each category. 
		  *  The expert must verify wich record is correct.  
		  */
	     String[] parts = p_tokenAdjective.getOntologyCategory().split(",");
	  
	     for(String s:parts){
			  
	    	  String addRecord = " Insert into  TEXT.character (biological_entity_id , taxon_description_id, line_number, sequence, " +
                       " name,  modifier,  value,  char_type,      vconstraint , constraintid ,geographical_constraint, organ_constraint ," +
                       " other_constraint, parallelism_constraint, taxon_constraint, in_brackets ,     ontologyid, notes, token_tree_id, " + 
                       "  constraint_preposition, constraint_conjunction, verb, verb_string)" +
                       " values ( ?,?,?,?,   ?,?,?,?,   ?,?,?,?,   ?,?,?,?,    ?,?,?, ?,?, ?, ?);";

	          PreparedStatement query = conn.prepareStatement(addRecord);

              query.setInt(1 ,p_structureId);                             //biological_entity 
              query.setInt(2, p_tokenAdjective.getTaxonDescriptionId());  //taxon_description_id
              query.setInt(3, p_tokenAdjective.getLineNumber());          //line_number
              query.setInt(4, p_tokenAdjective.getSequence() );           // sequence
              query.setString(5, s );                                     //name
              query.setString(6,p_adjectiveModifier);                     // modifier
              query.setString(7, p_tokenAdjective.getWordForm() );        //value   
              query.setString(8, p_tokenAdjective.getKnowledgeType() );   //char_type
              query.setString(9,v_constraint );                           //vConstraint
              query.setString(10, null );                                 //constraintId
              query.setString(11, null );                                 //geographical_constraint
              query.setString(12, null );                                 //organ_constraint      
              query.setString(13, "");                                    //other_constraint
              query.setString(14, null );                                 //parallelism_constraint
              query.setString(15, null );                                 //taxon_constraint
              query.setInt(16, 0 );                                       //in_brackets
              query.setString(17, p_tokenAdjective.getOntologyId() );     //ontology_id
              query.setString(18, "Caracter repetido");                   //notes
              query.setInt(19, p_tokenAdjective.getTokenTreeId());        //token_tree_id  
              query.setString(20, "");                                    //constraint_preposition
              query.setString(21, otherConstraint);                       //constraint_conjunction
              query.setString(22, "");                       //verb
              query.setString(23, "");                       //verb_string
              
              query.executeUpdate();

              close(query);
        }
	  }
	  else {   // Insert a single record. 
		  
		   String addRecord = " Insert into  TEXT.character (biological_entity_id , taxon_description_id, line_number, sequence, " +
                              " name,  modifier,  value,  char_type,      vconstraint , constraintid ,geographical_constraint, organ_constraint ," +
                              " other_constraint, parallelism_constraint, taxon_constraint, in_brackets ,     ontologyid ,  token_tree_id, " +
                              "  constraint_preposition, constraint_conjunction, verb, verb_string, notes)" +
                              " values ( ?,?,?,?,   ?,?,?,?,   ?,?,?,?,   ?,?,?,?,    ?,?, ?, ?, ?,?, ?);";
    
		   PreparedStatement query = conn.prepareStatement(addRecord);

           query.setInt(1 ,p_structureId);                             // biological_entity_id
           query.setInt(2, p_tokenAdjective.getTaxonDescriptionId());  // taxon-description_id
           query.setInt(3, p_tokenAdjective.getLineNumber());          // line_number
           query.setInt(4, p_tokenAdjective.getSequence() );           // sequence 
           query.setString(5, p_tokenAdjective.getOntologyCategory() ); // name
           query.setString(6,p_adjectiveModifier);                      // modifier   
           query.setString(7, p_tokenAdjective.getWordForm() );         // value
           query.setString(8, p_tokenAdjective.getKnowledgeType() );    //char_type
           query.setString(9, v_constraint );                           // vConstraint   
           query.setString(10, null );                                  // constraintId
           query.setString(11, null );                                  // geographical_constraint
           query.setString(12, null );                                  // organ_constraint
           query.setString(13, "");                                     // other_constraint
           query.setString(14, null );                                  //parallelims_cosntraint
           query.setString(15, null );                                  // taxon_constraint     
           query.setInt(16, 0 );                                        // in_brackets
           query.setString(17, p_tokenAdjective.getOntologyId() );      // ontology_id
           query.setInt(18, p_tokenAdjective.getTokenTreeId());         // token_tree_id
           query.setString(19, "");                                     // constraint_preposition
           query.setString(20, otherConstraint);                                     //constraint_conjunction
           query.setString(21, "");                       //verb
           query.setString(22, "");                       //verb_string
           query.setString(23, "");                   //notes



          query.executeUpdate();

           close(query);
		  
	  }
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
	  
  }  

  public static void insertStructure (Connection conn, Integer p_structureId, TokenParser parentToken, String otherConstraint){
	   // inserta estructura a partir de una anterior.  Sirve para procesar articulos que acompa~nana a un modificar y crean por lo tanto una nueva estructura.....
	  
   try
	   {  
		   
		   // Se busca la estrutura anterior que cumpla con el genero y numero de la anterior
	   if (p_structureId > 1) {
		   TokenParser tokenStructure = selectLastStructureById(conn, p_structureId);
 
		   	   
      	   String addRecord = " Insert into  TEXT.biological_entity (taxon_description_id, line_number, sequence, name," +
	                              " biological_entity_type_id, vconstraint , constraintid ,geographical_constraint, " 
	                              + " parallelism_constraint, taxon_constraint, in_brackets , alter_name, "
	                              + "name_original,  ontologyid, pos, gender, number , knowledgetype, constraint_preposition, " +
	                              " relation, other_constraint, verb, verb_string, constraint_conjunction, notes)" +
	                              " values ( ?,?,?,?,   ?,?,?,?,   ?,?,?,?,   ?,?,?,?,    ?, ?,?,?, ?, ?, ?, ?, ?);";
	    
		   PreparedStatement query = conn.prepareStatement(addRecord);

           query.setInt(1, parentToken.getTaxonDescriptionId());
	       query.setInt(2, parentToken.getLineNumber());
	       query.setInt(3, parentToken.getSequence() );
	       query.setString(4, tokenStructure.getWordLemma() );
	       query.setInt(5, 1);                                    // Biological_entity_type_id
	       query.setString(6, parentToken.getWordForm() );    //vconstraint
	       query.setString(7, null );
	       query.setString(8, null );
	       query.setString(9, null );
	       query.setString(10, null);
	       query.setInt(11, 0 );
	       query.setString(12, null );
	       query.setString(13, tokenStructure.getWordForm() );
	       query.setString(14, tokenStructure.getOntologyId() );
	
	        query.setString(15, tokenStructure.getWordTag());
	       query.setString(16, tokenStructure.getGender());
	       query.setString(17, tokenStructure.getNumber() );
	       query.setString(18, tokenStructure.getKnowledgeType());
           query.setString(19, "");                                    // constraint_preposition
           query.setString(20, "");                                    // relation
           query.setString(21, "");                                    // other_constraint
           query.setString(22, "");                                    // verb
           query.setString(23, "");                                    // string from verb to the end of chunk.
           query.setString(24, "");                                    // constraint_conjunction
           query.setString(25, "");                                    // notes
           
	       query.executeUpdate();

	       close(query);
		}
			  
	 }
	 catch(SQLException e){
	      e.printStackTrace();
	 }
		  
 }
  
  
  public static void insertStructure (Connection conn, TokenParser p_tokenStructure, String p_tokenConstraint, String otherConstraint){
	   
	   try
	      {  
		   
		   String v_constraint; 
		   
		   // Verify if otherConstraint must be apply.  Just if INDICATOR.conjunction_indicator = 0
			if ((otherConstraint != null) && (!otherConstraint.equals("")) && (conjunctionIndicatorIsCero(conn))) {
			  setConjunctionIndicator(conn, 1);		  
			} else otherConstraint = "";
			   
		   if  (p_tokenConstraint == null)  v_constraint = ""; 
		   else v_constraint = p_tokenConstraint;
		   	   
     	   String addRecord = " Insert into  TEXT.biological_entity (taxon_description_id, line_number, sequence, name," +
	                              " biological_entity_type_id, vconstraint , constraintid ,geographical_constraint, " 
	                              + " parallelism_constraint, taxon_constraint, in_brackets , alter_name, "
	                              + "name_original,  ontologyid, pos, gender, number , knowledgetype, constraint_preposition, " +
	                              " relation, other_constraint, verb, verb_string, constraint_conjunction, notes)" +
	                              " values ( ?,?,?,?,   ?,?,?,?,   ?,?,?,?,   ?,?,?,?,    ?, ?,?,?, ?, ?, ?, ?, ?);";
	    
		   PreparedStatement query = conn.prepareStatement(addRecord);

          query.setInt(1, p_tokenStructure.getTaxonDescriptionId());
	       query.setInt(2, p_tokenStructure.getLineNumber());
	       query.setInt(3, p_tokenStructure.getSequence() );
	       query.setString(4, p_tokenStructure.getWordLemma() );
	       query.setInt(5, 1);
	       query.setString(6, v_constraint );
	       query.setString(7, null );
	       query.setString(8, null );
	       query.setString(9, null );
	       query.setString(10, null);
	       query.setInt(11, 0 );
	       query.setString(12, null );
	       query.setString(13, p_tokenStructure.getWordForm() );
	       query.setString(14,p_tokenStructure.getOntologyId() );
	
	        query.setString(15, p_tokenStructure.getWordTag());
	       query.setString(16, p_tokenStructure.getGender());
	       query.setString(17, p_tokenStructure.getNumber() );
	       query.setString(18, p_tokenStructure.getKnowledgeType());
           query.setString(19, "");                                    // constraint_preposition
           query.setString(20, "");                                    // relation
           query.setString(21, "");                                    // other_constraint
           query.setString(22, "");                                    // verb
           query.setString(23, "");                                    // string from verb to the end of chunk.
           query.setString(24, otherConstraint);                                    // constraint_conjunction
           query.setString(25, "");                                    // notes

	       query.executeUpdate();

	       close(query);
			  
	      }
	      catch(SQLException e)
	      {
	          e.printStackTrace();
	      }
		  
	  }
  
  
  public static Integer cloneStructureById(Connection conn, Integer structureId){
   // Clone the structure that has the id = structureId and return the new structureId.
	  
	Integer newId = null;
	PreparedStatement st ;
    ResultSet rs = null;
    
    TokenParser tokenResult = null;   // To search the new structure
    

	try
	{  
		   
	   if ((structureId != null) && (structureId>0) ){
		  
	  	      st = conn.prepareStatement("SELECT taxon_description_id, line_number, sequence, name," +
	                              " biological_entity_type_id, vconstraint , constraintid ,geographical_constraint, " 
	                              + " parallelism_constraint, taxon_constraint, in_brackets , alter_name, "
	                              + "name_original,  ontologyid, notes, pos, gender, number , knowledgetype, constraint_preposition, " +
	                              " relation, other_constraint, verb, verb_string, constraint_conjunction FROM TEXT.BIOLOGICAL_ENTITY WHERE ID = ? ");
	  	      st.setInt(1,structureId);
	  			   
	       	  rs = st.executeQuery();
	       	  
	   	      if (rs.next()){
	  		     
	   	    	  tokenResult = new TokenParser(null, rs.getInt(1), rs.getInt(2), rs.getString(4), "",  rs.getString(16), 
   	        		   "", "", rs.getInt(3), null, "", "", "", null);
	   	    	  
		   	       
        	      String addRecord = " Insert into  TEXT.biological_entity (taxon_description_id, line_number, sequence, name," +
	                              " biological_entity_type_id, vconstraint , constraintid ,geographical_constraint, " 
	                              + " parallelism_constraint, taxon_constraint, in_brackets , alter_name, "
	                              + "name_original,  ontologyid, notes, pos, gender, number , knowledgetype, constraint_preposition, " +
	                              " relation, other_constraint, verb, verb_string, constraint_conjunction)" +
	                              " values ( ?,?,?,?,   ?,?,?,?,   ?,?,?,?,   ?,?,?,?,    ?, ?,?,?,  ?, ?, ?, ?,  ?);";
	    
		          PreparedStatement query = conn.prepareStatement(addRecord);

	              query.setInt(1, rs.getInt(1));
		          query.setInt(2, rs.getInt(2));
		          query.setInt(3, rs.getInt(3));
		          query.setString(4, rs.getString(4) );						//name
		          query.setInt(5, rs.getInt(5));                            // Biological_entity_type_id
		    
		          query.setString(6, "" );                              //vconstraint
		          query.setString(7, rs.getString(7)  );					// constraintid
		          query.setString(8, "" );						//geographical_constraint
		          query.setString(9,  "" );						//paralellism_constraint
		          query.setString(10, "");					//taxon_constraint
		          query.setInt(11, rs.getInt(11) );							//in_brackets
		          query.setString(12, rs.getString(12) );					//alter_name
		          query.setString(13, rs.getString(13) );					// name_original
		          query.setString(14, rs.getString(14) );					//ontologyId
		          if (rs.getString(15) != null) query.setString(15, rs.getString(15));					// notes	
		          else query.setString(15, rs.getString(15));	
		          query.setString(16, rs.getString(16));					//pos
		          query.setString(17, rs.getString(17) );					//gender
		          query.setString(18, rs.getString(18));					//number
	              query.setString(19, rs.getString(19));                    // knowledge_type
	              
	                               
	              query.setString(20, "");  // constraint_preposition
	              query.setString(21, "");                                         // relation
	             
	              query.setString(22, "");                                          // other_constraint
	              
	              query.setString(23, "");                                         // verb
	              query.setString(24, "");    
	              query.setString(25, "");                                          // constraint_conjunction
	           
		          query.executeUpdate();
	
		          close(query);
		 }
			  
	   }
	 }  
	 catch(SQLException e){
	      e.printStackTrace();
	 }
	
	  newId = selectPreviousStructureSameChunk(conn, tokenResult);  
	  
	  return (newId);
  }
  
  public static ResultSet selectBiologicalEntity(Connection conn,  Integer p_taxon_description_id )
	// Return all structures associated with p_taxon_description_id. 	  
		  
    { 
	   ResultSet rs = null;
       try
       {   
    	   PreparedStatement st ;
    	   
    	   st = conn.prepareStatement("SELECT * FROM TEXT.BIOLOGICAL_ENTITY WHERE taxon_description_id = ? " + 
                                      " order by taxon_description_id, line_number, id");
    	   
    	   st.setInt(1,p_taxon_description_id);
           rs = st.executeQuery();
    	  
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
       return (rs);
    } 
  
  public static ResultSet selectBiologicalEntity(Connection conn,  Integer p_taxon_description_id, Integer lineNumber)
	// Return all structures associated with p_taxon_description_id. 	  
		  
    { 
	   ResultSet rs = null;
       try
       {   
    	   PreparedStatement st ;
    	   
    	   if (lineNumber != null){
    	      st = conn.prepareStatement("SELECT * FROM TEXT.BIOLOGICAL_ENTITY WHERE taxon_description_id = ? and line_number = ? " + 
                                      " order by taxon_description_id, line_number, id");
    	   
    	      st.setInt(1,p_taxon_description_id);
    	      st.setInt(2, lineNumber);
              rs = st.executeQuery();
    	   } else
    	   { 	st = conn.prepareStatement("SELECT * FROM TEXT.BIOLOGICAL_ENTITY WHERE taxon_description_id = ? " + 
                   " order by taxon_description_id, line_number, id");
    	   
    	   		st.setInt(1,p_taxon_description_id);
    	   		rs = st.executeQuery();
    		   
    	   }
    	  
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
       return (rs);
    } 
  
  
  
  public static Integer selectLastStructure (Connection conn, TokenParser p_token){
	   
	    /* - Description: return the last structure_id in the same chunk (first) (that has the same 
	     *                taxon_description, line_number and sequence) of p_token.  If there is no structure in the same 
	     *                chunk the method find the the last structure included before p_token that has the same gender an number.
	     *                 If not it return  first structure of the clause   and if it has the same gender and number it return it.
	     *                 
	     * - Revision History:
	     *     21/06/2015 - Maria Aux. Mora
	     *     16/12/2016 - 
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     A structure_id that fulfill the conditions. 
		 */
			 
		{   
		    ResultSet rs = null;
		    Integer v_Id = null, v_lastId = null;
		    Boolean v_foundIt = false;
		    String v_entityGender, v_entityNumber, v_tokenGender, v_tokenNumber;
		   	PreparedStatement st ;
		   	Integer mainStructure = null;
			
			    
		    v_tokenGender = p_token.getGender();
		    v_tokenNumber =p_token.getNumber();
			   
		    // For tokens without gender or number (i.e numbers).
	        if (v_tokenGender == null) v_tokenGender = "N";
	    	if (v_tokenNumber == null) v_tokenNumber = "N";
	    	   
	    	   
			try
			   {   
				
				// Find the last included structure of the same chunk if there are not conjuctions being processed.
				 Integer indicatorUseStructure = getUsePreviousAssignedStructureIndicator(conn);
				 if (indicatorUseStructure!= null && indicatorUseStructure != 1){
				     v_Id = selectPreviousStructureSameChunk(conn, p_token);
				 }   
				if (v_Id == null || v_Id == 0) { 
					
				    /* If there are not structures in the same chunk -> find the last structure of the clause that has the 
						*  same gender and numbre than p_token.
						*/
					 
				        st = conn.prepareStatement(" select id, taxon_description_id, line_number, sequence, name, gender, number " +
		                                         " from text.biological_entity  " +
			                                       " where taxon_description_id = ? and line_number = ?  " +
			                                       " order by id desc;" );
				        
				        st.setInt(1, p_token.getTaxonDescriptionId() );
				    	st.setInt(2, p_token.getLineNumber() );
					 			   	    	         
				      	rs = st.executeQuery();
			
				   	    while ((!v_foundIt) && (rs.next()) )
				        {  /* Fetch next clause record */ 
				   	    	   v_entityGender = rs.getString(6);
				   	    	   v_entityNumber = rs.getString(7);
				   	    	   
				   	    	   // For tokens without gender or number (i.e numbers). Skip them. 
				   	    	   if (v_entityGender == null) v_entityGender = "";  //before it was N to use it.
				   	    	   if (v_entityNumber == null) v_entityNumber = "";  //before it was N 
				   	    	   
				     	       if  ( (v_tokenGender.equals(v_entityGender ) || v_entityGender.equals("N") || v_entityGender.equals("C") || v_tokenGender.equals("C") || v_tokenGender.equals("N") ) 
				     	    		   && (v_tokenNumber.equals(v_entityNumber) || v_entityNumber.equals("N") || v_tokenNumber.equals("N")) )
				     	    		    {
				     	    	 v_foundIt = true;
				     	    	 v_Id = rs.getInt(1);
				     	       } 
				     	       v_lastId = rs.getInt(1);
				     	    }  
					   	
					   	  			
				   
					if (v_Id == null || v_Id == 0) {	//aqui			
						// =============================================================================
						// find the first structure of the clause and compare gender and number with p_token
					 
				        st = conn.prepareStatement(" select id, taxon_description_id, line_number, sequence, name, gender, number " +
		                                         " from text.biological_entity  " +
			                                       " where taxon_description_id = ? and line_number = ?  " +
			                                       " order by id;" );
				        
				        st.setInt(1, p_token.getTaxonDescriptionId() );
				    	st.setInt(2, p_token.getLineNumber() );
		
					 			   	    	         
				      	rs = st.executeQuery();
					  	
					    
				   	    if (rs.next())
				        {  /* Fetch next clause record */ 
				   	    	   v_entityGender = rs.getString(6);
				   	    	   v_entityNumber = rs.getString(7);
				   	    	   
				   	    	   // For tokens without gender or number (i.e numbers). Skip them. 
				   	    	   if (v_entityGender == null) v_entityGender = "";  //before it was N to use it.
				   	    	   if (v_entityNumber == null) v_entityNumber = "";  //before it was N 
				   	    	   
				     	       if  ( (v_tokenGender.equals(v_entityGender ) || v_entityGender.equals("N") || v_entityGender.equals("C") || v_tokenGender.equals("C") || v_tokenGender.equals("N") ) 
				     	    		   && (v_tokenNumber.equals(v_entityNumber) || v_entityNumber.equals("N") || v_tokenNumber.equals("N")) )
				     	    		    {
				     	    	 v_foundIt = true;
				     	    	 v_Id = rs.getInt(1);
				     	    	 mainStructure = v_Id;
				     	       } 
				     	    }	  
				   	    // =====================================================================
		
						
			       } // if
				 }
		    }
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
			 // If there are not  structures in a clause then assing 1 to structure id. 
	/*		 if ((v_Id == null && v_lastId == null) ){
			    	v_Id = 1;
			    }
			 else 
				 if (v_Id == null && !(v_lastId == null) || (v_Id == 0 && !(v_lastId == null) ))
					 v_Id = v_lastId;
			
			 if (v_Id == null || v_Id == 0) v_Id = 1;
			 
			 if (v_Id == 1 &&  v_lastId != null && v_lastId !=0) v_Id = v_lastId;
			 else if (mainStructure != null && mainStructure != 0) v_Id = mainStructure;
	*/
			 if (v_Id == null || v_Id == 0) v_Id = 1;
			 if (v_Id == 1 &&  v_lastId != null && v_lastId !=0) v_Id = v_lastId;
			 
			 if (v_Id == 1 && mainStructure != null && mainStructure != 0) v_Id = mainStructure;
			 
			 return (v_Id) ;
		}
		
  }
  
  public static Integer selectLastStructureByGenderAndNumber (Connection conn, TokenParser p_token, String p_gender, String p_number){
	   
	    /* - Description: return the last structure_id in the same clause  (that has the same taxon_description_id and line_number) of 
	     *                p_token and gender equals p_gender and number equals p_number. 
	     *    
	     * - Revision History:
	     *     21/06/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     A structure_id that fulfill the conditions. 
		 */
			 
		{   
		    ResultSet rs = null;
		    Integer v_Id = null, v_lastId = null;
		    Boolean v_foundIt = false;
		    String v_entityGender, v_entityNumber, v_tokenGender, v_tokenNumber;
			    
		    v_tokenGender = p_gender;
		    v_tokenNumber =p_number;
			   
		    // For tokens without gender or number (i.e numbers).
	        if (v_tokenGender == null) v_tokenGender = "N"; // N = neutro
	    	if (v_tokenNumber == null) v_tokenNumber = "N"; // N = neutro
	    	   
	    	   
			try
			   {   
			   	PreparedStatement st ;
			   	
			   	//Le quite:  
			 
		        st = conn.prepareStatement(" select id, taxon_description_id, line_number, sequence, name, gender, number " +
                                       " from text.biological_entity  " +
	                                       " where taxon_description_id = ? and line_number = ?  " +
	                                       " order by id desc;" );
		        
		        st.setInt(1, p_token.getTaxonDescriptionId() );
		    	st.setInt(2, p_token.getLineNumber() );

			 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
		   	    while ((!v_foundIt) && (rs.next()) )
		        {  /* Fetch next clause record */ 
		   	    	   v_entityGender = rs.getString(6);
		   	    	   v_entityNumber = rs.getString(7);
		   	    	   
		   	    	   // For tokens without gender or number (i.e numbers).
		   	    	   if (v_entityGender == null) v_entityGender = "N";
		   	    	   if (v_entityNumber == null) v_entityNumber = "N";
		   	    	   
		     	       if  ( (v_tokenGender.equals(v_entityGender ) || v_entityGender.equals("N") || v_entityGender.equals("C") 
		     	    		   || v_tokenGender.equals("C") || v_tokenGender.equals("N") ) 
		     	    		   && (v_tokenNumber.equals(v_entityNumber) || v_entityNumber.equals("N") || v_tokenNumber.equals("N")) )
		     	    		    {
		     	    	 v_foundIt = true;
		     	    	 v_Id = rs.getInt(1);
		     	       } 
		     	       v_lastId = rs.getInt(1);
		     	    }  
			   	
			   	  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
			 // If there are not  structures in a clause then assing 1 to structure id. 
			 if (v_Id == null && v_lastId == null){
			    	v_Id = 1;
			    }
			 else // Assing the last estructure Id (thus: the structure that is at the beginning of the clause). 
				 if (!(v_lastId == null)) 
					 v_Id = v_lastId;
			
			 return (v_Id) ;
		}
		
}
  
  
  
  public static TokenParser selectLastStructureById (Connection conn, Integer structureId){
	   
	    /* - Description: return the structure which id is equals structureId 
	     *                If there is not structure that fulfill the condition it returns  struture #1. 
	     *               
	     * - Revision History:
	     *     20/08/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     A structure_id that fulfill the conditions. 
		 */
			 
		{   
		    ResultSet rs = null;
		    TokenParser tokenResult = null;
  	   
			try
			   {   
			   	PreparedStatement st ;
			   	
			   	//Le quite:  
			 
		        st = conn.prepareStatement(" select id, taxon_description_id, line_number, sequence, name, biological_entity_type_id, " + 
		                                           " in_brackets, alter_name, name_original, ontologyid, provenance, pos, gender, number, knowledgeType " +
                                     " from text.biological_entity  " +
	                                       " where id = ? ;" );
		        
		        st.setInt(1, structureId );

			 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
		   	   if (rs.next())
		        {  /* Fetch next clause record */ 
		   	    	 	 
		     	    // Generates the new token parser 
		     	    // Paramenters: TokenParser (p_token_id, p_taxon_descrition_id,  p_line_number,
		     		//           p_form,  p_lemma, p_tag, p_tag1, p_tag2, p_sequence,  p_book_id,
		    		//            p_knowledgeType,  p_ontologyCategory, p_ontologyId, p_tokenTreeId)
		     	    tokenResult = new TokenParser(rs.getInt(1),   // id
		     	    		             rs.getInt(2),     // taxon_description_id
		     	        	             rs.getInt(3),     // line_number
		     	    		             rs.getString(9),  // original_name or Word form
		     	    		             rs.getString(5),  // lemma 
		     	    		             rs.getString(12), // POS
		 	        		                null,             // tag1
		 	        		                null,             // tag2
		 	        		                rs.getInt(4),     // sequence or chunk number 
		 	        		                null,             // book_id
		 	        		               rs.getString(15),  // knowledgeType
		 	        		                "structure",      // OntologyCategory (estrutura)
		 	        		               rs.getString(10),  // ontologyId 
		 	        		               null );            // token_tree_id
		     	       }
		   	   else {
		   		tokenResult = new TokenParser(1,   // id
 			             null,     // taxon_description_id
 			             null,     // line_number
 			             "entidad temporal",  // original_name or Word form
 			             null,  // lemma 
 			             null, // POS
 		                 null,             // tag1
 		                 null,             // tag2
 		                 null,     // sequence or chunk number 
 		                 null,             // book_id
 		                 null,  // knowledgeType
 		                 "structure",      // OntologyCategory (estrutura)
 		                 null,  // ontologyId 
 		                null );            // token_tree_id
			    	
		   	   }
			   	
			   	  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }		
			return (tokenResult);
	
     }
}    
  
  
  public static Integer selectMaxBiologicalEntityId (Connection conn){
	   
	    /* - Description: return the max structure id of table BIOLOGICAL_ENTITY. 
	     *    
	     * - Revision History:
	     *    12/2/2016 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *    Max id. 
		 */
			 
		{   
		    ResultSet rs = null;
		    Integer v_Id = null;
		
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select max (id) from text.biological_entity ;" );
		       		   	    	         
		      	rs = st.executeQuery();
			  	
			    
             if (rs.next())   { 
      	       v_Id = rs.getInt(1);
		       }  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
			 return (v_Id) ;
		}
}  
	  

  public static Integer selectPreviousStructureNoMatterGenusAndNumber (Connection conn, TokenParser p_token){
	   
	    /* - Description: return the last structure_id that has the same key (taxon_description_id, line_number) of p_token. 
	     *   if there are no structure return NULL.
	     *    
	     * - Revision History:
	     *     22/06/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     A structure_id that fulfill the conditions. 
		 */
			 
		{   
		    ResultSet rs = null;
		    Integer v_Id = null;
		 
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select max(id) from text.biological_entity  " +
	                                       " where taxon_description_id = ? and  line_number = ? ;" );
		        
		        st.setInt(1, p_token.getTaxonDescriptionId() );
			  	st.setInt(2, p_token.getLineNumber() );

			 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
               if (rs.next())   { 
            	   v_Id = rs.getInt(1);
		       }  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
			 return (v_Id) ;
		}
}
  
  public static Integer selectMainStructureId (Connection conn, Integer taxonDescriptionId, Integer lineNumber  ){
	   
	    /* - Description: return the main structure id of a clause. 
	     *   if there are no structure return NULL.
	     *    
	     * - Revision History:
	     *     9/10/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     A structure's original_name that fulfill the conditions. 
		 */
			 
		{   
		    ResultSet rs = null;
		    Integer v_Id = null;
		
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select min (id) from text.biological_entity  " +
	                                       " where taxon_description_id = ? and  line_number = ? ;" );
		        
		        st.setInt(1, taxonDescriptionId );
			  	st.setInt(2, lineNumber );

			 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
               if (rs.next())   { 
        	       v_Id = rs.getInt(1);
		       }  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
			 return (v_Id) ;
		}
}
  
  
  public static String selectMainStructureClassOriginalName (Connection conn, Integer taxonDescriptionId, Integer lineNumber  ){
	   
	    /* - Description: return the main structure original_name of the clause that has the structureid. 
	     *   if there are no structure return NULL.
	     *    
	     * - Revision History:
	     *     2/10/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     A structure's original_name that fulfill the conditions. 
		 */
			 
		{   
		    ResultSet rs = null;
		    Integer v_Id = null;
		    String theOriginalName = "";
		 
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select min (id) from text.biological_entity  " +
	                                       " where taxon_description_id = ? and  line_number = ? ;" );
		        
		        st.setInt(1, taxonDescriptionId );
			  	st.setInt(2, lineNumber );

			 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
             if (rs.next())   { 
          	   v_Id = rs.getInt(1);
		       
          	   st = conn.prepareStatement(" select name_original from text.biological_entity  " +
                        " where id = ?  ;" );

          	   st.setInt(1, v_Id );

		   	    	         
          	   rs = st.executeQuery();


          	   if (rs.next())   { 
          		 theOriginalName = rs.getString(1);
         
          	   }
		     }  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
			 return (theOriginalName) ;
		}
}
  
  public static String selectMainStructureClassName (Connection conn, Integer taxonDescriptionId, Integer lineNumber  ){
	   
	    /* - Description: return the main structure name of the clause that has the structureid. 
	     *   if there are no structure return NULL.
	     *    
	     * - Revision History:
	     *     2/10/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     A structure's name that fulfill the conditions. 
		 */
			 
		{   
		    ResultSet rs = null;
		    Integer v_Id = null;
		    String theName = "";
		 
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select min (id) from text.biological_entity  " +
	                                       " where taxon_description_id = ? and  line_number = ? ;" );
		        
		        st.setInt(1, taxonDescriptionId );
			  	st.setInt(2, lineNumber );

			 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
           if (rs.next())   { 
        	   v_Id = rs.getInt(1);
		       
        	   st = conn.prepareStatement(" select name_original from text.biological_entity  " +
                      " where id = ?  ;" );

        	   st.setInt(1, v_Id );

		   	    	         
        	   rs = st.executeQuery();


        	   if (rs.next())   { 
        		 theName = rs.getString(1);
       
        	   }
		     }  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
			 return (theName) ;
		}
}
  
  
  public static Integer selectPreviousStructureNoMatterGenusAndNumberSameTaxonDescription (Connection conn, TokenParser p_token){
	   
	    /* - Description: return the last structure_id that has the same key (taxon_description_id) of p_token.  
	     *   If there are no structure return NULL.
	     *    
	     * - Revision History:
	     *     22/06/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     A structure_id that fulfill the conditions. 
		 */
			 
		{   
		    ResultSet rs = null;
		    Integer v_Id = null;
		 
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select max(id) from text.biological_entity  " +
	                                       " where taxon_description_id = ?  ;" );
		        
		        st.setInt(1, p_token.getTaxonDescriptionId() );
			 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
             if (rs.next())   { 
          	   v_Id = rs.getInt(1);
		       }  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
			 return (v_Id) ;
		}
}
  
  
  public static Integer selectPreviousStructureSameChunk (Connection conn, TokenParser p_token){
	   
	    /* - Description: return the last structure_id that has the same key (taxon_description_id, line_number, sequence) of p_token. 
	     *  If there are no structure return NULL.
	     *    
	     * - Revision History:
	     *     2/09/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     A structure_id that fulfill the conditions. 
		 */
			 
		{   
		    ResultSet rs = null;
		    Integer v_Id = null;
		 
		    try
			 {   
		    	
		    	if ( p_token != null){
				   	PreparedStatement st ;
				 
			        st = conn.prepareStatement(" select max(id) from text.biological_entity  " +
		                                       " where taxon_description_id = ? and  line_number = ? and sequence = ? ;" );
			        
			        st.setInt(1, p_token.getTaxonDescriptionId() );
				  	st.setInt(2, p_token.getLineNumber() );
				  	st.setInt(3, p_token.getSequence() );
		
	
				 			   	    	         
			      	rs = st.executeQuery();
				  	
				    
	                if (rs.next())   { 
	          	      v_Id = rs.getInt(1);
			          }  
			    }
			 }
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
			 return (v_Id) ;
		}
}
  
  
  public static String fromPrepositiontoEndString(Connection conn, TokenParser p_token, String p_closeWord){
	   
	    /* - Description: return from the tokenParser (or preposition) to the end of the chunk.  It is used for procesing prepositions. 
	     * 
	     * - Revision History:
	     *     2/09/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *    p_token: Token being evaluated (for instance a preposition).
		 *    p_closeWord:  Word taht will be used as a control word in case the token is repeated in the chunk.  
		 *   
		 * - Return Values:
		 *     A string that include the text from the current preposition to the end of the cunk. 
		 */
			 
		{   
		    ResultSet rs = null;               // Use to acces the database and return the chunk record that has the same key of p_token.       
		    String chunkContents  = null;      // Chunk value (database value)
		    int pos;                          
		    String prepositionString = null;   // Result
		    String chunkContentsFromPrepToEnd; // Chunk's substring from the preposition to the end.  
		    Integer j;
		 
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select contents from text.chunk  " +
	                                       " where taxon_description_id = ? and  line_number = ? and sequence = ? ;" );
		        
		        st.setInt(1, p_token.getTaxonDescriptionId() );
			  	st.setInt(2, p_token.getLineNumber() );
			  	st.setInt(3, p_token.getSequence());

			 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
             if (rs.next())   { 
          	   chunkContents = " " + rs.getString(1) + " ";
		       }  
             
             if (chunkContents != null){
            	 // Split the chunk in part using the initial character.  To verify if the character is repeted more than one
            	 // in the same chunk and identify the part of the chunk that include the additional word (as a control caracter.). 
          
            	// To avoid problems with word repetitions (to skip the first part of the chunk, from the begining to the first occurence of the preposition).
            	 
            	 j = chunkContents.indexOf(" " + p_token.getWordForm()+" ");
            	 
            	 if (j<0)  j = chunkContents.indexOf(" " + p_token.getWordForm());
            	 
            	 if (j >= 0) {
            		 
                	 chunkContentsFromPrepToEnd = chunkContents.substring(j);  

            	     String[] parts = chunkContentsFromPrepToEnd.split(" " + p_token.getWordForm() + " ");
            	 
            	     for (int i = 0; i<parts.length; i++ ){
            	    	 if (p_closeWord != null) {
            		        pos = parts[i].indexOf(p_closeWord);
            		        if (pos >= 0){
            			  
            			       prepositionString = p_token.getWordForm() + " " + chunkContentsFromPrepToEnd.substring(chunkContentsFromPrepToEnd.indexOf(parts[i])) ;
            		         }
            	    	 } else {
            	    		   prepositionString = p_token.getWordForm() + " " + chunkContentsFromPrepToEnd;
            	    	 }   
            	     }
            	     if (prepositionString == null) 
            	    	 prepositionString = chunkContentsFromPrepToEnd;
            	 }    
 		     } 			
             
		}
		    
		catch(SQLException e) {
			e.printStackTrace();
	    }
		
		    
		return (prepositionString) ;
	}
}
  
  public static String selectChunkContentsByToken(Connection conn, TokenParser p_token){
	   
	    /* - Description: return the contents  of the chunk with (taxon_description_id, line_number and seuqence) equals to p_token. 
	     *     
	     * - Revision History:
	     *     12/10/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *    p_token: Token being evaluated (for instance a preposition).
		 *   
		 * - Return Values:
		 *      the chunk contents
 		 */
			 
		{   
		    ResultSet rs = null;               // Use to acces the database and return the chunk record that has the same key of p_token.       
		    String chunkContents  = null;      // Chunk value (database value)

		    
		 
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select contents from text.chunk  " +
	                                       " where taxon_description_id = ? and  line_number = ? and sequence = ? ;" );
		        
		        st.setInt(1, p_token.getTaxonDescriptionId() );
			  	st.setInt(2, p_token.getLineNumber() );
			  	st.setInt(3, p_token.getSequence());
      
		      	rs = st.executeQuery();			  	
			    
           if (rs.next())   { 
        	   chunkContents = rs.getString(1) ;
		       }  
   
		} 			
		    
		catch(SQLException e) {
			e.printStackTrace();
	    }
		
		    
		return (chunkContents) ;
	}
} 
  
  public static String fromTextToEndString(Connection conn, TokenParser p_token){
	   
	    /* - Description: return the sub-string from tokenParser to the end of the chunk.  It is used for procesing verbs. 
	     *      It asume that p_token is not repeated inside the chunk. 
	     * 
	     * - Revision History:
	     *     14/09/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *    p_token: Token being evaluated (for instance a preposition).
		 *    p_closeWord:  Word taht will be used as a control word in case the token is repeated in the chunk.  
		 *   
		 * - Return Values:
		 *     A string that include the text from the current preposition to the end of the cunk. 
		 */
			 
		{   
		    ResultSet rs = null;               // Use to acces the database and return the chunk record that has the same key of p_token.       
		    String chunkContents  = null;      // Chunk value (database value)
	
		    String chunkContentsFromPrepToEnd = null; // Chunk's substring from the preposition to the end.  
		    Integer i;
		    
		 
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select contents from text.chunk  " +
	                                       " where taxon_description_id = ? and  line_number = ? and sequence = ? ;" );
		        
		        st.setInt(1, p_token.getTaxonDescriptionId() );
			  	st.setInt(2, p_token.getLineNumber() );
			  	st.setInt(3, p_token.getSequence());
    
		      	rs = st.executeQuery();			  	
			    
         if (rs.next())   { 
      	   chunkContents = rs.getString(1) ;
		       }  
         
         if (chunkContents != null){     
      	 i = chunkContents.indexOf( p_token.getWordForm() );  
      	 if (i>=0) chunkContentsFromPrepToEnd = chunkContents.substring(i);  
      	 else chunkContentsFromPrepToEnd = chunkContents.substring(0); 
        }
		} 			
		    
		catch(SQLException e) {
			e.printStackTrace();
	    }
		
		    
		return (chunkContentsFromPrepToEnd) ;
	}
}  
  

  public static void updateStructureConstraintById (Connection conn, TokenParser p_currentToken,  Integer p_id, String otherConstraint)
  {
     try
     {   
  	   
		 // Verify if otherConstraint must be apply.  Just if INDICATOR.conjunction_indicator = 0
		 if ((otherConstraint != null) && (!otherConstraint.equals("")) && (conjunctionIndicatorIsCero(conn))) {
			 setConjunctionIndicator(conn, 1);		  
	     } else otherConstraint = "";    	 
   	 
    	 
           	 
  	     String addRecord = "update text.biological_entity set vconstraint = vconstraint || ' | ' ||  ?, "
  	     		            + " gender = ? , number = ?, constraint_conjunction = constraint_conjunction || '|' || ? "+
                             " where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setString(1, p_currentToken.getWordForm().trim() );
         query.setString(2, "N");
         query.setString(3, "N");         
         query.setString(4, otherConstraint);
         query.setInt(5, p_id);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  
  public static void updateStructureConstraintPrepositionById(Connection conn, Integer structureId, String prepositionString,
		  String initialPreposition, TokenParser currentToken, String tokenConstraint)
  { // Actualiza el campo BIOLOGICAL_ENTITY.CONSTRAINT_PREPOSITION with prepositionString.  If the field is not empty,
	// the method keep the previous information. 
     try
     {   if (prepositionString != null) {
    	 
	    	 String theNote = "";  // To indicate that the structure includes the text of a initial preposition that must be presented as a 
	    	                  // new structure.
	    	 
	    	 if (initialPreposition!= null && initialPreposition.equals(currentToken.getWordForm().trim())){
	    		 theNote = "init with preposition";
	    	 }
	  	          	 
	  	     String addRecord = "update text.biological_entity set constraint_preposition = constraint_preposition || ' | ' ||  ? , " +
	                             " notes = notes || '|' || ?, vconstraint = vconstraint || ' | ' || ?  where id =  ?;";
	  	   
	         PreparedStatement query = conn.prepareStatement(addRecord);
	        
	         if (prepositionString != null) query.setString(1, prepositionString);
	         else query.setString(1, "");
	         
	         if (theNote != null) query.setString(2, theNote);
	         else query.setString(2, "");
	       
	         if (tokenConstraint != null) query.setString(3, tokenConstraint);
	         else query.setString(3, "");
	         
	         query.setInt(4, structureId);
	   
	         query.executeUpdate();
	         
	         close(query);
          } 
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  public static void cleanStructureConstraintPrepositionById(Connection conn, Integer structureId)
  { // Actualiza el campo BIOLOGICAL_ENTITY.CONSTRAINT_PREPOSITION with prepositionString.  If the field is not empty,
	// the method keep the previous information. 
     try
     {   
 	 
  	     String addRecord = "update text.biological_entity set constraint_preposition = '' , " +
                             " notes = '', relation = ''  where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setInt(1, structureId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  
  
  public static void updateStructureNameById(Connection conn, Integer structureId, String newName)
  { // Update the field BIOLOGICAL_ENTITY.name with the new name.  
     try
     {   
    	 String addRecord = "update text.biological_entity set name =  ?  " +
                             "  where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setString(1, newName);
         query.setInt(2, structureId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  public static void updateStructureConstraintConjunctionById(Connection conn, Integer structureId, String conjunctionString)
  { // Actualiza el campo BIOLOGICAL_ENTITY.CONSTRAINT_CONJUNCTION with conjunction string.  If the field is not empty,
	// the method keep the previous information. 
     try
     {   
  	          	 
  	     String addRecord = "update text.biological_entity set constraint_conjunction = constraint_conjunction || ' | ' ||  ? " +
                             " where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setString(1, conjunctionString);
         query.setInt(2, structureId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  
  
  public static void updateStructureOtherPrepositionById(Connection conn, Integer structureId, String prepositionString)
  { // Actualiza el campo BIOLOGICAL_ENTITY.CONSTRAINT_PREPOSITION with prepositionString.  If the field is not empty,
	// the method keep the previous information. 
     try
     {   
  	          	 
  	     String addRecord = "update text.biological_entity set other_constraint = other_constraint || ' | ' ||  ? " +
                             " where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         if (prepositionString != null) query.setString(1, prepositionString);
         else query.setString(1, "");
         query.setInt(2, structureId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  public static void updateStructureVerbById(Connection conn, Integer structureId, String verbString, String verb, String otherConstraint)
  { // Actualiza el campo BIOLOGICAL_ENTITY.verb with verbString.  If the field is not empty,
	// the method keep the previous information. 
     try
     {   
		 // Verify if otherConstraint must be apply.  Just if INDICATOR.conjunction_indicator = 0
		 if ((otherConstraint != null) && (!otherConstraint.equals("")) && (conjunctionIndicatorIsCero(conn))) {
		   setConjunctionIndicator(conn, 1);		  
		 } else otherConstraint = "";
    	 
  	          	 
  	     String addRecord = "update text.biological_entity set verb_string = verb_string || ' | ' ||  ? , "
  	     		+ " verb = verb || ' | ' ||  ? ,  constraint_conjunction = constraint_conjunction || '|' || ? " +
                " where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setString(1, verbString);
         query.setString(2,verb);
         query.setString(3, otherConstraint);
         query.setInt(4, structureId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  }  
  
  public static void updateCharacterVerbById(Connection conn, Integer structureId, String verbString, String verb, String otherConstraint)
  { /* Actualiza el campo CHARACTER.verb with verbString.  If the field is not empty,
	* the method keep the previous information. 
	 *  Date:  22 de octubre de 2015
	 */ 
     try
     {   
		 // Verify if otherConstraint must be apply.  Just if INDICATOR.conjunction_indicator = 0
		 if ((otherConstraint != null) && (!otherConstraint.equals("")) && (conjunctionIndicatorIsCero(conn))) {
		     setConjunctionIndicator(conn, 1);		  
		 } else otherConstraint = "";
    	 
  	          	 
  	     String addRecord = "update text.character set verb_string = verb_string || ' | ' ||  ? , "
  	     		+ " verb = verb || ' | ' ||  ? ,  constraint_conjunction = constraint_conjunction || '|' || ? " +
                " where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setString(1, verbString);
         query.setString(2,verb);
         query.setString(3, otherConstraint);
         query.setInt(4, structureId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  }  
 
  
  public static void updateStructureRelationById(Connection conn, Integer structureId, String prepositionString)
  { // Actualiza el campo BIOLOGICAL_ENTITY.relation with prepositionString.  If the field is not empty,
	// the method keep the previous information. 
     try
     {   
   	   	     	 
  	     String addRecord = "update text.biological_entity set relation = relation || ' | ' ||  ? " +
                             " where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setString(1, prepositionString);
         query.setInt(2, structureId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  }
  
  
  
  public static void updateStructureOtherConstraintById(Connection conn, Integer structureId, String prepositionString)
  { // Actualiza el campo BIOLOGICAL_ENTITY.CONSTRAINT_PREPOSITION with prepositionString.  If the field is not empty,
	// the method keep the previous information. 
     try
     {   
  	          	 
  	     String addRecord = "update text.biological_entity set other_constraint = other_constraint || ' | ' ||  ? " +
                             " where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         if (prepositionString != null) query.setString(1, prepositionString);
         else query.setString(1, "");
         query.setInt(2, structureId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  
  
  public static void updateCharacterConstraintPrepositionById(Connection conn, Integer characterId, String prepositionString, String tokenConstraint)
  { // Actualiza el campo CHARACTER.CONSTRAINT_PREPOSITION with prepositionString.  If the field is not empty,
	// the method keep the previous information. 
     try
     {   
  	          	 
  	     String addRecord = "update text.character set constraint_preposition = constraint_preposition || ' | ' ||  ?,  " +
                             " vconstraint = vconstraint || ' | ' || ?  where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         if (prepositionString != null) query.setString(1, prepositionString);
         else  query.setString(1, "");
         if (tokenConstraint != null) query.setString(2, tokenConstraint);
         else  query.setString(2, "");
  
         query.setInt(3, characterId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  public static void updateCharacterConstraintConjunctionById(Connection conn, Integer characterId, String conjunctionString)
  { // Actualiza el campo CHARACTER.CONSTRAINT_CONJUNCTION with conjunctionString.  If the field is not empty,
	// the method keep the previous information. 
     try
     {   
  	          	 
  	     String addRecord = "update text.character set constraint_conjunction = constraint_conjunction || ' | ' ||  ? " +
                             " where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setString(1, conjunctionString);
         query.setInt(2, characterId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  
  
  public static void updateCharacterOtherConstraintById(Connection conn, Integer characterId, String prepositionString)
  { // Actualiza el campo CHARACTER.CONSTRAINT_PREPOSITION with prepositionString.  If the field is not empty,
	// the method keep the previous information. 
     try
     {   
  	          	 
  	     String addRecord = "update text.character set other_constraint = other_constraint || ' | ' ||  ? " +
                             " where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         if (prepositionString != null) query.setString(1, prepositionString);
         else query.setString(1, "");
         query.setInt(2, characterId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  }  
  
  
  
  
  
  
  
  public static void updateStructureConstraintWithStringById (Connection conn, String p_constraint,  Integer p_id)
  {
     try
     {   
  	   
         /* Statement prepare*/
           	 
  	     String addRecord = "update text.biological_entity set vconstraint = vconstraint || ' | ' ||  ? " +
                             " where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setString(1, p_constraint );       
         query.setInt(2, p_id);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  
  public static void updateStructureGenderNumberById (Connection conn, String p_gender, String p_number,  Integer p_id)
  {
     try
     {   
  	   
         /* Statement prepare*/
           	 
  	     String addRecord = "update text.biological_entity set  "
  	     		            + " gender = ? , number = ?"+
                             " where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setString(1, p_gender);
         query.setString(2, p_number);         
         query.setInt(3, p_id);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  
  
  
  public static void updateStructureModifierById (Connection conn, TokenParser p_currentToken,  Integer p_structureId, String otherConstraint)
  {
     try
     {  
         
		 // Verify if otherConstraint must be apply.  Just if INDICATOR.conjunction_indicator = 0
		 if ((otherConstraint != null) && (!otherConstraint.equals("")) && (conjunctionIndicatorIsCero(conn))) {
		     setConjunctionIndicator(conn, 1);		  
		  } else otherConstraint = "";
           	 
  	     String addRecord = "update text.biological_entity set vconstraint = trim (vconstraint || ' | ' || ? ),  " +
                             " constraint_conjunction = constraint_conjunction || '|' || ?  where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setString(1, p_currentToken.getWordForm().trim() );   
         query.setString(2, otherConstraint);
         query.setInt(3, p_structureId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  }  
  
  
  public static void updateStructureInBrackets (Connection conn, TokenParser p_previousToken)
  {
     try
     {  
         /* Statement prepare*/
         
    	 Integer p_structureId = TextDatabase.selectLastStructure(conn, p_previousToken);
    	 
  	     String addRecord = "update text.biological_entity set in_brackets = 1 "  +
                             " where id =  ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setInt(1, p_structureId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  }  
   
  public static ResultSet selectCharacter(Connection conn,  Integer p_taxon_description_id, Integer structureId )
 	// Return all characters associated with p_taxon_description_id. 	  
 		  
     { 
 	   ResultSet rs = null;
        try
        {   
     	   PreparedStatement st ;
     	   
     	   st = conn.prepareStatement("SELECT * FROM TEXT.CHARACTER WHERE taxon_description_id = ? and biological_entity_id = ? " + 
                                       " order by taxon_description_id, line_number, id");
     	   
     	   st.setInt(1,p_taxon_description_id);
     	   st.setInt(2,structureId);
           rs = st.executeQuery();
     	  
         }
         catch(SQLException e)
         {
             e.printStackTrace();
         }
        return (rs);
     } 
  
  public static Integer selectPreviousCharacter (Connection conn, TokenParser p_token){
	   
	    /* - Description: return the last character_id with token_tree_id = p.token.getTokenTreeId().  
	     *    
	     * - Revision History:
	     *     23/06/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     A character_id that fulfill the conditions. 
		 */
			 
		{   
		    ResultSet rs = null;
		    Integer v_Id = null;
		 
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select id from text.character  " +
	                                       " where token_tree_id = ? order by id;" );
		        
		        st.setInt(1, p_token.getTokenTreeId());

			 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
             if (rs.next())   { 
          	   v_Id = rs.getInt(1);
		       }  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
			 return (v_Id) ;
		}
}
  
  public static TokenParser selectPreviousCharacterWithSameChunkKey (Connection conn, TokenParser p_token){
	   
	    /* - Description: return the last character_id that has the same key (taxon_description_id, line_number, sequence) of p_token or null.  
	     *    
	     * - Revision History:
	     *     23/06/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     A character_id that fulfill the conditions.  The character_Id corresponds to the text.character key.
		 */
			 
		{   
		    ResultSet rs = null;
		    TokenParser tokenResult  = null;
		 
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select id, taxon_description_id, line_number, sequence, name, value, token_tree_id from text.character  " +
	                                       " where taxon_description_id = ? and line_number = ? and sequence = ? " +
		        		                   " order by id desc;" );
		        
		        st.setInt(1, p_token.getTaxonDescriptionId());
		        st.setInt(2, p_token.getLineNumber());
		        st.setInt(3, p_token.getSequence());

			 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
           if (rs.next())   { 
        	   tokenResult = new TokenParser(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(6), null,  null, 
	        		    null, null, rs.getInt(4), null, null, rs.getString(5), null, rs.getInt(7));
        	   
        	   }  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
			 return (tokenResult) ;
		}
}
  
  
  public static Integer selectPreviousCharacterIdWithSameChunkKey (Connection conn, TokenParser p_token){
	   
	    /* - Description: return the last character_id that has the same key (taxon_description_id, line_number, sequence) of p_token or null.  
	     *    
	     * - Revision History:
	     *     23/06/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     A character_id that fulfill the conditions.  The character_Id corresponds to the text.character key.
		 */
			 
		{   
		    ResultSet rs = null;
		    Integer characterId  = null;
		 
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select max(id) " +
		                                   " from text.character " +
	                                       " where taxon_description_id = ? and line_number = ? and sequence = ? " );
		        
		        st.setInt(1, p_token.getTaxonDescriptionId());
		        st.setInt(2, p_token.getLineNumber());
		        st.setInt(3, p_token.getSequence());

			 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
         if (rs.next())   { 
      	   characterId = rs.getInt(1);
      	   
      	   }  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
			 return (characterId) ;
		}
}
  
  
  
  

  public static Timestamp selectPreviousCharacterTimestampWithSameChunkKey (Connection conn, TokenParser p_token){
	   
	    /* - Description: return the last character's last_modification_datetime that has the same key (taxon_description_id, line_number, sequence) 
	     *                of p_token or null.  
	     *    
	     * - Revision History:
	     *     31/08/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     last_modification_datetime that fulfill the conditions or null.
		 */
			 
		{   
		    Timestamp lastModificationDatetime = null;
		    ResultSet rs = null;
		 
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select last_modification_datetime from text.character " +
	                                       " where taxon_description_id = ? and line_number = ? and sequence = ? " +
		        		                   " order by 1 desc;" );
		        
		        st.setInt(1, p_token.getTaxonDescriptionId());
		        st.setInt(2, p_token.getLineNumber());
		        st.setInt(3, p_token.getSequence());

			 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
         if (rs.next())   { 
      	   lastModificationDatetime = rs.getTimestamp(1);
      	   
      	   }  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
			 return (lastModificationDatetime) ;
		}
}  
  
  public static Timestamp selectPreviousStructureTimestampWithSameChunkKey (Connection conn, TokenParser p_token){
	   
	    /* - Description: return the last characstructure's last_modification_datetime that has the same key (taxon_description_id, line_number, sequence) 
	     *                of p_token or it return null.  
	     *    
	     * - Revision History:
	     *     31/08/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     last_modification_datetime that fulfill the conditions or null.
		 */
			 
		{   
		    Timestamp lastModificationDatetime = null;
		    ResultSet rs = null;
		 
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select last_modification_datetime from text.biological_entity " +
	                                       " where taxon_description_id = ? and line_number = ? and sequence = ? " +
		        		                   " order by 1 desc;" );
		        
		        st.setInt(1, p_token.getTaxonDescriptionId());
		        st.setInt(2, p_token.getLineNumber());
		        st.setInt(3, p_token.getSequence());

			 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
       if (rs.next())   { 
    	   lastModificationDatetime = rs.getTimestamp(1);
    	   
    	   }  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
			 return (lastModificationDatetime) ;
		}
}    

  public static TokenParser selectLastStructureByGenderandNumber (Connection conn, TokenParser p_token, String p_gender, String p_number){
	   
	    /* - Description: return the last structure_id in the same clause  (that has the same taxon_description_id and line_number) of p_token and the one that 
	     *                has gender and number equals to p_gender and p_number. 
	     *                If there is not structure with these characteristics return the first one in the clause and if there is noone return  struture #1. 
	     *                OJOJOJOJOJOJOJOJOJO No FUE PROBADA y NO ESTA EN USO.  OJOJOJOJOJOJOJOJOJOJOJOJOJOJOJo
	     * - Revision History:
	     *     20/08/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     A structure_id that fulfill the conditions. 
		 */
			 
		{   
		    ResultSet rs = null;
		    Integer v_Id = null, v_lastId = null;
		    Boolean v_foundIt = false;
		    String v_entityGender, v_entityNumber, v_tokenGender, v_tokenNumber;
		    TokenParser tokenResult = null, lastToken = null;
			    
		    v_tokenGender = p_gender;
		    v_tokenNumber =p_number;
			   
		    // For tokens without gender or number (i.e numbers).
	        if (v_tokenGender == null || v_tokenGender.equals("")) v_tokenGender = "N";
	    	if (v_tokenNumber == null || v_tokenNumber.equals("")) v_tokenNumber = "N";
	    	   
	    	   
			try
			   {   
			   	PreparedStatement st ;
			   	
			   	//Le quite:  
			 
		        st = conn.prepareStatement(" select id, taxon_description_id, line_number, sequence, name, biological_entity_type_id, " + 
		                                           " in_brackets, alter_name, name_original, ontologyid, provenance, pos, gender, number, knowledgeType " +
                                       " from text.biological_entity  " +
	                                       " where taxon_description_id = ? and line_number = ?  " +
	                                       " order by id desc;" );
		        
		        st.setInt(1, p_token.getTaxonDescriptionId() );
		    	st.setInt(2, p_token.getLineNumber() );

			 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
		   	    while ((!v_foundIt) && (rs.next()) )
		        {  /* Fetch next clause record */ 
		   	    	   v_entityGender = rs.getString(6);
		   	    	   v_entityNumber = rs.getString(7);
		   	    	   
		   	    	   // For tokens without gender or number (i.e numbers).
		   	    	   if (v_entityGender == null) v_entityGender = "N";
		   	    	   if (v_entityNumber == null) v_entityNumber = "N";
		   	    	   
		     	       if  ( (v_tokenGender.equals(v_entityGender ) || v_entityGender.equals("N") || v_entityGender.equals("C") || 
		     	    		   v_tokenGender.equals("C") || v_tokenGender.equals("N") ) 
		     	    		   && (v_tokenNumber.equals(v_entityNumber) || v_entityNumber.equals("N") || v_tokenNumber.equals("N")) )
		     	    		    {
		     	    	 v_foundIt = true;
		     	    	 v_Id = rs.getInt(1); 
		     	    	 
		     	    	 // Generates the new token parser 
		     	    	 // Paramenters: TokenParser (p_token_id, p_taxon_descrition_id,  p_line_number,
		     			//           p_form,  p_lemma, p_tag, p_tag1, p_tag2, p_sequence,  p_book_id,
		    			//            p_knowledgeType,  p_ontologyCategory, p_ontologyId, p_tokenTreeId)
		     	    	 tokenResult = new TokenParser(rs.getInt(1),   // id
		     	    			             rs.getInt(2),     // taxon_description_id
		     	    			             rs.getInt(3),     // line_number
		     	    			             rs.getString(9),  // original_name or Word form
		     	    			             rs.getString(5),  // lemma 
		     	    			             rs.getString(12), // POS
		 	        		                 null,             // tag1
		 	        		                 null,             // tag2
		 	        		                 rs.getInt(4),     // sequence or chunk number 
		 	        		                 null,             // book_id
		 	        		                rs.getString(15),  // knowledgeType
		 	        		                 "structure",      // OntologyCategory (estrutura)
		 	        		                rs.getString(10),  // ontologyId 
		 	        		                null );            // token_tree_id
		     	       } 
		     	       v_lastId = rs.getInt(1);
		     	       
		     	       lastToken =  new TokenParser(rs.getInt(1),   // id
	    			             rs.getInt(2),     // taxon_description_id
	    			             rs.getInt(3),     // line_number
	    			             rs.getString(9),  // original_name or Word form
	    			             rs.getString(5),  // lemma 
	    			             rs.getString(12), // POS
       		                 null,             // tag1
       		                 null,             // tag2
       		                 rs.getInt(4),     // sequence or chunk number 
       		                 null,             // book_id
       		                rs.getString(15),  // knowledgeType
       		                 "structure",      // OntologyCategory (estrutura)
       		                rs.getString(10),  // ontologyId 
       		                null );            // token_tree_id
		     	       
		     	    }  
			   	
			   	  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }		
			 // If there are not  structures in a clause then assing 1 to structure id. 
			 if (v_Id == null && v_lastId == null){
			    	tokenResult = new TokenParser(1,   // id
    			             null,     // taxon_description_id
    			             null,     // line_number
    			             "entidad temporal",  // original_name or Word form
    			             null,  // lemma 
    			             null, // POS
    		                 null,             // tag1
    		                 null,             // tag2
    		                 null,     // sequence or chunk number 
    		                 null,             // book_id
    		                 null,  // knowledgeType
    		                 "structure",      // OntologyCategory (estrutura)
    		                 null,  // ontologyId 
    		                null );            // token_tree_id
			    	
			    }
			 else // Assing the last estructure Id (thus: the structure that is at the beginning of the clause). 
				 if (!(v_lastId == null)) 
					 v_Id = v_lastId;
			         tokenResult = lastToken;
			
			 return (tokenResult) ;
		}
		
}  
  
  
  
  public static Boolean characterWasIncluded (Connection conn, TokenParser p_token){
	   
	    /* - Description: return true if p_token exist in text.Character.
	     *  
	     * - Revision History:
	     *     28/07/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     True if p_token exist in text.Character. 
		 */
			 
		{   
		    ResultSet rs = null;
		    Boolean v_success = false;
		 
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select id from text.character  " +
	                                       " where token_tree_id = ? ;" );
		        
		        st.setInt(1, p_token.getTokenTreeId() );
		 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
           if (rs.next())   { 
        	  v_success = true;
		       }  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
			 return (v_success) ;
		}
}
  
  
 public static void updateLastTokenWithLastConstraint(Connection conn, TokenParser p_previousToken, String tokenConstraint){
	 // Hay casos en los que hay un constraint al final de un chunk que no se procesa porque por ejemplo hijo de una estructura 
	 //  Ejemplo (12,3,1) – EAF: típulas ausentes.  En este caso ausentes es un adjetivo pero el ontologyCategory es "quantity"
	 //  entonces el token funciona como constraint de adjetivo pero en este chunk no hay adjetivos hay que aplicarlo a la estructura.
	 
	   Integer v_characterId;
	   
	   TokenParser tr = TextDatabase.selectPreviousCharacterWithSameChunkKey(conn, p_previousToken);
	   
	   if (tr != null){ 
           v_characterId = tr.getTokenId();
	       if ((v_characterId !=null) && (v_characterId != 0) ){
		       TextDatabase.updateCharacterConstraintWithString(conn, tokenConstraint, v_characterId);
		    
	       } else {
			   Integer v_structureId = TextDatabase.selectPreviousStructureNoMatterGenusAndNumber(conn, p_previousToken);
			   if (v_structureId != null)
		          TextDatabase.updateStructureConstraintWithStringById(conn, tokenConstraint, v_structureId);
	   }	    
	   }
	 
 }
  
  public static void updateCharacterInBrackets (Connection conn, TokenParser p_previousToken)
  {
     try
     {   
    	 Integer v_characterId = selectPreviousCharacter(conn, p_previousToken);
    	 
         /* Statement prepare*/
         
    	 if (v_characterId != null) {
    	 
  	        String addRecord = "update text.character set in_brackets = 1 " +
                             " where id = ?;";
  	   
            PreparedStatement query = conn.prepareStatement(addRecord);
        
            query.setInt(1, v_characterId);
   
            query.executeUpdate();
         
            close(query);
    	 }  
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  

  
   public static void updateCharacterValue (Connection conn, TokenParser p_currentToken, Integer p_characterId, String otherConstraint)
  {  //  Actualiza el valor del character concatenando el valor que viene para el caso de m'as de un adjetivo y 
	 //  ademas asigna al token_tree_id el valor del ultimo adjetivo.
     try
     {   
  	     	 
         /* Statement prepare*/
    	 
     	  // Verify if otherConstraint must be apply.  Just if INDICATOR.conjunction_indicator = 0
   	    if ((otherConstraint != null) && (!otherConstraint.equals("")) && (conjunctionIndicatorIsCero(conn))) {
   		   setConjunctionIndicator(conn, 1);		  
   	    } else otherConstraint = "";
    	    	 
  	    String addRecord = "update text.character set value = value || ' ' || ? , "
  	     		+ "  token_tree_id = ?, constraint_conjunction = constraint_conjunction || ' | ' || ?  " +
                  " where id = ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setString(1, p_currentToken.wordForm);
         query.setInt(2, p_currentToken.getTokenTreeId());
         query.setString(3, otherConstraint);
         query.setInt(4, p_characterId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  
  
  public static void updateCharacterConstraint (Connection conn, TokenParser p_currentToken, Integer p_characterId)
  {
     try
     {   	     	 
         /* Statement prepare*/
           	 
/*  	     String addRecord = "update text.character set vconstraint = trim( vconstraint || ', ' || ?) " +
                             " where id = ?;";
 */ 
 	     String addRecord = "update text.character set vconstraint = trim( vconstraint || '| ' || ?) " +
                 " where id = ?;";

  	     
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setString(1, p_currentToken.wordForm);
         query.setInt(2, p_characterId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  public static void updateCharacterConstraintWithString (Connection conn, String p_constraint, Integer p_characterId)
  {
     try
     {   	     	 
  
    	 String addRecord = "update text.character set vconstraint = trim( vconstraint || '| ' || ?) " +
                 " where id = ?;";

  	     
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setString(1, p_constraint);
         query.setInt(2, p_characterId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  
  public static void updateCharacterConstraintBefore (Connection conn, TokenParser p_currentToken, Integer p_characterId)
  {
     try
     {   	     	 
         /* Statement prepare*/
           	 
  	     String addRecord = "update text.character set vconstraint = trim( ? || ' ' || vconstraint) " +
                             " where id = ?;";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
        
         query.setString(1, p_currentToken.wordForm);
         query.setInt(2, p_characterId);
   
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
  
  public static Boolean isRange(Connection conn, String p_measureUnit){
	   
	    /* - Description: return true if p_measureUnit is a continous unit of measure (i.e.cm, mm, etc.  
	     *  
	     * - Revision History:
	     *     11/08/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     True if p_measureUnit is continous. 
		 */
			 
	    ResultSet rs = null;
		Boolean v_success = false;
		 
        try{   
	     	PreparedStatement st ;
			 
            st = conn.prepareStatement(" select use from text.knowledge  " +
	                                       " where phrase = ? ;" );
		        
	        st.setString(1, p_measureUnit );
		 			   	    	         
	      	rs = st.executeQuery();
			  	
			    
            if (rs.next())   {
            	if (rs.getString(1)!= null && rs.getString(1).trim().equals("C"))
      	         v_success = true;
		    }  
		} catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
	     return (v_success) ;
}
  
  public static void updateCharacterAreaLength(Connection conn, TokenParser currentToken){
	    /* - Description: Update previous lenght included in text.character as part of the area being processed (length could include more than one record). 
	     *                All records that will be updated have name = 'width' or name = 'atypical_width'.    
	     *    
	     * - Revision History:
	     *     12/08/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     Text.character.name updated with "length" for all numbers with same key than currentToken.  
		 */
			 
		{   
		    ResultSet rs = null;
		    Integer v_id = null;
		    Boolean v_continue = true;
		    String  v_name;
		    String updateRecord;
		    PreparedStatement query;
    	   	    	   
			try
			   {   
			   	PreparedStatement st ;
			   	
			   	//Le quite:  
			 
		        st = conn.prepareStatement(" select id, taxon_description_id, line_number, sequence, name " +
                                           " from text.character  " +
	                                       " where taxon_description_id = ? and line_number = ? and sequence = ? and (name = 'width' or name = 'atypical_width') " +
	                                       " order by id desc;" );
		        
		        st.setInt(1, currentToken.getTaxonDescriptionId() );
		    	st.setInt(2, currentToken.getLineNumber() );
                st.setInt(3, currentToken.getSequence() );
			 			   	    	         
		      	rs = st.executeQuery();
			  	
			    
		   	    while ((rs.next()) )
		        {  
		   	    	   v_name = rs.getString(5);
		   	    	   v_id = rs.getInt(1);
		   	   		   	    	    
		     	       if  ((v_continue) && (v_name != null) && (v_name.equals("width") || v_name.equals("atypical_width") ) )  {
		     	    	  	     	    	   
		     	    	   if (v_name.equals("width")) {
		     	   	          updateRecord = "update text.character set name = 'length' " +
	                                   " where id = ?;";
		     	    	   } else {
		     	    		  updateRecord = "update text.character set name = 'atypical_length' " +
	                                   " where id = ?;"; 
		     	    	   }
	  	   
	                       query = conn.prepareStatement(updateRecord);
	              	       query.setInt(1, v_id);
	   
	                       query.executeUpdate();
	         
	                       close(query);
		     	    	   
		     	       }  else if ( (v_name != null) && !(v_name.equals("width") || v_name.equals("atypical_width") ) ) {v_continue = false; } 
		     	}  
			   	
			   	  
			} catch(SQLException e) {
			        e.printStackTrace();
			}
		}
  }
  
  
  public static void insertNumber(Connection conn, TokenParser p_tokenNumber, Integer p_structureId, String p_measureUnit, Boolean isArea, 
		  String vName, String tokenConstraint, String otherConstraint){
	   
	  /*  Inserta numeros.
	   * 	*  1/10/2015 Verifica antes el indicador TEXT.INDICATOR.USE_PREVIOUS_ASSIGNED_STRUCTURE if este indicador es 1 implcia que se debe usar como 
	*  estructura la que fua asignada al caracter previo.  Este cambio se introdujo con el manejo de conjunciones. 

	 *		    // Verify if TEXT.INDICATOR.USE_PREVIOUS_ASSINED_STRUCTURE is true/1.
			  p_structureId = setStructureIdAccordingWithUsePreviousAssignedStructure(conn, p_structureId, p_tokenNumber); 
		
	 *
	 */ 
	   
	
	  
	   try
	      { 
		    String v_charType = null;
		    String v_name = "size_or_quantity";
		    String[] parts; 
		    String v_from = "";
		    String v_to = "";
		    
		    // Verify if TEXT.INDICATOR.USE_PREVIOUS_ASSINED_STRUCTURE is true/1.
			  p_structureId = setStructureIdAccordingWithUsePreviousAssignedStructure(conn, p_structureId, p_tokenNumber); 
		
		    
		    // Verify if otherConstraint must be apply.  Just if INDICATOR.conjunction_indicator = 0
			  if ((otherConstraint != null) && (!otherConstraint.equals("")) && (conjunctionIndicatorIsCero(conn))) {
				  setConjunctionIndicator(conn, 1);		  
			  } else otherConstraint = "";
		    
		    
		    if (vName == null) v_name =  "size_or_quantity"; 
		    else v_name = vName;
		    
		    if (isRange(conn, p_measureUnit)){
		    	v_charType = "range_value";
		    }else  v_charType = "count";
		    
		    if ( (p_tokenNumber.getWordForm().contains("-")) ){
		       
		       parts = p_tokenNumber.getWordForm().split("-");
		       v_from = parts[0];
		       v_to = parts[1];
		    } 
 
		    // check if both part of the string are numbers or there is only one number
		    if (!((isNumeric(v_from)) && isNumeric(v_to) ) ) {
		    	v_from = p_tokenNumber.getWordForm();
		    	v_to = null;
		    }
		   	   
		   	String addRecord = " Insert into  TEXT.character (biological_entity_id , taxon_description_id, line_number, sequence, " +
                   " name,  modifier,  value,  char_type,      vconstraint , constraintid ,geographical_constraint, organ_constraint ," +
                   " other_constraint, parallelism_constraint, taxon_constraint, in_brackets ,     ontologyid, "
                   + " vfrom ,from_inclusive ,from_unit ,vto ,to_inclusive, to_unit ,type ,unit ,upper_restricted, " +
                   " constraint_preposition, constraint_conjunction, token_tree_id, notes, verb, verb_string)" +
                   " values ( ?,?,?,?,   ?,?,?,?,   ?,?,?,?,   ?,?,?,?,    ?,   ?,?,?,?,?,?,?,?,?, ?, ?, ? , ?,?,?);";

		   	PreparedStatement query = conn.prepareStatement(addRecord);

			query.setInt(1 ,p_structureId);
			query.setInt(2, p_tokenNumber.getTaxonDescriptionId()); //
			query.setInt(3, p_tokenNumber.getLineNumber());         // Clause_id 
			query.setInt(4, p_tokenNumber.getSequence() );          // chunk_id
			query.setString(5, v_name);                             //
			query.setString(6, null);                               // modifier
			query.setString(7, p_tokenNumber.getWordForm() );       // value 
			query.setString(8, v_charType );                        //
			if (tokenConstraint != null) query.setString(9, tokenConstraint );                              // vconstraint
			else query.setString(9, "" );   
			query.setString(10, null );                             // constraintid      
			query.setString(11, "" );                             // geographical_constraint
			query.setString(12, "" );                             // organ_constraint
			query.setString(13, "");                              // other_constraint   
			query.setString(14, "" );                             // parallelism_constraint
			query.setString(15, "" );                             // taxon_constraint
			query.setInt(16, 0 );                                   // in_brackets     
			query.setString(17, p_tokenNumber.getOntologyId() );    // ontologyid
			query.setString(18,  v_from);                           // vfrom
			query.setInt(19,  1);                                   // from_inclusive 
			query.setString(20, p_measureUnit);                     // from_unit 
			query.setString(21, v_to);                              // vto 
			query.setInt(22, 1);                                    // to_inclusive 
			query.setString(23, p_measureUnit);                     // to_unit    
			query.setString(24, p_tokenNumber.getKnowledgeType());  // type
			query.setString(25, null);                              // unit
			query.setInt(26, 0);                                    // upper_restricted
			query.setString(27, "");                                    // constraint_preposition
			if (otherConstraint != null) query.setString(28, otherConstraint);  // constraint_conjunction
			 else  query.setString(28, "");      	
			query.setInt(29, p_tokenNumber.getTokenTreeId());
			
            query.setString(30, "");                   //notes
            query.setString(31, "");                   //verb
            query.setString(32, "");                   //verb_string


			query.executeUpdate();

			close(query);
			  
	      }
	      catch(SQLException e)
	      {
	          e.printStackTrace();
	      }
		  
	  }
 
  public static void updateNumber(Connection conn, TokenParser p_tokenNumber, String p_measureUnit, String otherConstraint){
	   // Actualiza el numero 
	   try
	      { 
		    String v_charType = null;
		    String v_name = "size_or_quantity";
		    String[] parts; 
		    String v_from = "";
		    String v_to = "";
		    
		    if ( (p_tokenNumber.getWordForm().contains("-")) ){
		       v_charType = "range_value";
		       parts = p_tokenNumber.getWordForm().split("-");
		       v_from = parts[0];
		       v_to = parts[1];
		    } else  v_charType = "count";

		    // check if both part of the string are numbers or there is only one number
		    if (!((isNumeric(v_from)) && isNumeric(v_to) ) || (v_charType.equals("count"))) {
		    	v_charType = "count";
		    	v_from = p_tokenNumber.getWordForm();
		    	v_to = null;
		    }
		    
	 	     String addRecord = "update text.character set name =  ?, char_type = ? , vfrom = ? , from_inclusive = ? , from_unit = ? , " +
	 	             " vto = ? , to_inclusive = ? , to_unit = ?  " + 
                     " where token_tree_id = ?;";
 
             PreparedStatement query = conn.prepareStatement(addRecord);

             query.setString(1, v_name);
             query.setString(2, v_charType);
             query.setString(3, v_from);
             query.setInt(4, 1);
             query.setString(5, p_measureUnit);
             
             query.setString(6, v_to);
             query.setInt(7, 1);
             query.setString(8, p_measureUnit);
             query.setInt(9, p_tokenNumber.getTokenTreeId());
          
             query.executeUpdate();
 
             close(query);
			  
	      }
	      catch(SQLException e)
	      {
	          e.printStackTrace();
	      }
		  
	  }
  
  
  public static void updateUnitofMeasurewithNumber(Connection conn, TokenParser p_tokenNumber, Integer p_structureId, TokenParser p_previousToken){
	   
	   try
	      { 
		    String v_charType = null;
		    String v_name = "size_or_quantity";
		    String[] parts; 
		    String v_from = "";
		    String v_to = "";
		    
		    if ( (p_tokenNumber.getWordForm().contains("-")) ){
		       v_charType = "range_value";
		       parts = p_tokenNumber.getWordForm().split("-");
		       v_from = parts[0];
		       v_to = parts[1];
		    } else  v_charType = "count";

		    // check if both part of the string are numbers or there is only one number
		    if (!((isNumeric(v_from)) && isNumeric(v_to) ) || (v_charType.equals("count"))) {
		    	v_charType = "count";
		    	v_from = p_tokenNumber.getWordForm();
		    	v_to = null;
		    }
		    
	 	     String addRecord = "update text.character set name =  ?, char_type = ? , vfrom = ? , from_inclusive = ? , " +
	 	             " vto = ? , to_inclusive = ?, value = ?"+ 
                     " where token_tree_id = ?;";

            PreparedStatement query = conn.prepareStatement(addRecord);

            query.setString(1, v_name);
            query.setString(2, v_charType);
            query.setString(3, v_from);
            query.setInt(4, 1); 
            query.setString(5, v_to);
            query.setInt(6, 1);
            query.setString(7, p_tokenNumber.getWordForm());
            query.setInt(8, p_previousToken.getTokenTreeId());
         
            query.executeUpdate();

            close(query);
			  
	      }
	      catch(SQLException e)
	      {
	          e.printStackTrace();
	      }
		  
	  } 
  

  public static void insertUnitofMeasure(Connection conn, Integer p_structureId, TokenParser p_tokenUnit, String p_TokenConstraint){

	  /*   Inserta unidades de medida.
	   * 
	   * 	*  1/10/2015 Verifica antes el indicador TEXT.INDICATOR.USE_PREVIOUS_ASSIGNED_STRUCTURE if este indicador es 1 implcia que se debe usar como 
	*  estructura la que fua asignada al caracter previo.  Este cambio se introdujo con el manejo de conjunciones. 

	 *		    // Verify if TEXT.INDICATOR.USE_PREVIOUS_ASSINED_STRUCTURE is true/1.
			  p_structureId = setStructureIdAccordingWithUsePreviousAssignedStructure(conn, p_structureId, p_tokenNumber); 
		
	 */
	  
	   try
	      { 
		   
		    // Verify if TEXT.INDICATOR.USE_PREVIOUS_ASSINED_STRUCTURE is true/1.
				  p_structureId = setStructureIdAccordingWithUsePreviousAssignedStructure(conn, p_structureId, p_tokenUnit);   
			   	   
		   	String addRecord = " Insert into  TEXT.character (biological_entity_id , taxon_description_id, line_number, sequence, " +
                  " from_unit,  char_type,      vconstraint, token_tree_id , to_unit , constraint_preposition, other_constraint, " + 
		   		  " constraint_conjunction, notes, verb, verb_string  )" +
                  " values ( ?,?,?,?,   ?,?,?,? , ?, ?, ?, ?, ?, ?, ?);";

		   	PreparedStatement query = conn.prepareStatement(addRecord);

			query.setInt(1 ,p_structureId);
			query.setInt(2, p_tokenUnit.getTaxonDescriptionId());
			query.setInt(3, p_tokenUnit.getLineNumber());
			query.setInt(4, p_tokenUnit.getSequence() );
			query.setString(5, p_tokenUnit.getWordForm() );
			query.setString(6, p_tokenUnit.getKnowledgeType() );
			
			if (p_TokenConstraint != null) query.setString(7, p_TokenConstraint );
			else  query.setString(7, "" );
			query.setInt(8, p_tokenUnit.getTokenTreeId());
			query.setString(9,  p_tokenUnit.getWordForm());
            query.setString(10, "");
            query.setString(11, "");
            query.setString(12, "");                                    // constraint_conjunction
            query.setString(13, "");                   //notes
            query.setString(14, "");                   //verb
            query.setString(15, "");                   //verb_string
			
			query.executeUpdate();

			close(query);
			  
	      }
	      catch(SQLException e)
	      {
	          e.printStackTrace();
	      }
		  
	  }
  
  public static Integer getMainStructure (Connection conn, TokenParser currentToken){
	   
	    /* - Description: return the firts structure_id that has the same key (taxon_description_id, line_number, sequence) of p_token. 
	     *  If there are no structure return 1.
	     *    
	     * - Revision History:
	     *     1/10/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     A structure_id that fulfill the conditions. 
		 */
			 
		{   
		    ResultSet rs = null;
		    Integer v_Id = 1;
		 
		    try
			   {   
			   	PreparedStatement st ;
			 
		        st = conn.prepareStatement(" select min(id) from text.biological_entity  " +
	                                       " where taxon_description_id = ? and  line_number = ?  ;" );
		        
		        st.setInt(1, currentToken.getTaxonDescriptionId() );
			  	st.setInt(2, currentToken.getLineNumber() );
		 			   	    	         
		      	rs = st.executeQuery();
		    
           if (rs.next())   { 
        	   v_Id = rs.getInt(1);
		       }  
			}
			
		    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
			 return (v_Id) ;
		}
}
  
  public static void  insertCharacterWithInitialPrepositionString(Connection conn, TokenParser currentToken, String prepositionString)

  /*   OJOJOJOO no sirvio el enforque.
   *  Deprecated. Inserta chunks que inician con una preposicion en la tabla character asociados a la estructura principal.
   * 
 */
  {
   try
      { 
	   
	  // Return the main structure in a clause.
	  Integer  p_structureId = getMainStructure(conn, currentToken);   
		   	   
      String addRecord = " Insert into  TEXT.character (biological_entity_id , taxon_description_id, line_number, sequence, " +
              " name,  modifier,  value,  char_type,      vconstraint , constraintid ,geographical_constraint, organ_constraint ," +
              " other_constraint, parallelism_constraint, taxon_constraint, in_brackets ,     ontologyid, notes, token_tree_id, " + 
              "  constraint_preposition, constraint_conjunction, verb, verb_string)" +
              " values ( ?,?,?,?,   ?,?,?,?,   ?,?,?,?,   ?,?,?,?,    ?,?,?, ?,?, ?,?);";

     PreparedStatement query = conn.prepareStatement(addRecord);

     query.setInt(1 ,p_structureId);                         //biological_entity 
     query.setInt(2, currentToken.getTaxonDescriptionId());  //taxon_description_id
     query.setInt(3, currentToken.getLineNumber());          //line_number
     query.setInt(4, currentToken.getSequence() );           // sequence
     query.setString(5, currentToken.getWordForm() );        //name
     query.setString(6,"");                                  // modifier
     query.setString(7, "" );        //value   
     query.setString(8, currentToken.getKnowledgeType() );   //char_type
     query.setString(9, prepositionString );                 //vConstraint
     query.setString(10, null );                             //constraintId
     query.setString(11, "" );                               //geographical_constraint
     query.setString(12, "" );                               //organ_constraint      
     query.setString(13, "");                                //other_constraint
     query.setString(14, "" );                               //parallelism_constraint
     query.setString(15, "" );                               //taxon_constraint
     query.setInt(16, 0 );                                   //in_brackets
     query.setString(17, currentToken.getOntologyId() );     //ontology_id
     query.setString(18, "preposition");                     //notes
     query.setInt(19, currentToken.getTokenTreeId());        //token_tree_id  
     query.setString(20, "");                                //constraint_preposition
     query.setString(21, "");                                //constraint_conjunction

     query.setString(22, "");                   //verb
     query.setString(23, "");                   //verb_string

     query.executeUpdate();

     close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
	  
  }
  	
  public static void insertNumbertwithParentesis(Connection conn, Boolean isLeft, TokenParser p_tokenNumber, Boolean isArea,
		  String beginOrEndNumber,  String p_measureUnit, Integer p_structureId, String vName, String tokenConstraint, String otherConstraint){
	  /*   Inserta numeros que estan entre parentesis.
	   * 
	   * 	*  1/10/2015 Verifica antes el indicador TEXT.INDICATOR.USE_PREVIOUS_ASSIGNED_STRUCTURE if este indicador es 1 implcia que se debe usar como 
       *  estructura la que fue asignada al caracter previo.  Este cambio se introdujo con el manejo de conjunciones. 

	 *  Parameters: connection, isIzquierdo (si el rango tipico esta a la izquierda), tokenNumber, isArea, range beginning or end, unit of measure. 
	 *  Dependiendo de si el rango tipico est'a a la derecha o izquierda inserta en rango atipico.   

*/		   
		   try
		      { 
			    String v_charType ;
			    String v_name = vName;
			    String v_from = "";
			    String v_to = "";
			    Integer fromInclusive = 0, toInclusive = 0;
				    	    
			    
			    // Verify if TEXT.INDICATOR.USE_PREVIOUS_ASSINED_STRUCTURE is true/1.
			    p_structureId = setStructureIdAccordingWithUsePreviousAssignedStructure(conn, p_structureId, p_tokenNumber); 
 
			    if (isArea) {
			    	/* Las areas se definen como alto x ancho.  Por default al crear un registro se asigna el ancho (atypical_with).  
			    	 * Al procesar el token de rango se actualizan los registros anteriores con atypical_length y length 
			    	 */
				    v_charType = "range_value";
			    } else {
				    v_charType = "size_or_quantity";
				    if (v_name==null) v_name = "size_or_quantity";
			    }
			    	
			    // If isLeft (i.e. the habitual nuerical range is at the left side of parentesis)
			    if ( isLeft){
			    	if (beginOrEndNumber != null) v_from = beginOrEndNumber;
			       v_to = p_tokenNumber.getWordForm();
			       toInclusive =1;
			       v_name = "atypical_range";
			       
			    } else {
			    	if (beginOrEndNumber != null) v_to = beginOrEndNumber;
				   v_from = p_tokenNumber.getWordForm();
				   fromInclusive = 1;
				   v_name = "atypical_range";

			    }
			    
			    // Verify if otherConstraint must be apply.  Just if INDICATOR.conjunction_indicator = 0
				  if ((otherConstraint != null) && (!otherConstraint.equals("")) && (conjunctionIndicatorIsCero(conn))) {
					  setConjunctionIndicator(conn, 1);		  
				  } else otherConstraint = "";
	 

			   	String addRecord = " Insert into  TEXT.character (biological_entity_id , taxon_description_id, line_number, sequence, " +
	                   " name,  modifier,  value,  char_type,      vconstraint , constraintid ,geographical_constraint, organ_constraint ," +
	                   " other_constraint, parallelism_constraint, taxon_constraint, in_brackets ,     ontologyid, "
	                   + " vfrom ,from_inclusive ,from_unit ,vto ,to_inclusive, to_unit ,type ,unit ,upper_restricted," +
	                   " constraint_preposition, constraint_conjunction, notes, verb, verb_string  )" +
	                   " values ( ?,?,?,?,   ?,?,?,?,   ?,?,?,?,   ?,?,?,?,    ?,   ?,?,?,?,?,?,?,?,?, ?, ?, ?, ?, ? );";

			   	PreparedStatement query = conn.prepareStatement(addRecord);

				query.setInt(1 ,p_structureId);
				query.setInt(2, p_tokenNumber.getTaxonDescriptionId());
				query.setInt(3, p_tokenNumber.getLineNumber());
				query.setInt(4, p_tokenNumber.getSequence() );    
				query.setString(5, v_name);       // name
				query.setString(6, null);         // modifier   
				query.setString(7, p_tokenNumber.getWordForm() ); // value
				query.setString(8, v_charType ); //char_type
				query.setString(9, tokenConstraint );   // vconstraint
				query.setString(10, null );  // constraintid
				query.setString(11, "" );  // geographical_constraint
				query.setString(12, "" );  // organ_constraint
				query.setString(13, "");   // other_constraint
				query.setString(14, "" );  // parallelism_constraint
				query.setString(15, "" );  // taxon_constraint
				query.setInt(16, 1 );        // in_brackets    
				query.setString(17, p_tokenNumber.getOntologyId() );
				
				query.setString(18,  v_from);       // vFrom
				query.setInt(19,  fromInclusive);   // from_inclusive
				query.setString(20, p_measureUnit); // from_unit
				query.setString(21, v_to);          // Vto 
				query.setInt(22, toInclusive);      // to_inclusive
				query.setString(23, p_measureUnit);
				query.setString(24, p_tokenNumber.getKnowledgeType());
				query.setString(25, null);            //unit   
				query.setInt(26, 0);                  //upper_restricted
				query.setString(27, ""); //constraint_preposition
				if (otherConstraint != null ) query.setString(28, otherConstraint);  // constraint_conjunction
				else query.setString(28, "");  // constraint_conjunction
				
	            query.setString(29, "");                   //notes
	            query.setString(30, "");                   //verb
	            query.setString(31, "");                   //verb_string
				
				query.executeUpdate();

				close(query);
				  
		      }
		      catch(SQLException e)
		      {
		          e.printStackTrace();
		      }
			  
		  }	
  
  public static Integer indexOfWordInChunk (Connection conn, String chunkContents, TokenParser pToken, Boolean isPreviousToken ) {
	/* Retorna la posicione en la hilera a donde se encuentra el pToken.  Hay que tener en cuenta que 
	 * el pToken puede estar repetido.  Si el token corresponde a un token previamente procesado se debe tomar en cuenta que ese 
	 * ya fue incluido en la base de datos.
	 * */  
	 Integer theIndex = chunkContents.indexOf(pToken.getWordForm());
	 Integer counter = 0;
	 Integer dbCounter = 0;
	 Integer counterTemp = 0;
     String[] parts = chunkContents.split(" ");
     String text = "";
	  
     for(String s:parts){
    	if (s.equals(pToken.getWordForm())) {
    		counter ++;
    	}	
     }
     
     if ( counter > 1){
    	 /* Hay que contar cuantas veces aparece el token en la tabla biological_entity o characater segun corresponda. 
    	  *  Esto me va dar cuantos caracteres he procesado y por lo tanto el que corresponde
    	  */
	    if (pToken.isStructure()){
	    	dbCounter = countBiologicalEntityRepetitions (conn, pToken); 
	    } else { 
	    	dbCounter = countCharacterRepetitions ( conn, pToken);
	        if (isPreviousToken) dbCounter --;
	    }
	    
     }
     // Si solo hay una repeticion o hay mas de una pero no se ha procesado ninguna mande theIndex calculado incialmente.
     if (counter > 1 && dbCounter >0  && counter > dbCounter){
         for(String s:parts){ 
        	if (counterTemp <= dbCounter) { 	
                
        	  if (s.equals(pToken.getWordForm())) {
           		counterTemp ++;
        	   }
        		if (counterTemp <= dbCounter)  text = text.trim() + " " + s;    		
            }
         } 	
         
        theIndex = text.length()+1; 
     }
     
	 return (theIndex);
  }
  
  public static Integer countCharacterRepetitions (Connection conn, TokenParser pToken){
	   
	    /* - Description: return the repetitions (in a chunk) of a character in text.character.
	     *    
	     * - Revision History:
	     *     20/10/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *       repetitions. 
		 */
			 
		{ 
		ResultSet rs = null;
	    Integer dbCounter = 0;
		 
	    try
		   {   
		   	PreparedStatement st ;
		 
	        st = conn.prepareStatement(" select count(*) from text.character  " +
                                       " where taxon_description_id = ? and  line_number = ? and sequence = ? and value = ?;" );
	        
	        st.setInt(1, pToken.getTaxonDescriptionId() );
		  	st.setInt(2, pToken.getLineNumber() );
		  	st.setInt(3, pToken.getSequence());
		  	st.setString(4, pToken.getWordForm());
	 			   	    	         
	      	rs = st.executeQuery();
	    
           if (rs.next())   { 
 	          dbCounter = rs.getInt(1);
	       }  
		}
		
	    catch(SQLException e)
		    {
		        e.printStackTrace();
		    }
		
			 return (dbCounter) ;
		}
}
  
  public static Integer countBiologicalEntityRepetitions (Connection conn, TokenParser pToken){
	   
	    /* - Description: return the repetitions (in a chunk) of a character in text.biological_entity.
	     *    
	     * - Revision History:
	     *     20/10/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *       repetitions. 
		 */
			 
		{ 
		ResultSet rs = null;
	    Integer dbCounter = 0;
		 
	    try
		   {   
		   	PreparedStatement st ;
		 
	        st = conn.prepareStatement(" select count(*) from text.biological_entity  " +
                                     " where taxon_description_id = ? and  line_number = ? and sequence = ? and name_original = ?;" );
	        
	        st.setInt(1, pToken.getTaxonDescriptionId() );
		  	st.setInt(2, pToken.getLineNumber() );
		  	st.setInt(3, pToken.getSequence());
		  	st.setString(4, pToken.getWordForm());
	 			   	    	         
	      	rs = st.executeQuery();
	    
         if (rs.next())   { 
	          dbCounter = rs.getInt(1);
	       }  
		}
		
	    catch(SQLException e)
		    {
		        e.printStackTrace();
		    }
		
			 return (dbCounter) ;
		}
}  
  
  
  /* INDICATOR */
	
  public static void  setConjunctionIndicator(Connection conn, Integer indicatorValue){
	  // Si el indicador esta en cero implica que la conjuncion no se ha procesado.  en 1 indica que ya se proceso.  
	     try
	     {   	     	 
	         /* Statement prepare*/
	           	 
	  	     String addRecord = "update text.indicator set conjunction_indicator =  ? ;";
	  	   
	         PreparedStatement query = conn.prepareStatement(addRecord);
	        
	         query.setInt(1, indicatorValue);
	   
	         query.executeUpdate();
	         
	         close(query);
	      }
	      catch(SQLException e)
	      {
	          e.printStackTrace();
	      }  
	  
  }
  
  public static Boolean conjunctionIndicatorIsCero(Connection conn){
	   
	    /* - Description: return true if INDICATOR.CONJUNCTION_INDICATOR is cero.  
	     *  
	     * - Revision History:
	     *     28/09/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *     True if INDICATOR.CONJUNCTION_INDICATOR is cero. 
		 */
			 
	    ResultSet rs = null;
		Boolean isCero = false;
		 
      try{   
	     	PreparedStatement st ;
			 
          st = conn.prepareStatement(" select conjunction_indicator from text.indicator ; " );
		 			   	    	         
	      	rs = st.executeQuery();
			  	
			    
          if (rs.next())   {
          	if (rs.getInt(1)==0)
    	         isCero = true;
		    }  
		} catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
	     return (isCero) ;
}
  
  
  public static void  setUsePreviousAssignedStructureIndicator(Connection conn, Integer indicatorValue){
	  // Si el indicador esta en cero implica que la conjuncion no se ha procesado.  en 1 indica que ya se proceso.  
	     try
	     {   	     	 
	         /* Statement prepare*/
	           	 
	  	     String addRecord = "update text.indicator set use_previous_assigned_structure =  ? ;";
	  	   
	         PreparedStatement query = conn.prepareStatement(addRecord);
	        
	         query.setInt(1, indicatorValue);
	   
	         query.executeUpdate();
	         
	         close(query);
	      }
	      catch(SQLException e)
	      {
	          e.printStackTrace();
	      }  
	  
  }
  
  public static Integer getUsePreviousAssignedStructureIndicator(Connection conn){
	   
	    /* - Description: return the value of INDICATOR.Use_Previous_Assigned_structure.  
	     *  
	     * - Revision History:
	     *     1/10/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *    
		 */
			 
	    ResultSet rs = null;
		Integer theValue = 0;
		 
      try{   
	     	PreparedStatement st ;
			 
          st = conn.prepareStatement(" select use_previous_assigned_structure from text.indicator ; " );
		 			   	    	         
	      	rs = st.executeQuery();
			  	
			    
          if (rs.next())   {
          	
    	     theValue = rs.getInt(1);
          }  
		} catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
	     return (theValue) ;
}
  
  public static void  setAnalyzeTheRestOfTheChunkIndicator(Connection conn, Integer indicatorValue){
	  // Si el indicador esta en cero implica que el sisema no debe entrar al ciclo de procesar el arbol.  El 1 indica que el proceso es normal.  
	     try
	     {   	     	 
	         /* Statement prepare*/
	           	 
	  	     String addRecord = "update text.indicator set analyze_the_rest_of_the_chunk =  ? ;";
	  	   
	         PreparedStatement query = conn.prepareStatement(addRecord);
	        
	         query.setInt(1, indicatorValue);
	   
	         query.executeUpdate();
	         
	         close(query);
	      }
	      catch(SQLException e)
	      {
	          e.printStackTrace();
	      }  
	  
  }
  
  public static Integer getAnalyzeTheRestOfTheChunkIndicator(Connection conn){
	   
	    /* - Description: return the value of INDICATOR.analyze_the_rest_of_the_chunk.  
	     *  
	     * - Revision History:
	     *     9/10/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *   

		 * - Return Values:
		 *    
		 */
			 
	    ResultSet rs = null;
		Integer theValue = 0;
		 
      try{   
	     	PreparedStatement st ;
			 
          st = conn.prepareStatement(" select analyze_the_rest_of_the_chunk from text.indicator ; " );
		 			   	    	         
	      	rs = st.executeQuery();
			  	
			    
          if (rs.next())   {
          	
    	     theValue = rs.getInt(1);
          }  
		} catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		
	     return (theValue) ;
}
  

  
  
  
  /* POST SEMANTIC ANALISYS --------------------------------------------------------------------------------------------------------------*/
  
  public static  void createNewStructureWithRecordsWithInitPreposition (Connection conn,  Integer p_book_id) 
			  
	    /* - Description: For all structures that notes = “init with preposition”. Clone the record and update name = main.
	     *     Update the oringinal record with constraint_preposition = "".
	     *   
	     *    It did not work because the new structure got and id at the end of the description and did not fit with the whole clause.
	     *    
	     * - Revision History:
	     *     2/10/2015 - Maria Aux. Mora
		 *     
	     * - Arguments (input / output):
		 *    conn : database opened connection.
		 *    p_book_id :  Book identifier.

		 * - Return Values:
		 *     A ResultSet with token's records that fulfill the condition. 
		 */
			 
		{   
		    ResultSet rs = null;
		    Integer theNewStructureId;
		    String theOriginalNameofMainStructure;
		    
			try
			   {   
			   	PreparedStatement st ;
			    
			   	// Get all strcutures wiht notes with  'init with preposition'
			    st = conn.prepareStatement("select b.id, b.taxon_description_id, b.line_number from text.biological_entity b, text.taxon_description t " +
                                           " where  b.taxon_description_id= t.id and notes like  '%init with preposition%' and book_id = ? " +
                                           " order by b.taxon_description_id, b.line_number, b.id");
			  	st.setInt(1, p_book_id);
			  	
			   	rs = st.executeQuery();
	
			   	while (rs.next())
		     	   {  /* Fetch next clause record */ 
		       		  
		     		 theNewStructureId = cloneStructureById(conn, rs.getInt(1));
			   		 
			   		 theOriginalNameofMainStructure = selectMainStructureClassName(conn, rs.getInt(2), rs.getInt(3));
			   		 
			   		 updateStructureNameById (conn, theNewStructureId, theOriginalNameofMainStructure);
			   		 
			   		cleanStructureConstraintPrepositionById(conn,  rs.getInt(1));
		   	      
		     	    }  
			   	
			   	  
			    }
			    catch(SQLException e)
			    {
			        e.printStackTrace();
			    }
		} 
  
  
  /* DICTIONARY --------------------------------------------------------------------------------------------------------------- */

  public static ResultSet selectDictionaryByToken (Connection conn, String pToken) 
  
  /* - Description: return records which token are equals to pString.
    *    
    * - Revision History:
    *     19/11/2015 - Maria Aux. Mora
    *     
    * - Arguments (input / output):
    *    conn : database opened connection.
    *    pToken: a token (Spanish).

    * - Return Values:
    *     A ResultSet with token's records that fulfill the condition. 
 	*/

 {   
 	  ResultSet rs = null;
      try
      {   
   	   PreparedStatement st ;

 		   st = conn.prepareStatement("SELECT * FROM text.dictionary WHERE  token = ? order by id ;");
  	       st.setString(1, pToken);
   	    	         
   	   rs = st.executeQuery();
   	  
       }
       catch(SQLException e)
       {
           e.printStackTrace();
       }
      return (rs);
   } 
  
  public static ResultSet selectDictionaryByLemma (Connection conn, String pLemma) 
  
  /* - Description: return records which token are equals to pString.
    *    
    * - Revision History:
    *     19/11/2015 - Maria Aux. Mora
    *     
    * - Arguments (input / output):
    *    conn : database opened connection.
    *    pLemma: a lemma (Spanish).

    * - Return Values:
    *     A ResultSet with token's records that fulfill the condition. 
 	*/

 {   
 	  ResultSet rs = null;
      try
      {   
   	   PreparedStatement st ;

 		   st = conn.prepareStatement("SELECT * FROM text.dictionary WHERE  lemma = ? order by id ;");
  	       st.setString(1, pLemma);
   	    	         
   	   rs = st.executeQuery();
   	  
       }
       catch(SQLException e)
       {
           e.printStackTrace();
       }
      return (rs);
   } 
  
  
  
 public static ResultSet selectTempDictionary(Connection conn) 
  
  /* - Description: return records which token are equals to pString.
    *    
    * - Revision History:
    *     19/11/2015 - Maria Aux. Mora
    *     
    * - Arguments (input / output):
    *    conn : database opened connection.
    *    pString: a token (Spanish).

    * - Return Values:
    *     A ResultSet with token's records that fulfill the condition. 
 	*/

 {   
 	  ResultSet rs = null;
      try
      {   
   	   PreparedStatement st ;

 		   st = conn.prepareStatement("SELECT string FROM text.raw_dictionary order by string;");
   	    	         
   	   rs = st.executeQuery();
   	  
       }
       catch(SQLException e)
       {
           e.printStackTrace();
       }
      return (rs);
   } 
  
  
 
  public static void insertDictionary(Connection conn, String pToken,  String pLemma, String pEnglishLemma)
    {
       try
       {   
    	   // Insert only if the token was not included before.
    	   
     	   ResultSet rs = TextDatabase.selectDictionaryByToken (conn, pToken);
	    
   		   if (!rs.next()) {
    	   
	    	   String addRecord = "INSERT INTO text.DICTIONARY ( token, lemma, english_lemma, revision_level) " +
	                               "VALUES (?,?,?, ?);";
	    	     
	    	   PreparedStatement query = conn.prepareStatement(addRecord);     	      
	    	           
	           query.setString(1, pToken);
	           query.setString(2 , pLemma);
	           query.setString(3 , pEnglishLemma);
	           query.setInt(4, 0);
	           
	           
	           query.executeUpdate();
	           close(query);
   		   }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
       }
    } 
  
  public static void updateDictionaryRevisonLevelFromOTO(Connection conn)
  {/* - Description: Update DICTIONARY.revison_level using OTO.  If token exist in OTO then revision_level =2 
   	*                
  	* - Revision History:
  	*    20/11/2015 - Maria Aux. Mora
  	*     
  	* - Arguments (input / output):
  	*    conn               : database opened connection.
  	*    
  	* - Return Values:
  	*     Dictionary updated.  
  	*/
     try
     {   
  	   
         /* Statement prepare*/
  	   String addRecord = "UPDATE text.dictionary t SET revision_level = 2 "+ 
  			              " where english_lemma in " +
                          " (select distinct (t.english_lemma) " + 
                          "  from text.dictionary t left join text.oto o on trim(t.english_lemma) = trim(o.term) " +
                          "  where o.category is not null and (revision_level = 0 or revision_level is null)); ";
  	   
         PreparedStatement query = conn.prepareStatement(addRecord);
                 
         query.executeUpdate();
         
         close(query);
      }
      catch(SQLException e)
      {
          e.printStackTrace();
      }
  } 
 
  /* RELATION ----------------------------------------------------------------------------------------------------------------- */
  public static void insertRelationWithNextStructure(Connection conn, TokenParser currentToken,
		  Integer fromBiologicalEntityId)
  {
     try
     {   
  	   // Insert only if the token was not included before.
  	   
   	   Integer toBiologicalEntityId = TextDatabase.selectMaxBiologicalEntityId (conn);
	   
   	   Integer vNegation = 0;
   	   
   	   if (currentToken.getWordForm().equals("sin")){
   		   vNegation = 1;
   	   }
   	   
   	   
 	   if (fromBiologicalEntityId != null && toBiologicalEntityId != null) {
  	   
	      String addRecord = "INSERT INTO text.RELATION ( taxon_description_id, line_number, name, vfrom, vto, negation) " +
	                               "VALUES (?,?,?, ?,?,?);";
	    	     
	      PreparedStatement query = conn.prepareStatement(addRecord);     	      
	    	           
	      query.setInt(1 , currentToken.getTaxonDescriptionId());
	      query.setInt(2 , currentToken.getLineNumber());
          query.setString(3, currentToken.getWordForm());
	      query.setInt(4 , fromBiologicalEntityId);
	      query.setInt(5 , toBiologicalEntityId+1);   //Next structure procesed after the preposition.
	      query.setInt(6, vNegation);
	            
	      query.executeUpdate();
	      close(query);
         }
      }
      catch(SQLException e)
      {
          e.printStackTrace();
     }
  } 
  
  public static void deleteRelations(Connection conn, Integer pBookId)
  { //Delete text.relation of a book.  
	  	  
    Statement stmt = null;
	   
	try
	{      	     
	 /* Statement prepare*/
		 	  
	  stmt = conn.createStatement();
	  String sql = "delete from text.relation where taxon_description_id in (select id from text.taxon_description t where book_id = " +
	               pBookId.toString() + ");";
      stmt.executeUpdate(sql);
   }
   catch(SQLException e)
   {
       e.printStackTrace();
   }
	  
  }
  public static ResultSet SelectRelationByTaxonDescritionIdLineNumber (Connection conn, Integer p_taxonDescriptionid, 
		                          Integer p_lineNumber) 
  
  /* - Description: select all relations for a clause 
    *    
    * - Revision History:
    *     12/02/2016 - Maria Aux. Mora
    *     
    * - Arguments (input / output):
    *    conn : database opened connection.
    *    p_taxonDescriptionid: description id.
    *    p_lineNumber : clause id.

    * - Return Values:
    *     A ResultSet with relations records that fulfill the condition. 
	*/
 
 {   
	  ResultSet rs = null;
      try
      {   
   	   PreparedStatement st ;
 
 		   st = conn.prepareStatement("SELECT * FROM text.relation WHERE  taxon_description_id = ? and line_number = ?;");
  	       st.setInt(1, p_taxonDescriptionid);
  	       st.setInt(2, p_lineNumber);
   	    	         
   	   rs = st.executeQuery();
   	   	  
       }
       catch(SQLException e)
       {
           e.printStackTrace();
       }
      return (rs);
   } 
  
  
  
  /* OTHER METHODS ------------------------------------------------------------------------------------------------------------ */	
  
  
  /* Note (good practices): An application calls the method Statement.close to indicate that it has finished processing a statement.
   * All Statement objects will be closed when the connection that created them is closed. However, it is good 
   * coding practice for applications to close statements as soon as they have finished processing them. 
   * This allows any external resources that the statement is using to be released immediately.*/
  
  public static boolean isNumeric(String s) {  
        if ( (s.matches("[-+]?\\d*\\.?\\d+")) || ( s.matches("[-+]?\\d*\\,?\\d+")) )  
           return(true);
        else return(false);
	
} 
  
  
  public static void close(Statement s)
      {
          try
          {
              if (s != null)
              {
                  s.close();
              }
          }
          catch (SQLException e)
          {
              // log or report in someway
              e.printStackTrace();
          }
      }
  
  public static void close(ResultSet s)
  {
      try
      {
          if (s != null)
          {
              s.close();
          }
      }
      catch (SQLException e)
      {
          // log or report in someway
          e.printStackTrace();
      }
  }
 
  
  public static void close(Connection s)
  {
      try
      {
          if (s != null)
          {
              s.close();
          }
      }
      catch (SQLException e)
      {
          // log or report in someway
          e.printStackTrace();
      }
  }
 
}


