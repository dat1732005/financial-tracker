\# LAB 1: Hệ thống thu thập \& theo dõi dữ liệu tài chính (Financial News/Market Tracker)



\## 1. Mô tả bài toán



Xây dựng một backend service thu thập, lưu trữ và cung cấp dữ liệu tài chính (giá vàng, giá dầu, chỉ số chứng khoán Việt Nam) theo thời gian, kèm theo các tin tức/quyết định của nhà nước có liên quan. Người dùng (thông qua API) có thể tra cứu dữ liệu lịch sử, xem xu hướng biến động, và đọc tin tức liên quan đến từng loại tài sản.



Đây là dạng bài toán "data pipeline + API" — trọng tâm không phải là giao diện đẹp, mà là:

\- Thu thập dữ liệu tự động, đều đặn, đáng tin cậy.

\- Lưu trữ dữ liệu time-series hợp lý, truy vấn nhanh.

\- Thiết kế API rõ ràng, dễ mở rộng.



\## 2. Đối tượng sử dụng



\- Người dùng cuối: nhà đầu tư cá nhân muốn theo dõi biến động giá vàng/dầu/chứng khoán.

\- (Giả định) Admin: người quản lý nguồn dữ liệu, đăng tin tức liên quan.



\## 3. Yêu cầu chức năng (Functional Requirements)



\### 3.1 Must-have (bắt buộc)



\*\*Thu thập dữ liệu\*\*

\- \[ ] Job chạy định kỳ (dùng `@Scheduled`) để lấy dữ liệu giá vàng/dầu/chứng khoán.

\- \[ ] Nguồn dữ liệu giai đoạn đầu: dataset lịch sử có sẵn (CSV/Kaggle) nạp vào DB, hoặc mock data sinh tự động — \*\*không bắt buộc phải gọi API thật trả phí\*\*.

\- \[ ] Lưu log mỗi lần thu thập (thành công/thất bại, thời gian chạy).



\*\*Quản lý dữ liệu tài sản (Asset Price)\*\*

\- \[ ] CRUD cho danh mục tài sản theo dõi (vàng, dầu, mã CK: VNM, VIC, HPG...).

\- \[ ] Lưu lịch sử giá theo thời gian (timestamp, giá mở/đóng/cao/thấp nếu có).



\*\*API truy vấn\*\*

\- \[ ] `GET /api/assets` — danh sách tài sản đang theo dõi.

\- \[ ] `GET /api/assets/{code}/prices?from=...\&to=...` — lịch sử giá theo khoảng thời gian.

\- \[ ] `GET /api/assets/{code}/prices/latest` — giá mới nhất.

\- \[ ] `GET /api/news?assetCode=...` — tin tức liên quan đến 1 tài sản.

\- \[ ] Phân trang (pagination) cho các API trả danh sách dài.



\*\*Quản lý tin tức\*\*

\- \[ ] CRUD tin tức (tiêu đề, nội dung tóm tắt, ngày đăng, tài sản liên quan, nguồn).

\- \[ ] Thu thập tin tức tự động qua \*\*News API (NewsAPI.org hoặc GNews API)\*\* — dùng free tier:

&#x20; - Job `@Scheduled` riêng, gọi API theo từ khóa gắn với từng tài sản (VD: `gold price` → asset XAU, `oil price` → asset WTI, `Vietnam stock market` → nhóm CK VN).

&#x20; - Map response JSON (title, description, publishedAt, source, url) → entity `News`.

&#x20; - Check trùng lặp theo `url` hoặc `title` trước khi insert để tránh duplicate.

&#x20; - Tần suất gọi hợp lý (VD: 15-30 phút/lần) để không vượt quota free tier (NewsAPI free \~100 request/ngày).

\- \[ ] Nếu hết quota hoặc lỗi API, job phải log lại (`collection\_log`) và không làm crash hệ thống.



\### 3.2 Nice-to-have (nếu còn thời gian)



\- \[ ] Cảnh báo khi giá biến động vượt ngưỡng (%) trong ngày.

\- \[ ] Thống kê: giá cao nhất/thấp nhất/trung bình theo tuần/tháng (dùng `GROUP BY`).

\- \[ ] Cache kết quả API hay truy vấn (Spring Cache, TTL ngắn).

\- \[ ] Real-time update qua WebSocket khi có giá mới.

\- \[ ] Crawl tin tức tự động từ 1 nguồn công khai (nếu robots.txt cho phép).



\## 4. Yêu cầu phi chức năng (Non-functional Requirements)



\- \*\*Hiệu năng:\*\* API trả lịch sử giá phải có INDEX trên cột thời gian + mã tài sản để tránh full table scan.

\- \*\*Độ tin cậy:\*\* job thu thập dữ liệu lỗi không được làm crash toàn bộ service; phải log lại và retry hợp lý.

\- \*\*Khả năng mở rộng:\*\* thiết kế entity đủ tổng quát để thêm loại tài sản mới (crypto, tỷ giá...) mà không sửa schema lớn.

\- \*\*Testable:\*\* có unit test cho phần logic tính toán (nếu có thống kê/aggregation).



\## 5. Thiết kế dữ liệu (gợi ý)



\*\*Bảng `asset`\*\*

