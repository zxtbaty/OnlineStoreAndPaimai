
//本地环境

// var target = "http://172.20.10.2:8000/";
// var website = "http://172.20.10.2:8081";

var target = "http://192.168.1.5:8000/";
var website = "http://192.168.1.5:8081";
 
//  
// //正式环境
// var target = "http://www.jinyuanbigdata.com:8000/";
// var website = "http://paimai.jinyuanbigdata.com";

// 发布到Tomcat时注意,需要在发布后的文件夹下创建WEB-INF文件夹,里面加web.xml文件
// 里面加如下的配置:

// <?xml version="1.0" encoding="UTF-8"?>
// <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
//     xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
//                       http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
//     version="3.1" metadata-complete="true">
//     <display-name>Router for Tomcat</display-name>
//     <error-page>
//         <error-code>404</error-code>
//         <location>/index.html</location>
//     </error-page>
// </web-app>
 
 
export default{
	target,website
}
