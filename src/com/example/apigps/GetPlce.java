package com.example.apigps;

public class GetPlce {
	private String address = null;
	private String provider = null;
	private double longitude;    //����
	private double latitude;      //γ��
	private double accuracy;    //��ȷ��
	private int errorCode;         //������
	StringBuffer StrB = new StringBuffer();
	GetPlce(String Str){
		StrB.append(Str);
		int i = StrB.indexOf("}");
		StrB.replace(i, i+1, ",");
		errorCode = intFind("errorCode");
		if( errorCode==0 ){
			provider = StringFind("provider");
			if(!provider.equals("gps"))   //����Ǵ�GPS��ȡ�ľͲ���ȡ��ַ
			address = StringFind("address");
			latitude = doubleFind("latitude");
			longitude = doubleFind("longitude");
			accuracy = doubleFind("accuracy");
		}
		else System.out.println("errorCode:" + errorCode);
	}
	GetPlce(String Str,boolean x){    
		StrB.append(Str);
		int i = StrB.indexOf("}");
		if(i>=0)
		StrB.replace(i, i+1, ",");
		if(x){              //ֻ�õ���γ�ȣ������б�ErrorCode
			latitude = doubleFind("latitude");
			longitude = doubleFind("longitude");
		}
		else{     //xΪfalseʱ��ֻ�õ��ֶ�ģʽ
		}
	}
	public double Getlatitude(){
		return latitude;
	}
	public double Getlongitude(){
		return longitude;
	}
	public int GeterrorCode(){
		return errorCode;
	}
	public void Pr(){
		System.out.println(
				"provider:" + provider 
				+"\naddress:" + address 
				+ "\nlatitude:" + latitude 
				+"\nlongitude:" + longitude 
				+"\naccuracy:" + accuracy);
	}
	public int intFind(String S){
		int i = StrB.indexOf(S);
		int begin = StrB.indexOf(":",i)+1;
		int end = StrB.indexOf(",",i);
		String get = StrB.substring(begin,end);
		int N = Integer.parseInt(get);
		return N;
	}
	public double doubleFind(String S){
		int i = StrB.indexOf(S);
		int begin = StrB.indexOf(":",i)+1;
		int end = StrB.indexOf(",",i);
		String get = StrB.substring(begin,end);
		double N = Double.parseDouble(get);
		return N;
	}
	public String StringFind(String S){
		int i = StrB.indexOf(S);
		if(i <= 0) return null;   /***����ƥ���ַ������򷵻ؿ�***/
		int begin = StrB.indexOf(":",i)+1;
		int end = StrB.indexOf(",",i);
		String get = StrB.substring(begin,end);
		StrB.delete(i-1, end+1);      /****ɾ����ȡ���ַ������Ƚ�Ҫ�õ�,  bug:���û�и��ַ������bug****/
		return get;
	}
}
