package repository;

import model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lưu/đọc suất chiếu từ file data/showtimes.txt.
 * Mỗi dòng suất: ST|id|movieId|roomName|startTime|endTime|basePrice
 * Mỗi dòng ghế:  SEAT|showtimeId|seatId|number|type|status
 */
public class ShowtimeRepository {
    private static final String FILE = "data/showtimes.txt";
    private List<Showtime> list = new ArrayList<>();
    private final MovieRepository movieRepo;

    public ShowtimeRepository(MovieRepository movieRepo) {
        this.movieRepo = movieRepo;
        File f = new File(FILE);
        if (f.exists()) load();
        else            seedAndSave();
    }

    // ---- Seed data mẫu ----
    private void seedAndSave() {
        Movie mv01 = movieRepo.findById("MV01");
        Movie mv02 = movieRepo.findById("MV02");

        // Suất 1: Phòng 1, Avengers
        Showtime st1 = new Showtime("ST01", mv01, "Phong 1 - 2D",
                                    "19:00 20/11/2026", "21:30 20/11/2026", 85000);
        addSeatsToShowtime(st1, "ST01", 3, 3);  // 3x3 = 9 ghế
        list.add(st1);

        // Suất 2: Phòng 2, Spider-Man
        Showtime st2 = new Showtime("ST02", mv02, "Phong 2 - 3D",
                                    "20:00 20/11/2026", "22:30 20/11/2026", 100000);
        addSeatsToShowtime(st2, "ST02", 2, 3);  // 2x3 = 6 ghế
        // Đặt sẵn 1 ghế để demo
        Seat demo = st2.findSeat("A1");
        if (demo != null) demo.setStatus(SeatStatus.BOOKED);
        list.add(st2);

        saveAll();
    }

    /**
     * Tạo ghế cho suất chiếu: rows hàng x cols cột.
     * Hàng cuối = VIP, hàng cuối 2 = COUPLE (nếu Phòng VIP), còn lại NORMAL.
     */
    private void addSeatsToShowtime(Showtime st, String stId, int rows, int cols) {
        char[] rowNames = {'A','B','C','D','E'};
        for (int r = 0; r < rows; r++) {
            for (int c = 1; c <= cols; c++) {
                String num = rowNames[r] + String.valueOf(c);
                SeatType type;
                if (r == rows - 1) type = SeatType.VIP;
                else if (r == rows - 2 && rows > 2) type = SeatType.COUPLE;
                else type = SeatType.NORMAL;
                st.addSeat(new Seat(stId + "-" + num, num, type, SeatStatus.AVAILABLE));
            }
        }
    }

    // ---- Đọc file ----
    private void load() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(FILE), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|");
                if ("ST".equals(p[0])) {
                    Movie m = movieRepo.findById(p[2]);
                    if (m != null) {
                        Showtime st = new Showtime(p[1], m, p[3], p[4], p[5],
                                                   Double.parseDouble(p[6]));
                        list.add(st);
                    }
                } else if ("SEAT".equals(p[0])) {
                    Showtime st = findById(p[1]);
                    if (st != null) {
                        st.addSeat(new Seat(p[2], p[3],
                            SeatType.valueOf(p[4]), SeatStatus.valueOf(p[5])));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Loi doc showtimes.txt: " + e.getMessage());
        }
    }

    // ---- Ghi file ----
    public void saveAll() {
        new File("data").mkdirs();
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE), "UTF-8"))) {
            for (Showtime st : list) {
                bw.write("ST|" + st.getId() + "|" + st.getMovie().getId() + "|"
                        + st.getRoomName() + "|" + st.getStartTime() + "|"
                        + st.getEndTime() + "|" + (int)st.getBasePrice());
                bw.newLine();
                for (Seat s : st.getSeats()) {
                    bw.write("SEAT|" + st.getId() + "|" + s.getId() + "|"
                           + s.getNumber() + "|" + s.getType().name() + "|"
                           + s.getStatus().name());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Loi ghi showtimes.txt: " + e.getMessage());
        }
    }

    // ---- CRUD ----
    public List<Showtime> findAll() { return list; }
    public Showtime findById(String id) {
        for (Showtime st : list) if (st.getId().equals(id)) return st;
        return null;
    }
    public void update(Showtime st) { saveAll(); } // Lưu lại toàn bộ
}
