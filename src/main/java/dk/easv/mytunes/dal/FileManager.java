package dk.easv.mytunes.dal;

import java.io.File;
import java.io.IOException;

public class FileManager {

    public boolean deleteFile(String filePath) {
        File myFile = new File(filePath);
        return myFile.delete();
    }
}
