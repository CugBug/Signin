package com.example.apigps;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	/*IP:
	 * 虚拟机网 192.168.56.1
	 * 寝室网 192.168.1.183
	 * 360WiFi 172.27.11.1
	 * cug1004483.imwork.net*/
	public static String IPString  = "192.168.1.183"; 
	Button login = null;
	Button newuser = null;
	EditText passwords = null;
	EditText UID = null;
	CheckBox checkBox1 = null;
	CheckBox checkBox2 = null;
	String get = null;
	SharedPreferences sp = null;
	Editor editor = null;
	String U = null;  //UID
	String P = null;  //PassWords
	boolean C1 = false;  //Check1,选中为true
	boolean C2 = false;  //Check2,选中为true
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle("用户登录");
		login = (Button)findViewById(R.id.loginbutton);
		newuser = (Button)findViewById(R.id.newuser);
		passwords = (EditText) findViewById(R.id.passwords);
		UID = (EditText) findViewById(R.id.UID);
        checkBox1= (CheckBox) findViewById(R.id.checkBox1);  //显示密码
        checkBox2= (CheckBox) findViewById(R.id.checkBox2);  //自动登录
        sp = getSharedPreferences("User",MODE_PRIVATE);   //默认模式,代表该文件是私有数据,只能被应用本身访问
        editor=sp.edit();    
        UID.setHint("输入用户名");
        passwords.setHint("输入密码");
        newuser.setEnabled(false);
        if(sp.getBoolean("Exist", false)){
        	checkBox1.setChecked(sp.getBoolean("C1", C1));
        	checkBox2.setChecked(sp.getBoolean("C2", C2));
        	C1 = sp.getBoolean("C1", C1);
        	C2 = sp.getBoolean("C2", C2);
        	U = sp.getString("UID", U);
        	P = sp.getString("PassWords", P);
        	passwords.setText(P);
        	UID.setText(U);
        	if(C1) passwords.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        	else passwords.setTransformationMethod(PasswordTransformationMethod.getInstance());
        	if(C2){
        		Login();
        	}
        }
		View.OnClickListener listener = new OnClickListener() {
        	public void onClick(View v){
        		Button view = (Button) v;
        		switch (view.getId()){
        		case R.id.loginbutton:            //登陆按钮监听器
        			Login();
    				break;
        		case R.id.newuser:            //新用户按钮监听器
        			Intent intent = new Intent();
    				intent.putExtra("str", "");
    				intent.setClass(LoginActivity.this, MainActivity.class);
    				startActivity(intent);
        			//startActivity(new Intent(LoginActivity.this,MainActivity.class));
        			//startActivity(new Intent(LoginActivity.this,Newuser.class));
        			break;
        		}
        	}
        };
        login.setOnClickListener(listener);
        newuser.setOnClickListener(listener);
        checkBox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    //如果选中，显示密码      
                	C1 = true;
                    passwords.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //否则隐藏密码
                	C1 = false;
                    passwords.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        checkBox2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    //如果选中，自动登录      
                	C2 = true;
                }else{
                    //否则不自动登录
                	C2 = false;
                }
            }
        });
        
	}
	
	Handler LoginHandler = new Handler(){
    	public void dispatchMessage(android.os.Message msgl){
    		switch (msgl.arg1){
    		case Utils.TCP_FINISH:   //
    			GetPlce Get = new GetPlce(get,false);
    			int port = Get.intFind("port");
    			if( port >= 0){  /**还要添加一个告诉下一个activity的port**/
    				editor = sp.edit();
    				editor.putString("UID", U);
    				editor.putString("PassWords", P);
    				editor.putBoolean("C1", C1);
    				editor.putBoolean("C2", C2);
    				editor.putBoolean("Exist", true);
    				editor.commit();
    				Intent intent = new Intent();
    				intent.putExtra("str", get);
    				intent.setClass(LoginActivity.this, MainActivity.class);
    				startActivity(intent);
    			}
    			else if( port == -2)
    				Toast.makeText(getApplicationContext(), "该用户已经登录", Toast.LENGTH_SHORT).show();
    			else Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
    			break;
    		case Utils.TCP_ERROR:  //
    			Toast.makeText(getApplicationContext(), "无法连接到服务器", Toast.LENGTH_SHORT).show();
    			break;
    		default:
    			break;
    		}
    	}
    };
    
	public void Login(){
		U = UID.getText().toString();
		P = passwords.getText().toString();
		if(U.length() == 0 || P.length() == 0){
			Toast.makeText(getApplicationContext(), "请输入合法的用户与密码", Toast.LENGTH_SHORT).show();
			return ;
		}
		final String out = "{\"UID\":" + U + ",\"PassWords:" + P + ",}";   //{"UID":???,"PassWords":????}
			   
		new Thread(){
			Message msgl = LoginHandler.obtainMessage();
			public void run(){
				try {
					TCP tcp = new TCP(IPString,65534);
					tcp.Out(out);
					get = tcp.In(false);
					tcp.close();   //该函数可以放到析构函数里面?
					msgl.arg1 = Utils.TCP_FINISH;
				}catch (Exception e){
					Log.e("TCP Error","Tcp Error!");
					msgl.arg1 = Utils.TCP_ERROR;
				}
				finally{
					if(get == null )  msgl.arg1 = Utils.TCP_ERROR;  //若收到空串则也是出错
					LoginHandler.sendMessage(msgl);
				}
			}
		}.start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override 
	protected void onDestroy() { 
	// TODO 自动生成的方法存根 
		{      //为了记录下之前的信息
			editor = sp.edit();
			editor.putString("UID", U);
			editor.putString("PassWords", P);
			editor.putBoolean("C1", C1);
			editor.putBoolean("C2", C2);
			editor.putBoolean("Exist", true);
			editor.commit();
		}
		super.onDestroy(); 
	} 
}
