package util;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import gui.apps.Terminal;
import information.Information;
import items.Device;

public class ExecutionFile extends File{

    public ExecutionFile(String uuid, String parentDirUuid, boolean isDirectory, Device device) {
        super(uuid, parentDirUuid, isDirectory, device);
    }

    public ExecutionFile(File file){
        super(file.uuid, file.parentDirUuid, file.isDirectory, file.device);

    }

    public void execute() throws UnknownMicroserviceException, InvalidServerResponseException {
        Terminal ter = Information.Desktop.startTerminal();
        for(String line: this.getContent().split("\n")){
            ter.getCommandArea().execute(line);
        }
    }

    @Override
    public ExecutionFile toExecutionFile() {
        return this;
    }
}
