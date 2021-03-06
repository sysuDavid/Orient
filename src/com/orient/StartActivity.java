package com.orient;

import java.util.ArrayList;
import java.util.List;

import com.constant.Constant;
import com.network.GetRoomInfo;
import com.network.Login;
import com.util.Room;
import com.util.TeamParcelable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class StartActivity extends Activity{
    SQLiteDatabase db;
    String username;
    String pw;
    private Context context = this;
    GlobalVarApplication gva;
    
    private final Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			gva = (GlobalVarApplication)getApplication();
			Bundle bundle = msg.getData();
			String status = bundle.getString("status", "no status");
			String info = bundle.getString("info", "no info");
			String roomid = bundle.getString("RoomId", "no room id");
			String teamid = bundle.getString("TeamId", "no team id");
			String nickname = bundle.getString("nickname", "no nickname");
			String gender = bundle.getString("gender", "no gender");
			String telephone = bundle.getString("telephone", "no telephone");
			int portrait = Integer.valueOf(bundle.getString("portrait", "0"));
			switch(msg.what){
			case Constant.NETWORK_SUCCESS_MESSAGE_TAG: 
				if (status.equalsIgnoreCase("succeed")){
					Toast.makeText(context, "登录成功", 
							Toast.LENGTH_SHORT).show();
					gva.nickname = nickname;
					gva.gender = gender;
					gva.telephone = telephone;
					gva.portrait = portrait;
					if (info.equalsIgnoreCase("ok")){
						Intent intent = new Intent();
						intent.setClass(StartActivity.this, HomeActivity.class);
						startActivity(intent);
						finish();
					}else if (info.equalsIgnoreCase("in room")){
						Toast.makeText(context, "正在房间中", 
								Toast.LENGTH_SHORT).show(); 
						int room_id_num = Integer.parseInt(roomid);
						gva.curRoomId = room_id_num;
						Log.i("yuan", teamid);
						int team_id_num = Integer.parseInt(teamid);
						gva.teamid = team_id_num;
						Intent intent = new Intent();
						intent.putExtra("RoomId", room_id_num);
						intent.setClass(StartActivity.this, RoomNew.class);
						startActivity(intent);
						finish();
					}else if (info.equalsIgnoreCase("in game")){
						Toast.makeText(context, "正在游戏中", 
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(StartActivity.this, GameMap.class);
						startActivity(intent);
						finish();
					}/*else {
						Intent intent = new Intent();
						intent.setClass(StartActivity.this, HomeActivity.class);
						startActivity(intent);
						finish();
					}*/
				}
				else if (status.equalsIgnoreCase("not exists")){
					Toast.makeText(context, "用户名不存在", 
							Toast.LENGTH_LONG).show();
					Intent intent = new Intent();
					intent.setClass(StartActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}else if (status.equalsIgnoreCase("failed")){
					Toast.makeText(context, "请重新登录", 
							Toast.LENGTH_LONG).show();
					Log.i("lin", info);
					Intent intent = new Intent();
					intent.setClass(StartActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}else{
					Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
				}
				break;
			case Constant.NETWORK_FAILED_MESSAGE_TAG:
				Toast.makeText(context, "网络连接有错，请稍后再试",
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent();
				intent.setClass(StartActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
				break;
			default:
				break;
			}
		
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start_background);
		SharedPreferences sharePreferences = getSharedPreferences(
				Constant.USER_SHAREDPREFERENCE, MODE_PRIVATE);
		username = sharePreferences.getString(Constant.SHAREDPREFERENCE_KEY_USERNAME, "");
		pw = sharePreferences.getString(Constant.SHAREDPREFERENCE_KEY_PASSWORD, "");
		gva = (GlobalVarApplication)getApplication();
        if (!username.equals("") && !pw.equals("")){
        	Login login = new Login(gva.httpClient, mHandler, username, pw);
        	new Thread(login).start();
        }else {
        	Intent intent = new Intent();
			intent.setClass(StartActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
        }
        
	}
	
}
