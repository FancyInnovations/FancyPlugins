package de.oliver.jdb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * The JDB class provides a simple JSON document-based storage system in a specified directory.
 */
public class JDB {
    public final static Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private static final String FILE_EXTENSION = ".json";
    private final @NotNull String basePath;
    private final @NotNull File baseDirectory;
    private final JIndex index;

    /**
     * Constructs a new JDB instance with the specified base path.
     *
     * @param basePath the base directory path where documents will be stored
     */
    public JDB(@NotNull String basePath) {
        this.basePath = basePath;
        this.baseDirectory = new File(basePath);

        this.index = JIndex.load("jdb_index", basePath);
    }

    /**
     * Retrieves a document from the specified path, deserializing it into the given class type.
     *
     * @param <T>   the type of the object to be returned
     * @param path  the relative path (excluding .json extension) where the document is located
     * @param clazz the class type to which the document should be deserialized
     * @return a JDocument containing the deserialized object and its path, or null if the file does not exist
     * @throws IOException if an I/O error occurs during file reading
     */
    public <T> T get(@NotNull String path, @NotNull Class<T> clazz) throws IOException {
        File documentFile = new File(baseDirectory, createFilePath(path));
        if (!documentFile.exists()) {

            // Check index for alternative path
            if (index.indexMap().containsKey(path)) {
                String indexPath = index.indexMap().get(path);
                return get(indexPath, clazz);
            }

            return null;
        }
        BufferedReader bufferedReader = Files.newBufferedReader(documentFile.toPath());
        return GSON.fromJson(bufferedReader, clazz);
    }

    /**
     * Retrieves a document from the specified path.
     *
     * @param path the relative path (excluding .json extension) where the document is located
     * @return a JDocument containing the deserialized data or null if the file does not exist
     * @throws IOException if an I/O error occurs during file reading
     */
    public JDocument getDocument(@NotNull String path) throws IOException {
        File documentFile = new File(baseDirectory, createFilePath(path));
        if (!documentFile.exists()) {
            return null;
        }
        BufferedReader bufferedReader = Files.newBufferedReader(documentFile.toPath());
        Map<String, Object> data = (Map<String, Object>) GSON.fromJson(bufferedReader, Map.class);
        return new JDocument(data);
    }

    /**
     * Retrieves all documents from the specified directory path, deserializing them into the given class type.
     *
     * @param <T>   the type of objects to be returned
     * @param path  the relative directory path containing the documents
     * @param clazz the class type to which the documents should be deserialized
     * @return a List of JDocument objects containing the deserialized objects and their paths, or null if the directory or files do not exist
     * @throws IOException if an I/O error occurs during file reading
     */
    public <T> List<T> getAll(@NotNull String path, @NotNull Class<T> clazz) throws IOException {
        File directory = new File(baseDirectory, path);
        if (!directory.exists()) {
            return new ArrayList<>();
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        List<T> documents = new ArrayList<>(files.length);
        for (File file : files) {
            documents.add(get(path + "/" + file.getName().replace(FILE_EXTENSION, ""), clazz));
        }
        return documents;
    }

    /**
     * Counts the number of documents in the specified directory path.
     *
     * @param path the relative directory path
     * @return the number of documents in the directory
     */
    public int countDocuments(@NotNull String path) {
        File directory = new File(baseDirectory, path);
        if (!directory.exists()) {
            return 0;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return 0;
        }

        return files.length;
    }

    /**
     * Saves the given value as a document at the specified path.
     *
     * @param <T>   the type of the object to be saved
     * @param path  the relative path (excluding .json extension) where the document will be saved
     * @param value the object to be saved as a JSON document
     * @throws IOException if an I/O error occurs during file writing
     */
    public <T> void set(@NotNull String path, @NotNull T value) throws IOException {
        File documentFile = new File(baseDirectory, createFilePath(path));
        if (!documentFile.exists()) {
            documentFile.getParentFile().mkdirs();
            documentFile.createNewFile();
        }
        String json = GSON.toJson(value);
        Files.write(documentFile.toPath(), json.getBytes());
    }

    /**
     * Saves the given value as a document at the specified path and indexes it under additional paths.
     */
    public <T> void set(@NotNull String path, @NotNull T value, String... indexPaths) throws IOException {
        set(path, value);
        for (String indexPath : indexPaths) {
            indexDocument(indexPath, path);
        }
    }

    /**
     * Indexes a document by mapping the original path to the index path.
     *
     * @param originalPath the original relative path (excluding .json extension) of the document
     * @param indexPath    the index relative path (excluding .json extension) to map to the original document
     */
    public void indexDocument(@NotNull String originalPath, @NotNull String indexPath) {
        index.indexMap().put(originalPath, indexPath);
        index.save();
    }

    /**
     * Deletes the document(s) at the specified path.
     *
     * @param path the relative path (excluding .json extension) of the document(s) to be deleted
     */
    public void delete(@NotNull String path) {
        for (Map.Entry<String, String> entry : new HashSet<>(index.indexMap().entrySet())) {
            if (entry.getKey().equals(path) || entry.getValue().equals(path)) {
                index.indexMap().remove(entry.getKey());
                index.save();
            }
        }

        File file = new File(baseDirectory, path);
        if (file.isDirectory()) {
            deleteDirectory(file);
            return;
        }

        File documentFile = new File(baseDirectory, createFilePath(path));
        if (documentFile.exists()) {
            documentFile.delete();
        }
    }

    /**
     * Creates the file path by appending the base path, provided path, and the file extension.
     *
     * @param path the relative path (excluding .json extension)
     * @return the full file path
     */
    private String createFilePath(@NotNull String path) {
        return path + FILE_EXTENSION;
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else {
                    f.delete();
                }
            }
        }
        directory.delete();
    }
}
