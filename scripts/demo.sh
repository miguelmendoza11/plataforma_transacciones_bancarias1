#!/usr/bin/env bash
# Demo de punta a punta: lanza los dos flujos del enunciado contra transaction-api.
# Uso (con "docker compose up -d" ya corriendo):  bash scripts/demo.sh
# En Windows se corre con Git Bash.
set -euo pipefail

API_URL="${API_URL:-http://localhost:8080}"
INTENTOS=60   # 60 x 2 s = 2 minutos maximo

echo "Esperando a que transaction-api responda en $API_URL ..."
for ((i = 1; i <= INTENTOS; i++)); do
  if curl -fsS "$API_URL/actuator/health" > /dev/null 2>&1; then
    echo "API lista."
    break
  fi
  if ((i == INTENTOS)); then
    echo "La API no respondio. Revisa: docker compose ps" >&2
    exit 1
  fi
  sleep 2
done

# Envia una transferencia e imprime el tx_id que devuelve la API (202 Accepted)
transferir() {
  local descripcion="$1" cuerpo="$2" respuesta
  respuesta=$(curl -fsS -X POST "$API_URL/transfer" \
    -H 'Content-Type: application/json' -d "$cuerpo")
  echo "$descripcion -> tx_id: $(echo "$respuesta" | sed -E 's/.*"tx_id" *: *"([^"]+)".*/\1/')"
}

echo
transferir "Flujo 1 - normal (\$50)"       '{"from_user":"A","to_user":"B","amount":50}'
transferir "Flujo 2 - fraude (\$20.000)"   '{"from_user":"A","to_user":"B","amount":20000}'

echo
echo "Que revisar ahora:"
echo "  1. Dashboard: http://localhost:8081/dashboard.html  (la de \$20.000 debe salir en rojo, Estado SOSPECHOSA y Fraude HIGH_VALUE)"
echo "  2. Correos:   docker compose logs -f email-worker  (ENVIANDO EMAIL a [user_a@email.com]: [Envió \$50.00 a B] ...)"
echo "  3. Consola RabbitMQ: http://localhost:15672 (guest / guest)"
echo "  4. Divisa no admitida (debe responder 400):"
echo "     curl -X POST $API_URL/transfer -H 'Content-Type: application/json' -d '{\"from_user\":\"A\",\"to_user\":\"B\",\"amount\":50,\"currency\":\"USD\"}'"
