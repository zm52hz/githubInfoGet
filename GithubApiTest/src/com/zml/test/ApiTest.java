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

	/* ��ȡgithub��ĿList
	 * parm:String requestUrl ����URL
	 * parm:String gitUserInfo ���������û���Ϣ
	 * return:github��ĿList
	 * */
	public static  List<GithubProject> jsonGet(String requestUrl , String gitUserInfo) {

        //����httpRequest��������������
        String string = httpRequest(requestUrl,gitUserInfo);

        //�����ص�JSON���ݲ�����
        
        JSONArray jsonArray = JSONArray.fromObject(string);
        List<GithubProject> githubPList = new ArrayList<GithubProject>();
        for ( int i = 0 ; i < jsonArray.size() ; i++ ) {
        	JSONObject jsonObj = (JSONObject)jsonArray.get(i);
        	
        	//�Ի�ȡ��JSON���ݽ��з�װ
        	GithubProject githubP = new GithubProject();
        	githubP.setIdGit(jsonObj.getString("id"));
        	githubP.setNameGit(jsonObj.getString("name"));
        	githubP.setStarNumGit(jsonObj.getInt("stargazers_count"));
        	githubPList.add( githubP);
        }
        //����github����List
    	return githubPList;
	}
	
	/* ����http����
	 * parm:String requestUrl ����URL
	 * parm:String gitUserInfo ���������û���Ϣ
	 * return:JSON����
	 * */
	private static String httpRequest(String requestUrl , String gitUserInfo) {
    	//buffer���ڽ��ܷ��ص��ַ�
    	StringBuffer buffer = new StringBuffer();
        try {
        	URL url = new URL(requestUrl);  
        	
        	//���˺��������8Bit�ֽ���ı���
        	String encoding = new String (Base64.getEncoder().encode(new String(gitUserInfo).getBytes()));
        	//��http����
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.setRequestProperty( "Authorization","Basic " + encoding);
            httpUrlConn.connect();
            System.out.println("http������Ӧ��......................");
            //�������
            InputStream inputStream = httpUrlConn.getInputStream();  
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");  
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
            
            //��bufferReader��ֵ���ŵ�buffer��
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {  
                buffer.append(str);
            }

            //�ر�bufferReader��������
            bufferedReader.close();  
            inputStreamReader.close();  
            inputStream.close();  
            inputStream = null;  
            //�Ͽ�����
            httpUrlConn.disconnect();
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        //���������JSON�ַ���
        return buffer.toString();  
    }

	/* �����ļ�����
	 * parm:String requestUrl ����URL
	 * parm:String gitUserInfo ���������û���Ϣ
	 * return:JSON����
	 * */
	public static void saveFile(List<GithubProject> githubPList) {
        BufferedWriter writer = null;
        String destDirName="e:\\githubApiTest";
        String fileName = "githubSave";
        File dir = new File(destDirName);
        if (dir.exists()) {
            System.out.println("����Ŀ¼" + destDirName + "ʧ�ܣ�Ŀ��Ŀ¼�Ѿ�����");
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //����Ŀ¼
        if (dir.mkdirs()) {
            System.out.println("����Ŀ¼" + destDirName + "�ɹ���");
        } else {
            System.out.println("����Ŀ¼" + destDirName + "ʧ�ܣ�(��Ӱ��д��)");
        }
        File file = new File(destDirName+ fileName + ".txt");

        //����ļ������ڣ����½�һ��
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(fileName + ".txt�ļ�������");
            System.out.println("�����ļ�" + fileName);
        }
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false), "UTF-8"));
            //����д��
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
        System.out.println("�ļ�д��ɹ���");
	}
	public static void main(String[] args) {
    	//�����õ�github API
		String requestUrl = "https://api.github.com/users/FIRHQ/repos"; 
		//ʹ��github API ����https���� ��Ҫʹ��github�˺������½
		String username = "786200288@qq.com";
    	String password = "zhuming0014";
		
		List jsonList = jsonGet(requestUrl , username + ":" + password);
		saveFile(jsonList);
	}
	
}
