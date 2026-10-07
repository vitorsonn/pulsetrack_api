import requests
import time


ROUTE = [
    {"latitude": -23.264123, "longitude": -47.299876},
    {"latitude": -23.264800, "longitude": -47.298900},
    {"latitude": -23.265500, "longitude": -47.298000},
    {"latitude": -23.266200, "longitude": -47.297100},
    {"latitude": -23.267000, "longitude": -47.296200},
    {"latitude": -23.267800, "longitude": -47.295400},
    {"latitude": -23.268500, "longitude": -47.295000}
]

ORDER_ID = 1
API_URL = "http://localhost:8080/deliveries/location"

print(f"🚀 A iniciar simulação de entrega para o Pedido #{ORDER_ID}...")

for index, point in enumerate(ROUTE, start=1):
    payload = {
        "orderId": ORDER_ID,
        "latitude": point["latitude"],
        "longitude": point["longitude"]
    }
    
    try:
        response = requests.post(API_URL, json=payload)
        print(f"📍 [{index}/{len(ROUTE)}] Pulso enviado: Lat {point['latitude']} | Lng {point['longitude']} - Status: {response.status_code}")
    except Exception as e:
        print(f"❌ Erro ao enviar pulso: {e}")
    

    time.sleep(2.5)

print("🎉 Simulação finalizada com sucesso! O entregador chegou ao destino.")