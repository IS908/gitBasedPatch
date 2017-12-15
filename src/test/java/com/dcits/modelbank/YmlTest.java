package com.dcits.modelbank;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created on 2017-12-15 00:17.
 *
 * @author kevin
 */
public class YmlTest {

    public static void test1() {
        try {
            Yaml yaml = new Yaml();
            File file = new File("D:\\fuxin\\modelBankCI\\src\\main\\resources\\conf.yml");
//            URL url = YmlTest.class.getClassLoader().getResource("conf.yaml");
            HashMap<String, List<String>> map = yaml.loadAs(new FileInputStream(file), HashMap.class);
            for (String key : map.keySet()) {
                System.out.println("key : " + key);
                System.out.println("value : " + map.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public static void test2() {
        try {
            Contact c1 = new Contact("test1", 1, Arrays.asList(
                    new Phone("home", "1111"),
                    new Phone("work", "2222")));
            Contact c2 = new Contact("test2", 23, Arrays.asList(
                    new Phone("home", "1234"),
                    new Phone("work", "4321")));
            List contacts = Arrays.asList(c1, c2);
            Yaml yaml = new Yaml();
            yaml.dump(contacts, new FileWriter("contact.yaml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static void main(String[] args) {
        test1();
    }

}
