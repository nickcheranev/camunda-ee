https://www.youtube.com/watch?v=srR9KARbGoQ

cd /opt/kafka/bin ./kafka-console-producer.sh --broker-list localhost:9092 --topic kafka-start
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic kafka-start ./kafka-topics.sh --bootstrap-server
localhost:9092 --list