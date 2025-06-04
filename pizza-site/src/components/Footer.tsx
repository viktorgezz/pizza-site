import { useEffect, useState } from "react";
import { Card, CardBody } from "@heroui/card";
import { API_BASE_URL } from "../config/api";

interface RestaurantInfo {
  address: string;
  status: string;
  openingTime: string;
  closingTime: string;
}

export default function Footer() {
  const [restaurantInfo, setRestaurantInfo] = useState<RestaurantInfo | null>(null);

  useEffect(() => {
    fetch(`${API_BASE_URL}/restaurant`)
      .then((response) => response.json())
      .then((data) => setRestaurantInfo(data))
      .catch((error) => console.error("Error fetching restaurant info:", error));
  }, []);

  if (!restaurantInfo) return null;

  return (
    <footer className="w-full py-6 px-4 mt-8">
      <Card className="max-w-[1200px] mx-auto">
        <CardBody>
          <div className="flex items-center justify-between gap-4 text-sm">
            <div className="flex items-center gap-2">
              <span className="font-semibold">Наш адрес:</span>
              <span>{restaurantInfo.address}</span>
            </div>
            <div className="flex items-center gap-2">
              <span className="font-semibold">Время работы:</span>
              <span>
                {restaurantInfo.openingTime} - {restaurantInfo.closingTime}
              </span>
            </div>
            <div className="flex items-center gap-2">
              <span
                className={`px-2 py-1 rounded-full text-white ${
                  restaurantInfo.status === "OPEN"
                    ? "bg-green-500"
                    : "bg-red-500"
                }`}
              >
                {restaurantInfo.status}
              </span>
            </div>
          </div>
        </CardBody>
      </Card>
    </footer>
  );
} 