package util.path;

import Exceptions.InvalidServerResponseException;
import Exceptions.NoDirectoryException;
import Exceptions.UnknownMicroserviceException;
import items.Device;
import util.file.File;

import java.util.Arrays;

public class DirectoryPath extends Path {


    public DirectoryPath(Device device) {
        super(device);
    }

    public DirectoryPath(Path path) throws NoDirectoryException {
        super(path);
        if (!path.getCurrentFile().isDirectory()) {
            try {
                throw new NoDirectoryException(path.getCurrentFile());
            } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
                e.printStackTrace();
            }
        }
    }

    public DirectoryPath(DirectoryPath path) {
        super(path);
    }

    public void setDirectory(File f) throws NoDirectoryException {
        if (!f.isDirectory()) {
            try {
                throw new NoDirectoryException(f);
            } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
                e.printStackTrace();
            }
            super.setPath(f);
        }
    }

    @Deprecated
    @Override
    public void setPath(File f) {
        super.setPath(f);
    }

    public String changeDirectory(String path) throws UnknownMicroserviceException, InvalidServerResponseException {
        // change to root-Directory if path is . or /
        if (path.equals(".") || path.equals("/")) {
            currentFile = device.getRootDirectory();
            updatePwd();
            return "";
        }
        // go on step back if path is ..
        if (path.equals("..")) {
            currentFile = File.getParentDir(currentFile);
            updatePwd();
            return "";
        }
        // search the folder with the name in the actual path
        File dir = null;
        try {
            for (File f : currentFile.getFiles()) {
                if (path.equals(f.getName())) {
                    dir = f;
                    break;
                }
            }
        } catch (NoDirectoryException e) {
            e.printStackTrace();
        }
        // handle errors
        if (dir == null) {
            return "No such directory";
        }
        if (!dir.isDirectory()) {
            return dir.getName() + " isn't a directory";
        }
        // finally
        currentFile = dir;
        updatePwd();
        return "";
    }

    /**
     * List files from the actual path
     *
     * @return String of files/dirs in the actual path
     */
    public String listFiles() {
        try {
            StringBuilder s = new StringBuilder();
            // list files from the actual directory
            for (File f : this.currentFile.getFiles()) {
                s.append("[")
                        .append(f.isDirectory() ? "DIR" : "FILE")
                        .append("]\t")
                        .append(f.getName())
                        .append("\n");
            }
            if (s.length() == 0) {
                return "";
            }
            return s.substring(0, s.length() - 1);
        } catch (NoDirectoryException | UnknownMicroserviceException | InvalidServerResponseException e) {
            e.printStackTrace();
            return Arrays.toString(e.getStackTrace());
        }
    }


}
