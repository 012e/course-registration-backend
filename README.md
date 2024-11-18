# Demo cache

## Dependency

- Docker (phiên bản mới mới một xíu)
- Python >=3.10

## Architecture

![main.d2](https://github.com/user-attachments/assets/a8634292-ddb4-4439-92be-1d58c6d46211)

## Chạy

### Không có cache

Chạy lệnh sau để bắt đầu web server (sử dụng postgres) ở `localhost:8080`

```sh
docker compose --profile database up
```

**Lưu ý**: Đây chỉ là chạy để phát triển, không có k6, Prometheus, Grafana,... Để chạy benchmark/giám sát, xem nội dung
phần benchmark.

### Có cache

Chạy lệnh sau để bắt đầu web server (sử dụng postgres + redis) ở `localhost:8080`

```sh
docker compose --profile cache up
```

**Lưu ý**: Đây chỉ là chạy để phát triển, không có k6, Prometheus, Grafana,... Để chạy benchmark/giám sát, xem nội dung
phần benchmark.

## Benchmark và giám sát

### Cách chạy

Để chạy benchmark, trước hết cần chạy docker compose với các profile phù hợp

```sh
docker compose --profile database --profile test up --build -d
```

hoặc để bật cache thì

```sh
docker compose --profile cache --profile test up --build -d
```

Sau đó thì chạy benchmark bằng script benchmark/benchmark.py (python chỉ dùng để gọi các api của k6)

```shell
python benchmark/benchmark.py
```

hoặc điều khiển k6 bằng cli

```shell
k6 status
k6 scale --vus=100
...
```

### Nội dung benchmark

1. Tạo username, pasword ngẫu nhiên
2. Đăng ký với username, password đó
3. Đăng nhập với username, password đó
4. Truy cập `/auth/secret` để đọc thông tin bí mật (ngẫu nhiên từ 1-30 lần)
5. Đăng xuất

### Giám sát

Để giám sát thì ta có thể truy cập `http://localhost:3000` để sử dụng grafana dashboard. Sau đó có thể import dashboard
đã trong thư mục `infrastructure/grafana/dashboard.json` (TODO).

## Các Endpoint của webserver

Có thể xem chi tiết các endpoint ở http://localhost:8080/schema/swagger

## Chi tiết các container được sử dụng

Dưới đây là bảng tóm tắt cấu hình các dịch vụ trong Docker Compose:

| **Tên Container**     | **Image**                                             | **Cổng Mở** | **Profiles** | **Mô Tả**                                         |
|-----------------------|-------------------------------------------------------|-------------|--------------|---------------------------------------------------|
| **db**                | postgres:16.4                                         | 5432:5432   | -            | Container cho cơ sở dữ liệu PostgreSQL.           |
| **redis**             | redis                                                 | 6379:6379   | -            | Container cho Redis (lưu trữ key-value).          |
| **app-db**            | Chạy spring boot webserver với tính năng cache bị tắt | 8080:8080   | `database`   | Container ứng dụng sử dụng profile database.      |
| **app-cache**         | Chạy spring boot webserver với đầy đủ                 | 8080:8080   | `cache`      | Container ứng dụng sử dụng profile cache.         |
| **k6**                | grafana/k6                                            | 6565:6565   | `test`       | Công cụ kiểm thử tải K6, tích hợp với Prometheus. |
| **my-grafana**        | grafana/grafana                                       | 3000:3000   | `test`       | Dashboard giám sát bằng Grafana.                  |
| **my-prometheus**     | prom/prometheus                                       | 9090:9090   | `test`       | Prometheus thu thập và giám sát dữ liệu.          |
| **postgres-exporter** | quay.io/prometheuscommunity/postgres-exporter         | 9187:9187   | `test`       | Exporter cho dữ liệu PostgreSQL sang Prometheus.  |
| **redis-exporter**    | oliver006/redis_exporter                              | 9121:9121   | `test`       | Exporter cho dữ liệu Redis sang Prometheus.       |
| **dex**               | spx01/dex                                             | 3456:3456   | `test`       | Giám sát các container.                           |

- Các **Profiles**:
  - `database`: Chạy phiên bản không có cache.
  - `cache`: Chạy phiên bản có cache.
  - `test`: Dùng cho việc kiểm thử hiệu năng và thiết lập giám sát.

