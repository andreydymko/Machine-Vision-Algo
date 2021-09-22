package com.andreydymko.recoginition1.RecognitionCore;

import android.content.Context;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class RecognitionDatabase {
    private static RecognitionDatabase instance;
    private final Context context;

    private RecognitionDatabase(Context context) {
        this.context = context.getApplicationContext();
    }

    public static RecognitionDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (RecognitionDatabase.class) {
                if (instance == null) {
                    instance = new RecognitionDatabase(context);
                }
            }
        }
        return instance;
    }

    public void saveModel(RecognitionModel model) {
        if (hasModel(model.getName())) {
            try {
                deleteModel(model.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (DataOutputStream dos = new DataOutputStream(context.openFileOutput(model.getName(), Context.MODE_PRIVATE))) {
            dos.writeInt(model.getWidth());
            dos.writeInt(model.getHeight());
            byte[] temp = model.getData().toByteArray();
            dos.writeInt(temp.length);
            dos.write(temp);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteModel(String name) throws IOException {
        File file = new File(context.getFilesDir(), name);
        if (!file.delete()) {
            throw new IOException();
        }
    }

    public RecognitionModel loadModel(String name) {
        RecognitionModel model = new RecognitionModel();
        model.setName(name);
        try (DataInputStream dis = new DataInputStream(context.openFileInput(name))) {
            model.setWidth(dis.readInt());
            model.setHeight(dis.readInt());
            int byteLen = dis.readInt();
            byte[] arr = new byte[byteLen];
            dis.read(arr);
            model.setData(BitSet.valueOf(arr));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }

    public boolean hasModel(String name) {
        return context.getFileStreamPath(name).exists();
    }

    public String[] modelNameList() {
        return context.fileList();
    }

    public List<RecognitionModel> loadModels() {
        List<RecognitionModel> list = new ArrayList<>();
        for (String name : modelNameList()) {
            list.add(loadModel(name));
        }
        return list;
    }
}
