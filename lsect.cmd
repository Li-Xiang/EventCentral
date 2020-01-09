set PRG_HOME=%~dp0
set PRG_LIB="%PRG_HOME%*;.;%PRG_HOME%lib\*"

java -XX:+UseG1GC -XX:+UseStringDeduplication -cp %PRG_LIB% org.littlestar.event_central.test.EventTester %*