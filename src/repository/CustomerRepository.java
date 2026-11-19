package repository;

import model.*;
import utils.IdGenerator;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lưu/đọc danh sách khách hàng từ file data/customers.txt.
 * Mỗi dòng: id|fullName|phone|email|type
 */
public class CustomerRepository {
    private static final String FILE = "data/customers.txt";
    private List<Customer> list = new ArrayList<>();

    public CustomerRepository() {
        File f = new File(FILE);
        if (f.exists()) load();
        else            seedAndSave();
    }

    // ---- Seed data mẫu ----
    private void seedAndSave() {
        list.add(new Customer("KH001", "Nguyen Van An",   "0912345678",
                              "an@gmail.com",   CustomerType.NORMAL));
        list.add(new Customer("KH002", "Tran Thi Binh",   "0987654321",
                              "binh@edu.vn",    CustomerType.STUDENT));
        list.add(new Customer("KH003", "Le Minh Cuong",   "0909111222",
                              "cuong@gmail.com", CustomerType.VIP));
        IdGenerator.setCustomerCounter(4);  // ID tiếp theo bắt đầu từ KH004
        saveAll();
    }

    // ---- Đọc file ----
    private void load() {
        int maxNum = 1;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(FILE), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|");
                list.add(new Customer(p[0], p[1], p[2], p[3],
                                      CustomerType.valueOf(p[4])));
                // Cập nhật counter
                try {
                    int n = Integer.parseInt(p[0].replace("KH", ""));
                    if (n >= maxNum) maxNum = n + 1;
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException e) {
            System.out.println("Loi doc customers.txt: " + e.getMessage());
        }
        IdGenerator.setCustomerCounter(maxNum);
    }

    // ---- Ghi file ----
    public void saveAll() {
        new File("data").mkdirs();
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE), "UTF-8"))) {
            for (Customer c : list) {
                bw.write(c.getId() + "|" + c.getFullName() + "|"
                       + c.getPhone() + "|" + c.getEmail() + "|"
                       + c.getType().name());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi ghi customers.txt: " + e.getMessage());
        }
    }

    // ---- CRUD ----
    public List<Customer> findAll() { return list; }
    public Customer findById(String id) {
        for (Customer c : list) if (c.getId().equals(id)) return c;
        return null;
    }
    public void save(Customer c) { list.add(c); saveAll(); }
}
