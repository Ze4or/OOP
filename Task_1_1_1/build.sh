javac src/main/java/ru/nsu/batyaev/task_1_1_1/*.java -d build

javadoc -d build/docs/javadoc \
-sourcepath src/main/java \
-subpackages ru.nsu.batyaev.task_1_1_1

jar cfe out.jar ru.nsu.batyaev.task_1_1_1.HeapSort -C build ru

java -jar out.jar