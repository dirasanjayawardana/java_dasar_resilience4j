package dirapp.java_dasar_resilience4j;

import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
public class DecoratorsTest {
  // Decorators merupakan module di resilience4J untuk menggabungkan beberapa module sekaligus
  // namun saat ini, Decorators belum bisa menggabungkan module Time Limiter


  @SneakyThrows
  public void slow() {
    log.info("Slow");
    Thread.sleep(1_000L);
    throw new IllegalArgumentException("Error");
  }


  @SneakyThrows
  public String sayHello(){
    log.info("Say hello");
    Thread.sleep(1_000L);
    throw new IllegalArgumentException("Ups");
  }


  @Test
  void decorators() throws InterruptedException {
    RateLimiter rateLimiter = RateLimiter.of("dirapp-ratelimiter", RateLimiterConfig.custom()
        .limitForPeriod(5)
        .limitRefreshPeriod(Duration.ofMinutes(1))
        .build());

    Retry retry = Retry.of("dirapp-retry", RetryConfig.custom()
        .maxAttempts(10)
        .waitDuration(Duration.ofMillis(10))
        .build());

    Runnable runnable = Decorators.ofRunnable(() -> slow())
        .withRetry(retry)
        .withRateLimiter(rateLimiter)
        .decorate();

    for (int i = 0; i < 100; i++) {
      new Thread(runnable).start();
    }

    Thread.sleep(10_000L);
  }


  // untuk kasus dimana functional interfacenya mengembalikan value, maka bisa menambahkan fallback di dalam Decorators
  // jika terjadi error, maka fallback akan dipanggil
  @Test
  void fallback() throws InterruptedException {
    RateLimiter rateLimiter = RateLimiter.of("dirapp-ratelimiter", RateLimiterConfig.custom()
        .limitForPeriod(5)
        .limitRefreshPeriod(Duration.ofMinutes(1))
        .build());

    Retry retry = Retry.of("dirapp-retry", RetryConfig.custom()
        .maxAttempts(10)
        .waitDuration(Duration.ofMillis(10))
        .build());

    Supplier<String> supplier = Decorators.ofSupplier(() -> sayHello())
        .withRetry(retry)
        .withRateLimiter(rateLimiter)
        .withFallback(throwable -> "Hello Guest")
        .decorate();

    System.out.println(supplier.get());
  }
}