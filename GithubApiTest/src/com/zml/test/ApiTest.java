package com.zml.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import com.zml.myclass.GithubProject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ApiTest {

	/* 获取github项目List
	 * parm:String requestUrl 请求URL
	 * parm:String gitUserInfo 请求数据用户信息
	 * return:github项目List
	 * */
	public static  List<GithubProject> jsonGet(String requestUrl , String gitUserInfo) {

        //调用httpRequest方法，发出请求
        String string = httpRequest(requestUrl,gitUserInfo);

        //处理返回的JSON数据并返回
        
        JSONArray jsonArray = JSONArray.fromObject(string);
        List<GithubProject> githubPList = new ArrayList<GithubProject>();
        for ( int i = 0 ; i < jsonArray.size() ; i++ ) {
        	JSONObject jsonObj = (JSONObject)jsonArray.get(i);
        	
        	//对获取的JSON数据进行封装
        	GithubProject githubP = new GithubProject();
        	githubP.setIdGit(jsonObj.getString("id"));
        	githubP.setNameGit(jsonObj.getString("name"));
        	githubP.setStarNumGit(jsonObj.getInt("stargazers_count"));
        	githubPList.add( githubP);
        }
        //返回github工程List
    	return githubPList;
	}
	
	/* 进行http请求
	 * parm:String requestUrl 请求URL
	 * parm:String gitUserInfo 请求数据用户信息
	 * return:JSON数据
	 * */
	private static String httpRequest(String requestUrl , String gitUserInfo) {
    	//buffer用于接受返回的字符
    	StringBuffer buffer = new StringBuffer();
        try {
        	URL url = new URL(requestUrl);  
        	
        	//对账号密码进行8Bit字节码的编码
        	String encoding = new String (Base64.getEncoder().encode(new String(gitUserInfo).getBytes()));
        	//打开http连接
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.setRequestProperty( "Authorization","Basic " + encoding);
            httpUrlConn.connect();
            System.out.println("http请求响应中......................");
            //获得输入
            InputStream inputStream = httpUrlConn.getInputStream();  
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");  
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
            
            //将bufferReader的值给放到buffer里
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {  
                buffer.append(str);
            }

            //关闭bufferReader和输入流
            bufferedReader.close();  
            inputStreamReader.close();  
            inputStream.close();  
            inputStream = null;  
            //断开连接
            httpUrlConn.disconnect();
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        //返回请求的JSON字符串
        return buffer.toString();  
    }

	/* 进行文件保存
	 * parm:String requestUrl 请求URL
	 * parm:String gitUserInfo 请求数据用户信息
	 * return:JSON数据
	 * */
	public static void saveFile(List<GithubProject> githubPList) {
        BufferedWriter writer = null;
        String destDirName="e:\\githubApiTest";
        String fileName = "githubSave";
        File dir = new File(destDirName);
        if (dir.exists()) {
            System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            System.out.println("创建目录" + destDirName + "成功！");
        } else {
            System.out.println("创建目录" + destDirName + "失败！(不影响写入)");
        }
        File file = new File(destDirName+ fileName + ".txt");

        //如果文件不存在，则新建一个
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(fileName + ".txt文件不存在");
            System.out.println("创建文件" + fileName);
        }
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false), "UTF-8"));
            //遍历写入
            writer.write("----------------------------------------------");
            writer.write("\r\n");
            writer.write("githubId:githubName:githubStarNum");
            writer.write("\r\n");
            for (GithubProject githubProject : githubPList) {
            	writer.write(githubProject.getIdGit() + ":" + githubProject.getNameGit() + ":" + githubProject.getStarNumGit());
            	writer.write("\r\n");
            }
            writer.write("----------------------------------------------");
            
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("文件写入成功！");
	}
	public static void main(String[] args) {
    	//所调用的github API
		String requestUrl = "https://api.github.com/users/FIRHQ/repos"; 
		//使用github API 进行https请求 需要使用github账号密码登陆
		String username = "786200288@qq.com";
    	String password = "zhuming0014";
		
		List jsonList = jsonGet(requestUrl , username + ":" + password);
		saveFile(jsonList);
	}
	
}
