package dirapp.java_dasar_resilience4j;

import org.junit.jupiter.api.Test;

import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import java.util.function.Supplier;

@Slf4j
public class RetryTest {

  // Retry merupakan interface representasi module di Resilience untuk mecoba melakukan eksekusi kode secara berulang dalam jumlah yang sudah ditentukan
  // Retry.ofDefaults("namaNyabebas") --> membuat Retry
  // Konsep Resilience4J adalah membungkus functional interface atau lambda yang dibuat, yang secara otomatis akan menghasilkan object lainnya yang sudah dibungkus dengan module Resilience4J
  // ketika coba eksekusi object hasil nya tersebut, maka fitur Resilience4J secara otomatis akan digunakan pada object tersebut

  void callMe(){
    log.info("Try call me");
    throw new IllegalArgumentException("Ups error");
  }


  @Test
  void createNewRetry() {
    Retry retry = Retry.ofDefaults("dirapp");

    // Runnable returnnya void
    Runnable runnable = Retry.decorateRunnable(retry, () -> callMe());
    runnable.run();
  }


  String hello(){
    log.info("Call say hello");
    throw new IllegalArgumentException("Ups error say hello");
  }


  @Test
  void createRetrySupplier() {
    Retry retry = Retry.ofDefaults("dirapp");

    // Supplier returnnya generic, contohnya <String>
    Supplier<String> supplier = Retry.decorateSupplier(retry, () -> hello());
    supplier.get();
  }
}