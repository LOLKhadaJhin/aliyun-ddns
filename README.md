# aliyun-ddns</br>
视频教程：https://www.bilibili.com/video/BV1R3411M7LW/</br>
配置说明。只要修改三样数据：1、你买的域名。2、能够修改域名信息的账号</br>
https://github.com/LOLKhadaJhin/aliyun-ddns/blob/master/src/main/resources/application.properties</br>
启动：</br>
1、安装java11</br>
2、cmd里输入java -jar 文件夹/aliyun-ddns.jar</br>
</br>
原理：</br>
1.第一次运行获取域名IP，并记录，若与公网IP一致，不会有任何动作，若不一致则修改，若域名没有绑定IP则添加。</br>
2.之后10分钟获取一次公网IP与之前对比，若与公网IP一致，不会有任何动作，若不一致则修改。</br>
</br>
注意：</br>
为了节省性能，只有启动的时候获取域名信息，之后的每10分钟只会获取公网IP，不会获取域名信息，所以之后去阿里云后台可以查看域名信息，但不要人为修改或删除解析。</br>
如果修改或删除了域名解析信息，那程序里的域名信息就失效了，需要重启程序或者访问下面网址刷新域名信息。</br>
</br>
对外有一个访问地址，用于手动刷新，会显示结果。</br>
ip:port/{username}/{password}/{refresh}</br>
ip是群晖ip或群晖映射之后的公网ip。port是端口号。</br>
username是visit账号，password是visit密码。</br>
refresh随便是什么填什么，如果是refresh或者sx会先刷新公网信息，然后刷新。</br>
外网访问需要映射，内网访问,前面填安装电脑ip地址如：</br>
192.168.66.66:6666/root/root/refresh 或 192.168.66.66:6666/root/root/sx，这两个是会获取公网信息刷新。</br>
192.168.66.66:6666/root/root/随意，如果后面不是refresh或sx。
