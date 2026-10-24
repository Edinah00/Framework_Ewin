package mg.itu.FrameworkEwin.annotations.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileUploads {
    // nom du champ du formulaire -> fichiers recus
    private Map<String, List<FileManager>> files;

    public FileUploads(Map<String, List<FileManager>> files) {
        this.files = files;
    }

    public Map<String, List<FileManager>> getFiles() { return files; }

    public List<FileManager> get(String champ) {
        return files.getOrDefault(champ, new ArrayList<>());
    }

    public List<FileManager> all() {
        List<FileManager> all = new ArrayList<>();
        files.values().forEach(all::addAll);
        return all;
    }
}
