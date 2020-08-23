package util.interpreter.classes;

import Exceptions.InvalidServerResponseException;
import Exceptions.NoDirectoryException;
import Exceptions.file.UnknownFileSourceException;
import Exceptions.interpreter.UnknownSourceLocation;
import util.file.File;
import util.interpreter.Interpreter;
import util.interpreter.annotations.UsableClass;
import util.interpreter.annotations.UsableConstructor;
import util.interpreter.annotations.UsableFunction;
import util.interpreter.annotations.UsableMethod;
import util.items.Device;
import util.path.Path;

import java.util.Objects;


@UsableClass(name = "File")
public class FileClass extends File {

    Interpreter interpreter;

    @UsableConstructor
    public FileClass(Interpreter interpreter, String path) throws UnknownSourceLocation {
        this(interpreter, getPathFromString(interpreter.getSourcePath(), path));
    }

    @UsableConstructor
    public FileClass(Interpreter interpreter, Path path) {
        this(interpreter, path.getCurrentFile());
    }

    public FileClass(Interpreter interpreter, File file) {
        super(file.getUuid(), file.getParentDirUuid(), file.isDirectory(), file.getDevice());
        this.interpreter = interpreter;
    }

    private static Path getPathFromString(Path srcPath, String path) throws UnknownSourceLocation {
        Path p;
        try {
            p = Path.getPathFromString(srcPath, path);
        } catch (NoDirectoryException e) {
            throw new UnknownSourceLocation("The source " + path + " is unknown");
        }
        if (p == null) {
            throw new UnknownSourceLocation("The source " + path + " is unknown");
        }
        return p;
    }

    @UsableFunction(name = "getFileByUuid")
    public static FileClass getFileByUuid(Interpreter interpreter, String uuid, Device device) throws UnknownFileSourceException {
            return new FileClass(interpreter, Objects.requireNonNull(File.getFileByUuid(uuid, device)));
    }

    @UsableFunction(name = "createDirectory")
    public static FileClass createDirectory(Interpreter interpreter, String name, File parentDir, Device device) throws InvalidServerResponseException {
        return new FileClass(interpreter, Objects.requireNonNull(File.createDirectory(name, parentDir.getParentDirUuid(), device)));
    }

    @UsableFunction(name = "createDirectory")
    public static FileClass createDirectory(Interpreter interpreter, String name, String parentDirUuid, Device device) throws InvalidServerResponseException {
        return new FileClass(interpreter, Objects.requireNonNull(File.createDirectory(name, parentDirUuid, device)));
    }

    @UsableFunction(name = "createFile")
    public static FileClass createFile(Interpreter interpreter, String name, String content, File parentDir, Device device) throws InvalidServerResponseException {
        return new FileClass(interpreter, Objects.requireNonNull(File.createFile(name, content, parentDir.getUuid(), device)));
    }

    @UsableFunction(name = "createFile")
    public static FileClass createFile(Interpreter interpreter, String name, String content, String parentDirUuid, Device device) throws InvalidServerResponseException {
        return new FileClass(interpreter, Objects.requireNonNull(File.createFile(name, content, parentDirUuid, device)));
    }

    // new create Functions
    @UsableFunction(name = "createFile")
    public static FileClass createFile(Interpreter interpreter, String path, String content) throws InvalidServerResponseException, NoDirectoryException {
        int last = path.lastIndexOf("/");
        String name = path.substring(last + 1);
        if (name.equals("")) {
            throw new RuntimeException();
        }
        Path p = Path.getPathFromString(interpreter.getSourcePath(), path.substring(0, last));
        return new FileClass(interpreter, Objects.requireNonNull(File.createFile(name, content, p.getCurrentFile().getUuid(), interpreter.getSourceDevice())));
    }

    @UsableFunction(name = "createDir")
    public static FileClass createDirectory(Interpreter interpreter, String path) throws NoDirectoryException, InvalidServerResponseException {
        int last = path.lastIndexOf("/");
        String name = path.substring(last + 1);
        if (name.equals("")) {
            throw new RuntimeException();
        }
        Path p = Path.getPathFromString(interpreter.getSourcePath(), path.substring(0, last));
        return new FileClass(interpreter, Objects.requireNonNull(File.createDirectory(name, p.getCurrentFile().getUuid(), interpreter.getSourceDevice())));

    }


    @Override
    @UsableMethod(name = "isDirectory")
    public boolean isDirectory() {
        return super.isDirectory();
    }

    @Override
    @UsableMethod(name = "getUuid")
    public String getUuid() {
        return super.getUuid();
    }

    @Override
    @UsableMethod(name = "getParentDir")
    public FileClass getParentDir() {
        return new FileClass(this.interpreter, super.getParentDir());
    }

    @Override
    @UsableMethod(name = "getDevice")
    public DeviceClass getDevice() {
        return new DeviceClass(interpreter, super.getDevice());
    }

    @Override
    @UsableMethod(name = "getName")
    public String getName() {
        return super.getName();
    }

    @Override
    @UsableMethod(name = "rename")
    public void rename(String newName) {
        super.rename(newName);
    }

    @Override
    @UsableMethod(name = "move")
    public void moveOnly(String newParentDirUuid) {
        super.moveOnly(newParentDirUuid);
    }

    @UsableMethod(name = "move")
    public void moveOnly(File newParentDir) {
        super.moveOnly(newParentDir.getUuid());
    }

    @Override
    @UsableMethod(name = "move")
    public void move(String newParentDirUuid, String newName) {
        super.move(newParentDirUuid, newName);
    }

    @UsableMethod(name = "move")
    public void move(File newParentDir, String newName) {
        super.move(newParentDir.getUuid(), newName);
    }

    @Override
    @UsableMethod(name = "setContent")
    public void setContent(String newContent) throws InvalidServerResponseException {
        super.setContent(newContent);
    }

    @Override
    @UsableMethod(name = "getContent")
    public String getContent() {
        return super.getContent();
    }

    @Override
    @UsableMethod(name = "delete")
    public void delete() {
        super.delete();
    }

    //TODO PathFile for Interpreter, return it
    @Override
    public Path getPath() {
        return super.getPath();
    }

    @Override
    public String toString() {
        return "File:\n\tUUID: " + getUuid() + "\n\tName: " + getName() + "\n\tPath: " + getPath().getPwd();
    }
}
