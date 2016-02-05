package com.example.apigps;

import java.util.ArrayList;
import java.util.Calendar;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;


public class MainActivity extends Activity implements
		OnCheckedChangeListener, AMapLocationListener{
	
	private AMapLocationClient mLocationClient = null;
	private AMapLocationClientOption mLocationOption = null;
	private Button btn;
	private Button joinin;
	private Button teamnew;
	private TextView txv;
	private LinearLayout linearLayout;
	private Button leave;
	private Button moveout;
	private ListView lstv;
//	private String[] teaminfo;		//小组名的数组
	double range = 800;
	int hourstart ;
	int hourstop ;
	int TeamIndex;      //点击的String数组下标
	int Flag = 0;
	String get = null;
	String Teamname = null;
	String U = null;
	ArrayAdapter<String> listAdapter;
	ArrayAdapter<String> adapter;///////////////
	ArrayList<String> list=new ArrayList<String>();///////////////
	ArrayList<String> al = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        btn = (Button)findViewById(R.id.button);
        joinin = (Button)findViewById(R.id.joinin);
        teamnew = (Button)findViewById(R.id.newteam);
        txv = (TextView)findViewById(R.id.textView1);
        lstv = (ListView)findViewById(R.id.listview);
        linearLayout = (LinearLayout)findViewById(R.id.leamove);
        leave = (Button)findViewById(R.id.leave);
        moveout = (Button)findViewById(R.id.moveout);
        mLocationClient = new AMapLocationClient(this.getApplicationContext());
        mLocationOption = new AMapLocationClientOption();
		// 设置定位模式为高精度模式
        mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		// 设置定位监听
        mLocationClient.setLocationListener(this);
        SharedPreferences sp = getSharedPreferences("User",MODE_PRIVATE);
		U = sp.getString("UID", null);
		setTitle(U);
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);///////////////
		{
			Intent intent = getIntent();
	        String intentString= intent.getStringExtra("str");
	        Log.d("intent",intentString);
			GetPlce intentGetPlce = new GetPlce(intentString,false);
			int i = 0;
			String xString;
			while( (xString = intentGetPlce.StringFind("team")) != null){
				al.add(xString);
				i++;
			}
			if( i == 0){
				al.add("未加入小组");
				lstv.setVisibility(View.GONE);
			}
//			teaminfo = (String[]) al.toArray(new String[al.size()]);
		}
		listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,al);
		lstv.setAdapter(listAdapter);
		lstv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
				TeamIndex = arg2;
				StringBuffer teamString = new StringBuffer();
				teamString.append("{\"infotype\":"+"teaminfo,"+"\"team\":"+al.get(arg2)+",\"user\":"+U+",");
				tcpthread(teamString.toString(),true);
			}
		});
		leave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				if(v.getId()==R.id.leave){
					AdminDialog(true);
				}
			}
		});
		moveout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				if(v.getId()==R.id.moveout){
					AdminDialog(false);
				}
			}
		});
        joinin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				if(v.getId()==R.id.joinin){
					showAddDialog("joinin");
				}
			}
		});
        teamnew.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				if(v.getId()==R.id.newteam){
					showAddDialog("newteam");
				}
			}
		});
        btn.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				/**添加功能，如果询问服务器发现只有一个加入的签到小组，则开始签这个到
				 * 否则跳出dialog选择一个小组进行签到**//**上述貌似无用**/
				if(v.getId()==R.id.button){
					Log.d("onClick", "Begin");
					if(btn.getText().equals(getResources().getString(R.string.button_start))){
						joinin.setEnabled(false);
						teamnew.setEnabled(false);
						Flag = Utils.FLAG_SIGNIN;
						mLocationClient.setLocationOption(mLocationOption);
						mLocationClient.startLocation();
						btn.setText(getResources().getString(
								R.string.button_stop));
						mHandler.sendEmptyMessage(Utils.MSG_LOCATION_START);
					}
					else {
						btn.setText(getResources().getString(
								R.string.button_start));
						joinin.setEnabled(true);
						teamnew.setEnabled(true);
						// 停止定位
						mLocationClient.stopLocation();
						mHandler.sendEmptyMessage(Utils.MSG_LOCATION_STOP);
					}
					Log.d("onClick", "Finished");
				}
			}
        });
        Log.d("onCreat", "Finished");
    }

	protected void AdminDialog(boolean x) {   //x==true是离开，否则为移除成员
        LayoutInflater factory = LayoutInflater.from(this); 
        final View textEntryView = factory.inflate(R.layout.admin_dialog, null); 
        final Spinner member = (Spinner)textEntryView.findViewById(R.id.member);
        final TextView txv = (TextView)textEntryView.findViewById(R.id.admintxv);
        
        AlertDialog.Builder ad1 = new AlertDialog.Builder(this); 
        //ad1.setIcon(android.R.drawable.ic_dialog_info);     //可以添加图标
        ad1.setView(textEntryView); 
        if(x){
        	txv.setText("确定要离开本小组吗？");
        	ad1.setTitle("离开小组");
        	member.setVisibility(View.GONE);
        	ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() { 
                public void onClick(DialogInterface dialog, int i) { 
                	tcpthread("{\"infotype\":leave,\"leaveteam\":"+al.get(TeamIndex)+",\"member\":"+U+",", false);
					al.remove(al.get(TeamIndex));
					listAdapter.notifyDataSetChanged();
                }
            }); 
        }
        else {
        	txv.setText("要移除的成员：");
        	ad1.setTitle("移除成员");
        	member.setVisibility(View.VISIBLE);
        	member.setAdapter(adapter);
        	ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() { 
                public void onClick(DialogInterface dialog, int i) { 
                	String x = (String) member.getSelectedItem();
                	tcpthread("{\"infotype\":leave,\"leaveteam\":"+al.get(TeamIndex)+",\"member\":"+x+",", false);
                }
            }); 
        }
        ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() { 
            public void onClick(DialogInterface dialog, int i) { 
            	return;
            } 
        }); 
        ad1.show();// 显示对话框 
    }
	
	protected void showAddDialog(final String type) { 
        LayoutInflater factory = LayoutInflater.from(this); 
        final View textEntryView = factory.inflate(R.layout.dialog, null); 
        final EditText edt = (EditText)textEntryView.findViewById(R.id.edittext); 
        final EditText edt1 = (EditText)textEntryView.findViewById(R.id.edittext1); 
        final LinearLayout layout = (LinearLayout)textEntryView.findViewById(R.id.layout); 
        final Spinner start = (Spinner)textEntryView.findViewById(R.id.Spinner01); 
        final Spinner stop = (Spinner)textEntryView.findViewById(R.id.Spinner02);
        final Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);  //24小时制
        String Hint = null;
    	String Title = null;
        if(type.equals("joinin")){
        	Hint = "输入签到小组名";
        	Title = "加入签到小组";
        	edt1.setVisibility(View.GONE);
        	layout.setVisibility(View.GONE);
        } else if(type.equals("newteam")){
        	Hint = "输入签到小组名";
        	Title = "新建签到小组";
        	edt1.setVisibility(View.VISIBLE);
        	edt1.setHint("签到距离（米），可不填");
        	start.setSelection(hour, true);
        	stop.setSelection( (hour+3)%24 , true);
        }
        AlertDialog.Builder ad1 = new AlertDialog.Builder(this); 
        edt.setHint(Hint);
        ad1.setTitle(Title); 
        //ad1.setIcon(android.R.drawable.ic_dialog_info);     //可以添加图标
        ad1.setView(textEntryView); 
        
        ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() { 
            public void onClick(DialogInterface dialog, int i) { 
            	Teamname = edt.getText().toString();
            	 if(type.equals("joinin")){
            		 tcpthread("{\"infotype\":joinin,\"joinin\":"+Teamname+",\"user\":"+U+",",false);
            	 }
            	 else if(type.equals("newteam")){
            		 if(edt1.getText().toString().length() <= 0){   //没有输入范围，则自定义为800
            			 range = 800;
            		 }else range = Double.parseDouble(edt1.getText().toString());
            		 hourstart = (int) start.getSelectedItemId();
            		 hourstop = (int) stop.getSelectedItemId();
//            		 if(hourstart<hourstop){
	             		 Flag = Utils.FLAG_NEWTEAM;
	            		 mLocationClient.setLocationOption(mLocationOption);
	            		 mLocationClient.startLocation();
//            		 }else 
            	 }
            }
        }); 
        ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() { 
            public void onClick(DialogInterface dialog, int i) { 
            	return;
            } 
        }); 
        ad1.show();// 显示对话框 
    }
	public void tcpthread(final String S,final boolean show){   //show是是否读取多行
			new Thread(){          //关于网络连接这部分要在一个新的线程中使用
			Message msgtcp = tcpHandler.obtainMessage();
			Message text = Text.obtainMessage();
			@Override
			public void run(){
				try {
					TCP tcp = new TCP(LoginActivity.IPString,65533);
					tcp.Out(S);
					if(show)
					get = tcp.In(true);
					else get = tcp.In(false);
					Log.d("tcpin", get);
					/**
					 * 是否需要这样做，还是换一个方法*/
					GetPlce myGetPlce = new GetPlce(get,false);
					String X = myGetPlce.StringFind("infotype");
					if(X.equals("signin")){
						get = myGetPlce.StringFind("data");
						text.arg1 = Utils.MSG_TOAST_SHOW;
					}else if(X.equals("joinin")){
						get = myGetPlce.StringFind("data");
						text.arg1 = Utils.MSG_TEXT_SHOW;
					}else if(X.equals("newteam")){
						get = myGetPlce.StringFind("data");
						text.arg1 = Utils.MSG_TEXT_SHOW;
						if(get.equals("成功建立签到小组")){
							mLocationClient.stopLocation();
							al.add(Teamname);
							listAdapter.notifyDataSetChanged();
							//mHandler.sendEmptyMessage(Utils.MSG_LOCATION_STOP);
						}
					}else if(X.equals("teaminfo")){
						get = myGetPlce.StringFind("data");
						String ifadmin = myGetPlce.StringFind("ifadmin");
						adapter.clear();
						String xString;
						while( (xString = myGetPlce.StringFind("member"))!=null){
							adapter.add(xString);
						}
						if(ifadmin.equals("yes"))
							text.arg1 = Utils.MSG_BUTTON_SHOW_TWO;
						else text.arg1 = Utils.MSG_BUTTON_SHOW_ONE;
					}else if(X.equals("nonesignin")){
						get = myGetPlce.StringFind("data");
						text.arg1 = Utils.MSG_TEXT_SHOW;
					}else if(X.equals("leave")){
						get = myGetPlce.StringFind("data");
						text.arg1 = Utils.MSG_TEXT_SHOW;
					}else {
						Log.e("infotype", "unknowtype");
					}
					tcp.close();   //该函数可以放到析构函数里面?
					msgtcp.arg1 = Utils.TCP_FINISH;
				}catch (Exception e){
					Log.e("TCP Error","Tcp Error!");
					msgtcp.arg1 = Utils.TCP_ERROR;
				}
				finally{
					Text.sendMessage(text);
					tcpHandler.sendMessage(msgtcp);
				}
			}
		}.start();
	}
	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    
    Handler tcpHandler = new Handler(){
    	public void dispatchMessage(android.os.Message msgtcp){
    		switch (msgtcp.arg1){
    		case Utils.TCP_FINISH:   //
//    			Toast.makeText(getApplicationContext(),get, Toast.LENGTH_SHORT).show();
    			Log.d("TCPSuccess","TCPSuccess");
    			break;
    		case Utils.TCP_ERROR:  //
    			Log.d("TCPError","TCPError");
//    			Toast.makeText(getApplicationContext(), "TCP_ERROR", Toast.LENGTH_SHORT).show();
    			break;
    		default:
    			break;
    		}
    	}
    };
    Handler Text = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.arg1) {
			case Utils.MSG_BUTTON_SHOW_ONE:
				txv.setText(get);
				linearLayout.setVisibility(View.VISIBLE);
				moveout.setEnabled(false);
				break;
			case Utils.MSG_BUTTON_SHOW_TWO:
				txv.setText(get);
				linearLayout.setVisibility(View.VISIBLE);
				moveout.setEnabled(true);
				break;
			case Utils.MSG_TEXT_SHOW:
				txv.setText(get);
				linearLayout.setVisibility(View.GONE);
				break;
			case Utils.MSG_TOAST_SHOW:
				linearLayout.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(),get, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};
	
    Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			linearLayout.setVisibility(View.GONE);
			switch (msg.what) {
			//开始定位
			case Utils.MSG_LOCATION_START:
				txv.setText("正在定位...");
				break;
			// 定位完成
			case Utils.MSG_LOCATION_FINISH:
				AMapLocation loc = (AMapLocation) msg.obj;
				String result = Utils.getLocationStr(loc);
				txv.setText(result);      // ????注释后问题
				break;
			//停止定位
			case Utils.MSG_LOCATION_STOP:
				txv.setText("定位停止");
				break;
			default:
				break;
			}
			Log.d("mHandler", "Finished");
		};
	};
	@Override
	public void onLocationChanged(AMapLocation loc) {
		// TODO 自动生成的方法存根
		if(loc != null){
			switch (Flag) {
			case Utils.FLAG_SIGNIN:   //签到模式
				Message msg = mHandler.obtainMessage();
				msg.obj = loc;
				msg.what = Utils.MSG_LOCATION_FINISH;
				mHandler.sendMessage(msg);
				Log.d("onLocationChanged", "Finished");
				final String toServer = loc.toStr(1) + "\"user\":" +U+"," +"\"infotype\":signin,";
				if( loc.getErrorCode()==0 ){
					tcpthread(toServer,true);
				}
				break;
			case Utils.FLAG_NEWTEAM:   //设置签到点模式
				final String toServer1 = loc.toStr(1) + "\"user\":" +U+",\"infotype\":newteam,\"newteam\":"
													+Teamname+",\"range\":"+range
													+",\"hourstart\":"+hourstart+",\"hourstop\":"+hourstop+",";
				if( loc.getErrorCode()==0 ){
					tcpthread(toServer1,false);
				}
			default:
				break;
			}
			
		}
	}
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO 自动生成的方法存根
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mLocationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			mLocationClient.onDestroy();
			mLocationClient = null;
			mLocationOption = null;
		}
		new Thread(){         
			@Override
			public void run(){
				try {
					TCP tcp = new TCP(LoginActivity.IPString,65534);
					tcp.Out("{\"Bye\":"+ U + ",}");   //{"Bye":User,}
					tcp.close();   //该函数可以放到析构函数里面?
				}catch (Exception e){
					Log.e("TCP Error","Tcp Error!");
				}
			}
		}.start();
		Log.d("onDestroy", "Finished");
	}
	
}
