package edu.whut.baiwentao.androidjob1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /******1.实例化两个EditText对象和两个Button对象，
         并通过findViewById方法实现layout中的控件与java对象的关联*****/
        final EditText unameEdt=(EditText)findViewById(R.id.edt_username);
        final EditText pwdEdt=(EditText)findViewById(R.id.edt_password);
        Button quitBtn=(Button)findViewById(R.id.btn_quit);
        Button loginBtn=(Button)findViewById(R.id.btn_login);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = unameEdt.getText().toString();
                String pass_word = pwdEdt.getText().toString();
                if(user_name.equals("") || !pass_word.equals("123456")){
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent=new Intent(LoginActivity.this,ListActivity.class);
                    intent.putExtra("user_name",user_name);
                    startActivity(intent);
                }
            }
        });

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





        /******2.为两个Button对象添加监听器，并实现其onClick方法
         其中退出按钮监听器的OnClick中实现程序的退出
         (可使用下面两条语句实现
         android.os.Process.killProcess(android.os.Process.myPid());
         System.exit(0);
         )
         登录按钮监听器的OnClick中实现当用户输入的用户名与密码满足要求时，进行Activity的带参数跳转
         (1.if...else结构判断用户输入的用户名和密码是否满足要求
           （用户输入数据可通过EditText对象的getText方法获取，并通过toString方法转换为字符串，如edittext1.getText().toString()）：
            用户名不可为空串，密码必须为123456，
         (unameEdt.getText().toString()==null||unameEdt.getText().toString().equals("") || !pwdEdt.getText().toString().equals("123456"))
            如不满足要求，else部分Toast提示错误信息；
            如满足要求，if部分通过后面3步实现Activity的带参数跳转
            (注意java中字符串比较是否相等不能用比较运算符，需使用equals方法，如pwd.equals("1111"),相等返回true；不等返回false)
         2.实例化一个Intent对象
         3.通过Intent对象的putExtra方法绑定要传递的参数（用户名）
         4.通过startActivity方法实现Activity的切换。
         Activity的带参数切换可参考教学资源中课件例子中的“ Activcity创建与多Activity切换”
         )*****/









    }
}
