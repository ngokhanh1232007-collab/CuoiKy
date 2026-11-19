# Hệ Thống Quản Lý Mua Vé Xem Phim Cho Sinh Viên (Nhóm 06)

> **Môn học:** Lập trình Hướng đối tượng (Java OOP)  
> **Đề tài:** Nhóm 06 - Hệ thống quản lý mua vé xem phim cho sinh viên  
> **Ngôn ngữ:** Java (Console Terminal Application)
> **Tác giả:** Ngo Hoang Khanh, Nguyen Duy Khanh, Nguyen Quang Tu

---

## 📁 Cấu Trúc Dự Án (Chuẩn 5 Package)

```
OOPCuoiKyNew/
├── src/
│   ├── model/                     # Các class dữ liệu & interface OOP
│   │   ├── User.java              # Abstract Base Class (Encapsulation, Abstraction)
│   │   ├── Customer.java          # Kế thừa User (Inheritance)
│   │   ├── CustomerType.java      # Enum: NORMAL, STUDENT, VIP
│   │   ├── Movie.java             # Phim (Mã, tên, thể loại, thời lượng, độ tuổi, trạng thái)
│   │   ├── MovieStatus.java       # Enum: SHOWING (Đang chiếu), STOPPED (Ngừng chiếu)
│   │   ├── Seat.java              # Ghế ngồi
│   │   ├── SeatType.java          # Enum: NORMAL (+0đ), VIP (+20.000đ), COUPLE (+50.000đ)
│   │   ├── SeatStatus.java        # Enum: AVAILABLE (Trống), BOOKED (Đã đặt)
│   │   ├── Showtime.java          # Suất chiếu (Phim, phòng, giờ chiếu String, danh sách ghế)
│   │   ├── Ticket.java            # Vé xem phim (Khách, suất, ghế, giá, trạng thái thanh toán)
│   │   ├── TicketPricePolicy.java # Interface chính sách giá (Polymorphism)
│   │   ├── NormalPrice.java       # Triển khai giá khách thường (0% giảm)
│   │   ├── StudentPrice.java      # Triển khai giá sinh viên (giảm 20%)
│   │   ├── VipPrice.java          # Triển khai giá thành viên VIP (giảm 15%)
│   │   ├── PaymentMethod.java     # Interface thanh toán (Polymorphism)
│   │   ├── CashPayment.java       # Triển khai thanh toán tiền mặt
│   │   ├── BankTransferPayment.java # Triển khai chuyển khoản ngân hàng
│   │   └── EWalletPayment.java    # Triển khai ví điện tử MoMo
│   │
│   ├── service/                   # Xử lý logic nghiệp vụ
│   │   ├── BookingException.java  # Exception nghiệp vụ duy nhất (gọn gàng, dễ hiểu)
│   │   ├── MovieService.java      # Quản lý xem & tìm kiếm phim
│   │   ├── CustomerService.java   # Quản lý khách hàng & chọn chính sách giá
│   │   ├── ShowtimeService.java   # Quản lý suất chiếu & in sơ đồ ghế trực quan
│   │   └── BookingService.java    # Logic đặt vé, tính giá, thanh toán, kiểm tra business rules
│   │
│   ├── repository/                # Đọc/Ghi dữ liệu File I/O (.txt với ngăn cách '|')
│   │   ├── MovieRepository.java   # Đọc/ghi movies.txt & khởi tạo 3 phim mẫu
│   │   ├── CustomerRepository.java# Đọc/ghi customers.txt & khởi tạo 3 khách hàng mẫu
│   │   ├── ShowtimeRepository.java# Đọc/ghi showtimes.txt & khởi tạo 2 suất chiếu kèm sơ đồ ghế
│   │   └── TicketRepository.java  # Đọc/ghi tickets.txt (lưu vé đã đặt & trạng thái thanh toán)
│   │
│   ├── utils/                     # Tiện ích dùng chung
│   │   ├── IdGenerator.java       # Tự động sinh mã vé (TK001...), mã KH (KH001...)
│   │   └── Input.java             # Đọc dữ liệu Scanner an toàn, chống crash chương trình
│   │
│   └── main/
│       └── Main.java              # Điều khiển Menu Console (KHÔNG nhồi nhét logic nghiệp vụ)
│
├── data/                          # Thư mục lưu dữ liệu text file (tự tạo nếu chưa có)
│   ├── movies.txt
│   ├── customers.txt
│   ├── showtimes.txt
│   └── tickets.txt
├── out/                           # Thư mục chứa file .class sau khi biên dịch
├── build.bat                      # Script biên dịch và chạy nhanh trên Windows
└── README.md
```

