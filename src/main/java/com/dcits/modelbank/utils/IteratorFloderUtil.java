package com.dcits.modelbank.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created on 2017-11-10 17:48.
 *
 * @author kevin
 */
public class IteratorFloderUtil {
    private void getFileItem(String floderPath) {

    }

    private void findFloderFile(String floderPath,List<HashMap<String, Object>> list){
        File nodeRootFile = new File(floderPath);
        File[] files = nodeRootFile.listFiles();
        for (File file: files) {
            if(file.isDirectory()) {
                //Todo
            }
            HashMap<String,Object> fileMap= new HashMap<String,Object>();
            fileMap.put("INTERFACE_PATH", file.getAbsolutePath());
            fileMap.put("INTERFACE_NAME", file.getName());
            list.add(fileMap);
        }
    }

    private void findFloderAll(String floderPath,List<HashMap<String, Object>> list){
        File nodeRootFile = new File(floderPath);
        File[] files = nodeRootFile.listFiles();
        for (File file: files) {
            if(file.isDirectory()) {
                HashMap<String,Object> fileMap= new HashMap<String,Object>();
                fileMap.put("INTERFACE_PATH", file.getAbsolutePath());
                fileMap.put("INTERFACE_NAME", file.getName());
                list.add(fileMap);
            }
        }
    }

    public static void main(String[] args) {
        IteratorFloderUtil iutil = new IteratorFloderUtil();
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        iutil.findFloderFile("E:/svnwks/trunk/1_project/SmartBranch/SmartBranch/dev/services/proxy/HTP",list);
//		iutil.findFloderAll("E:/svnwks/trunk/1_project/SmartBranch/SmartBranch/dev/services/proxy",list);
        if (list.size()>0) {
            for (int i = 0; i < list.size(); i++) {
                HashMap<String,Object> map = new HashMap<String,Object>();
                map = list.get(i);
                System.out.println(map.get("INTERFACE_PATH"));
                System.out.println(map.get("INTERFACE_NAME"));
            }
        }

    }
}
