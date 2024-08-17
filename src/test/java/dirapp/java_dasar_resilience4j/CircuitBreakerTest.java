package dirapp.java_dasar_resilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class CircuitBreakerTest {
  // Circuit Breaker merupakan module resilience4J yg digunakan untuk memberhentikan eksekusi yang kemungkinan gagal
  // Circuit Breaker adalah implementasi dari finite state machine, dengan tiga normal state
  // CLOSED, OPEN, HALF_OPEN, dan ada dua spesial state DISABLED dan FORCED_OPEN
  // circuit breaker bisa digunakan berbasis hitungan N atau waktu

  // state awal Circuit Breaker adalah CLOSED, ketika terjadi error (failure ratenya > threshold), maka Circuit Breaker akan OPEN
  // setelah durasi tunggu tertentu (afterWaitDuration), Circuit Breaker akan HALF_OPEN (hanya mengirim sebagian request)
  // request dari HALF_OPEN akan dicoba, jika failure ratenya > threshold, maka Circuit Breaker akan OPEN
  // jika failure ratenya < threshold, maka Circuit Breaker akan CLOSED


  public void callMe(){
    log.info("Call Me");
    throw new IllegalArgumentException("Ups");
  }


  @Test
  void circuitBreaker() {
    CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("dirapp");

    for (int i = 0; i < 200; i++) {
      try {
        Runnable runnable = CircuitBreaker.decorateRunnable(circuitBreaker, () -> callMe());
        runnable.run();
      } catch (Exception e){
        log.error("Error : {}", e.getMessage());
      }
    }
  }


  @Test
  void circuitBreakerConfig() {
    CircuitBreakerConfig config = CircuitBreakerConfig.custom()
        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED) // menentukan mode cisrcuit breaker berdasarkan hitungan, defaultnya menggunakan hitungan dengan jumlah minimal 100
        .failureRateThreshold(10f) // minimal persentase error rate yang membuat state circuit breaker menjadi OPEN, defaultnya 50%
        .slidingWindowSize(10) // jumlah sliding window yang direcord pada waktu state CLOSED, defaultnya 100
        .minimumNumberOfCalls(10) // jumlah minimal eksekusi sebelum error rate dihitung, defaultnya 100
        // .waitDurationInOpenState() // waktu tunggu agar OPEN menjadi HALF_OPEN, defaultnya 6000ms
        // .permittedNumberOfCallsInHalfOpenState() // jumlah eksekusi yang diperbolehkan ketika Circuit Breaker HALF_OPEN, defaultnya 10
        // .maxWaitDurationInHalfOpenState() // jumlah maksimal menunggu di HALF_OPEN untuk kembali OPEN, defaultnya 0, artinya menunggu tak terbatas
        .build();

    CircuitBreaker circuitBreaker = CircuitBreaker.of("dirapp", config);

    for (int i = 0; i < 200; i++) {
      try {
        Runnable runnable = CircuitBreaker.decorateRunnable(circuitBreaker, () -> callMe());
        runnable.run();
      }catch (Exception e){
        log.error("Error : {}", e.getMessage());
      }
    }
  }


  // Registry merupakan tempat untuk menyimpan object-object dari Resilience4J (mirip seprti Pooling di java database)
  // dengan menggunakan Registry, bisa menggunakan ulang object yang sudah dibuat, tanpa harus buat baru
  @Test
  void circuitBreakerRegistry() {
    CircuitBreakerConfig config = CircuitBreakerConfig.custom()
        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
        .failureRateThreshold(10f)
        .slidingWindowSize(10)
        .minimumNumberOfCalls(10)
        .build();

    CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
    registry.addConfiguration("config", config);

    CircuitBreaker circuitBreaker = registry.circuitBreaker("dirapp", "config");

    for (int i = 0; i < 200; i++) {
      try {
        Runnable runnable = CircuitBreaker.decorateRunnable(circuitBreaker, () -> callMe());
        runnable.run();
      }catch (Exception e){
        log.error("Error : {}", e.getMessage());
      }
    }
  }
}