| Cột | Kiểu | Ghi chú |

|---|---|---|

| id | BIGINT PK | |

| code | VARCHAR | mã tài sản, unique (VD: XAU, WTI, VNM) |

| name | VARCHAR | tên hiển thị |

| type | VARCHAR/ENUM | GOLD / OIL / STOCK |



\*\*Bảng `asset\_price`\*\*

| Cột | Kiểu | Ghi chú |

|---|---|---|

| id | BIGINT PK | |

| asset\_id | BIGINT FK | tham chiếu `asset` |

| price\_time | TIMESTAMP | thời điểm ghi nhận giá — \*\*INDEX\*\* |

| open\_price | DECIMAL | |

| close\_price | DECIMAL | |

| high\_price | DECIMAL | |

| low\_price | DECIMAL | |



→ Cân nhắc composite index `(asset\_id, price\_time)` vì đây là pattern query chính.



\*\*Bảng `news`\*\*

| Cột | Kiểu | Ghi chú |

|---|---|---|

| id | BIGINT PK | |

| title | VARCHAR | |

| summary | TEXT | |

| published\_at | TIMESTAMP | |

| source | VARCHAR | tên nguồn (VD: Reuters, Bloomberg...) |

| url | VARCHAR | unique — dùng để check trùng lặp khi insert từ News API |



\*\*Bảng `news\_asset`\*\* (nhiều-nhiều giữa news và asset)

| Cột | Kiểu |

|---|---|

| news\_id | FK |

| asset\_id | FK |



\*\*Bảng `collection\_log`\*\* (log job thu thập dữ liệu)

| Cột | Kiểu | Ghi chú |

|---|---|---|

| id | BIGINT PK | |

| run\_at | TIMESTAMP | |

| status | VARCHAR | SUCCESS / FAILED |

| message | TEXT | lỗi nếu có |



\## 6. Kiến trúc kỹ thuật



\- \*\*Backend:\*\* Java + Spring Boot (Spring Web, Spring Data JPA, Spring Scheduling).

\- \*\*Nguồn tin tức:\*\* NewsAPI.org hoặc GNews API (free tier) — gọi qua `RestTemplate`/`WebClient`, cấu hình API key qua biến môi trường (không hardcode, không commit lên Git).

\- \*\*Database:\*\* PostgreSQL (dùng Flyway để quản lý migration schema).

\- \*\*Đóng gói:\*\* Dockerfile cho app + `docker-compose.yml` chạy app + DB cùng lúc.

\- \*\*CI:\*\* GitHub Actions — chạy `mvn test` + build mỗi khi push code.

\- \*\*Cấu trúc thư mục gợi ý:\*\*

```

src/main/java/.../

├── controller/    # REST controllers

├── service/       # business logic

├── repository/    # Spring Data JPA repositories

├── entity/        # JPA entities

├── dto/           # request/response objects

├── scheduler/      # @Scheduled jobs thu thập dữ liệu

└── config/        # cấu hình (cache, scheduling...)

```



\## 7. Kế hoạch triển khai theo tuần (bám lịch tổng thể)



| Việc | Nội dung |

|---|---|

| Bước 1 | Setup project Spring Boot, kết nối PostgreSQL qua Docker Compose |

| Bước 2 | Thiết kế entity + migration Flyway cho `asset`, `asset\_price` |

| Bước 3 | Viết API CRUD cơ bản cho `asset` + import dữ liệu mẫu (CSV/mock) |

| Bước 4 | Viết `@Scheduled` job giả lập thu thập giá định kỳ + `collection\_log` |

| Bước 5 | API truy vấn lịch sử giá (có phân trang, lọc theo thời gian) |

| Bước 6 | Entity + API cho `news`, liên kết với `asset` |

| Bước 7 | Viết Dockerfile, hoàn thiện docker-compose, setup GitHub Actions CI |

| Bước 8 | Viết unit test cho service quan trọng, hoàn thiện README |



\## 8. Tiêu chí hoàn thành (Definition of Done)



\- \[ ] Chạy được toàn bộ hệ thống bằng 1 lệnh `docker-compose up`.

\- \[ ] Có ít nhất 6 API hoạt động đúng như mô tả ở mục 3.1.

\- \[ ] Có job thu thập dữ liệu chạy tự động, có log lại kết quả.

\- \[ ] Có ít nhất vài unit test cho phần logic (không cần coverage cao).

\- \[ ] CI (GitHub Actions) chạy pass khi push code.

\- \[ ] README mô tả: cách chạy, kiến trúc, ERD, các quyết định thiết kế quan trọng.



\## 9. Câu hỏi phỏng vấn có thể gặp (tự chuẩn bị câu trả lời)



\- Tại sao chọn thiết kế bảng `asset\_price` như vậy? Có tính đến việc dữ liệu tăng rất nhanh theo thời gian không (partitioning)?

\- Nếu job thu thập dữ liệu chạy trùng giờ với lúc DB đang bị quá tải thì xử lý sao?

\- Vì sao dùng index composite `(asset\_id, price\_time)` mà không phải 2 index riêng?

\- Nếu phải chuyển sang dữ liệu real-time (mỗi giây có giá mới), kiến trúc hiện tại có scale được không?

