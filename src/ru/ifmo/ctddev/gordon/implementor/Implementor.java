package ru.ifmo.ctddev.gordon.implementor;


import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by Freemahn on 11.03.2015.
 */
public class Implementor implements Impler {


    @Override
    public void implement(Class<?> token, File root) throws ImplerException {
        File outFile;
        String source;
        String FS = System.getProperty("file.separator");
        if (!token.isInterface())
            throw new ImplerException();
        outFile = new File(root.getPath() + FS + token.getPackage().getName().replace(".", FS) + FS + token.getSimpleName() + "Impl.java");
        File f = outFile.getParentFile();
        f.mkdirs();
        FileOutputStream fw = null;
        source = "package " + token.getPackage().getName() + ";\n";
        source += genModifiers(token.getModifiers());
        source += " class " + token.getSimpleName() + "Impl";
        source += " implements " + token.getCanonicalName() + "{\n";
        for (Method m : token.getMethods()) {
            source += genModifiers(m.getModifiers());
            source += m.getAnnotatedReturnType().getType().getTypeName() + " ";
            source += m.getName() + "(";
            source += genMethodParameters(m) + ")";
            source += genMethodThrows(m);
            source += " {\n" + genMethodReturn(m) + "\n}\n";
        }
        source += "}";
        try {
            fw = new FileOutputStream(outFile);
            fw.write(source.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String genModifiers(int mod) {
        String result = "";
        if (Modifier.isPublic(mod))
            result += "public ";
        if (Modifier.isStatic(mod))
            result += "static ";
        return result;
    }

    private String genMethodParameters(Method m) {
        String result = "";
        if (m.getParameters().length == 0)
            return result;
        for (Object o : m.getParameters()) {
            result += o.toString() + ",";
        }
        return result.substring(0, result.length() - 1);

    }

    private String genMethodThrows(Method m) {
        if (m.getAnnotatedExceptionTypes().length == 0) return "";
        String result = "throws ";
        for (Class aClass : m.getExceptionTypes()) {
            result += aClass.getCanonicalName() + ",";
        }
        return result.substring(0, result.length() - 1);
    }

    private String genMethodReturn(Method m) {
        String className = m.getAnnotatedReturnType().getType().getTypeName();
        String result = "return ";
        if (className.equals("void"))
            return "";
        else if (className.equals("long") || className.equals("int") || className.equals("byte"))
            result += "0";
        else if (className.equals("double") || className.equals("float"))
            result += "0.0";
        else if (className.equals("boolean"))
            result += "false";
        else
            result += "null";
        return result + ";";
    }

}
