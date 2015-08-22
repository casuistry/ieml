package  com.casuistry.ieml.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import NewParser.ParserImpl;
import NewParser.Token;



// http://blog.kdecherf.com/2011/06/19/java-jersey-a-cors-compliant-rest-api/


@Path("/iemlparser")
public class ParserService {
	private String _corsHeaders;
	@OPTIONS
	 public Response corsMyResource(@HeaderParam("Access-Control-Request-Headers") String requestH) {
	      _corsHeaders = requestH;
	      return makeCORS(Response.ok(), requestH);
	   }
	
	private Response makeCORS(ResponseBuilder req, String returnMethod) {
		   ResponseBuilder rb = req.header("Access-Control-Allow-Origin", "*")
		      .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");

		   if (!"".equals(returnMethod)) {
		      rb.header("Access-Control-Allow-Headers", returnMethod);
		   }

		   return rb.build();
		}

		private Response makeCORS(ResponseBuilder req) {
		   return makeCORS(req, _corsHeaders);
		}
	
	  @GET
	  @Produces("application/json")
	  public Response parse(@QueryParam("iemltext") String iemltext) throws JSONException {
 
		JSONObject jsonObject = new JSONObject();
		
		ParserImpl parser = new ParserImpl();
		 try {				
				Token n = parser.parse(iemltext);
				jsonObject.put("success", true); 
				jsonObject.put("class", n.GetTokenClass());
				jsonObject.put("level", n.layer);
				
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
	  @Consumes("application/x-www-form-urlencoded")

	  public Response parsep(@FormParam("iemltext") String iemltext) throws JSONException {
	 
		  
		JSONObject jsonObject = new JSONObject();
		
		ParserImpl parser = new ParserImpl();
		 try {				
				Token n = parser.parse(iemltext);
				jsonObject.put("success", true); 
				jsonObject.put("class", n.GetTokenClass());
				jsonObject.put("level", n.layer);
				
				
			} catch (Exception e) {
				
				jsonObject.put("exception", e.getMessage()); 
				jsonObject.put("success", false); 
				jsonObject.put("at",parser.GetCounter());
				
				
			}		
			finally {
				parser.Reset();
		} 
		
 
		String result =jsonObject.toString();
		
		return makeCORS(Response.status(200).entity(result));
	  }
 
	
}