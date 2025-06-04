import React, { useEffect, useState } from 'react';
import DefaultLayout from "@/layouts/default";
import { Button } from "@heroui/react";
import { getUser } from "../service/authService";
import { useNavigate } from "react-router-dom";

interface Order {
  id: number;
  customerId: number;
  restaurantId: number;
  courierId: number | null;
  status: 'PENDING' | 'CONFIRMED' | 'DELIVERED' | 'CANCELLED';
  orderType: 'DELIVERY' | 'PICKUP';
  date: string;
  menuItems: string[];
}

interface UserInfo {
  id: number;
  username: string;
  roles: string[];
}

export default function CourierPanel() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [takenOrders, setTakenOrders] = useState<Set<number>>(new Set());
  const [isAuthorized, setIsAuthorized] = useState<boolean>(false);
  const navigate = useNavigate();

  const fetchDeliveredOrders = async () => {
    if (!isAuthorized) return;
    
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        navigate("/login");
        return;
      }

      const response = await fetch('http://localhost:8080/order/status/DELIVERED', {
        headers: {
          Authorization: `Bearer ${user.access_token}`,
        },
        credentials: 'include'
      });

      if (!response.ok) throw new Error('Failed to fetch orders');
      const data = await response.json();
      // Фильтруем только заказы с типом DELIVERY
      setOrders(data.filter((order: Order) => order.orderType === 'DELIVERY'));
    } catch (error) {
      console.error('Error fetching orders:', error);
    }
  };

  const handleTakeOrder = (orderId: number) => {
    setTakenOrders(prev => new Set([...prev, orderId]));
  };

  const handleDeliverOrder = async (orderId: number) => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        navigate("/login");
        return;
      }

      const response = await fetch(`http://localhost:8080/order/${orderId}?status=CONFIRMED`, {
        method: 'PATCH',
        headers: {
          Authorization: `Bearer ${user.access_token}`,
        },
        credentials: 'include'
      });

      if (!response.ok) throw new Error('Failed to update order');
      
      // Удаляем заказ из списка взятых
      setTakenOrders(prev => {
        const newSet = new Set(prev);
        newSet.delete(orderId);
        return newSet;
      });
      
      // Обновляем список заказов
      fetchDeliveredOrders();
    } catch (error) {
      console.error('Error updating order:', error);
    }
  };

  useEffect(() => {
    const checkAccess = async () => {
      try {
        const user = await getUser();
        if (!user) {
          console.log('No user found, redirecting to login');
          navigate("/login");
          return;
        }

        console.log('Checking user access token:', !!user.access_token);
        
        const userInfoResponse = await fetch('http://localhost:8080/me', {
          headers: {
            Authorization: `Bearer ${user.access_token}`,
          },
          credentials: 'include'
        });
        
        if (!userInfoResponse.ok) {
          console.error('User info response not OK:', userInfoResponse.status);
          throw new Error('Failed to fetch user info');
        }
        
        const userInfo: UserInfo = await userInfoResponse.json();
        console.log('User Info:', userInfo);
        
        // Проверяем структуру ответа
        if (!userInfo || typeof userInfo !== 'object') {
          console.error('Invalid user info response format');
          navigate("/");
          return;
        }

        // Проверяем наличие и формат массива ролей
        if (!Array.isArray(userInfo.roles)) {
          console.error('Roles is not an array:', userInfo.roles);
          navigate("/");
          return;
        }

        const hasCourierRole = userInfo.roles.some(role => 
          role === 'ROLE_COURIER' || role === 'COURIER'
        );

        if (!hasCourierRole) {
          console.log('Access denied: User does not have ROLE_COURIER');
          console.log('Available roles:', userInfo.roles);
          navigate("/");
          return;
        }

        console.log('Access granted: User has ROLE_COURIER');
        setIsAuthorized(true);
      } catch (error) {
        console.error('Error checking access:', error);
        navigate("/");
      }
    };

    checkAccess();
  }, [navigate]);

  useEffect(() => {
    if (isAuthorized) {
      fetchDeliveredOrders();
      // Обновляем список заказов каждые 30 секунд
      const interval = setInterval(fetchDeliveredOrders, 30000);
      return () => clearInterval(interval);
    }
  }, [isAuthorized]);

  if (!isAuthorized) {
    return null;
  }

  return (
    <DefaultLayout>
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-2xl font-bold mb-6">Панель курьера</h1>
        <div className="grid gap-4">
          {orders.map(order => (
            <div key={order.id} className="bg-white p-4 rounded-lg shadow">
              <div className="flex justify-between items-center">
                <div>
                  <h3 className="font-semibold">Заказ #{order.id}</h3>
                  <p className="text-sm text-gray-600">
                    Статус: {order.status}
                  </p>
                  <p className="text-sm text-gray-600">
                    Дата: {new Date(order.date).toLocaleString()}
                  </p>
                  <div className="mt-2">
                    <p className="text-sm font-medium">Состав заказа:</p>
                    <ul className="text-sm text-gray-600">
                      {order.menuItems.map((item, index) => (
                        <li key={index}>• {item}</li>
                      ))}
                    </ul>
                  </div>
                </div>
                <div className="flex gap-2">
                  {!takenOrders.has(order.id) ? (
                    <Button
                      color="primary"
                      onClick={() => handleTakeOrder(order.id)}
                    >
                      Взять заказ
                    </Button>
                  ) : (
                    <Button
                      color="success"
                      onClick={() => handleDeliverOrder(order.id)}
                    >
                      Доставить
                    </Button>
                  )}
                </div>
              </div>
            </div>
          ))}
          {orders.length === 0 && (
            <p className="text-center text-gray-500">Нет заказов для доставки</p>
          )}
        </div>
      </div>
    </DefaultLayout>
  );
} 