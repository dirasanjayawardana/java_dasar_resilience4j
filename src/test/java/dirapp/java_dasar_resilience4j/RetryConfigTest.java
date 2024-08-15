package dirapp.java_dasar_resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
public class RetryConfigTest {

  // RetryConfig merupakan object yang berisi config untuk melakukan retry
  // maxAttempts --> seberapa banyak retry dilakukan (defaultnya 3)
  // waitDuration --> waktu menunggu sebelum melakukan retry (defaultnya 500 ms)
  // retryExceptions --> jenis exception yang akan di retry (defaultnya semua exception)
  // ignoreExceptions --> jenis exception yang tidak akan di retry (defaultnya tidak ada)


  String hello() {
    log.info("Call hello()");
    throw new IllegalArgumentException("Ups");
  }


  @Test
  void retryConfig() {
    RetryConfig config = RetryConfig.custom()
        .maxAttempts(5)
        .waitDuration(Duration.ofSeconds(2))
        // .ignoreExceptions(IllegalArgumentException.class)
        // .retryExceptions(IllegalArgumentException.class)
        .build();

    Retry retry = Retry.of("dirapp", config);

    Supplier<String> supplier = Retry.decorateSupplier(retry, () -> hello());
    supplier.get();
  }
}