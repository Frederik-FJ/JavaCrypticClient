package util;

import Exceptions.InvalidServerResponseException;
import Exceptions.NoDirectoryException;
import Exceptions.UnknownMicroserviceException;
import items.Device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Path {

    private File currentDirectory;
    private String pwd = "/";
    private final Device device;

    public Path(Device device){
        this.device = device;
        try {
            currentDirectory = device.getRootDirectory();
        } catch (UnknownMicroserviceException | InvalidServerResponseException e) {
            e.printStackTrace();
        }
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
        if(path.equals(".") || path.equals("/")){
            currentDirectory = device.getRootDirectory();
            updatePwd();
            return "";
        }
        if(path.equals("..")){
            currentDirectory = File.getParentDir(currentDirectory);
            //System.out.println(gson.toJson(currentDirectory));
            updatePwd();
            return "";
        }
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
        if(dir == null){
            return "No such directory";
        }
        if(!dir.isDirectory()){
            return dir.getName() + " isn't a directory";
        }
        currentDirectory = dir;
        updatePwd();
        return "";
    }

    public String listFiles() throws UnknownMicroserviceException, InvalidServerResponseException {
        try {
            StringBuilder s = new StringBuilder();
            for(File f : this.currentDirectory.getFiles()){
                s.append("[")
                        .append(f.isDirectory()?"DIR":"FILE")
                        .append("]\t")
                        .append(f.getName())
                        .append("\n");
            }
            return s.substring(0, s.length()-1);
        } catch (NoDirectoryException e) {
            e.printStackTrace();
            return Arrays.toString(e.getStackTrace());
        }
    }

    public void updatePwd() throws InvalidServerResponseException, UnknownMicroserviceException {
        if (currentDirectory.getUuid() == null) {
            pwd = "/";
            return;
        }
        List<File> dirs = new ArrayList<>();
        dirs.add(currentDirectory);
        File dir = currentDirectory;
        System.out.println(dir.getUuid());
        while((dir = File.getParentDir(dir)).getUuid() != null){
            System.out.println("UUID: " + dir.getUuid() + "\nParent UUID: " +  dir.getParentDirUuid());
            dirs.add(dir);
        }
        StringBuilder pwdBuilder = new StringBuilder();
        for(int i = dirs.size()-1; i >= 0; i--){
            pwdBuilder.append("/")
                    .append(dirs.get(i).getName());
        }
        pwd = pwdBuilder.toString();
    }

    public String getPwd(){
        return pwd;
    }

    public File getCurrentDirectory(){
        return currentDirectory;
    }
}
