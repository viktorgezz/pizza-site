import DefaultLayout from "@/layouts/default";
import { useEffect, useState } from "react";
import { getUser, logout } from "../service/authService";
import { fetchUserInfoFromResourceServer } from "../service/resourceUserService";
import { useNavigate } from "react-router-dom";
import { LoginButton } from "../components/LoginButton";
import { Button } from "@heroui/button";

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

export default function ProfilePage() {
  const [user, setUser] = useState<any>(null);
  const [userInfo, setUserInfo] = useState<any>(null);
  const [userOrders, setUserOrders] = useState<Order[]>([]);
  const navigate = useNavigate();

  const fetchUserOrders = async () => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) return;

      const response = await fetch('http://localhost:8080/order/my', {
        headers: {
          Authorization: `Bearer ${user.access_token}`,
        },
        credentials: 'include'
      });

      if (!response.ok) throw new Error('Failed to fetch orders');
      const data = await response.json();
      setUserOrders(data);
    } catch (error) {
      console.error('Error fetching user orders:', error);
    }
  };

  useEffect(() => {
    getUser().then(u => {
      setUser(u);
      if (!u) navigate("/login");
    });
  }, [navigate]);

  useEffect(() => {
    if (user) {
      fetchUserInfoFromResourceServer()
        .then(setUserInfo)
        .catch(() => setUserInfo(null));
    }
  }, [user]);

  useEffect(() => {
    if (userInfo?.roles?.includes('ROLE_USER')) {
      fetchUserOrders();
    }
  }, [userInfo]);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING': return 'text-yellow-600';
      case 'CONFIRMED': return 'text-green-600';
      case 'DELIVERED': return 'text-blue-600';
      case 'CANCELLED': return 'text-red-600';
      default: return 'text-gray-600';
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'PENDING': return 'В обработке';
      case 'CONFIRMED': return 'Исполнен';
      case 'DELIVERED': return 'Доставлен';
      case 'CANCELLED': return 'Отменен';
      default: return status;
    }
  };

  return (
    <DefaultLayout>
      <section className="flex flex-col items-center justify-center gap-6 py-12 md:py-16">
        <div className="max-w-3xl w-full p-8 bg-default-100 shadow-lg rounded-xl flex flex-col items-center">
          {user ? (
            <>
              <img
                src="/profile_photo.jpg"
                alt="Аватар пользователя"
                className="w-32 h-32 rounded-full mb-6 border-4 border-default-200 shadow"
              />
              <h2 className="text-2xl font-bold text-default-900 mb-2">
                {user.profile.name || user.profile.sub}
              </h2>
              {userInfo && (
                <div className="w-full text-left mt-4">
                  <div><b>ID:</b> {userInfo.id}</div>
                  <div><b>Username:</b> {userInfo.username}</div>
                  <div><b>Roles:</b> {userInfo.roles?.join(", ")}</div>
                  <div className="flex flex-wrap gap-2 mt-4">
                    {userInfo.roles?.includes('ROLE_MAIN') && (
                      <Button
                        color="primary"
                        onClick={() => navigate("/main-panel")}
                      >
                        Панель управления
                      </Button>
                    )}
                    {userInfo.roles?.includes('ROLE_MANAGER') && (
                      <Button
                        color="secondary"
                        onClick={() => navigate("/manager-panel")}
                      >
                        Панель менеджера
                      </Button>
                    )}
                    {userInfo.roles?.includes('ROLE_WORKER') && (
                      <Button
                        color="success"
                        onClick={() => navigate("/worker-panel")}
                      >
                        Панель работника
                      </Button>
                    )}
                    {userInfo.roles?.includes('ROLE_COURIER') && (
                      <Button
                        color="primary"
                        onClick={() => navigate("/courier-panel")}
                      >
                        Панель курьера
                      </Button>
                    )}
                  </div>

                  {userInfo.roles?.includes('ROLE_USER') && userOrders.length > 0 && (
                    <div className="mt-8 w-full">
                      <h3 className="text-xl font-bold mb-4">История заказов</h3>
                      <div className="space-y-4">
                        {userOrders.map(order => (
                          <div key={order.id} className="bg-white p-4 rounded-lg shadow">
                            <div className="flex justify-between items-start">
                              <div>
                                <h4 className="font-semibold">Заказ #{order.id}</h4>
                                <p className="text-sm text-gray-600">
                                  Тип: {order.orderType === 'DELIVERY' ? 'Доставка' : 'Самовывоз'}
                                </p>
                                <p className="text-sm text-gray-600">
                                  Дата: {new Date(order.date).toLocaleString()}
                                </p>
                                <p className={`text-sm font-medium ${getStatusColor(order.status)}`}>
                                  Статус: {getStatusText(order.status)}
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
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              )}
              <button
                onClick={logout}
                className="mt-6 bg-gray-200 px-4 py-2 rounded"
              >
                Выйти
              </button>
            </>
          ) : (
            <>
              <h2 className="text-2xl font-bold text-default-900 mb-2">
                Войдите, чтобы просмотреть профиль
              </h2>
              <LoginButton />
            </>
          )}
        </div>
      </section>
    </DefaultLayout>
  );
} 