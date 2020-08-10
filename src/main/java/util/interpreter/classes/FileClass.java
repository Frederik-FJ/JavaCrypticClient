package util.interpreter.classes;

import Exceptions.InvalidServerResponseException;
import Exceptions.file.UnknownFileSourceException;
import util.file.File;
import util.interpreter.annotations.UsableClass;
import util.interpreter.annotations.UsableConstructor;
import util.interpreter.annotations.UsableFunction;
import util.interpreter.annotations.UsableMethod;
import util.items.Device;
import util.path.Path;

import java.util.Objects;


@UsableClass(name = "File")
public class FileClass extends File {

    @UsableConstructor
    public FileClass(String uuid, String parentDirUuid, boolean isDirectory, Device device) {
        super(uuid, parentDirUuid, isDirectory, device);
    }

    public FileClass(File file) {
        super(file.getUuid(), file.getParentDirUuid(), file.isDirectory(), file.getDevice());
    }

    @UsableMethod(name = "getFileByUuid")
    public static FileClass getFileByUuid(String uuid, Device device) throws UnknownFileSourceException {
            return new FileClass(Objects.requireNonNull(File.getFileByUuid(uuid, device)));
    }

    @UsableFunction(name = "createDirectory")
    public static FileClass createDirectory(String name, File parentDir, Device device) throws InvalidServerResponseException {
        return new FileClass(Objects.requireNonNull(File.createDirectory(name, parentDir.getParentDirUuid(), device)));
    }

    @UsableFunction(name = "createDirectory")
    public static FileClass createDirectory(String name, String parentDirUuid, Device device) throws InvalidServerResponseException {
        return new FileClass(Objects.requireNonNull(File.createDirectory(name, parentDirUuid, device)));
    }

    @UsableFunction(name = "createFile")
    public static FileClass createFile(String name, String content, File parentDir, Device device) throws InvalidServerResponseException {
        return new FileClass(Objects.requireNonNull(File.createFile(name, content, parentDir.getUuid(), device)));
    }

    @UsableFunction(name = "createFile")
    public static FileClass createFile(String name, String content, String parentDirUuid, Device device) throws InvalidServerResponseException {
        return new FileClass(Objects.requireNonNull(File.createFile(name, content, parentDirUuid, device)));
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
        return new FileClass(super.getParentDir());
    }

    @Override
    @UsableMethod(name = "getDevice")
    public DeviceClass getDevice() {
        return new DeviceClass(super.getDevice());
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
