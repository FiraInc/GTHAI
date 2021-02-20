package com.zostio.myai.training;

import com.zostio.myai.StatUtils;

import java.io.*;

public class SerializationTool {

    public static Object deSerializeObject(String filepath) {
        if (filepath == null) {
            StatUtils.printMessage("Filepath cannot be null");
            return null;
        }

        FileInputStream fis;
        try {
            fis = new FileInputStream(filepath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void serializeObject(String directory, String filename, Object serializeObject) {
        if (directory == null) {
            StatUtils.printMessage("Could not save to folder specified. Folder cannot be null");
            return;
        }
        File directoryOfSave = new File(directory);
        if (!directoryOfSave.exists()) {
            directoryOfSave.mkdirs();
        }

        File networkFile = new File(directoryOfSave.getPath() + File.separator + filename);

        FileOutputStream fout;
        try {
            fout = new FileOutputStream(networkFile.getPath());
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(serializeObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
