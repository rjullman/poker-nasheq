javac ComputeNash.java; 

(
java -Xms8092m -Xmx8092m ComputeNash 0 1 out/01;
java -Xms8092m -Xmx8092m ComputeNash 0 2 out/02;
) &

(
java -Xms8092m -Xmx8092m ComputeNash 1 2 out/12
) &
