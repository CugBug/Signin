package com.example.apigps;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class TCP {
	//private String SevIP;
	//private int port;
	//private String toSever;   //���͵���Ϣ
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	
	TCP(String SevIP,int port){   //���캯��
		//this.SevIP = SevIP;
		//this.port = port;
		try {
			InetAddress serverAddr = InetAddress.getByName(SevIP);
			socket = new Socket(serverAddr, port);
			out = new PrintWriter(new BufferedWriter(  
					 new OutputStreamWriter(socket.getOutputStream(),"utf-8")),true);
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream(),"utf-8"));
		} catch (UnknownHostException e) {
			Log.e("UnknownHostException", "To server:'" + SevIP);
			e.printStackTrace();
		}// TCPServer.SERVERIP
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void Out(String outstr){  //outstr��Ҫ���͵�String
		out.println(outstr);
		out.flush();
	}
	
	public String In(boolean line){   //�õ���һ��string����  ,line��ʾ�Ƿ�����е�
		StringBuffer get = new StringBuffer();
		try {
			if(line){
				String X = null;
				while((X = in.readLine())!=null){
					get.append(X).append("\n");
				}
			}
			else get.append(in.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return get.toString();
	}
	
	public void close(){   //�ر�socket
		try {
			socket.close();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}
	/*   ��û�в�����û����
	public void finalize(){
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("finalize","finished");
	}
	*/
}
