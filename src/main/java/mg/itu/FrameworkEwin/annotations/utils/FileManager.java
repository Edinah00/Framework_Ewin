package mg.itu.FrameworkEwin.annotations.utils;

public class FileManager {
    private String filename;
    private String extension;
    private byte[] bytes;

    public FileManager() {
    }

    public FileManager(String filename, String extension, byte[] bytes) {
        this.filename = filename;
        this.extension = extension;
        this.bytes = bytes;
    }

    // separe "photo.png" en filename "photo" et extension "png"
    public static FileManager from(String submittedName, byte[] bytes) {
        int dot = submittedName.lastIndexOf('.');
        String name = dot > 0 ? submittedName.substring(0, dot) : submittedName;
        String ext = dot > 0 ? submittedName.substring(dot + 1) : "";
        return new FileManager(name, ext, bytes);
    }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }
    public byte[] getBytes() { return bytes; }
    public void setBytes(byte[] bytes) { this.bytes = bytes; }
}
