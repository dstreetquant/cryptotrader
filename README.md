# cryptotrader
Trading platform for Crypto Currencies

# Pre Requisites
Install Java JDK and JRE 1.8
Install Maven
Install Python 2.7 (better install anaconda)
Set environment path for all the three above - Java, Maven, Python
Confirm path set by - "java --version", "javac", "mvn -version", "python example.py"
Set VM/machine time to UTC time - e.g. in linux sync ntp timestamp

# Running Steps
Go to the path of pom.xml (root dir cryptotrader)
Command line in this path - Build the project - command is "mvn clean install"
Go to cryptotrader\target path - copy cryptotrader-0.0.1-SNAPSHOT-jar-with-dependencies.jar and  place it in the folder to run
Go to path cryptotrader\src\main\java\com\ritesh\cryptotrader\trading\strategy\TestShortLongMACrossOver03
Copy python strategy microservice file strategy_service.py from the above path
Run this py file by - "python strategy_service.py" - It will run the microservice at port 3000
Run the Java file copied above - "java -jar cryptotrader-0.0.1-SNAPSHOT-jar-with-dependencies.jar"
