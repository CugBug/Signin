package com.example.apigps;

public class GetPlce {
	private String address = null;
	private String provider = null;
	private double longitude;    //经度
	private double latitude;      //纬度
	private double accuracy;    //精确度
	private int errorCode;         //错误码
	StringBuffer StrB = new StringBuffer();
	GetPlce(String Str){
		StrB.append(Str);
		int i = StrB.indexOf("}");
		StrB.replace(i, i+1, ",");
		errorCode = intFind("errorCode");
		if( errorCode==0 ){
			provider = StringFind("provider");
			if(!provider.equals("gps"))   //如果是从GPS获取的就不获取地址
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
		if(x){              //只得到经纬度，不需判别ErrorCode
			latitude = doubleFind("latitude");
			longitude = doubleFind("longitude");
		}
		else{     //x为false时则只得到字段模式
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
		if(i <= 0) return null;   /***若无匹配字符串，则返回空***/
		int begin = StrB.indexOf(":",i)+1;
		int end = StrB.indexOf(",",i);
		String get = StrB.substring(begin,end);
		StrB.delete(i-1, end+1);      /****删除截取的字符串，比较要用到,  bug:如果没有改字符串会出bug****/
		return get;
	}
}
