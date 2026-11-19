package repository;

import model.*;
import utils.IdGenerator;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lưu/đọc danh sách vé từ file data/tickets.txt.
 * Mỗi dòng: id|customerId|showtimeId|seatNumber|finalPrice|paid|paymentMethod
 */
public class TicketRepository {
    private static final String FILE = "data/tickets.txt";
    private List<Ticket> list = new ArrayList<>();

    private final CustomerRepository  customerRepo;
    private final ShowtimeRepository  showtimeRepo;

    public TicketRepository(CustomerRepository customerRepo,
                            ShowtimeRepository showtimeRepo) {
        this.customerRepo = customerRepo;
        this.showtimeRepo = showtimeRepo;
        File f = new File(FILE);
        if (f.exists()) load();
    }

    // ---- Đọc file ----
    private void load() {
        int maxNum = 1;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(FILE), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", 7);
                Customer c  = customerRepo.findById(p[1]);
                Showtime st = showtimeRepo.findById(p[2]);
                if (c == null || st == null) continue;

                Seat seat = st.findSeat(p[3]);
                if (seat == null) continue;

                double price = Double.parseDouble(p[4]);
                boolean paid = Boolean.parseBoolean(p[5]);
                String method = "null".equals(p[6]) ? null : p[6];

                Ticket t = new Ticket(p[0], c, st, seat, price);
                if (paid) t.markPaid(method);
                list.add(t);

                // Cập nhật counter
                try {
                    int n = Integer.parseInt(p[0].replace("TK", ""));
                    if (n >= maxNum) maxNum = n + 1;
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException e) {
            System.out.println("Loi doc tickets.txt: " + e.getMessage());
        }
        IdGenerator.setTicketCounter(maxNum);
    }

    // ---- Ghi file ----
    public void saveAll() {
        new File("data").mkdirs();
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE), "UTF-8"))) {
            for (Ticket t : list) {
                bw.write(t.getId() + "|"
                       + t.getCustomer().getId() + "|"
                       + t.getShowtime().getId() + "|"
                       + t.getSeat().getNumber() + "|"
                       + (int)t.getFinalPrice() + "|"
                       + t.isPaid() + "|"
                       + (t.getPaymentMethod() == null ? "null" : t.getPaymentMethod()));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi ghi tickets.txt: " + e.getMessage());
        }
    }

    // ---- CRUD ----
    public List<Ticket> findAll() { return list; }
    public Ticket findById(String id) {
        for (Ticket t : list) if (t.getId().equals(id)) return t;
        return null;
    }
    public void save(Ticket t)   { list.add(t); saveAll(); }
    public void update(Ticket t) { saveAll(); }
}
