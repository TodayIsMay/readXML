Запуск программы:

1. установить [JDK 11](https://corretto.aws/downloads/latest/amazon-corretto-11-x64-windows-jdk.msi.) и [Maven](https://dlcdn.apache.org/maven/maven-3/3.8.4/binaries/apache-maven-3.8.4-bin.zip)
2. иметь доступ к MySQL(имя пользователя, пароль, созданная БД)
3. в командной строке зайти в папку readXML
4. ввести команду "mvn compile"
5. ввести команду mvn exec:java -Dexec.mainClass="Main"
6. по запросам программы ввести имя базы данных, затем имя пользователя и затем пароль
После ввода данных программа создаст таблицу в БД и занесет в нее данные из лежащего
в папке проекта XML файла