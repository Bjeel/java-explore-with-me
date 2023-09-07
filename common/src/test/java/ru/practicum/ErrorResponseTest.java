package ru.practicum;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

  @Test
  void getError() {
    ErrorResponse errorResponse = new ErrorResponse("message");

    assertThat(errorResponse.getError(), is("message"));
  }

  @Test
  void getNullError() {
    ErrorResponse errorResponse = new ErrorResponse(null);

    assertThat(errorResponse.getError(), nullValue());
  }
}
