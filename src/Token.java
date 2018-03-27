
public class Token {
    /* Class to manage Tokens */
	final Integer tokenId;      // Database sequence automatic assigned
	final Integer TaxonDescriptionId;  // Token's Taxon Description Record ID 
	final Integer lineNumber;  // Line number associated to Taxon Description Record ID
	final String  wordForm;  // Word as it appear in the document
	final String  wordLemma; // Lemma of wordForm
	final String  wordTag;   // Tag assigned to wordForm
	final String  wordRole;  // Role n/m/b
	final Integer sequence;   // Sequence inside current line (first position =1)
	final Integer book_id;   // book identifier

	public Token (Integer p_token_id, Integer p_taxon_descrition_id, Integer  p_line_number, String p_form, 
			   String p_lemma, String p_tag, String p_role, Integer p_seq, Integer p_book_id) {
		tokenId = p_token_id;
		TaxonDescriptionId = p_taxon_descrition_id;
		lineNumber = p_line_number;
		wordForm = p_form;
		wordLemma = p_lemma;
		wordTag = p_tag;
		wordRole = p_role;
		sequence = p_seq;
		book_id = p_book_id;
	}
	
}
