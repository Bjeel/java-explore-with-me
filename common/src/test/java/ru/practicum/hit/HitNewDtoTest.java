package ru.practicum.hit;

//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.json.JsonTest;
//import org.springframework.boot.test.json.JacksonTester;
//import org.springframework.boot.test.json.JsonContent;
//
//import static org.assertj.core.api.Assertions.assertThat;

//@JsonTest
//class HitNewDtoTest {
//  @Autowired
//  private JacksonTester<HitNewDto> json;
//
//  @Test
//  public void testBookingDto() throws Exception {
//    String app = "ewm-main-service";
//    String uri = "/events/1";
//    String ip = "192.158.0.1";
//
//    HitNewDto hitDto = HitNewDto.builder()
//      .app(app)
//      .uri(uri)
//      .ip(ip)
//      .build();
//
//    JsonContent<HitNewDto> result = json.write(hitDto);
//
//    assertThat(result).extractingJsonPathNumberValue("$.app").isEqualTo(app);
//    assertThat(result).extractingJsonPathNumberValue("$.uri").isEqualTo(uri);
//    assertThat(result).extractingJsonPathStringValue("$.ip").isEqualTo(ip);
//  }
//}
