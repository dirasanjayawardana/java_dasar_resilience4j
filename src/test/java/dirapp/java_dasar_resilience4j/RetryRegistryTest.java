package dirapp.java_dasar_resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Slf4j
public class RetryRegistryTest {
  // Registry merupakan tempat untuk menyimpan object-object dari Resilience4J (mirip seprti Pooling di java database)
  // dengan menggunakan Registry, bisa menggunakan ulang object yang sudah dibuat, tanpa harus buat baru


  void callMe(){
    log.info("Try call me");
    throw new IllegalArgumentException("Ups error");
  }


  @Test
  void testRetryRegistry() {
    RetryRegistry registry = RetryRegistry.ofDefaults();

    // untuk retry1, karena retry dengan nama "dirapp" belum ada, maka akn dibuatkan object retry dengan nama "dirapp"
    Retry retry1 = registry.retry("dirapp");
    // untuk retry2, karena sudah pernah dibuat object retry dengan nama "dirapp", maka tidak dibuat lagi, akan mengambil object retry yang sudah ada
    Retry retry2 = registry.retry("dirapp");

    Assertions.assertSame(retry1, retry2);
  }


  @Test
  void testRetryRegistryConfig() {
    RetryConfig config = RetryConfig.custom()
        .maxAttempts(5)
        .waitDuration(Duration.ofSeconds(2))
        .build();

    // menambahkan config, bisa juga dengan --> RetryRegistry.of(config)
    RetryRegistry registry = RetryRegistry.ofDefaults();
    registry.addConfiguration("contohConfig", config);

    // menggunakan config dengan menyebutkan nama config yang sudah ditambahkan
    Retry retry1 = registry.retry("dirapp", "contohConfig");
    Retry retry2 = registry.retry("dirapp", "contohConfig");

    Assertions.assertSame(retry1, retry2);

    Runnable runnable = Retry.decorateRunnable(retry1, () -> callMe());
    runnable.run();
  }
}