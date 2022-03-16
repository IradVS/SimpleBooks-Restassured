package books;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class SimpleBooks {
	String URI = "https://simple-books-api.glitch.me";
	String token ="9c23be4c376d74ad3778410fed9ebd21efc9dba2f50550a79fe1f3e994ab35d2";

	@BeforeTest
	public void startTest() {
		RestAssured.baseURI = URI;
	}

	// 1. Customer is able to log into the website (No errors received)
	@Test
	public void welcome() {
		given().when().get().then().log().all().assertThat().statusCode(200).body("message",
				equalTo("Welcome to the Simple Books API."));
	}

	// 2. Customer is able to view all books offered by the store/Customer is able
	// to view up to 5 ids per page
	@Test
	public void limitBooks() {
		given().queryParam("limit", 2)// URI/?available=true
				.when().get("/books").then().log().all().assertThat().statusCode(200);
	}

	// 3. Customer is able to select books by their type
	@Test
	public void fictionBooks() {
		given().queryParam("type", "fiction")// URI/?available=true
				.when().get("/books").then().log().all().assertThat().statusCode(200);
	}

	@Test
	public void nonFictionBooks() {
		given().queryParam("type", "non-fiction")// URI/?available=true
				.when().get("/books").then().log().all().assertThat().statusCode(200);
	}

	// 4. Customer is able to select a book and view information about it (Price,
	// Name, Author)
	@Test
	public void singleBook() {
		getBook(1);
	}

	// 5. Customer is able to create a profile (in order for the developer to
	// identify the
	// customer, we will receive an authentication token once registered)
	@Test
	public void registerAPIClient() {
		token = createUser("joseir7uiyuyy", "joseir7iuyiuyt@email.com");

	}

	// 6. Customer is able to place an order
	@Test
	public void authCreateOrder() {
		given().auth().oauth2(token).header("Content-Type", "application/json")
				.body("{\n" + "    \"bookId\": 3,\n" + "    \"customerName\": \"patriciaGB\"\n" + "}").when()
				.post("/orders").then().log().all().assertThat().statusCode(201);
	}

	// 7. Customer is able to view all orders made
	@Test
	public void showOrders() {
		given().header("Authorization", "Bearer " + token).when().get("/orders").then().log().all().assertThat()
				.statusCode(200);
	}

	public void getBook(int id) {
		RestAssured.baseURI = URI;
		given().pathParam("id", id).when().get("/books/{id}").then().log().all().assertThat().statusCode(200);
	}

	public String createUser(String Name, String Email) {
		RestAssured.baseURI = URI;
		Response response = given().header("Content-Type", "application/json")
				.body("{\n" + "    \"clientName\": \"" + Name + "\",\n" + "    \"clientEmail\": \"" + Email + "\"\n"
						+ "}")
				.when().post("/api-clients").then().log().all().assertThat().statusCode(201)
				.body("$", hasKey("accessToken")).extract().response();

		JsonPath jsonr = new JsonPath(response.asString());
		return jsonr.getString("accessToken");
	}

	/*@Test
	public void showBooks() {
		given().when().get("/books").then().log().all().assertThat().statusCode(200);
	}

	@Test
	public void availableBooks() {
		given().queryParam("available", true)// URI/?available=true
				.when().get("/books").then().log().all().assertThat().statusCode(200);
	}*/

}
