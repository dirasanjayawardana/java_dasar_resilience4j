package dirapp.java_dasar_resilience4j;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class TimeLimiterTest {
  // Time Limiter merupakan module di resilience4J untuk membatasi durasi dari sebuah eksekusi kode program
  // jika sudah mencapai batas durasi, akan throw TimeotException
  // Time Limiter membutuhkan eksekusi dalam bentuk Future atau Completable Future


  @SneakyThrows
  public String slow(){
    log.info("Slow");
    Thread.sleep(5000L);
    return "Dira";
  }


  @Test
  void timeLimiter() throws Exception {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<String> future = executorService.submit(() -> slow());

    TimeLimiter timeLimiter = TimeLimiter.ofDefaults("dirapp"); // default durasi tunggu nya 1s
    Callable<String> callable = TimeLimiter.decorateFutureSupplier(timeLimiter, () -> future);

    callable.call();
  }


  @Test
  void timeLimiterConfig() throws Exception {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<String> future = executorService.submit(() -> slow());

    TimeLimiterConfig config = TimeLimiterConfig.custom()
        .timeoutDuration(Duration.ofSeconds(10)) // durasi waktu tunggu, defaultnya 1s
        .cancelRunningFuture(true) // apakah future akan dibatalkan jika terjadi timeout, defaultnya true
        .build();

    TimeLimiter timeLimiter = TimeLimiter.of("dirapp", config);
    Callable<String> callable = TimeLimiter.decorateFutureSupplier(timeLimiter, () -> future);

    callable.call();
  }


  // Registry merupakan tempat untuk menyimpan object-object dari Resilience4J (mirip seprti Pooling di java database)
  // dengan menggunakan Registry, bisa menggunakan ulang object yang sudah dibuat, tanpa harus buat baru
  @Test
  void timeLimiterRegistry() throws Exception {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<String> future = executorService.submit(() -> slow());

    TimeLimiterConfig config = TimeLimiterConfig.custom()
        .timeoutDuration(Duration.ofSeconds(10))
        .cancelRunningFuture(true)
        .build();

    TimeLimiterRegistry registry = TimeLimiterRegistry.ofDefaults();
    registry.addConfiguration("config", config);

    TimeLimiter timeLimiter = registry.timeLimiter("dirapp", "config");
    Callable<String> callable = TimeLimiter.decorateFutureSupplier(timeLimiter, () -> future);

    callable.call();
  }
}