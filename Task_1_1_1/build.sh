rm -rf manual-build
mkdir manual-build

javac -d manual-build src/main/java/org/example/HeapSort.java

javadoc -d manual-build/docs src/main/java/org/example/HeapSort.java

jar cfe manual-build/heapsort.jar org.example.HeapSort -C manual-build .

java -jar manual-build/heapsort.jar