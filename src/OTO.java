import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
*/

import org.apache.http.HttpEntity;
import org.apache.http.util.*;
import org.apache.http.impl.client.*;
import org.apache.http.client.methods.*;

public class OTO {

	 public static void main(String[] args) throws IOException, Exception {
	/*	 HttpClient client = new DefaultHttpClient();
		 HttpGet request = new HttpGet("http://biosemantics.arizona.edu/OTO/rest/termCategories/Plant/straight");
		 HttpResponse response = client.execute(request);
		 BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
		 String line = "";
		 while ((line = rd.readLine()) != null) {
		     System.out.println(line);
		  }
		}
	*/
		 /* Ejemplo de https://hc.apache.org/httpcomponents-client-ga/quickstart.html */
		 
		 CloseableHttpClient httpclient = HttpClients.createDefault();
		 HttpGet httpGet = new HttpGet("http://biosemantics.arizona.edu/OTO/rest/termCategories/Plant/alternate");
		 CloseableHttpResponse response1 = httpclient.execute(httpGet);
		 // The underlying HTTP connection is still held by the response object
		 // to allow the response content to be streamed directly from the network socket.
		 // In order to ensure correct deallocation of system resources
		 // the user MUST call CloseableHttpResponse#close() from a finally clause.
		 // Please note that if response content is not fully consumed the underlying
		 // connection cannot be safely re-used and will be shut down and discarded
		 // by the connection manager. 
		 try {
		     System.out.println(response1.getStatusLine());
		     HttpEntity entity1 = response1.getEntity();
		     // do something useful with the response body
		     // and ensure it is fully consumed
		     
			 BufferedReader rd = new BufferedReader (new InputStreamReader(entity1.getContent()));
			 String line = "";
			 while ((line = rd.readLine()) != null) {
			     System.out.println(line);
			  }

		     EntityUtils.consume(entity1);
		 } finally {
		     response1.close();
		 }
		
		
	}
	 
}

