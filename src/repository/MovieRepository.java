package repository;

import model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lưu/đọc danh sách phim từ file data/movies.txt.
 * Mỗi dòng: id|title|genre|duration|ageLimit|status
 */
public class MovieRepository {
    private static final String FILE = "data/movies.txt";
    private List<Movie> list = new ArrayList<>();

    public MovieRepository() {
        File f = new File(FILE);
        if (f.exists()) load();
        else            seedAndSave();
    }

    // ---- Seed data mẫu ----
    private void seedAndSave() {
        list.add(new Movie("MV01", "Avengers: Endgame",   "Hanh dong", 181, 13, MovieStatus.SHOWING));
        list.add(new Movie("MV02", "Spider-Man: NWH",     "Sieu anh hung", 148, 13, MovieStatus.SHOWING));
        list.add(new Movie("MV03", "The Dark Knight",     "Hanh dong", 152, 13, MovieStatus.STOPPED));
        saveAll();
    }

    // ---- Đọc file ----
    private void load() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(FILE), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|");
                list.add(new Movie(p[0], p[1], p[2],
                    Integer.parseInt(p[3]), Integer.parseInt(p[4]),
                    MovieStatus.valueOf(p[5])));
            }
        } catch (IOException e) {
            System.out.println("Loi doc movies.txt: " + e.getMessage());
        }
    }

    // ---- Ghi file ----
    public void saveAll() {
        new File("data").mkdirs();
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE), "UTF-8"))) {
            for (Movie m : list) {
                bw.write(m.getId() + "|" + m.getTitle() + "|" + m.getGenre() + "|"
                       + m.getDuration() + "|" + m.getAgeLimit() + "|"
                       + m.getStatus().name());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi ghi movies.txt: " + e.getMessage());
        }
    }

    // ---- CRUD ----
    public List<Movie> findAll()          { return list; }
    public Movie findById(String id) {
        for (Movie m : list) if (m.getId().equals(id)) return m;
        return null;
    }
}
