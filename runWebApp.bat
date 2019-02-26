:startRun
java -jar build\libs\PiServerSpringBoot-1.0.jar configTestDataWIN.json
if %errorlevel% neq 0 goto startRun
