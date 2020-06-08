package util.file;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import gui.apps.terminal.Terminal;
import information.Information;
import items.Device;

public class ExecutionFile extends File {

    public ExecutionFile(String uuid, String parentDirUuid, boolean isDirectory, Device device) {
        super(uuid, parentDirUuid, isDirectory, device);
    }

    public ExecutionFile(File file){
        super(file.uuid, file.parentDirUuid, file.isDirectory, file.device);

    }

    /**
     * opens the Terminal and execute the commands from the file
     */
    public void execute(){
        Terminal ter = Information.Desktop.startTerminal();
        try {
            for(String line: this.getContent().split("\n")){
                ter.getCommandArea().execute(line);
            }
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ExecutionFile toExecutionFile() {
        return this;
    }
}
