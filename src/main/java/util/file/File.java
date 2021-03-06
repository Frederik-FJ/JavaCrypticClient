package util.file;

import Exceptions.InvalidServerResponseException;
import Exceptions.NoDirectoryException;
import Exceptions.file.UnknownFileSourceException;
import Exceptions.UnknownMicroserviceException;
import com.google.gson.Gson;
import information.Information;
import util.items.Device;
import util.path.Path;

import javax.swing.*;
import java.util.*;
import java.util.Timer;

public class File {

    String parentDirUuid;
    String uuid;
    boolean isDirectory;
    Device device;

    String name = null;

    static int createdFiles = 0;
    static final int unit = 60000; // 1 Minute
    static final int maxFilesPerUnit = 5;

    public File(String uuid, String parentDirUuid, boolean isDirectory, Device device) {
        this.isDirectory = isDirectory;
        this.uuid = uuid;
        this.parentDirUuid = parentDirUuid;
        this.device = device;
    }

    private File(String uuid, String parentDirUuid, boolean isDirectory, Device device, String name) {
        this(uuid, parentDirUuid, isDirectory, device);
        this.name = name;
    }

    private static File create(Device device, String filename, String content, String parentDirUuid, boolean isDirectory) throws UnknownMicroserviceException, InvalidServerResponseException {
        if (createdFiles >= maxFilesPerUnit) {
            JOptionPane.showInternalMessageDialog(Information.Desktop, "You created already 5 files in the last minute. Just wait a little bit to create the next file.");
            throw new RuntimeException("Zu viele Dateien erstellt.");
        }

        List<String> endpoint = Arrays.asList("file", "create");
        Map<String, Object> data = new HashMap<>();
        data.put("device_uuid", device.getUuid());
        data.put("filename", filename);
        data.put("content", content);
        data.put("parent_dir_uuid", parentDirUuid);
        data.put("is_directory", isDirectory);
        Map<?, ?> result = Information.webSocketClient.microservice("device", endpoint, data);

        createdFiles++;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                createdFiles--;
            }
        }, unit);


        return new File(result.get("uuid").toString(), (String) result.get("parent_dir_uuid"), (boolean) result.get("is_directory"), new Device(result.get("device").toString()));
    }

    public static File createFile(String filename, String content, String parentDirUuid, Device device) throws InvalidServerResponseException {
        try {
            return create(device, filename, content, parentDirUuid, false);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File createFile(String filename, String content, Path path, Device device) throws InvalidServerResponseException {
        try {
            return create(device, filename, content, path.getCurrentFile().getUuid(), false);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File createDirectory(String name, String parentDirUuid, Device device) throws InvalidServerResponseException {
        try {
            return create(device, name, "", parentDirUuid, true);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getFileByUuid(String uuid, Device device) throws UnknownFileSourceException {
        Gson gson = new Gson();
        List<String> endpoint = Arrays.asList("file", "info");
        Map<String, String> data = new HashMap<>();
        data.put("file_uuid", uuid);
        data.put("device_uuid", device.getUuid());
        try {
            Map response = Information.webSocketClient.microservice("device", endpoint, data);
            String parentDir;
            try {
                parentDir = response.get("parent_dir_uuid").toString();
            } catch (NullPointerException e) {
                parentDir = null;
            }
            return new File(response.get("uuid").toString(),
                    parentDir,
                    (boolean) response.get("is_directory"),
                    new Device(response.get("device").toString()),
                    response.get("filename").toString());
        } catch (InvalidServerResponseException e) {
            if(gson.toJson(e.getResponse()).contains("file_not_found")){
                throw new UnknownFileSourceException(uuid);
            }
            e.printStackTrace();
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getParentDir(File f) throws UnknownMicroserviceException, InvalidServerResponseException {
        if (f.getParentDirUuid() == null || f.getParentDirUuid().equals("")) {
            return f.device.getRootDirectory();
        }
        List<String> endpoint = Arrays.asList("file", "info");
        Map<String, String> data = new HashMap<>();
        data.put("file_uuid", f.parentDirUuid);
        data.put("device_uuid", f.device.getUuid());
        Map result = Information.webSocketClient.microservice("device", endpoint, data);
        return new File(result.get("uuid").toString(), (String) result.get("parent_dir_uuid"), true, f.device);
    }

    public String getParentDirUuid() {
        return parentDirUuid;
    }

    public Device getDevice() {
        return device;
    }

    public File getParentDir() {
        if (this.getParentDirUuid() == null || this.getParentDirUuid().equals("")) {
            return this.device.getRootDirectory();
        }
        List<String> endpoint = Arrays.asList("file", "info");
        Map<String, String> data = new HashMap<>();
        data.put("file_uuid", this.parentDirUuid);
        data.put("device_uuid", this.device.getUuid());
        Map result = null;
        try {
            result = Information.webSocketClient.microservice("device", endpoint, data);
            return new File(result.get("uuid").toString(), (String) result.get("parent_dir_uuid"), true, this.device);
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getUuid() {
        return uuid;
    }

    public Map getInfo() throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("file", "info");
        Map<String, String> data = new HashMap<>();
        data.put("file_uuid", this.uuid);
        data.put("device_uuid", device.getUuid());
        return Information.webSocketClient.microservice("device", endpoint, data);
    }

    public String getName() {
        if(uuid == null){
            return null;
        }
        if (name == null) {
            try {
                this.name = getInfo().get("filename").toString();
            } catch (UnknownMicroserviceException | InvalidServerResponseException e) {
                e.printStackTrace();
            }
        }
        return name;
    }

    public String getContent() {
        try {
            return this.getInfo().get("content").toString();
        } catch (UnknownMicroserviceException | InvalidServerResponseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setContent(String newContent) throws InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("file", "update");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", this.device.getUuid());
        data.put("file_uuid", this.uuid);
        data.put("content", newContent);
        try {
            Information.webSocketClient.microservice("device", endpoint, data);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
        }
    }

    public List<File> getFiles() throws UnknownMicroserviceException, InvalidServerResponseException, NoDirectoryException {
        if (!isDirectory) throw new NoDirectoryException(this);
        List<String> endpoint = Arrays.asList("file", "all");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", device.getUuid());
        data.put("parent_dir_uuid", this.uuid);
        Map result = Information.webSocketClient.microservice("device", endpoint, data);
        List<File> fileList = new ArrayList<>();
        for (Map<String, Object> m : (List<Map<String, Object>>) result.get("files")) {
            File f = new File(m.get("uuid").toString(),
                    (String) m.get("parent_dir_uuid"),
                    (boolean) m.get("is_directory"),
                    new Device(m.get("device").toString()),
                    m.get("filename").toString());
            fileList.add(f);
        }

        return fileList;
    }

    public void move(String newParentDirUuid, String newName) {
        List<String> endpoint = Arrays.asList("file", "move");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", this.device.getUuid());
        data.put("file_uuid", this.getUuid());
        data.put("new_parent_dir_uuid", newParentDirUuid);
        data.put("new_filename", newName);
        try {
            Information.webSocketClient.microservice("device", endpoint, data);
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            e.printStackTrace();
        }
    }

    public void rename(String newName) {
        this.move(this.parentDirUuid, newName);
    }

    public void moveOnly(String newParentDirUuid) {
        this.move(newParentDirUuid, this.getName());
    }

    public void delete() {
        List<String> endpoint = Arrays.asList("file", "delete");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", this.device.getUuid());
        data.put("file_uuid", this.uuid);
        try {
            Information.webSocketClient.microservice("device", endpoint, data);
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            e.printStackTrace();
        }
    }

    public Path getPath() {
        Path p = new Path(device);
        p.setPath(this);
        return p;
    }

    public ExecutionFile toExecutionFile() {
        return new ExecutionFile(this);
    }
}
