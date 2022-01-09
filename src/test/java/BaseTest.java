import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import model.Booking;
import model.BookingDates;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utils.ApiHelper;

public class BaseTest {

  protected static RequestSpecification requestSpec;
  protected int bookingId;
  protected Faker faker = Faker.instance();
  protected static String BOOKING_API = "/booking/";

  @BeforeAll
  static void beforeAll() {
    String token = "token=" + new ApiHelper().generateToken();
    RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    requestSpec =
        new RequestSpecBuilder()
            .setAccept("application/json")
            .setContentType("application/json")
            .addHeader("Cookie", token)
            .build();
  }

  @BeforeEach
  void beforeEach() {
    createBooking();
  }

  private void createBooking() {
    String checkinDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    String checkoutDate = LocalDate.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
    Booking booking =
        Booking.builder()
            .firstname("Jim")
            .lastname("Brown")
            .totalprice(111)
            .depositpaid(true)
            .bookingdates(
                BookingDates.builder().checkin(checkinDate).checkout(checkoutDate).build())
            .additionalneeds("Breakfast")
            .build();
    Response response =
        RestAssured.given().spec(requestSpec).body(booking).when().post(BOOKING_API);
    response.then().statusCode(HttpStatus.SC_OK);
    bookingId = response.then().extract().path("bookingid");
  }
}
