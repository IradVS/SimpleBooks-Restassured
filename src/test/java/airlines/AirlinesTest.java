package airlines;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class AirlinesTest {
	String URI="https://api.instantwebtools.net/";
	@BeforeMethod
	public void beforeMethod() {
		RestAssured.baseURI=URI;
	}
			
	@Test(enabled=false)
	public void getAirlines() {
		Response response=given().log().all()
				.when().get("v1/airlines")
				.then().log().all().assertThat().statusCode(200).body("$.size()", greaterThan(0))
					.extract().response();
		JsonPath jsonR= new JsonPath(response.asString());
		System.out.println("Primer nombre de la respuesta "+jsonR.getString("name[0]"));
		//System.out.println("Respuesta de GET: "+response.asString());
		Assert.assertEquals(jsonR.getString("name[0]"), "Thai Airways");
	}
	
	@Test(enabled=false)
	public void getAirline() {
		given().pathParam("id", 5)
		.when().get("v1/airlines/{id}")
		.then().log().all()
		.assertThat().statusCode(200)
		.header("Server", equalTo("nginx/1.18.0"))
		.body("country", equalTo("Taiwan"));
	}
	
	@Test(enabled=false)
	public void createAirline() {
		given().header("Content-Type","application/json").body("{\n"
				+ "    \"id\": 100000000000055555,\n"
				+ "    \"name\": \"Mijael's Airelines\",\n"
				+ "    \"country\": \"Mexico\",\n"
				+ "    \"logo\": \"https://upload.wikimedia.org/wikipedia/en/thumb/9/9b/Qatar_Airways_Logo.svg/sri_lanka.png\",\n"
				+ "    \"slogan\": \"Just do it\",\n"
				+ "    \"head_quaters\": \"Katunayake, Sri Lanka\",\n"
				+ "    \"website\": \"www.srilankaairways.com\",\n"
				+ "    \"established\": \"1990\"\n"
				+ "}")
		.when().post("v1/airlines")
		.then().log().all().assertThat().statusCode(200).body("$",hasKey("_id"));
	}
	
	@Test
	public void endToEnd() {
		
		Response resposeGET = given().pathParam("id", registerAeroline(1123234534))
		.when().get("v1/airlines/{id}")
		.then().log().all()
		.assertThat().statusCode(200).extract().response();
		
		JsonPath jsonGET = new JsonPath(resposeGET.asString());
		
		Assert.assertEquals(jsonGET.getString("country"), "Mexico");
		Assert.assertEquals(jsonGET.getString("logo"), "https://upload.wikimedia.org/wikipedia/en/thumb/9/9b/Qatar_Airways_Logo.svg/sri_lanka.png");
		Assert.assertEquals(jsonGET.getString("slogan"), "Just do it");
	}
	
	public Long registerAeroline(long id) {
		RestAssured.baseURI=URI;
		Response response = given().header("Content-Type","application/json").body("{\n"
				+ "    \"id\": "+id+",\n"
				+ "    \"name\": \"Mijael's Airelines\",\n"
				+ "    \"country\": \"Mexico\",\n"
				+ "    \"logo\": \"https://upload.wikimedia.org/wikipedia/en/thumb/9/9b/Qatar_Airways_Logo.svg/sri_lanka.png\",\n"
				+ "    \"slogan\": \"Just do it\",\n"
				+ "    \"head_quaters\": \"Katunayake, Sri Lanka\",\n"
				+ "    \"website\": \"www.srilankaairways.com\",\n"
				+ "    \"established\": \"1990\"\n"
				+ "}")
		.when().post("v1/airlines")
		.then().log().all().assertThat().statusCode(200).body("$",hasKey("_id")).extract().response();
		
		JsonPath jsonr=new JsonPath(response.asString());
		
		return jsonr.getLong("id");
	}
}
