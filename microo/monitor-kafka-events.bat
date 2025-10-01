@echo off
echo ================================================================================
echo                    Real-time Kafka Event Monitor
echo ================================================================================
echo.
echo This script will monitor the marketplace.service.events topic in real-time
echo while you test your API endpoints.
echo.

cd /d "d:\Martin\Programas\Web\microoservicio_servicios\main\clase-11380-microservicio-servicios\microo"

echo Starting Kafka consumer to monitor events...
echo.
echo Topic: marketplace.service.events
echo Press Ctrl+C to stop monitoring
echo.
echo ================================================================================
echo                    LIVE KAFKA EVENTS MONITOR
echo ================================================================================

docker exec -it microo-kafka-1 kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic marketplace.service.events --from-beginning

echo.
echo Monitor stopped.
pause