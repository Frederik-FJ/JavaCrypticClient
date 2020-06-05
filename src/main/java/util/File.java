package util;

import Exceptions.InvalidServerResponseException;
import Exceptions.NoDirectoryException;
import Exceptions.UnknownMicroserviceException;
import information.Information;
import items.Device;

import java.util.*;

public class File {

    String parentDirUuid;
    String uuid;
    boolean isDirectory;
    Device device;

    String name = null;

    public File(String uuid, String parentDirUuid, boolean isDirectory, Device device){
        this.isDirectory  = isDirectory;
        this.uuid = uuid;
        this.parentDirUuid = parentDirUuid;
        this.device = device;
    }

    private File(String uuid, String parentDirUuid, boolean isDirectory, Device device, String name){
        this(uuid, parentDirUuid, isDirectory, device);
        this.name = name;
    }

    public String getParentDirUuid() {
        return parentDirUuid;
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

    public String getName() throws InvalidServerResponseException, UnknownMicroserviceException {
        if(name == null)
            this.name = getInfo().get("filename").toString();
        return name;
    }

    public String getContent() throws InvalidServerResponseException, UnknownMicroserviceException {
        return this.getInfo().get("content").toString();
    }

    public List<File> getFiles() throws UnknownMicroserviceException, InvalidServerResponseException, NoDirectoryException {
        if(!isDirectory) throw new NoDirectoryException(this);
        List<String> endpoint = Arrays.asList("file", "all");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", device.getUuid());
        data.put("parent_dir_uuid", this.uuid);
        Map result =  Information.webSocketClient.microservice("device", endpoint, data);
        List<File> fileList = new ArrayList<>();
        for(Map<String, Object> m: (List<Map<String, Object>>)result.get("files")){
            File f = new File(m.get("uuid").toString(),
                    (String) m.get("parent_dir_uuid"),
                    (boolean) m.get("is_directory"),
                    new Device(m.get("device").toString()),
                    m.get("filename").toString());
            fileList.add(f);
        }

        return fileList;
    }

    public void move(String newParentDirUuid, String newName) throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("file", "move");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", this.device.getUuid());
        data.put("file_uuid", this.getUuid());
        data.put("new_parent_dir_uuid", newParentDirUuid);
        data.put("new_filename", newName);
        Information.webSocketClient.microservice("device", endpoint, data);
    }

    public void rename(String newName) throws InvalidServerResponseException, UnknownMicroserviceException {
        this.move(this.parentDirUuid, newName);
    }

    public void moveOnly(String newParentDirUuid) throws UnknownMicroserviceException, InvalidServerResponseException {
        this.move(newParentDirUuid, this.getName());
    }

    public void setContent(String newContent) throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("file", "update");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", this.device.getUuid());
        data.put("file_uuid", this.uuid);
        data.put("content", newContent);
        Information.webSocketClient.microservice("device", endpoint, data);
    }

    public void delete() throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("file", "delete");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", this.device.getUuid());
        data.put("file_uuid", this.uuid);
        Information.webSocketClient.microservice("device", endpoint, data);
    }





    private static File create(Device device, String filename, String content, String parentDirUuid, boolean isDirectory) throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("file", "create");
        Map<String, Object> data = new HashMap<>();
        data.put("device_uuid", device.getUuid());
        data.put("filename", filename);
        data.put("content", content);
        data.put("parent_dir_uuid", parentDirUuid);
        data.put("is_directory", isDirectory);
        Map result = Information.webSocketClient.microservice("device", endpoint, data);
        return new File(result.get("uuid").toString(), (String) result.get("parent_dir_uuid"), (boolean) result.get("is_directory"), new Device(result.get("device").toString()));
    }

    public static File createFile(String filename, String content, String parentDirUuid, Device device) throws InvalidServerResponseException, UnknownMicroserviceException {
        return create(device, filename, content, parentDirUuid, false);
    }

    public static File createDirectory(String name, String parentDirUuid, Device device) throws InvalidServerResponseException, UnknownMicroserviceException {
        return create(device, name, "", parentDirUuid, true);
    }

    public static File getParentDir(File f) throws UnknownMicroserviceException, InvalidServerResponseException {
        if(f.getParentDirUuid() == null || f.getParentDirUuid().equals("")){
            return f.device.getRootDirectory();
        }
        List<String> endpoint = Arrays.asList("file", "info");
        Map<String, String> data = new HashMap<>();
        data.put("file_uuid", f.parentDirUuid);
        data.put("device_uuid", f.device.getUuid());
        Map result =  Information.webSocketClient.microservice("device", endpoint, data);
        return new File(result.get("uuid").toString(), (String) result.get("parent_dir_uuid"), true, f.device);
    }

    public ExecutionFile toExecutionFile(){
        return new ExecutionFile(this);
    }
}
