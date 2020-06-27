package util.path;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import items.Device;
import util.file.File;

import java.util.ArrayList;
import java.util.List;

public class Path {

    protected final Device device;
    protected File currentFile;
    protected String pwd = "/";


    public Path(Device device) {
        this.device = device;
        currentFile = device.getRootDirectory();
    }

    public Path(Path path) {
        this(path.device);
        this.currentFile = path.currentFile;
        this.pwd = path.pwd;
    }

    public Device getDevice() {
        return device;
    }

    public void setPath(File f) {
        if (f == null) {
            throw new NullPointerException();
        }
        this.currentFile = f;
        updatePwd();
    }

    /**
     * Updates the pwd variable
     */
    public void updatePwd() {
        if (currentFile.getUuid() == null) {
            pwd = "/";
            return;
        }
        List<File> dirs = new ArrayList<>();
        dirs.add(currentFile);
        File dir = currentFile;
        try {
            // while dir has parent-dir add the parent dir to the path
            while ((dir = File.getParentDir(dir)).getUuid() != null) {
                dirs.add(dir);
            }
            StringBuilder pwdBuilder = new StringBuilder();

            // go backwards through the dir-list to have the first dir at the first place
            for (int i = dirs.size() - 1; i >= 0; i--) {
                pwdBuilder.append("/")
                        .append(dirs.get(i).getName());
            }
            pwd = pwdBuilder.toString();
            if (currentFile.isDirectory()) {
                pwd += "/";
            }
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            e.printStackTrace();
        }
    }

    public String getPwd() {
        return pwd;
    }

    public File getCurrentFile() {
        return currentFile;
    }


}
