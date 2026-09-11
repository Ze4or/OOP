javac src/main/java/ru/nsu/batyaev/task_1_1_1/*.java -d build

javadoc -d build/docs/javadoc \
-sourcepath src/main/java \
-subpackages ru.nsu.batyaev.task_1_1_1

jar cf out.jar -C build ru
