export APPLI_HOME=.
export APPLI_CONFIG=${APPLI_HOME}/conf

log4jconfig=log4j.properties

APPLI_CLASSPATH=.:$APPLI_HOME/iPodBackuper.jar

for i in ${APPLI_HOME}/lib/*.jar; 
 do APPLI_CLASSPATH=$i:${APPLI_CLASSPATH}
done

echo $APPLI_CLASSPATH

java -classpath $APPLI_CLASSPATH -Dlog4j.configuration=file:$APPLI_CONFIG/$log4jconfig com.ipod.backuper.Launcher
