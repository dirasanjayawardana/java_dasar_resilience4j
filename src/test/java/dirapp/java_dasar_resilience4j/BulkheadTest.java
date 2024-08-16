package dirapp.java_dasar_resilience4j;

import io.github.resilience4j.bulkhead.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Slf4j
public class BulkheadTest {
  // Bulkhead merupakan module Resilience4J untuk membatasi jumlah eksekusi concurrent, atau membatasi jumlah eksekusi yang berbarengan
  // jika Bulkhead sudah penuh, maka akan throw exception BulkheadFullException
  // inpplementasi Bulkhead ada dua, Semaphore (ditentukan jumlahnya berapa) dan FixThreadPool (akan membuat thread pool)


  private AtomicLong counter = new AtomicLong(0L);


  @SneakyThrows
  public void slow() {
    long value = counter.incrementAndGet();
    log.info("Slow : " + value);
    Thread.sleep(1_000L);
  }


  @Test
  void testSemaphore() throws InterruptedException {
    Bulkhead bulkhead = Bulkhead.ofDefaults("dirapp");

    for (int i = 0; i < 1000; i++) {
      Runnable runnable = Bulkhead.decorateRunnable(bulkhead, () -> slow());
      new Thread(runnable).start();
    }

    Thread.sleep(10_000L);
  }


  @Test
  void testThreadPool() {
    log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));

    // defaultnya adalah sesuai jumlah cpu device
    ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.ofDefaults("dirapp");

    for (int i = 0; i < 1000; i++) {
      Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
      supplier.get();
    }
  }


  @Test
  void testSemaphoreConfig() throws InterruptedException {
    BulkheadConfig config = BulkheadConfig.custom()
        .maxConcurrentCalls(5) // defaultnya 25 eksekusi
        .maxWaitDuration(Duration.ofSeconds(5)) // defaultnya 0s
        .build();

    Bulkhead bulkhead = Bulkhead.of("dirapp", config);

    for (int i = 0; i < 10; i++) {
      Runnable runnable = Bulkhead.decorateRunnable(bulkhead, () -> slow());
      new Thread(runnable).start();
    }

    Thread.sleep(10_000L);
  }


  @Test
  void testThreadPoolConfig() throws InterruptedException {
    ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
        .maxThreadPoolSize(5) // maksimal thread yg ada di pool, defaultnya sejumlah processor device
        .coreThreadPoolSize(5) // minimal thread awal yang ada di pool, defaultnya sejumlah processor - 1
        .queueCapacity(1) // kapasitas antrian, defaultnya 100
        // .keepAliveDuration(Duration.ofSeconds(1))  // lama thread hidup jika tidak berkerja, defaultnya 20ms
        .build();

    log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));

    ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.of("dirapp", config);

    for (int i = 0; i < 20; i++) {
      Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
      supplier.get();
    }

    Thread.sleep(10_000L);
  }


  @Test
  void testSemaphoreRegistry() throws InterruptedException {
    BulkheadConfig config = BulkheadConfig.custom()
        .maxConcurrentCalls(5)
        .maxWaitDuration(Duration.ofSeconds(5))
        .build();

    BulkheadRegistry registry = BulkheadRegistry.ofDefaults();
    registry.addConfiguration("config", config);

    Bulkhead bulkhead = registry.bulkhead("dirapp", "config");

    for (int i = 0; i < 10; i++) {
      Runnable runnable = Bulkhead.decorateRunnable(bulkhead, () -> slow());
      new Thread(runnable).start();
    }

    Thread.sleep(10_000L);
  }


  @Test
  void testThreadPoolRegistry() throws InterruptedException {
    ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
        .maxThreadPoolSize(5)
        .coreThreadPoolSize(5)
        .queueCapacity(1)
        .build();

    log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));

    ThreadPoolBulkheadRegistry registry = ThreadPoolBulkheadRegistry.ofDefaults();
    registry.addConfiguration("config", config);

    ThreadPoolBulkhead bulkhead = registry.bulkhead("dirapp", "config");

    for (int i = 0; i < 20; i++) {
      Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
      supplier.get();
    }

    Thread.sleep(10_000L);
  }
}