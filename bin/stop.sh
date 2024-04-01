ps -ef|grep umbrella_wms_admin.jar | grep 8081 | grep -v grep | awk '{print $2}'| xargs kill -9
