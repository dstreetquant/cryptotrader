# cryptotrader
Trading platform for Crypto Currencies

# Pre Requisites
1. Install Java JDK and JRE 1.8
2. Install Maven
3. Install Python 2.7 (better install anaconda)
4. Set environment path for all the three above - Java, Maven, Python. Confirm path set by - "java --version", "javac", "mvn -version", "python example.py"
5. Set VM/machine time to UTC time - e.g. in linux sync ntp timestamp

# Running Steps
1. Go to the path of pom.xml (root dir cryptotrader)
2. Command line in this path - Build the project - command is "mvn clean install"
3. Go to cryptotrader\target path - copy cryptotrader-0.0.1-SNAPSHOT-jar-with-dependencies.jar and  place it in the folder to run
4. Go to path cryptotrader\src\main\java\com\ritesh\cryptotrader\trading\strategy\TestShortLongMACrossOver03
5. Copy python strategy microservice file strategy_service.py from the above path
6. Run this py file by - "python strategy_service.py" - It will run the microservice at port 3000
7. Run the Java file copied above - "java -jar cryptotrader-0.0.1-SNAPSHOT-jar-with-dependencies.jar"
