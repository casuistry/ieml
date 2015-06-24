package  com.casuistry.ieml.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import NewParser.ParserImpl;
import NewParser.Token;

@Path("/iemlparser")
public class ParserService {
 
	 
	
	
	  @GET
	  @Produces("application/json")
	  public Response parse(@QueryParam("iemltext") String iemltext) throws JSONException {
 
		JSONObject jsonObject = new JSONObject();
		
		ParserImpl parser = new ParserImpl();
		 try {				
				Token n = parser.parse(iemltext);
				jsonObject.put("success", true); 
				
			} catch (Exception e) {
				
				jsonObject.put("exception", e.getMessage()); 
				jsonObject.put("success", false); 
				jsonObject.put("at",parser.GetCounter());
				
				
			}		
			finally {
				parser.Reset();
		} 
		
 
		String result =jsonObject.toString();
		return Response.status(200).entity(result).build();
	  }
	  
	  @POST
	  @Produces("application/json")
	  public Response parsep(@QueryParam("iemltext") String iemltext) throws JSONException {
 
		JSONObject jsonObject = new JSONObject();
		
		ParserImpl parser = new ParserImpl();
		 try {				
				Token n = parser.parse(iemltext);
				jsonObject.put("success", true); 
				
			} catch (Exception e) {
				
				jsonObject.put("exception", e.getMessage()); 
				jsonObject.put("success", false); 
				jsonObject.put("at",parser.GetCounter());
				
				
			}		
			finally {
				parser.Reset();
		} 
		
 
		String result =jsonObject.toString();
		return Response.status(200).entity(result).build();
	  }
 
	
}