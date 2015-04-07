javac ComputeNash.java; 

java -ea -Xms8092m -Xmx8092m ComputeNash 0 1 0.1 output/mmhc3p/sc/01 > output/mmhc3p/sc/01.out &
java -ea -Xms8092m -Xmx8092m ComputeNash 0 2 0.1 output/mmhc3p/sc/02 > output/mmhc3p/sc/02.out &
java -ea -Xms8092m -Xmx8092m ComputeNash 1 2 0.1 output/mmhc3p/sc/12 > output/mmhc3p/sc/12.out &

java -ea -Xms8092m -Xmx8092m ComputeNash 0 1 0.1 output/mmhc3p/wc/01 > output/mmhc3p/wc/01.out &
java -ea -Xms8092m -Xmx8092m ComputeNash 0 2 0.1 output/mmhc3p/wc/02 > output/mmhc3p/wc/02.out &
java -ea -Xms8092m -Xmx8092m ComputeNash 1 2 0.1 output/mmhc3p/wc/12 > output/mmhc3p/wc/12.out &

java -ea -Xms8092m -Xmx8092m ComputeNash 0 1 0.1 output/mmhc2p/mmhc2p > output/mmhc2p/mmhc2p.out &

