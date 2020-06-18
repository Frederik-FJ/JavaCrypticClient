package util;

import Exceptions.InvalidServerResponseException;
import Exceptions.NoDirectoryException;
import Exceptions.UnknownMicroserviceException;
import items.Device;
import util.file.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Path {

    private File currentDirectory;
    private String pwd = "/";
    private final Device device;

    public Path(Device device){
        this.device = device;
        currentDirectory = device.getRootDirectory();
    }

    public Path(Path path){
        this(path.getDevice());
        this.currentDirectory = path.getCurrentDirectory();
        this.pwd = path.pwd;
    }

    public Device getDevice(){
        return  device;
    }

    public void setDirectory(File f) throws UnknownMicroserviceException, InvalidServerResponseException, NoDirectoryException {
        if(!f.isDirectory()) throw new NoDirectoryException(f);
        this.currentDirectory = f;
        updatePwd();
    }

    public String changeDirectory(String path) throws UnknownMicroserviceException, InvalidServerResponseException {
        // change to root-Directory if path is . or /
        if(path.equals(".") || path.equals("/")){
            currentDirectory = device.getRootDirectory();
            updatePwd();
            return "";
        }
        // go on step back if path is ..
        if(path.equals("..")){
            currentDirectory = File.getParentDir(currentDirectory);
            updatePwd();
            return "";
        }
        // search the folder with the name in the actual path
        File dir = null;
        try {
            for(File f: currentDirectory.getFiles()){
                if(path.equals(f.getName())){
                    dir = f;
                    break;
                }
            }
        } catch (NoDirectoryException e) {
            e.printStackTrace();
        }
        // handle errors
        if(dir == null){
            return "No such directory";
        }
        if(!dir.isDirectory()){
            return dir.getName() + " isn't a directory";
        }
        // finally
        currentDirectory = dir;
        updatePwd();
        return "";
    }

    /**
     *  List files from the actual path
     * @return String of files/dirs in the actual path
     */
    public String listFiles(){
        try {
            StringBuilder s = new StringBuilder();
            // list files from the actual directory
            for(File f : this.currentDirectory.getFiles()){
                s.append("[")
                        .append(f.isDirectory()?"DIR":"FILE")
                        .append("]\t")
                        .append(f.getName())
                        .append("\n");
            }
            if(s.length() == 0){
                return "";
            }
            return s.substring(0, s.length()-1);
        } catch (NoDirectoryException | UnknownMicroserviceException | InvalidServerResponseException e) {
            e.printStackTrace();
            return Arrays.toString(e.getStackTrace());
        }
    }

    /**
     * Updates the pwd variable
     */
    public void updatePwd(){
        if (currentDirectory.getUuid() == null) {
            pwd = "/";
            return;
        }
        List<File> dirs = new ArrayList<>();
        dirs.add(currentDirectory);
        File dir = currentDirectory;
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
        } catch (InvalidServerResponseException | UnknownMicroserviceException e){
            e.printStackTrace();
        }
    }

    public String getPwd(){
        return pwd;
    }

    public File getCurrentDirectory(){
        return currentDirectory;
    }
}
