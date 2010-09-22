@echo off
setlocal
for %%i in (.\target\*.jar) do call :setpath %%i
for %%i in (.\lib\*.jar) do call :setpath %%i
goto :endsubs
:setpath
set CLASSPATH=%CLASSPATH%;%1
goto :EOF
:endsubs
echo Running with classpath %CLASSPATH%
echo Starting...
"%JAVA_HOME%\bin\java.exe" -classpath "%CLASSPATH%" org.seasar.robot.example.Crawler %1 %2
