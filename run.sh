javac ComputeNash.java; 

java -ea -Xms8092m -Xmx8092m ComputeNash 0 1 0.1 output/mmh3p/sc/01 > output/mmh3p/sc/01.out &
java -ea -Xms8092m -Xmx8092m ComputeNash 0 2 0.1 output/mmh3p/sc/02 > output/mmh3p/sc/02.out &
java -ea -Xms8092m -Xmx8092m ComputeNash 1 2 0.1 output/mmh3p/sc/12 > output/mmh3p/sc/12.out &
