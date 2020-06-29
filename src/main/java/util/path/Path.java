package util.path;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import items.Device;
import util.file.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Path {

    protected final Device device;
    protected File currentFile;
    protected String pwd = "/";

    List<PathChangeListener> pathChangeListeners = new ArrayList<>();


    public Path(Device device) {
        this.device = device;
        currentFile = device.getRootDirectory();
    }

    public Path(Path path) {
        this(path.device);
        this.currentFile = path.currentFile;
        this.pwd = path.pwd;
    }

    public Path(File file) {
        this(file.getDevice());
        this.currentFile = file;
        updatePwd();
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
            for(PathChangeListener p: pathChangeListeners){
                p.onPathChanged();
            }
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
            for(PathChangeListener p: pathChangeListeners){
                p.onPathChanged();
            }
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            e.printStackTrace();
        }
    }

    public  List<File> getPathFiles(){
        List<File> dirs = new ArrayList<>();
        dirs.add(currentFile);
        File dir = currentFile;
        try {
            while ((dir = File.getParentDir(dir)).getUuid() != null) {
                dirs.add(dir);
            }
            if(currentFile.getUuid() != null){
                dirs.add(dir);
            }
        } catch (UnknownMicroserviceException | InvalidServerResponseException e) {
            e.printStackTrace();
        }
        Collections.reverse(dirs);
        return dirs;
    }

    public String getPwd() {
        return pwd;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public void addPathChangeListener(PathChangeListener pathChangeListener) {
        this.pathChangeListeners.add(pathChangeListener);
    }

    public static abstract class PathChangeListener {
        public abstract void onPathChanged();
    }


}
