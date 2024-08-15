# Resilience4J
Merupakan library yang sangat ringan, digunakan untuk meningkatkan kemampuan functional interface, lambda expression dan method reference. Resilience4J merupakan suatu library yang didesain agar aplikasi yang dibangun lebih toleran terhadap kesalahan.
- Konsep Resilience4J adalah membungkus functional interface atau lambda yang dibuat, yang secara otomatis akan menghasilkan object lainnya yang sudah dibungkus dengan module Resilience4J
- ketika coba eksekusi object hasil nya tersebut, maka fitur Resilience4J secara otomatis akan digunakan pada object tersebut.

## Resilience4J pattern (Module)
- Retry --> mengulangi eksekusi yang gagal
- Curcuit Breaker --> sementara menolak eksekusi yang memungkinkan gagal
- Rate Limiter --> membatasi eksekusi dalam kurun waktu tertentu
- Bulkhead --> membatasi eksekusi yang terjadi secara berbarengan
- Cache --> mengingat hasil eksekusi yang sukses
- Fallback --> menyediakan alternatif hasil dari eksekusi yang gagal

## Learning
- test/RetryTest.java
- test/RetryConfigTest.java
- test/RetryRegistryTest.java
- test/RateLimiterTest.java
Rate Limiter merupakan module di Resilience4J untuk membatasi jumlah eksekusi pada waktu tertentu
jika jumlah eksekusi melebihi batas yang sudah ditentukan, secara otomatis akan throw exception RequestNotPermitted