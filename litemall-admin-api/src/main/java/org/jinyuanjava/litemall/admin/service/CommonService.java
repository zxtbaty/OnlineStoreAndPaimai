package org.jinyuanjava.litemall.admin.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

@Service
public class CommonService {
//    public  String getProjectConfig(String key) {
//        try {
//            Resource resource = new ClassPathResource("application.yml");
//            Properties props = PropertiesLoaderUtils.loadProperties(resource);
//            return props.getProperty(key);
//        }catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

    public  String getProjectYml(String key) {
        try {
            Resource resource = new ClassPathResource("application.yml");
            InputStream inputStream = resource.getInputStream();
//            File file = resource.getFile();
            Yaml yaml = new Yaml();
            //也可以将值转换为Map
            Map mRoot, mServer;
            mRoot= (Map)yaml.load(inputStream);

            mServer = (Map) mRoot.get("server");

            String strResult = mServer.get(key).toString();

            return strResult;
        }catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public  Boolean saveProjectYml(String key,String value) {
        try {
            Map mRoot, mServer;
            Yaml yaml = new Yaml();
            Resource resource = new ClassPathResource("application.yml");
            InputStream inputStream = resource.getInputStream();
            File file =  resource.getFile();
            //也可以将值转换为Map
            mRoot = (Map) yaml.load(inputStream);
            //通过map我们取值就可以了.
            mServer = (Map) mRoot.get("server");
            if(key.equals("logFileRows")){
                mServer.put(key,Integer.valueOf(value));
            } else {
                mServer.put(key, value);
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(yaml.dump(mRoot));
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
