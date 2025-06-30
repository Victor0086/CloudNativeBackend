#!/bin/bash

# Espera a que rabbitmq1 esté disponible
echo "Esperando a rabbitmq1..."
until nc -z rabbitmq1 5672; do
  sleep 2
done

# Comandos para unir al cluster
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster rabbit@rabbitmq1
rabbitmqctl start_app

# Iniciar el servidor RabbitMQ como proceso principal
exec docker-entrypoint.sh rabbitmq-server
