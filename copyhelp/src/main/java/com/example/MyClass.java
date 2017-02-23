package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyClass {
    final static  String path1="sociallib";
    final static  String path2="jkysactivitybase";
    final static String srcaTargetPackage=path2;

   static Pattern p=Pattern.compile("(R\\.layout\\..*?[\\),])");
    static Pattern p2=Pattern.compile("(@layout/.*?\")");
    static Pattern p3=Pattern.compile("(@drawable/.*?\")");
    static Pattern p4=Pattern.compile("(R\\.drawable\\..*?[\\),])");

    public static void main(String[]arg) throws IOException {
        File file =new File("./"+srcaTargetPackage+"/src/main/java");
        getAllFile(file);
        getAllDrawAbleInjava(file);
         file =new File("./"+srcaTargetPackage+"/src/main/res");
        getAllDrawAble(file);
        getAllFileInRes(file);

    }

    public static void getAllFile(File f) throws IOException {
        if (f.isFile()){
            if (f.getName().contains(".java")) {
                BufferedReader fread = new BufferedReader(new FileReader(f));
                StringBuilder builder = new StringBuilder();
                String str = "";
                while ((str = fread.readLine()) != null) {
                    builder.append(str);
                    builder.append("\n");
                }
                fread.close();
                Matcher matcher = p.matcher(builder.toString());
                   while (matcher.find()) {
                       String res=matcher.group(1);
                       res=res.replace("R.layout.","");
                       res=res.substring(0,res.length()-1);
                       System.out.println(res);
                       String targetPath="/Users/tom/githubs/Social/"+srcaTargetPackage+"/src/main/res/layout/"
                       +res+".xml";
                       File target=new File(targetPath);
                       if (target.exists()) {
                           continue;
                       }
                       String sourcePath="/Users/tom/leipatient/Diabetes_pt_225/src/main/res/layout/"
                               +res+".xml";
                       File source=new File(sourcePath);
                       if (!source.exists()) continue;
                       FileInputStream fis=new FileInputStream(source);
                       byte[] b=new byte[1024];
                       FileOutputStream fos=new FileOutputStream(target);
                      int count;
                       while ((count= fis.read(b))>0){
                           fos.write(b,0,count);
                       }
                       fos.flush();
                       fos.close();
                       fis.close();
                       System.out.println(targetPath+"...ok");
                   }

                return;
            }
        }else {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                getAllFile(files[i]);
            }
        }
    }

    static void checkDrawAble(String dpilevel,String res) throws IOException {
        File source=null;
        File[]drawabls=new File("/Users/tom/leipatient/Diabetes_pt_225/src/main/res/drawable"+dpilevel).listFiles();
        for (int i = 0; i < drawabls.length; i++) {
            if (drawabls[i].getName().split("\\.")[0].trim().equals(res)){
                source=drawabls[i];
            }
        }

        if (source==null||!source.exists()) {
            return;
        }
        String targetPath = "/Users/tom/githubs/Social/"+srcaTargetPackage+"/src/main/res/drawable"+dpilevel+"/"
                + source.getName();
        File target=new File(targetPath);
        if (target.exists()) return;
        FileInputStream fis = new FileInputStream(source);
        byte[] b = new byte[1024];
        FileOutputStream fos = new FileOutputStream(target);
        int count;
        while ((count = fis.read(b)) > 0) {
            fos.write(b, 0, count);
        }
        fos.flush();
        fos.close();
        fis.close();
        System.out.println(targetPath + "..."+dpilevel+"...ok");

    }

    public static void getAllFileInRes(File f) throws IOException{
        if (f.isFile()){
            if (f.getName().contains(".xml")) {
                System.out.println(f.getName());
                BufferedReader fread = new BufferedReader(new FileReader(f));
                StringBuilder builder = new StringBuilder();
                String str = "";
                while ((str = fread.readLine()) != null) {
                    builder.append(str);
                    builder.append("\n");
                }
                fread.close();
                Matcher matcher = p3.matcher(builder.toString());
                while (matcher.find()) {
                    String res = matcher.group(1);
                    res = res.replace("@drawable/", "");
                    res = res.substring(0, res.length() - 1);
                    checkDrawAble("",res);
                    checkDrawAble("-xhdpi",res);
                    checkDrawAble("-xxhdpi",res);


                }

                return;
            }
        }else {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                getAllFileInRes(files[i]);
            }
        }

    }

    public static void getAllDrawAble(File f) throws IOException{
        if (f.isFile()){
            if (f.getName().contains(".xml")) {
                System.out.println(f.getName());
                BufferedReader fread = new BufferedReader(new FileReader(f));
                StringBuilder builder = new StringBuilder();
                String str = "";
                while ((str = fread.readLine()) != null) {
                    builder.append(str);
                    builder.append("\n");
                }
                fread.close();
                Matcher matcher = p2.matcher(builder.toString());
                while (matcher.find()) {
                    String res=matcher.group(1);
                    res=res.replace("@layout/","");
                    res=res.substring(0,res.length()-1);
                    String targetPath="/Users/tom/githubs/Social/"+srcaTargetPackage+"/src/main/res/layout/"
                            +res+".xml";
                    File target=new File(targetPath);
                    if (target.exists()) {
                        continue;
                    }
                    String sourcePath="/Users/tom/leipatient/Diabetes_pt_225/src/main/res/layout/"
                            +res+".xml";
                    File source=new File(sourcePath);
                    if (!source.exists()) return;
                    FileInputStream fis=new FileInputStream(source);
                    byte[] b=new byte[1024];
                    FileOutputStream fos=new FileOutputStream(target);
                    int count;
                    while ((count= fis.read(b))>0){
                        fos.write(b,0,count);
                    }
                    fos.flush();
                    fos.close();
                    fis.close();
                    System.out.println(targetPath+"...ok");
                }

                return;
            }
        }else {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                getAllDrawAble(files[i]);
            }
        }

    }

    public static void getAllDrawAbleInjava(File f) throws IOException{
        if (f.isFile()){
            if (f.getName().contains(".java")) {
                System.out.println(f.getName());
                BufferedReader fread = new BufferedReader(new FileReader(f));
                StringBuilder builder = new StringBuilder();
                String str = "";
                while ((str = fread.readLine()) != null) {
                    builder.append(str);
                    builder.append("\n");
                }
                fread.close();
                Matcher matcher = p4.matcher(builder.toString());
                while (matcher.find()) {
                    String res = matcher.group(1);
                    res = res.replace("R.drawable.", "");
                    res = res.substring(0, res.length() - 1);
                    System.out.println(res);
                    checkDrawAble("",res);
                    checkDrawAble("-xhdpi",res);
                    checkDrawAble("-xxhdpi",res);


                }

                return;
            }
        }else {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                getAllDrawAbleInjava(files[i]);
            }
        }

    }

}
