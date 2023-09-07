package ru.practicum.hit;

//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.json.JsonTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.json.JacksonTester;
//import org.springframework.boot.test.json.JsonContent;
//
//import static org.assertj.core.api.Assertions.assertThat;

//@JsonTest
//class HitDtoTest {
//  @Autowired
//  private JacksonTester<HitDto> json;
//
//  @Test
//  public void testBookingDto() throws Exception {
//    Long hitsCount = 10L;
//    String app = "ewm-main-service";
//    String uri = "/events/1";
//
//    HitDto hitDto = HitDto.builder()
//      .hits(hitsCount)
//      .app(app)
//      .uri(uri)
//      .build();
//
//    JsonContent<HitDto> result = json.write(hitDto);
//
//    assertThat(result).extractingJsonPathNumberValue("$.app").isEqualTo(app);
//    assertThat(result).extractingJsonPathNumberValue("$.uri").isEqualTo(uri);
//    assertThat(result).extractingJsonPathStringValue("$.hits").isEqualTo(hitsCount);
//  }
//}
