package dirapp.java_dasar_resilience4j;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RateLimiterTest {
  // Rate Limiter merupakan module di Resilience4J untuk membatasi jumlah eksekusi pada waktu tertentu
  // jika jumlah eksekusi melebihi batas yang sudah ditentukan, secara otomatis akan throw exception RequestNotPermitted


  private final AtomicLong counter = new AtomicLong(0L);


  @Test
  void testRateLimiter() {
    // default limit eksekusi rate limiter adalah 50 eksekusi per 500ns
    RateLimiter rateLimiter = RateLimiter.ofDefaults("dirapp");

    for (int i = 0; i < 10_000; i++) {
      Runnable runnable = RateLimiter.decorateRunnable(rateLimiter, () -> {
        long result = counter.incrementAndGet();
        log.info("Result: {}", result);
      });

      runnable.run();
    }
  }


  // menggunakan custom config untuk rate limiter
  // limitForPeriod() --> jumlah yang diperbolehkan dalam periode refresh (defaultnya 50)
  // limitRefreshPeriod() --> durasi refersh dalam satu periode referesh (defaultnya 500ns)
  // timeoutDuration() --> waktu maksimal mengunggu rate limiter (defaultnya 5s)
  @Test
  void testRateLimiterConfig() {
    RateLimiterConfig config = RateLimiterConfig.custom()
        .limitForPeriod(100)
        .limitRefreshPeriod(Duration.ofMinutes(1))
        .timeoutDuration(Duration.ofSeconds(2))
        .build();

    RateLimiter rateLimiter = RateLimiter.of("dirapp", config);

    for (int i = 0; i < 10_000; i++) {
      Runnable runnable = RateLimiter.decorateRunnable(rateLimiter, () -> {
        long result = counter.incrementAndGet();
        log.info("Result: {}", result);
      });

      runnable.run();
    }
  }


  // menggunakan regsitry (menampung object-object) rate limiter
  @Test
  void testRateLimiterRegistry() {
    RateLimiterConfig config = RateLimiterConfig.custom()
        .limitForPeriod(100)
        .limitRefreshPeriod(Duration.ofMinutes(1))
        .timeoutDuration(Duration.ofSeconds(2))
        .build();

    RateLimiterRegistry registry = RateLimiterRegistry.ofDefaults();
    registry.addConfiguration("contohConfig", config);

    RateLimiter rateLimiter = registry.rateLimiter("dirapp", "contohConfig");

    for (int i = 0; i < 10_000; i++) {
      Runnable runnable = RateLimiter.decorateRunnable(rateLimiter, () -> {
        long result = counter.incrementAndGet();
        log.info("Result: {}", result);
      });

      runnable.run();
    }
  }
}