---

##  4 Tính Chất OOP Được Thể Hiện Rõ Ràng

| Tính chất OOP | Thể hiện trong Source Code |
|---|---|
| **Encapsulation (Đóng gói)** | Tất cả thuộc tính trong các model đều đặt `private`, cung cấp đầy đủ constructor và getter/setter để kiểm soát truy cập dữ liệu. |
| **Inheritance (Kế thừa)** | `Customer` kế thừa từ lớp cha `User` (`super(id, fullName, phone, email)`), tái sử dụng mã nguồn và mở rộng thêm thuộc tính loại khách hàng. |
| **Abstraction (Trừu tượng)** | Lớp cơ sở trừu tượng `User` với phương thức abstract `getRole()`. Hai interface `TicketPricePolicy` và `PaymentMethod` định nghĩa hành vi chung. |
| **Polymorphism (Đa hình)** | <br>1. **Chính sách giá:** `TicketPricePolicy` được triển khai bởi `NormalPrice` (giá gốc), `StudentPrice` (giảm 20%), `VipPrice` (giảm 15%). Gọi chung qua hàm `policy.calculate(...)`.<br>2. **Thanh toán:** `PaymentMethod` được triển khai bởi `CashPayment`, `BankTransferPayment`, `EWalletPayment`. Gọi chung qua hàm `method.pay(...)`. |

---

##  Các Quy Tắc Nghiệp Vụ (Business Rules)

1. **Phim ngừng chiếu:** Ném `BookingException` khi đặt vé cho phim có trạng thái `STOPPED`.
2. **Suất chiếu:** Kiểm tra suất chiếu tồn tại trước khi cho phép đặt vé.
3. **Ghế ngồi:** Ném `BookingException` nếu ghế không tồn tại hoặc ghế đã được đặt trước đó (`BOOKED`).
4. **Phụ phí ghế:** Ghế VIP phụ phí +20.000đ, Ghế Đôi phụ phí +50.000đ so với giá cơ bản.
5. **Ưu đãi giá:** Sinh viên giảm 20%, Thành viên VIP giảm 15%. Giá vé cuối cùng luôn lớn hơn 0.
6. **Cập nhật trạng thái:** Khi đặt vé thành công, ghế chuyển thành `BOOKED` và lưu vào file. Khi thanh toán thành công, vé chuyển thành `PAID` và lưu vào file.
7. **Thanh toán:** Không cho phép thanh toán vé đã thanh toán trước đó.



##  Danh Sách Chức Năng Menu Console
1. **[1] Xem / Tìm kiếm phim:** Xem danh sách phim đang chiếu, toàn bộ phim hoặc tìm kiếm theo tên.
2. **[2] Xem suất chiếu & sơ đồ ghế:** Xem danh sách suất chiếu, lọc theo phim và hiển thị sơ đồ ghế ma trận trực quan (`[ ]` ghế trống, `[X]` đã đặt, kèm ký hiệu Thường `N`, VIP `V`, Đôi `C`).
3. **[3] Quản lý khách hàng:** Xem danh sách khách hàng và thêm khách hàng mới (tự động sinh mã `KH00x`, kiểm tra trùng số điện thoại).
4. **[4] Đặt vé xem phim:** Chọn khách hàng -> Chọn phim -> Chọn suất chiếu -> Xem sơ đồ & chọn ghế -> Xem chi tiết tính giá và ưu đãi -> Xác nhận đặt vé -> Lưu vé và cập nhật ghế `BOOKED`.
5. **[5] Thanh toán vé:** Liệt kê các vé chưa thanh toán -> Chọn mã vé -> Chọn hình thức (Tiền mặt / Chuyển khoản / Ví điện tử) -> Xác nhận thanh toán -> Cập nhật trạng thái `PAID`.
6. **[6] Xem danh sách vé đã đặt:** Bảng tổng hợp tất cả vé, trạng thái thanh toán và doanh thu thực tế.
7. **[0] Thoát chương trình.**
"# OOPcuoiKy" 
"# OOPcuoiKy" 
