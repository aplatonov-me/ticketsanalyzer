Prepare your tickets.json before running!

# JRE 21 
1. Download and unzip https://github.com/user-attachments/files/21684102/TicketAnalyzer-1.0-SNAPSHOT-jar-with-dependencies.zip
2. Execute:
```
java -jar TicketAnalyzer-1.0-SNAPSHOT-jar-with-dependencies.jar -t .\tickets.json -o Владивосток -d Тель-Авив
```
# Docker
```
docker run -v $PWD/tickets.json:/data/tickets.json aplatonovme/ticketsanalyzer:1.0 -t /data/tickets.json -o Владивосток -d Тель-Авив
```
