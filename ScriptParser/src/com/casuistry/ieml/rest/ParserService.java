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
import Utilities.RelationBuilder;
import Utilities.RelationGenerator;
import Utilities.TableGenerator;
import Utilities.TableGenerator.JsonTables;



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
				jsonObject.put("taille", n.taille);				
				JSONObject canon = n.buildCanonical();
				jsonObject.put("canonical", canon.getJSONArray("canonical"));
				
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
				jsonObject.put("taille", n.taille);
				JSONObject canon = n.buildCanonical();
				jsonObject.put("canonical", canon.getJSONArray("canonical"));
				
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
 
	  
	  @POST
	  @Path("/tree")
	  @Produces("application/json")
	  @Consumes("application/x-www-form-urlencoded")

	  public Response partree(@FormParam("iemltext") String iemltext) throws JSONException {
	 
		  
		JSONObject jsonObject = new JSONObject();
		
		ParserImpl parser = new ParserImpl();
		 try {				
				Token n = parser.parse(iemltext);
				jsonObject.put("success", true); 
				jsonObject.put("tree", n.buildTree(null));
				jsonObject.put("level", n.layer);
				jsonObject.put("taille", n.taille);
				JSONObject canon = n.buildCanonical();
				jsonObject.put("canonical", canon.getJSONArray("canonical"));
				
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
	  
	  
	  @POST
	  @Path("/tables")
	  @Produces("application/json")
	  @Consumes("application/x-www-form-urlencoded")

	  public Response partable(@FormParam("iemltext") String iemltext) throws JSONException {
	 
		  
		JSONObject jsonObject = new JSONObject();
		
		ParserImpl parser = new ParserImpl();
		 try {				
				Token n = parser.parse(iemltext);
				jsonObject.put("success", true); 
				TableGenerator tgen = new TableGenerator();
				JsonTables json = tgen.genJSONTables(n);			
				jsonObject.put("tree", new JSONObject(json.getMaterial()));			
				
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
 
	  @POST
	  @Path("/relationship")
	  @Produces("application/json")
	  @Consumes("application/x-www-form-urlencoded")

	  public Response relations(@FormParam("iemltext") String iemltext) throws JSONException {
	 
		  
	
		
		 try {				
				String rel = 
						RelationBuilder.GetRelations(iemltext);
				
				return Response.status(200).entity(rel).build();
				
			} catch (Exception e) {
				 
				return Response.status(500).entity(e.getMessage()).build();
				
				
				
			}		
			finally {
				
		} 
	
	
	  }
	  
	  
	  @POST
	  @Path("/relationship2")
	  @Produces("application/json")
	  @Consumes("application/x-www-form-urlencoded")
	  public Response relations2(@FormParam("iemltext") String iemltext, @FormParam("parad") int parad) throws JSONException {

		 try {				
				String rel = RelationGenerator.generateRelations(iemltext, parad);				
				return Response.status(200).entity(rel).build();				
			} catch (Exception e) {				 
				return Response.status(500).entity(e.getMessage()).build();
			}		
			finally {
				
			} 
	  }
}